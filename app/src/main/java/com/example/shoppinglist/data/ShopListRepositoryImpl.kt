package com.example.shoppinglist.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.map
import com.example.shoppinglist.data.database.ShopListItemDatabase
import com.example.shoppinglist.domain.ShopItem
import com.example.shoppinglist.domain.ShopListRepository

class ShopListRepositoryImpl(private val application: Application) : ShopListRepository {
    private val TAG = "ShopListRepositoryImpl"

    private val connectDB = ShopListItemDatabase.getInstance(application).shopListItemDAO()
    private val mapper = ShopListMapper()

    override suspend fun addShopItemToList(shopItem: ShopItem) {
        connectDB.addNewShopItemToDB(mapper.mapEntityToDbModel(shopItem))
    }

    override suspend fun getShopItem(shopItemId: Int): ShopItem {
        val shopItemDbModel = connectDB.getShopItem(shopItemId)
        Log.d(TAG, "getShopItem: shopItemDbModel = $shopItemDbModel")
        return mapper.mapDbModelToEntity(shopItemDbModel)
    }

    override fun getShopList() =
        connectDB.getShopListFromDB().map { mapper.mapListDbModelToListEntity(it) }


    override suspend fun removeShopItem(shopItem: ShopItem) {
        connectDB.removeShopItemFromDB(mapper.mapEntityToDbModel(shopItem))
    }

    override suspend fun editShopItem(shopItem: ShopItem) {
        connectDB.addNewShopItemToDB(mapper.mapEntityToDbModel(shopItem))
    }
}
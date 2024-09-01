package com.example.shoppinglist.data

import android.app.Application
import com.example.shoppinglist.database.ShopListItemDatabase
import com.example.shoppinglist.domain.ShopItem
import com.example.shoppinglist.domain.ShopListRepository

object ShopListRepositoryImpl : ShopListRepository {

    private val listOfItems = mutableListOf<ShopItem>()
    private var autoIncrementId = 0

    override fun addShopItemToList(shopItem: ShopItem) {
        if (shopItem.id == ShopItem.UNDEFINED_ID) {
            shopItem.id = autoIncrementId++
        }
        listOfItems.add(shopItem)
    }

    override fun getShopItem(shopItemId: Int): ShopItem {
        return listOfItems.find { it.id == shopItemId }
            ?: throw RuntimeException("Элемент с ID $shopItemId не найден")
    }

    override fun getShopList(): List<ShopItem> {
        return listOfItems.toMutableList()
    }

    override fun removeShopItem(shopItem: ShopItem) {
        listOfItems.remove(shopItem)
    }

    override fun editShopItem(shopItem: ShopItem) {
        val oldElement = getShopItem(shopItem.id)
        removeShopItem(oldElement)
        addShopItemToList(shopItem)
    }
}
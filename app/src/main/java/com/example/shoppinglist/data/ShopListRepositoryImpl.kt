package com.example.shoppinglist.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.shoppinglist.data.database.ShopListItemDAO
import com.example.shoppinglist.domain.ShopItem
import com.example.shoppinglist.domain.ShopListRepository
import kotlin.random.Random

object ShopListRepositoryImpl : ShopListRepository {
    private val TAG = "ShopListRepositoryImpl"

    private val shopListLD: MutableLiveData<List<ShopItem>> = MutableLiveData()
    private val listOfItems = mutableListOf<ShopItem>()

    private var autoIncrementId = 0

    init {
        for (i in 0..10) {
            val item = ShopItem("Имя $i", i, Random.nextBoolean(), 0)
            listOfItems.add(item)
        }
        Log.d(TAG, "Элементы списка: $listOfItems")
    }

    override fun addShopItemToList(shopItem: ShopItem) {
        if (shopItem.id == ShopItem.UNDEFINED_ID) {
            shopItem.id = autoIncrementId++
        }
        listOfItems.add(shopItem)
        updateList()
    }

    override fun getShopItem(shopItemId: Int): ShopItem {
        return listOfItems.find { it.id == shopItemId }
            ?: throw RuntimeException("Элемент с ID $shopItemId не найден")
    }

    override fun getShopList(): List<ShopItem> {
        return listOfItems
    }

    override fun removeShopItem(shopItem: ShopItem) {
        listOfItems.remove(shopItem)
        updateList()
    }

    override fun editShopItem(shopItem: ShopItem): ShopItem {
        val oldElement = getShopItem(shopItem.id)
        removeShopItem(oldElement)
        addShopItemToList(shopItem)
        updateList()
        return shopItem
    }

    private fun updateList(){
        shopListLD.value = listOfItems.toList()
    }
}
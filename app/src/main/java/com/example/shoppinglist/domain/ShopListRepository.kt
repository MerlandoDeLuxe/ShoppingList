package com.example.shoppinglist.domain

import androidx.lifecycle.LiveData

interface ShopListRepository {

    suspend fun addShopItemToList(shopItem: ShopItem)

    suspend fun getShopItem(shopItemId: Int): ShopItem

    fun getShopList(): LiveData<List<ShopItem>>

    suspend fun removeShopItem(shopItem: ShopItem)

    suspend fun editShopItem(shopItem: ShopItem)
}
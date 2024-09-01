package com.example.shoppinglist.domain

interface ShopListRepository {

    fun addShopItemToList(shopItem: ShopItem)

    fun getShopItem(shopItemId: Int): ShopItem

    fun getShopList(): List<ShopItem>

    fun removeShopItem(shopItem: ShopItem)

    fun editShopItem(shopItem: ShopItem)

}
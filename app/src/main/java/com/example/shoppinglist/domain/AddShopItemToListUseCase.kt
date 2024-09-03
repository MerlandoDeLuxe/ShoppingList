package com.example.shoppinglist.domain

class AddShopItemToListUseCase(private val shopListRepository: ShopListRepository) {

    fun addShopItemToList(shopItem: ShopItem) {
        shopListRepository.addShopItemToList(shopItem)
    }
}
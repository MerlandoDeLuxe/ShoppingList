package com.example.shoppinglist.domain

class AddShopItemToListUseCase(private val shopListRepository: ShopListRepository) {

    suspend fun addShopItemToList(shopItem: ShopItem) {
        shopListRepository.addShopItemToList(shopItem)
    }
}
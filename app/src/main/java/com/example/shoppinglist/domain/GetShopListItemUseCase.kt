package com.example.shoppinglist.domain

import androidx.lifecycle.LiveData

class GetShopListItemUseCase(private val shopListRepository: ShopListRepository) {

    fun getShopList(): LiveData<List<ShopItem>> {
        return shopListRepository.getShopList()
    }
}
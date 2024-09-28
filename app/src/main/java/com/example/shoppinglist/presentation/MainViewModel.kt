package com.example.shoppinglist.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.shoppinglist.data.ShopListRepositoryImpl
import com.example.shoppinglist.domain.EditShopItemUseCase
import com.example.shoppinglist.domain.GetShopListItemUseCase
import com.example.shoppinglist.domain.RemoveShopItemUseCase
import com.example.shoppinglist.domain.ShopItem
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "MainViewModel"

    private val repository = ShopListRepositoryImpl(application)

    private val getShopListItemUseCase = GetShopListItemUseCase(repository)
    private val editShopListItemUseCase = EditShopItemUseCase(repository)
    private val removeShopItemUseCase = RemoveShopItemUseCase(repository)

    init {
        getShopListFromDB()
    }

    fun getShopListFromDB(): LiveData<List<ShopItem>> {
        return getShopListItemUseCase.getShopList()
    }

    fun removeShopItemFromDB(shopItem: ShopItem) {
        viewModelScope.launch { removeShopItemUseCase.removeShopItem(shopItem) }
    }

    fun editShopListItemAndSaveToDB(shopItem: ShopItem) {
        viewModelScope.launch {
            val tempShopItem = shopItem.copy(enabled = !shopItem.enabled)
            editShopListItemUseCase.editShopItem(tempShopItem)
        }
    }
}
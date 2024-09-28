package com.example.shoppinglist.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.shoppinglist.data.ShopListRepositoryImpl
import com.example.shoppinglist.data.database.ShopItemDbModel
import com.example.shoppinglist.data.database.ShopListItemDatabase
import com.example.shoppinglist.domain.AddShopItemToListUseCase
import com.example.shoppinglist.domain.EditShopItemUseCase
import com.example.shoppinglist.domain.GetShopItemUseCase
import com.example.shoppinglist.domain.ShopItem
import kotlinx.coroutines.launch

class ShopItemViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "ShopItemViewModel"

    private val connectDB = ShopListItemDatabase.getInstance(application).shopListItemDAO()

    private val _shopItemLD = MutableLiveData<ShopItem>()
    val shopItemLD: LiveData<ShopItem>
        get() = _shopItemLD

    private val _errorInputNameLD = MutableLiveData<Boolean>(false)
    val errorInputNameLD: LiveData<Boolean>
        get() = _errorInputNameLD
    private val _errorInputQuantityLD = MutableLiveData<Boolean>(false)
    val errorInputQuantityLD: LiveData<Boolean>
        get() = _errorInputQuantityLD

    private val _shouldCloseScreenLD = MutableLiveData<Unit>()
    val shouldCloseScreenLD: LiveData<Unit>
        get() = _shouldCloseScreenLD

    private val repository = ShopListRepositoryImpl(application)
    private val addShopItemToListUseCase = AddShopItemToListUseCase(repository)
    private val editShopItemUseCase = EditShopItemUseCase(repository)
    private val getShopItemUseCase = GetShopItemUseCase(repository)

    fun getShopItem(id: Int) {
        viewModelScope.launch {
            _shopItemLD.value = getShopItemUseCase.getShopItem(id)
        }
    }

    fun monitoringShopItemExists(shopItemId: Int): LiveData<ShopItemDbModel> {
        return connectDB.monitoringShopItemExist(shopItemId)
    }

    fun addShopItem(inputName: String?, inputQuantity: String?) {
        val name = parseName(inputName)
        val quantity = parseQuantity(inputQuantity)
        val fieldsValid = validateInput(name, quantity)
        if (fieldsValid) {
            val shopItem = ShopItem(name, quantity, true, ShopItem.UNDEFINED_ID)
            viewModelScope.launch {
                addShopItemToListUseCase.addShopItemToList(shopItem)
                finishWork()
            }
        }
    }

    fun editShopItem(inputName: String?, inputQuantity: String?) {
        Log.d(TAG, "editShopItem: Вход в метод editShopItem()")
        val name = parseName(inputName)
        Log.d(TAG, "editShopItem: Название пересохранено как: $name")
        val quantity = parseQuantity(inputQuantity)
        Log.d(TAG, "editShopItem: Количество пересохранено как: $quantity")
        val fieldsValid = validateInput(name, quantity)
        Log.d(TAG, "editShopItem: Результат валидации: $fieldsValid")
        if (fieldsValid) {
            _shopItemLD.let {
                val tempShopItem = it.value?.copy(name = name, count = quantity)
                if (tempShopItem != null) {
                    viewModelScope.launch {
                        editShopItemUseCase.editShopItem(tempShopItem)
                    }
                }
            }
        }
    }

    private fun parseName(inputName: String?) = inputName?.trim() ?: ""

    private fun parseQuantity(inputQuantity: String?): Int {
        return try {
            inputQuantity?.trim()?.toInt() ?: 0
        } catch (e: Exception) {
            0
        }
    }

    private fun validateInput(name: String, quantity: Int): Boolean {
        var result = true
        if (name.isBlank()) {
            _errorInputNameLD.value = true
            Log.d(TAG, "validateInput: Установлен ")
            result = false
        }
        if (quantity <= 0) {
            _errorInputQuantityLD.value = true
            result = false
        }
        return result
    }

    fun resetErrorInputName() {
        _errorInputNameLD.value = false
    }

    fun resetErrorInputQuantity() {
        _errorInputQuantityLD.value = false
    }

    private fun finishWork() {
        Log.d(TAG, "finishWork: Запись в лайвдату: можно выходить на главный экран")
        _shouldCloseScreenLD.value = Unit
    }
}
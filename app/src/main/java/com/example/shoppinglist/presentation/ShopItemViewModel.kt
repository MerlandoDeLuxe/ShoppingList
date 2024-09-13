package com.example.shoppinglist.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.shoppinglist.data.database.ShopListItemDatabase
import com.example.shoppinglist.domain.ShopItem
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class ShopItemViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "ShopItemViewModel"

    private val compositeDisposable = CompositeDisposable()
    private val connectDB = ShopListItemDatabase.getInstance(application).shopListItemDAO

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

    fun monitoringShopItemExists(id: Int): LiveData<ShopItem> {
        return connectDB.monitoringShopItemExist(id)
    }

    fun getShopItem(id: Int) {
        val disposable = connectDB.getShopItem(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _shopItemLD.value = it
                Log.d(TAG, "getShopItem: из БД получен объект $it")
            }, {
                Log.d(TAG, "getShopItem: Ошибка подключения к БД: ${it.message}")
            })

        compositeDisposable.add(disposable)
    }

    fun addShopItem(inputName: String?, inputQuantity: String?) {
        val name = parseName(inputName)
        val quantity = parseQuantity(inputQuantity)
        val fieldsValid = validateInput(name, quantity)
        if (fieldsValid) {
            val shopItem = ShopItem(name, quantity, true, 0)

            val disposable = connectDB.addNewShopItemToDB(shopItem)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d(TAG, "addShopItem: Добавление успешно завершено")
                    finishWork()
                }, {
                    Log.d(TAG, "addShopItem: Ошибка подключения к БД: ${it.message}")
                })
            compositeDisposable.add(disposable)
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
                val disposable = tempShopItem?.let { it1 ->
                    connectDB.editShopItemElement(it1)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            Log.d(TAG, "editShopItem: Объект успешно изменен")
                            finishWork()
                        }, {
                            Log.d(TAG, "editShopItem: Ошибка подключения к БД: ${it.message}")
                        })
                }
                if (disposable != null) {
                    compositeDisposable.add(disposable)
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

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
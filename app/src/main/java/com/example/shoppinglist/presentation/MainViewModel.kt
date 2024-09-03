package com.example.shoppinglist.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.shoppinglist.data.ShopListRepositoryImpl
import com.example.shoppinglist.data.database.ShopListItemDatabase
import com.example.shoppinglist.domain.EditShopItemUseCase
import com.example.shoppinglist.domain.GetShopListItemUseCase
import com.example.shoppinglist.domain.RemoveShopItemUseCase
import com.example.shoppinglist.domain.ShopItem
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "MainViewModel"

    private val connectDB = ShopListItemDatabase.getInstance(application).shopListItemDAO
    private val compositeDisposable = CompositeDisposable()

    private val repository = ShopListRepositoryImpl

    private val getShopListItemUseCase = GetShopListItemUseCase(repository)
    private val editShopListItemUseCase = EditShopItemUseCase(repository)
    private val removeShopItemUseCase = RemoveShopItemUseCase(repository)

//    val shopListLD = getShopListItemUseCase.getShopList()
//    val shopListFromDB: MutableLiveData<MutableList<ShopItem>> = MutableLiveData()

    init {
//        removeAllElementsShopItemElementFromDB()
        getShopListAndSetToDB()
//        getShopListFromDB()
    }

    fun getShopListFromDB(): LiveData<MutableList<ShopItem>> {
        return connectDB.getListFromDB()
    }

    fun removeShopItemFromDB(shopItem: ShopItem) {
        val disposable = connectDB.removeShopItemFromDB(shopItem)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
//                getShopListFromDB()
                Log.d(TAG, "removeShopItemFromDB: удаление $shopItem успешно завершено")
            }, {
                Log.d(TAG, "removeShopItemFromDB: Ошибка подключения к БД: ${it.message}")
            })
        compositeDisposable.add(disposable)
    }

    fun removeAllElementsShopItemFromDB() {
        val disposable = connectDB.removeAllElementsShopItemFromDB()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d(TAG, "removeShopItemFromDB: Удаление всех элементов успешно завершено")
                //getShopListFromDB()
            }, {
                Log.d(TAG, "removeShopItemFromDB: Ошибка подклчюения к БД ${it.message}")
            })
        compositeDisposable.add(disposable)
    }

    fun editShopListItemAndSaveToDB(shopItem: ShopItem) {
        if (shopItem.enabled) {
            shopItem.enabled = false
        } else {
            shopItem.enabled = true
        }
        val tempShopItem = shopItem.copy()
        val disposable =
            connectDB.editShopItemElement(tempShopItem)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d(TAG, "editShopListItemAndSaveToDB: Изменение успешно")
                    Log.d(TAG, "editShopListItemAndSaveToDB: shopItem.enabled = ${shopItem.enabled}")
                    connectDB.removeElementShopItemFromDB(shopItem)
                    getShopListFromDB()
                }, {
                    Log.d(
                        TAG,
                        "editShopListItemAndSaveToDB: Ошибка подключения к БД: ${it.message}"
                    )
                })
        compositeDisposable.add(disposable)
    }

    fun getShopListAndSetToDB() {
        Log.d(TAG, "getShopListAndSetToDB: мы тут")
        val disposable = getShopListItemUseCase.getShopList().let {
            connectDB.saveListToDatabase(it)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    getShopListFromDB()
                }, {
                    Log.d(TAG, "getShopListAndSetToDB: Ошибка подключения к БД: ${it.message}")
                })
        }
        if (disposable != null) {
            compositeDisposable.add(disposable)
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
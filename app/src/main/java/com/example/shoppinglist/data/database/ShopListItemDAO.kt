package com.example.shoppinglist.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.shoppinglist.domain.ShopItem
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface ShopListItemDAO {

    @Insert(ShopItem::class, OnConflictStrategy.REPLACE)
    fun saveListToDatabase(shopItemList: List<ShopItem>): Completable

    @Query("select * from shop_item")
    fun getListFromDB(): LiveData<MutableList<ShopItem>>

    @Update
    fun editShopItemElement(shopItem: ShopItem): Completable

    @Query("delete from shop_item")
    fun removeAllElementsShopItemFromDB(): Completable

    @Delete
    fun removeElementShopItemFromDB(shopItem: ShopItem): Completable

    @Delete
    fun removeShopItemFromDB(shopItem: ShopItem): Completable

    @Query("select * from shop_item where id = :id")
    fun getShopItem(id: Int): Single<ShopItem>

    @Query("select * from shop_item where id = :id")
    fun monitoringShopItemExist(id: Int): LiveData<ShopItem>

    @Insert
    fun addNewShopItemToDB(shopItem: ShopItem): Completable
}
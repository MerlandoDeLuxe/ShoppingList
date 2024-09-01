package com.example.shoppinglist.data.database

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
    fun getListFromDB(): Single<List<ShopItem>>

    @Update
    fun editShopItemElement(shopItem: ShopItem): Completable

    @Delete
    fun removeShopItemElementFromDB(shopItem: ShopItem): Completable
}
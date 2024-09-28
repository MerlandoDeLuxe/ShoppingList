package com.example.shoppinglist.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable

@Dao
interface ShopListItemDAO {

    @Query("select * from shop_item")
    fun getShopListFromDB(): LiveData<List<ShopItemDbModel>>

    @Query("delete from shop_item")
    suspend fun removeAllElementsShopItemFromDB()

    @Delete
    suspend fun removeShopItemFromDB(shopItem: ShopItemDbModel)

    @Query("select * from shop_item where id = :shopItemId")
    suspend fun getShopItem(shopItemId: Int): ShopItemDbModel

    @Query("select * from shop_item where id = :shopItemId")
    fun monitoringShopItemExist(shopItemId: Int): LiveData<ShopItemDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNewShopItemToDB(shopItem: ShopItemDbModel)
}
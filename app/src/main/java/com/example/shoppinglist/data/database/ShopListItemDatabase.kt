package com.example.shoppinglist.data.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ShopItemDbModel::class], version = 2, exportSchema = false)
abstract class ShopListItemDatabase : RoomDatabase() {

    companion object {

        private val DB_NAME = "shop_list_db"
        private var db_instance: ShopListItemDatabase? = null
        private val LOCK = Any()

        fun getInstance(application: Application): ShopListItemDatabase {
            db_instance?.let {
                return it
            }
            synchronized(LOCK) {
                db_instance?.let {  //Двойная проверка, вдруг второй поток будет ждать, пока первый создаст экземпляр БД и затем тоже его создаст
                    return it
                }
                val db = Room.databaseBuilder(
                    application.applicationContext,
                    ShopListItemDatabase::class.java,
                    DB_NAME
                ).build()
                db_instance = db
                return db
            }
        }
    }

    abstract fun shopListItemDAO(): ShopListItemDAO
}
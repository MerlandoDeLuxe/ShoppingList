package com.example.shoppinglist.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("shop_item")
data class ShopItemDbModel(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var name: String,
    val count: Int,
    var enabled: Boolean
)

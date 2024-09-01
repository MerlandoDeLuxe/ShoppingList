package com.example.shoppinglist.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("shop_item")
data class ShopItem(
    val name: String,
    val count: Int,
    val enabled: Boolean,
    @PrimaryKey(autoGenerate = true) var id: Int
) {
    //Неопределенный ид
    companion object {
        const val UNDEFINED_ID = -1
    }
}

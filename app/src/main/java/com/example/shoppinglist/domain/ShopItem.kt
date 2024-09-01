package com.example.shoppinglist.domain

import androidx.room.Entity

@Entity("shop_item")
data class ShopItem(
    val name: String,
    val count: Int,
    val enabled: Boolean,
    var id: Int = UNDEFINED_ID
) {
    //Неопределенный ид
    companion object {
        const val UNDEFINED_ID = -1
    }
}

package com.example.shoppinglist.domain

data class ShopItem(
    var name: String,
    val count: Int,
    var enabled: Boolean,
    var id: Int
) {
    //Неопределенный ид
    companion object {
        const val UNDEFINED_ID = 0
    }
}

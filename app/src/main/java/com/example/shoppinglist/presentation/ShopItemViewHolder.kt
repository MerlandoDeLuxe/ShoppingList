package com.example.shoppinglist.presentation

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.shoppinglist.R

class ShopItemViewHolder(itemView: View) : ViewHolder(itemView) {
    val textViewShopItemName = itemView.findViewById<TextView>(R.id.textViewShopItemName)
    val textViewShopItemQuantity =
        itemView.findViewById<TextView>(R.id.textViewShopItemQuantity)
}
package com.example.shoppinglist.presentation

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.example.shoppinglist.domain.ShopItem

class ShopItemDiffCallback : DiffUtil.ItemCallback<ShopItem>() {
    private val TAG = "ShopItemDiffCallback"

    override fun areItemsTheSame(oldItem: ShopItem, newItem: ShopItem): Boolean {
//        Log.d(TAG, "areItemsTheSame: ${oldItem.id}")
//        Log.d(TAG, "areItemsTheSame: ${newItem.id}")
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ShopItem, newItem: ShopItem): Boolean {
        Log.d(TAG, "areItemsTheSame: oldItem ${oldItem}")
        Log.d(TAG, "areItemsTheSame: newItem ${newItem}")
        Log.d(TAG, "areContentsTheSame: ${oldItem.enabled == newItem.enabled}")
        return newItem==oldItem
    }
}
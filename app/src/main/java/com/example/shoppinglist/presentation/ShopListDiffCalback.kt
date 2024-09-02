package com.example.shoppinglist.presentation

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.example.shoppinglist.domain.ShopItem

@Deprecated("Или этот класс для адаптера или получше - ShopItemDiffCalback")
class ShopListDiffCalback(
    private val oldListShopItem: List<ShopItem>,
    private val newListShopItem: List<ShopItem>
) : DiffUtil.Callback() {
    private val TAG = "ShopListDiffCalback"

    override fun getOldListSize(): Int {
        return oldListShopItem.size
    }

    override fun getNewListSize(): Int {
        return newListShopItem.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldListShopItem.get(oldItemPosition)
        val newItem = newListShopItem.get(newItemPosition)

        if (oldItem.id == newItem.id) {
            return true
        } else {
            return false
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldListShopItem.get(oldItemPosition)
        val newItem = newListShopItem.get(newItemPosition)
        if (oldItem == newItem) {
            Log.d(TAG, "areContentsTheSame: Объекты равны: ")
            Log.d(TAG, "areContentsTheSame: $oldItem")
            Log.d(TAG, "areContentsTheSame: $newItem")
            return true
        } else {
            Log.d(TAG, "areContentsTheSame: Объекты не равны: ")
            Log.d(TAG, "areContentsTheSame: $oldItem")
            Log.d(TAG, "areContentsTheSame: $newItem")
            return false
        }
    }
}
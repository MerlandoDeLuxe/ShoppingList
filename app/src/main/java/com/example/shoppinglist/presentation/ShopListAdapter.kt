package com.example.shoppinglist.presentation

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.shoppinglist.R
import com.example.shoppinglist.domain.ShopItem

class ShopListAdapter :
    ListAdapter<ShopItem, ShopItemViewHolder>(ShopItemDiffCalback()) {

    companion object {
        private const val TAG = "ShopListAdapter"
        const val ENABLED_VIEW = 100
        const val DISABLED_VIEW = 200
        const val MAX_POOL_SIZE = 30
        private var count = 0;
    }

    //Поскольку у нас объявлен функциональный интерфейс, то мы можем создать переменную
    //и передать сразу в неё функцию. То есть переменная содержит функцию,
    //которая принимает в качестве аргумента ShopItem и ничего не возвращает
    //А если в неё ничего не прилетело, значит в ней null и она может быть нуллабельной
    var onShopItemLongClickListener: ((ShopItem) -> Unit)? = null
//    var onShopItemClickListener: ((ShopItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopItemViewHolder {
        Log.d(TAG, "onCreateViewHolder: count = ${++count}")
        val layoutId = when (viewType) {
            ENABLED_VIEW -> R.layout.new_enabled_shopitem_layout_template
            DISABLED_VIEW -> R.layout.new_disabled_shopitem_layout_template
            else -> throw RuntimeException("Неизвестный тип ViewType: $viewType в ShopListAdapter")
        }
        val view = LayoutInflater.from(parent.context).inflate(
            layoutId,
            parent,
            false
        )
        return ShopItemViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return if (item.enabled) {
            ENABLED_VIEW
        } else {
            DISABLED_VIEW
        }
    }


    override fun onBindViewHolder(holder: ShopItemViewHolder, position: Int) {
        val shopItem = getItem(position)
        holder.textViewShopItemName.text = shopItem.name
        holder.textViewShopItemQuantity.text = shopItem.count.toString()

        holder.itemView.setOnLongClickListener {
            //invoke равнозначен просто вызову функции,
            // но поскольку тип нуллабельный, значит надо использовать invoke
            onShopItemLongClickListener?.invoke(shopItem)
            true
        }
    }
}
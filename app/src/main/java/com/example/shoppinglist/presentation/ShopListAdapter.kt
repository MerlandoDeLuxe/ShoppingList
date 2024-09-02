package com.example.shoppinglist.presentation

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.shoppinglist.R
import com.example.shoppinglist.domain.ShopItem

class ShopListAdapter : RecyclerView.Adapter<ShopListAdapter.ShopItemViewHolder>() {

    companion object{
        private const val TAG = "ShopListAdapter"
        const val ENABLED_VIEW = 100
        const val DISABLED_VIEW = 200
        private var count = 0;
    }

    //Поскольку у нас объявлен функциональный интерфейс, то мы можем создать переменную
    //и передать сразу в неё функцию. То есть переменная содержит функцию,
    //которая принимает в качестве аргумента ShopItem и ничего не возвращает
    //А если в неё ничего не прилетело, значит в ней null и она может быть нуллабельной
    var onShopItemLongClickListener: ((ShopItem) -> Unit)? = null

    var onShopItemClickListener: ((ShopItem) -> Unit)? = null

    var shopItemList: List<ShopItem> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ShopItemViewHolder(itemView: View) : ViewHolder(itemView) {
        val textViewShopItemName = itemView.findViewById<TextView>(R.id.textViewShopItemName)
        val textViewShopItemQuantity =
            itemView.findViewById<TextView>(R.id.textViewShopItemQuantity)
    }

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
        if (shopItemList.get(position).enabled) {
            return ENABLED_VIEW
        } else {
            return DISABLED_VIEW
        }
    }

    override fun onBindViewHolder(holder: ShopItemViewHolder, position: Int) {
        holder.textViewShopItemName.text = shopItemList.get(position).name
        holder.textViewShopItemQuantity.text = shopItemList[position].count.toString()

        holder.itemView.setOnLongClickListener {
            //invoke равнозначен просто вызову функции,
            // но поскольку тип нуллабельный, значит надо использовать invoke
            onShopItemLongClickListener?.invoke(shopItemList.get(position))
            true
        }

        holder.itemView.setOnClickListener {
            onShopItemClickListener?.invoke(shopItemList.get(position))
        }
    }

    fun interface OnShopItemLongClickListener{
        fun onShopItemLongClick(shopItem: ShopItem)
    }

    override fun getItemCount(): Int {
        return shopItemList.size
    }
}
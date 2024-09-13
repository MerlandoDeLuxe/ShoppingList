package com.example.shoppinglist.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.shoppinglist.R
import com.example.shoppinglist.domain.ShopItem
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ShopItemActivity : AppCompatActivity(), ShopItemFragment.OnEditingFinishedListener {
    private var screenMode = MODE_UNKNOWN
    private var shopItemId = ShopItem.UNDEFINED_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_item_contrainer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.shop_item_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        parseIntent()
        if (savedInstanceState == null) { //Чтобы фрагмент не пересоздавался при перевороте активити дважды:
            //тут и в shopitemfragment, проверяем, что состояние null =
            //то есть переворотов еще не было. Значит только 1 раз вызываем создание фрагмента
            //То есть если фрагмент уже был добавлен на экран, то добавлять его при перевороте заново не нужно, система это сделает за нас
            screenSelection()
        }
    }

    override fun onEditingFinished(message: String) {
        finish()
    }

    private fun screenSelection() {
        val fragment =
            when (screenMode) {
                MODE_ADD -> ShopItemFragment.newInstanceAddItem()
                MODE_EDIT -> ShopItemFragment.newInstanceEditItem(shopItemId)
                else -> throw RuntimeException("Неизвестный тип экрана $screenMode")
            }
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.shop_item_container,
                fragment
            ).commit()
    }

    fun parseIntent() {
        if (!intent.hasExtra(EXTRA_SCREEN_MODE)) {
            throw RuntimeException("Отсутствует параметр режима экрана")
        }
        val mode = intent.getStringExtra(EXTRA_SCREEN_MODE)
        if (mode != MODE_ADD && mode != MODE_EDIT) {
            throw RuntimeException("Неизвестный тип экрана $mode")
        }
        screenMode = mode
        if (screenMode == MODE_EDIT) {
            if (!intent.hasExtra(EXTRA_SHOP_ITEM_ID)) {
                throw RuntimeException("Не задан ID у элемента")
            }
            shopItemId = intent.getIntExtra(EXTRA_SHOP_ITEM_ID, -1)
        }
    }

    companion object {
        private const val TAG = "ShopItemActivity"
        private const val EXTRA_SCREEN_MODE = "extra_screen_mode"
        private const val EXTRA_SHOP_ITEM_ID = "extra_shop_item_id"
        private const val MODE_ADD = "mode_add"
        private const val MODE_EDIT = "mode_edit"
        private const val MODE_UNKNOWN = ""

        fun newIntentAddItem(context: Context): Intent {
            val intent = Intent(context, ShopItemActivity::class.java)
            intent.putExtra(EXTRA_SCREEN_MODE, MODE_ADD)
            return intent
        }

        fun newIntentEditItem(context: Context, id: Int): Intent {
            val intent = with(Intent(context, ShopItemActivity::class.java)) {
                putExtra(EXTRA_SHOP_ITEM_ID, id)
                putExtra(EXTRA_SCREEN_MODE, MODE_EDIT)
            }
            return intent
        }
    }
}
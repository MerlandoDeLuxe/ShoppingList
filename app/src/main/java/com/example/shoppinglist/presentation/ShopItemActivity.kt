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

class ShopItemActivity : AppCompatActivity() {
    private lateinit var textInputLayoutName: TextInputLayout
    private lateinit var editTextName: TextInputEditText
    private lateinit var textInputLayoutQuantity: TextInputLayout
    private lateinit var editTextQuantity: TextInputEditText
    private lateinit var buttonSave: Button
    private var screenMode = MODE_UNKNOWN
    private var shopItemId = ShopItem.UNDEFINED_ID

    private lateinit var viewModel: ShopItemViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_shop_item)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.shop_item_fragment)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        parseIntent()
//        initializeAllElements()
//        screenSelection()
//        observeViewModel()
//        addTextChangeListeners()
    }

    private fun observeViewModel() {
        viewModel.shouldCloseScreenLD.observe(this) {
            finish()
        }

        viewModel.errorInputNameLD.observe(this) {
            Log.d(TAG, "observeViewModel: it = $it")
            if (it) {
                textInputLayoutName.error = getString(R.string.error_name)
            } else {
                textInputLayoutName.error = null
            }
        }

        viewModel.errorInputQuantityLD.observe(this) {
            if (it) {
                textInputLayoutQuantity.error = getString(R.string.error_quantity)
            } else {
                textInputLayoutQuantity.error = null
            }
        }
    }

    private fun addTextChangeListeners() {
        editTextName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.resetErrorInputName()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        editTextQuantity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.resetErrorInputQuantity()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun screenSelection() {
        when (screenMode) {
            MODE_ADD -> launchAddScreenMode()
            MODE_EDIT -> launchEditScreenMode()
        }
    }

    fun launchEditScreenMode() {
        viewModel.getShopItem(shopItemId)
        viewModel.shopItemLD.observe(this) {
            editTextName.setText(it.name)
            editTextQuantity.setText(it.count.toString())
        }
        buttonSave.setOnClickListener {
            viewModel.editShopItem(editTextName.text?.toString(), editTextQuantity.text?.toString())
        }
    }

    fun launchAddScreenMode() {
        buttonSave.setOnClickListener {
            viewModel.addShopItem(editTextName.text.toString(), editTextQuantity.text.toString())
        }
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

    private fun initializeAllElements() {
        textInputLayoutName = findViewById(R.id.textInputLayoutName)
        editTextName = findViewById(R.id.editTextName)
        textInputLayoutQuantity = findViewById(R.id.textInputLayoutQuantity)
        editTextQuantity = findViewById(R.id.editTextQuantity)
        buttonSave = findViewById(R.id.buttonSave)

        viewModel = ViewModelProvider(this).get(ShopItemViewModel::class.java)
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
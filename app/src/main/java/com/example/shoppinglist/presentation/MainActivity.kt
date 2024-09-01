package com.example.shoppinglist.presentation

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.shoppinglist.R
import com.example.shoppinglist.data.ShopListRepositoryImpl
import com.example.shoppinglist.domain.ShopItem

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var viewModel: MainViewModel
    private lateinit var buttonUpdate: Button
    private lateinit var buttonRemove: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeAllElements()

        viewModel.shopListLD.observe(this) {
            Log.d(TAG, "onCreate: it = ${it.toString()}")
        }

        buttonUpdate.setOnClickListener {
            val shopItem = ShopItem("Имя 1", 10, false, 0)
            viewModel.editShopListItemAndSaveToDB(shopItem)
        }

        buttonRemove.setOnClickListener {
            val shopItem = ShopItem("Имя 0", 0, true, 1)
            viewModel.removeShopItemFromDB(shopItem)
        }
    }

    private fun initializeAllElements() {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        buttonUpdate = findViewById(R.id.buttonUpdate)
        buttonRemove = findViewById(R.id.buttonRemove)
    }
}
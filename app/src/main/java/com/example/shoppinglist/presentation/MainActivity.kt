package com.example.shoppinglist.presentation

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.example.shoppinglist.domain.ShopItem

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var viewModel: MainViewModel
    private lateinit var recycleView: RecyclerView
    private lateinit var shopListAdapter: ShopListAdapter

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
        observeViewModel()
        setupOnClickListener()
        setupSwipeClickListener()
    }

    private fun observeViewModel() {
        viewModel.getShopListFromDB().observe(this,{
            Log.d(TAG, "observeViewModel: из бд прилетел лист: $it")
            Log.d(TAG, "observeViewModel: в адаптере лист: ${shopListAdapter.currentList}")
            Log.d(TAG, "notifyDataSetChanged: в адаптере лист: ${shopListAdapter.currentList}")
            shopListAdapter.submitList(it)

            Log.d(TAG, "observeViewModel: в адаптере лист: ${shopListAdapter.currentList}")
            Log.d(TAG, "=====================================================================")
        })

//        viewModel.shopListFromDB.observe(this) {
//
//            shopListAdapter.submitList(it)
//        }
    }

    private fun setupOnClickListener() {
        shopListAdapter.onShopItemLongClickListener = {
           // Log.d(TAG, "setupOnClickListener: долгое нажатие на: $it")
            viewModel.editShopListItemAndSaveToDB(it)
            //Log.d(TAG, "setupOnClickListener: долгое нажатие на: $it")
        }
    }

    private fun setupSwipeClickListener() {
        val itemTouchHelperCallback = object :
            ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val shopItem = shopListAdapter.currentList.get(viewHolder.adapterPosition)
                viewModel.removeShopItemFromDB(shopItem)
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recycleView)
    }

    private fun initializeAllElements() {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        shopListAdapter = ShopListAdapter()
        recycleView = findViewById(R.id.recycleView)
        with(recycleView) {
            recycledViewPool.setMaxRecycledViews(
                ShopListAdapter.ENABLED_VIEW,
                ShopListAdapter.MAX_POOL_SIZE
            )
            recycledViewPool.setMaxRecycledViews(
                ShopListAdapter.DISABLED_VIEW,
                ShopListAdapter.MAX_POOL_SIZE
            )
            //setHasFixedSize(true)
            adapter = shopListAdapter
        }
    }
}
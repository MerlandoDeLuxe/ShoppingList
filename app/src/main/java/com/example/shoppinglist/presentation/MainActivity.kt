package com.example.shoppinglist.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.example.shoppinglist.domain.ShopItem
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private lateinit var imageViewNewShopItem: FloatingActionButton
    private lateinit var viewModel: MainViewModel
    private lateinit var recycleView: RecyclerView
    private lateinit var shopListAdapter: ShopListAdapter

    private var shopItemContainer: FragmentContainerView? = null

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
        launchFragmentOnMainScreen()

    }

    fun isVerticalOrientation() =
        shopItemContainer == null    //Метод, проверяющий, что мы в вертикальном режиме.

    fun launchFragmentOnMainScreen(shopItemId: Int = ShopItem.UNDEFINED_ID) {
        supportFragmentManager.popBackStack() //Удалить из бекстека предыдущий фрагмент. Если его там нет, то он ничего не делает
        //Если мы не в вертикальной, а горизонтальной ориентации, то сразу вызываем отображение фрагмента на добавление элемента
        Log.d(TAG, "onCreate: isVerticalOrientation = ${isVerticalOrientation()}")
        if (!isVerticalOrientation()) {
            if (shopItemId != ShopItem.UNDEFINED_ID) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.shop_item_container, ShopItemFragment.newInstanceEditItem(shopItemId))
                    .addToBackStack(null)
                    .commit()
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.shop_item_container, ShopItemFragment.newInstanceAddItem())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.getShopListFromDB().observe(this) {
            shopListAdapter.submitList(it)
        }
    }

    private fun setupOnClickListener() {
        shopListAdapter.onShopItemLongClickListener = {
            viewModel.editShopListItemAndSaveToDB(it)
        }

        shopListAdapter.onShopItemClickListener = {
            Log.d(TAG, "setupOnClickListener: isVerticalOrientstion = ${isVerticalOrientation()}")
            if (!isVerticalOrientation()) {
                launchFragmentOnMainScreen(it.id)
            } else {
                val intent = ShopItemActivity.newIntentEditItem(this, it.id)
                startActivity(intent)
            }
        }

        imageViewNewShopItem.setOnClickListener {
            if (!isVerticalOrientation()) {
                launchFragmentOnMainScreen()
            } else {
                val intent = ShopItemActivity.newIntentAddItem(this)
                startActivity(intent)
            }
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
        imageViewNewShopItem = findViewById(R.id.imageViewNewShopItem)
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
            adapter = shopListAdapter
        }
        shopItemContainer = findViewById(R.id.shop_item_container)
    }
}
package com.example.shoppinglist.presentation

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.shoppinglist.R
import com.example.shoppinglist.domain.ShopItem
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ShopItemFragment : Fragment() {

    private lateinit var onEditingFinishedListener: OnEditingFinishedListener

    private lateinit var textInputLayoutName: TextInputLayout
    private lateinit var editTextName: TextInputEditText
    private lateinit var textInputLayoutQuantity: TextInputLayout
    private lateinit var editTextQuantity: TextInputEditText
    private lateinit var buttonSave: Button

    private lateinit var viewModel: ShopItemViewModel

    private var screenMode: String = MODE_UNKNOWN
    private var shopItemId: Int = ShopItem.UNDEFINED_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: ")
        parseParams()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: ")
        return inflater.inflate(R.layout.fragment_shop_item_template, container, false)
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach: ")
        super.onAttach(context)
        if (context is OnEditingFinishedListener) {
            onEditingFinishedListener = context
        } else {
            throw RuntimeException(getString(R.string.interface_is_not_init_in_module, context))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")
        initializeAllElements(view)
        screenSelection()
        observeViewModel()
        addTextChangeListeners()
    }

    fun screenSelection() {
        when (screenMode) {
            MODE_ADD -> launchAddScreenMode()
            MODE_EDIT -> launchEditScreenMode()
        }
    }

    private fun observeViewModel() {
        viewModel.shouldCloseScreenLD.observe(viewLifecycleOwner) {
            onEditingFinishedListener.onEditingFinished(getString(R.string.success))
        }

        viewModel.errorInputNameLD.observe(viewLifecycleOwner) {
            if (it) {
                textInputLayoutName.error = getString(R.string.error_name)
            } else {
                textInputLayoutName.error = null
            }
        }

        viewModel.errorInputQuantityLD.observe(viewLifecycleOwner) {
            if (it) {
                textInputLayoutQuantity.error = getString(R.string.error_quantity)
            } else {
                textInputLayoutQuantity.error = null
            }
        }
    }



    private fun addTextChangeListeners() {

        editTextName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.resetErrorInputName()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        editTextQuantity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.resetErrorInputQuantity()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    fun launchEditScreenMode() {
        viewModel.getShopItem(shopItemId)
        viewModel.shopItemLD.observe(viewLifecycleOwner) {
            editTextName.setText(it.name)
            editTextQuantity.setText(it.count.toString())
        }
        buttonSave.setOnClickListener {
            viewModel.editShopItem(editTextName.text?.toString(), editTextQuantity.text?.toString())
        }

        //Проверка, что элемент не был удален в процессе редактирования.
        // Если это случилось, то выходим из редактирования, вызывая метод интерфейса
        viewModel.monitoringShopItemExists(shopItemId).observe(viewLifecycleOwner) {
            Log.d(TAG, "launchEditScreenMode: it = $it")
            if (it == null) {
                onEditingFinishedListener.onEditingFinished(getString(R.string.element_was_removed))
            }
        }
    }

    fun launchAddScreenMode() {
        buttonSave.setOnClickListener {
            viewModel.addShopItem(editTextName.text.toString(), editTextQuantity.text.toString())
        }
    }

    fun parseParams() {
        val args = requireArguments()
        if (!args.containsKey(SCREEN_MODE)) {
            throw RuntimeException("Отсутствует параметр режима экрана")
        }
        val mode = args.getString(SCREEN_MODE)
        if (mode != MODE_ADD && mode != MODE_EDIT) {
            throw RuntimeException("Неизвестный тип экрана $mode")
        }
        screenMode = mode
        if (screenMode == MODE_EDIT) {
            if (!args.containsKey(SHOP_ITEM_ID)) {
                throw RuntimeException("Не задан ID у элемента")
            }
            shopItemId = args.getInt(SHOP_ITEM_ID, ShopItem.UNDEFINED_ID)
        }
    }

    private fun initializeAllElements(view: View) {
        textInputLayoutName = view.findViewById(R.id.textInputLayoutName)
        editTextName = view.findViewById(R.id.editTextName)
        textInputLayoutQuantity = view.findViewById(R.id.textInputLayoutQuantity)
        editTextQuantity = view.findViewById(R.id.editTextQuantity)
        buttonSave = view.findViewById(R.id.buttonSave)

        viewModel = ViewModelProvider(this).get(ShopItemViewModel::class.java)
    }

    interface OnEditingFinishedListener {
        fun onEditingFinished(message: String)
    }

    companion object {
        private const val TAG = "ShopItemFragment"
        private const val SCREEN_MODE = "extra_screen_mode"
        private const val SHOP_ITEM_ID = "extra_shop_item_id"
        private const val MODE_ADD = "mode_add"
        private const val MODE_EDIT = "mode_edit"
        private const val MODE_UNKNOWN = ""

        fun newInstanceAddItem(): ShopItemFragment {
            return ShopItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_ADD)
                }
            }
        }

        fun newInstanceEditItem(shopItemId: Int): ShopItemFragment {
            return ShopItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_EDIT)
                    putInt(SHOP_ITEM_ID, shopItemId)
                }
            }
        }
    }
}
package com.brandonjamesyoung.levelup.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.data.ShopItem
import com.brandonjamesyoung.levelup.constants.Mode
import com.brandonjamesyoung.levelup.validation.InputValidator
import com.brandonjamesyoung.levelup.viewmodels.NewItemViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class NewItem : Fragment(R.layout.new_item) {
    private val viewModel: NewItemViewModel by activityViewModels()

    private var mode: MutableLiveData<Mode> = MutableLiveData<Mode>()

    private val args: NewItemArgs by navArgs()

    @Inject lateinit var validator: InputValidator

    private fun loadPointsAcronym() = lifecycleScope.launch(Dispatchers.IO) {
        val settings = viewModel.getSettings()
        val view = requireView()
        val acronymLabel = view.findViewById<TextView>(R.id.CostRtLabel)

        withContext(Dispatchers.Main) {
            acronymLabel.text = settings.pointsAcronym
        }
    }

    private fun navigateToShop() {
        findNavController().navigate(R.id.action_newItem_to_shop)
        Log.i(TAG, "Going from New Item to Shop")
    }

    private fun addNavigation() {
        val view = requireView()
        val button = view.findViewById<View>(R.id.CancelButton)

        button.setOnClickListener{
            navigateToShop()
        }
    }

    private fun isValidInput() : Boolean {
        val view = requireView()
        val nameView = view.findViewById<EditText>(R.id.ItemNameInput)
        if (!validator.isValidQuestName(nameView, TAG, this)) return false
        val costView = view.findViewById<EditText>(R.id.CostInput)

        return validator.isValidNum(
            editText = costView,
            minNumber = -9999,
            maxNumber = 99999,
            emptyValuesAllowed = true,
            resources = resources
        )
    }

    private fun saveItem() {
        val view = requireView()
        val nameInput = view.findViewById<EditText>(R.id.ItemNameInput)
        var name: String? = nameInput.text.trim().toString()

        if (name == "") {
            name = null
        }

        val costInput = view.findViewById<EditText>(R.id.CostInput)
        val costInputText = costInput.text
        val cost = if (costInputText.isBlank()) DEFAULT_COST else costInputText.toString().toInt()

        val shopItem = ShopItem(
            name = name,
            cost = cost,
        )

        if (mode.value == Mode.DEFAULT) {
            viewModel.insert(shopItem)
        } else if (mode.value == Mode.EDIT) {
            shopItem.id = args.itemId
            viewModel.update(shopItem)
        }
    }

    private fun setupConfirmButton() {
        val view = requireView()
        val saveButton = view.findViewById<AppCompatButton>(R.id.ConfirmButton)

        saveButton.setOnClickListener {
            if (isValidInput()){
                saveItem()
                navigateToShop()
            }
        }
    }

    private fun loadItem(shopItem: ShopItem) {
        val view = requireView()
        val nameInput = view.findViewById<EditText>(R.id.ItemNameInput)
        nameInput.setText(shopItem.name)
        val costInput = view.findViewById<EditText>(R.id.CostInput)
        costInput.setText(shopItem.cost.toString())
    }

    private fun activateEditMode() {
        val view = requireView()
        val pageLabel = view.findViewById<TextView>(R.id.NewItemLabel)
        pageLabel.text = getString(R.string.edit_item_label)

        viewModel.getItem(args.itemId).observe(viewLifecycleOwner) { item ->
            loadItem(item)
        }
    }

    private fun setupMode() {
        mode.value = Mode.DEFAULT

        if (args.itemId != INVALID_ITEM_ID) {
            mode.value = Mode.EDIT
        }

        mode.observe(viewLifecycleOwner) { mode ->
            when (mode) {
                Mode.DEFAULT -> Unit
                Mode.EDIT -> activateEditMode()
                else -> Log.e(TAG, "Unknown mode detected")
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            Log.i(TAG, "On New Item page")
            addNavigation()
            setupConfirmButton()
            setupMode()
            loadPointsAcronym()
        }
    }

    companion object {
        private const val TAG = "NewItem"
        private const val INVALID_ITEM_ID = 0
        private const val DEFAULT_COST = 0
    }
}
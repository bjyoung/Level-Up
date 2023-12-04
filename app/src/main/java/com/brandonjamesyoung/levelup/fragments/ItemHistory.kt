package com.brandonjamesyoung.levelup.fragments

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.constants.ITEM_ROW_LANDSCAPE_WIDTH_DP
import com.brandonjamesyoung.levelup.data.PurchasedItem
import com.brandonjamesyoung.levelup.utility.ButtonConverter
import com.brandonjamesyoung.levelup.utility.OrientationManager
import com.brandonjamesyoung.levelup.viewmodels.ItemHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ItemHistory : Fragment(R.layout.item_history) {
    private val viewModel: ItemHistoryViewModel by activityViewModels()

    @Inject lateinit var buttonConverter: ButtonConverter

    private fun navigateToShop() {
        NavHostFragment.findNavController(this).navigate(R.id.action_itemHistory_to_shop)
        Log.i(TAG, "Going from Item History to Shop")
    }

    private fun activateShopButton() {
        val view = requireView()
        val questListButton = view.findViewById<Button>(R.id.ShopButton)

        questListButton.setOnClickListener {
            navigateToShop()
        }
    }

    private fun createItemRow(
        purchasedItem: PurchasedItem,
        parentLayout: ViewGroup
    ) : ConstraintLayout {
        val newItemRow = layoutInflater.inflate(
            R.layout.item_row,
            parentLayout,
            false
        ) as ConstraintLayout

        newItemRow.id = View.generateViewId()
        val itemName = newItemRow.findViewById<TextView>(R.id.ItemName)
        val defaultName = getString(R.string.placeholder_text)

        // TODO instead of setting a constant for item row width, set up constraints so
        //  that item name is automatically extended no matter what device it is on
        if (!OrientationManager.inPortraitMode(resources)) {
            val itemNameLandscapeWidth = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                ITEM_ROW_LANDSCAPE_WIDTH_DP,
                resources.displayMetrics
            )

            itemName.layoutParams.width = itemNameLandscapeWidth.toInt()
        }

        itemName.text = purchasedItem.name ?: defaultName
        val itemCost = newItemRow.findViewById<TextView>(R.id.ItemCost)
        itemCost.text = purchasedItem.cost.toString()
        return newItemRow
    }

    private fun addItemRow(purchasedItem: PurchasedItem) {
        val view = requireView()
        val itemHistoryLinearLayout = view.findViewById<LinearLayout>(R.id.ItemHistoryLinearLayout)
        val itemRow = createItemRow(purchasedItem, itemHistoryLinearLayout)
        itemHistoryLinearLayout.addView(itemRow)
    }

    private fun setupObservables() {
        viewModel.itemHistoryList.observe(viewLifecycleOwner) { itemList ->
            val view = requireView()
            val itemListLayout = view.findViewById<LinearLayout>(R.id.ItemHistoryLinearLayout)
            itemListLayout.removeAllViews()
            val sortedItemHistory = itemList.sortedByDescending { it.datePurchased }
            sortedItemHistory.forEach { item -> addItemRow(item) }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch{
            Log.i(TAG, "On Item History page")
            activateShopButton()
            setupObservables()
        }
    }

    companion object {
        private const val TAG = "ItemHistory"
    }
}
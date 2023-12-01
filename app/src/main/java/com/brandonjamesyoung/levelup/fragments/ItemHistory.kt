package com.brandonjamesyoung.levelup.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.data.PurchasedItem
import com.brandonjamesyoung.levelup.utility.ButtonConverter
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
        buttonConverter.convertNavButton(
            targetId = R.id.ShopButton,
            iconDrawableId = R.drawable.shopping_bag_icon_large,
            buttonMethod = ::navigateToShop,
            view = requireView(),
            resources = resources
        )
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
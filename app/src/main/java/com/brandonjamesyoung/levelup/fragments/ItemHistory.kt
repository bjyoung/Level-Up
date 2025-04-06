package com.brandonjamesyoung.levelup.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.data.PurchasedItem
import com.brandonjamesyoung.levelup.utility.ButtonConverter
import com.brandonjamesyoung.levelup.utility.InsetHandler
import com.brandonjamesyoung.levelup.utility.ItemTableManager
import com.brandonjamesyoung.levelup.viewmodels.ItemHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ItemHistory : Fragment(R.layout.item_history) {
    private val viewModel: ItemHistoryViewModel by activityViewModels()

    @Inject lateinit var buttonConverter: ButtonConverter

    @Inject lateinit var itemTableManager: ItemTableManager

    private fun navigateToShop() {
        findNavController().navigate(R.id.action_itemHistory_to_shop)
        Log.i(TAG, "Going from Item History to Shop")
    }

    private fun activateShopButton() {
        val view = requireView()
        val questListButton = view.findViewById<Button>(R.id.ShopButton)

        questListButton.setOnClickListener {
            navigateToShop()
        }
    }

    private fun addItemRow(purchasedItem: PurchasedItem) {
        val view = requireView()
        val itemHistoryLinearLayout = view.findViewById<LinearLayout>(R.id.ItemHistoryLinearLayout)

        val itemRow: ConstraintLayout = itemTableManager.createItemRow(
            purchasedItem,
            layoutInflater,
            itemHistoryLinearLayout
        )

        itemHistoryLinearLayout.addView(itemRow)
    }

    private fun showNoItemsMessage() {
        val view = requireView()
        val noIconsMessage = view.findViewById<TextView>(R.id.NoItemsMessage)
        noIconsMessage.visibility = View.VISIBLE
    }

    private fun hideNoItemsMessage() {
        val view = requireView()
        val noIconsMessage = view.findViewById<TextView>(R.id.NoItemsMessage)
        noIconsMessage.visibility = View.GONE
    }

    private fun setupObservables() {
        viewModel.itemHistoryList.observe(viewLifecycleOwner) { itemList ->
            val view = requireView()
            val itemListLayout = view.findViewById<LinearLayout>(R.id.ItemHistoryLinearLayout)
            itemListLayout.removeAllViews()
            val sortedItemHistory = itemList.sortedByDescending { it.datePurchased }
            sortedItemHistory.forEach { item -> addItemRow(item) }
            if (itemList.isEmpty()) showNoItemsMessage() else hideNoItemsMessage()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        InsetHandler.addInsetPadding(requireView())

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
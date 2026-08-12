package com.brandonjamesyoung.levelup.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.data.ItemRow
import com.brandonjamesyoung.levelup.data.PurchasedItem
import com.brandonjamesyoung.levelup.compose.ItemTableCreator
import com.brandonjamesyoung.levelup.utility.InsetHandler
import com.brandonjamesyoung.levelup.viewmodels.ItemHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ItemHistory : Fragment(R.layout.item_history) {
    private val viewModel: ItemHistoryViewModel by activityViewModels()

    @Inject lateinit var itemTableCreator: ItemTableCreator

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

    private fun reloadTable(items: List<PurchasedItem>) {
        if (items.isEmpty()) showNoItemsMessage() else hideNoItemsMessage()

        val itemRows: List<ItemRow> = items.map {
            ItemRow(it, false)
        }

        val composeView = requireView().findViewById<ComposeView>(R.id.ItemHistoryTableComposeView)

        composeView.setContent {
            itemTableCreator.ItemTableView(
                itemRows = itemRows
            )
        }
    }

    private fun setupObservables() {
        viewModel.itemHistoryList.observe(viewLifecycleOwner) { itemList ->
            val sortedItems = itemList.sortedByDescending { it.datePurchased }
            reloadTable(sortedItems)
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
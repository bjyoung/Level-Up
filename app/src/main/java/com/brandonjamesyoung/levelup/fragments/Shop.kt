package com.brandonjamesyoung.levelup.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.data.ShopItem
import com.brandonjamesyoung.levelup.data.Player
import com.brandonjamesyoung.levelup.utility.ButtonConverter
import com.brandonjamesyoung.levelup.constants.Mode
import com.brandonjamesyoung.levelup.utility.ItemTableManager
import com.brandonjamesyoung.levelup.utility.PointsDisplay
import com.brandonjamesyoung.levelup.utility.SnackbarHelper.Companion.showSnackbar
import com.brandonjamesyoung.levelup.viewmodels.ShopViewModel
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class Shop : Fragment(R.layout.shop) {
    private val viewModel: ShopViewModel by activityViewModels()

    private val selectedItemIds: MutableSet<Int> = mutableSetOf()

    private val selectedItemRowIds: MutableSet<Int> = mutableSetOf()

    @Inject lateinit var buttonConverter: ButtonConverter

    @Inject lateinit var itemTableManager: ItemTableManager

    @Inject lateinit var pointsDisplay: PointsDisplay

    private var pointsLoaded: Boolean = false

    private fun navigateToNewItem(itemId: Int? = null) {
        val action = if (itemId != null) {
            ShopDirections.actionShopToNewItem(itemId)
        } else {
            ShopDirections.actionShopToNewItem()
        }

        findNavController().navigate(action)
        Log.i(TAG, "Going from Shop to New Item")
    }

    private fun activateNewItemButton() {
        buttonConverter.convertNavButton(
            targetId = R.id.AddNewItemButton,
            iconDrawableId = R.drawable.plus_icon_large,
            buttonMethod = ::navigateToNewItem,
            view = requireView(),
        )
    }

    private fun navigateToQuestList() {
        findNavController().navigate(R.id.action_shop_to_questList)
        Log.i(TAG, "Going from Shop to Quest List")
    }

    private fun activateQuestListButton() {
        buttonConverter.convertNavButton(
            targetId = R.id.QuestListButton,
            iconDrawableId = R.drawable.bullet_list_icon_large,
            buttonMethod = ::navigateToQuestList,
            view = requireView(),
        )
    }

    private fun navigateToItemHistory() {
        findNavController().navigate(R.id.action_shop_to_itemHistory)
        Log.i(TAG, "Going from Shop to Item History")
    }

    private fun navigateToSettings() {
        val action = ShopDirections.actionShopToSettings(R.id.Shop)
        findNavController().navigate(action)
        Log.i(TAG, "Going from Shop to Settings")
    }

    private fun activateSettingsButton() {
        buttonConverter.convertNavButton(
            targetId = R.id.ShopSettingsButton,
            iconDrawableId = R.drawable.gear_icon_large,
            buttonMethod = ::navigateToSettings,
            view = requireView(),
        )
    }

    private fun activateItemHistoryButton() {
        val view = requireView()
        val itemHistoryButton = view.findViewById<MaterialButton>(R.id.ItemHistoryButton)

        itemHistoryButton.setOnClickListener {
            navigateToItemHistory()
        }
    }

    private fun activateDefaultMode() {
        selectedItemIds.clear()
        selectedItemRowIds.clear()
        activateNewItemButton()
        activateQuestListButton()
        activateSettingsButton()
    }

    private fun cancelSelectedItems() {
        val view = requireView()
        val selectedRowIdCopy = selectedItemRowIds.toMutableList()

        for (id in selectedRowIdCopy) {
            // TODO probably better to de-select all programmatically
            //  instead of simulating button presses
            val itemRow : ConstraintLayout = view.findViewById(id)
            itemRow.callOnClick()
        }
    }

    private fun activateCancelButton() {
        buttonConverter.convertNavButton(
            targetId = R.id.ShopSettingsButton,
            iconDrawableId = R.drawable.cancel_icon_large,
            buttonMethod = ::cancelSelectedItems,
            view = requireView(),
        )
    }

    private fun deleteItems() {
        viewModel.deleteItems(selectedItemIds.toSet())
        viewModel.switchToDefaultMode()
    }

    private fun activateDeleteButton() {
        buttonConverter.convertNavButton(
            targetId = R.id.AddNewItemButton,
            iconDrawableId = R.drawable.trash_bin_icon_large,
            iconColorId = R.color.warning_icon,
            buttonMethod = ::deleteItems,
            view = requireView(),
        )
    }

    private fun buyItems() {
        viewModel.buyItems(selectedItemIds.toSet())
        cancelSelectedItems()
    }

    private fun activateBuyButton() {
        buttonConverter.convertNavButton(
            targetId = R.id.QuestListButton,
            iconDrawableId = R.drawable.shopping_cart_icon_large,
            iconColorId = R.color.confirm_icon,
            buttonMethod = ::buyItems,
            view = requireView(),
        )
    }

    private fun activateSelectMode() {
        activateCancelButton()
        activateDeleteButton()
        activateBuyButton()
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

    private fun isSelected(itemId: Int) : Boolean {
        return selectedItemIds.contains(itemId)
    }

    private fun selectItem(itemId: Int, itemRow: ConstraintLayout) {
        if (!isSelected(itemId)) {
            selectedItemIds.add(itemId)
            selectedItemRowIds.add(itemRow.id)

            val selectedColor: Int = resources.getColor(
                R.color.selected,
                requireContext().theme,
            )

            itemRow.setBackgroundColor(selectedColor)
        } else {
            selectedItemIds.remove(itemId)
            selectedItemRowIds.remove(itemRow.id)
            itemRow.setBackgroundColor(Color.TRANSPARENT)
        }

        if (selectedItemIds.isNotEmpty()) {
            viewModel.switchToSelectMode()
        } else {
            viewModel.switchToDefaultMode()
        }
    }

    private fun longPressItemRow(shopItem: ShopItem) {
        if (viewModel.mode.value == Mode.DEFAULT) {
            Log.i(TAG, "Item '${shopItem.name}' is long pressed")
            navigateToNewItem(shopItem.id)
        }
    }

    private fun addItemRow(shopItem: ShopItem) {
        val view = requireView()
        val itemListLayout = view.findViewById<LinearLayout>(R.id.ItemListLinearLayout)

        val itemRow: ConstraintLayout = itemTableManager.createItemRow(
            shopItem,
            layoutInflater,
            itemListLayout
        )

        itemRow.setOnClickListener{
            selectItem(shopItem.id, itemRow)
        }

        itemRow.setOnLongClickListener {
            longPressItemRow(shopItem)
            true
        }

        itemListLayout.addView(itemRow)
    }

    private fun updatePointsDisplay(player: Player?) {
        pointsDisplay.updatePointsText(
            player,
            R.id.PointsAmount,
            pointsLoaded,
            requireView()
        )

        pointsLoaded = true
    }

    private fun loadPointsAcronym() = lifecycleScope.launch(Dispatchers.IO) {
        val settings = viewModel.getSettings()
        val view = requireView()
        val pointsLabel : TextView = view.findViewById(R.id.PointsLabel)

        withContext(Dispatchers.Main) {
            pointsLabel.text = settings.pointsAcronym
        }
    }

    private fun setupObservables() {
        val view = requireView()
        activateItemHistoryButton()
        viewModel.switchToDefaultMode()

        viewModel.mode.observe(viewLifecycleOwner) { mode ->
            when (mode) {
                Mode.DEFAULT -> activateDefaultMode()
                Mode.SELECT -> activateSelectMode()
                else -> Log.e(TAG, "Unknown mode detected")
            }
        }

        viewModel.shopItemList.observe(viewLifecycleOwner) { itemList ->
            val itemListLayout = view.findViewById<LinearLayout>(R.id.ItemListLinearLayout)
            itemListLayout.removeAllViews()
            val sortedItemList = itemList.sortedBy { it.dateCreated }
            sortedItemList.forEach { item -> addItemRow(item) }
            if (itemList.isEmpty()) showNoItemsMessage() else hideNoItemsMessage()
        }

        viewModel.player.observe(viewLifecycleOwner) { player ->
            updatePointsDisplay(player)
        }

        viewModel.message.observe(viewLifecycleOwner) { message ->
            message.getContentIfNotHandled()?.let {
                val questListButton: View = view.findViewById(R.id.QuestListButton)
                showSnackbar(it, requireView(), questListButton)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch{
            Log.i(TAG, "On Shop page")
            loadPointsAcronym()
            setupObservables()
        }
    }

    companion object {
        private const val TAG = "Shop"
    }
}
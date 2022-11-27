package com.brandonjamesyoung.levelup.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.data.Item
import com.brandonjamesyoung.levelup.data.Player
import com.brandonjamesyoung.levelup.data.Settings
import com.brandonjamesyoung.levelup.shared.Mode
import com.brandonjamesyoung.levelup.ui.ButtonHelper.Companion.convertButton
import com.brandonjamesyoung.levelup.viewmodels.ShopViewModel
import kotlinx.coroutines.launch

private const val TAG = "Shop"

class Shop : Fragment(R.layout.shop) {
    private val viewModel: ShopViewModel by activityViewModels()
    private val selectedItemIds: MutableSet<Int> = mutableSetOf()
    private val selectedItemRowIds: MutableSet<Int> = mutableSetOf()
    private var mode: MutableLiveData<Mode> = MutableLiveData<Mode>()

    private fun navigateToNewItem() {
        NavHostFragment.findNavController(this).navigate(R.id.action_shop_to_newItem)
        Log.i(TAG, "Going from Shop to New Item")
    }

    private fun activateNewItemButton() {
        convertButton(
            targetId = R.id.AddNewItemButton,
            iconDrawableId = R.drawable.plus_icon,
            iconColorId = R.color.icon_primary,
            buttonMethod = ::navigateToNewItem,
            view = requireView(),
            resources = resources
        )
    }

    private fun navigateToQuestList() {
        NavHostFragment.findNavController(this).navigate(R.id.action_shop_to_questList)
        Log.i(TAG, "Going from Shop to Quest List")
    }

    private fun activateQuestListButton() {
        convertButton(
            targetId = R.id.QuestListButton,
            iconDrawableId = R.drawable.bullet_list_icon,
            iconColorId = R.color.icon_primary,
            buttonMethod = ::navigateToQuestList,
            view = requireView(),
            resources = resources
        )
    }

    private fun navigateToSettings() {
        NavHostFragment.findNavController(this).navigate(R.id.action_shop_to_settings)
        Log.i(TAG, "Going from Shop to Settings")
    }

    private fun activateSettingsButton() {
        convertButton(
            targetId = R.id.ShopSettingsButton,
            iconDrawableId = R.drawable.gear_icon,
            iconColorId = R.color.icon_primary,
            buttonMethod = ::navigateToSettings,
            view = requireView(),
            resources = resources
        )
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
        convertButton(
            targetId = R.id.ShopSettingsButton,
            iconDrawableId = R.drawable.cancel_icon,
            iconColorId = R.color.icon_primary,
            buttonMethod = ::cancelSelectedItems,
            view = requireView(),
            resources = resources
        )
    }

    private fun deleteItems() {
        viewModel.deleteItems(selectedItemIds.toSet())
        mode.value = Mode.DEFAULT
    }

    private fun activateDeleteButton() {
        convertButton(
            targetId = R.id.AddNewItemButton,
            iconDrawableId = R.drawable.trash_bin_icon,
            iconColorId = R.color.cancel,
            buttonMethod = ::deleteItems,
            view = requireView(),
            resources = resources
        )
    }

    private fun buyItems() {
        viewModel.buyItems(selectedItemIds.toSet())
        cancelSelectedItems()
        // TODO show toast that shows that items were purchased, potential Easter egg too?
    }

    private fun activateBuyButton() {
        convertButton(
            targetId = R.id.QuestListButton,
            iconDrawableId = R.drawable.shopping_cart_icon,
            iconColorId = R.color.confirm,
            buttonMethod = ::buyItems,
            view = requireView(),
            resources = resources
        )
    }

    private fun activateSelectMode() {
        activateCancelButton()
        activateDeleteButton()
        activateBuyButton()
    }

    private fun isSelected(itemId: Int) : Boolean {
        return selectedItemIds.contains(itemId)
    }

    private fun selectItem(itemId: Int, itemRow: ConstraintLayout) {
        val view = requireView()

        if (!isSelected(itemId)) {
            selectedItemIds.add(itemId)
            selectedItemRowIds.add(itemRow.id)

            val selectedColor: Int = resources.getColor(
                R.color.selected,
                view.context.theme
            )

            itemRow.setBackgroundColor(selectedColor)
        } else {
            selectedItemIds.remove(itemId)
            selectedItemRowIds.remove(itemRow.id)
            itemRow.setBackgroundColor(Color.TRANSPARENT)
        }

        mode.value = if (selectedItemIds.isNotEmpty()) Mode.SELECT else Mode.DEFAULT
    }

    private fun createItemRow(item: Item, parentLayout: ViewGroup) : ConstraintLayout {
        val newItemRow = layoutInflater.inflate(
            R.layout.item_row,
            parentLayout,
            false
        ) as ConstraintLayout

        newItemRow.id = View.generateViewId()
        val itemName = newItemRow.findViewById<TextView>(R.id.ItemName)
        val defaultName = resources.getString(R.string.placeholder_text)
        itemName.text = item.name ?: defaultName
        val itemCost = newItemRow.findViewById<TextView>(R.id.ItemCost)
        itemCost.text = item.cost.toString()

        newItemRow.setOnClickListener{
            selectItem(item.id, newItemRow)
        }

        return newItemRow
    }

    private fun addItemRow(item: Item) {
        val view = requireView()
        val itemListLayout = view.findViewById<LinearLayout>(R.id.ItemListLinearLayout)
        val itemRow = createItemRow(item, itemListLayout)
        itemListLayout.addView(itemRow)
    }

    private fun updatePoints(view: View, player: Player?) {
        val placeholderText = getString(R.string.placeholder_text)
        val rtStr = player?.rt?.toString() ?: placeholderText
        val pointsAmount = view.findViewById<TextView>(R.id.PointsAmount)
        pointsAmount.text = rtStr
    }

    private fun updatePointsAcronym(settings: Settings?) {
        if (settings == null) return
        val view = requireView()
        val pointsLabel : TextView = view.findViewById(R.id.PointsLabel)
        pointsLabel.text = settings.pointsAcronym
    }

    private fun setupObservables(view: View) {
        mode.value = Mode.DEFAULT

        mode.observe(viewLifecycleOwner) { mode ->
            when (mode) {
                Mode.DEFAULT -> activateDefaultMode()
                Mode.SELECT -> activateSelectMode()
                else -> Log.e(TAG, "Unknown mode detected")
            }
        }

        viewModel.itemList.observe(viewLifecycleOwner) { itemList ->
            val itemListLayout = view.findViewById<LinearLayout>(R.id.ItemListLinearLayout)
            itemListLayout.removeAllViews()
            val sortedItemList = itemList.sortedBy { it.dateCreated }

            for (item in sortedItemList) {
                addItemRow(item)
            }
        }

        viewModel.player.observe(viewLifecycleOwner) { player ->
            updatePoints(view, player)
        }

        viewModel.settings.observe(viewLifecycleOwner) { settings ->
            updatePointsAcronym(settings)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch{
            Log.i(TAG, "On Shop page")
            setupObservables(view)

            setFragmentResult(
                "PREV_FRAGMENT",
                bundleOf("FRAGMENT_ID" to R.id.Shop)
            )
        }
    }
}
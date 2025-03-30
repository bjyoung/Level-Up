package com.brandonjamesyoung.levelup.fragments

import android.graphics.Color
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
import com.brandonjamesyoung.levelup.data.ShopItem
import com.brandonjamesyoung.levelup.data.Player
import com.brandonjamesyoung.levelup.utility.ButtonConverter
import com.brandonjamesyoung.levelup.constants.Mode
import com.brandonjamesyoung.levelup.constants.POP_UP_BUTTON_WAIT_PERIOD
import com.brandonjamesyoung.levelup.constants.SortOrder
import com.brandonjamesyoung.levelup.constants.SortType
import com.brandonjamesyoung.levelup.data.Settings
import com.brandonjamesyoung.levelup.utility.InsetHandler
import com.brandonjamesyoung.levelup.utility.ItemTableManager
import com.brandonjamesyoung.levelup.utility.PointsDisplay
import com.brandonjamesyoung.levelup.utility.SnackbarHelper.Companion.showSnackbar
import com.brandonjamesyoung.levelup.utility.SortButtonManager
import com.brandonjamesyoung.levelup.viewmodels.ShopViewModel
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.timer

@AndroidEntryPoint
class Shop : Fragment(R.layout.shop) {
    private val viewModel: ShopViewModel by activityViewModels()

    private val selectedItemIds: MutableSet<Int> = mutableSetOf()

    private val selectedItemRowIds: MutableSet<Int> = mutableSetOf()

    @Inject lateinit var buttonConverter: ButtonConverter

    @Inject lateinit var itemTableManager: ItemTableManager

    @Inject lateinit var pointsDisplay: PointsDisplay

    @Inject lateinit var sorter: SortButtonManager

    private var pointsLoaded: Boolean = false

    private var sortTimer: Timer? = null

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
            tooltip = getString(R.string.add_item_button_tooltip),
            view = requireView()
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
            tooltip = getString(R.string.quest_list_button_tooltip),
            view = requireView()
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
            tooltip = getString(R.string.settings_button_tooltip),
            view = requireView()
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
        Log.i(TAG, "Activating DEFAULT mode in Shop")
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
            val itemRow: ConstraintLayout = view.findViewById(id)
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
        viewModel.switchMode(Mode.DEFAULT)
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
        Log.i(TAG, "Activating SELECT mode in Shop")
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

    private fun highlightRow(itemRow: ConstraintLayout) {
        val selectedColor: Int = resources.getColor(
            R.color.selected,
            requireContext().theme,
        )

        itemRow.setBackgroundColor(selectedColor)
    }

    private fun selectItem(itemId: Int, itemRow: ConstraintLayout) {
        Log.i(TAG, "Selecting item $itemId")
        selectedItemIds.add(itemId)
        selectedItemRowIds.add(itemRow.id)
        highlightRow(itemRow)
    }

    private fun deselectItem(itemId: Int, itemRow: ConstraintLayout) {
        Log.i(TAG, "De-selecting item $itemId")
        selectedItemIds.remove(itemId)
        selectedItemRowIds.remove(itemRow.id)
        itemRow.setBackgroundColor(Color.TRANSPARENT)
    }

    private fun tapItem(itemId: Int, itemRow: ConstraintLayout) {
        if (!viewModel.mode.hasObservers()) setupModeObserver()
        if (!isSelected(itemId)) selectItem(itemId, itemRow) else deselectItem(itemId, itemRow)
        val targetMode = if (selectedItemIds.isNotEmpty()) Mode.SELECT else Mode.DEFAULT
        viewModel.switchMode(targetMode)
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

        if (selectedItemIds.contains(shopItem.id)) {
            highlightRow(itemRow)
            selectedItemRowIds.add(itemRow.id)
        }

        itemRow.setOnClickListener{
            tapItem(shopItem.id, itemRow)
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

    private fun startSortTimer() {
        val sortButton: Button = requireView().findViewById(R.id.SortButton)
        val sortTrigger: Button = requireView().findViewById(R.id.SortTrigger)
        val waitPeriod: Long = POP_UP_BUTTON_WAIT_PERIOD

        sortTimer = timer(initialDelay = waitPeriod, period = waitPeriod) {
            lifecycleScope.launch {
                sorter.hideSortButton(sortButton, sortTrigger)
                sortTimer?.cancel()
                sortTimer = null
            }
        }
    }

    private fun setupSortTrigger() {
        val view = requireView()
        val sortButton: Button = view.findViewById(R.id.SortButton)
        val sortTrigger: Button = view.findViewById(R.id.SortTrigger)

        sortTrigger.setOnClickListener {
            sorter.showSortButton(sortButton, sortTrigger)
            startSortTimer()
        }
    }

    private fun setupSortButton() {
        val sortButton: Button = requireView().findViewById(R.id.SortButton)

        sortButton.setOnClickListener {
            sortTimer?.cancel()
            startSortTimer()
            Log.d(TAG, "Sort hide timer reset")

            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.switchSort()
            }
        }
    }

    private fun setupModeObserver() {
        viewModel.mode.observe(viewLifecycleOwner) { mode ->
            when (mode) {
                Mode.DEFAULT -> activateDefaultMode()
                Mode.SELECT -> activateSelectMode()
                else -> Log.e(TAG, "Unknown mode detected")
            }
        }
    }

    private fun changeSortIcon() {
        val sortType: SortType? = viewModel.settings.value?.shopSortType

        val possibleSortIcons: List<Int> = when (sortType) {
            SortType.NAME -> listOf(
                R.drawable.sort_alpha_up_icon,
                R.drawable.sort_alpha_down_icon
            )
            SortType.PRICE -> listOf(
                R.drawable.sort_price_up_icon,
                R.drawable.sort_price_down_icon
            )
            else -> listOf(
                R.drawable.sort_date_up_icon,
                R.drawable.sort_date_down_icon
            )
        }

        val sortOrder: SortOrder? = viewModel.settings.value?.shopSortOrder

        val sortIconId: Int = when (sortOrder) {
            SortOrder.ASC -> possibleSortIcons[1]
            else -> possibleSortIcons[0]
        }

        buttonConverter.convertNavButton(
            targetId = R.id.SortButton,
            iconDrawableId = sortIconId,
            iconColorId = R.color.icon_primary,
            view = requireView(),
        )
    }

    private fun reloadItemList(itemList: List<ShopItem>) {
        val itemListLayout = requireView().findViewById<LinearLayout>(R.id.ItemListLinearLayout)
        itemListLayout.removeAllViews()
        val settings: Settings? = viewModel.settings.value

        var sortedItemList = when (settings?.shopSortType) {
            SortType.NAME -> itemList.sortedBy { it.name }
            SortType.PRICE -> itemList.sortedBy { it.cost }
            else -> itemList.sortedBy { it.dateCreated }
        }

        if (settings?.shopSortOrder == SortOrder.ASC) {
            sortedItemList = sortedItemList.reversed()
        }

        selectedItemRowIds.clear()
        sortedItemList.forEach { item -> addItemRow(item) }
        if (itemList.isEmpty()) showNoItemsMessage() else hideNoItemsMessage()
    }

    private fun setupItemListObserver() {
        viewModel.shopItemList.observe(viewLifecycleOwner) { itemList ->
            reloadItemList(itemList)
        }
    }

    private fun setupMessageObserver() {
        viewModel.message.observe(viewLifecycleOwner) { message ->
            message.getContentIfNotHandled()?.let {
                val questListButton: View = requireView().findViewById(R.id.QuestListButton)
                showSnackbar(it, requireView(), questListButton)
            }
        }
    }

    private fun setupObservables() {
        activateItemHistoryButton()
        viewModel.switchMode(Mode.DEFAULT)
        setupModeObserver()
        setupItemListObserver()

        viewModel.settings.observe(viewLifecycleOwner) { _ ->
            changeSortIcon()
            viewModel.shopItemList.value?.let { reloadItemList(it) }
        }

        viewModel.player.observe(viewLifecycleOwner) { player ->
            updatePointsDisplay(player)
        }

        setupMessageObserver()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        InsetHandler.addInsetPadding(requireView())

        lifecycleScope.launch(Dispatchers.Main) {
            Log.i(TAG, "On Shop page")
            loadPointsAcronym()
            setupSortTrigger()
            setupSortButton()
            setupObservables()
        }
    }

    companion object {
        private const val TAG = "Shop"
    }
}
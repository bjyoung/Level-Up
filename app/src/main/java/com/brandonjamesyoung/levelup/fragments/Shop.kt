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
import com.brandonjamesyoung.levelup.data.ShopItem
import com.brandonjamesyoung.levelup.data.Player
import com.brandonjamesyoung.levelup.utility.ButtonConverter
import com.brandonjamesyoung.levelup.constants.Mode
import com.brandonjamesyoung.levelup.constants.POP_UP_BUTTON_WAIT_PERIOD
import com.brandonjamesyoung.levelup.constants.SortOrder
import com.brandonjamesyoung.levelup.constants.SortType
import com.brandonjamesyoung.levelup.data.Item
import com.brandonjamesyoung.levelup.data.ItemRow
import com.brandonjamesyoung.levelup.data.Settings
import com.brandonjamesyoung.levelup.compose.ItemTableCreator
import com.brandonjamesyoung.levelup.utility.InsetHandler
import com.brandonjamesyoung.levelup.utility.PointsDisplay
import com.brandonjamesyoung.levelup.utility.SnackbarHelper.Companion.showSnackbar
import com.brandonjamesyoung.levelup.utility.SortButtonManager
import com.brandonjamesyoung.levelup.viewmodels.ShopViewModel
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.timer

@AndroidEntryPoint
class Shop : Fragment(R.layout.shop) {
    private val viewModel: ShopViewModel by activityViewModels()

    private val selectedItemIds: MutableSet<Int> = mutableSetOf()

    private var latestItems: List<ShopItem> = mutableListOf()

    @Inject lateinit var itemTableCreator: ItemTableCreator

    @Inject lateinit var buttonConverter: ButtonConverter

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
        activateNewItemButton()
        activateQuestListButton()
        activateSettingsButton()
    }

    private fun cancelSelectedItems() {
        selectedItemIds.clear()
        viewModel.switchMode(Mode.DEFAULT)
        hideTotalCost()
        resetTotalCost()
        reloadLazyItemList(latestItems)
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
        hideTotalCost()
        resetTotalCost()
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

    private fun updateTotalCostAmount(selectedItemCost: Int) {
        val totalCostView = requireView().findViewById<TextView>(R.id.TotalCostAmount)

        val total: Int = try {
            Integer.parseInt(totalCostView.text as String) + selectedItemCost
        } catch (_: Exception) {
            0
        }

        totalCostView.text = total.toString()
    }

    private fun resetTotalCost() {
        val totalCost = requireView().findViewById<TextView>(R.id.TotalCostAmount)
        totalCost.text = "0"
    }

    private fun showTotalCost() {
        val view = requireView()
        val totalCostLabel = view.findViewById<TextView>(R.id.TotalCostLabel)
        val totalCostAmount = view.findViewById<TextView>(R.id.TotalCostAmount)
        totalCostLabel.visibility = View.VISIBLE
        totalCostAmount.visibility = View.VISIBLE
    }

    private fun hideTotalCost() {
        val view = requireView()
        val totalCostLabel = view.findViewById<TextView>(R.id.TotalCostLabel)
        val totalCostAmount = view.findViewById<TextView>(R.id.TotalCostAmount)
        totalCostLabel.visibility = View.INVISIBLE
        totalCostAmount.visibility = View.INVISIBLE
    }

    private fun updateTotalCost(selectedItemCost: Int) {
        if (selectedItemIds.isNotEmpty()) {
            updateTotalCostAmount(selectedItemCost)
            showTotalCost()
        } else {
            hideTotalCost()
            resetTotalCost()
        }
    }

    private fun selectItem(item: Item) {
        Log.i(TAG, "Selecting item ${item.id}")
        selectedItemIds.add(item.id)
        updateTotalCost(item.cost)
        reloadLazyItemList(latestItems)
    }

    private fun deselectItem(item: Item) {
        Log.i(TAG, "De-selecting item ${item.id}")
        selectedItemIds.remove(item.id)
        updateTotalCost(-item.cost)
        reloadLazyItemList(latestItems)
    }

    private fun tapItem(item: Item) {
        if (!viewModel.mode.hasObservers()) setupModeObserver()

        if (!isSelected(item.id)) {
            selectItem(item)
        } else {
            deselectItem(item)
        }

        val targetMode = if (selectedItemIds.isNotEmpty()) Mode.SELECT else Mode.DEFAULT
        viewModel.switchMode(targetMode)
    }

    private fun editItem(item: Item) {
        if (viewModel.mode.value == Mode.DEFAULT) {
            Log.i(TAG, "Long press on '${item.name}'")
            navigateToNewItem(item.id)
        }
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
        val acronym = settings.pointsAcronym
        setPointsLabel(acronym)
    }

    private fun setPointsLabel(acronym: String) = lifecycleScope.launch(Dispatchers.Main) {
        val pointsLabel : TextView = requireView().findViewById(R.id.PointsLabel)
        pointsLabel.text = acronym
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
            SortOrder.DESC -> possibleSortIcons[1]
            else -> possibleSortIcons[0]
        }

        buttonConverter.convertNavButton(
            targetId = R.id.SortButton,
            iconDrawableId = sortIconId,
            iconColorId = R.color.icon_primary,
            view = requireView(),
        )
    }


    private fun reloadLazyItemList(items: List<ShopItem>) {
        latestItems = items.toMutableList()
        if (items.isEmpty()) showNoItemsMessage() else hideNoItemsMessage()

        val itemRows: List<ItemRow> = items.map {
            ItemRow(it, isSelected(it.id))
        }

        val composeView = requireView().findViewById<ComposeView>(R.id.ShopTableComposeView)

        composeView.setContent {
            itemTableCreator.ItemTableView(
                itemRows = itemRows,
                tapAction = ::tapItem,
                longPressAction = ::editItem,
            )
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

    private fun sortItems(
        items: List<ShopItem>,
        sortType: SortType?,
        sortOrder: SortOrder?
    ) : List<ShopItem> {
        if (sortType == null) {
            return items
        }

        val questSortOrder = sortOrder ?: SortOrder.ASC

        return if (questSortOrder == SortOrder.ASC) {
            when (sortType) {
                SortType.DATE_CREATED -> items.sortedBy { it.dateCreated }
                SortType.NAME -> items.sortedBy { it.name }
                SortType.PRICE -> items.sortedBy { it.cost }
                else -> items.sortedBy { it.dateCreated }
            }
        } else {
            when (sortType) {
                SortType.DATE_CREATED -> items.sortedByDescending { it.dateCreated }
                SortType.NAME -> items.sortedByDescending { it.name }
                SortType.PRICE -> items.sortedByDescending { it.cost }
                else -> items.sortedByDescending { it.dateCreated }
            }
        }
    }

    private fun setupSortObserver() {
        viewModel.settings.observe(viewLifecycleOwner) { settings ->
            changeSortIcon()

            viewModel.shopItemList.value?.let { items ->
                val sortType = settings?.shopSortType
                val sortOrder = settings?.shopSortOrder
                val sortedItems: List<ShopItem> = sortItems(items, sortType, sortOrder)
                reloadLazyItemList(sortedItems)
            }
        }
    }

    private fun setupObservables() {
        activateItemHistoryButton()
        viewModel.switchMode(Mode.DEFAULT)
        setupModeObserver()

        viewModel.shopItemList.observe(viewLifecycleOwner) { itemList ->
            val settings: Settings

            runBlocking {
                settings = viewModel.getSettings()
            }

            val sortedItemList = sortItems(itemList, settings.shopSortType, settings.shopSortOrder)
            reloadLazyItemList(sortedItemList)
        }

        setupSortObserver()

        viewModel.player.observe(viewLifecycleOwner) { player ->
            updatePointsDisplay(player)
        }

        setupMessageObserver()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        InsetHandler.addInsetPadding(requireView())
        loadPointsAcronym()

        lifecycleScope.launch(Dispatchers.Main) {
            Log.i(TAG, "On Shop page")
            setupSortTrigger()
            setupSortButton()
            setupObservables()
        }
    }

    companion object {
        private const val TAG = "Shop"
    }
}
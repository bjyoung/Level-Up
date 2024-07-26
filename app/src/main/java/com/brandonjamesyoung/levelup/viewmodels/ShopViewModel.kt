package com.brandonjamesyoung.levelup.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.brandonjamesyoung.levelup.constants.MAX_POINTS_PER_PURCHASE
import com.brandonjamesyoung.levelup.constants.Mode
import com.brandonjamesyoung.levelup.data.*
import com.brandonjamesyoung.levelup.di.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val shopItemRepository: ShopItemRepository,
    private val itemHistoryRepository: ItemHistoryRepository,
    private val playerRepository: PlayerRepository,
    private val settingsRepository: SettingsRepository,
) : BaseViewModel() {
    val shopItemList: LiveData<List<ShopItem>> = shopItemRepository.observeAll().asLiveData()

    val player: LiveData<Player> = playerRepository.observe().asLiveData()

    init {
        validModes = listOf(Mode.DEFAULT, Mode.SELECT)
    }

    suspend fun getSettings() : Settings {
        return settingsRepository.get()
    }

    private fun getPurchaseMessage(numItems: Int, cost: Int, pointsAcronym: String) : String {
        val numItemsString = if (numItems == 1) "an item" else "$numItems items"
        return "Bought $numItemsString for $cost $pointsAcronym"
    }

    private fun calculateTotalCost(costs: List<Int>) : Int {
        var totalCost = 0

        for (cost in costs) {
            val estimatedTotal = totalCost + cost

            totalCost += if (estimatedTotal > MAX_POINTS_PER_PURCHASE) {
                MAX_POINTS_PER_PURCHASE - totalCost
            } else {
                cost
            }
        }

        return totalCost
    }

    fun buyItems(ids: Set<Int>) = viewModelScope.launch(ioDispatcher) {
        val costs: List<Int> = shopItemRepository.getCosts(ids)
        val totalCost = calculateTotalCost(costs)
        val player = playerRepository.get()
        val pointsAcronym = settingsRepository.get().pointsAcronym

        if (totalCost > 0 && totalCost > player.points) {
            Log.i(TAG, "Player does not have enough " +
                    "$pointsAcronym to purchase the selected items")
            showSnackbar("You do not have enough $pointsAcronym")
        } else {
            player.points -= totalCost
            playerRepository.update(player)
            itemHistoryRepository.record(ids)
            val numItemsBought = ids.count()
            Log.i(TAG, "Player bought $numItemsBought item(s) for $totalCost $pointsAcronym")
            val displayedMessage = getPurchaseMessage(numItemsBought, totalCost, pointsAcronym)
            showSnackbar(displayedMessage)
        }
    }

    fun deleteItems(ids: Set<Int>) = viewModelScope.launch(ioDispatcher) {
        shopItemRepository.delete(ids)
        val numDeleted = ids.count()
        Log.i(TAG, "Delete $numDeleted item(s)")
    }

    companion object {
        private const val TAG = "ShopViewModel"
    }
}
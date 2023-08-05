package com.brandonjamesyoung.levelup.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
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
    private val itemRepository: ItemRepository,
    private val playerRepository: PlayerRepository,
    private val settingsRepository: SettingsRepository,
) : BaseViewModel() {
    val itemList: LiveData<List<Item>> = itemRepository.observeAll().asLiveData()

    val player: LiveData<Player> = playerRepository.observe().asLiveData()

    val settings: LiveData<Settings> = settingsRepository.observe().asLiveData()

    private var _mode: MutableLiveData<Mode> = MutableLiveData<Mode>(Mode.DEFAULT)

    val mode: LiveData<Mode>
        get() = _mode

    fun switchToDefaultMode() {
        _mode.value = Mode.DEFAULT
    }

    fun switchToSelectMode() {
        _mode.value = Mode.SELECT
    }

    private fun getPurchaseMessage(numItems: Int, cost: Int, pointsAcronym: String) : String {
        val numItemsString = if (numItems == 1) "an item" else "$numItems items"
        return "Bought $numItemsString for $cost $pointsAcronym"
    }

    fun buyItems(ids: Set<Int>) = viewModelScope.launch(ioDispatcher) {
        val totalCost = itemRepository.getTotalCost(ids)
        val player = playerRepository.get()
        val pointsAcronym = settingsRepository.get().pointsAcronym

        if (totalCost > player.points) {
            Log.i(TAG, "Player does not have enough " +
                    "$pointsAcronym to purchase the selected items")
            showSnackbar("You do not have enough $pointsAcronym")
        } else {
            player.apply {
                points -= totalCost
            }

            playerRepository.update(player)
            val numItemsBought = ids.count()
            Log.i(TAG, "Player bought $numItemsBought item(s) for $totalCost $pointsAcronym")
            val displayedMessage = getPurchaseMessage(numItemsBought, totalCost, pointsAcronym)
            showSnackbar(displayedMessage)
        }
    }

    fun deleteItems(ids: Set<Int>) = viewModelScope.launch(ioDispatcher) {
        itemRepository.delete(ids)
        val numDeleted = ids.count()
        Log.i(TAG, "Delete $numDeleted item(s)")
    }

    companion object {
        private const val TAG = "ShopViewModel"
    }
}
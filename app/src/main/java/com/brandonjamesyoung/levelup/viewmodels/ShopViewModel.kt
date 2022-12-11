package com.brandonjamesyoung.levelup.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
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
) : ViewModel() {
    val itemList: LiveData<List<Item>> = itemRepository.observeAll().asLiveData()
    val player: LiveData<Player> = playerRepository.observe().asLiveData()
    val settings: LiveData<Settings> = settingsRepository.observe().asLiveData()

    fun buyItems(ids: Set<Int>) = viewModelScope.launch(ioDispatcher) {
        val totalCost = itemRepository.getTotalCost(ids)
        val player = playerRepository.get()

        if (totalCost > player.points) {
            Log.i(TAG, "Player does not have enough points to purchase the selected items")
            // TODO should tell player via toast message that they cannot buy the items
        } else {
            player.apply {
                points -= totalCost
            }

            playerRepository.update(player)
            val numItemsBought = ids.count()
            val pointsAcronym = settingsRepository.get().pointsAcronym
            Log.i(TAG, "Player bought $numItemsBought item(s) for $totalCost $pointsAcronym")
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
package com.brandonjamesyoung.levelup.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.brandonjamesyoung.levelup.data.*
import com.brandonjamesyoung.levelup.di.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewItemViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val shopItemRepository: ShopItemRepository,
    private val settingsRepository: SettingsRepository
) : BaseViewModel() {
    suspend fun getSettings() : Settings {
        return settingsRepository.get()
    }

    fun getItem(id: Int) : LiveData<ShopItem> {
        return shopItemRepository.observe(id).asLiveData()
    }

    private suspend fun logItemSave(shopItem: ShopItem, isEdit: Boolean = false) {
        var logMessage = if (!isEdit) {
            "Add new item with "
        } else {
            "Edit item to have "
        }

        logMessage += if (shopItem.name != null) "name '${shopItem.name}'" else "no name"

        logMessage += if (!isEdit) {
            " that costs ${shopItem.cost} "
        } else {
            " and cost ${shopItem.cost} "
        }

        logMessage += settingsRepository.get().pointsAcronym
        Log.i(TAG, logMessage)
    }

    fun insert(shopItem: ShopItem) = viewModelScope.launch(ioDispatcher) {
        shopItemRepository.insert(shopItem)
        logItemSave(shopItem)
    }

    fun update(shopItem: ShopItem) = viewModelScope.launch(ioDispatcher) {
        val currentItem: ShopItem = shopItemRepository.get(shopItem.id)

        currentItem.apply {
            name = shopItem.name
            cost = shopItem.cost
        }

        shopItemRepository.update(currentItem)
        logItemSave(shopItem = currentItem, isEdit = true)
    }

    companion object {
        private const val TAG = "NewItemViewModel"
    }
}
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
class NewItemViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val itemRepository: ItemRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val settings: LiveData<Settings> = settingsRepository.observe().asLiveData()

    fun getItem(id: Int): LiveData<Item> {
        return itemRepository.observe(id).asLiveData()
    }

    private suspend fun logItemSave(item: Item, isEdit: Boolean = false) {
        var logMessage = if (!isEdit) {
            "Add new item with "
        } else {
            "Edit item to have "
        }

        logMessage += if (item.name != null) "name '${item.name}'" else "no name"

        logMessage += if (!isEdit) {
            " that costs ${item.cost} "
        } else {
            " and cost ${item.cost} "
        }

        logMessage += settingsRepository.get().pointsAcronym
        Log.i(TAG, logMessage)
    }

    fun insert(item: Item) = viewModelScope.launch(ioDispatcher) {
        itemRepository.insert(item)
        logItemSave(item)
    }

    fun update(item: Item) = viewModelScope.launch(ioDispatcher) {
        val currentQuest = itemRepository.get(item.id)

        if (item.dateCreated == null) {
            item.dateCreated = currentQuest.dateCreated
        }

        itemRepository.update(item)
        logItemSave(item = item, isEdit = true)
    }

    companion object {
        private const val TAG = "NewItemViewModel"
    }
}
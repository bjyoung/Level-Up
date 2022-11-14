package com.brandonjamesyoung.levelup.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brandonjamesyoung.levelup.data.*
import com.brandonjamesyoung.levelup.di.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "NewItemViewModel"

@HiltViewModel
class NewItemViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val itemRepository: ItemRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private suspend fun logItemCreation(item: Item) {
        var logMessage = "Add new item with "
        logMessage += if (item.name != null) "name '${item.name}'" else "no name"
        logMessage += " that costs ${item.cost} "
        logMessage += settingsRepository.get().pointsAcronym

        Log.i(TAG, logMessage)
    }

    fun insert(item: Item) = viewModelScope.launch(ioDispatcher) {
        itemRepository.insert(item)
        logItemCreation(item)
    }
}
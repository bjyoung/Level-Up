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

private const val TAG = "ShopViewModel"

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

    fun deleteItems(ids: Set<Int>) = viewModelScope.launch(ioDispatcher) {
        itemRepository.delete(ids)
        val numDeleted = ids.count()
        Log.i(TAG, "Delete $numDeleted item(s)")
    }
}
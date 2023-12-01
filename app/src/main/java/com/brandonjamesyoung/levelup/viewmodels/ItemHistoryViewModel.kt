package com.brandonjamesyoung.levelup.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.brandonjamesyoung.levelup.data.ItemHistoryRepository
import com.brandonjamesyoung.levelup.data.PurchasedItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ItemHistoryViewModel @Inject constructor(
    private val itemHistoryRepository: ItemHistoryRepository
) : BaseViewModel() {
    val itemHistoryList: LiveData<List<PurchasedItem>> =
        itemHistoryRepository.observeAll().asLiveData()

    companion object {
        private const val TAG = "ItemHistoryViewModel"
    }
}
package com.brandonjamesyoung.levelup.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.brandonjamesyoung.levelup.data.*
import com.brandonjamesyoung.levelup.di.IoDispatcher
import com.brandonjamesyoung.levelup.shared.IconGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class IconSelectViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val iconRepository: IconRepository,
) : BaseViewModel() {
    var selectedIconGroup: IconGroup = IconGroup.SPADES

    val spadesIcons: LiveData<List<Icon>> =
        iconRepository.observeGroup(IconGroup.SPADES).asLiveData()

    val heartsIcons: LiveData<List<Icon>> =
        iconRepository.observeGroup(IconGroup.HEARTS).asLiveData()

    val diamondsIcons: LiveData<List<Icon>> =
        iconRepository.observeGroup(IconGroup.DIAMONDS).asLiveData()

    val clubsIcons: LiveData<List<Icon>> =
        iconRepository.observeGroup(IconGroup.CLUBS).asLiveData()

    companion object {
        private const val TAG = "IconSelectViewModel"
    }
}
package com.brandonjamesyoung.levelup.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.brandonjamesyoung.levelup.data.*
import com.brandonjamesyoung.levelup.di.IoDispatcher
import com.brandonjamesyoung.levelup.constants.IconGroup
import com.brandonjamesyoung.levelup.constants.Mode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IconSelectViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val iconRepository: IconRepository,
) : BaseViewModel() {
    val initSelectedGroup = IconGroup.SPADES

    var currentIconGroup: IconGroup? = null

    var selectedIconGroup: MutableLiveData<IconGroup?> = MutableLiveData(null)

    val mode: MutableLiveData<Mode> = MutableLiveData<Mode>(Mode.DEFAULT)

    val spadesIcons: LiveData<List<Icon>> =
        iconRepository.observeGroup(IconGroup.SPADES).asLiveData()

    val heartsIcons: LiveData<List<Icon>> =
        iconRepository.observeGroup(IconGroup.HEARTS).asLiveData()

    val diamondsIcons: LiveData<List<Icon>> =
        iconRepository.observeGroup(IconGroup.DIAMONDS).asLiveData()

    val clubsIcons: LiveData<List<Icon>> =
        iconRepository.observeGroup(IconGroup.CLUBS).asLiveData()

    fun deleteIcons(iconIds: List<Int>) = viewModelScope.launch(ioDispatcher) {
        iconRepository.delete(iconIds.toSet())
        val iconIdsString = "[" + iconIds.joinToString(", ") + "]"
        Log.i(TAG, "Delete icons with ids $iconIdsString")
    }

    fun moveIcons(iconIds: List<Int>, iconGroup: IconGroup) = viewModelScope.launch(ioDispatcher) {
        iconRepository.moveToNewIconGroup(iconIds, iconGroup)
        val iconIdsString = "[" + iconIds.joinToString(", ") + "]"
        Log.i(TAG, "Move icons with ids $iconIdsString to $iconGroup icon group")
    }

    companion object {
        private const val TAG = "IconSelectViewModel"
    }
}
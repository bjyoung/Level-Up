package com.brandonjamesyoung.levelup.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
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
    val initIconGroup = IconGroup.SPADES

    var selectedIconGroup: MutableLiveData<IconGroup?> = MutableLiveData(null)

    var displayedIcons: MutableLiveData<List<Icon>> = MutableLiveData(null)

    val selectedIconIds: MutableSet<Int> = mutableSetOf()

    init {
        validModes = listOf(Mode.SELECT, Mode.EDIT, Mode.MOVE)
        selectedIconGroup.value = initIconGroup
        loadIconGroup(initIconGroup)
    }

    fun loadIconGroup(iconGroup: IconGroup) = viewModelScope.launch(ioDispatcher) {
        Log.i(TAG, "Loading $iconGroup icons")
        displayedIcons.postValue(iconRepository.getIcons(iconGroup))
        Log.i(TAG, "Successfully loaded $iconGroup icons")
    }

    fun deleteIcons(iconIds: Set<Int>) = viewModelScope.launch(ioDispatcher) {
        iconRepository.delete(iconIds.toSet())
        val iconIdsString = "[" + iconIds.joinToString(", ") + "]"
        Log.i(TAG, "Delete icons with ids $iconIdsString")
    }

    fun moveIcons(iconIds: Set<Int>, iconGroup: IconGroup) = viewModelScope.launch(ioDispatcher) {
        iconRepository.moveToNewIconGroup(iconIds, iconGroup)
        val iconIdsString = "[" + iconIds.joinToString(", ") + "]"
        Log.i(TAG, "Move icons with ids $iconIdsString to $iconGroup icon group")
    }

    companion object {
        private const val TAG = "IconSelectViewModel"
    }
}
package com.brandonjamesyoung.levelup.viewmodels

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.brandonjamesyoung.levelup.data.*
import com.brandonjamesyoung.levelup.di.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NameEntryViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val playerRepository: PlayerRepository,
) : BaseViewModel() {
    fun update(name: String?) = viewModelScope.launch(ioDispatcher) {
        val player = playerRepository.get()
        player.name = name
        playerRepository.update(player)
        Log.i(TAG, "Player's name is set to $name")
    }

    companion object {
        private const val TAG = "NameEntryViewModel"
    }
}
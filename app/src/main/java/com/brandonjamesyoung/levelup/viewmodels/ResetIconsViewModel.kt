package com.brandonjamesyoung.levelup.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.brandonjamesyoung.levelup.data.*
import com.brandonjamesyoung.levelup.di.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetIconsViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val iconRepository: IconRepository,
) : BaseViewModel() {
    fun resetIcons(context: Context) = viewModelScope.launch(ioDispatcher) {
        iconRepository.resetToDefault(context)
        Log.i(TAG, "Reset icons to default")
    }

    companion object {
        private const val TAG = "ResetIconsViewModel"
    }
}
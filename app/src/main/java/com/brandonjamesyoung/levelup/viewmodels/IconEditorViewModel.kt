package com.brandonjamesyoung.levelup.viewmodels

import com.brandonjamesyoung.levelup.di.IoDispatcher
import com.brandonjamesyoung.levelup.utility.IconWorkspace
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class IconEditorViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : BaseViewModel() {
    val workspace: IconWorkspace = IconWorkspace(DEFAULT_GRID_SIZE, DEFAULT_GRID_SIZE)

    companion object {
        private const val DEFAULT_GRID_SIZE = 16
        private const val TAG = "IconEditorViewModel"
    }
}
package com.brandonjamesyoung.levelup.viewmodels

import com.brandonjamesyoung.levelup.di.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class IconEditorViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : BaseViewModel() {
    companion object {
        private const val TAG = "IconEditorViewModel"
    }
}
package com.brandonjamesyoung.levelup.viewmodels

import com.brandonjamesyoung.levelup.data.*
import com.brandonjamesyoung.levelup.di.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class NewIconViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val iconRepository: IconRepository
) : BaseViewModel() {
    companion object {
        private const val TAG = "NewIconViewModel"
    }
}
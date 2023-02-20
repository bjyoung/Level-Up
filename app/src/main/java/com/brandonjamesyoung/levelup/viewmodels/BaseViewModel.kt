package com.brandonjamesyoung.levelup.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.brandonjamesyoung.levelup.shared.Event

open class BaseViewModel : ViewModel() {
    private val statusMessage = MutableLiveData<Event<String>>()

    val message : LiveData<Event<String>>
        get() = statusMessage

    fun showSnackbar(message: String) {
        statusMessage.postValue(Event(message))
    }
}
package com.brandonjamesyoung.levelup.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.brandonjamesyoung.levelup.constants.Mode
import com.brandonjamesyoung.levelup.utility.Event

open class BaseViewModel : ViewModel() {
    private val statusMessage = MutableLiveData<Event<String>>()

    val message : LiveData<Event<String>>
        get() = statusMessage

    private var _mode: MutableLiveData<Mode> = MutableLiveData<Mode>(Mode.DEFAULT)

    val mode: LiveData<Mode>
        get() = _mode

    var validModes: List<Mode> = listOf(Mode.DEFAULT)

    fun showSnackbar(message: String) {
        statusMessage.postValue(Event(message))
    }

    fun switchMode(mode: Mode) {
        if (!validModes.contains(mode)) {
            Log.e(TAG, "[$mode] is not a valid mode")
        }

        if (_mode.value != mode) _mode.value = mode
    }

    companion object {
        private const val TAG = "BaseViewModel"
    }
}
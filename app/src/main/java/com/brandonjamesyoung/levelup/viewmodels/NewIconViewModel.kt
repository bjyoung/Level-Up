package com.brandonjamesyoung.levelup.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.brandonjamesyoung.levelup.constants.Mode
import com.brandonjamesyoung.levelup.data.*
import com.brandonjamesyoung.levelup.di.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewIconViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val iconRepository: IconRepository
) : BaseViewModel() {
    var name: String? = null

    var editIconId: Int? = null

    var loadedIcon: MutableLiveData<Icon?> = MutableLiveData(null)

    var iconDataLoaded: Boolean = false

    init {
        validModes = listOf(Mode.DEFAULT, Mode.EDIT)
    }

    fun loadIcon(id: Int) = viewModelScope.launch(ioDispatcher) {
        Log.i(TAG, "Loading icon with id: $id")
        val icon = iconRepository.get(id)
        loadedIcon.postValue(icon)
        Log.i(TAG, "Successfully loaded icon: ${icon.name}")
    }

    private fun logIconSave(icon: Icon, isEdit: Boolean = false) {
        var logMessage = if (!isEdit) "Added" else "Edited"
        logMessage += " icon ${icon.name} succesfully"
        Log.i(TAG, logMessage)
    }

    // TODO fully implement
    fun insert(icon: Icon) = viewModelScope.launch(ioDispatcher) {
/*        iconRepository.insert(icon)
        logIconSave(icon)*/
    }

    fun update(icon: Icon) = viewModelScope.launch(ioDispatcher) {
        val currentIcon: Icon = iconRepository.get(icon.id)

        // TODO only updating name for now, but should be able to update the bitmap later
        currentIcon.apply {
            name = icon.name
        }

        iconRepository.update(currentIcon)
        logIconSave(icon = currentIcon, isEdit = true)
    }

    fun saveIcon() {
        if (mode.value == Mode.DEFAULT) {
            // TODO setup new icon here
            Log.d(TAG, "Creating a new icon isn't implemented yet. Icon not saved.")
            /*            val icon = Icon()
                        insert(icon)*/
        } else if (mode.value == Mode.EDIT) {
            val icon = loadedIcon.value

            if (icon == null)  {
                Log.e(TAG, "Error: no loaded icon to save")
                return
            }

            icon.name = name
            update(icon)
        }
    }

    fun resetPage() {
        name = null
        editIconId = null
        iconDataLoaded = false
    }

    companion object {
        private const val TAG = "NewIconViewModel"
    }
}
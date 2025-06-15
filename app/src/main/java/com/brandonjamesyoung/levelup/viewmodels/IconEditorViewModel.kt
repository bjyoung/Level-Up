package com.brandonjamesyoung.levelup.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.brandonjamesyoung.levelup.di.IoDispatcher
import com.brandonjamesyoung.levelup.utility.IconWorkspace
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class IconEditorViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : BaseViewModel() {
    private val _workspace: MutableLiveData<IconWorkspace?> = MutableLiveData(null)

    val workspace: LiveData<IconWorkspace?>
        get() = _workspace

    init {
        val newWorkspace = IconWorkspace(DEFAULT_GRID_SIZE, DEFAULT_GRID_SIZE)
        _workspace.postValue(newWorkspace)
    }

    fun paintPixel(x: Int, y: Int) {
        val currWorkspace: IconWorkspace? = _workspace.value
        if (currWorkspace == null) Log.e(TAG, "Workspace not found")
        currWorkspace?.paintPixel(x, y)
        val newWorkspace = currWorkspace?.getCopy()
        _workspace.postValue(newWorkspace)
    }

    fun reset() {
        _workspace.value?.clear()
    }

    companion object {
        private const val DEFAULT_GRID_SIZE = 16
        private const val TAG = "IconEditorViewModel"
    }
}
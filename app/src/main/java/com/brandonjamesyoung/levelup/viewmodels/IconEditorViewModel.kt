package com.brandonjamesyoung.levelup.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.brandonjamesyoung.levelup.constants.DEFAULT_GRID_SIZE
import com.brandonjamesyoung.levelup.constants.MAX_HEIGHT
import com.brandonjamesyoung.levelup.constants.MAX_WIDTH
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

    var height: Int? = null

    var width: Int? = null

    init {
        val newWorkspace = IconWorkspace(DEFAULT_GRID_SIZE, DEFAULT_GRID_SIZE)
        _workspace.postValue(newWorkspace)
    }

    // Force recompose by posting a copy of the current workspace
    fun refreshWorkspace()
    {
        val currWorkspace: IconWorkspace? = _workspace.value
        val newWorkspace = currWorkspace?.getCopy()
        _workspace.postValue(newWorkspace)
    }


    fun setHeight(newHeight: Int) {
        if (newHeight < 0 || newHeight > MAX_HEIGHT) {
            Log.e(TAG, "Cannot set height to $newHeight. Height must be between 0 and $MAX_HEIGHT")
            return
        }

        height = newHeight
        _workspace.value?.modifyHeight(newHeight)
    }

    fun setWidth(newWidth: Int) {
        if (newWidth < 0 || newWidth > MAX_WIDTH) {
            Log.e(TAG, "Cannot set width to $newWidth. Width must be between 0 and $MAX_WIDTH")
            return
        }

        width = newWidth
        _workspace.value?.modifyWidth(newWidth)
    }

    fun resizeGrid(width: Int, height: Int) {
        setWidth(width)
        setHeight(height)
        refreshWorkspace()
        Log.i(TAG, "Workspace grid size updated to $width x $height")
    }

    fun paintPixel(x: Int, y: Int) {
        val currWorkspace: IconWorkspace? = _workspace.value
        if (currWorkspace == null) Log.e(TAG, "Workspace not found")
        currWorkspace?.paintPixel(x, y)
        refreshWorkspace()
    }

    fun reset() {
        height = null
        width = null
        _workspace.value?.clear()
    }

    companion object {
        private const val TAG = "IconEditorViewModel"
    }
}
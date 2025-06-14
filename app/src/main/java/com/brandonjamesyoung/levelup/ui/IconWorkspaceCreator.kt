package com.brandonjamesyoung.levelup.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.brandonjamesyoung.levelup.utility.IconWorkspace
import com.brandonjamesyoung.levelup.utility.OrientationManager

class IconWorkspaceCreator(val context: Context) {
    fun pixelClickTest(x: Int, y: Int) {
        Log.d(TAG, "Pixel [$x, $y] clicked")
    }

    fun calculatePixelSize(workspaceWidth: Int, workspaceHeight: Int) : Float {
        val inPortraitMode = OrientationManager.inPortraitMode(context.resources)
        val displayMetrics = context.resources.displayMetrics

        return if (inPortraitMode) {
            val screenWidthDp = displayMetrics.widthPixels/displayMetrics.density
            screenWidthDp/workspaceWidth.toFloat()
        } else {
            val screenHeightDp = displayMetrics.heightPixels/displayMetrics.density
            screenHeightDp/workspaceHeight.toFloat()
        }
    }

    @Composable
    fun PixelView(x: Int, y: Int, workspace: IconWorkspace) {
        val pixelSize: Float by remember(key1 = workspace.width, key2 = workspace.height) {
            val boxSize: Float = calculatePixelSize(workspace.width, workspace.height)
            mutableFloatStateOf(boxSize)
        }

        Box(
            modifier = Modifier
                .size(pixelSize.dp)
                .clip(DEFAULT_PIXEL_SHAPE)
                .background(Color.LightGray)
                .clickable {
                    pixelClickTest(x, y)
//                    workspace.paintPixel(x, y)
                }
        )
    }

     // Set up and display a grid of interactable pixels
    @Composable
    fun IconWorkspaceView(workspace: IconWorkspace) {
        val inPortraitMode: Boolean = OrientationManager.inPortraitMode(context.resources)
        val paddingModifier: Modifier = if (inPortraitMode) {
            Modifier.padding(0.dp, 0.dp, 0.dp, 200.dp)
        } else {
            Modifier.padding(100.dp, 0.dp)
        }

        Column (modifier = paddingModifier) {
            for (y in 0..<workspace.height) {
                Row {
                    for (x in 0..<workspace.width) {
                        PixelView(x, y, workspace)
                    }
                }
            }
        }
    }

    companion object {
        private val DEFAULT_PIXEL_SHAPE = RectangleShape
        private const val TAG = "IconWorkspaceCreator"
    }
}
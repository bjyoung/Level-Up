package com.brandonjamesyoung.levelup.ui

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.brandonjamesyoung.levelup.constants.EMPTY_PIXEL_INTENSITY
import com.brandonjamesyoung.levelup.utility.IconWorkspace
import com.brandonjamesyoung.levelup.utility.OrientationManager.Companion.inPortraitMode
import kotlin.math.min

class IconWorkspaceCreator(val context: Context) {
    @Composable
    fun PixelView(
        x: Int,
        y: Int,
        pixelSize: Dp,
        workspace: IconWorkspace,
        pixelTapAction: (Int, Int) -> Unit
    ) {
        val intensity = workspace.grid[y][x]

        val pixelColor: Color by remember (key1 = intensity) {
            val targetColor = if (intensity != EMPTY_PIXEL_INTENSITY) {
                Color.Black
            } else {
                Color.LightGray
            }

            mutableStateOf(targetColor)
        }

        Box(
            modifier = Modifier
                .size(pixelSize)
                .clip(DEFAULT_PIXEL_SHAPE)
                .background(pixelColor)
                .clickable { pixelTapAction(x, y) }
        )
    }

    fun calculatePixelSize(workspaceWidth: Int, workspaceHeight: Int) : Dp {
        val inPortraitMode = inPortraitMode(context.resources)
        val displayMetrics = context.resources.displayMetrics

        val shortDimensionMaxSize: Float
        val longDimensionMaxSize: Float

        if (inPortraitMode) {
            val screenWidth = displayMetrics.widthPixels/displayMetrics.density
            shortDimensionMaxSize = screenWidth/workspaceWidth.toFloat()
            longDimensionMaxSize = screenWidth/workspaceHeight.toFloat()
        } else {
            val screenHeight = displayMetrics.heightPixels/displayMetrics.density
            shortDimensionMaxSize = screenHeight/workspaceHeight.toFloat()
            longDimensionMaxSize = screenHeight/workspaceWidth.toFloat()
        }

        val pixelSize = min(shortDimensionMaxSize, longDimensionMaxSize)
        return pixelSize.dp
    }

    // Set up and display a grid of interactable pixels
    @Composable
    fun IconWorkspaceView(
        workspace: IconWorkspace,
        pixelTapAction: ((Int, Int) -> Unit),
    ) {
        val inPortraitMode: Boolean = inPortraitMode(context.resources)

        val workspacePaddingModifier = if (inPortraitMode) {
            Modifier.padding(0.dp, 0.dp, 0.dp, 200.dp)
        } else {
            Modifier.padding(100.dp, 0.dp)
        }

        val pixelSize: Dp by remember(key1 = workspace.width, key2 = workspace.height) {
            val boxSize: Dp = calculatePixelSize(workspace.width, workspace.height)
            mutableStateOf(boxSize)
        }

        Column(modifier = workspacePaddingModifier.border(
            BorderStroke(WORKSPACE_BORDER_THICKNESS, WORKSPACE_BORDER_COLOR)
        )) {
            for (y in 0..<workspace.height) {
                Row {
                    for (x in 0..<workspace.width) {
                        PixelView(x, y, pixelSize, workspace, pixelTapAction)
                    }
                }
            }
        }
    }

    companion object {
        private val DEFAULT_PIXEL_SHAPE = RectangleShape
        private const val TAG = "IconWorkspaceCreator"
        private val WORKSPACE_BORDER_THICKNESS = 2.dp
        private val WORKSPACE_BORDER_COLOR = Color.White
    }
}
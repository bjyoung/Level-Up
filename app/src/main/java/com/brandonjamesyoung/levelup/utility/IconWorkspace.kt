package com.brandonjamesyoung.levelup.utility

import android.util.Log
import androidx.collection.MutableIntList

class IconWorkspace(
    var width: Int,
    var height: Int
) {
    var grid: MutableList<MutableIntList> = MutableList<MutableIntList>(
        height,
        init = { index -> MutableIntList(width) }
    )

    // Add new rows to the bottom of the grid
    private fun addRows(numNewRows: Int) {
        if (numNewRows == 0) return

        repeat (numNewRows) {
            val newRow = MutableIntList(width)
            for (colNum in 1..width) newRow[colNum] = EMPTY_PIXEL_INTENSITY
            grid.add(newRow)
        }
    }

    // Remove rows from the bottom of the grid
    private fun removeRows(numRowsToRemove: Int) {
        if (numRowsToRemove == 0) return
        repeat(numRowsToRemove) { grid.removeAt(grid.lastIndex) }
    }

    fun modifyNumRows(newHeight: Int) {
        if (newHeight < 0 || newHeight > MAX_HEIGHT) {
            throw IllegalArgumentException("# rows must be between 0 and $MAX_HEIGHT")
        }

        val oldHeight = height
        val numRowDiff = newHeight - oldHeight
        if (numRowDiff == 0) return
        if (numRowDiff < 0) removeRows(numRowDiff) else addRows(numRowDiff)
        height = newHeight
        Log.i(TAG, "Number of rows changed from $oldHeight to $newHeight")
    }

    // Add new rows to the bottom of the grid
    private fun addCols(numNewCols: Int) {
        if (numNewCols == 0) return
        grid.forEach { it.add(EMPTY_PIXEL_INTENSITY) }
    }

    // Remove rows from the bottom of the grid
    private fun removeCols(numRowsToRemove: Int) {
        if (numRowsToRemove == 0) return
        repeat(numRowsToRemove) { grid.removeAt(grid.lastIndex) }
    }

    fun modifyNumCols(newWidth: Int) {
        if (newWidth < 0 || newWidth > MAX_WIDTH) {
            throw IllegalArgumentException("# columns must be between 0 and $MAX_WIDTH")
        }

        val oldWidth = width
        val numColDiff = newWidth - oldWidth
        if (numColDiff == 0) return
        if (numColDiff < 0) removeRows(numColDiff) else addRows(numColDiff)
        width = newWidth
        Log.i(TAG, "Number of columns changed from $oldWidth to $newWidth")
    }

    // Paint the pixels at the given coordinates if they don't match.
    // If they are already the target intensity, clear the pixel instead
    // If no intensity is given, defaults to black color
    fun paintPixels(pixelCoordinates: Set<IntArray>, intensity: Int = DEFAULT_PIXEL_INTENSITY) {
        var numPainted = 0
        var numCleared = 0

        for (coordinate in pixelCoordinates) {
            val x = coordinate[0]
            val y = coordinate[1]
            val currIntensity = grid[y][x]

            if (currIntensity == intensity) {
                grid[y][x] = EMPTY_PIXEL_INTENSITY
                numCleared++
            } else {
                grid[y][x] = intensity
                numPainted++
            }
        }

        Log.i(TAG, "Painted $numPainted pixels")
        Log.i(TAG, "Cleared $numCleared pixels")
    }

    // Paint a single pixel at the given coordinates
    // If they are already the target intensity, clear the pixel instead
    // If no intensity is given, defaults to black color
    fun paintPixel(x: Int, y: Int, intensity: Int = DEFAULT_PIXEL_INTENSITY) {
        val currIntensity = grid[y][x]

        if (currIntensity == intensity) {
            grid[y][x] = EMPTY_PIXEL_INTENSITY
            Log.d(TAG, "Painted pixel [$x, $y] with intensity ($intensity)")
        } else {
            grid[y][x] = intensity
            Log.d(TAG, "Cleared pixel [$x, $y]")
        }
    }

    fun clear() {
        for (y in 0..<height) {
            for (x in 0..<width) {
                grid[y][x] = EMPTY_PIXEL_INTENSITY
            }
        }

        Log.i(TAG, "Pixel grid cleared")
    }

    companion object {
        private const val MAX_HEIGHT = 40
        private const val MAX_WIDTH = 40
        private const val DEFAULT_PIXEL_INTENSITY = 0
        private const val EMPTY_PIXEL_INTENSITY = -1 // Used to tell if cell should be empty or not
        private const val TAG = "IconWorkspace"
    }
}
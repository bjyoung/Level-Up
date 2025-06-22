package com.brandonjamesyoung.levelup.utility

import android.util.Log
import androidx.collection.MutableIntList
import com.brandonjamesyoung.levelup.constants.DEFAULT_PIXEL_INTENSITY
import com.brandonjamesyoung.levelup.constants.EMPTY_PIXEL_INTENSITY
import com.brandonjamesyoung.levelup.constants.MAX_HEIGHT
import com.brandonjamesyoung.levelup.constants.MAX_WIDTH

class IconWorkspace(
    var width: Int,
    var height: Int,
    givenGrid: MutableList<MutableIntList>? = null
) {
    var grid: MutableList<MutableIntList>

    fun createInitGrid() : MutableList<MutableIntList> {
        val initGrid: MutableList<MutableIntList> = mutableListOf()

        repeat(height) {
            val row = MutableIntList(width)

            repeat(width) {
                row.add(EMPTY_PIXEL_INTENSITY)
            }

            initGrid.add(row)
        }

        return initGrid
    }

    init {
        grid = givenGrid?.toMutableList() ?: createInitGrid()
    }

    // Add new rows to the bottom of the grid
    private fun addRows(numNewRows: Int) {
        if (numNewRows == 0) return

        repeat (numNewRows) {
            val newRow = MutableIntList(width)
            repeat(width) { newRow.add(EMPTY_PIXEL_INTENSITY) }
            grid.add(newRow)
        }
    }

    // Remove rows from the bottom of the grid
    private fun removeRows(numRowsToRemove: Int) {
        if (numRowsToRemove == 0) return
        repeat(numRowsToRemove) { grid.removeAt(grid.lastIndex) }
    }

    fun modifyHeight(newHeight: Int) {
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

        grid.forEach { row ->
            repeat (numNewCols) {
                row.add(EMPTY_PIXEL_INTENSITY)
            }
        }
    }

    // Remove rows from the bottom of the grid
    private fun removeCols(numRowsToRemove: Int) {
        if (numRowsToRemove == 0) return

        repeat(numRowsToRemove) {
            grid.removeAt(grid.lastIndex)
        }
    }

    fun modifyWidth(newWidth: Int) {
        if (newWidth < 0 || newWidth > MAX_WIDTH) {
            throw IllegalArgumentException("# columns must be between 0 and $MAX_WIDTH")
        }

        val oldWidth = width

        if (newWidth == oldWidth) {
            Log.i(TAG, "No width change needed. Width is $oldWidth")
            return
        }

        val numColDiff = newWidth - oldWidth
        if (numColDiff < 0) removeCols(numColDiff) else addCols(numColDiff)
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
            Log.d(TAG, "Cleared pixel [$x, $y]")
        } else {
            grid[y][x] = intensity
            Log.d(TAG, "Painted pixel [$x, $y] with intensity ($intensity)")
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

    fun getCopy() : IconWorkspace {
        return IconWorkspace(width, height, grid)
    }

    companion object {
        private const val TAG = "IconWorkspace"
    }
}
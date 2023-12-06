package com.brandonjamesyoung.levelup.utility

import android.content.res.Resources
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.constants.ITEM_ROW_LANDSCAPE_WIDTH_DP
import com.brandonjamesyoung.levelup.data.Item

class ItemTableManager {
    fun createItemRow(
        item: Item,
        layoutInflater: LayoutInflater,
        parentLayout: ViewGroup,
        resources: Resources
    ) : ConstraintLayout {
        val newItemRow = layoutInflater.inflate(
            R.layout.item_row,
            parentLayout,
            false
        ) as ConstraintLayout

        newItemRow.id = View.generateViewId()
        val itemName = newItemRow.findViewById<TextView>(R.id.ItemName)
        val defaultName = resources.getString(R.string.placeholder_text)
        itemName.text = item.name ?: defaultName

        // TODO instead of setting a constant for item row width, set up constraints so
        //  that item name is automatically extended no matter what device it is on
        if (!OrientationManager.inPortraitMode(resources)) {
            val itemNameLandscapeWidth = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                ITEM_ROW_LANDSCAPE_WIDTH_DP,
                resources.displayMetrics
            )

            itemName.layoutParams.width = itemNameLandscapeWidth.toInt()
        }

        val itemCost = newItemRow.findViewById<TextView>(R.id.ItemCost)
        itemCost.text = item.cost.toString()
        return newItemRow
    }
}
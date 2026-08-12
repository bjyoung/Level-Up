package com.brandonjamesyoung.levelup.compose

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.constants.*
import com.brandonjamesyoung.levelup.data.*
import com.brandonjamesyoung.levelup.utility.OrientationManager.Companion.inPortraitMode

class ItemTableCreator(val context: Context) {
    @Composable
    fun ShopTableHeader(priceColumnName: String) {
        val headerFontSize = dimensionResource(R.dimen.shop_header_font_size).value.sp
        val headerBotPadding = dimensionResource(R.dimen.shop_header_bottom_padding)

        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Name",
                color = Color(TEXT_COLOR_PRIMARY),
                textAlign = TextAlign.Left,
                fontSize = headerFontSize,
                fontFamily = FontFamily(Font(R.font.press_start_2p)),
                modifier = Modifier
                    .padding(0.dp, 0.dp, 0.dp ,headerBotPadding)
            )
            Text(
                text = priceColumnName,
                color = Color(TEXT_COLOR_PRIMARY),
                textAlign = TextAlign.Right,
                fontSize = headerFontSize,
                fontFamily = FontFamily(Font(R.font.press_start_2p)),
                modifier = Modifier
                    .padding(0.dp, 0.dp, 0.dp ,headerBotPadding)
            )
        }
    }

    @Composable
    fun ShopTableBorder() {
        val borderThickness = dimensionResource(R.dimen.shop_table_border_thickness)

        HorizontalDivider(
            thickness = borderThickness,
            color = Color(TEXT_COLOR_PRIMARY)
        )
    }

    @Composable
    fun ShopTableRow(
        itemRow: ItemRow,
        tapAction: ((Item) -> Unit)? = null,
        longPressAction: ((Item) -> Unit)? = null
    ) {
        var backgroundColor: Color by remember(key1 = itemRow.selected) {
            val color: Color = if (itemRow.selected) Color.Blue else Color.Transparent
            mutableStateOf(color)
        }

        val rowVerticalPadding = dimensionResource(R.dimen.shop_table_row_vertical_padding)
        val rowMaxWidth = dimensionResource(R.dimen.shop_table_row_name_max_width)

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(0.dp, rowVerticalPadding)
                .combinedClickable(
                    interactionSource = remember {
                        MutableInteractionSource()
                    },
                    indication = null,
                    onClick = {
                        if (tapAction != null) {
                            tapAction(itemRow.item)
                        }
                    },
                    onLongClick = {
                        if (longPressAction != null) {
                            longPressAction(itemRow.item)
                        }
                    },
                )
        ) {
            Text(
                text = itemRow.item.name ?: "???",
                color = Color(TEXT_COLOR_PRIMARY),
                fontFamily = FontFamily(Font(R.font.press_start_2p)),
                modifier = Modifier
                    .widthIn(0.dp, rowMaxWidth)
            )
            Text(
                text = itemRow.item.cost.toString(),
                color = Color(TEXT_COLOR_PRIMARY),
                fontFamily = FontFamily(Font(R.font.press_start_2p))
            )
        }
    }

    @Composable
    fun ShopTableContents(
        itemRows: List<ItemRow>,
        tapAction: ((Item) -> Unit)? = null,
        longPressAction: ((Item) -> Unit)? = null
    ) {
        LazyColumn {
            items (itemRows) { itemRow ->
                ShopTableRow(itemRow, tapAction, longPressAction)
            }
        }
    }

    // Set up and display a list of items
    @Composable
    fun ItemTableView(
        itemRows: List<ItemRow>,
        priceColumnName: String = "Price",
        tapAction: ((Item) -> Unit)? = null,
        longPressAction: ((Item) -> Unit)? = null
    ) {
        val inPortraitMode = inPortraitMode(context.resources)

        val tableHeight = if (inPortraitMode) {
            dimensionResource(R.dimen.shop_table_height_portrait)
        } else {
            dimensionResource(R.dimen.shop_table_height_landscape)
        }

        val tableWidth = if (inPortraitMode) {
            dimensionResource(R.dimen.shop_table_width_portrait)
        } else {
            dimensionResource(R.dimen.shop_table_width_landscape)
        }

        val tablePaddingTop = if (inPortraitMode) {
            dimensionResource(R.dimen.shop_table_padding_top_portrait)
        } else {
            dimensionResource(R.dimen.shop_table_padding_top_landscape)
        }

        Column (
            modifier = Modifier
                .height(tableHeight)
                .width(tableWidth)
                .padding(0.dp, tablePaddingTop, 0.dp, 0.dp)
        ) {
            ShopTableHeader(priceColumnName)
            ShopTableBorder()
            ShopTableContents(itemRows, tapAction, longPressAction)
        }
    }
}
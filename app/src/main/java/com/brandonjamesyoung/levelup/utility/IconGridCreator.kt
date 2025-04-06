package com.brandonjamesyoung.levelup.utility

import android.content.Context
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.constants.Mode
import com.brandonjamesyoung.levelup.constants.TEXT_COLOR_PRIMARY
import com.brandonjamesyoung.levelup.data.SelectableIcon
import com.brandonjamesyoung.levelup.utility.OrientationManager.Companion.inPortraitMode

class IconGridCreator(val context: Context) {

    @Composable
    fun IconView(
        selectableIcon: SelectableIcon,
        iconAction: ((SelectableIcon) -> Unit)? = null,
        pageMode: () -> Mode?
    ) {
        val iconAlpha: Float by remember(key1 = pageMode) {
            val alpha: Float = if (pageMode() != Mode.MOVE) 1.0F else DISABLE_TRANSPARENCY
            mutableFloatStateOf(alpha)
        }

        // Icon image setup
        val icon = selectableIcon.icon
        val iconSize = dimensionResource(R.dimen.quest_card_icon_size)
        val iconPadding = 15.dp

        var selected: Boolean by remember(key1 = selectableIcon.selected) {
            mutableStateOf(selectableIcon.selected)
        }

        var iconBitmap = icon.toImageBitmap()
        var iconContentDescription = "${icon.name} Icon"

        if (selected && pageMode() != Mode.SELECT) {
            iconBitmap = ImageBitmap.imageResource(R.drawable.check_icon_green_large)
            iconContentDescription = "Green Checkmark Icon"
        } else {
            iconBitmap = icon.toImageBitmap()
            iconContentDescription = "${icon.name} Icon"
        }

        // Icon name setup
        val nameFontSize = dimensionResource(R.dimen.quest_card_font_size).value.sp
        val nameLineHeight = dimensionResource(R.dimen.quest_card_line_height).value.sp

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.graphicsLayer { this.alpha = iconAlpha }
        ) {
            Image(
                bitmap = iconBitmap,
                contentDescription = iconContentDescription,
                modifier = Modifier
                    .size(iconSize)
                    .background(
                        color = Color.White,
                        shape = CircleShape
                    )
                    .padding(iconPadding)
                    .width(128.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if(iconAction != null && pageMode() != Mode.MOVE) {
                            selected = !selected
                            iconAction(selectableIcon)
                        }
                    }
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = icon.name.toString(),
                color = Color(TEXT_COLOR_PRIMARY),
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
                fontSize = nameFontSize,
                fontFamily = FontFamily(Font(R.font.press_start_2p)),
                lineHeight = nameLineHeight,
                modifier = Modifier
                    .width(128.dp)
                    .padding(0.dp, 0.dp, 0.dp, 0.dp)
            )
        }
    }

    @Composable
    fun IconSelectGridView(
        icons: List<SelectableIcon>,
        iconAction: ((SelectableIcon) -> Unit)? = null,
        pageMode: (() -> Mode?)
    ) {
        val inPortraitMode = inPortraitMode(context.resources)

        val iconSize = 128.dp
        val iconSpacing = 16.dp

        // To move the icon grid to the right position
        val topPadding = if (inPortraitMode) 32.dp else 50.dp
        val botPadding = if (inPortraitMode) 0.dp else 52.dp

        val scrollingEnabled: Boolean by remember(key1 = pageMode) {
            mutableStateOf(pageMode() != Mode.MOVE)
        }

        LazyHorizontalGrid (
            rows = GridCells.FixedSize(iconSize),
            horizontalArrangement = Arrangement.spacedBy(iconSpacing),
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .height(700.dp)
                .padding(0.dp, topPadding, 0.dp, botPadding),
            userScrollEnabled = scrollingEnabled
        ) {
            items(
                items = icons,
                key = { it.icon.id }
            ) { icon ->
                Row(
                    Modifier.animateItem(
                        fadeInSpec = tween(durationMillis = 500),
                        fadeOutSpec = tween(durationMillis = 200),
                        placementSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioLowBouncy
                        )
                    )) {
                    IconView(icon, iconAction, pageMode)
                }
            }
        }
    }

    companion object {
        private const val DISABLE_TRANSPARENCY = 0.25F
    }
}
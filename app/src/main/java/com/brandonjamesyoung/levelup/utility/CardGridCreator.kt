package com.brandonjamesyoung.levelup.utility

import android.content.Context
import android.graphics.RuntimeShader
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.constants.*
import com.brandonjamesyoung.levelup.data.*

class CardGridCreator(val context: Context) {
    @Composable
    fun QuestNameView(questName: String) {
        val nameFontSize = dimensionResource(R.dimen.quest_card_font_size).value.sp
        val nameLineHeight = dimensionResource(R.dimen.quest_card_line_height).value.sp
        val minHeight = dimensionResource(R.dimen.quest_name_min_height)
        val maxHeight = dimensionResource(R.dimen.quest_name_max_height)
        val sidePadding = dimensionResource(R.dimen.quest_name_side_padding)
        val botPadding = dimensionResource(R.dimen.quest_name_bottom_padding)

        Text(
            text = questName,
            color = Color(TEXT_COLOR_SECONDARY),
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
            fontSize = nameFontSize,
            fontFamily = FontFamily(Font(R.font.press_start_2p)),
            lineHeight = nameLineHeight,
            modifier = Modifier
                .heightIn(min = minHeight, max = maxHeight)
                .fillMaxWidth()
                .padding(sidePadding, 0.dp, sidePadding, botPadding)
        )
    }

    @Composable
    fun QuestIconView(
        card: QuestCard,
        iconAction: ((QuestCard) -> Unit)?,
    ) {
        val iconSize = dimensionResource(R.dimen.quest_card_icon_size)
        val iconPadding = dimensionResource(R.dimen.quest_card_icon_padding)

        var selected: Boolean by remember(key1 = card.selected) {
            mutableStateOf(card.selected)
        }

        val hasIcon: Boolean by remember {
            mutableStateOf(card.icon != null)
        }

        val iconContentDescription: String
        val iconBitmap: ImageBitmap

        if (selected) {
            iconBitmap = ImageBitmap.imageResource(R.drawable.check_icon_green_large)
            iconContentDescription = "Green Checkmark Icon"
        } else if (hasIcon) {
            val icon: Icon = card.icon!!
            iconBitmap = icon.toImageBitmap()
            iconContentDescription = "${icon.name} Icon"
        } else {
            iconBitmap = ImageBitmap.imageResource(R.drawable.question_mark_icon_large)
            iconContentDescription = "Question Mark Icon"
        }

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
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if(iconAction != null) {
                        selected = !selected
                        iconAction(card)
                    }
                }
        )
    }

    @Composable
    fun QuestCardContents(
        card: QuestCard,
        iconAction: ((QuestCard) -> Unit)?,
        backgroundShaderSrc: String
    ) {
        val quest = card.quest

        val questCardWidth: Dp = dimensionResource(R.dimen.quest_card_width)

        val shader by remember {
            val runtimeShader = RuntimeShader(backgroundShaderSrc)

            runtimeShader.setFloatUniform(
                "iResolution",
                questCardWidth.value.toFloat(),
                questCardWidth.value.toFloat()
            )

            runtimeShader.setColorUniform(
                "iColor",
                quest.getGraphicsColor()
            )

            mutableStateOf(runtimeShader)
        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(brush = ShaderBrush(shader))
        ) {
            if (quest.name != null) {
                QuestNameView(quest.getName(context))
            }

            QuestIconView(card, iconAction)

            if (quest.name != null) {
                Spacer(Modifier.height(10.dp))
            }
        }
    }

    @Composable
    fun QuestCardView(
        card: QuestCard,
        cardAction: ((Int) -> Unit)?,
        iconAction: ((QuestCard) -> Unit)?,
        cardShaderSrc: String = BASIC_COLOR_SHADER_SRC
    ) {
        val quest: Quest = card.quest
        val questCardWidth: Dp = dimensionResource(R.dimen.quest_card_width)

        Card(
            modifier = Modifier
                .size(questCardWidth)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (cardAction != null) {
                        cardAction(quest.id)
                    }
                }
        ) {
            QuestCardContents(card, iconAction, cardShaderSrc)
        }
    }

    @Composable
    fun QuestGridView(
        cards: List<QuestCard>,
        cardAction: ((Int) -> Unit)? = null,
        iconAction: ((QuestCard) -> Unit)? = null,
        cardShader: String = BASIC_COLOR_SHADER_SRC
    ) {
        val inPortraitMode = OrientationManager.inPortraitMode(context.resources)

        // To move the grid just below the EXP progress bar
        val topPadding = if (inPortraitMode) 40.dp else 65.dp
        val botPadding = if (inPortraitMode) 0.dp else 70.dp

        val questCardWidth = dimensionResource(R.dimen.quest_card_width)
        val cardSpacing = dimensionResource(R.dimen.quest_card_spacing)

        LazyHorizontalGrid (
            rows = GridCells.FixedSize(questCardWidth),
            horizontalArrangement = Arrangement.spacedBy(cardSpacing),
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .height(670.dp)
                .padding(0.dp, topPadding, 0.dp, botPadding),
            contentPadding = PaddingValues(25.dp, 0.dp)
        ) {
            items(
                items = cards,
                key = { it.quest.id }
            ) { card ->
                Row(
                    Modifier
                        .animateItem(
                        fadeInSpec = tween(durationMillis = 500),
                        fadeOutSpec = tween(durationMillis = 200),
                        placementSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioLowBouncy
                    )
                )) {
                    QuestCardView(card, cardAction, iconAction, cardShader)
                }
            }
        }
    }
}
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.constants.QUEST_CARD_WIDTH
import com.brandonjamesyoung.levelup.constants.TEXT_COLOR_SECONDARY
import com.brandonjamesyoung.levelup.data.Quest
import com.brandonjamesyoung.levelup.data.Icon
import com.brandonjamesyoung.levelup.data.QuestCard

class CardGridCreator(val context: Context) {
    @Composable
    fun QuestGridView(
        cards: List<QuestCard>,
        cardAction: ((Int) -> Unit)? = null,
        iconAction: ((QuestCard) -> Unit)? = null
    ) {
        val inPortraitMode = OrientationManager.inPortraitMode(context.resources)

        // To move the grid just below the EXP progress bar
        val topPadding = if (inPortraitMode) 26.dp else 21.dp
        val botPadding = if (inPortraitMode) 40.dp else 15.dp

        LazyVerticalGrid(
            columns = GridCells.Adaptive(QUEST_CARD_WIDTH),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(
                20.dp,
                alignment = Alignment.Top
            ),
            modifier = Modifier
                .width(QUEST_CARD_WIDTH)
                .padding(0.dp, topPadding, 0.dp, botPadding),
            contentPadding = PaddingValues(0.dp, 15.dp)
        ) {
            items(
                items = cards,
                key = { it.quest.id }
            ) { card ->
                Row(Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 500),
                    fadeOutSpec = tween(durationMillis = 200),
                    placementSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioLowBouncy
                    )
                )) {
                    QuestCardView(card, cardAction, iconAction)
                }
            }
        }
    }

    @Composable
    fun QuestCardView(
        card: QuestCard,
        cardAction: ((Int) -> Unit)?,
        iconAction: ((QuestCard) -> Unit)?
    ) {
        val quest: Quest = card.quest
        val cardBackgroundColor = quest.getColor()

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

        Card(
            colors = CardDefaults.cardColors(
                containerColor = cardBackgroundColor
            ),
            modifier = Modifier
                .size(width = QUEST_CARD_WIDTH, height = QUEST_CARD_WIDTH)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (cardAction != null) {
                        cardAction(quest.id)
                    }
                }
        ) {
            Text(
                text = quest.getName(context),
                color = Color(TEXT_COLOR_SECONDARY),
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
                fontSize = 9.sp,
                fontFamily = FontFamily(Font(R.font.press_start_2p)),
                lineHeight = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(6.dp)
                    .wrapContentHeight(align = Alignment.CenterVertically)
            )
            Image(
                bitmap = iconBitmap,
                contentDescription = iconContentDescription,
                modifier = Modifier
                    .size(66.dp)
                    .background(
                        color = Color.White,
                        shape = CircleShape
                    )
                    .padding(15.dp)
                    .align(Alignment.CenterHorizontally)
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
    }
}
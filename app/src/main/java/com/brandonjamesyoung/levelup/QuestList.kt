package com.brandonjamesyoung.levelup

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.brandonjamesyoung.levelup.shared.NavigationHelper
import com.brandonjamesyoung.levelup.shared.StringHelper

class QuestList : Fragment(R.layout.quest_list) {
    private fun addNavigation(view: View){
        val navHelper = NavigationHelper()

        val buttonNavMap = mapOf(
            R.id.AddNewQuestButton to R.id.action_questList_to_newQuest,
            R.id.SettingsButton to R.id.action_questList_to_settings,
            R.id.ShopButton to R.id.action_questList_to_shop
        )

        for ((buttonId, navId) in buttonNavMap) {
            navHelper.addNavigationToView(this, view, buttonId, navId)
        }
    }

    private fun substitutePlaceholderText(view: View){
        val placeholderText = getString(R.string.placeholder_text)
        val pointsAcronym = getString(R.string.points_acronym)
        val stringHelper = StringHelper()
        stringHelper.substituteText(view, R.id.Username, placeholderText, placeholderText)
        stringHelper.substituteText(view, R.id.PointsLabel, placeholderText, pointsAcronym)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addNavigation(view)
        substitutePlaceholderText(view)
    }
}
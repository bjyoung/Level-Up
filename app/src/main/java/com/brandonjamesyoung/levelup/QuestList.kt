package com.brandonjamesyoung.levelup

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.brandonjamesyoung.levelup.shared.NavigationHelper

class QuestList : Fragment(R.layout.quest_list) {
    private fun addNavigation(view: View){
        val navHelper = NavigationHelper()

        val buttonNavMap = mapOf(
            R.id.AddNewQuestButton to R.id.action_questList_to_newQuest,
            R.id.SettingsButton to R.id.action_questList_to_settings,
            R.id.ShopButton to R.id.action_questList_to_shop
        )

        for ((buttonId, navId) in buttonNavMap) {
            navHelper.addNavigationToView(
                buttonId = buttonId,
                navActionId = navId,
                pageView = view,
                fragment = this,
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addNavigation(view)
    }
}
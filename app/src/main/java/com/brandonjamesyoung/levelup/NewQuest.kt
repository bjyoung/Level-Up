package com.brandonjamesyoung.levelup

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.brandonjamesyoung.levelup.shared.NavigationHelper

class NewQuest : Fragment(R.layout.new_quest) {
    private fun addNavigation(view: View){
        NavigationHelper().addNavigationToView(this, view, R.id.CancelButton, R.id.action_newQuest_to_questList)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addNavigation(view)
    }
}
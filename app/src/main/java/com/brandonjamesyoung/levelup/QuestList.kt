package com.brandonjamesyoung.levelup

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton

class QuestList : Fragment(R.layout.quest_list) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val addNewQuestButton = view.findViewById<FloatingActionButton>(R.id.AddNewQuestButton)

        addNewQuestButton.setOnClickListener{
            findNavController().navigate(R.id.action_questList_to_newQuest)
        }
    }
}
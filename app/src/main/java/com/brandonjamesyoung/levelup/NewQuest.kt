package com.brandonjamesyoung.levelup

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.brandonjamesyoung.levelup.shared.NavigationHelper

class NewQuest : Fragment(R.layout.new_quest) {
    private var selectedDifficulty: String? = null
    private var difficultyButtonIds = arrayOf(
        R.id.EasyButton,
        R.id.MediumButton,
        R.id.HardButton,
        R.id.ExpertButton
    )

    private fun addNavigation(view: View){
        NavigationHelper().addNavigationToView(
            this,
            view,
            R.id.CancelButton,
            R.id.action_newQuest_to_questList
        )
    }

    // Move box to given button
    private fun moveDifficultySelectBox(button: AppCompatButton, parentView: View){
        val constraintLayout = parentView.findViewById<ConstraintLayout>(R.id.NewQuest)
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        val selectBoxId = R.id.DifficultySelectBox
        constraintSet.connect(selectBoxId, ConstraintSet.BOTTOM, button.id, ConstraintSet.BOTTOM)
        constraintSet.connect(selectBoxId, ConstraintSet.START, button.id, ConstraintSet.START)
        constraintSet.connect(selectBoxId, ConstraintSet.END, button.id, ConstraintSet.END)
        constraintSet.connect(selectBoxId, ConstraintSet.TOP, button.id, ConstraintSet.TOP)
        constraintSet.applyTo(constraintLayout)
    }

    private fun setSelectedDifficulty(button: AppCompatButton, view: View){
        selectedDifficulty = button.text as String
        val selectBox = view.findViewById<ImageView>(R.id.DifficultySelectBox)
        selectBox.visibility = ImageView.INVISIBLE
        moveDifficultySelectBox(button, view)
        selectBox.visibility = ImageView.VISIBLE
    }

    private fun setDifficultyButtonListeners(view: View){
        for (id in difficultyButtonIds) {
            val button = view.findViewById<AppCompatButton>(id)

            button.setOnClickListener{
                setSelectedDifficulty(button, view)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addNavigation(view)
        setDifficultyButtonListeners(view)
    }
}
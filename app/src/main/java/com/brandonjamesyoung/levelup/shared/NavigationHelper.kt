package com.brandonjamesyoung.levelup.shared

import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController

class NavigationHelper {
    fun addNavigationToView(
        buttonId: Int,
        navActionId: Int,
        pageView: View,
        fragment: Fragment
    ){
        val button = pageView.findViewById<View>(buttonId)

        button.setOnClickListener{
            findNavController(fragment).navigate(navActionId)
        }
    }
}
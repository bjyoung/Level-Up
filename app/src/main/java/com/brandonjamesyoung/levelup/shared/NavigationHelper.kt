package com.brandonjamesyoung.levelup.shared

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import android.view.View

class NavigationHelper {
    companion object {
        fun addNavigationToView(
            fragment: Fragment,
            pageView: View,
            buttonId: Int,
            navActionId: Int
        ) {
            val button = pageView.findViewById<View>(buttonId)

            button.setOnClickListener{
                findNavController(fragment).navigate(navActionId)
            }
        }
    }
}
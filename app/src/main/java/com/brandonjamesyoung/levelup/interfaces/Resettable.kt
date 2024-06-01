package com.brandonjamesyoung.levelup.interfaces

import androidx.activity.addCallback
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController

interface Resettable {
    // Add functionality to run before backwards navigation occurs
    fun onBackNavigation(
        addOnFunction: () -> Unit,
        activity: FragmentActivity,
        lifecycleOwner: LifecycleOwner,
        navController: NavController
    ) {
        activity.onBackPressedDispatcher.addCallback(lifecycleOwner) {
            if (navController.currentBackStackEntry != null) addOnFunction()
            navController.popBackStack()
        }
    }
}
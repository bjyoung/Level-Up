package com.brandonjamesyoung.levelup.utility

import android.content.Context
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import com.brandonjamesyoung.levelup.R

class SortButtonManager (val context: Context) {
    // If show button isn't already visible, then animate it entering the page
    // If the animation is already triggered, then do nothing
    fun showSortButton(sortButton: Button, sortTrigger: Button) {
        Log.i(TAG, "Showing sort button")
        val animateSlideUp = AnimationUtils.loadAnimation(context, R.anim.button_slide_in_up)
        sortButton.startAnimation(animateSlideUp)
        sortButton.isEnabled = true
        sortButton.visibility = View.VISIBLE
        sortTrigger.visibility = View.INVISIBLE
    }

    // Animate sort button sliding off-screen
    fun hideSortButton(sortButton: Button, sortTrigger: Button) {
        Log.i(TAG, "Hiding sort button")

        val animateSlideDown = AnimationUtils.loadAnimation(
            context,
            R.anim.button_slide_out_down
        )

        sortButton.startAnimation(animateSlideDown)
        sortButton.isEnabled = false
        sortButton.visibility = View.INVISIBLE
        sortTrigger.visibility = View.VISIBLE
    }

    companion object {
        private const val TAG = "SortButtonManager"
    }
}
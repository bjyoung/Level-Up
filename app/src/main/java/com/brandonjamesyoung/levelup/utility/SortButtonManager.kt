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
    fun showSortButton(sortButton: Button, sortTrigger: Button, tag: String) {
        Log.i(tag, "Showing sort button")
        val animateSlideUp = AnimationUtils.loadAnimation(context, R.anim.button_slide_in_up)
        sortButton.startAnimation(animateSlideUp)
        sortButton.visibility = View.VISIBLE
        sortButton.isEnabled = true
        sortTrigger.visibility = View.INVISIBLE
    }

    fun hideSortButton(
        sortButton: Button,
        sortTrigger: Button,
        tag: String
    ) {
        Log.i(tag, "Hiding sort button")

        val animateSlideDown = AnimationUtils.loadAnimation(
            context,
            R.anim.button_slide_out_down
        )

        sortButton.isEnabled = false
        sortTrigger.visibility = View.VISIBLE
        sortButton.startAnimation(animateSlideDown)
    }
}
package com.brandonjamesyoung.levelup.utility

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import com.brandonjamesyoung.levelup.R
import com.brandonjamesyoung.levelup.constants.POINT_UPDATE_ANIM_DURATION
import com.brandonjamesyoung.levelup.data.Player

class PointsDisplay {
    // Set points value in UI without any animation
    private fun setPoints(points: Int?, pointsViewId: Int, rootView: View) {
        val pointsAmount = rootView.findViewById<TextView>(pointsViewId)
        val resources = rootView.resources
        if (points == null) pointsAmount.text = resources.getString(R.string.placeholder_text)
        pointsAmount.text = points.toString()
    }

    // Animate points from its original value to the player's current value
    private fun animatePoints(
        player: Player?,
        pointsViewId: Int,
        rootView: View
    ) {
        val pointsAmount = rootView.findViewById<TextView>(pointsViewId)

        if (player == null) {
            setPoints(null, pointsViewId, rootView)
            return
        }

        val resources = rootView.resources

        val prevPoints = if (pointsAmount.text == resources.getString(R.string.placeholder_text)) {
            0
        } else {
            Integer.parseInt(pointsAmount.text.toString())
        }

        val animator = ValueAnimator.ofInt(prevPoints, player.points)
        animator.interpolator = DecelerateInterpolator()
        animator.duration = POINT_UPDATE_ANIM_DURATION

        animator.addUpdateListener {
                animation -> pointsAmount.text = animation.animatedValue.toString()
        }

        animator.start()
    }

    // Update points display and decide whether to animate or set the value directly
    // When the player transitions to the page the value should be set immediately
    // When the player earns points, then their points should be animated
    fun updatePointsText(
        player: Player?,
        pointsViewId: Int,
        pointsLoaded: Boolean,
        view: View
    ) {
        if (player == null) {
            setPoints(null, pointsViewId, view)
            return
        }

        val pointsAmount = view.findViewById<TextView>(pointsViewId)

        if (pointsLoaded && pointsAmount.text != "0") {
            animatePoints(player, pointsViewId, view)
        } else {
            setPoints(player.points, pointsViewId, view)
        }
    }
}
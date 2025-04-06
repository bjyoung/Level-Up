package com.brandonjamesyoung.levelup.views

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.brandonjamesyoung.levelup.R
import com.google.android.material.snackbar.BaseTransientBottomBar

class RpgSnackbar(
    parent: ViewGroup,
    content: RpgSnackbarView
) : BaseTransientBottomBar<RpgSnackbar>(parent, content, content) {
    init {
        val view = getView()
        view.setBackgroundColor(Color.TRANSPARENT)
    }

    companion object {
        fun make(viewGroup: ViewGroup, message: String): RpgSnackbar {
            val rpgSnackbarLayout = LayoutInflater.from(viewGroup.context).inflate(
                R.layout.rpg_snackbar_layout,
                viewGroup,
                false
            ) as RpgSnackbarView
            val snackbar = rpgSnackbarLayout.findViewById<TextView>(R.id.RpgSnackbarMessage)
            snackbar.text = message

            return RpgSnackbar(viewGroup, rpgSnackbarLayout)
        }
    }
}
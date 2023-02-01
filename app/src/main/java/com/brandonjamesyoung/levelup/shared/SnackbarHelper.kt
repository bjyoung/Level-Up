package com.brandonjamesyoung.levelup.shared

import android.view.View
import android.view.ViewGroup
import com.brandonjamesyoung.levelup.views.RpgSnackbar

class SnackbarHelper {
    companion object {
        fun showSnackbar(message: String, view: View, anchorView: View? = null) {
            val snackbar = RpgSnackbar.make(view.rootView as ViewGroup, message)
            if (anchorView != null) snackbar.anchorView = anchorView
            snackbar.show()
        }
    }
}
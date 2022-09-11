package com.brandonjamesyoung.levelup.shared

import android.view.View
import android.widget.TextView

class StringHelper {
    companion object {
        fun substituteText(pageView: View, textViewId: Int, placeholderText: String){
            val view = pageView.findViewById<TextView>(textViewId)
            val viewText = view.text.toString()
            val substitutedViewText = String.format(viewText, placeholderText)
            view.text = substitutedViewText
        }

        fun substituteText(pageView: View, textViewId: Int, firstText: String, secondText: String){
            val view = pageView.findViewById<TextView>(textViewId)
            val viewText = view.text.toString()
            val substitutedViewText = String.format(viewText, firstText, secondText)
            view.text = substitutedViewText
        }
    }
}
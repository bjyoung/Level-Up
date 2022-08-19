package com.brandonjamesyoung.levelup.shared

import android.view.View
import android.widget.TextView

class StringHelper {
    fun substituteText(pageView: View, viewId: Int, placeholderText: String){
        val view = pageView.findViewById<TextView>(viewId)
        val viewText = view.text.toString()
        val substitutedViewText = String.format(viewText, placeholderText)
        view.text = substitutedViewText
    }

    fun substituteText(pageView: View, viewId: Int, placeholderTextFirst: String, placeholderTextSecond: String){
        val view = pageView.findViewById<TextView>(viewId)
        val viewText = view.text.toString()
        val substitutedViewText = String.format(viewText, placeholderTextFirst, placeholderTextSecond)
        view.text = substitutedViewText
    }
}
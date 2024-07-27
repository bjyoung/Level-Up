package com.brandonjamesyoung.levelup.utility

import android.content.Context
import android.view.View
import android.widget.TextView
import com.brandonjamesyoung.levelup.R
import java.time.Instant

class DateLabelManager (val context: Context) {
    fun setupDateCreatedLabel(dateCreated: Instant?, dateView: TextView) {
        if (dateCreated == null) return
        val dateString = TypeConverter.convertInstantToString(dateCreated)
        val resources = context.resources
        dateView.text = resources.getString(R.string.date_created_label, dateString)
        dateView.visibility = View.VISIBLE
    }
}
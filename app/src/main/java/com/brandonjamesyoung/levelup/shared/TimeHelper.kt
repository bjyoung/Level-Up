package com.brandonjamesyoung.levelup.shared

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.TimeZone

class TimeHelper {
    companion object {
        fun convertInstantToString(date: Instant) : String {
            val timeZoneId: ZoneId = TimeZone.getDefault().toZoneId()
            val zonedDate = date.atZone(timeZoneId)
            val dateFormatter = DateTimeFormatter.ofPattern("M/d/uuuu")
            return zonedDate.toLocalDate().format(dateFormatter)
        }
    }
}
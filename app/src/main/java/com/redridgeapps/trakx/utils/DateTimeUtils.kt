package com.redridgeapps.trakx.utils

import java.util.*
import java.util.concurrent.TimeUnit

object DateTimeUtils {

    val todayDateInstance: Calendar
        get() {
            val todayCalendar = Calendar.getInstance()
            todayCalendar.set(Calendar.HOUR_OF_DAY, 0)
            todayCalendar.set(Calendar.MINUTE, 0)
            todayCalendar.set(Calendar.SECOND, 0)
            return todayCalendar
        }

    fun isUpcoming(todayCalendar: Calendar, airCalendar: Calendar): Boolean {
        return airCalendar.after(todayCalendar) || isToday(todayCalendar, airCalendar)
    }

    fun humanReadableTimeToGo(airDate: Long): String {
        val today = todayDateInstance.timeInMillis
        val diffDuration = airDate - today

        val testCalendar = Calendar.getInstance()
        testCalendar.timeInMillis = airDate

        if (diffDuration >= 1) {
            val diffInDays = TimeUnit.MILLISECONDS.toDays(diffDuration) + 1
            return if (diffInDays == 1L) "Tomorrow" else "In $diffInDays days"
        }

        return "Today"
    }

    private fun isToday(todayCalendar: Calendar, airCalendar: Calendar): Boolean {
        return todayCalendar.get(Calendar.DAY_OF_YEAR) == airCalendar.get(Calendar.DAY_OF_YEAR)
                && todayCalendar.get(Calendar.YEAR) == airCalendar.get(Calendar.YEAR)
    }
}

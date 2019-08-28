package com.redridgeapps.trakx.utils

import java.util.*

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

    private fun isToday(todayCalendar: Calendar, airCalendar: Calendar): Boolean {
        return todayCalendar.get(Calendar.DAY_OF_YEAR) == airCalendar.get(Calendar.DAY_OF_YEAR)
                && todayCalendar.get(Calendar.YEAR) == airCalendar.get(Calendar.YEAR)
    }
}

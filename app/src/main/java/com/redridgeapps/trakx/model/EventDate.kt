package com.redridgeapps.trakx.model

import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

inline class EventDate(val longDate: Long) {

    companion object {
        val NO_DATE = EventDate(-1)
    }
}

private val todayDate: ZonedDateTime
    get() = ZonedDateTime.now(ZoneId.systemDefault())

private fun EventDate.asZonedLocalDate(): ZonedDateTime {
    return Instant.ofEpochSecond(longDate)
        .atZone(ZoneId.systemDefault())
}

fun EventDate.isUpcoming(): Boolean = todayDate.isBefore(asZonedLocalDate())

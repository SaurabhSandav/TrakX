package com.redridgeapps.trakx.model

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

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

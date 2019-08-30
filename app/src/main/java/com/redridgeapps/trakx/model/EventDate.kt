package com.redridgeapps.trakx.model

inline class EventDate(val longDate: Long) {

    companion object {
        val NO_DATE = EventDate(-1)
    }
}

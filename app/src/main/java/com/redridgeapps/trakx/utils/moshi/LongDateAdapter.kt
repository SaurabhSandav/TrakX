package com.redridgeapps.trakx.utils.moshi

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.ToJson
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class LongDate

@Retention(AnnotationRetention.RUNTIME)
private annotation class Nullable

@Suppress("unused")
class LongDateAdapter {

    @ToJson
    fun toJson(@LongDate longDate: Long) = if (longDate == -1L) null else simpleDateFormat.format(longDate)

    @FromJson
    @LongDate
    fun fromJson(@Nullable dateStr: String?): Long {
        if (dateStr == null) return -1L

        try {
            return simpleDateFormat.parse(dateStr).time
        } catch (e: ParseException) {
        }

        return -1L
    }
}

private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

package com.redridgeapps.trakx.utils.serialization

import com.redridgeapps.trakx.model.EventDate
import com.redridgeapps.trakx.utils.Constants.ZONE_ID_UTC
import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.internal.nullable
import kotlinx.serialization.serializer
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Serializer(forClass = Long::class)
object LongDateSerializer : KSerializer<Long> {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val zoneId = ZONE_ID_UTC
    private val serializer = String.serializer().nullable

    override fun serialize(encoder: Encoder, obj: Long) {
        val convertedStr = if (obj == EventDate.NO_DATE.longDate) null else obj.asISODate()

        encoder.encodeSerializableValue(serializer, convertedStr)
    }

    override fun deserialize(decoder: Decoder): Long {
        val defaultResult = EventDate.NO_DATE.longDate

        return kotlin.runCatching {
            decoder.decodeSerializableValue(serializer)!!.asEpochSeconds()
        }.getOrDefault(defaultResult)
    }

    private fun Long.asISODate(): String {
        return Instant.ofEpochSecond(this)
            .atZone(zoneId)
            .toLocalDate()
            .format(formatter)
    }

    private fun String.asEpochSeconds(): Long {
        return LocalDate.parse(this, formatter)
            .atStartOfDay(zoneId)
            .toEpochSecond()
    }
}

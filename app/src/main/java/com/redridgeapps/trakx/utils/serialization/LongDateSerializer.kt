package com.redridgeapps.trakx.utils.serialization

import com.redridgeapps.trakx.model.EventDate
import com.redridgeapps.trakx.utils.Constants.ZONE_ID_UTC
import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.internal.NullableSerializer
import kotlinx.serialization.serializer
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.text.ParseException

@Serializer(forClass = Long::class)
object LongDateSerializer : KSerializer<Long> {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val zoneId = ZONE_ID_UTC
    private val serializer = NullableSerializer(String.serializer())

    override fun serialize(encoder: Encoder, obj: Long) {
        val convertedStr = if (obj == EventDate.NO_DATE.longDate) null else obj.asISODate()

        encoder.encodeSerializableValue(serializer, convertedStr)
    }

    override fun deserialize(decoder: Decoder): Long {
        val result = EventDate.NO_DATE.longDate
        val dateStr = decoder.decodeSerializableValue(serializer) ?: return result

        return try {
            dateStr.asEpochSeconds()
        } catch (e: ParseException) {
            result
        }
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

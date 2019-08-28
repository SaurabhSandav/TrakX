package com.redridgeapps.trakx.utils.serialization

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.internal.NullableSerializer
import kotlinx.serialization.serializer
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Serializer(forClass = Long::class)
object LongDateSerializer : KSerializer<Long> {

    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val serializer = NullableSerializer(String.serializer())

    override fun serialize(encoder: Encoder, obj: Long) {
        val convertedStr = if (obj == -1L) null else simpleDateFormat.format(obj)

        encoder.encodeSerializableValue(serializer, convertedStr)
    }

    override fun deserialize(decoder: Decoder): Long {
        val dateStr = decoder.decodeSerializableValue(serializer) ?: return -1L

        var result = -1L

        try {
            simpleDateFormat.parse(dateStr)
                ?.time
                ?.let { result = it }
        } catch (e: ParseException) {
            // Ignore
        }

        return result
    }
}

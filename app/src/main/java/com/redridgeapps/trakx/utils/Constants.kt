package com.redridgeapps.trakx.utils

import org.threeten.bp.ZoneId

object Constants {

    const val KEY_VIEWMODEL_ARGS = "VIEWMODEL_ARGS"

    val DEFAULT_CATEGORY_MAIN = RequestType.POPULAR
    val ZONE_ID_UTC: ZoneId = ZoneId.of("UTC")

    enum class RequestType {
        TRACKED, POPULAR, TOP_RATED, ON_THE_AIR, AIRING_TODAY
    }
}

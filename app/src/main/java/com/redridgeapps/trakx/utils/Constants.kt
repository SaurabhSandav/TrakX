package com.redridgeapps.trakx.utils

object Constants {

    val DEFAULT_CATEGORY_MAIN = RequestType.POPULAR

    enum class RequestType {
        TRACKED, POPULAR, TOP_RATED, ON_THE_AIR, AIRING_TODAY
    }
}

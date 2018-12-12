package com.redridgeapps.trakx.model.local

import androidx.room.Entity

@Entity(primaryKeys = ["showId", "cacheCategory"])
data class CachedCategory(
    val showId: Int,
    val position: Int,
    val page: Int,
    val cacheCategory: String
)
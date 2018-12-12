package com.redridgeapps.trakx.model.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["showId"], unique = true)])
data class TrackedShow(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val showId: Int
)
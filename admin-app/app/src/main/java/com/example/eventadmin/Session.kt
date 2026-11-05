package com.example.eventadmin

import java.time.LocalTime

data class Session(
    val id: Long = -1,
    val eventId: Long,
    val name: String,
    val time: LocalTime,
    val speaker: String? = null,
    val room: String? = null,
    val type: String,
    val desc: String? = null
)
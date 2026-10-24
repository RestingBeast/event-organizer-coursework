package com.example.eventadmin

import java.time.LocalDate

data class Event(
    val name: String,
    val date: LocalDate,
    val location: String,
    val organizer: String,
    val type: String,
    val status: String,
    val id: Long = 0
)
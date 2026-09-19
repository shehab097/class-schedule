package com.shehab.classschedule.model

val DAY_ORDER = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

data class ClassSession(
    val id: Int,
    val course: String,
    val title: String,
    val teacher: String,
    val day: String,
    val room: String,
    val startTime: String,
    val endTime: String,
    val section: String,
    val isLab: Boolean
)

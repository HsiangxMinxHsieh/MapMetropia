package com.timmy.mapmetropia.util

import java.text.SimpleDateFormat
import java.util.*

fun Int.toTimeInclude12hour() = this.toLong().times(DateTool.oneSec).toDate().toString("h:mm a")

fun Long.toDate(): Date {
    return Date(this)
}

fun Date.toString(format: String, timeZone: TimeZone = TimeZone.getDefault()): String {
    val sdf = SimpleDateFormat(format, Locale.ENGLISH) //強制用英文(因 Zeplin 上是英文)
    sdf.timeZone = timeZone
    return sdf.format(this)
}

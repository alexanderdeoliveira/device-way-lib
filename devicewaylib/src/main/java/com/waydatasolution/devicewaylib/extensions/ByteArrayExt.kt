package com.waydatasolution.devicewaylib.extensions

import java.util.Calendar
import java.util.Date

internal fun ByteArray.unixToDate(): Date {
    if (this.size == 4) {
        val rawTimestamp = ((this[0].toInt() and 0xFF) shl 24) or
                ((this[1].toInt() and 0xFF) shl 16) or
                ((this[2].toInt() and 0xFF) shl 8) or
                (this[3].toInt() and 0xFF)

        val calendar: Calendar = Calendar.getInstance()

        calendar.time = Date(10800000)
        calendar.add(Calendar.SECOND, rawTimestamp)
        calendar.add(Calendar.MINUTE, -Date().timezoneOffset)

        return calendar.time
    } else {
        throw IllegalArgumentException("UnixDate must have size of 4")
    }
}
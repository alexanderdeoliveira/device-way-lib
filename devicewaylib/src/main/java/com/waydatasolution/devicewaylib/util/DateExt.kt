package com.waydatasolution.devicewaylib.util

import java.text.SimpleDateFormat
import java.util.Locale

internal fun Long.toDatetime(): String {
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT)
    return simpleDateFormat.format(this)
}
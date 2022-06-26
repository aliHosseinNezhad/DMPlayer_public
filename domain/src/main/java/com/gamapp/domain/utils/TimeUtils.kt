package com.gamapp.domain.utils

import java.util.concurrent.TimeUnit


fun Long.toTimeFormat(): String {
    return String.format(
        "%02d:%02d",
        TimeUnit.MILLISECONDS.toMinutes(this) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(this)),
        TimeUnit.MILLISECONDS.toSeconds(this) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(this))
    )
}

fun Int.toTimeFormat(): String {
    return this.toLong().toTimeFormat()
}
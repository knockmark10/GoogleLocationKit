package com.markoid.googlelocationkit.entities

import android.os.Looper

data class SettingsOptions(
    val updateInterval: Long = (15 * 1000).toLong(),
    val fastestInterval: Long = (10 * 1000).toLong(),
    val useLooper: Boolean = false
) {

    val looper: Looper?
        get() = if (useLooper) Looper.myLooper() else null

}
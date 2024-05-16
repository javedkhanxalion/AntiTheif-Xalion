package com.example.antitheifproject.service

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

class VibrationManager(private val context: Context) {

    private var vibrator: Vibrator? = null

    init {
        vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    fun startVibration() {
        if (vibrator != null) {
            stopVibration()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(2000L, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(2000L)
            }
        }
    }

    private fun stopVibration() {
        vibrator?.cancel()
    }
}

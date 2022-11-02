package com.example.firebase_test

import android.os.Build

object Utils {
    fun isAndroidQ(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    }

    fun isAndroidP(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    }

    fun isAndroidO(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }

     fun isAndroidR(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    }
}
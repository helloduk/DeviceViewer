package com.solluzfa.solluzviewer

import android.util.Log

object Log {
    const val TAG = "SOLLUZFA"

    fun i(tag: String, data: String) {
        Log.i(TAG, "$tag: $data")
    }

    fun e(tag: String, data: String) {
        Log.e(TAG, "$tag: $data")
    }
}
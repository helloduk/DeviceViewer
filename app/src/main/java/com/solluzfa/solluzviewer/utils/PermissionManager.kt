package com.solluzfa.solluzviewer.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.widget.Toast

const val PERMISSION_REQUEST_CODE = 200
val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION
)

fun requestPermission(activity: Activity) {
    ActivityCompat.requestPermissions(activity, PERMISSIONS, PERMISSION_REQUEST_CODE)
}

fun hasPermissions(context: Context?, permissions: Array<String>): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null) {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
    }
    return true
}

fun onRequestPermissionsResult(
        context: Context,
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
) {
    when (requestCode) {
        PERMISSION_REQUEST_CODE -> {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Permissions granted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Permissions must be granted", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


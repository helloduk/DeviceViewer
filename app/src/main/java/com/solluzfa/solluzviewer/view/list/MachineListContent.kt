package com.solluzfa.solluzviewer.view.list

import android.graphics.Color
import android.view.Gravity
import java.util.*

object MachineListContent {
    val ITEMS: MutableList<PlaceholderItem> = ArrayList()

    data class PlaceholderItem(
        var title: String?,
        var tt: String,
        var tb: Int,
        var tf: Int,
        var ta: Int,

        var ftt: String = "0",
        var ftb: Int = Color.rgb(255, 255, 255),
        var ftf: Int = Color.rgb(255, 255, 255),
        var fta: Int = Gravity.RIGHT,

        var deleteChecked: Boolean = false
    )
}
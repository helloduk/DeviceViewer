package com.solluzfa.solluzviewer.view.list

import java.util.*

object MachineListContent {
    val ITEMS: MutableList<PlaceholderItem> = ArrayList()

    data class PlaceholderItem(
        var title: String?,
        var tt: String,
        var tb: Int,
        var tf: Int,
        var ta: Int,
        var deleteChecked: Boolean = false
    )
}
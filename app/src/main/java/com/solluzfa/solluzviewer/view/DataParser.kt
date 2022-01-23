package com.solluzfa.solluzviewer.view

import android.graphics.Color
import android.view.Gravity
import com.solluzfa.solluzviewer.Log

object DataParser {
    private val TAG = "DataParser"

    fun parse(data: String, items: MutableList<Item>): String? {
        Log.i(TAG, "parse $data")
        //Row:4,TT1:Text1,TB1:255255255,TF1:000000000,TT2:Text2,TB2:243175175,TF2:000000000,TT3:Text3,TB3:255255255,TF3:000000000,TT4:Text4,TB4:255255255,TF4:000000000
        //Name:Title Text,Row:4,CT1:Caption1,CB1:011097019,CF1:255255255,TA1:Right,CT2:Caption2,CB2:164020020,CF2:255255255,TA2:Left,CT3:Caption3,CB3:052118232,CF3:000000000,TA3:Center,CT4:Caption4,CB4:023108097,CF4:255255255,TA4:Center

        val datas = data.split(";")

        if (datas.size != 2) {
            return null
        }

        val valueData = datas[0].split(",")
        val layoutData = datas[1].split(",")

        val rows = valueData.get(0).substringAfterLast(":")?.toIntOrNull()

        if (rows == null) {
            return null
        }

        if (valueData != null && layoutData != null) {
            val title = layoutData[0]?.substringAfterLast(":") ?: "Parsing error"

            rows?.let {
                if (items.size != rows)
                    items.clear()

                for (i in 0 until rows) {
                    val dataItem: Item = Item(
                        tt = valueData[1 + (i * 3)]?.substringAfterLast(":"),
                        tb = parseColor(valueData[2 + (i * 3)]?.substringAfterLast(":")),
                        tf = parseColor(valueData[3 + (i * 3)]?.substringAfterLast(":")),
                        ct = layoutData[2 + (i * 4)]?.substringAfterLast(":"),
                        cb = parseColor(layoutData[3 + (i * 4)]?.substringAfterLast(":")),
                        cf = parseColor(layoutData[4 + (i * 4)]?.substringAfterLast(":")),
                        ta = when (layoutData[5 + (i * 4)]?.substringAfterLast(":")) {
                            "Right" -> Gravity.RIGHT.or(Gravity.CENTER_VERTICAL)
                            "Left" -> Gravity.LEFT.or(Gravity.CENTER_VERTICAL)
                            "Center" -> Gravity.CENTER_HORIZONTAL.or(Gravity.CENTER_VERTICAL)
                            else -> Gravity.RIGHT.or(Gravity.CENTER_VERTICAL)
                        }
                    )
                    if (items.size >= i + 1)
                        items[i] = dataItem
                    else
                        items.add(i, dataItem)
                }
            }
            return title
        }
        return null
    }

    private fun parseColor(input: String): Int {
        val red = input.substring(0, 3)
        val green = input.substring(3, 6)
        val blue = input.substring(6)

        return Color.rgb(red.toInt(), green.toInt(), blue.toInt())
    }

    data class Item(
        var tt: String,
        var tb: Int,
        var tf: Int,
        var ta: Int,
        var ct: String,
        var cb: Int,
        var cf: Int
    )
}
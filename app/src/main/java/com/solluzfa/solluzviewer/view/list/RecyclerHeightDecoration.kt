package com.solluzfa.solluzviewer.view.list

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class RecyclerHeightDecoration(private val divHeight: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.top = divHeight;
    }
}
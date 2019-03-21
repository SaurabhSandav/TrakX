package com.redridgeapps.trakx.ui.common

import android.content.Context
import android.util.TypedValue
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.ceil
import kotlin.math.max

class AutoFitGridLayoutManager(
    context: Context,
    maxColumnWidth: Float
) : GridLayoutManager(context, 1) {

    private var columnWidth: Float = 0F
    private var columnWidthChanged = true

    init {

        // Initially set spanCount to 1, will be changed automatically later.
        setColumnWidth(checkedColumnWidth(context, maxColumnWidth))
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {

        if (columnWidthChanged && columnWidth > 0 && width > 0 && height > 0) {

            val spanCount = max(1F, ceil(getTotalSpace() / columnWidth)).toInt()
            setSpanCount(spanCount)

            columnWidthChanged = false
        }

        super.onLayoutChildren(recycler, state)
    }

    private fun checkedColumnWidth(context: Context, newColumnWidth: Float): Float {
        if (newColumnWidth > 0) return newColumnWidth

        /* Set default maxColumnWidth value (48dp here). It is better to move this constant
               to static constant on top, but we need context to convert it to dp, so can't really
               do so. */
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 48f,
            context.resources.displayMetrics
        )
    }

    private fun setColumnWidth(newColumnWidth: Float) {
        if (newColumnWidth > 0 && newColumnWidth != columnWidth) {
            columnWidth = newColumnWidth
            columnWidthChanged = true
        }
    }

    private fun getTotalSpace(): Int = when (orientation) {
        LinearLayoutManager.VERTICAL -> width - paddingRight - paddingLeft
        else -> height - paddingTop - paddingBottom
    }
}

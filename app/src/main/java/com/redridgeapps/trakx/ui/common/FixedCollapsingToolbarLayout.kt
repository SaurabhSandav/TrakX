package com.redridgeapps.trakx.ui.common

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.CollapsingToolbarLayout

/* Fixes empty status bar sized space showing under child views */
class FixedCollapsingToolbarLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : CollapsingToolbarLayout(context, attrs, defStyle) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var newHeightMeasureSpec = heightMeasureSpec
        super.onMeasure(widthMeasureSpec, newHeightMeasureSpec)

        val fs = this.javaClass.superclass!!.getDeclaredField("lastInsets")
        fs.isAccessible = true
        val mLastInsets = fs.get(this) as WindowInsetsCompat?
        val mode = MeasureSpec.getMode(newHeightMeasureSpec)
        val topInset = mLastInsets?.systemWindowInsetTop ?: 0
        if (mode == MeasureSpec.UNSPECIFIED && topInset > 0) {
            // fix the bottom empty padding
            newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                measuredHeight - topInset, MeasureSpec.EXACTLY
            )
            super.onMeasure(widthMeasureSpec, newHeightMeasureSpec)
        }

    }
}

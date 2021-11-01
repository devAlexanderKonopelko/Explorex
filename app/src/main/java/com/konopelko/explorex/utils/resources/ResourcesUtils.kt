package com.konopelko.explorex.utils.resources

import android.content.res.Resources
import android.graphics.Bitmap
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.konopelko.explorex.R

fun getBitmapFromResource(resources: Resources, drawableId: Int): Bitmap {
    val drawable = ResourcesCompat.getDrawable(resources, drawableId, null)
    return drawable!!.toBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight
    )
}
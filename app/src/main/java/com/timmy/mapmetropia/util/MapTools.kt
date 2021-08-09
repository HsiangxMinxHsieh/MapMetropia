package com.timmy.mapmetropia.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.location.Location
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory


/** GoogleMap：根据传入的 view，创建 BitmapDescriptor 对象  */
fun fromView(context: Context, view: View): BitmapDescriptor? {
    val frameLayout = FrameLayout(context)
    frameLayout.addView(view)
    frameLayout.isDrawingCacheEnabled = true
    val bitmap = getBitmapFromView(frameLayout)
    val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap)
    bitmap?.recycle()
    return bitmapDescriptor
}

/** Convert a view to bitmap  */
fun getBitmapFromView(view: View): Bitmap? {
    return try {
        banTextViewHorizontallyScrolling(view)
        view.destroyDrawingCache()
        view.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        val matrix = Matrix()
        matrix.postRotate(180f) /*翻轉180度*/

        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        val bitmap = view.drawingCache
        bitmap?.copy(Bitmap.Config.ARGB_8888, false)
    } catch (ex: Throwable) {
        logi("getBitmapFromView", "getBitmapFromView is null ")
        null
    }
}

/** 禁止 TextView 水平滚动  */
private fun banTextViewHorizontallyScrolling(view: View) {
    if (view is ViewGroup) {
        for (index in 0 until view.childCount) {
            banTextViewHorizontallyScrolling(view.getChildAt(index))
        }
    } else if (view is TextView) {
        view.setHorizontallyScrolling(false)
    }
}

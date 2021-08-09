package com.timmy.mapmetropia.uitool

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.View
import java.lang.reflect.Field

object ViewTool {

    fun DpToPx(context: Context, dp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()
    }

}
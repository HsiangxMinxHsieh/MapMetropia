package com.timmy.mapmetropia.uitool

import android.R
import android.annotation.TargetApi
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.timmy.mapmetropia.util.logi

/**
 * 开启全屏模式
 */
fun hideSystemUI(view: View) {
    // Set the IMMERSIVE flag.
    // Set the content to appear under the system bars so that the content
    // doesn't resize when the system bars hide and show.
    //开启全屏模式
    view.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar

//                or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar

            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
}

fun View.setViewSize(w: Int, h: Int) {
    try {
        this.layoutParams.width = w
        this.layoutParams.height = h
    } catch (e: Exception) {
        //如果prams不存在 則重新建立
        val params = ViewGroup.LayoutParams(w, h)
        params.width = w
        params.height = h
        this.layoutParams = params
    }
    this.requestLayout()
}

/**
 * 設定 view的長寬 單位為dp
 * @param view
 * @param w
 * @param h
 * @author Wang / Robert
 * @date 2015/5/8 下午3:13:42
 * @version
 */
fun View.setViewSizeByDpUnit(w: Int, h: Int) {
    setViewSize(getPixelFromDpByDevice(this.context, w), getPixelFromDpByDevice(this.context, h))
}

fun View.setTextSize(sp: Int) {
    val displayMetrics = this.context.resources.displayMetrics
    val realSpSize =
        ((sp * displayMetrics.widthPixels).toFloat() / displayMetrics.density / 360f).toInt()
    if (this is TextView) {
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, realSpSize.toFloat())
    } else if (this is Button) {
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, realSpSize.toFloat())
    } else if (this is EditText) {
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, realSpSize.toFloat())
    } else {
        (this as TextView).setTextSize(TypedValue.COMPLEX_UNIT_SP, realSpSize.toFloat())
    }
}

/**
 *
 * 輸入DP單位數值 根據裝置動態 回傳像素:
 * @author Robert Chou didi31139@gmail.com
 * @param dpSize 整數 單位為dp
 * @date 2015/6/17 下午5:25:39
 * @return dp根據裝置動態計算 回傳pixel
 * @version
 */
fun getPixelFromDpByDevice(context: Context, dpSize: Int): Int {
    val displayMetrics = context.resources.displayMetrics
    val realSpSize =
        ((dpSize * displayMetrics.widthPixels).toFloat() / displayMetrics.density / 360f).toInt()
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        realSpSize.toFloat(),
        context.resources.displayMetrics
    ).toInt()
}

/**
 *
 * 設定物件間距  單位為畫素(pixel)
 * 上層類別須為 RelativeLayout or LinearLayout
 * @author Wang / Robert Chou didi31139@gmail.com
 * @date 2015/5/26 下午3:25:33
 * @version
 */
fun View.setMarginByDpUnit(leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int) {
    val params = this.layoutParams
    if (params is ViewGroup.MarginLayoutParams) {
        params.setMargins(
            getPixelFromDpByDevice(this.context, leftMargin),
            getPixelFromDpByDevice(this.context, topMargin),
            getPixelFromDpByDevice(this.context, rightMargin),
            getPixelFromDpByDevice(this.context, bottomMargin)
        )
    }
    this.layoutParams = params
    this.requestLayout()
}

fun View.setPaddingByDpUnit(
    leftPadding: Int,
    topPadding: Int,
    rightPadding: Int,
    bottomPadding: Int
) {
    this.setPadding(
        getPixelFromDpByDevice(this.context, leftPadding),
        getPixelFromDpByDevice(this.context, topPadding),
        getPixelFromDpByDevice(this.context, rightPadding),
        getPixelFromDpByDevice(this.context, bottomPadding)
    )
}
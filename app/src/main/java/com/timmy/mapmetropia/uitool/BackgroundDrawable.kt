package com.timmy.mapmetropia.uitool

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import androidx.core.graphics.drawable.DrawableCompat
import androidx.appcompat.content.res.AppCompatResources
import android.view.View

/**
 * @param context
 * @param tldp 左上弧度 (PixelByDevice)
 * @param trdp 右上弧度 (PixelByDevice)
 * @param bldp 左下弧度 (PixelByDevice)
 * @param brdp 右下弧度 (PixelByDevice)
 * @param bgColorID 背景填滿色
 * @param strokeColorID 邊框顏色
 * @param strokeWidth 邊框粗細 (PixelByDevice)
 * @date 2015/10/16 下午5:01:09
 * @version
 */
fun getRectangleBg(
    context: Context,
    tldp: Int, trdp: Int, bldp: Int, brdp: Int,
    bgColorID: Int, strokeColorID: Int = 0, strokeWidth: Int = 0
): GradientDrawable {
    val tl = getPixelFromDpByDevice(context, tldp)
    val tr = getPixelFromDpByDevice(context, trdp)
    val bl = getPixelFromDpByDevice(context, bldp)
    val br = getPixelFromDpByDevice(context, brdp)
    return DrawableModular.createShapeDrawable(context, bgColorID, floatArrayOf(tl.toFloat(), tl.toFloat(), tr.toFloat(), tr.toFloat(), br.toFloat(), br.toFloat(), bl.toFloat(), bl.toFloat()), getPixelFromDpByDevice(context, strokeWidth), strokeColorID, GradientDrawable.RECTANGLE)
}

/**
 * @param context
 * @param corner 圓角弧度
 * @param bgColorID 背景填滿色
 * @param strokeColorID 邊框顏色
 * @param strokeWidth 邊框粗細 (PixelByDevice)
 * @date 2015/10/16 下午5:01:09
 * @version
 */
fun getRoundBg(
    context: Context,
    corner: Int,
    bgColorID: Int, strokeColorID: Int = 0, strokeWidth: Int = 0
): GradientDrawable {

    return getRectangleBg(context, corner, corner, corner, corner, bgColorID, strokeColorID, strokeWidth)
}

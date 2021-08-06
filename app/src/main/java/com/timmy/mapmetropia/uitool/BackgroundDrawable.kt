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
 *
 * 用來製造囍婚禮所需要的背景圖片
 * @author Robert Chou didi31139@gmail.com
 * @date 2015/5/26 上午11:39:06
 * @version
 */

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
    val tl = UserInterfaceTool.getPixelFromDpByDevice(context, tldp)
    val tr = UserInterfaceTool.getPixelFromDpByDevice(context, trdp)
    val bl = UserInterfaceTool.getPixelFromDpByDevice(context, bldp)
    val br = UserInterfaceTool.getPixelFromDpByDevice(context, brdp)
    return DrawableModular.createShapeDrawable(context, bgColorID, floatArrayOf(tl.toFloat(), tl.toFloat(), tr.toFloat(), tr.toFloat(), br.toFloat(), br.toFloat(), bl.toFloat(), bl.toFloat()), UserInterfaceTool.getPixelFromDpByDevice(context, strokeWidth), strokeColorID, GradientDrawable.RECTANGLE)
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
    val tl = UserInterfaceTool.getPixelFromDpByDevice(context, corner)
    val tr = UserInterfaceTool.getPixelFromDpByDevice(context, corner)
    val bl = UserInterfaceTool.getPixelFromDpByDevice(context, corner)
    val br = UserInterfaceTool.getPixelFromDpByDevice(context, corner)
    return DrawableModular.createShapeDrawable(context, bgColorID, floatArrayOf(tl.toFloat(), tl.toFloat(), tr.toFloat(), tr.toFloat(), br.toFloat(), br.toFloat(), bl.toFloat(), bl.toFloat()), UserInterfaceTool.getPixelFromDpByDevice(context, strokeWidth), strokeColorID, GradientDrawable.RECTANGLE)
}

/**
 * @param context
 * @param tl 左上弧度 (PixelByDevice)
 * @param tr 右上弧度 (PixelByDevice)
 * @param bl 左下弧度 (PixelByDevice)
 * @param br 右下弧度 (PixelByDevice)
 * @param bgColorID 背景填滿色
 * @param strokeColorID 邊框顏色
 * @param strokeWidth 邊框粗細 (PixelByDevice)
 * @date 2015/10/16 下午5:01:09
 * @version
 */
fun getRectangleBgByPx(
    context: Context,
    tl: Int, tr: Int, bl: Int, br: Int,
    bgColorID: Int, strokeColorID: Int, strokeWidth: Int
): GradientDrawable {
    return DrawableModular.createShapeDrawable(context, bgColorID, floatArrayOf(tl.toFloat(), tl.toFloat(), tr.toFloat(), tr.toFloat(), br.toFloat(), br.toFloat(), bl.toFloat(), bl.toFloat()), strokeWidth, strokeColorID, GradientDrawable.RECTANGLE)
}

/**
 * @param context
 * @param tldp 左上弧度 (PixelByDevice)
 * @param trdp 右上弧度 (PixelByDevice)
 * @param bldp 左下弧度 (PixelByDevice)
 * @param brdp 右下弧度 (PixelByDevice)
 * @param left 是否顯示邊框 true為顯示
 * @param top 是否顯示邊框 true為顯示
 * @param right 是否顯示邊框 true為顯示
 * @param bottom 是否顯示邊框 true為顯示
 * @param bgColorID 背景填滿色
 * @param strokeColorID 邊框顏色
 * @param strokeWidth 邊框粗細 (PixelByDevice)
 * @date 2015/10/16 下午5:01:09
 * @version
 */

fun getRectangleBg(
    context: Context,
    tldp: Int, trdp: Int, bldp: Int, brdp: Int,
    left: Boolean, top: Boolean, right: Boolean, bottom: Boolean,
    bgColorID: Int, strokeColorID: Int, strokeWidth: Int
): LayerDrawable {

    val tl = UserInterfaceTool.getPixelFromDpByDevice(context, tldp)
    val tr = UserInterfaceTool.getPixelFromDpByDevice(context, trdp)
    val bl = UserInterfaceTool.getPixelFromDpByDevice(context, bldp)
    val br = UserInterfaceTool.getPixelFromDpByDevice(context, brdp)

    val drawable =
        DrawableModular.createShapeDrawable(context, bgColorID, floatArrayOf(tl.toFloat(), tl.toFloat(), tr.toFloat(), tr.toFloat(), br.toFloat(), br.toFloat(), bl.toFloat(), bl.toFloat()), UserInterfaceTool.getPixelFromDpByDevice(context, strokeWidth), strokeColorID, GradientDrawable.RECTANGLE)

    val layerDrawable = LayerDrawable(arrayOf<Drawable>(drawable))
    val w = UserInterfaceTool.getPixelFromDpByDevice(context, strokeWidth)
    layerDrawable.setLayerInset(0, if (left == true) 0 else -w, if (top == true) 0 else -w, if (right == true) 0 else -w, if (bottom == true) 0 else -w)

    return layerDrawable
}

/**
 *
 * 回傳色碼的圓形圖片
 * @author Robert Chou didi31139@gmail.com
 * @date 2015/6/3 上午1:24:10
 * @version
 */
fun getOVALPointDrawable(context: Context, colorID: Int): GradientDrawable {
    return DrawableModular.createShapeDrawable(context, colorID, floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f), 0, 0, GradientDrawable.OVAL)
}

fun getRepatDrawable(context: Context, drawableID: Int): BitmapDrawable {
    return getRepatDrawable(context, BitmapFactory.decodeResource(context.resources, drawableID))
}

fun getRepatDrawable(context: Context, bitmap: Bitmap): BitmapDrawable {
    val bitmapDrawable = BitmapDrawable(context.resources, bitmap)
    bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
    return bitmapDrawable
}

/**
 * 取得 svg drawable
 * */
fun getTintedDrawable(view: View, drawableId: Int, colorId: Int): Drawable {
    var drawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        view.resources.getDrawable(drawableId, view.context.theme)
    } else {
        view.resources.getDrawable(drawableId)
    }
    drawable = DrawableCompat.wrap(drawable)
    DrawableCompat.setTintList(drawable.mutate(), AppCompatResources.getColorStateList(view.context, colorId))
    return drawable
}
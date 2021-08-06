package com.timmy.mapmetropia.uitool

import android.content.Context
import android.graphics.drawable.GradientDrawable

/**
 *
 * Description:
 * @author Robert Chou didi31139@gmail.com
 * @date 2015/5/26 上午11:39:51
 * @version
 */
object DrawableModular {

    /**
     *
     * 建立形狀的圖片並回傳
     * @author Robert Chou didi31139@gmail.com
     * @param context
     * @param colorID ex:填滿顏色
     * @param radii The corners are ordered top-left, top-right, bottom-right, bottom-left.
     * @param strokeWidth 外框畫筆寬度
     * @param strokeColorID 外框顏色
     * @param gradientDrawableShape 圖片形狀 ex:GradientDrawable.RECTANGLE
     * @date 2015/5/26 上午11:50:33
     * @version
     */
    fun createShapeDrawable(context: Context, colorID: Int, radii: FloatArray?, strokeWidth: Int, strokeColorID: Int, gradientDrawableShape: Int): GradientDrawable {
        val gradientDrawable = GradientDrawable()
        gradientDrawable.setColor(context.resources.getColor(colorID))
        if (radii != null) {
            gradientDrawable.cornerRadii = radii
        }
        if (strokeWidth != 0) {
            gradientDrawable.setStroke(strokeWidth, context.resources.getColor(strokeColorID))
        }

        if (gradientDrawableShape != 0) {
            gradientDrawable.shape = gradientDrawableShape
        }
        return gradientDrawable
    }

}

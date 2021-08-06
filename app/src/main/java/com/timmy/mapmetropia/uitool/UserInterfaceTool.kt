package com.timmy.mapmetropia.uitool

import android.R
import android.annotation.TargetApi
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.os.Build.VERSION
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

/**
 *
 * 統一快速處理物件 大小/間距 /點擊狀態/點擊顏色
 * @author Robert Chou didi31139@gmail.com
 * @date 2015/5/28 下午2:50:52
 * @version
 */
object UserInterfaceTool {

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

    /**
     *
     * 取得螢幕寬度 單位為整數(pixel)
     * @author Robert Chou didi31139@gmail.com
     * @date 2015/5/29 下午1:37:13
     * @version
     */
    fun getScreenWidthPixels(context: Context?): Int {
        return context!!.resources.displayMetrics.widthPixels
    }

    /**
     *
     * 取得螢幕高度 單位為整數(pixel)
     * @author Robert Chou didi31139@gmail.com
     * @date 2015/5/29 下午1:37:13
     * @version
     */
    fun getScreenHeightPixels(context: Context?): Int {
        return context!!.resources.displayMetrics.heightPixels
    }

    fun getWindowHeight(windowManager: WindowManager): Int {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)

        val display = windowManager.defaultDisplay
        val size = Point()
        var height = 0
        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(size)
            height = size.y
        } else {
            height = display.height
        }
        return height
    }

    fun getWindowWidth(windowManager: WindowManager): Int {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)

        val display = windowManager.defaultDisplay
        val size = Point()
        var width = 0
        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(size)
            width = size.x
        } else {
            width = display.width
        }
        return width
    }

    /**
     * 設定 view的長寬 單位為畫素(pixel)
     * @param view
     * @param w
     * @param h
     * @author Wang / Robert
     * @date 2015/5/8 下午3:13:42
     * @version
     */
    fun setViewSize(view: View, w: Int, h: Int) {
        try {
            view.layoutParams.width = w
            view.layoutParams.height = h
        } catch (e: Exception) {
            //如果prams不存在 則重新建立
            val params = ViewGroup.LayoutParams(w, h)
            params.width = w
            params.height = h
            view.layoutParams = params
        }
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
    fun setViewSizeByDpUnit(view: View, w: Int, h: Int) {
        setViewSize(view, getPixelFromDpByDevice(view.context, w), getPixelFromDpByDevice(view.context, h))
    }

    /**
     * 設定 view的長寬 單位為畫素(pixel) 自動高度
     * @param view
     * @param w
     * @param rid
     * @author Wang / Robert
     * @date 2015/5/8 下午3:13:42
     * @version
     */
    fun setViewSizeByResWidth(view: View, w: Int, rid: Int) {
        val h = ViewTool.getImageHeight(view.context, rid, w)
        try {
            view.layoutParams.width = w
            view.layoutParams.height = h
        } catch (e: Exception) {
            //如果prams不存在 則重新建立
            val params = ViewGroup.LayoutParams(w, h)
            params.width = w
            params.height = h
            view.layoutParams = params
        }
    }

    /**
     * 設定 view的長寬 單位為畫素(pixel) 自動寬度
     * @param view
     * @param h
     * @param rid
     * @author Wang / Robert
     * @date 2015/5/8 下午3:13:42
     * @version
     */
    fun setViewSizeByResHeight(view: View, h: Int, rid: Int) {
        val w = ViewTool.getImageWidth(view.context, rid, h)
        try {
            view.layoutParams.width = w
            view.layoutParams.height = h
        } catch (e: Exception) {
            //如果prams不存在 則重新建立
            val params = ViewGroup.LayoutParams(w, h)
            params.width = w
            params.height = h
            view.layoutParams = params
        }
    }

    /**
     *
     * 設定物件間距  單位為畫素(pixel)
     * 上層類別須為 RelativeLayout or LinearLayout
     * @author Wang / Robert Chou didi31139@gmail.com
     * @date 2015/5/26 下午3:25:33
     * @version
     */
    fun setMargin(view: View, leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int) {
        val params = view.layoutParams
        if (params is LinearLayout.LayoutParams) {
            params.setMargins(leftMargin, topMargin, rightMargin, bottomMargin)
        } else if (params is RelativeLayout.LayoutParams) {
            params.setMargins(leftMargin, topMargin, rightMargin, bottomMargin)
        }

        view.layoutParams = params
    }

    /**
     *
     * 設定物件間距  單位為畫素(pixel)
     * 上層類別須為 RelativeLayout or LinearLayout
     * @author Wang / Robert Chou didi31139@gmail.com
     * @date 2015/5/26 下午3:25:33
     * @version
     */
    fun setMarginByDpUnit(view: View, leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int) {
        setMargin(view, getPixelFromDpByDevice(view.context, leftMargin), getPixelFromDpByDevice(view.context, topMargin), getPixelFromDpByDevice(view.context, rightMargin), getPixelFromDpByDevice(view.context, bottomMargin))
    }

    fun setPadding(view: View, leftPadding: Int, topPadding: Int, rightPadding: Int, bottomPadding: Int) {
        view.setPadding(leftPadding, topPadding, rightPadding, bottomPadding)
    }

    fun setPaddingByDpUnit(view: View, leftPadding: Int, topPadding: Int, rightPadding: Int, bottomPadding: Int) {
        setPadding(view, getPixelFromDpByDevice(view.context, leftPadding), getPixelFromDpByDevice(view.context, topPadding), getPixelFromDpByDevice(view.context, rightPadding), getPixelFromDpByDevice(view.context, bottomPadding))
    }

    /**
     *
     * 根據版本加入背景:
     * @author Robert Chou didi31139@gmail.com
     * @date 2015/5/26 下午2:48:26
     * @param view 物件存在的集合
     * @param drawable 要設定的背景圖片
     * @version
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    fun setBackground(view: View, drawable: Drawable?) {
        view.background = drawable
    }

    /**
     *
     *  設定View的文字大小(以360DP寬的比例)
     * @author Robert Chou didi31139@gmail.com
     * @date 2014/11/19 上午12:07:08
     * @param sp 為當前的sp整數下去做轉換
     */
    fun setTextSize(view: View, sp: Int) {
        val displayMetrics = view.context.resources.displayMetrics
        val realSpSize = ((sp * displayMetrics.widthPixels).toFloat() / displayMetrics.density / 360f).toInt()
        if (view is TextView) {
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, realSpSize.toFloat())
        } else if (view is Button) {
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, realSpSize.toFloat())
        } else if (view is EditText) {
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, realSpSize.toFloat())
        } else {
            (view as TextView).setTextSize(TypedValue.COMPLEX_UNIT_SP, realSpSize.toFloat())
        }
    }

    /**取得換算的文字大小單位pixel */
    fun getTextSize(context: Context, sp: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        return ((sp * displayMetrics.widthPixels).toFloat() / displayMetrics.density / 360f).toInt()
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
        val realSpSize = ((dpSize * displayMetrics.widthPixels).toFloat() / displayMetrics.density / 360f).toInt()
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, realSpSize.toFloat(), context.resources.displayMetrics).toInt()
    }

    /**
     * 設定 壓下的圖片切換效果
     * @param unPressedDrawable 未按下的圖片 R.drawable.image
     * @param pressedDrawable 未按下的圖片 R.drawable.pressedimage
     */
    fun setPressedImage(view: View, unPressedDrawable: Drawable, pressedDrawable: Drawable?) {
        if (pressedDrawable == null) {
            setBackground(view, unPressedDrawable)
            return
        }
        val states = StateListDrawable()
        states.addState(intArrayOf(android.R.attr.state_pressed), pressedDrawable)
        states.addState(intArrayOf(android.R.attr.state_focused), pressedDrawable)
        states.addState(intArrayOf(android.R.attr.state_checked), pressedDrawable)
        states.addState(intArrayOf(), unPressedDrawable)
        if (view is Button) {
            setBackground(view, states)
        } else {
            (view as ImageView).setImageDrawable(states)
        }

    }

    /**
     * 設定 壓下的圖片切換效果
     * @param unPressedDrawableID 未按下的圖片 R.drawable.image
     * @param pressedDrawableID 未按下的圖片 R.drawable.pressedimage
     */
    fun setPressedImage(view: View, unPressedDrawableID: Int, pressedDrawableID: Int) {
        setPressedImage(view, view.context.resources.getDrawable(unPressedDrawableID), view.context.resources.getDrawable(pressedDrawableID))
    }

    /**
     * 設定 壓下的圖片切換效果
     * @param unPressedDrawable 未按下的圖片 R.drawable.image
     * @param pressedDrawable 未按下的圖片 R.drawable.pressedimage
     */
    fun setPressedBackground(view: View, unPressedDrawable: Drawable, pressedDrawable: Drawable?) {
        if (pressedDrawable == null) {
            setBackground(view, unPressedDrawable)
            return
        }
        val states = StateListDrawable()
        states.addState(intArrayOf(android.R.attr.state_pressed), pressedDrawable)
        states.addState(intArrayOf(android.R.attr.state_focused), pressedDrawable)
        states.addState(intArrayOf(android.R.attr.state_checked), pressedDrawable)
        states.addState(intArrayOf(), unPressedDrawable)
        setBackground(view, states)
    }

    /**
     * 設定 壓下的圖片切換效果
     * @param unPressedDrawable 未按下的圖片 R.drawable.image
     * @param pressedDrawable 未按下的圖片 R.drawable.pressedimage
     */
    fun setPressedBackground(view: View, unPressedDrawable: Int, pressedDrawable: Int) {
        setPressedBackground(view, view.context.resources.getDrawable(unPressedDrawable), view.context.resources.getDrawable(pressedDrawable))
    }

    /**
     * check box 狀態設定
     * @param basedrawable 未按下的圖片 R.drawable.image
     * @param checkeddrawable 未按下的圖片 R.drawable.pressedimage 0為不給
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    fun setCheckDrawable(context: Context, view: View, basedrawable: Int, checkeddrawable: Int) {
        if (checkeddrawable == 0) {
            view.setBackgroundResource(basedrawable)
            return
        }
        val states = StateListDrawable()
        states.addState(intArrayOf(android.R.attr.state_checkable), context.resources.getDrawable(basedrawable))
        states.addState(intArrayOf(android.R.attr.state_checked), context.resources.getDrawable(checkeddrawable))
        states.addState(intArrayOf(), context.resources.getDrawable(basedrawable))

        if (view is CheckBox) {
            view.buttonDrawable = states
        } else if (VERSION.SDK_INT >= 16) {
            view.background = states
        } else {
            view.setBackgroundDrawable(states)
        }
    }

    /**
     * 設定按鈕 被按住的顏色背景
     * @param unPressedColor 未按下的顏色背景 R.color.color1
     * @param pressedColor 按下的顏色 R.color.color 0為不給
     */
    fun setPressedBackgroundColor(view: View, unPressedColor: Int, pressedColor: Int) {
        val context = view.context
        if (pressedColor == 0) {
            view.setBackgroundResource(unPressedColor)
            return
        }
        val unPressedcolorDrawable = ColorDrawable(context.resources.getColor(unPressedColor))
        val pressedcolorDrawable = ColorDrawable(context.resources.getColor(pressedColor))
        val states = StateListDrawable()
        states.addState(intArrayOf(android.R.attr.state_pressed), pressedcolorDrawable)
        states.addState(intArrayOf(android.R.attr.state_focused), pressedcolorDrawable)
        states.addState(intArrayOf(android.R.attr.state_checked), pressedcolorDrawable)
        states.addState(intArrayOf(), unPressedcolorDrawable)
        setBackground(view, states)
    }


    /**
     * 設定按鈕 被按住的顏色背景
     * @param unPressedColor 未按下的顏色背景 R.color.color1
     * @param pressedColor 按下的顏色 R.color.color 0為不給
     */
    fun setPressedTextColor(view: View, unPressedColor: Int, pressedColor: Int) {
        val context = view.context
        if (pressedColor == 0) {
            (view as TextView).setTextColor(context.resources.getColor(unPressedColor))
            return
        }
        val colorStateList = ColorStateList(arrayOf(intArrayOf(android.R.attr.state_pressed), intArrayOf(android.R.attr.state_focused), intArrayOf(android.R.attr.state_checked), intArrayOf()), intArrayOf(context.resources.getColor(pressedColor), context.resources.getColor(pressedColor), context.resources.getColor(pressedColor), context.resources.getColor(unPressedColor)))
        (view as TextView).setTextColor(colorStateList)
    }

    /**
     * 設定Tab按鈕 被按住的顏色背景
     */
    fun setTabPressedImage(view: View, unPressedDrawable: Drawable, pressedDrawable: Drawable?) {
        if (pressedDrawable == null) {
            setBackground(view, unPressedDrawable)
            return
        }
        val states = StateListDrawable()
        states.addState(intArrayOf(android.R.attr.state_pressed), unPressedDrawable)
        states.addState(intArrayOf(android.R.attr.state_focused), unPressedDrawable)
        states.addState(intArrayOf(android.R.attr.state_checked), unPressedDrawable)
        states.addState(intArrayOf(android.R.attr.state_selected), pressedDrawable)
        states.addState(intArrayOf(), unPressedDrawable)
        if (view is Button) {
            setBackground(view, states)
        } else {
            (view as ImageView).setImageDrawable(states)
        }

    }

    /**
     * 設定Tab按鈕 被按住的顏色背景
     * @param unPressedDrawableID 未按下的顏色背景 R.color.color1
     * @param pressedDrawableID 按下的顏色 R.color.color 0為不給
     */
    fun setTabPressedImage(view: View, unPressedDrawableID: Int, pressedDrawableID: Int) {
        var unPressedDrawable: Drawable? = null
        var pressedDrawable: Drawable? = null
        if (unPressedDrawableID == 0) {
            unPressedDrawable = ColorDrawable(view.resources.getColor(android.R.color.transparent))
        } else {
            unPressedDrawable = view.resources.getDrawable(unPressedDrawableID)
        }
        if (pressedDrawableID == 0) {
            pressedDrawable = ColorDrawable(view.resources.getColor(android.R.color.transparent))
        } else {
            pressedDrawable = view.resources.getDrawable(pressedDrawableID)
        }
        if (pressedDrawable == null) {
            setBackground(view, unPressedDrawable)
            return
        }
        val states = StateListDrawable()
        states.addState(intArrayOf(android.R.attr.state_pressed), unPressedDrawable)
        states.addState(intArrayOf(android.R.attr.state_focused), unPressedDrawable)
        states.addState(intArrayOf(android.R.attr.state_checked), unPressedDrawable)
        states.addState(intArrayOf(android.R.attr.state_selected), pressedDrawable)
        states.addState(intArrayOf(), unPressedDrawable)
        if (view is Button) {
            setBackground(view, states)
        } else {
            (view as ImageView).setImageDrawable(states)
        }

    }

    /**
     * 設定Tab按鈕 被按住的顏色背景
     * @param unPressedDrawableID 未按下的顏色背景 R.color.color1
     * @param pressedDrawableID 按下的顏色 R.color.color 0為不給
     */
    fun setTabPressedBackgroundColor(view: View, unPressedDrawableID: Int, pressedDrawableID: Int) {
        var unPressedDrawable: Drawable? = null
        var pressedDrawable: Drawable? = null
        if (unPressedDrawableID == 0) {
            unPressedDrawable = ColorDrawable(view.resources.getColor(android.R.color.transparent))
        } else {
            unPressedDrawable = view.resources.getDrawable(unPressedDrawableID)
        }
        if (pressedDrawableID == 0) {
            pressedDrawable = ColorDrawable(view.resources.getColor(android.R.color.transparent))
        } else {
            pressedDrawable = view.resources.getDrawable(pressedDrawableID)
        }
        if (pressedDrawable == null) {
            setBackground(view, unPressedDrawable)
            return
        }
        val states = StateListDrawable()
        states.addState(intArrayOf(android.R.attr.state_pressed), unPressedDrawable)
        states.addState(intArrayOf(android.R.attr.state_focused), unPressedDrawable)
        states.addState(intArrayOf(android.R.attr.state_checked), unPressedDrawable)
        states.addState(intArrayOf(android.R.attr.state_selected), pressedDrawable)
        states.addState(intArrayOf(), unPressedDrawable)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            //設定水波文
            setRippleBackround(view, R.color.holo_blue_bright, states)
        }else{
            view.background = states
        }
    }

    /**設定水波文*/
    fun setRippleBackround(view: View, color: Int, states: Drawable?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            val rippleDrawable = RippleDrawable(ColorStateList.valueOf(Color.BLACK) , states , null)
            val attrs = intArrayOf(android.R.attr.selectableItemBackground)
            val typedArray = view.context.obtainStyledAttributes(attrs)
            val rippleDrawable = typedArray.getDrawable(0) as RippleDrawable
            typedArray.recycle()
            rippleDrawable.setColor(ColorStateList.valueOf(view.resources.getColor(color)))
            if (states != null) {
                if (VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    rippleDrawable.addLayer(states)
                }
            }
            view.background = rippleDrawable
        }
    }

    /**
     * 設定Tab按鈕 被按住的顏色背景
     * @param unPressedColor 未按下的顏色背景 R.color.color1
     * @param pressedColor 按下的顏色 R.color.color 0為不給
     */
    fun setTabPressedTextColor(view: View, unPressedColor: Int, pressedColor: Int) {
        val context = view.context
        if (pressedColor == 0) {
            (view as TextView).setTextColor(context.resources.getColor(unPressedColor))
            return
        }
        val colorStateList = ColorStateList(arrayOf(intArrayOf(android.R.attr.state_pressed), intArrayOf(android.R.attr.state_focused), intArrayOf(android.R.attr.state_checked), intArrayOf(android.R.attr.state_selected), intArrayOf()), intArrayOf(context.resources.getColor(unPressedColor), context.resources.getColor(unPressedColor), context.resources.getColor(unPressedColor), context.resources.getColor(pressedColor), context.resources.getColor(unPressedColor)))
        (view as TextView).setTextColor(colorStateList)
    }

    fun setTextView(textView: TextView, width: Int, height: Int, sp: Int, stringid: Int, unPressedColor: Int, pressedColor: Int) {
        setViewSize(textView, width, height)
        setTextSize(textView, sp)
        textView.text = textView.context.getString(stringid)
        setPressedTextColor(textView, unPressedColor, pressedColor)
    }

    fun setTextView(textView: TextView, width: Int, height: Int, sp: Int, string: String, unPressedColor: Int, pressedColor: Int) {
        setViewSize(textView, width, height)
        setTextSize(textView, sp)
        textView.text = string
        setPressedTextColor(textView, unPressedColor, pressedColor)
    }
}

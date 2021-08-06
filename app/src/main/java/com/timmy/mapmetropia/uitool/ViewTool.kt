package com.timmy.mapmetropia.uitool

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.View
import java.lang.reflect.Field

object ViewTool {

    fun SpToPx(context: Context, sp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.resources.displayMetrics).toInt()
    }

    fun PxToSp(context: Context, px: Int): Float {
        return px / context.resources.displayMetrics.scaledDensity
    }

    fun DpToPx(context: Context, dp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()
    }

    fun PxToDp(context: Context, px: Int): Float {
        return px / context.resources.displayMetrics.density
    }

    fun getImageHeight(context: Context, id: Int, maxWidth: Int): Int {
        val drawable = context.resources.getDrawable(id)
        val w = drawable.intrinsicWidth.toFloat()
        val h = drawable.intrinsicHeight.toFloat()
        val scale = h / w
        val ansHeight = maxWidth * scale

        return ansHeight.toInt()
    }

    fun getImageHeight(context: Context, uri: String, maxWidth: Int): Int {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(uri, options)

        val w = options.outWidth.toFloat()
        val h = options.outHeight.toFloat()
        val scale = h / w
        val ansHeight = maxWidth * scale

        return ansHeight.toInt()
    }

    fun getImageWidth(context: Context, id: Int, maxHeight: Int): Int {
        val drawable = context.resources.getDrawable(id)
        val w = drawable.intrinsicWidth.toFloat()
        val h = drawable.intrinsicHeight.toFloat()
        val scale = w / h
        val ansWidth = maxHeight * scale

        return ansWidth.toInt()
    }

    fun getImageWidth(context: Context, uri: String, maxHeight: Int): Int {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(uri, options)

        val w = options.outWidth.toFloat()
        val h = options.outHeight.toFloat()
        val scale = w / h
        val ansWidth = maxHeight * scale

        return ansWidth.toInt()
    }

    /**
     * Returns the current View.OnClickListener for the given View
     * @param view the View whose click listener to retrieve
     * @return the View.OnClickListener attached to the view; null if it could not be retrieved
     */
    fun getOnClickListener(view: View): View.OnClickListener? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getOnClickListenerV14(view)
        } else {
            getOnClickListenerV(view)
        }
    }

    //Used for APIs lower than ICS (API 14)
    fun getOnClickListenerV(view: View): View.OnClickListener? {
        var retrievedListener: View.OnClickListener? = null
        val viewStr = "android.view.View"
        val field: Field

        try {
            field = Class.forName(viewStr).getDeclaredField("mOnClickListener")
            retrievedListener = field.get(view) as View.OnClickListener
        } catch (ex: NoSuchFieldException) {
            Log.e("Reflection", "No Such Field.")
        } catch (ex: IllegalAccessException) {
            Log.e("Reflection", "Illegal Access.")
        } catch (ex: ClassNotFoundException) {
            Log.e("Reflection", "Class Not Found.")
        }

        return retrievedListener
    }

    //Used for new ListenerInfo class structure used beginning with API 14 (ICS)
    fun getOnClickListenerV14(view: View): View.OnClickListener? {
        var retrievedListener: View.OnClickListener? = null
        val viewStr = "android.view.View"
        val lInfoStr = "android.view.View\$ListenerInfo"

        try {
            val listenerField = Class.forName(viewStr).getDeclaredField("mListenerInfo")
            var listenerInfo: Any? = null

            if (listenerField != null) {
                listenerField.isAccessible = true
                listenerInfo = listenerField.get(view)
            }

            val clickListenerField = Class.forName(lInfoStr).getDeclaredField("mOnClickListener")

            if (clickListenerField != null && listenerInfo != null) {
                retrievedListener = clickListenerField.get(listenerInfo) as View.OnClickListener
            }
        } catch (ex: NoSuchFieldException) {
            Log.e("Reflection", "No Such Field.")
        } catch (ex: IllegalAccessException) {
            Log.e("Reflection", "Illegal Access.")
        } catch (ex: ClassNotFoundException) {
            Log.e("Reflection", "Class Not Found.")
        }

        return retrievedListener
    }

    fun getOnTouchListener(v: View): View.OnTouchListener? {
        var listener: View.OnTouchListener? = null

        try {
            val listenerInfo = getListenerInfo(v)

            if (listenerInfo != null) {
                val clazz = listenerInfo.javaClass

                val f = clazz.getDeclaredField("mOnTouchListener")
                f.isAccessible = true
                listener = f.get(listenerInfo) as View.OnTouchListener
            }

        } catch (e: Exception) {
            Log.i("", "Could not extract mOnTouchListener", e)
        }

        return listener
    }

    private fun getListenerInfo(v: View): Any? {
        var f: Field? = null

        try {

            f = View::class.java.getDeclaredField("mListenerInfo")
            f!!.isAccessible = true

            return f.get(v) as Any

        } catch (e: Exception) {
            Log.i("", "Could not extract field mListenerInfo", e)
            return null
        }

    }


    //	public static View.OnTouchListener getOnTouchListener(View view) {
    //	    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
    //	        return getOnTouchListenerV14(view);
    //	    } else {
    //	        return getOnTouchListenerV(view);
    //	    }
    //	}

    //Used for APIs lower than ICS (API 14)
    fun getOnTouchListenerV(view: View): View.OnTouchListener? {
        var retrievedListener: View.OnTouchListener? = null
        val viewStr = "android.view.View"
        val field: Field

        try {
            field = Class.forName(viewStr).getDeclaredField("mOnTouchListener")
            retrievedListener = field.get(view) as View.OnTouchListener
        } catch (ex: NoSuchFieldException) {
            Log.e("Reflection", "No Such Field.")
        } catch (ex: IllegalAccessException) {
            Log.e("Reflection", "Illegal Access.")
        } catch (ex: ClassNotFoundException) {
            Log.e("Reflection", "Class Not Found.")
        }

        return retrievedListener
    }

    //Used for new ListenerInfo class structure used beginning with API 14 (ICS)
    fun getOnTouchListenerV14(view: View): View.OnTouchListener? {
        var retrievedListener: View.OnTouchListener? = null
        val viewStr = "android.view.View"
        val lInfoStr = "android.view.View\$ListenerInfo"

        try {
            val listenerField = Class.forName(viewStr).getDeclaredField("mListenerInfo")
            var listenerInfo: Any? = null

            if (listenerField != null) {
                listenerField.isAccessible = true
                listenerInfo = listenerField.get(view)
            }

            val clickListenerField = Class.forName(lInfoStr).getDeclaredField("mOnTouchListener")

            if (clickListenerField != null && listenerInfo != null) {
                retrievedListener = clickListenerField.get(listenerInfo) as View.OnTouchListener
            }
        } catch (ex: NoSuchFieldException) {
            Log.e("Reflection", "No Such Field.")
        } catch (ex: IllegalAccessException) {
            Log.e("Reflection", "Illegal Access.")
        } catch (ex: ClassNotFoundException) {
            Log.e("Reflection", "Class Not Found.")
        }

        return retrievedListener
    }
}
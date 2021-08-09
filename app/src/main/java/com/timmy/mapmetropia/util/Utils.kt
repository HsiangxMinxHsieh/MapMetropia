package com.timmy.mapmetropia.util

import android.content.Context
import android.location.Location
import android.os.Build
import android.os.ParcelUuid
import android.provider.Settings.System.DATE_FORMAT
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.timmy.mapmetropia.BuildConfig
import java.io.*
import java.text.DecimalFormat
import java.text.Format
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


fun Int.meterToMilesOrFit(): String {
    return if (this > 643)// x.x 英里 // 題目有 0.4mi，解讀為「如果大於0.4mi要回傳英里，而」
        "${(this.toDouble() * 0.000621371192).format("#.#")} mi"
    else // x ft
        "${(this.toDouble()*3.2808399).format("#")} ft"
}

fun Double.format(format: String = "#.#"): String {
    return DecimalFormat(format).format(this)
}

fun Any.toJson() = Gson().toJson(this)

inline fun <reified T> String.toGson(param: T) = Gson().fromJson<T>(this, T::class.java) as T

fun getRaw(context: Context, id: Int): String {
    val stream: InputStream = context.resources.openRawResource(id)
    return read(stream)
}

fun read(stream: InputStream?): String {
    return read(stream, "utf-8")
}

fun read(`is`: InputStream?, encode: String?): String {
    if (`is` != null) {
        try {
            val reader = BufferedReader(InputStreamReader(`is`, encode))
            val sb = java.lang.StringBuilder()
            var line: String? = null
            while (reader.readLine().also { line = it } != null) {
                sb.append(
                    """
                            $line
                            
                            """.trimIndent()
                )
            }
            `is`.close()
            return sb.toString()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return ""
}

fun Location.latLng() = LatLng(this.latitude, this.longitude)

/**傳感器和圖片旋轉的角度系統不同，因此於此轉換
 * 傳感器角度：東：π/2，西：-π/2，南：π，北：0
 * 圖片角度：東：0，西：180，南：90，北：-90
 * */
fun Float.getPicRotationAngle() = ((this - Math.PI) * 180 / Math.PI).toFloat()

fun logi(tag: String, log: Any) {

    if (BuildConfig.DEBUG_MODE) Log.i(tag, log.toString())
    if (BuildConfig.LOG2FILE) {
        val current = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now().toString()
        } else {
            "TIME"
        }
    }
}

fun loge(tag: String, log: Any, tr: Throwable? = null) {

    if (BuildConfig.DEBUG_MODE && tr != null) Log.e(tag, log.toString(), tr)
    else if (BuildConfig.DEBUG_MODE) Log.e(tag, log.toString(), tr)

    if (BuildConfig.LOG2FILE) {
        val current = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now().toString()
        } else {
            "TIME"
        }
    }
}


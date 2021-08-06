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
import java.text.Format
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

fun Any.toJson() = Gson().toJson(this)

inline fun < reified T>String.toGson(param: T) = Gson().fromJson<T>(this,T::class.java) as T

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

fun UUID.short(): String {
    return this.toString().substring(4, 8)
}

fun String.getUUID(): UUID {
    return UUID.fromString("0000$this-0000-1000-8000-00805f9b34fb")
}

fun String.toParcelUUID(): ParcelUuid {
    return ParcelUuid.fromString("0000$this-0000-1000-8000-00805f9b34fb")
}

fun Date.todaylbl(): Int {
    val sdf: Format = SimpleDateFormat(DATE_FORMAT)
    return sdf.format(this).toInt()
}

fun Date.tomorrowlbl(): Int {
    val calendar = GregorianCalendar()
    calendar.time = this
    calendar.add(Calendar.DATE, 1)
    val sdf: Format = SimpleDateFormat(DATE_FORMAT)
    return sdf.format(calendar.time).toInt()
}

fun Date.thisHrlbl(): Int {
    val sdf: Format = SimpleDateFormat("hh")
    return sdf.format(this).toInt()
}

fun Date.thisMinOfThisHrlbl(): Int {
    val sdf: Format = SimpleDateFormat("mm")
    return sdf.format(this).toInt()
}


fun ByteArray.hex4EasyRead(): String {
    val sb = StringBuilder()
    for (b in this) sb.append(String.format("%02X ", b))
    return sb.toString()
}

fun ByteArray.toHexString(): String {
    val sb = StringBuilder()
    for (b in this) sb.append(String.format("%02X:", b))
    return sb.toString()
}

fun logi(tag: String, log: Any) {

    if (BuildConfig.DEBUG_MODE) Log.i(tag, log.toString())
    if (BuildConfig.LOG2FILE) {
        val current = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now().toString()
        } else {
            "TIME"
        }
//        appendLog("$current : $tag : $log ")
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
//        appendLog("$current : $tag : $log ")
    }
}

//fun appendLog(text: String) {
//    val directory = App.instance.ctx().externalCacheDir
//    val logFile = File(directory, "loge.file")
////    Log.e("AAAA", " path is $logFile")
//    if (!logFile.exists()) {
//        try {
//            logFile.createNewFile()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//
//    }
//    try {
//        //BufferedWriter for performance, true to set append to file flag
//        val buf = BufferedWriter(FileWriter(logFile, true))
//        buf.append(text)
//        buf.newLine()
//        buf.close()
//    } catch (e: IOException) {
//        e.printStackTrace()
//    }
//
//}

//private fun delay(h: Handler, sec: Float, lambda: () -> Unit) {
//    h.postDelayed({ lambda() }, (sec * 1000).toLong())
//}

fun parseScanRecord(scanRecord: ByteArray): Map<Int, ByteArray> {
    val dict = mutableMapOf<Int, ByteArray>()
    val rawData: ByteArray?
    var index = 0
    while (index < scanRecord.size) {
        val length = scanRecord[index++].toInt()
        //if no record
        if (length == 0) break
        //type
        val type = scanRecord[index].toInt()
        //if not valid type
//        print("UTILS", "[MANUFACTURE] type is $type")
//        print("UTILS", "[MANUFACTURE] length is $length")
        if (type == 0) break
        dict[type] = Arrays.copyOfRange(scanRecord, index + 1, index + length)
        //next
        index += length
    }

    return dict
}


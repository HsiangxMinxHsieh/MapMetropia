package com.timmy.mapmetropia.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun String.toDate(format: String): Date? {
    var milliseconds: Long = -1
    try {
        val f = SimpleDateFormat(format, Locale.getDefault())
        return f.parse(this)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return null
}

fun String.toDateNonNull(format: String, default: Date = Date(0)): Date {
    return toDate(format) ?: default
}

fun Long.toDate(): Date {
    return Date(this)
}

fun Long.toInteval(): Long {
    return (Date().time - this)
}

fun Date.toString(format: String, timeZone: TimeZone = TimeZone.getDefault()): String {
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    sdf.timeZone = timeZone
    return sdf.format(this)
}

fun Date.toYMDHMS(needShowMillsecond: Boolean = true, timeZone: TimeZone = TimeZone.getDefault()): String {
    return if (needShowMillsecond) this.toString("yyyy-MM-dd HH:mm:ss.SSS", timeZone) else this.toString("yyyy-MM-dd HH:mm:ss", timeZone)
}

fun Long.toYMDHMS(needShowMillsecond: Boolean = true, timeZone: TimeZone = TimeZone.getDefault()): String {
    return this.toDate().toYMDHMS(needShowMillsecond, timeZone)
}

fun Date.toYMD(): String {
    return this.toString("yyyyMMdd")
}

val Date.dateInt: Int
    get() = toString("yyyyMMdd").toInt()

val Date.calender: Calendar
    get() {
        val calender = Calendar.getInstance()
        calender.time = this
        return calender
    }

val Calendar.year: Int
    get() = get(Calendar.YEAR)

val Calendar.month: Int
    get() = get(Calendar.MONTH) + 1

val Calendar.dayOfMonth: Int
    get() = get(Calendar.DAY_OF_MONTH)

val Calendar.dayOfWeek: Int
    get() = get(Calendar.DAY_OF_WEEK) - 1

val Calendar.dayOfYear: Int
    get() = get(Calendar.DAY_OF_YEAR)

val Calendar.hour: Int
    get() = get(Calendar.HOUR)

val Calendar.hourOfDay: Int
    get() = get(Calendar.HOUR_OF_DAY)

val Calendar.minute: Int
    get() = get(Calendar.MINUTE)

val Calendar.second: Int
    get() = get(Calendar.SECOND)

val Calendar.millisecond: Int
    get() = get(Calendar.MILLISECOND)

fun Date.init(
    year: Int,
    month: Int,
    dayOfMonth: Int,
    hourOfDay: Int = 0,
    minute: Int = 0,
    second: Int = 0,
    millisecond: Int = 0
): Date {
    val calender = Calendar.getInstance()
    calender.set(Calendar.YEAR, year)
    calender.set(Calendar.MONTH, month)
    calender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
    calender.set(Calendar.HOUR_OF_DAY, hourOfDay)
    calender.set(Calendar.MINUTE, minute)
    calender.set(Calendar.SECOND, second)
    calender.set(Calendar.MILLISECOND, millisecond)
    return calender.time
}

/**
 * 取得年
 * @author 蝦米
 * @date 2020/05/15
 */
fun Date.getYearSerial(): Int {
    val calender = Calendar.getInstance()
    calender.time = this
    return calender.get(Calendar.YEAR)
}


/**
 * 取得月
 * @author 蝦米
 * @date 2020/05/15
 */
fun Date.getMonthSerial(): Int {
    val calender = Calendar.getInstance()
    calender.time = this
    return calender.get(Calendar.MONTH) + 1
}

/**
 * 藉由count取得一個月的開始時間
 * @author 蝦米
 * @date 2020/05/15
 * */
fun Date.getMonthStartByCount(count: Int): Date {
    return this.getFieldByCount(Calendar.MONTH, count).getMonthFirstDay().getStartOfDay()
}

/**
 * 藉由count取得一個月的結束時間
 * @author 蝦米
 * @date 2020/05/15
 * */
fun Date.getMonthEndByCount(count: Int): Date {
    return this.getFieldByCount(Calendar.MONTH, count).getMonthEndDay().getEndOfDay()
}

/**
 * 藉由field與count取得正確日期資料
 * @author wang
 * @date 2020/05/15
 * */
fun Date.getFieldByCount(field: Int, count: Int): Date {
    val calender = Calendar.getInstance()
    calender.time = Date()
    calender.add(field, count)
    return calender.time
}

/**
 * 取得一天的起始時間
 * @author wang
 * @date 2020/02/07
 * */
fun Date.getStartOfDay(): Date {
    val calender = Calendar.getInstance()
    calender.time = this
    calender.set(Calendar.HOUR_OF_DAY, 0)
    calender.set(Calendar.MINUTE, 0)
    calender.set(Calendar.SECOND, 0)
    calender.set(Calendar.MILLISECOND, 0)
    return calender.time
}

/**
 * 取得一天的結束時間
 * @author wang
 * @date 2020/02/07
 * */
fun Date.getEndOfDay(): Date {
    val calender = Calendar.getInstance()
    calender.time = this
    calender.set(Calendar.HOUR_OF_DAY, 23)
    calender.set(Calendar.MINUTE, 59)
    calender.set(Calendar.SECOND, 59)
    calender.set(Calendar.MILLISECOND, 999)
    return calender.time
}

/**
 * 取得一週的第一天
 * @author wang
 * @date 2019/03/04
 * @param week 一週第一天 預設星期天
 * @param calendarUse 日曆的部分由於在「每月第一天是一周的第一天」的情況中，會顯示在第一行，因此多了這個變數判斷是否是日曆使用
 *                    若是，且上述的情況發生，將會取前一周，以讓其顯示在第二行(為了美觀)
 * */
fun Date.getWeekFirstDay(week: Int = Calendar.SUNDAY, calendarUse: Boolean = false): Date {
    val calender = Calendar.getInstance()
    calender.time = this
    calender.set(Calendar.DAY_OF_WEEK, week)
    //蝦米修改：確保取得的時間一定小於傳入值
    return if (calendarUse && calender.time.time == this.time)
        Date(calender.time.time - DateTool.oneWeek)
    else
        calender.time
}

/**
 * 取得一週的最後一天
 * @author wang
 * @date 2019/03/04
 * @param week 一週最後一天 預設星期六
 * */
fun Date.getWeekEndDay(week: Int = Calendar.SATURDAY): Date {
    val calender = Calendar.getInstance()
    calender.time = this
    calender.set(Calendar.DAY_OF_WEEK, week)
    //蝦米修改：確保取得的時間一定大於傳入值
    return if (calender.time.time >= this.time)
        calender.time
    else
        Date(calender.time.time + DateTool.oneWeek)
}


/**
 * 取得星期
 * @author wang
 * @date 2016/3/1 下午2:22:39
 * @version
 */
fun Date.getDayOfWeek(): Int {
    val date = this
    val calender = Calendar.getInstance()
    calender.time = date
    return calender.get(Calendar.DAY_OF_WEEK) - 1
}

/**
 * 取得一個月的第一天
 * @author wang
 * @date 2019/03/04
 * */
fun Date.getMonthFirstDay(): Date {
    val calender = Calendar.getInstance()
    calender.set(Calendar.MONTH, 0)
    calender.time = this
    calender.set(Calendar.DAY_OF_MONTH, calender.getActualMinimum(Calendar.DAY_OF_MONTH))
    return calender.time
}

/**
 * 取得一個月的第一天
 * @author wang
 * @date 2019/03/04
 * @param year 取直年份
 * @param month 取直月份
 * */
fun Date.getMonthFirstDay(year: Int, month: Int): Date {
    val calender = Calendar.getInstance()
    calender.set(Calendar.YEAR, year)
    calender.set(Calendar.MONTH, month)
    calender.set(Calendar.DAY_OF_MONTH, calender.getActualMinimum(Calendar.DAY_OF_MONTH))
    return calender.time
}

/**
 * 取得一個月的最後一天
 * @author wang
 * @date 2019/03/04
 * */
fun Date.getMonthEndDay(): Date {
    val calender = Calendar.getInstance()
    calender.time = this
    calender.set(Calendar.DAY_OF_MONTH, calender.getActualMaximum(Calendar.DAY_OF_MONTH))
    return calender.time
}

/**
 * 取得一個月的第一天
 * @author wang
 * @date 2019/03/04
 * @param year 取直年份
 * @param month 取直月份
 * */
fun Date.getMonthEndDay(year: Int, month: Int): Date {
    val calender = Calendar.getInstance()
    calender.set(Calendar.YEAR, year)
    calender.set(Calendar.MONTH, month)
    calender.set(Calendar.DAY_OF_MONTH, calender.getActualMaximum(Calendar.DAY_OF_MONTH))
    return calender.time
}

/**
 * 取得一季的第一天
 * @author wang
 * @date 2019/03/04
 * */
fun Date.getSeasonFirstDay(): Date {
    val calender = Calendar.getInstance()
    calender.time = this
    val year = calender.get(Calendar.YEAR)
    val month = calender.get(Calendar.MONTH)
    val season = when (month) {
        in 0..2 -> 0
        in 3..5 -> 1
        in 6..8 -> 2
        in 9..11 -> 3
        else -> 0
    }
    return getMonthFirstDay(year, season * 3)
}

/**
 * 取得一季的最後一天
 * @author wang
 * @date 2019/03/04
 * @param milliseconds 取直時間
 * */
fun Date.getSeasonEndDay(): Date {
    val calender = Calendar.getInstance()
    calender.time = this
    val year = calender.get(Calendar.YEAR)
    val month = calender.get(Calendar.MONTH)
    val season = when (month) {
        in 0..2 -> 0
        in 3..5 -> 1
        in 6..8 -> 2
        in 9..11 -> 3
        else -> 0
    }
    return getMonthEndDay(year, season * 3 + 2)
}

/**
 * 取得一年的第一天
 * @author Hsieh
 * @date 2019/04/09
 * @param date 取值日期
 * */
fun Date.getYearFirstDay(): Date {
    val calender = Calendar.getInstance()
    calender.time = this
    calender.set(Calendar.YEAR, calender.get(Calendar.YEAR))
    calender.set(Calendar.MONTH, calender.getActualMinimum(Calendar.MONTH))
    calender.set(Calendar.DAY_OF_MONTH, calender.getActualMinimum(Calendar.DAY_OF_MONTH))
    calender.set(Calendar.HOUR_OF_DAY, calender.getActualMinimum(Calendar.HOUR_OF_DAY))
    calender.set(Calendar.MINUTE, calender.getActualMinimum(Calendar.MINUTE))
    calender.set(Calendar.SECOND, calender.getActualMinimum(Calendar.SECOND))
    calender.set(Calendar.MILLISECOND, calender.getActualMinimum(Calendar.MILLISECOND))
    return calender.time
}

/**
 * 取得一年的最後一天
 * @author Hsieh
 * @date 2019/04/09
 * @param date 取值日期
 * */
fun Date.getYearEndDay(date: Int): Date {
    val calender = Calendar.getInstance()
    calender.time = this
    calender.set(Calendar.YEAR, calender.get(Calendar.YEAR))
    calender.set(Calendar.MONTH, calender.getActualMaximum(Calendar.MONTH))
    calender.set(Calendar.DAY_OF_MONTH, calender.getActualMaximum(Calendar.DAY_OF_MONTH))
    calender.set(Calendar.HOUR_OF_DAY, calender.getActualMaximum(Calendar.HOUR_OF_DAY))
    calender.set(Calendar.MINUTE, calender.getActualMaximum(Calendar.MINUTE))
    calender.set(Calendar.SECOND, calender.getActualMaximum(Calendar.SECOND))
    calender.set(Calendar.MILLISECOND, calender.getActualMaximum(Calendar.MILLISECOND))
    return calender.time
}

fun Date.add(field: Int, amount: Int): Date {
    val calender = this.calender
    calender.add(field, amount)
    return calender.time
}

/**
 * 獲取當前時間的下個月的Long值(日曆部分取得每個月第一天的List使用)
 * @author Hsieh
 * @date  2019/05/02
 * @param time 想知道下個月的long值的時間
 */
fun Date.getNextMonth(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    calendar.add(Calendar.MONTH, 1)
    return calendar.time
}

/**
 * 獲取當前時間所在年的週數
 * (此專案中主要用於取超過本年份的週數，例如說12/31可能為隔年第1周，此方法要取為前年的第53周。)
 * @author Hsieh
 * @date  2019/04/10
 * @param date 想知道所在週數的時間
 *
 */
val Date.weekOfYear: Int
    get() {
        var indexdate = DateTool.getWeekEndDay(
            DateTool.getMilliseconds(
                DateTool.getYearFirstDay(
                    DateTool.getTime(
                        this.time,
                        "yyyyMMdd"
                    ).toInt()
                ).toString(),
                "yyyyMMdd"
            )
        )
        var week = 1
        while (indexdate < this.time) {
            indexdate += DateTool.oneWeek
            week += 1
        }
        return week
    }

/**
 * 取得傳入時間範圍的月份天數
 * @author Hsieh
 * @date 2020/05/27
 * @param startTime 取值開始時間
 * @param endTime 取值結束時間
 * */
fun Date.getDaysInRange(startTime: Long, endTime: Long): Int {

    val startDate = Date(startTime).getStartOfDay()
    val endDate = Date(endTime).getEndOfDay()
    return ((endDate.time + 1L - startDate.time) / DateTool.oneDay).toInt()
}

/**
 * 取得傳入時間範圍的月份週數
 * @author Hsieh
 * @date 2019/04/10
 * @param startTime 取值開始時間
 * @param endTime 取值結束時間
 * */
fun Date.getWeeksInRange(startTime: Long, endTime: Long): Int {

    val startDate = Date(startTime).getMonthFirstDay()
    val endDate = Date(endTime).getMonthEndDay()

    val startCalender = Calendar.getInstance()
    startCalender.time = startDate
    val endCalender = Calendar.getInstance()
    endCalender.time = endDate

    var endWeek = endCalender.get(Calendar.WEEK_OF_YEAR)
    val startWeek = startCalender.get(Calendar.WEEK_OF_YEAR)

    if (endWeek < startWeek) {//發生換年週數問題：結束週數比開始週數小，此處特殊處理：
        //先取得開始時間的年份的年總週數，並將endWeek設為總週數，以確保endWeek絕對大於startWeek。
        endWeek = DateTool.getWeekOfYear(endDate)
    }
    return endWeek - startWeek + 1
}

/**
 * 時區轉換
 * @author wang
 * @date 2020/02/10
 * @param timeZone 指定時區
 * */
fun Date.toTimeZone(timeZone: TimeZone): Date {
//    try{
//        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
//        val date = format.parse(this.toString("yyyy-MM-dd HH:mm:ss:SSS"))
//        format.timeZone = timeZone
//        return format.format(date).toDateNonNull("yyyy-MM-dd HH:mm:ss:SSS")
//    }catch (e: ParseException){
//        e.printStackTrace()
//    }
//    return Date()
    var date = Date(this.time + timeZone.rawOffset)
    if (timeZone.inDaylightTime(date)) {
        date = Date(date.time + timeZone.dstSavings)
    }
    return date
}

/**
 * 時區轉換
 * @author wang
 * @date 2020/02/10
 * @param frome 從指定時區
 * @param to 轉換到指定時區
 * */
fun Date.toTimeZone(frome: TimeZone, to: TimeZone): Date {
    return toTimeZone(frome).toTimeZone(to)
}

/**
 * 時區轉換成+0
 * @author wang
 * @date 2020/02/10
 * @param timeZone date的時區
 * */
fun Date.toGMT(timeZone: TimeZone = TimeZone.getDefault()): Date {
    var date = Date(this.time + timeZone.rawOffset)
    if (timeZone.inDaylightTime(date)) {
        date = Date(date.time + timeZone.dstSavings)
    }
    return date
}

/**
 * 時區轉換成+0
 * @author wang
 * @date 2020/02/10
 * @param timeZone date的時區
 * */
fun Date.toGMT(timeZoneId: String): Date {
    return toGMT(TimeZone.getTimeZone(timeZoneId))
}

/**
 * 時區轉換成當地時區
 * @author wang
 * @date 2020/02/10
 * @param timeZone 要轉的時區
 * */
fun Date.toLocal(timeZone: TimeZone = TimeZone.getDefault()): Date {
    return toTimeZone(timeZone)
}
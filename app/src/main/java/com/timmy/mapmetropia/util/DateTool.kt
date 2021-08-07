package com.timmy.mapmetropia.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 * @author Wang
 */
object DateTool {
    /** 一秒 */
    val oneSec = 1000L
    /** 一分 */
    val oneMin = oneSec * 60L
    /** 一小時 */
    val oneHour = oneMin * 60L
    /** 一天 */
    val oneDay = oneHour * 24L
    /** 一周 */
    val oneWeek = oneDay * 7L
    /** 一月 */
    val oneMonth = oneDay * 30L
    /** 一年 */
    val oneYear = oneMonth * 12L

    /**
     * 取得現在時間
     * @author wang
     * @date 2016/3/1 下午2:22:39
     * @version
     */
    val nowDate: Long
        get() = System.currentTimeMillis()

    /**
     * long 轉 date
     * @author wang
     * @date 2016/3/1 下午2:22:39
     * @version
     */
    fun LongToDate(milliseconds: Long): Date {
        return Date(milliseconds)
    }

    /**
     * date 轉 long
     * @author wang
     * @date 2016/3/1 下午2:22:39
     * @version
     */
    @JvmOverloads
    fun DateToLong(date: String, format: String = "yyyy/MM/dd HH:mm:ss"): Long {
        var milliseconds: Long = -1
        try {
            val f = SimpleDateFormat(format, Locale.getDefault())
            val d = f.parse(date)
            milliseconds = d.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return milliseconds
    }


    /**
     * 取得時間 依照所給格式
     * @author wang
     * @date 2016/3/1 下午2:22:39
     * @version
     */
    fun getMilliseconds(dateString: String, template: String): Long {
        var milliseconds: Long = 0
        val format = SimpleDateFormat(template, Locale.getDefault())
        try {
            val date = format.parse(dateString)
            milliseconds = date.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return milliseconds
    }

    /**
     * 取得時間 依照所給格式
     * @author wang
     * @date 2016/3/1 下午2:22:39
     * @version
     */
    @JvmOverloads
    fun getTime(milliseconds: Long, template: String = "a hh:mm"): String {
        val date = Date(milliseconds)
        val calender = Calendar.getInstance()
        calender.time = date
        val sdf = SimpleDateFormat(template, Locale.getDefault())
        return sdf.format(date)
    }

    /**
     * 獲取當前時間的下個月的Long值(日曆部分取得每個月第一天的List使用)
     * @author Hsieh
     * @date  2019/05/02
     * @param time 想知道下個月的long值的時間
     */
    fun getNextMonth(time: Long): Long {
        val date = Date(time)
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.add(Calendar.MONTH, 1)
        return calendar.time.time
    }

    /**
     * 獲取當前時間所在年的週數
     * (此專案中主要用於取超過本年份的週數，例如說12/31可能為隔年第1周，此方法要取為前年的第53周。)
     * @author Hsieh
     * @date  2019/04/10
     * @param date 想知道所在週數的時間
     *
     */
    fun getWeekOfYear(date: Date): Int {
        //國銘算法：
        var indexdate = getWeekEndDay(
            getMilliseconds(
                getYearFirstDay(getTime(date.time, "yyyyMMdd").toInt()).toString(),
                "yyyyMMdd"
            )
        )
        var week = 1
        while (indexdate < date.time) {
            indexdate += oneWeek
            week += 1
        }
        return week

        //舊算法：
//        val c = GregorianCalendar()
//        c.firstDayOfWeek = Calendar.SUNDAY
//        c.minimalDaysInFirstWeek = 7
//        c.time = date
//        return c.get(Calendar.WEEK_OF_YEAR)
    }

    /**
     * 取得傳入時間範圍的月份週數
     * @author Hsieh
     * @date 2019/04/10
     * @param startTime 取值開始時間
     * @param endTime 取值結束時間
     * */
    fun getWeeksInRange(startTime: Long, endTime: Long): Int {

        val startDate = Date(getMonthFirstDay(startTime))
        val endDate = Date(getMonthEndDay(endTime))

        val startCalender = Calendar.getInstance()
        startCalender.time = startDate
        val endCalender = Calendar.getInstance()
        endCalender.time = endDate

        var endWeek = endCalender.get(Calendar.WEEK_OF_YEAR)
        val startWeek = startCalender.get(Calendar.WEEK_OF_YEAR)

        if (endWeek < startWeek) {//發生換年週數問題：結束週數比開始週數小，此處特殊處理：
            //先取得開始時間的年份的年總週數，並將endWeek設為總週數，以確保endWeek絕對大於startWeek。
            endWeek = getWeekOfYear(endDate)
        }
        return endWeek - startWeek + 1
    }

    /**
     * 取得傳入時間的月份週數
     * @author Hsieh
     * @date 2019/04/10
     * @param milliseconds 取值時間
     * */
    fun getWeeks(milliseconds: Long): Int {

        val startDate = Date(getMonthFirstDay(milliseconds))
        val endDate = Date(getMonthEndDay(milliseconds))

        val startCalender = Calendar.getInstance()
        startCalender.time = startDate
        val endCalender = Calendar.getInstance()

        endCalender.time = endDate
        var endWeek = endCalender.get(Calendar.WEEK_OF_YEAR)
        val startWeek = startCalender.get(Calendar.WEEK_OF_YEAR)
        if (endWeek < startWeek) {//發生換年週數問題：結束週數比開始週數小，此處特殊處理：
            //先取得開始時間的年份的年總週數，並將endWeek設為總週數，以確保endWeek絕對大於startWeek。
            endWeek = getWeekOfYear(endDate)
        }
        return endWeek - startWeek + 1
    }

    /**
     * 取得一天的起始時間
     * @author wang
     * @date 2020/02/07
     * */
    fun getStartOfDay(milliseconds: Long): Long {
        val date = Date(milliseconds)
        val calender = Calendar.getInstance()
        calender.time = date
        calender.set(Calendar.HOUR_OF_DAY, 0)
        calender.set(Calendar.MINUTE, 0)
        calender.set(Calendar.SECOND, 0)
        calender.set(Calendar.MILLISECOND, 0)
        return calender.time.time
    }

    /**
     * 取得一天的起始時間
     * @author wang
     * @date 2020/02/07
     * */
    fun getStartOfDay(year: Int, month: Int, dayOfMonth: Int): Long {
        val calender = Calendar.getInstance()
        calender.set(Calendar.YEAR, year)
        calender.set(Calendar.MONTH, month)
        calender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        calender.set(Calendar.HOUR_OF_DAY, 0)
        calender.set(Calendar.MINUTE, 0)
        calender.set(Calendar.SECOND, 0)
        calender.set(Calendar.MILLISECOND, 0)
        return calender.time.time
    }

    /**
     * 取得一天的結束時間
     * @author wang
     * @date 2020/02/07
     * */
    fun getEndOfDay(milliseconds: Long): Long {
        val date = Date(milliseconds)
        val calender = Calendar.getInstance()
        calender.time = date
        calender.set(Calendar.HOUR_OF_DAY, 23)
        calender.set(Calendar.MINUTE, 59)
        calender.set(Calendar.SECOND, 59)
        calender.set(Calendar.MILLISECOND, 999)
        return calender.time.time
    }

    /**
     * 取得一天的結束時間
     * @author wang
     * @date 2020/02/07
     * */
    fun getEndOfDay(year: Int, month: Int, dayOfMonth: Int): Long {
        val calender = Calendar.getInstance()
        calender.set(Calendar.YEAR, year)
        calender.set(Calendar.MONTH, month)
        calender.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        calender.set(Calendar.HOUR_OF_DAY, 23)
        calender.set(Calendar.MINUTE, 59)
        calender.set(Calendar.SECOND, 59)
        calender.set(Calendar.MILLISECOND, 999)
        return calender.time.time
    }

    /**
     * 取得一週的第一天
     * @author wang
     * @date 2019/03/04
     * @param milliseconds 取直時間
     * @param week 一週第一天 預設星期天
     * @param calendarUse 日曆的部分由於在「每月第一天是一周的第一天」的情況中，會顯示在第一行，因此多了這個變數判斷是否是日曆使用
     *                    若是，且上述的情況發生，將會取前一周，以讓其顯示在第二行(為了美觀)
     * */
    fun getWeekFirstDay(milliseconds: Long, week: Int = Calendar.SUNDAY, calendarUse: Boolean = false): Long {
        val date = Date(milliseconds)
        val calender = Calendar.getInstance()
        calender.time = date
        calender.set(Calendar.DAY_OF_WEEK, week)
        //蝦米修改：確保取得的時間一定小於傳入值
        return if (calendarUse && calender.time.time == milliseconds)
            calender.time.time - oneWeek
        else
            calender.time.time
    }

    /**
     * 取得一週的最後一天
     * @author wang
     * @date 2019/03/04
     * @param milliseconds 取直時間
     * @param week 一週最後一天 預設星期六
     * */
    fun getWeekEndDay(milliseconds: Long, week: Int = Calendar.SATURDAY): Long {
        val date = Date(milliseconds)
        val calender = Calendar.getInstance()
        calender.time = date
        calender.set(Calendar.DAY_OF_WEEK, week)
        //蝦米修改：確保取得的時間一定大於傳入值
        return if (calender.time.time >= milliseconds)
            calender.time.time
        else
            calender.time.time + oneWeek
    }

    /**
     * 取得一個月的第一天
     * @author wang
     * @date 2019/03/04
     * @param milliseconds 取直時間
     * */
    fun getMonthFirstDay(milliseconds: Long): Long {
        val date = Date(milliseconds)
        val calender = Calendar.getInstance()
        calender.set(Calendar.MONTH, 0)
        calender.time = date
        calender.set(Calendar.DAY_OF_MONTH, calender.getActualMinimum(Calendar.DAY_OF_MONTH))
        return calender.time.time
    }

    /**
     * 取得一個月的第一天
     * @author wang
     * @date 2019/03/04
     * @param year 取直年份
     * @param month 取直月份
     * */
    fun getMonthFirstDay(year: Int, month: Int): Long {
        val calender = Calendar.getInstance()
        calender.set(Calendar.YEAR, year)
        calender.set(Calendar.MONTH, month)
        calender.set(Calendar.DAY_OF_MONTH, calender.getActualMinimum(Calendar.DAY_OF_MONTH))
        return calender.time.time
    }

    /**
     * 取得一個月的最後一天
     * @author wang
     * @date 2019/03/04
     * @param milliseconds 取直時間
     * */
    fun getMonthEndDay(milliseconds: Long): Long {
        val date = Date(milliseconds)
        val calender = Calendar.getInstance()
        calender.time = date
        calender.set(Calendar.DAY_OF_MONTH, calender.getActualMaximum(Calendar.DAY_OF_MONTH))
        return calender.time.time
    }

    /**
     * 取得一個月的第一天
     * @author wang
     * @date 2019/03/04
     * @param year 取直年份
     * @param month 取直月份
     * */
    fun getMonthEndDay(year: Int, month: Int): Long {
        val calender = Calendar.getInstance()
        calender.set(Calendar.YEAR, year)
        calender.set(Calendar.MONTH, month)
        calender.set(Calendar.DAY_OF_MONTH, calender.getActualMaximum(Calendar.DAY_OF_MONTH))
        return calender.time.time
    }

    /**
     * 取得一季的第一天
     * @author wang
     * @date 2019/03/04
     * @param milliseconds 取直時間
     * */
    fun getSeasonFirstDay(milliseconds: Long): Long {
        val date = Date(milliseconds)
        val calender = Calendar.getInstance()
        calender.time = date
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
    fun getSeasonEndDay(milliseconds: Long): Long {
        val date = Date(milliseconds)
        val calender = Calendar.getInstance()
        calender.time = date
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
    fun getYearFirstDay(date: Int): Int {
        return date / 10000 * 10000 + 101
    }

    /**
     * 取得一年的最後一天
     * @author Hsieh
     * @date 2019/04/09
     * @param date 取值日期
     * */
    fun getYearEndDay(date: Int): Int {
        return date / 10000 * 10000 + 1231
    }

    /**
     * 取得一年的第一天
     * @author Hsieh
     * @date 2019/04/09
     * @param time 取值時間
     * */
    fun getYearFirstDay(time: Long): Long {
        return getMilliseconds(getYearFirstDay(getTime(time,"yyyyMMdd").toInt()).toString(),"yyyyMMdd")
    }

    /**
     * 取得一年的最後一天
     * @author Hsieh
     * @date 2019/04/09
     * @param time 取值時間
     * */
    fun getYearEndDay(time: Long): Long {
        return getMilliseconds(getYearEndDay(getTime(time,"yyyyMMdd").toInt()).toString(),"yyyyMMdd")
    }
    /**
     * 取得年
     * @author wang
     * @date 2016/3/1 下午2:22:39
     * @version
     */
    fun getYear(milliseconds: Long): Int {
        val date = Date(milliseconds)
        val calender = Calendar.getInstance()
        calender.time = date
        return calender.get(Calendar.YEAR)
    }

    /**
     * 取得月
     * @author wang
     * @date 2016/3/1 下午2:22:39
     * @version
     */
    fun getMonth(milliseconds: Long): Int {
        val date = Date(milliseconds)
        val calender = Calendar.getInstance()
        calender.time = date
        return calender.get(Calendar.MONTH) + 1
    }

    /**
     * 取得日(一個月中的第幾天)
     * @author wang
     * @date 2016/3/1 下午2:22:39
     * @version
     */
    fun getDayOfMonth(milliseconds: Long): Int {
        val date = Date(milliseconds)
        val calender = Calendar.getInstance()
        calender.time = date
        return calender.get(Calendar.DAY_OF_MONTH)
    }

    /**
     * 取得星期
     * @author wang
     * @date 2016/3/1 下午2:22:39
     * @version
     */
    fun getDayOfWeek(milliseconds: Long): Int {
        val date = Date(milliseconds)
        val calender = Calendar.getInstance()
        calender.time = date
        return calender.get(Calendar.DAY_OF_WEEK) - 1
    }

    /**
     * 取得日(一年中的第幾天)
     * @author wang
     * @date 2016/3/1 下午2:22:39
     * @version
     */
    fun getDayOfYear(milliseconds: Long): Int {
        val date = Date(milliseconds)
        val calender = Calendar.getInstance()
        calender.time = date
        return calender.get(Calendar.DAY_OF_YEAR)
    }

    /**
     * 取得小時 (24小時制)
     * @author wang
     * @date 2016/3/1 下午2:22:39
     * @version
     */
    fun getHour(milliseconds: Long): Int {
        return Integer.parseInt(getTime(milliseconds, "HH"))
    }

    /**
     * 取得分
     * @author wang
     * @date 2016/3/1 下午2:22:39
     * @version
     */
    fun getMinute(milliseconds: Long): Int {
        return Integer.parseInt(getTime(milliseconds, "mm"))
    }

    /**
     * 取得秒
     * @author wang
     * @date 2016/3/1 下午2:22:39
     * @version
     */
    fun getSec(milliseconds: Long): Int {
        return Integer.parseInt(getTime(milliseconds, "ss"))
    }

    /**
     * local time to GMT time
     * 輸入格式 : "yyyy-MM-dd HH:mm:ss" || "yyyy-MM-dd"
     */
    fun LocalTimeToGmtTime(time: String): String? {
        // local time to GMT time
        val s = time.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var d: String? = null
        var t: String? = null
        if (s.size > 1) {
            d = s[0]
            t = s[1]
        }
        return if (d != null && t != null) {
            string2TimezoneDefault("$d $t", TimeZone.getDefault().id, "GMT")
        } else string2TimezoneDefault(time, TimeZone.getDefault().id, "GMT")
    }

    /**
     * GMT time to local time
     * 輸入格式 : "yyyy-MM-dd HH:mm:ss" || "yyyy-MM-dd"
     */
    fun GmtTimeToLocalTime(time: String): String? {
        // GMT time to local time
        val s = time.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var d: String? = null
        var t: String? = null
        if (s.size > 1) {
            d = s[0]
            t = s[1]
        }
        return if (d != null && t != null) {
            string2TimezoneDefault("$d $t", "GMT", TimeZone.getDefault().id)
        } else string2TimezoneDefault(time, "GMT", TimeZone.getDefault().id)
    }

    /**
     * GMT time to local time
     * 輸入格式 : Long
     */
    fun toGMT(time: Long): Long{
        val timeZone =  TimeZone.getDefault()
        var date = Date(time - timeZone.rawOffset)
        if (timeZone.inDaylightTime(date)) {
            date = Date( date.time - timeZone.dstSavings)
        }
        return date.time
    }

    /**
     * GMT time to local time
     * 輸入格式 : Long
     */
    fun toLocal(time: Long, timeZone: TimeZone): Long{
        var date = Date(time + timeZone.rawOffset)
        if (timeZone.inDaylightTime(date)) {
            date = Date( date.time + timeZone.dstSavings)
        }
        return date.time
    }

    //	/**
    //	 * local date to GMT date
    //	 * 輸入格式 : "yyyy-MM-dd"
    //	 */
    //	public static String LocalDateToGmtDate(String time){
    //		// local time to GMT time
    //		return string2TimezoneDefault(time , TimeZone.getDefault().getID() , "GMT");
    //	}
    //
    //	/**
    //	 * GMT date to local date
    //	 * 輸入格式 : "yyyy-MM-dd"
    //	 */
    //	public static String GmtDateToLocalDate(String time){
    //		// GMT time to local time
    //		return string2TimezoneDefault(time , "GMT" , TimeZone.getDefault().getID());
    //	}

    /**
     * 对日期(时间)中的日进行加减计算. <br></br>
     * 例子: <br></br>
     * 如果Date类型的d为 2005年8月20日,那么 <br></br>
     * calculateByDate(d,-10)的值为2005年8月10日 <br></br>
     * 而calculateByDate(d,+10)的值为2005年8月30日 <br></br>
     *
     * @param d
     * 日期(时间).
     * @param amount
     * 加减计算的幅度.+n=加n天;-n=减n天.
     * @return 计算后的日期(时间).
     */
    fun calculateByDate(d: Date, amount: Int): Date? {
        return calculate(d, GregorianCalendar.DATE, amount)
    }

    /**
     * 对日期(时间)中的日进行加减计算. <br></br>
     * 例子: <br></br>
     * 如果Date类型的d为 2005年8月20日,那么 <br></br>
     * calculateByDate(d,-10)的值为2005年8月10日 <br></br>
     * 而calculateByDate(d,+10)的值为2005年8月30日 <br></br>
     *
     * @param d
     * 日期(时间).
     * @param amount
     * 加减计算的幅度.+n=加n月;-n=减n月.
     * @return 计算后的日期(时间).
     */
    fun calculateByMinute(d: Date, amount: Int): Date? {
        return calculate(d, GregorianCalendar.MINUTE, amount)
    }

    /**
     * 对日期(时间)中的日进行加减计算. <br></br>
     * 例子: <br></br>
     * 如果Date类型的d为 2005年8月20日,那么 <br></br>
     * calculateByDate(d,-10)的值为2005年8月10日 <br></br>
     * 而calculateByDate(d,+10)的值为2005年8月30日 <br></br>
     *
     * @param d
     * 日期(时间).
     * @param amount
     * 加减计算的幅度.+n=加n年;-n=减n年.
     * @return 计算后的日期(时间).
     */
    fun calculateByYear(d: Date, amount: Int): Date? {
        return calculate(d, GregorianCalendar.YEAR, amount)
    }

    /**
     * 对日期(时间)中由field参数指定的日期成员进行加减计算. <br></br>
     * 例子: <br></br>
     * 如果Date类型的d为 2005年8月20日,那么 <br></br>
     * calculate(d,GregorianCalendar.YEAR,-10)的值为1995年8月20日 <br></br>
     * 而calculate(d,GregorianCalendar.YEAR,+10)的值为2015年8月20日 <br></br>
     *
     * @param d
     * 日期(时间).
     * @param field
     * 日期成员. <br></br>
     * 日期成员主要有: <br></br>
     * 年:GregorianCalendar.YEAR <br></br>
     * 月:GregorianCalendar.MONTH <br></br>
     * 日:GregorianCalendar.DATE <br></br>
     * 时:GregorianCalendar.HOUR <br></br>
     * 分:GregorianCalendar.MINUTE <br></br>
     * 秒:GregorianCalendar.SECOND <br></br>
     * 毫秒:GregorianCalendar.MILLISECOND <br></br>
     * @param amount
     * 加减计算的幅度.+n=加n个由参数field指定的日期成员值;-n=减n个由参数field代表的日期成员值.
     * @return 计算后的日期(时间).
     */
    private fun calculate(d: Date?, field: Int, amount: Int): Date? {
        if (d == null)
            return null
        val g = GregorianCalendar()
        g.gregorianChange = d
        g.add(field, amount)
        return g.time
    }

    /**
     * 日期(时间)转化为字符串.
     *
     * @param formater
     * 日期或时间的格式.
     * @param aDate
     * java.util.Date类的实例.
     * @return 日期转化后的字符串.
     */
    @JvmOverloads
    fun date2String(formater: String?, aDate: Date? = Date()): String? {
        if (formater == null || "" == formater)
            return null
        return if (aDate == null) null else SimpleDateFormat(formater, Locale.getDefault()).format(aDate)
    }

    /**
     * 获取当前日期对应的星期数.
     * <br></br>1=星期天,2=星期一,3=星期二,4=星期三,5=星期四,6=星期五,7=星期六
     * @return 当前日期对应的星期数
     */
    fun dayOfWeek(): Int {
        var g: GregorianCalendar? = GregorianCalendar()
        val ret = g!!.get(Calendar.DAY_OF_WEEK)
        g = null
        return ret
    }


    /**
     * 获取所有的时区编号. <br></br>
     * 排序规则:按照ASCII字符的正序进行排序. <br></br>
     * 排序时候忽略字符大小写.
     *
     * @return 所有的时区编号(时区编号已经按照字符[忽略大小写]排序).
     */
    fun fecthAllTimeZoneIds(): Array<String> {
        var v: Vector<String>? = Vector()
        val ids = TimeZone.getAvailableIDs()
        for (i in ids.indices) {
            v!!.add(ids[i])
        }
        java.util.Collections.sort(v, String.CASE_INSENSITIVE_ORDER)
        v!!.copyInto(ids)
        v = null
        return ids
    }

    //    /**
    //     * 测试的main方法.
    //     *
    //     * @param argc
    //     */
    //    public static void main(String[] argc) {
    //
    //        String[] ids = fecthAllTimeZoneIds();
    //        String nowDateTime =date2String("yyyy-MM-dd HH:mm:ss");
    //        System.out.println("The time Asia/Shanhai is " + nowDateTime);//程序本地运行所在时区为[Asia/Shanhai]
    //        //显示世界每个时区当前的实际时间
    //        for(int i=0;i <ids.length;i++){
    //            System.out.println(" * " + ids[i] + "=" + string2TimezoneDefault(nowDateTime,ids[i]));
    //        }
    //        //显示程序运行所在地的时区
    //        System.out.println("TimeZone.getDefault().getID()=" +TimeZone.getDefault().getID());
    //    }

    /**
     * 将日期时间字符串根据转换为指定时区的日期时间.
     *
     * @param srcFormater
     * 待转化的日期时间的格式.
     * @param srcDateTime
     * 待转化的日期时间.
     * @param strTimeZoneId
     * 待转化的时区编号.
     * @param dstFormater
     * 目标的日期时间的格式.
     * @param dstTimeZoneId
     * 目标的时区编号.
     *
     * @return 转化后的日期时间.
     */
    fun string2Timezone(
        srcFormater: String?,
        srcDateTime: String?,
        strTimeZoneId: String,
        dstFormater: String?,
        dstTimeZoneId: String?
    ): String? {
        if (srcFormater == null || "" == srcFormater)
            return null
        if (srcDateTime == null || "" == srcDateTime)
            return null
        if (dstFormater == null || "" == dstFormater)
            return null
        if (dstTimeZoneId == null || "" == dstTimeZoneId)
            return null
        var sdf: SimpleDateFormat? = SimpleDateFormat(srcFormater, Locale.getDefault())
        sdf!!.timeZone = TimeZone.getTimeZone(strTimeZoneId)
        try {
            val diffTime = getDiffTimeZoneRawOffset(dstTimeZoneId)
            var d = sdf.parse(srcDateTime)
            val nowTime = d.time
            val newNowTime = nowTime - diffTime
            d = Date(newNowTime)
            return date2String(dstFormater, d)
        } catch (e: ParseException) {
            e.printStackTrace()
            return null
        } finally {
            sdf = null
        }
    }

    /**
     * 获取系统当前默认时区与指定时区的时间差.(单位:毫秒)
     *
     * @param timeZoneId
     * 时区Id
     * @return 系统当前默认时区与指定时区的时间差.(单位:毫秒)
     */
    private fun getDiffTimeZoneRawOffset(timeZoneId: String): Int {
        return TimeZone.getDefault().rawOffset - TimeZone.getTimeZone(timeZoneId).rawOffset
    }

    /**
     * 将日期时间字符串根据转换为指定时区的日期时间.
     *
     * @param srcDateTime
     * 待转化的日期时间.
     * @param strTimeZoneId
     * 待转化的时区编号.
     * @param dstTimeZoneId
     * 目标的时区编号.
     *
     * @return 转化后的日期时间.
     * @see .string2Timezone
     */
    fun string2TimezoneDefault(srcDateTime: String, strTimeZoneId: String, dstTimeZoneId: String): String? {
        var srcDateTime = srcDateTime
        srcDateTime = srcDateTime.trim { it <= ' ' }
        var date: String? = null//年月日
        var time: String? = null//時分秒

        var yyyy: String? = null//年
        var MM: String? = null//月
        var dd: String? = null//日
        var HH: String? = null//時
        var mm: String? = null//分
        var ss: String? = null//秒

        var format = ""

        if (srcDateTime.indexOf(" ") > -1) {
            val s = srcDateTime.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (s.size > 1) {
                date = s[0]
                time = s[1]
            }
        } else {
            if (srcDateTime.indexOf("-") > -1) {
                date = srcDateTime
            }
            if (srcDateTime.indexOf(":") > -1) {
                time = srcDateTime
            }
        }
        if (date != null) {
            try {
                val s = date.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                yyyy = s[0]
                MM = s[1]
                dd = s[2]
            } catch (e: Exception) {

            }

            if (yyyy != null) {
                format += "yyyy"
            }
            if (MM != null) {
                if (format.length > 0)
                    format += "-MM"
                else
                    format += "MM"
            }
            if (dd != null) {
                if (format.length > 0)
                    format += "-dd"
                else
                    format += "dd"
            }
        }
        if (time != null) {
            try {
                val s = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                HH = s[0]
                mm = s[1]
                ss = s[2]
            } catch (e: Exception) {

            }

            if (format.length > 0) {
                format += " "
            }
            if (HH != null) {
                format += "HH"
            }
            if (mm != null) {
                format += ":mm"
            }
            if (ss != null) {
                format += ":ss"
            }
        }
        return string2Timezone(format, srcDateTime, strTimeZoneId, format, dstTimeZoneId)
    }

    /**取得今天日期的Integer*/
    fun getYMDate(time: Long) = getTime(time, "yyyyMMdd").toInt()

    /**取得今天的詳細日期*/
    fun getFullTime(time: Long) = getTime(time, "yyyy/MM/dd HH:mm:ss")

    /**取一天整數的方法，用於將傳入值的小時及分鐘數去掉。*/
    fun getMillSecondAtZeroOclock(time: Long) =
        getMilliseconds(getTime(time, "yyyyMMdd"), "yyyyMMdd")//作法為將其轉為日期格式再轉回毫秒。

}

package com.ubt.alpha1e.edu.ui.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimeHelper {
	  private static final String DATETIME = "yyyy-MM-dd HH:mm:ss";
	    private static final String DATE = "yyyy-MM-dd";
	    private static final String TIME = "HH:mm:dd";
	    private static final String YEAR = "yyyy";
	    private static final String MONTH = "MM";
	    private static final String DAY = "dd";
	    private static final String HOUR = "HH";
	    private static final String MINUTE = "mm";
	    private static final String SEC = "ss";
	    private static final String DATETIMECHINESE = "yyyy年MM月dd日 HH时mm分ss秒";
	    private static final String DATECHINESE = "yyyy年MM月dd日";
	    private static final String SIMPLEDATECHINESE = "MM月dd日";
	    /**
	     * 判断一个字符串日期是否过期
	     *
	     * @param dateTime
	     * @return (int)&nbsp;过期返回1，不过期返回0
	     * @throws ParseException
	     */
	    public static int isOutOfDate(String dateTime) throws ParseException {
	        long nowTimeLong = new Date().getTime();
	        long ckTimeLong = new SimpleDateFormat(DATETIME).parse(dateTime)
	                .getTime();
	        if (nowTimeLong - ckTimeLong > 0) {// 过期
	            return 1;
	        }
	        return 0;
	    }
	    /**
	     * 判断是否在一个起止日期内<br/>
	     * 例如:2012-04-05 00:00:00~2012-04-15 00:00:00
	     *
	     * @param start_time
	     * @param over_time
	     * @return (int)&nbsp;在这个时间段内返回1，不在返回0
	     * @throws ParseException
	     */
	    public static boolean isOutOfDate(String start_time, String over_time)
	            throws ParseException {
	        long nowTimeLong = new Date().getTime();
	        long ckStartTimeLong = new SimpleDateFormat(DATETIME).parse(start_time)
	                .getTime();
	        long ckOverTimeLong = new SimpleDateFormat(DATETIME).parse(over_time)
	                .getTime();
	        if (nowTimeLong > ckStartTimeLong && nowTimeLong < ckOverTimeLong) {
	            return true;
	        }
	        return false;
	    }
	    /**
	     * 判断一个自定义日期是否在一个起止日期内<br/>
	     * 例如:判断2012-01-05 00:00:00是否在2012-04-05 00:00:00~2012-04-15 00:00:00
	     *
	     * @param start_time
	     * @param over_time
	     * @return (int)&nbsp;在这个时间段内返回1，不在返回0
	     * @throws ParseException
	     */
	    public static int isOutOfDate(String time, String start_time,
	            String over_time) throws ParseException {
	        long timeLong = new SimpleDateFormat(DATETIME).parse(time).getTime();
	        long ckStartTimeLong = new SimpleDateFormat(DATETIME).parse(start_time)
	                .getTime();
	        long ckOverTimeLong = new SimpleDateFormat(DATETIME).parse(over_time)
	                .getTime();
	        if (timeLong > ckStartTimeLong && timeLong < ckOverTimeLong) {
	            return 1;
	        }
	        return 0;
	    }
	    /**
	     * 判断是否在一个时间段内<br/>
	     * 例如:8:00~10:00
	     *
	     * @param time_limit_start
	     * @param time_limit_over
	     * @return (int) 1在这个时间段内，0不在这个时间段内
	     * @throws ParseException
	     */
	    public static boolean isInTime(String time_limit_start, String time_limit_over)
	            throws ParseException {
	        // 获取当前日期
	        String nowDate = new SimpleDateFormat(DATE).format(new Date());
	        return isOutOfDate(nowDate + " " + time_limit_start, nowDate + " "
	                + time_limit_over);
	    }
	    /**
	     * 判断一个自定义时间是否在一个时间段内<br/>
	     * 例如:判断02:00是否在08:00~10:00时间段内
	     *
	     * @param time_limit_start
	     * @param time_limit_over
	     * @return (int) 1在这个时间段内，0不在这个时间段内
	     * @throws ParseException
	     */
	    public static int isInTime(String time, String time_limit_start,
	            String time_limit_over) throws ParseException {
	        String nowDate = new SimpleDateFormat(DATE).format(new Date());
	        return isOutOfDate(nowDate + " " + time, nowDate + " "
	                + time_limit_start, nowDate + " " + time_limit_over);
	    }
	    /**
	     * 取得自定义月份后的日期，如13个月以后的时间
	     *
	     * @param monthNum
	     *            往后几个月
	     * @return 时间字符串
	     */
	    public static String crateTimeFromNowTimeByMonth(int monthNum) {
	        Calendar calendar = new GregorianCalendar(Integer.parseInt(getYear()),
	                Integer.parseInt(getMonth()) - 1, Integer.parseInt(getDay()),
	                Integer.parseInt(getHour()), Integer.parseInt(getMinute()),
	                Integer.parseInt(getSec()));
	        calendar.add(Calendar.MONTH, monthNum);
	        return new SimpleDateFormat(DATETIME).format(calendar.getTime());
	    }
	    /**
	     * 取得自定义天数后的日期，如13天以后的时间
	     *
	     * @param dayNum
	     *            往后几天
	     * @return 时间字符串(DateTime)
	     */
	    public static String crateTimeFromNowTimeByDay(int dayNum) {
	        Calendar calendar = new GregorianCalendar(Integer.parseInt(getYear()),
	                Integer.parseInt(getMonth()) - 1, Integer.parseInt(getDay()),
	                Integer.parseInt(getHour()), Integer.parseInt(getMinute()),
	                Integer.parseInt(getSec()));
	        calendar.add(Calendar.DATE, dayNum);
	        return new SimpleDateFormat(DATETIME).format(calendar.getTime());
	    }
	                              
	    /**
	     * 取得自定义天数后的日期，如13天以后的时间
	     *
	     * @param dayNum
	     *            往后几天
	     * @return 时间字符串(Date)
	     */
	    public static String crateTimeFromNowDayByDay(int dayNum) {
	        Calendar calendar = new GregorianCalendar(Integer.parseInt(getYear()),
	                Integer.parseInt(getMonth()) - 1, Integer.parseInt(getDay()),
	                Integer.parseInt(getHour()), Integer.parseInt(getMinute()),
	                Integer.parseInt(getSec()));
	        calendar.add(Calendar.DATE, dayNum);
	        return new SimpleDateFormat(DATE).format(calendar.getTime());
	    }
	    /**
	     * 取得自定义时间后再过几分钟的时间，如12:05以后5分钟的时间
	     *
	     * @param dayNum
	     *            往后几天
	     * @return 时间字符串(Date)
	     */
	    public static String crateTimeFromNowDayByTime(int timeNum) {
	        Calendar calendar = new GregorianCalendar(Integer.parseInt(getYear()),
	                Integer.parseInt(getMonth()) - 1, Integer.parseInt(getDay()),
	                Integer.parseInt(getHour()), Integer.parseInt(getMinute()),
	                Integer.parseInt(getSec()));
	        calendar.add(Calendar.MINUTE, timeNum);
	        return new SimpleDateFormat(DATETIME).format(calendar.getTime());
	    }
	    /**
	     * 计算两个时间间隔(精确到分钟)
	     *
	     * @param startDay
	     *            开始日(整型):0表示当日，1表示明日
	     * @param startTime
	     *            开始时间(24h):00:00
	     * @param endDay
	     *            结束日(整型):0表示当日，1表示明日，限制：大于等于 startDay
	     * @param endTime
	     *            结束时间(24h):23:50
	     * @return 格式化的日期格式：DD天HH小时mm分
	     */
	    public static String calculateIntervalTime(int startDay, String startTime,
	            int endDay, String endTime) {
	        int day = endDay - startDay;
	        int hour = 0;
	        int mm = 0;
	        if (day < 0) {
	            return null;
	        } else {
	            int sh = Integer.valueOf(startTime.split(":")[0]);
	            int eh = Integer.valueOf(endTime.split(":")[0]);
	            int sm = Integer.valueOf(startTime.split(":")[1]);
	            int em = Integer.valueOf(endTime.split(":")[1]);
	            hour = eh - sh;
	            if (hour > 0) {
	                mm = em - sm;
	                if (mm < 0) {
	                    hour--;
	                    mm = 60 + mm;
	                }
	            } else {
	                day = day - 1;
	                hour = 24 + hour;
	                mm = em - sm;
	                if (mm < 0) {
	                    hour--;
	                    mm = 60 + mm;
	                }
	            }
	        }
	        if (hour == 24) {
	            day++;
	            hour = 0;
	        }
	        if (day != 0) {
	            return day + "天" + hour + "小时" + mm + "分";
	        } else {
	            return hour + "小时" + mm + "分";
	        }
	    }
	    /**
	     * 计算两个时间差
	     *
	     * @param startTime
	     * @param endTime
	     * @return long
	     * @throws ParseException
	     */
	    public static long calculateIntervalTime(String startTime, String endTime)
	            throws ParseException {
	        return parseDateTime(endTime).getTime()
	                - parseDateTime(startTime).getTime();
	    }
	    // 字符串转换成时间
	    public static Date parseDateTime(String datetime) throws ParseException {
	        SimpleDateFormat sdf = new SimpleDateFormat(DATETIME);
	        return sdf.parse(datetime);
	    }
	    // 获取当前详细日期时间
	    public static String getDateTime() {
	        return new SimpleDateFormat(DATETIME).format(new Date());
	    }
	    // 转换为中文时间
	    public static String getChineseDateTime() {
	        return new SimpleDateFormat(DATETIMECHINESE).format(new Date());
	    }
	    // 转换为中文时间
	    public static String getChineseDate() {
	        return new SimpleDateFormat(DATECHINESE).format(new Date());
	    }
	    // 转换为中文时间
	    public static String getSimpleChineseDate() {
	        return new SimpleDateFormat(SIMPLEDATECHINESE).format(new Date());
	    }
	    // 转换为中文时间 如果num为-1表示前一天 1为后一天 0为当天
	    public static String getSimpleChineseDate(int num) {
	        Date d = new Date();
	        try {
	            d = parseDateTime(crateTimeFromNowTimeByDay(num));
	        } catch (ParseException e) {
	            e.printStackTrace();
	        }
	        return new SimpleDateFormat(SIMPLEDATECHINESE).format(d);
	    }
	    // 获取当前时间
	    public static String getTime() {
	        return new SimpleDateFormat(TIME).format(new Date());
	    }
	    // 获取当前年
	    public static String getYear() {
	        return new SimpleDateFormat(YEAR).format(new Date());
	    }
	    // 获取当前月
	    public static String getMonth() {
	        return new SimpleDateFormat(MONTH).format(new Date());
	    }
	    // 获取当前日
	    public static String getDay() {
	        return new SimpleDateFormat(DAY).format(new Date());
	    }
	    // 获取当前时
	    public static String getHour() {
	        return new SimpleDateFormat(HOUR).format(new Date());
	    }
	    // 获取当前分
	    public static String getMinute() {
	        return new SimpleDateFormat(MINUTE).format(new Date());
	    }
	    // 获取当前秒
	    public static String getSec() {
	        return new SimpleDateFormat(SEC).format(new Date());
	    }
	    // 获取昨天日期
	    public static String getYestday() {
	        Calendar cal = Calendar.getInstance();
	        cal.add(Calendar.DATE, -1);
	        Date d = cal.getTime();
	        return new SimpleDateFormat(DATETIME).format(d);// 获取昨天日期
	    }
	    public static String getMonday() {
	        Calendar calendar = new GregorianCalendar();
	        // 取得本周一
	        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
	                calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
	        calendar.setFirstDayOfWeek(Calendar.MONDAY);
	        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
	        return new SimpleDateFormat(DATETIME).format(calendar.getTime());// 获取昨天日期
	    }
	    
//	    public static void changeSkin()
//	    {
//	    	if(AlphaApplication.isNeedToChangeSkin)
//			{
//				File skin = new File(AlphaApplication.filePath);
//				if(skin.exists())
//				SkinManager.getInstance().load(skin.getAbsolutePath(),
//		                null);
//			}else
//			{
//				SkinManager.getInstance().restoreDefaultTheme();
//			}
//	    }
	   
}

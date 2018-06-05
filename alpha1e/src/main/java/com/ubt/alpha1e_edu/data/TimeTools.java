package com.ubt.alpha1e_edu.data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeTools {
    private static final long ONE_MINUTE = 60000L;
    private static final long ONE_HOUR = 3600000L;
    private static final long ONE_DAY = 86400000L;
    private static final long ONE_WEEK = 604800000L;

    private static final String ONE_SECOND = "刚刚";
    private static final String ONE_SECOND_AGO = "秒前";
    private static final String ONE_MINUTE_AGO = "分钟前";
    private static final String ONE_HOUR_AGO = "小时前";
    private static final String ONE_DAY_AGO = "天前";
    private static final String ONE_MONTH_AGO = "月前";
    private static final String ONE_YEAR_AGO = "年前";

    public static SimpleDateFormat yymmddFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static String format(String time) {
        String result = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:m:s");
        try {
            Date date = format.parse(time);
            result = format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String format(Date date) {
        long delta = new Date().getTime() - date.getTime();
        if (delta < 1L * ONE_MINUTE) {
            long seconds = toSeconds(delta);
            //return (seconds <= 0 ? 1 : seconds) + ONE_SECOND_AGO;
            return ONE_SECOND;
        }
        if (delta < 45L * ONE_MINUTE) {
            long minutes = toMinutes(delta);
            return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;
        }
        if (delta < 24L * ONE_HOUR) {
            long hours = toHours(delta);
            return (hours <= 0 ? 1 : hours) + ONE_HOUR_AGO;
        }

        /*if (delta < 48L * ONE_HOUR) {
            return "昨天";
        }
        if (delta < 30L * ONE_DAY) {
            long days = toDays(delta);
            return (days <= 0 ? 1 : days) + ONE_DAY_AGO;
        }
        if (delta < 12L * 4L * ONE_WEEK) {
            long months = toMonths(delta);
            return (months <= 0 ? 1 : months) + ONE_MONTH_AGO;
        } else {
            long years = toYears(delta);
            return (years <= 0 ? 1 : years) + ONE_YEAR_AGO;
        }*/

        return yymmddFormat.format(date);
    }

    /**
     * 需求：
     * 1分钟之内显示刚刚，
     * 一小时内按分钟数显示，如6分钟前发布则显示6分钟前；
     * 24小时内按小时数显示，如4小时前发布则显示4小时前；
     * 24小时以外则以日期显示，本年内仅显示月日，其他年显示年月日
     *
     * @param dateMin
     * @return
     */

    public static String format(long dateMin) {
        return format(new Date(dateMin));
    }


    private static long toSeconds(long date) {
        return date / 1000L;
    }

    private static long toMinutes(long date) {
        return toSeconds(date) / 60L;
    }

    private static long toHours(long date) {
        return toMinutes(date) / 60L;
    }

    private static long toDays(long date) {
        return toHours(date) / 24L;
    }

    private static long toMonths(long date) {
        return toDays(date) / 30L;
    }

    private static long toYears(long date) {
        return toMonths(date) / 365L;
    }


    /**
     * 从时间(毫秒)中提取出时间(时:分)
     * 时间格式:  时:分
     *
     * @param millisecond
     * @return
     */
    public static String getTimeFromMillisecond(Long millisecond) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        Date date = new Date(millisecond);
        String timeStr = simpleDateFormat.format(date);
        return timeStr;
    }

    public static String[] getHMArray(int day) {
        String[] dayArr = new String[day];
        for (int i = 0; i < day; i++) {
            dayArr[i] = i + "";
        }
        return dayArr;
    }

    public static String getCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        Date date = calendar.getTime();
        return sdf.format(date);

    }

    public static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
        Date date = calendar.getTime();
        return sdf.format(date);
    }

    public static String getNextDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = calendar.getTime();
        return sdf.format(date);
    }

    public static byte[] getCurrentDateTimeBytes() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yy:MM:dd:HH:mm:ss");
        Date date = calendar.getTime();
        String now = sdf.format(date);
        String[] data = now.split(":");
        byte[] timeData = new byte[6];
        for (int i = 0; i < 6; i++) {
            timeData[i] = Byte.parseByte(data[i]);
        }
        return timeData;
    }

    public static String[] getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date date = calendar.getTime();
        String now = sdf.format(date);
        String[] data = now.split(":");

        return data;
    }

    public static String getDistanceTime(String str1, String str2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        day = 0;
        String str_hour = "";
        if (hour < 10) {
            str_hour = "0" + hour;
        } else {
            str_hour = "" + hour;

        }
        String str_min = "";
        if (min < 10) {
            str_min = "0" + min;
        } else {
            str_min = "" + min;

        }

        return day + "天" + hour + "小时" + min + "分" + sec + "秒";
    }

    public static String[] getDistanceTimeStringArray(String str1, String str2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        day = 0;
        String str_hour = "";
        if (hour < 10) {
            str_hour = "0" + hour;
        } else {
            str_hour = "" + hour;

        }
        String str_min = "";
        if (min < 10) {
            str_min = "0" + min;
        } else {
            str_min = "" + min;

        }
        String str_sec = "";
        if (sec < 10) {
            str_sec = "0" + sec;
        } else {
            str_sec = "" + sec;

        }
        String[] time = new String[3];
        time[0] = str_hour;
        time[1] = str_min;
        time[2] = str_sec;
        return time;
    }

    public static String getTimeVal() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        String str = format.format(date);
        return str;
    }

    public static String getMMTime(int time) {
        int ss = time / 1000;
        int mm = ss / 60;
        ss = ss - mm * 60;
        return (mm < 10 ? "0" + mm : mm) + ":" + (ss < 10 ? "0" + ss : ss);
    }

    public static String getMMTimeForPublish(int time) {
        int mm = time / 60;
        time = time - mm * 60;
        return (mm < 10 ? "0" + mm : mm) + ":" + (time < 10 ? "0" + time : time);
    }

    public static String getDateStr(long time) {
        Date date = new Date(time);
        return (1900 + date.getYear())
                + "/"
                + (1 + date.getMonth())
                + "/"
                + date.getDay()
                + " "
                + (date.getHours() < 10 ? "0" + date.getHours() : date
                .getHours())
                + ":"
                + (date.getMinutes() < 10 ? "0" + date.getMinutes() : date
                .getMinutes())
                + ":"
                + (date.getSeconds() < 10 ? "0" + date.getSeconds() : date
                .getSeconds());
    }

    public static String getDataSimpleStr(long time) {
        Date date = new Date(time);
        return (1 + date.getMonth()) + "-" + (1 + date.getDay());
    }
}

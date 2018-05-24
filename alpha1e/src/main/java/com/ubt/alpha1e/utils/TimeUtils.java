package com.ubt.alpha1e.utils;

import com.ubt.alpha1e.utils.log.UbtLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/1/11.
 */
public class TimeUtils {

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

	public static SimpleDateFormat yymmddFormat=new SimpleDateFormat("yyyy-MM-dd");

	public static void main(String[] args) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:m:s");
		Date date = format.parse("2013-11-11 18:35:35");
		System.out.println(format(date));
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
	 *     1分钟之内显示刚刚，
	 *     一小时内按分钟数显示，如6分钟前发布则显示6分钟前；
	 *     24小时内按小时数显示，如4小时前发布则显示4小时前；
	 *     24小时以外则以日期显示，本年内仅显示月日，其他年显示年月日
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
	// 两次点击按钮之间的点击间隔不能少于1000毫秒
	private static final int MIN_CLICK_DELAY_TIME = 1000;
	private static long lastClickTime;

	public static boolean isFastClick() {
		boolean flag = false;
		long curClickTime = System.currentTimeMillis();
		if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
			flag = true;
		}
		lastClickTime = curClickTime;
		UbtLog.d("isNotFastClick", "lastclickTime===" + lastClickTime + "         ++++flag==" + flag);
		return flag;
	}

}
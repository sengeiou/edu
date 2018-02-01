package com.ubt.alpha1e.utils;

import com.ubt.alpha1e.utils.log.UbtLog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2018/1/28.
 */

public class DateUtils {

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");


    public static boolean isHoliday(Date date){
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY )
            {
                return true;
            } else{
                return false;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    public static boolean isHolidayOfToday(){
        return isHoliday(new Date());
    }

    /**
     * 获得指定日期的后一天
     *
     * @param date
     * @return
     */
    public static Date getSpecifiedDayAfter(Date date) {
        Calendar c = Calendar.getInstance();

        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day + 1);
        return c.getTime();
    }
}

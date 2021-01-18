package com.whr.baseui.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by 开发 on 2018/5/7.
 */

public class DateUtils {
    public static String MODULE_1 = "yyyy-MM-dd HH:mm:ss";
    public static String MODULE_2 = "yyyy年MM月dd日";
    public static String MODULE_3 = "MM月dd日 HH:mm";
    public static String MODULE_4 = "yyyy-MM-dd";
    public static String MODULE_5 = "yyyy/MM/dd";
    public static String MODULE_6 = "yyyy年MM月dd日 HH:mm";
    public static String MODULE_7 = "yyyy.MM.dd";
    public static String MODULE_8 = "HH:mm";
    public static String MODULE_9 = "yyyy年MM月";
    public static String MODULE_10 = "HH:mm:ss";
    public static String MODULE_11 = "MM/dd";
    public static String MODULE_12= "HH:mm:ss yyyy-MM-dd";
    public static String MODULE_13= "MM-dd HH:mm";
    public static String MODULE_14 = "MM月dd日";
    public static String MODULE_15 = "yyyy-MM-dd HH:mm";
    public static String getCurrentData() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MODULE_2);
        String dateStr = simpleDateFormat.format(date);
        return dateStr;
    }
    public static String secToTime1(int seconds) {
        int minute = (seconds ) / 60;
        int second = (seconds  - minute * 60);
        StringBuffer sb = new StringBuffer();
        if (minute > 0) {
            sb.append(minute + "分");
        }
        if (second > 0) {
            sb.append(second + "秒");
        }
        if (second == 0) {
            sb.append("<1秒");
        }
        return sb.toString();
    }

    public static String secToTime2(int seconds) {
        int minute = (seconds ) / 60;
        int second = (seconds  - minute * 60);
        StringBuffer sb = new StringBuffer();
        if (minute > 0) {
            sb.append(minute + ":");
        }
        if (second > 0) {
            sb.append(second );
        }
        if (second == 0) {
            sb.append("1");
        }
        return sb.toString();
    }


    public static String timeStampToDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MODULE_4);
        String dateStr = simpleDateFormat.format(date);
        return dateStr;
    }

    public static String timeStampToDate(Date date, String module) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(module);
        String dateStr = simpleDateFormat.format(date);
        return dateStr;
    }

    public static String timeStampToDate(long timeStamp) {
        if (EmptyUtils.isEmpty(timeStamp) || timeStamp == 0) return "";
        Date date = new Date(timeStamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = simpleDateFormat.format(date);
        return dateStr;
    }

    public static String timeStampToDate(long timeStamp, String module) {
        if (EmptyUtils.isEmpty(timeStamp) || timeStamp == 0) return "";
        Date date = new Date(timeStamp*1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(module);
        String dateStr = simpleDateFormat.format(date);
        return dateStr;
    }

    public static String timeStampToDate(String timeStamp) {
        if (EmptyUtils.isEmpty(timeStamp)) return "";
        long time = Long.parseLong(timeStamp);
        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = simpleDateFormat.format(date);
        return dateStr;
    }

    //月和日
    public static String timeStampToDateMD(long timeStamp) {
        Date date = new Date(timeStamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd日");
        String dateStr = simpleDateFormat.format(date);
        return dateStr;
    }

    /**
     * 将时间戳转换成当天零点的时间戳
     *
     * @param milliseconds
     * @return
     */
    private static Calendar zeroFromHour(long milliseconds) {
        Calendar calendar = Calendar.getInstance(); // 获得一个日历
        calendar.setTimeInMillis(completMilliseconds(milliseconds));
        zeroFromHour(calendar);
        return calendar;
    }
    /**
     * 将时，分，秒，以及毫秒值设置为0
     *
     * @param calendar
     */
    private static void zeroFromHour(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
    /**
     * 由于服务器返回的是10位，手机端使用需要补全3位
     *
     * @param milliseconds
     * @return
     */
    private static long completMilliseconds(long milliseconds) {
        String milStr = Long.toString(milliseconds);
        if (milStr.length() == 10) {
            milliseconds = milliseconds * 1000;
        }
        return milliseconds;
    }


    public static String getWhatDay (long timeStamp) {
        Calendar cal = zeroFromHour(timeStamp);
        String whatDay="";
        if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY){
            whatDay="星期六";
        }
        if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
            whatDay="星期日";
        }
        if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY){
            whatDay = "星期一";
        }
        if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY){
            whatDay = "星期二";
        }
        if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY){
            whatDay = "星期三";
        }
        if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY){
            whatDay = "星期四";
        }
        if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY){
            whatDay = "星期五";
        }
        return whatDay;
    }

    /**
     * 当前时间延期的年数
     *
     * @param year
     * @return
     */
    public static String getDelayYearData(int year) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MODULE_1, Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(System.currentTimeMillis());
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, year);
        return simpleDateFormat.format(calendar.getTime());
    }

    public static String getDelayDayData(int day) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MODULE_1, Locale.CHINA);
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(System.currentTimeMillis());
        calendar.setTime(date);
        calendar.add(Calendar.DATE, day);
        return simpleDateFormat.format(calendar.getTime());
    }


    public static int getTimeInterval(String dbtime1, String dbtime2) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
            Date date1 = format.parse(dbtime1);
            Date date2 = format.parse(dbtime2);
            int a = (int) ((date1.getTime() - date2.getTime()) / (1000 * 3600 * 24)) + 1;
            return a;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getTimeInterval(long dbtime1, long dbtime2) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            String da1 = format.format(dbtime1);
            String da2 = format.format(dbtime2);
            Date date1 = format.parse(da1);
            Date date2 = format.parse(da2);

            return date1.compareTo(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String subStringData(String date) {
        if (EmptyUtils.isEmpty(date)) return "";
        String[] dateStr;
        dateStr = date.split(" ");
        if (EmptyUtils.isEmpty(dateStr) || dateStr.length < 1) return "";
        return dateStr[0];
    }

    public final static int DAY = 86400000; //１天＝24*60*60*1000ms
    public final static int HOUR = 3600000;
    public final static int MIN = 60000;

    /**
     * 获取某个月份的天数
     *
     * @param year
     * @param month
     * @return
     */
    public static int getMonthDays(int year, int month) {
        if (month > 12) {
            month = 1;
            year += 1;
        } else if (month < 1) {
            month = 12;
            year -= 1;
        }
        int[] arr = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int days = 0;

        if (isLeapYear(year)) {
            arr[1] = 29; // 闰年2月29天
        }

        try {
            days = arr[month - 1];
        } catch (Exception e) {
            e.getStackTrace();
        }

        return days;
    }

    /**
     * 是否为闰年
     *
     * @param year
     * @return
     */
    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
    }


    /**
     * 根据年份和月份获取日期数组，1、2、3...
     *
     * @param year
     * @param month
     * @return
     */
    public static List<String> getMonthDaysArray(int year, int month) {
        List<String> dayList = new ArrayList<String>();
        int days = getMonthDays(year, month);
        for (int i = 1; i <= days; i++) {
            dayList.add(i + "");
        }
        return dayList;
    }


    /**
     * 获取当前系统时间的年份
     *
     * @return
     */
    public static int getYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    /**
     * 获取当前系统时间的月份
     *
     * @return
     */
    public static int getMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    /**
     * 获取当前系统时间的月份的第几天
     *
     * @return
     */
    public static int getCurrentMonthDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取当前系统时间的小时数
     *
     * @return
     */
    public static int getHour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取当前系统时间的分钟数
     *
     * @return
     */
    public static int getMinute() {
        return Calendar.getInstance().get(Calendar.MINUTE);
    }

    /**
     * 获取当前系统时间的秒数
     *
     * @return
     */
    public static int getSecond() {
        return Calendar.getInstance().get(Calendar.SECOND);
    }

    /**
     * 获取当前系统时间的毫秒数
     *
     * @return
     */
    public static int getMillSecond() {
        return Calendar.getInstance().get(Calendar.MILLISECOND);
    }


    /**
     * 根据系统默认时区，获取当前时间与time的天数差
     *
     * @param time 相差的天数
     * @return　等于０表示今天，大于０表示今天之前
     */
    public static long getDaySpan(long time) {
        return getTimeSpan(time, DAY);
    }

    public static long getHourSpan(long time) {
        return getTimeSpan(time, HOUR);
    }

    public static long getMinSpan(long time) {
        return getTimeSpan(time, MIN);
    }

    public static long getTimeSpan(long time, long span) {
        // 系统默认时区，ms
        int tiemzone = TimeZone.getDefault().getRawOffset();
        return (System.currentTimeMillis() + tiemzone) / span
                - (time + tiemzone) / span;
    }

    public static boolean isToday(long time) {
        return getDaySpan(time) == 0;
    }

    public static boolean isYestoday(long time) {
        return getDaySpan(time) == 1;
    }

    public static boolean isTomorrow(long time) {
        return getDaySpan(time) == -1;
    }

    /**
     * @return 返回当前时间，yyyy-MM-dd HH-mm-ss
     */
    public static String getDate() {
        return getDate("yyyy-MM-dd HH-mm-ss");
    }

    public static String getDate(String format) {
        return getDate(new Date().getTime(), format);
    }

    public static String getDate(long time, String format) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(format);
        String date = sDateFormat.format(time);
        return date;
    }
}

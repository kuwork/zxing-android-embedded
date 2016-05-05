package com.ajb.merchants.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author 陈国宏
 * @ClassName: DateConvertor
 * @Description:数据库时间转换
 * @date 2013年8月26日 上午10:47:35
 */
public class DateConvertor {
    /*
     * MySQL 日期类型：日期格式、所占存储空间、日期范围 比较。 日期类型 存储空间 日期格式 日期范围 ------------
	 * --------- --------------------- -----------------------------------------
	 * datetime 8 bytes YYYY-MM-DD HH:MM:SS 1000-01-01 00:00:00 ~ 9999-12-31
	 * 23:59:59 timestamp 4 bytes YYYY-MM-DD HH:MM:SS 1970-01-01 00:00:01 ~ 2038
	 * date 3 bytes YYYY-MM-DD 1000-01-01 ~ 9999-12-31 year 1 bytes YYYY 1901 ~
	 * 2155
	 */

    /**
     * 获得当前的日期时间
     *
     * @return 返回String类型, "yyyy-MM-dd HH:mm:ss"
     */
    public static String getTimestampString() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp time = new Timestamp(System.currentTimeMillis());
        return df.format(time);
    }

    /**
     * 将TimeStamp转化成yyyy-MM-dd HH:mm:ss样式
     *
     * @param time 类型为java.sql.Timstamp
     * @return 返回String类型，样式为 "yyyy-MM-dd HH:mm:ss"
     */
    public static String getTimestampString(Timestamp time) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(df.format(time));
        return df.format(time);
    }

    /**
     * 将TimeStamp转化成yyyy-MM-dd HH:mm:ss样式
     *
     * @return 返回String类型，样式为 "yyyy-MM-dd HH:mm:ss"
     */
    public static String getTimestampString(long s) {
        Timestamp time = new Timestamp(s);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(df.format(time));
        return df.format(time);
    }

    /**
     * @return 返回String类型，日期时间 "yyyy-MM-dd HH:mm:ss"
     */
    public static String getTimestampString(String timestampString) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(Timestamp.valueOf(timestampString));
    }

    /**
     * @return 当前时间，类型为java.sql.Timstamp
     */
    public static Timestamp getTimestamp() {
        Timestamp time = new Timestamp(System.currentTimeMillis());
        return time;
    }

    /**
     * @param timestampString 类型为String
     * @return 返回java.sql.Timestamp实例对象
     */
    public static Timestamp getTimestamp(String timestampString) {
        Timestamp time = Timestamp.valueOf(timestampString);
        return time;
    }

    /**
     * @param timestamp 类型为long
     * @return 返回java.sql.Timestamp实例对象
     */
    public static Timestamp getTimestamp(long timestamp) {
        return new Timestamp(timestamp);
    }

    /**
     * @return 返回当前日期，类型为String
     */
    public static String getDateString() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date date = new java.util.Date();
        return df.format(date);
    }

    /**
     * @return 返回当前日期+星期，类型为String
     */
    public static String getDateWeekString(long s) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 E");
        Timestamp time = new Timestamp(s);
        return df.format(time).replace("周", "星期");
    }

    /**
     * @param date 日期，类型为java.util.Date
     * @return 返回日期，类型为String
     */
    public static String getUtilDateString(java.util.Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }

    /**
     * @return 但会当前日期，类型为java.util.Date
     */
    public static java.util.Date getUtilDate() {
        java.util.Date date = new java.util.Date();
        return date;
    }

    /**
     * @param date 日期，类型为String
     * @return java.util.Date实例对象
     */
    public static java.util.Date getUtilDate(String date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date date1 = null;
        try {
            date1 = df.parse(date);
        } catch (ParseException ex) {
            date1 = null;
        }
        return date1;
    }

    /**
     * @param date 日期，类型为java.sql.Date
     * @return 返回日期，类型为String
     */
    public static String getSqlDateString(java.sql.Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }

    /**
     * @return 返回当前日期，类型为java.sql.Date
     */
    public static java.sql.Date getSqlDate() {
        java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
        return date;
    }

    /**
     * @param date 日期，类型为String
     * @return java.sql.Date实例对象
     */
    public static java.sql.Date getSqlDate(String date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date date1 = null;
        try {
            date1 = df.parse(date);
        } catch (ParseException ex) {
            return null;
        }
        java.sql.Date date2 = new java.sql.Date(date1.getTime());
        return date2;
    }

    /**
     * @param timestampString 格式化时间字符串
     * @return 毫秒
     * @Title: getTimeFromString
     * @Description:由格式化时间字符串装成LONG毫秒
     */
    public static long getTimeFromString(String timestampString) {
        Timestamp timestamp = Timestamp.valueOf(timestampString);
        return timestamp.getTime();
    }

    /**
     * 毫秒转换为时分秒
     *
     * @param time 毫秒数
     * @return
     */
    public static String millToTime(long time) {
        return secToTime((int) (time / 1000));
    }

    // a integer to xx:xx:xx 整数(秒数)转换为时分秒格式(xx:xx:xx)
    public static String secToTime(long time) {
        String timeStr = null;
        long hour = 0;
        long minute = 0;
        long second = 0;
        if (time <= 0)
            return "00分00秒";
        else {
            hour = time / 60 / 60;
            minute = (time - hour * 3600) / 60;
            second = time - hour * 3600 - minute * 60;
            if (hour == 0 && minute != 0) {
                timeStr = unitFormat(minute) + "分" + unitFormat(second) + "秒";
            } else if (hour == 0 && minute == 0) {
                timeStr = unitFormat(second) + "秒";
            } else {
                timeStr = unitFormat(hour) + "时" + unitFormat(minute) + "分" + unitFormat(second) + "秒";
            }
        }
        return timeStr;
    }

    private static String unitFormat(long i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Long.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }
}
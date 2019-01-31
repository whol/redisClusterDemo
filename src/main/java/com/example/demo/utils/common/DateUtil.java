package com.example.demo.utils.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class DateUtil {

    private DateUtil() {

    }

    /**
     * 日期格式
     */
    public interface DATE_PATTERN {
        String HHMMSS = "HHmmss";
        String HH_MM_SS = "HH:mm:ss";
        String YYYYMMDD = "yyyyMMdd";
        String YYYYMMDDHH = "yyyyMMddHH";
        String YYYY_MM_DD = "yyyy_MM_dd";
        String YYYYMM = "yyyyMM";
        String YYYYMMDDHHMMSS= "yyyyMMddHHmmss";
        String YYYYMMDDHHMMSSSSS= "yyyyMMddHHmmssSSS";
        String YYYY_MM_DD_HH_MM_SS= "yyyy-MM-dd HH:mm:ss";
        String YYYY_MM_DD_HH_MM= "yyyy-MM-dd HH:mm";
        String START_HH_MM_SS = " 00:00:00";
        String END_HH_MM_SS = " 23:59:59";
    }

    /**
     * 将Date类型转换成String类型
     * @param date
     * @return 形如："yyyy-MM-dd HH:mm:ss"
     */
    public static String date2String(Date date) {
        return date2String(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 获取当前日期的后一天
     * @return
     */
    public static Date currentNextDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    /**
     * 将Date按格式转化成String
     * @param date
     * @param pattern
     * @return
     */
    public static String date2String(Date date, String pattern) {
        if (date == null || pattern == null) {
            return null;
        }
        return DateFormatUtils.format(date, pattern);
    }

    /**
     * 将String类型转换成Date类型
     * @param date
     * @return
     */
    public static Date string2Date(String date) {
        try {
            return DateUtils.parseDate(date, DATE_PATTERN.YYYY_MM_DD_HH_MM_SS);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 对时间进行增加，减少
     * @param date
     * @param offset
     * @param datetype
     * @return
     */
    public static Date string2DateOfset(String date, int offset, int datetype) {
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(DateUtils.parseDate(date, DATE_PATTERN.YYYY_MM_DD_HH_MM_SS));
            if (datetype == 1) {
                c.add(Calendar.DAY_OF_YEAR, offset);
            }

            if (datetype == 2) {
                c.add(Calendar.HOUR, offset);
            }

            if (datetype == 3) {
                c.add(Calendar.MINUTE, offset);
            }

            if (datetype == 4) {
                c.add(Calendar.SECOND, offset);
            }

            if (datetype == 5) {
                c.add(Calendar.MONTH, offset);
            }

            return c.getTime();
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date getSysdate() {
        Calendar c = Calendar.getInstance();
        return c.getTime();
    }

    public static void genDateInListToStringFormat(List<Map<String, Object>> listMap, String[] listString) {
        if (null == listMap || listMap.isEmpty()) {
            return;
        }
        for (Map<String, Object> map : listMap) {
            for (String string : listString) {
                Date date = (Date) map.get(string);
                String trans = date2String(date);
                map.put(string, trans);
            }
        }
    }

    public static void genDateToStringFormatForEs(List<Map<String, Object>> listMap, String[] listString) {
        if (null == listMap || listMap.isEmpty()) {
            return;
        }
        for (Map<String, Object> map : listMap) {
            for (String string : listString) {
                String date = (String) map.get(string);
                if (StringUtils.isEmpty(date)) {
                    continue;
                }
                if (date.length() >=19) {
                    String trans = date.substring(0, 19);
                    map.put(string, trans);
                }
                if (date.length() == 10) {
                    map.put(string, date + DATE_PATTERN.START_HH_MM_SS);
                }
            }
        }
    }

    /**
     * 处理es返回的时间格式
     * @param date
     * @return
     */
    public static Date string2DateForES(String date) {
        String time;
        try {
            if (StringUtils.isNotBlank(date)) {
                if (date.length() >= 19) {
                    time = date.substring(0, 19);
                } else if (date.length() == 10) {
                    time = date + DATE_PATTERN.START_HH_MM_SS;
                } else {
                    time = date + DATE_PATTERN.START_HH_MM_SS;
                }

                return DateUtils.parseDate(time, DATE_PATTERN.YYYY_MM_DD_HH_MM_SS);
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("时间转换异常", e);
            return null;
        }
    }

    public static String date2StrForES(String dateStr) {
        Date date = string2DateForES(dateStr);
        return date != null ? date2String(date) : "";
    }

    public static String stringFormatForES(String date) {
        String time = "";
        if (StringUtils.isNotBlank(date)) {
            if (date.length() >= 19) {
                time = date.substring(0, 19);
            }

            if (date.length() == 10) {
                time = date + DATE_PATTERN.START_HH_MM_SS;
            }

            return time;
        } else {
            return "";
        }
    }

    /**
     * 转换日期字符串格式
     * @param srcDate
     * @param srcFormat
     * @param distFormat
     * @return
     * @throws ParseException
     */
    public static String formatString2String(String srcDate, String srcFormat, String distFormat) throws ParseException {
        String resStr = "";
        if (StringUtils.isEmpty(srcDate)) {
            return resStr;
        } else {
            DateFormat df1 = new SimpleDateFormat(srcFormat);
            DateFormat df2 = new SimpleDateFormat(distFormat);
            Date tmpDate = df1.parse(srcDate);
            resStr = df2.format(tmpDate);
            return resStr;
        }
    }

    /**
     * 获得日期当月第一天
     * @param date
     * @return
     */
    public static Date getFirstDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //获取某月最小天数
        int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最小天数
        cal.set(Calendar.DAY_OF_MONTH, firstDay);
        return cal.getTime();
    }

    /**
     * 依据格式校验日期字符串
     * @param date
     * @param format
     * @return
     */
    public static boolean isFormat(String date, String format) {
        if (StringUtils.isBlank(date) && "null".equals(date) && date.length() != format.length()) {
            return false;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.parse(date);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
}

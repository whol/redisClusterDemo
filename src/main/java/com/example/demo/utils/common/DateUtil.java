package com.example.demo.utils.common;

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
}

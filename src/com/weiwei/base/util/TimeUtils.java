package com.weiwei.base.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     *
     * @param
     * @return
     */
   public static String stampToDate(Long stamp) {
       SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
       Date date = new Date(stamp);
       return simpleDateFormat.format(date);
   }
}

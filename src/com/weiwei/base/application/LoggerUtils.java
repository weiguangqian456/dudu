package com.weiwei.base.application;

import android.os.Environment;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;


/**
 * 日志记录
 */
public class LoggerUtils {


    /**
     * 当前阶段标示
     */

    private static String path;
    private static File file;
    private static FileOutputStream outputStream;
    private static String pattern = "yyyy-MM-dd HH:mm:ss";

    static {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File externalStorageDirectory = Environment.getExternalStorageDirectory();
            path = externalStorageDirectory.getAbsolutePath() + "/log/";
            initLogFile();
        } else {
            File innerDirectory = Environment.getDataDirectory();
            path = innerDirectory.getAbsolutePath() + "/log/";
            initLogFile();
        }
    }


    /**
     * 初始化日志文件
     */
    private static void initLogFile() {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        file = new File(new File(path), "log.txt");

        try {
            outputStream = new FileOutputStream(file, true);
        } catch (FileNotFoundException e) {
//			LogUtils.e("init Log wrong!!!");
        }
    }

    public static void info(String msg) {
        info(LoggerUtils.class, msg);
    }

    /**
     * 记录日志
     *
     * @param clazz
     * @param msg
     * @param time
     */
    private static void writeLogFile(Class<?> clazz, String msg, String time) {
        if (outputStream != null) {
            try {
                outputStream.write(time.getBytes());
                String className = "";
                if (clazz != null) {
                    className = clazz.getSimpleName();
                }
                outputStream.write(("    " + className + "\r\n").getBytes());
                outputStream.write(msg.getBytes());
                outputStream.write("\r\n".getBytes());
                outputStream.flush();
            } catch (IOException e) {
//				LogUtils.e( "an error occured while writing file...", e);
            }
        }
    }

    /**
     * 这个只是用来记录日志的，
     * 如果开发阶段要打印日志，可以通过Logger调用他的父类LogUtils身上的方法。
     *
     * @param clazz
     * @param msg
     */
    public static void info(Class<?> clazz, String msg) {
        switch (GlobalParams.currentStage) {
            case GlobalParams.DEVELOP:
                // 控制台输出
//			LogUtils.i(msg);
                break;
            case GlobalParams.DEBUG:
                // 在应用下面创建目录存放日志
                writeLogFile(clazz, msg, formatDate());
                break;
            case GlobalParams.BATE:
                // 写日志到文件
                writeLogFile(clazz, msg, formatDate());
                break;
            case GlobalParams.RELEASE:
                // 一般不做日志记录
                break;
        }
    }

    private static String formatDate() {
        Date date = new Date();
        String time = DateFormatUtils.format(date, pattern);
        return time;
    }
}

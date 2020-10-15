package com.weiwei.base.dataprovider;

import android.content.Context;
import android.content.Intent;

import com.weiwei.base.common.CustomLog;

//此类中只关注函数具体功能的实现，并保存数据。不涉及到具体的线程管理。
//每个函数执行完成后，以广播形式通知activity.
//注意:每个函数都要判断参数。线程管理里面不判断参数

public class GlobalFuntion {

    private final static boolean bLogFlag = true; // 是否打印日志
    public static String sendData; // post要发送的数据
    public static String recvData; // http访问返回的数据

    // 打印变量
    public static void PrintValue(String keyStr, String value) {
        String methodName = "线程名：" + Thread.currentThread().getName() + " 线程id:"
                + String.valueOf(Thread.currentThread().getId()) + " ";
        if (bLogFlag) {
            CustomLog.i(Thread.currentThread().getStackTrace()[2].getFileName(), methodName + keyStr + ":" + value);
        }
    }

    // 打印变量
    public static void PrintValue(String keyStr, int value) {
        String methodName = "线程名：" + Thread.currentThread().getName() + " 线程id:"
                + String.valueOf(Thread.currentThread().getId()) + " ";
        if (bLogFlag) {
            CustomLog.i(Thread.currentThread().getStackTrace()[2].getFileName(),
                    methodName + keyStr + ":" + String.valueOf(value));
        }
    }

    // 打印变量
    public static void PrintValue(String keyStr, long value) {
        String methodName = "线程名：" + Thread.currentThread().getName() + " 线程id:"
                + String.valueOf(Thread.currentThread().getId()) + " ";
        if (bLogFlag) {
            CustomLog.i(Thread.currentThread().getStackTrace()[2].getFileName(),
                    methodName + keyStr + ":" + String.valueOf(value));
        }
    }

    // 打印变量
    public static void PrintValue(String keyStr, Boolean value) {
        String methodName = "线程名：" + Thread.currentThread().getName() + " 线程id:"
                + String.valueOf(Thread.currentThread().getId()) + " ";
        if (bLogFlag) {
            CustomLog.i(Thread.currentThread().getStackTrace()[2].getFileName(),
                    methodName + keyStr + ":" + String.valueOf(value));
        }
    }

    /**
     * 函数执行完成后发送的广播消息，activity接收到广播后会进行相应的UI更新
     *
     * @param context
     * @param actinoName :发送广播的action name
     * @param data       ：函数执行正确后返回的数据，Activity有获取此数据时也可通过全局变量中的值获取
     */
    public static void SendBroadcastMsg(Context context, String actinoName, String data) {
        Intent intent = new Intent();
        intent.setAction(actinoName);
        intent.setPackage(context.getPackageName());//只能本应用收到
        intent.putExtra(GlobalVariables.VS_KeyMsg, data);
        context.sendBroadcast(intent);
        PrintValue("发送广播消息, data", data);
    }

}

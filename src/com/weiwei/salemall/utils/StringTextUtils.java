package com.weiwei.salemall.utils;

import android.content.Context;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: EDZ
 * @description: 拟解决 TextView自动换行问题
 */
public class StringTextUtils {
    private static StringTextUtils instance;
    private Context mContext;

    private StringTextUtils(Context context) {
        this.mContext = context;
    }

    public synchronized static StringTextUtils getInstance(Context context) {
        if (instance == null) {
            instance = new StringTextUtils(context);
        }
        return instance;
    }

    public static String stringFilter(String str) {
        char[] c = str.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375) c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    public static String ToDBC(String str) {
        str = str.replaceAll("【", "[").replaceAll("】", "]")
                .replaceAll("！", "!").replaceAll("：", ":");// 替换中文标号

        String regEx = "[『』]"; // 清除掉特殊字符
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }
}

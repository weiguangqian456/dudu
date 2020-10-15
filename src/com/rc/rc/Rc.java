package com.rc.rc;

public class Rc {
	//rc4解密
	public static native String getString(String key,int keyType);
	//rc4加密
    public static native String putString(String key, int keyType);
    //rc4签名
    public static native String  getSign(String key, int keyType);

    static {
        System.loadLibrary("RcSign");
    }
}

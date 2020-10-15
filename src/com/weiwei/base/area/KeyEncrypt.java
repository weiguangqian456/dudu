package com.weiwei.base.area;


public class KeyEncrypt {
	public static KeyEncrypt Keyencrypt = null;

	// JNIEXPORT jstring JNICALL Java_com_keepc_service_KeyEncrypt_KcDecode(JNIEnv * env,jobject thiz,jstring
	// src,jstring keystr,jint srclen,jint deType,jint keyType,jint keylen)
	public native String KcDecode(String src, String keyStr, int srcLen, int deType, int keyType, int keyLen);

	public static KeyEncrypt getInstance() {
		if (Keyencrypt == null) {
			Keyencrypt = new KeyEncrypt();
		}
		return Keyencrypt;
	}

	static {
		System.loadLibrary("kcDecode");
	}

	/**
	 * @param src
	 *            Ҫ���ܵ��ַ�
	 * @param keyStr
	 *            ������key�ַ�
	 * @param srcLen
	 *            ���ܵ��ַ���
	 * @param deType
	 *            �����㷨����
	 * @param keyType
	 *            ����key����
	 * @param keyLen
	 *            ������key�ַ��� ע�����keystrΪ�գ���ʹ������key�ַ�
	 * @return
	 */
	public synchronized String put_KcDecode(String src, String keyStr, int srcLen, int deType, int keyType, int keyLen) {
		// Log.i("", "md5=" + KcDecode(src, keyStr, srcLen, deType, keyType, keyLen));
		// Log.i("", "rc4=" + KcDecode(inStr, "", inStr.length(), 2, 1, 0));
		return KcDecode(src, keyStr, srcLen, deType, keyType, keyLen);
	}
}

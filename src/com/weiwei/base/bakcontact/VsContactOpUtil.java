package com.weiwei.base.bakcontact;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Relation;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.CommonDataKinds.Website;
import android.text.TextUtils;

public class VsContactOpUtil {

	private static int posti = 0;

	// ------------------ContactManagerOf-----------------------
	public static int getPhoneType(Context context, String lable) {
		int type = Phone.TYPE_CUSTOM;
		if ("住宅".equals(lable)) {
			type = Phone.TYPE_HOME;
		} else if ("手机".equals(lable)) {
			type = Phone.TYPE_MOBILE;
		} else if ("工作".equals(lable)) {
			type = Phone.TYPE_WORK;
		} else if ("工作传真".equals(lable)) {
			type = Phone.TYPE_FAX_WORK;
		} else if ("住宅传真".equals(lable)) {
			type = Phone.TYPE_FAX_HOME;
		} else if ("寻呼机".equals(lable)) {
			type = Phone.TYPE_PAGER;
		} else if ("其他".equals(lable)) {
			type = Phone.TYPE_OTHER;
		} else if ("回拨号码".equals(lable)) {
			type = Phone.TYPE_CALLBACK;
		} else if ("车载电话".equals(lable)) {
			type = Phone.TYPE_CAR;
		} else if ("公司总机".equals(lable)) {
			type = Phone.TYPE_COMPANY_MAIN;
		} else if ("ISDN".equals(lable)) {
			type = Phone.TYPE_ISDN;
		} else if ("总机".equals(lable)) {
			type = Phone.TYPE_MAIN;
		} else if ("其他传真".equals(lable)) {
			type = Phone.TYPE_OTHER_FAX;
		} else if ("无线装置".equals(lable)) {
			type = Phone.TYPE_RADIO;
		} else if ("电报".equals(lable)) {
			type = Phone.TYPE_TELEX;
		} else if ("TTY TDD".equals(lable)) {
			type = Phone.TYPE_TTY_TDD;
		} else if ("单位手机".equals(lable)) {
			type = Phone.TYPE_WORK_MOBILE;
		} else if ("单位寻呼机".equals(lable)) {
			type = Phone.TYPE_WORK_PAGER;
		} else if ("助理".equals(lable)) {
			type = Phone.TYPE_ASSISTANT;
		} else if ("彩信".equals(lable)) {
			type = Phone.TYPE_MMS;
		}
		return type;
	}

	public static int getEmailType(Context context, String lable) {
		int type = Email.TYPE_CUSTOM;
		if ("家庭".equals(lable)) {
			type = Email.TYPE_HOME;
		} else if ("手机".equals(lable)) {
			type = Email.TYPE_MOBILE;
		} else if ("其他".equals(lable)) {
			type = Email.TYPE_OTHER;
		} else if ("工作".equals(lable)) {
			type = Email.TYPE_WORK;
		}
		return type;
	}

	public static int getAddressType(Context context, String lable) {
		int type = StructuredPostal.TYPE_CUSTOM;
		if ("家庭".equals(lable)) {
			type = StructuredPostal.TYPE_HOME;
		} else if ("工作".equals(lable)) {
			type = StructuredPostal.TYPE_WORK;
		} else if ("其他".equals(lable)) {
			type = StructuredPostal.TYPE_OTHER;
		}
		return type;
	}

	public static int getUrlType(Context context, String lable) {
		int type = Website.TYPE_CUSTOM;
		if ("家庭".equals(lable)) {
			type = Website.TYPE_HOME;
		} else if ("工作".equals(lable)) {
			type = Website.TYPE_WORK;
		} else if ("其他".equals(lable)) {
			type = Website.TYPE_OTHER;
		}
		return type;
	}

	public static int getRelationType(String lable) {
		int type = Relation.TYPE_CUSTOM;
		if ("Assistant".equals(lable)) {
			type = Relation.TYPE_ASSISTANT;
		} else if ("Brother".equals(lable)) {
			type = Relation.TYPE_BROTHER;
		} else if ("Child".equals(lable)) {
			type = Relation.TYPE_CHILD;
		} else if ("DomesticPartner".equals(lable)) {
			type = Relation.TYPE_DOMESTIC_PARTNER;
		} else if ("Father".equals(lable)) {
			type = Relation.TYPE_FATHER;
		} else if ("Friend".equals(lable)) {
			type = Relation.TYPE_FRIEND;
		} else if ("Manager".equals(lable)) {
			type = Relation.TYPE_MANAGER;
		} else if ("Mother".equals(lable)) {
			type = Relation.TYPE_MOTHER;
		} else if ("Parent".equals(lable)) {
			type = Relation.TYPE_PARENT;
		} else if ("Partner".equals(lable)) {
			type = Relation.TYPE_PARTNER;
		} else if ("ReferredBy".equals(lable)) {
			type = Relation.TYPE_REFERRED_BY;
		} else if ("Relative".equals(lable)) {
			type = Relation.TYPE_RELATIVE;
		} else if ("Sister".equals(lable)) {
			type = Relation.TYPE_SISTER;
		} else if ("Spouse".equals(lable)) {
			type = Relation.TYPE_SPOUSE;
		}
		return type;
	}

	public static int getImType(Context context, String lable) {
		int type = Im.TYPE_CUSTOM;
		if ("家庭".equals(lable)) {
			type = Im.TYPE_HOME;
		} else if ("工作".equals(lable)) {
			type = Im.TYPE_WORK;
		} else if ("其他".equals(lable)) {
			type = Im.TYPE_OTHER;
		}
		return type;
	}

	public static int getImProtocolType(String lable) {
		int type = Im.PROTOCOL_CUSTOM;
		if ("AIM".equals(lable)) {
			type = Im.PROTOCOL_AIM;
		} else if ("MSN".equals(lable)) {
			type = Im.PROTOCOL_MSN;
		} else if ("Yahoo".equals(lable)) {
			type = Im.PROTOCOL_YAHOO;
		} else if ("Skype".equals(lable)) {
			type = Im.PROTOCOL_SKYPE;
		} else if ("QQ".equals(lable)) {
			type = Im.PROTOCOL_QQ;
		} else if ("GoogleTalk".equals(lable)) {
			type = Im.PROTOCOL_GOOGLE_TALK;
		} else if ("ICQ".equals(lable)) {
			type = Im.PROTOCOL_ICQ;
		} else if ("Jabber".equals(lable)) {
			type = Im.PROTOCOL_JABBER;
		} else if ("NetMeeting".equals(lable)) {
			type = Im.PROTOCOL_NETMEETING;
		}

		return type;
	}

	// ------------------ContactOperationsOf-----------------------
	@SuppressWarnings("deprecation")
	public static byte[] downloadAvatar(final String avatarUrl, boolean isWifi) {
		if (TextUtils.isEmpty(avatarUrl)) {
			return null;
		}
		HttpURLConnection connection = null;
		InputStream is = null;
		try {
			URL url = new URL(avatarUrl);
			String proxyHost = android.net.Proxy.getDefaultHost();
			if (isWifi) {
				connection = (HttpURLConnection) url.openConnection();
			} else if (proxyHost != null) {// 如果是wap方式，要加网关
				java.net.Proxy p = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(
						android.net.Proxy.getDefaultHost(), android.net.Proxy.getDefaultPort()));
				connection = (HttpURLConnection) url.openConnection(p);
			} else {
				connection = (HttpURLConnection) url.openConnection();
			}
			connection.connect();
			try {
				is = connection.getInputStream();
				ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
				int ch;
				while ((ch = is.read()) != -1) {
					bytestream.write(ch);
				}
				byte imgdata[] = bytestream.toByteArray();
				bytestream.flush();
				bytestream.close();
				return imgdata;
			} finally {
				if (is != null) {
					is.close();
					is = null;
				}
				connection.disconnect();
			}
		} catch (MalformedURLException muex) {
			muex.printStackTrace();
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
		return null;
	}

	// ------------------ContactHelperOf-----------------------

	/**
	 * 根据type获得名称
	 */
	public static String getPhoneLable(Context context, String lable, int type) {
		switch (type) {
		case Phone.TYPE_HOME:
			return "住宅";
		case Phone.TYPE_MOBILE:
			return "手机";
		case Phone.TYPE_WORK:
			return "工作";
		case Phone.TYPE_FAX_WORK:
			return "工作传真";
		case Phone.TYPE_FAX_HOME:
			return "住宅传真";
		case Phone.TYPE_PAGER:
			return "寻呼机";
		case Phone.TYPE_OTHER:
			return "其他";
		case Phone.TYPE_CALLBACK:
			return "回拨号码";
		case Phone.TYPE_CAR:
			return "车载电话";
		case Phone.TYPE_COMPANY_MAIN:
			return "公司总机";
		case Phone.TYPE_ISDN:
			return "ISDN";
		case Phone.TYPE_MAIN:
			return "总机";
		case Phone.TYPE_OTHER_FAX:
			return "其他传真";
		case Phone.TYPE_RADIO:
			return "无线装置";
		case Phone.TYPE_TELEX:
			return "电报";
		case Phone.TYPE_TTY_TDD:
			return "TTY TDD";
		case Phone.TYPE_WORK_MOBILE:
			return "单位手机";
		case Phone.TYPE_WORK_PAGER:
			return "单位寻呼机";
		case Phone.TYPE_ASSISTANT:
			return "助理";
		case Phone.TYPE_MMS:
			return "彩信";
		}
		return lable;
	}

	public static String getEmailLbale(Context context, String lable, int type) {
		switch (type) {
		case Email.TYPE_HOME:
			return "家庭";
		case Email.TYPE_MOBILE:
			return "手机";
		case Email.TYPE_OTHER:
			return "其他";
		case Email.TYPE_WORK:
			return "工作";
		}
		return lable;
	}

	public static String getUrlLbale(Context context, String lable, int type) {
		switch (type) {
		case Website.TYPE_HOME:
			return "家庭";
		case Website.TYPE_WORK:
			return "工作";
		case Website.TYPE_OTHER:
			return "其他";
		}
		return lable;
	}

	public static String getRelationLable(String lable, int type) {
		switch (type) {
		case Relation.TYPE_ASSISTANT:
			return "Assistant";
		case Relation.TYPE_BROTHER:
			return "Brother";
		case Relation.TYPE_CHILD:
			return "Child";
		case Relation.TYPE_DOMESTIC_PARTNER:
			return "DomesticPartner";
		case Relation.TYPE_FATHER:
			return "Father";
		case Relation.TYPE_FRIEND:
			return "Friend";
		case Relation.TYPE_MANAGER:
			return "Manager";
		case Relation.TYPE_MOTHER:
			return "Mother";
		case Relation.TYPE_PARENT:
			return "Parent";
		case Relation.TYPE_PARTNER:
			return "Partner";
		case Relation.TYPE_REFERRED_BY:
			return "ReferredBy";
		case Relation.TYPE_RELATIVE:
			return "Relative";
		case Relation.TYPE_SISTER:
			return "Sister";
		case Relation.TYPE_SPOUSE:
			return "Spouse";
		}
		return lable;
	}

	public static String getAddressLable(Context context, String lable, int type) {
		switch (type) {
		case StructuredPostal.TYPE_HOME:
			return "家庭";
		case StructuredPostal.TYPE_WORK:
			return "工作";
		case StructuredPostal.TYPE_OTHER:
			return "其他";
		}
		return lable;
	}

	public static String getImLable(Context context, String lable, int type) {
		switch (type) {
		case Im.TYPE_HOME:
			return "家庭";
		case Im.TYPE_WORK:
			return "工作";
		case Im.TYPE_OTHER:
			return "其他";
		}
		return lable;
	}

	public static String getImProtocolLable(String lable, int type) {
		switch (type) {
		case Im.PROTOCOL_AIM:
			return "AIM";
		case Im.PROTOCOL_MSN:
			return "MSN";
		case Im.PROTOCOL_YAHOO:
			return "Yahoo";
		case Im.PROTOCOL_SKYPE:
			return "Skype";
		case Im.PROTOCOL_QQ:
			return "QQ";
		case Im.PROTOCOL_GOOGLE_TALK:
			return "GoogleTalk";
		case Im.PROTOCOL_ICQ:
			return "ICQ";
		case Im.PROTOCOL_JABBER:
			return "Jabber";
		case Im.PROTOCOL_NETMEETING:
			return "NetMeeting";
		}
		return lable;
	}

	/**
	 * 此方法描述的是：将二进制保存为图片
	 * 
	 * @param data
	 * @param name
	 * @author: 谢康林
	 * @version: 2012-5-9 下午04:00:07
	 */
	public static void bc(byte[] data, String name) {
		FileOutputStream outputstream = null; // 创建文件输出流
		try {
			File file = new File(name); // 创建文件
			Bitmap bitmap = getBitmapFromByte(data);
			outputstream = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputstream);// 将二进制文件输出
			bitmap.recycle();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (outputstream != null) {
					outputstream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}// 关闭
		}
	}

	/**
	 * 此方法描述的是：的到Bitmap便于压缩
	 * 
	 * @param temp
	 * @return
	 * @author: 谢康林
	 * @version: 2012-5-9 下午04:01:48
	 */
	public static Bitmap getBitmapFromByte(byte[] temp) {
		if (temp != null) {
			Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
			return bitmap;
		} else {
			return null;
		}
	}

	// ------------------ContactSyncOf-----------------------

	/**
	 * 此方法描述的是：压缩文件
	 * 
	 * @param str
	 *            字符串
	 * @param sd
	 *            压缩存放路径
	 * @throws IOException
	 * @author: 谢康林
	 * @version: 2012-5-7 下午05:20:31
	 */
	public static boolean compress(String str, String sd) throws IOException {
		if (str == null || str.length() == 0) {
			return false;
		}
		FileOutputStream os = new FileOutputStream(sd);
		GZIPOutputStream gz = new GZIPOutputStream(os);
		byte[] strbt = str.getBytes("utf-8");
		byte[] buffer = new byte[1024];
		int len = 0;
		int strlen = strbt.length;
		while (len < strlen) {
			if (strlen < buffer.length) {
				gz.write(strbt, 0, strlen);
			} else {
				if ((strlen - len) < buffer.length) {
					gz.write(strbt, len, strlen - len);
				} else {
					gz.write(strbt, len, buffer.length);
				}
			}
			len += buffer.length;
		}
		gz.close();
		os.close();
		return true;
	}

	/**
	 * 此方法描述的是：将联系人数据库中图片二进制转换为图片文件
	 * 
	 * @param image
	 *            图片二进制
	 * @param filePaht
	 *            保存路径
	 * @author: 谢康林
	 * @version: 2012-5-9 下午02:48:34
	 */
	public static void doByteImageFile(byte[] image, String filePaht) {
		OutputStream os = null;
		try {
			os = new FileOutputStream(filePaht);
			byte[] img = image;
			byte[] buffer = new byte[1024];
			int len = 0;
			int strlen = img.length;
			while (len < strlen) {
				if (strlen < buffer.length) {
					os.write(img, 0, strlen);
				} else {
					if ((strlen - len) < buffer.length) {
						os.write(img, len, strlen - len);
					} else {
						os.write(img, len, buffer.length);
					}
				}
				len += buffer.length;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 此方法描述的是：下载服务器上的图片
	 * 
	 * @param fileUrl
	 *            下载路径
	 * @param filePaht
	 *            保存路径
	 * @return
	 * @author: 谢康林
	 * @version: 2012-5-9 下午02:53:09
	 */
	@SuppressWarnings("deprecation")
	public static int doUrlImageFile(String fileUrl, String filePaht, boolean isWifi) {
		int filelength = 0;
		HttpURLConnection connection = null;
		InputStream is = null;
		OutputStream os = null;
		try {
			URL url = new URL(fileUrl);
			String proxyHost = android.net.Proxy.getDefaultHost();
			if (isWifi) {
				connection = (HttpURLConnection) url.openConnection();
			} else if (proxyHost != null) {// 如果是wap方式，要加网关
				java.net.Proxy p = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(
						android.net.Proxy.getDefaultHost(), android.net.Proxy.getDefaultPort()));
				connection = (HttpURLConnection) url.openConnection(p);
			} else {
				connection = (HttpURLConnection) url.openConnection();
			}
			connection.connect();
			is = connection.getInputStream();

			byte[] bs = new byte[1024];
			int len;
			os = new FileOutputStream(filePaht);
			while ((len = is.read(bs)) != -1) {
				os.write(bs, 0, len);
				filelength += len;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			connection.disconnect();
		}
		return filelength;
	}

	/**
	 * 此方法描述的是：解析下载文件
	 * 
	 * @param sd
	 *            文件的存放路径
	 * @return
	 * @throws IOException
	 * @author: 谢康林
	 * @version: 2012-5-7 下午05:19:43
	 */
	public static String uncompress(String sd) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		FileInputStream in = new FileInputStream(sd);
		GZIPInputStream gunzip = new GZIPInputStream(in);
		byte[] buffer = new byte[1024];
		int n;
		while ((n = gunzip.read(buffer)) >= 0) {
			out.write(buffer, 0, n);
		}
		gunzip.close();
		in.close();
		out.close();
		return out.toString("utf-8");
	}

	/**
	 * 此方法描述的是：下载联系人gz直接解析
	 * 
	 * @param fileUrl
	 *            下载路径
	 * @return
	 * @author: 谢康林
	 * @version: 2012-5-7 下午05:18:42
	 */
	@SuppressWarnings("deprecation")
	public static String downloadfile(String fileUrl, boolean isWifi) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		HttpURLConnection connection = null;
		InputStream is = null;
		GZIPInputStream gunzip = null;
		try {
			URL url = new URL(fileUrl);
			String proxyHost = android.net.Proxy.getDefaultHost();
			if (isWifi) {
				connection = (HttpURLConnection) url.openConnection();
			} else if (proxyHost != null) {// 如果是wap方式，要加网关
				java.net.Proxy p = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(
						android.net.Proxy.getDefaultHost(), android.net.Proxy.getDefaultPort()));
				connection = (HttpURLConnection) url.openConnection(p);
			} else {
				connection = (HttpURLConnection) url.openConnection();
			}
			connection.connect();
			is = connection.getInputStream();
			gunzip = new GZIPInputStream(is);
			byte[] bs = new byte[1024];
			int len;
			while ((len = gunzip.read(bs)) != -1) {
				out.write(bs, 0, len);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			gunzip.close();
			out.close();
			is.close();
			connection.disconnect();
		}
		return out.toString("utf-8");
	}

	/**
	 * 此方法描述的是：下载联系人gz
	 * 
	 * @param fileUrl
	 *            下载路径
	 * @param filePaht
	 *            下载保存路径
	 * @return
	 * @author: 谢康林
	 * @version: 2012-5-7 下午05:18:42
	 */
	@SuppressWarnings("deprecation")
	public static int downloadfile(String fileUrl, String filePaht, boolean isWifi) {
		int filelength = 0;
		HttpURLConnection connection = null;
		InputStream is = null;
		OutputStream os = null;
		try {
			URL url = new URL(fileUrl);
			String proxyHost = android.net.Proxy.getDefaultHost();
			if (isWifi) {
				connection = (HttpURLConnection) url.openConnection();
			} else if (proxyHost != null) {// 如果是wap方式，要加网关
				java.net.Proxy p = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(
						android.net.Proxy.getDefaultHost(), android.net.Proxy.getDefaultPort()));
				connection = (HttpURLConnection) url.openConnection(p);
			} else {
				connection = (HttpURLConnection) url.openConnection();
			}
			connection.connect();
			is = connection.getInputStream();

			byte[] bs = new byte[1024];
			int len;
			os = new FileOutputStream(filePaht);
			while ((len = is.read(bs)) != -1) {
				os.write(bs, 0, len);
				filelength += len;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			connection.disconnect();
		}
		return filelength;
	}

	// -----------------------共用----------------------
	/**
	 * 删除指定目录下指定文件
	 * 
	 * @param path
	 *            ：String 目录名
	 * @param fileName
	 *            ：String 文件名
	 */
	public static boolean deleteFile(String path, String fileName, Context context) {
		boolean misDle = false;
		if (isExistsSDcard()) {
			File directory = android.os.Environment.getExternalStorageDirectory();
			directory = new File(path);
			File file = new File(directory, fileName);
			if ((file != null) && file.delete()) {
				misDle = true;
			}
		} else {
			File directory = android.os.Environment.getDataDirectory();
			directory = new File(path);
			File file = new File(directory, fileName);
			if ((file != null) && file.delete()) {
				misDle = true;
			}
		}
		return misDle;
	}

	/**
	 * 此方法描述的是：创建目录
	 * 
	 * @param dirName
	 *            需要创建的子目录
	 * @return
	 * @author: 谢康林
	 * @version: 2012-5-10 下午04:39:22
	 */
	public static boolean mkdir(String dirName) {
		boolean ismkdir = false;
		if ((dirName != null) && !dirName.equals("")) {
			File directory = android.os.Environment.getExternalStorageDirectory();
			directory = new File(directory, "/AboutYX/UserHeaderCache");
			if (directory.exists()) {
				ismkdir = true;
			} else {
				String[] dir = dirName.split("/");
				int lenth = dir.length;
				for (int i = 0; i < lenth; i++) {
					if (!"".equals(dir[i])) {
						directory = new File(directory, dir[i]);
						if (!directory.exists()) {
							boolean ismk = directory.mkdir();
							if (i + 1 == lenth) {
								ismkdir = ismk;
							}
						}
					}
				}
			}
		}
		return ismkdir;
	}

	/**
	 * 此方法描述的是：判断sd卡状态
	 * 
	 * @return
	 * @author: 谢康林
	 * @version: 2012-5-3 下午12:30:22
	 */
	public static boolean isExistsSDcard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	public static String post(boolean isWifi, String actionUrl, Map<String, File> files, boolean proxy)
			throws IOException {
		String jString = "";
		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FROM_DATA = "multipart/form-data";
		String CHARSET = "UTF-8";
		HttpURLConnection conn = null;
		try {
			URL uri = new URL(actionUrl);
			String proxyHost = android.net.Proxy.getDefaultHost();
			if (isWifi) {
				conn = (HttpURLConnection) uri.openConnection();
			} else if (proxyHost != null) {// 如果是wap方式，要加网关
				java.net.Proxy p = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(
						android.net.Proxy.getDefaultHost(), android.net.Proxy.getDefaultPort()));
				if (proxy)
					conn = (HttpURLConnection) uri.openConnection();
				else
					conn = (HttpURLConnection) uri.openConnection(p);
			} else {
				conn = (HttpURLConnection) uri.openConnection();
			}
			conn.setReadTimeout(10 * 1000); // 缓存的最长时间
			conn.setConnectTimeout(10 * 1000);
			conn.setDoInput(true);// 允许输入
			conn.setDoOutput(true);// 允许输出
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Accept-Language", "zh-CN");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);
			conn.setRequestProperty(
					"Accept",
					"image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash, application/msword, application/vnd.ms-excel, application/vnd.ms-powerpoint, */*");
			conn.setRequestProperty(
					"User-Agent",
					"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
			conn.connect();

			DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
			// 发送文件数据
			if (files != null) {
				for (Map.Entry<String, File> file : files.entrySet()) {
					StringBuilder sb1 = new StringBuilder();
					sb1.append(PREFIX);
					sb1.append(BOUNDARY);
					sb1.append(LINEND);
					sb1.append("Content-Disposition: form-data; name=\"" + file.getKey() + "\"; filename=\""
							+ file.getValue().getName() + "\"" + LINEND);
					sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
					sb1.append(LINEND);
					outStream.write(sb1.toString().getBytes());

					InputStream is = new FileInputStream(file.getValue());
					byte[] buffer = new byte[1024];
					int len = 0;
					while ((len = is.read(buffer)) != -1) {
						outStream.write(buffer, 0, len);
					}

					is.close();
					outStream.write(LINEND.getBytes());
				}
			}

			// 请求结束标志
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
			outStream.write(end_data);
			outStream.flush();
			// 得到响应码
			int res = conn.getResponseCode();
			InputStream in = null;
			if (res == 200) {
				in = conn.getInputStream();
				jString = convertStreamToString(in);
			} else {
				if (posti == 0) {
					posti++;
					post(isWifi, actionUrl, files, true);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.disconnect();
		}

		return jString;
	}

	public static String convertStreamToString(InputStream is) {
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length = -1;
			while ((length = is.read(buffer)) != -1) {
				stream.write(buffer, 0, length);
			}
			stream.flush();
			stream.close();
			is.close();
			return stream.toString();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}

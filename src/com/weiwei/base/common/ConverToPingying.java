package com.weiwei.base.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

import com.weiwei.base.application.VsApplication;

/**
 * 拼音转换类
 */
public class ConverToPingying {

	public static ConverToPingying py;

	public static ConverToPingying getInstance() {
		if (py == null) {
			py = new ConverToPingying();
		}
		return py;
	}

	private Properties properties;
	private boolean isLoadResource = false;

	public void clearProperties() {
		if (properties != null) {
			properties.clear();
			isLoadResource = false;
			System.gc();
		}
	}

	/**
	 * 输入汉字，转失汉字结果集 returnStr[0]首字母 returnStr[1]首字母数字 returnStr[2]全拼 returnStr[3]全拼数字
	 * 
	 * @param chines
	 * @return
	 * @author: xiaozhenhua
	 * @data:2011-1-31 下午5:30:43
	 */
	public String[] converToPingYingAndNumber(String name) {
		if (!isLoadResource) {
			loadResource();
		}
		String[] returnStr = new String[4];
		StringBuilder pingYingHead = new StringBuilder(); // 首字母
		StringBuilder pingYingHeadNumber = new StringBuilder(); // 首字母数字
		StringBuilder pingYing = new StringBuilder(); // 全拼
		StringBuilder pingYingNumber = new StringBuilder(); // 全拼数字
		name = replaceString(name);
		char[] charName = name.toCharArray();
		if (charName != null) {
			for (char c : charName) {
				String str = nameConverToPingying(c);
				try {
					char headChar = conversionHeadUppercase(str.toCharArray()[0]);
					if (str.length() > 1) {
						pingYing.append(headChar + str.substring(1, str.length()));
					} else {
						pingYing.append(headChar);
					}
					pingYingHead.append(headChar);
				} catch (Exception e) {
					e.printStackTrace();
					// System.out.println("ERROR_NAME:"+name);
				}
			}
		}
		pingYingHeadNumber.append(converEnToNumber(pingYingHead.toString()));
		pingYingNumber.append(converEnToNumber(pingYing.toString()));

		returnStr[0] = pingYingHead.toString();
		returnStr[1] = pingYingHeadNumber.toString();
		returnStr[2] = pingYing.toString();
		returnStr[3] = pingYingNumber.toString();

		// System.out.println("NAME:"+name);
		// System.out.println("PY_HEAD:"+pingYingHead.toString());
		// System.out.println("PY_HEAD_NUMBER:"+pingYingHeadNumber.toString());
		// System.out.println("PY:"+pingYing.toString());
		// System.out.println("PY_NUMBER:"+pingYingNumber.toString());
		pingYingHead.delete(0, pingYingHead.length());
		pingYingHeadNumber.delete(0, pingYingHeadNumber.length());
		pingYing.delete(0, pingYing.length());
		pingYingNumber.delete(0, pingYingNumber.length());
		return returnStr;
	}

	/**
	 * 转换过程
	 * 
	 * @param name
	 * @return
	 * @author: xiaozhenhua
	 * @data:2011-2-1 下午4:16:22
	 */
	private String nameConverToPingying(char name) {
		String str4 = "";
		String str1 = null;
		// String str2 = null;
		// String str3 = null;
		try {
			if (isChinese(String.valueOf(name))) {
				int i = name;
				str1 = Integer.toHexString(i).toUpperCase();
				str4 = properties.getProperty(str1);
				// str3 = isValidRecord(str2) ? str2 : null;

				// if(null != str3){
				// int n = str2.indexOf("(");
				// int j = str2.lastIndexOf(")");
				// str4 = str2.substring(n + "(".length(), j);
				// }
				// if(!str4.equals("")){
				// if(str4.indexOf(",")>0){
				// str4 = str4.split(",")[0];
				// }
				// str4 = str4.substring(0, str4.length() -1);
				// }
			} else {
				str4 = String.valueOf(name);
			}
		} finally {
			str1 = null;
			// str2 = null;
			// str3 = null;
		}
		return str4;
	}

	public boolean isChinese(String str) {
		return Pattern.compile("[\\u4e00-\\u9fa5]").matcher(str).find();
	}

	private void loadResource() {
		if (properties == null) {
			// System.out.println("CREATE PROPERTIES ... ");
			properties = new Properties();
		}
		try {
			// System.out.println("PROPERTIES LOAD RESOURCE ... ");
			java.io.InputStream inputStream = VsApplication.getContext().getAssets()
					.open("unicode_to_hanyu_pinyin.propertes");
			properties.load(inputStream);
			if (inputStream != null) {
				inputStream.close();
				inputStream = null;
			}
			isLoadResource = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			isLoadResource = false;
			// System.out.println("1-PROPERTIES LOAD RESOURCE ERROR:"+e.toString());
		} catch (IOException e) {
			e.printStackTrace();
			isLoadResource = false;
			// System.out.println("2-PROPERTIES LOAD RESOURCE ERROR:"+e.toString());
		}
	}

	/*
	 * private boolean isValidRecord(String paramString) { return (null != paramString) &&
	 * (!paramString.equals("(none0)")) && (paramString.startsWith("(")) && (paramString.endsWith(")")); }
	 */

	private char conversionHeadUppercase(char c) {
		switch (c) {
		case 'a':
			return 'A';
		case 'b':
			return 'B';
		case 'c':
			return 'C';
		case 'd':
			return 'D';
		case 'e':
			return 'E';
		case 'f':
			return 'F';
		case 'g':
			return 'G';
		case 'h':
			return 'H';
		case 'i':
			return 'I';
		case 'j':
			return 'J';
		case 'k':
			return 'K';
		case 'l':
			return 'L';
		case 'm':
			return 'M';
		case 'n':
			return 'N';
		case 'o':
			return 'O';
		case 'p':
			return 'P';
		case 'q':
			return 'Q';
		case 'r':
			return 'R';
		case 's':
			return 'S';
		case 't':
			return 'T';
		case 'u':
			return 'U';
		case 'v':
			return 'V';
		case 'w':
			return 'W';
		case 'x':
			return 'X';
		case 'y':
			return 'Y';
		case 'z':
			return 'Z';
		default:
			return c;
		}
	}

	/**
	 * 
	 * 将输入的拼音转成数字
	 * 
	 * @param str
	 * @return
	 * @author: 肖珍华
	 * @version: 2012-4-18 下午04:24:18
	 */
	public String converEnToNumber(String str) {
		char[] chars = str.toCharArray();
		StringBuffer sbf = new StringBuffer();
		for (char c : chars) {
			sbf.append(getOneNumFromAlpha(c));
		}
		return sbf.toString();
	}

	/**
	 * 
	 * 将字母转换成数字
	 * 
	 * @param firstAlpha
	 * @return
	 * @author: 肖珍华
	 * @version: 2012-4-16 下午04:38:19
	 */
	public char getOneNumFromAlpha(char firstAlpha) {
		switch (firstAlpha) {
		case 'a':
		case 'b':
		case 'c':
		case 'A':
		case 'B':
		case 'C':
			return '2';
		case 'd':
		case 'e':
		case 'f':
		case 'D':
		case 'E':
		case 'F':
			return '3';
		case 'g':
		case 'h':
		case 'i':
		case 'G':
		case 'H':
		case 'I':
			return '4';
		case 'j':
		case 'k':
		case 'l':
		case 'J':
		case 'K':
		case 'L':
			return '5';
		case 'm':
		case 'n':
		case 'o':
		case 'M':
		case 'N':
		case 'O':
			return '6';
		case 'p':
		case 'q':
		case 'r':
		case 's':
		case 'P':
		case 'Q':
		case 'R':
		case 'S':
			return '7';
		case 't':
		case 'u':
		case 'v':
		case 'T':
		case 'U':
		case 'V':
			return '8';
		case 'w':
		case 'x':
		case 'y':
		case 'z':
		case 'W':
		case 'X':
		case 'Y':
		case 'Z':
			return '9';
		default:
			return firstAlpha;
		}
	}

	/**
	 * 首字母是否有英文
	 * 
	 * @param str
	 * @return
	 */
	public boolean isEng(String str) {
		if (str != null && str.length() > 0) {
			return str.charAt(0) >= 0x0000 && str.charAt(0) <= 0x00ff;
		} else {
			return false;
		}
	}

	/**
	 * 获得汉语拼音首字母
	 * 
	 * @param str
	 * @return
	 * @author: xiaozhenhua
	 * @data:2012-8-7 上午9:39:12
	 */
	public String getAlpha(String str) {
		if (str == null) {
			return "#";
		}
		if (str.trim().length() == 0) {
			return "#";
		}
		char c = str.trim().substring(0, 1).charAt(0);
		// 正则表达式，判断首字母是否是英文字母
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase();
		} else {
			return "#";
		}
	}

	/**
	 * 替换掉中文标点
	 * 
	 * @param chines
	 * @return
	 */
	public static String replaceString(String chines) {
		return chines.replace("《", "").replace("》", "").replace("！", "").replace("￥", "").replace("【", "")
				.replace("】", "").replace("（", "").replace("）", "").replace("－", "").replace("；", "").replace("：", "")
				.replace("”", "").replace("“", "").replace("。", "").replace("，", "").replace("、", "").replace("？", "")
				.replace(" ", "").replace("-", "").replace("*", "").replace("…", "").replace(",", "");
	}
}

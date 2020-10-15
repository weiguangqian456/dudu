package com.weiwei.base.common;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;

import com.weiwei.base.dataprovider.DfineAction;

/**
 * 归属地名字查找工具
 * 
 * @author Administrator
 */
public class VsLocalNameFinder {
	private static ArrayList<NationalTable> ntiCodeList;
	private static ArrayList<AreaTable> strCodeList;
	private static ArrayList<NumHeadItem> numHeadItemList;
	private static ArrayList<NumItem> numItemList;
	private static String currentNumHead;
	public static HashMap<String, ArrayList<NumItem>> map = new HashMap<String, ArrayList<NumItem>>();

	/**
	 * 根据电话号码查找归属地名称
	 * 
	 * @param phoneNum
	 *            电话号码
	 * @param isAreaCode
	 *            是否为区号
	 * @return 归属地名称
	 */
	public static synchronized String findLocalName(String phoneNum, boolean isAreaCode, Context context) {
		try {
			if (phoneNum == null || phoneNum.length() < 3) {
				return "";
			}
			phoneNum = formatFreeCallNum(phoneNum);
			if (phoneNum == null) {
				return "";
			}
			// CustomLog.v(TAG, "====phoneNum==:" + phoneNum.substring(0, 5));
			// System.out.println("find:"+phoneNum+" local name");
			if (strCodeList == null) {
				strCodeList = new ArrayList<AreaTable>(500);
				ntiCodeList = new ArrayList<NationalTable>(300);
				numHeadItemList = new ArrayList<NumHeadItem>(30);
				numItemList = new ArrayList<NumItem>(10000);
				initData(context);
			}

			if (phoneNum.startsWith("+86")) {
				phoneNum = phoneNum.substring(3);
			} else if (phoneNum.startsWith("86")) {
				phoneNum = phoneNum.substring(2);
			}

			if (phoneNum.startsWith("0")) { // 处理区号
				if (phoneNum.startsWith("00")) {
					String pString = interNational(phoneNum);
					if (pString == "" || pString.equals("")) {
						if (phoneNum.length() >= 5) {
							String phoneNation = phoneNum.substring(0, 5);
							if (phoneNation.equals("00852")) {
								return "中国香港";
							} else if (phoneNation.equals("00853")) {
								return "中国澳门";
							} else if (phoneNation.equals("00886")) {
								return "中国台湾";
							}
						} else {
							return pString;
						}
					} else {
						// 国际区号处理
						return pString;
					}
				} else

				if (phoneNum.startsWith("010") || phoneNum.startsWith("02")) {
					try {
						// 为手机号码
						short head = (short) Integer.parseInt(phoneNum.substring(1, 3));
						int idx = -1;
						for (int i = 0; i < strCodeList.size(); i++) {
							if (strCodeList.get(i).code == head) {
								idx = i;
								break;
							}
						}
						if (idx == -1) {
							return "";
						}
						// 二分查找
						// short num = (short)
						// Integer.parseInt(phoneNum.substring(1, 3));
						// int pos = binarySearch(num, head);

						String locaName = strCodeList.get(idx).name;
						short locaCode = strCodeList.get(idx).code;
						if (isAreaCode) {
							return "" + locaCode;
						} else {
							return locaName;
						}

					} catch (Exception e) {
					}
				} else if (phoneNum.startsWith("03") || phoneNum.startsWith("07") || phoneNum.startsWith("05")
						|| phoneNum.startsWith("06") || phoneNum.startsWith("04") || phoneNum.startsWith("08")
						|| phoneNum.startsWith("09")) {
					try {
						//
						short head = (short) Integer.parseInt(phoneNum.substring(1, 4));
						int idx = -1;
						for (int i = 0; i < strCodeList.size(); i++) {
							if (strCodeList.get(i).code == head) {
								idx = i;
								break;
							}
						}
						if (idx == -1) {
							return "";
						}
						// 二分查找
						// short num = (short)
						// Integer.parseInt(phoneNum.substring(1, 4));
						// int pos = binarySearch(num, head);

						String locaName = strCodeList.get(idx).name;
						return locaName;
					} catch (Exception e) {
					}
				} else {
					// 为手机号码
					if (phoneNum.length() < 7) {
						return "";
					}
					try {
						// 为手机号码
						short head = (short) Integer.parseInt(phoneNum.substring(1, 4));
						int idx = -1;
						for (int i = 0; i < numHeadItemList.size(); i++) {
							if (numHeadItemList.get(i).headName == head) {
								idx = i;
								break;
							}
						}
						if (idx == -1) {
							return "";
						}

						// TODO 还可以边读取，边查找
						readPro(numHeadItemList.get(idx).startPos, numHeadItemList.get(idx).length, head, context);
						// 二分查找
						short num = (short) Integer.parseInt(phoneNum.substring(4, 8));

						// System.out.println(num);

						int pos = binarySearch(num, head);

						// System.out.println(numItemList.get(pos).num);

						String locaName = strCodeList.get(numItemList.get(pos).index).name;
						short locaCode = strCodeList.get(numItemList.get(pos).index).code;
						// System.out.println(locaName+","+locaCode);
						if (isAreaCode) {
							return "" + locaCode;
						} else {
							return locaName;
						}

					} catch (Exception e) {
						// e.printStackTrace();
					}
				}
			} else { // 为手机号码
				if (phoneNum.length() < 7) {
					return "";
				}
				if (phoneNum.startsWith("17909") || phoneNum.startsWith("17951") || phoneNum.startsWith("17911")
						|| phoneNum.startsWith("12593")) {
					phoneNum = phoneNum.substring(5);
				}
				try {
					// 为手机号码
					short head = (short) Integer.parseInt(phoneNum.substring(0, 3));
					int idx = -1;
					for (int i = 0; i < numHeadItemList.size(); i++) {
						if (numHeadItemList.get(i).headName == head) {
							// CustomLog.i("other","phone num = "+phoneNum+" head = "+head);
							idx = i;
							break;
						}
					}
					if (idx == -1) {
						return "";
					}

					// TODO 还可以边读取，边查找
					readPro(numHeadItemList.get(idx).startPos, numHeadItemList.get(idx).length, head, context);
					// 二分查找
					short num = (short) Integer.parseInt(phoneNum.substring(3, 7));

					int pos = binarySearch(num, head);
					// int index = numItemList.get(pos).index;
					for (int i = 0; i < numItemList.size(); i++) {
					}
					String locaName = strCodeList.get(numItemList.get(pos).index).name;
					short locaCode = strCodeList.get(numItemList.get(pos).index).code;
					// CustomLog.i("findLocalName","phone head = "+head+" phone middle num = "
					// +num+" pos = " + pos+" local name = "
					// +locaName+" localcode =" +locaCode);
					if (isAreaCode) {
						return "" + locaCode;
					} else {
						return locaName;
					}
				} catch (Exception e) {
					// e.printStackTrace();
				}
			}
			return "";
		} catch (Exception e) {
			return "";
		}
	}

	// Type a[],const Type& x,int n
	private static int binarySearch(int num, short head) {

		if (map.containsKey(String.valueOf(head))) {
			// numItemList.clear();
			numItemList = map.get(String.valueOf(head));
			// CustomLog.i("map","map contain head "+head+" and the list has "+numItemList.size()+" elem");
		} else {
			// CustomLog.i("map","the map can't find the haed "+head+" list");
			return -1;
		}

		int left = 0;
		int right = numItemList.size() - 1;
		while (left <= right) {
			int middle = (left + right) / 2;
			short n = numItemList.get(middle).num;
			if (num == n)
				return middle;
			if (num > n) {
				left = middle + 1;
			} else {
				right = middle - 1;
			}
		}
		return right;
	}

	private static class AreaTable {
		short code;
		String name;
	}

	private static class NationalTable {
		short code;
		String name;
	}

	private static class NumHeadItem {
		short headName;
		int startPos;
		int length;
	}

	private static class NumItem {
		short num;
		short index;
	}

	private static short readShort(InputStream is) throws IOException {
		byte[] b = new byte[2];
		is.read(b);
		short value = (short) (((int) (b[0] & 0xff)) | (((int) (b[1] & 0xff)) << 8));
		return value;
	}

	private static int readInt(InputStream is) throws IOException {
		byte[] b = new byte[4];
		is.read(b);
		int value = (int) (((int) (b[0] & 0xff)) | (((int) (b[1] & 0xff)) << 8) | (((int) (b[2] & 0xff)) << 16) | (((int) (b[3] & 0xff)) << 24));
		return value;
	}

	private static String readStr(InputStream is) throws IOException {
		int len = is.read();
		if (len < 0)
			return "";

		byte[] b = new byte[len];
		is.read(b);
		String s = new String(b, "UTF-16");
		return s;
	}

	private static void initData(Context context) {
		try {
			FileInputStream in = new FileInputStream(DfineAction.mWldhFilePath + "PhoneNumberQuery.dat");
			readIndexData(in);
			readAreaData(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void readIndexData(InputStream is) throws IOException {
		// 号码段数量
		int num = readShort(is);
		for (int i = 0; i < num; i++) {
			NumHeadItem item = new NumHeadItem();
			item.headName = readShort(is);
			item.startPos = readInt(is);
			item.length = readInt(is);
			// CustomLog.i("numHeadItemList","numHeadItemList headName = "
			// +item.headName+ " startPos = " +item.startPos+
			// " length = "+item.length);
			/*
			 * CustomLog.v(TAG, "numHeadItemList headName = " + item.headName + " startPos = " + item.startPos +
			 * " length = " + item.length);
			 */
			numHeadItemList.add(item);
		}
	}

	private static void readAreaData(InputStream is) throws IOException {
		int count = readShort(is);
		for (int i = 0; i < count; i++) {
			AreaTable areaLine = new AreaTable();
			short code = readShort(is);
			String name = readStr(is);
			areaLine.code = code;
			areaLine.name = name;
			// CustomLog.i("strCodeList","strCodeList name = " +name+ " code = "
			// +
			// code);
			// CustomLog.v(TAG, "strCodeList name = " + name + " code = " +
			// code);
			strCodeList.add(areaLine);
		}

		count = readShort(is);
		for (int i = 0; i < count; i++) {
			NationalTable nationalTable = new NationalTable();
			short code = readShort(is);
			String name = readStr(is);
			nationalTable.code = code;
			nationalTable.name = name;
			// CustomLog.v(TAG, "ntiCodeList name = " + name + " code = " +
			// code);
			ntiCodeList.add(nationalTable);
		}
	}

	private static void readPro(int start, int len, short headName, Context context) throws IOException {

		int i = 0;
		if (currentNumHead != null && currentNumHead.equals(Short.toString(headName)) && numItemList.size() > 0) {
			return;
		}
        if("400".equals(Short.toString(headName))){
        	return ;
        }

		if (map.containsKey(String.valueOf(headName))) {
			// ArrayList<NumItem> temp = map.get(String.valueOf(headName));
			// int count = temp.size();
			// CustomCustomLog.i("key = "+"===============>hashmap contain has inserted head "
			// +headName+" and the list size = "+count);
			return;
		}

		FileInputStream in = null;

		ArrayList<NumItem> tem = new ArrayList<NumItem>(100);
		// numItemList.clear();
		byte[] data = null;
		try {
			data = new byte[len];
			in = new FileInputStream(DfineAction.mWldhFilePath + "PhoneNumberQuery.dat");
			in.skip(start);
			in.read(data);
			ByteArrayInputStream is = new ByteArrayInputStream(data);
			byte[] b = new byte[3];
			while (is.read(b, 0, b.length) != -1) {
				int v = ((int) b[0] & 0xff) | (((int) b[1] & 0xff) << 8) | (((int) b[2] & 0xff) << 16);
				short phoneNum = (short) (v >> 10);
				short code = (short) (v & 0x3FF);

				NumItem item = new NumItem();
				item.num = phoneNum;
				item.index = code;
				i++;
				tem.add(item);
			}
			is.close();
			// CustomLog.i("other","-------------->hashmap insert head "
			// +headName+" and add  "+i+" numItem");
			map.put(String.valueOf(headName), tem);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			data = null;
			if (in != null) {
				in.close();
			}
		}
	}

	/**
	 * xkl
	 * 
	 * @param phoneNum
	 * @return
	 */
	public static String interNational(String phoneNum) {
		String locaName = "";
		try {
			// 为国际号码
			short head = (short) Integer.parseInt(phoneNum.substring(0, 6));
			int idx = -1;
			for (int i = 0; i < ntiCodeList.size(); i++) {
				if (ntiCodeList.get(i).code == head) {
					idx = i;
					break;
				}
			}
			// System.out.println("idx:"+idx+"====head:"+head);
			if (idx == -1) {
				head = (short) Integer.parseInt(phoneNum.substring(0, 5));
				for (int i = 0; i < ntiCodeList.size(); i++) {
					if (ntiCodeList.get(i).code == head) {
						idx = i;
						break;
					}
				}
				if (idx == -1) {
					head = (short) Integer.parseInt(phoneNum.substring(0, 4));
					for (int i = 0; i < ntiCodeList.size(); i++) {
						if (ntiCodeList.get(i).code == head) {
							idx = i;
							break;
						}
					}
					if (idx == -1) {
						head = (short) Integer.parseInt(phoneNum.substring(0, 3));
						for (int i = 0; i < ntiCodeList.size(); i++) {
							if (ntiCodeList.get(i).code == head) {
								idx = i;
								break;
							}

						}
						if (idx == -1) {
							return "";
						}
					}
				}
			}

			locaName = ntiCodeList.get(idx).name;
			if (locaName.lastIndexOf("(") != -1) {
				locaName = locaName.substring(0, locaName.lastIndexOf("("));
			} else {
				locaName = locaName.substring(0, locaName.lastIndexOf("（"));
			}
		} catch (Exception e) {
			try {
				short head = (short) Integer.parseInt(phoneNum.substring(0, 5));
				int idx = -1;
				for (int i = 0; i < ntiCodeList.size(); i++) {
					if (ntiCodeList.get(i).code == head) {
						idx = i;
						break;
					}
				}
				locaName = ntiCodeList.get(idx).name;
				if (locaName.lastIndexOf("(") != -1) {
					locaName = locaName.substring(0, locaName.lastIndexOf("("));
				} else {
					locaName = locaName.substring(0, locaName.lastIndexOf("（"));
				}
			} catch (Exception e2) {
				try {
					short head = (short) Integer.parseInt(phoneNum.substring(0, 4));
					int idx = -1;
					for (int i = 0; i < ntiCodeList.size(); i++) {
						if (ntiCodeList.get(i).code == head) {
							idx = i;
							break;
						}
					}
					locaName = ntiCodeList.get(idx).name;
					if (locaName.lastIndexOf("(") != -1) {
						locaName = locaName.substring(0, locaName.lastIndexOf("("));
					} else {
						locaName = locaName.substring(0, locaName.lastIndexOf("（"));
					}
				} catch (Exception e3) {
					try {
						short head = (short) Integer.parseInt(phoneNum.substring(0, 3));
						int idx = -1;
						for (int i = 0; i < ntiCodeList.size(); i++) {
							if (ntiCodeList.get(i).code == head) {
								idx = i;
								break;
							}
						}
						locaName = ntiCodeList.get(idx).name;
						if (locaName.lastIndexOf("(") != -1) {
							locaName = locaName.substring(0, locaName.lastIndexOf("("));
						} else {
							locaName = locaName.substring(0, locaName.lastIndexOf("（"));
						}
					} catch (Exception e4) {
						// TODO: handle exception
					}
				}
			}

		}
		return locaName;
	}

	/**
	 * 判断是否是国际电话
	 * 
	 * @param phoneNum
	 * @return
	 * @author: 龙小龙
	 * @version: 2012-3-6 下午03:37:33
	 */
	public static boolean isITT(String phoneNum) {
		if (phoneNum == null || phoneNum.equals("")) {
			return false;
		}

		if (phoneNum.startsWith("00") && !phoneNum.startsWith("0086")) {
			return true;
		}

		if (phoneNum.startsWith("+") && !phoneNum.startsWith("+86")) {
			return true;
		}
		return false;
	}

	/**
	 * 1.以18/15/13/14开头的号码只能是11位，且以1开头后面不是数字3,4,5,8，那么就是错误号码 2.仅以一个0开头座机号规则10<=X<=12 3.以两个0开头的号码就是国际电话
	 * 4.以+86/12593/17951/17909/17911的结合规则1和2，其中+86后面的座机号码和手机号码均可以带0也可以不带0
	 * 
	 * @param num
	 * @return 格式后的电话 null标示号码不匹配
	 */
	public static String formatFreeCallNum(String num) {

		String oldString = num.replace("-", "");
		oldString = oldString.replace("+", "");

		if (oldString.length() < 3) {
			return null;
		}

		if (oldString.matches("^86.*"))
			oldString = oldString.substring("86".length());
		if (oldString.matches("^12593.*|17951.*|17909.*|17911.*")) {
			oldString = oldString.substring("12593".length());
		}

		if (oldString.matches("^(0){1}[1-9]*$")) {
			if (oldString.matches("[0-9]{8,12}")) {
				return oldString;
			} else {
				return null;
			}
		}
		if (oldString.startsWith("1")) {
			if (oldString.matches("^13.*|14.*|15.*|18.*")) {
				if (oldString.matches("[0-9]{11}")) {
					return oldString;
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
		return oldString;

	}
}
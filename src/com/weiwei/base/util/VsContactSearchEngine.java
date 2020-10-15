package com.weiwei.base.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.weiwei.base.item.FilterColorItem;
import com.weiwei.base.item.FilterItem;

/**
 * 联系人搜索工具
 * 
 * @author Administrator
 * 
 */
public class VsContactSearchEngine {

	private final String GBK_ENCODE = "gbk";

	private final String[] KeyMapNums = { "abc",// 2
			"def",// 3
			"ghi",// 4
			"jkl",// 5
			"mno",// 6
			"pqrs",// 7
			"tuv",// 8
			"wxyz"// 9
	};

	// // 声母表 b p m f d t n l g k h j q x zh ch sh r z c s y w
	// private final static String[] LetterTableA = { "b", "p", "m", "f", "d",
	// "t", "n", "l", "g", "k", "h", "j", "q", "x", "zh", "ch", "sh", "r",
	// "z", "c", "s", "y", "w", };
	//
	// // 韵母表 a o e i u v ai ei ui ao ou iu ie ve er an en in un vn ang eng ing
	// ong
	// private final static String[] LetterTableB = { "a", "o", "e", "i", "u",
	// "v", "ai", "ei", "ui", "ao", "ou", "iu", "ie", "ve", "er", "an",
	// "en", "in", "un", "vn", "ang", "eng", "ing", "ong", };

	// 共396行
	private final String[] py = { "a", "ai", "an", "ang", "ao", "ba", "bai", "ban", "bang", "bao", "bei", "ben",
			"beng", "bi", "bian", "biao", "bie", "bin", "bing", "bo", "bu", "ca", "cai", "can", "cang", "cao", "ce",
			"ceng", "cha", "chai", "chan", "chang", "chao", "che", "chen", "cheng", "chi", "chong", "chou", "chu",
			"chuai", "chuan", "chuang", "chui", "chun", "chuo", "ci", "cong", "cou", "cu", "cuan", "cui", "cun", "cuo",
			"da", "dai", "dan", "dang", "dao", "de", "deng", "di", "dian", "diao", "die", "ding", "diu", "dong", "dou",
			"du", "duan", "dui", "dun", "duo", "e", "en", "er", "fa", "fan", "fang", "fei", "fen", "feng", "fo", "fou",
			"fu", "ga", "gai", "gan", "gang", "gao", "ge", "gei", "gen", "geng", "gong", "gou", "gu", "gua", "guai",
			"guan", "guang", "gui", "gun", "guo", "ha", "hai", "han", "hang", "hao", "he", "hei", "hen", "heng",
			"hong", "hou", "hu", "hua", "huai", "huan", "huang", "hui", "hun", "huo", "ji", "jia", "jian", "jiang",
			"jiao", "jie", "jin", "jing", "jiong", "jiu", "ju", "juan", "jue", "jun", "ka", "kai", "kan", "kang",
			"kao", "ke", "ken", "keng", "kong", "kou", "ku", "kua", "kuai", "kuan", "kuang", "kui", "kun", "kuo", "la",
			"lai", "lan", "lang", "lao", "le", "lei", "leng", "li", "lia", "lian", "liang", "liao", "lie", "lin",
			"ling", "liu", "long", "lou", "lu", "lv", "luan", "lue", "lun", "luo", "ma", "mai", "man", "mang", "mao",
			"me", "mei", "men", "meng", "mi", "mian", "miao", "mie", "min", "ming", "miu", "mo", "mou", "mu", "na",
			"nai", "nan", "nang", "nao", "ne", "nei", "nen", "neng", "ni", "nian", "niang", "niao", "nie", "nin",
			"ning", "niu", "nong", "nu", "nv", "nuan", "nue", "nuo", "o", "ou", "pa", "pai", "pan", "pang", "pao",
			"pei", "pen", "peng", "pi", "pian", "piao", "pie", "pin", "ping", "po", "pu", "qi", "qia", "qian", "qiang",
			"qiao", "qie", "qin", "qing", "qiong", "qiu", "qu", "quan", "que", "qun", "ran", "rang", "rao", "re",
			"ren", "reng", "ri", "rong", "rou", "ru", "ruan", "rui", "run", "ruo", "sa", "sai", "san", "sang", "sao",
			"se", "sen", "seng", "sha", "shai", "shan", "shang", "shao", "she", "shen", "sheng", "shi", "shou", "shu",
			"shua", "shuai", "shuan", "shuang", "shui", "shun", "shuo", "si", "song", "sou", "su", "suan", "sui",
			"sun", "suo", "ta", "tai", "tan", "tang", "tao", "te", "teng", "ti", "tian", "tiao", "tie", "ting", "tong",
			"tou", "tu", "tuan", "tui", "tun", "tuo", "wa", "wai", "wan", "wang", "wei", "wen", "weng", "wo", "wu",
			"xi", "xia", "xian", "xiang", "xiao", "xie", "xin", "xing", "xiong", "xiu", "xu", "xuan", "xue", "xun",
			"ya", "yan", "yang", "yao", "ye", "yi", "yin", "ying", "yo", "yong", "you", "yu", "yuan", "yue", "yun",
			"za", "zai", "zan", "zang", "zao", "ze", "zei", "zen", "zeng", "zha", "zhai", "zhan", "zhang", "zhao",
			"zhe", "zhen", "zheng", "zhi", "zhong", "zhou", "zhu", "zhua", "zhuai", "zhuan", "zhuang", "zhui", "zhun",
			"zhuo", "zi", "zong", "zou", "zu", "zuan", "zui", "zun", "zuo" };

	private final int py_code[] = { -20319, -20317, -20304, -20295, -20292, -20283, -20265, -20257, -20242, -20230,
			-20051, -20036, -20032, -20026, -20002, -19990, -19986, -19982, -19976, -19805, -19784, -19775, -19774,
			-19763, -19756, -19751, -19746, -19741, -19739, -19728, -19725, -19715, -19540, -19531, -19525, -19515,
			-19500, -19484, -19479, -19467, -19289, -19288, -19281, -19275, -19270, -19263, -19261, -19249, -19243,
			-19242, -19238, -19235, -19227, -19224, -19218, -19212, -19038, -19023, -19018, -19006, -19003, -18996,
			-18977, -18961, -18952, -18783, -18774, -18773, -18763, -18756, -18741, -18735, -18731, -18722, -18710,
			-18697, -18696, -18526, -18518, -18501, -18490, -18478, -18463, -18448, -18447, -18446, -18239, -18237,
			-18231, -18220, -18211, -18201, -18184, -18183, -18181, -18012, -17997, -17988, -17970, -17964, -17961,
			-17950, -17947, -17931, -17928, -17922, -17759, -17752, -17733, -17730, -17721, -17703, -17701, -17697,
			-17692, -17683, -17676, -17496, -17487, -17482, -17468, -17454, -17433, -17427, -17417, -17202, -17185,
			-16983, -16970, -16942, -16915, -16733, -16708, -16706, -16689, -16664, -16657, -16647, -16474, -16470,
			-16465, -16459, -16452, -16448, -16433, -16429, -16427, -16423, -16419, -16412, -16407, -16403, -16401,
			-16393, -16220, -16216, -16212, -16205, -16202, -16187, -16180, -16171, -16169, -16158, -16155, -15959,
			-15958, -15944, -15933, -15920, -15915, -15903, -15889, -15878, -15707, -15701, -15681, -15667, -15661,
			-15659, -15652, -15640, -15631, -15625, -15454, -15448, -15436, -15435, -15419, -15416, -15408, -15394,
			-15385, -15377, -15375, -15369, -15363, -15362, -15183, -15180, -15165, -15158, -15153, -15150, -15149,
			-15144, -15143, -15141, -15140, -15139, -15128, -15121, -15119, -15117, -15110, -15109, -14941, -14937,
			-14933, -14930, -14929, -14928, -14926, -14922, -14921, -14914, -14908, -14902, -14894, -14889, -14882,
			-14873, -14871, -14857, -14678, -14674, -14670, -14668, -14663, -14654, -14645, -14630, -14594, -14429,
			-14407, -14399, -14384, -14379, -14368, -14355, -14353, -14345, -14170, -14159, -14151, -14149, -14145,
			-14140, -14137, -14135, -14125, -14123, -14122, -14112, -14109, -14099, -14097, -14094, -14092, -14090,
			-14087, -14083, -13917, -13914, -13910, -13907, -13906, -13905, -13896, -13894, -13878, -13870, -13859,
			-13847, -13831, -13658, -13611, -13601, -13406, -13404, -13400, -13398, -13395, -13391, -13387, -13383,
			-13367, -13359, -13356, -13343, -13340, -13329, -13326, -13318, -13147, -13138, -13120, -13107, -13096,
			-13095, -13091, -13076, -13068, -13063, -13060, -12888, -12875, -12871, -12860, -12858, -12852, -12849,
			-12838, -12831, -12829, -12812, -12802, -12607, -12597, -12594, -12585, -12556, -12359, -12346, -12320,
			-12300, -12120, -12099, -12089, -12074, -12067, -12058, -12039, -11867, -11861, -11847, -11831, -11798,
			-11781, -11604, -11589, -11536, -11358, -11340, -11339, -11324, -11303, -11097, -11077, -11067, -11055,
			-11052, -11045, -11041, -11038, -11024, -11020, -11019, -11018, -11014, -10838, -10832, -10815, -10800,
			-10790, -10780, -10764, -10587, -10544, -10533, -10519, -10331, -10329, -10328, -10322, -10315, -10309,
			-10307, -10296, -10281, -10274, -10270, -10262, -10260, -10256, -10254 };

	public VsContactSearchEngine() {

	}

	public class TInt {
		public int value = -1;
	}

	/**
	 * 汉字转拼音
	 * 
	 * @param name
	 */
	public ArrayList<String> convertToPinYin(String name) {
		ArrayList<String> charPinYinList = new ArrayList<String>();
		for (int i = 0; i < name.length(); i++) {
			try {
				String pinyin = getCharPinYin(name.charAt(i));
				if (pinyin.length() > 0) {
					charPinYinList.add(pinyin);
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return charPinYinList;
	}

	// numStr, 0, charPinYinList, 0, sb, aPos --------> 8,0,[bai qing],0,"",aPos
	private void matchKeyNumMapList(String numStr, int numPos, ArrayList<String> charPinYinList, int pos2,
			StringBuffer sb, TInt startPos) {
		// numStr, 0, charPinYinList, 0, sb, aPos
		// 33,0,[di di],0,"",-1

		// 第二次：33,1,[di di],1,d,0
		if (numPos >= numStr.length() || pos2 >= charPinYinList.size()) {
			return;
		}

		char ch = numStr.charAt(numPos); // 3(ascii:33)
		int index = ch - '2'; // 1(ascii:33-32 = 1)
		// KeyMapNums:8
		if (index >= 0 && index < KeyMapNums.length) { // 成立
			boolean match = false;
			for (int j = 0; j < KeyMapNums[index].length(); j++) {
				if (KeyMapNums[index].charAt(j) == charPinYinList.get(pos2) // d
																			// ==
																			// d
						.charAt(0)) {
					match = true;
					if (startPos.value == -1) {
						startPos.value = pos2; // pos2:0,startPos:0
					}
					pos2++;// 1
					sb.append(KeyMapNums[index].charAt(j));
					break;
				}
			}
			if (match) {
				numPos++; // 1
				matchKeyNumMapList(numStr, numPos, charPinYinList, pos2, sb, startPos);// 33,1,[di
																						// di],1,d,0
			} else {// 匹配不成功
				if (pos2 != 0) {// 不是第一个，不需要在继续匹配
					return;
				}
				pos2++;
				matchKeyNumMapList(numStr, numPos, charPinYinList, pos2, sb, startPos);
			}
		}
	}

	public FilterItem filter(String numStr, String phoneNum, String name) {

		// numStr:7
		// phoneNum:7954688
		// name:柏庆　Long Xiaolong

		int pos = phoneNum.indexOf(numStr); // pos 0
		if (pos != -1) {// 号码匹配成功
			FilterItem filterItem = new FilterItem();
			filterItem.type = 0;
			// 前面部分
			if (pos > 0) {
				FilterColorItem item = new FilterColorItem();
				item.content = phoneNum.substring(0, pos); // item.content 1
				filterItem.colorItemList.add(item);
			}

			// 匹配成功部分,设置高亮
			{
				FilterColorItem item = new FilterColorItem();
				item.content = numStr;
				item.hightlight = true;
				filterItem.colorItemList.add(item);
			}

			// 后面部分
			// pos = 0
			// pos = 7954688-7 = 6 成立
			if (pos < phoneNum.length() - numStr.length()) {
				FilterColorItem item = new FilterColorItem();
				item.content = phoneNum.substring(pos + numStr.length()); // 6749255
				filterItem.colorItemList.add(item);
			}
			return filterItem;
		}

		/* 第一个版本不进行首字母匹配 */
		ArrayList<String> charPinYinList = convertToPinYin(name); // 弟弟
		// 匹配首字母
		if (numStr.length() <= charPinYinList.size()) { // 3 3 成立
			StringBuffer sb = new StringBuffer();
			TInt aPos = new TInt();
			matchKeyNumMapList(numStr, 0, charPinYinList, 0, sb, aPos);
			String matchHeadStr = sb.toString();
			sb = null;

			// 要把这去
			// matchHeadStr = "lxlo";
			if (matchHeadStr.length() < numStr.length()) {
				// 匹配失败
				return null;
			}
			// 匹配成功

			int headIndex = 0;
			FilterItem filterItem = new FilterItem();
			filterItem.type = 1;
			for (int i = 0; i < charPinYinList.size(); i++) { // 会循环输出：long xiao
																// long
				String s = charPinYinList.get(i); // long
				if (s.length() > 1) {
					s = s.substring(0, 1).toUpperCase() + s.substring(1); // Long
				} else {
					s = s.substring(0, 1).toUpperCase(); // L
				}
				if (i < aPos.value) {
					FilterColorItem item = new FilterColorItem();
					item.content = s;
					filterItem.colorItemList.add(item);
				} else {

					if (headIndex < matchHeadStr.length()) {
						{
							FilterColorItem item = new FilterColorItem();
							item.content = s.substring(0, 1);
							item.hightlight = true;
							filterItem.colorItemList.add(item);
						}

						{
							FilterColorItem item = new FilterColorItem();
							item.content = s.substring(1);
							filterItem.colorItemList.add(item);
						}

					} else {
						FilterColorItem item = new FilterColorItem();
						item.content = s;
						filterItem.colorItemList.add(item);
					}
					headIndex++;
				}
			}
			return filterItem;
		}

		return null;
	}

	/**
	 * 得到一个unicode字符的gbk编码值
	 * 
	 * @param ch
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private int getCharCode(char ch) throws UnsupportedEncodingException {
		int l = 0;
		String tmp = ch + "";
		byte[] data = tmp.getBytes(GBK_ENCODE);
		if (data.length >= 2) {
			l = ((int) (data[0] & 0xff) << 8) | ((int) data[1] & 0xff);
			l = l - 65536;
		} else {
			l = data[0];
		}
		return l;
	}

	/**
	 * 得到一个unicode字符的拼音
	 * 
	 * @param ch
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String getCharPinYin(char ch) throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		int ret_code = getCharCode(ch);

		if (ret_code > 0 && ret_code < 160) {
			sb.append(ch);
			return sb.toString();
		}
		if (ret_code < -20319 || ret_code > -10247) {

		} else {
			int i = 0;
			for (i = 396 - 1; i >= 0; i--) {
				if (py_code[i] <= ret_code) {
					break;
				}
			}
			sb.append(py[i]);
		}
		return sb.toString();
	}

}

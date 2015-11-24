package com.ben.mc.bean.util;

public class ShortNameUtil {

	public final static String makeShortName(String str, String tag) {
		return str.substring(str.lastIndexOf(tag) + 1);
	}

	public final static String makeShortName(String str) {
		return makeShortName(str, ".");
	}

	public final static String makeLowerHumpNameShortName(String str) {
		return objectHumpNameLower(makeShortName(str, "."));
	}

	/***
	 * 驼峰名字
	 * @param one
	 * @return
	 */
	public final static String objectHumpNameLower(String one) {
		char[] v1 = one.toCharArray();
		if (v1[0] >= 'A' && v1[0] <= 'Z') {
			v1[0] += 32;
		}
		return new String(v1);
	}

	/***
	 * 驼峰名字
	 * @param one
	 * @return
	 */
	public final static String objectHumpNameUpper(String one) {
		char[] v1 = one.toCharArray();
		if (v1[0] >= 'a' && v1[0] <= 'z') {
			v1[0] -= 32;
		}
		return new String(v1);
	}

	public final static boolean objectHumpNameEquals(String firstBit, String two) {
		if (firstBit == two) {
			return true;
		}
		int n = 0;
		if ((n = firstBit.length()) != two.length())
			return false;
		char v1[] = firstBit.toCharArray();
		char v2[] = two.toCharArray();
		if (v1[0] >= 'A' && v1[0] <= 'Z') {
			v1[0] += 32;
		}
		int i = 0;
		while (i != n) {
			if (v1[i] != v2[i])
				return false;
			i++;
		}
		return true;
	}

	public static void main(String[] args) {
		System.err.println(objectHumpNameEquals("menaaa", "menaaa"));
	}
}

package cn.csdb.msis.util;

import org.apache.logging.log4j.util.Strings;

/**
 * @Author jinbao
 * @Date 2019/8/2 10:11
 * @Description 参数校验工具类
 **/
public class ParamUtil {

	/**
	 * @param stringParams 被检验参数列表
	 * @return 当所有参数非""且非null返回true 否则返回false
	 */
	public static boolean stringParamsNoBlank(String... stringParams) {
		for (String stringParam : stringParams) {
			if (Strings.isBlank(stringParam)) {
				return false;
			}
		}
		return true;
	}

	public static String numberString(String s) {
		if (Strings.isBlank(s)) {
			return "0";
		}
		return s;
	}

}

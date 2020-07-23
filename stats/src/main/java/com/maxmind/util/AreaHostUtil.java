package com.maxmind.util;

import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AreaHostUtil {

	public final static String GEOIPCITY = System.getProperty("user.dir") + "/GeoLiteCity.dat";
	public final static Map<String, String> mapping = new HashMap<String, String>();
	public static LookupService cl = null;

	static {


		mapping.put("CN_01", "安徽");
		mapping.put("CN_02", "浙江");
		mapping.put("CN_03", "江西");
		mapping.put("CN_04", "江苏");
		mapping.put("CN_05", "吉林");
		mapping.put("CN_06", "青海");
		mapping.put("CN_07", "福建");
		mapping.put("CN_08", "黑龙江");
		mapping.put("CN_09", "河南");
		mapping.put("CN_10", "河北");
		mapping.put("CN_11", "湖南");
		mapping.put("CN_12", "湖北");
		mapping.put("CN_13", "新疆");
		mapping.put("CN_14", "西藏");
		mapping.put("CN_15", "甘肃");
		mapping.put("CN_16", "广西");
		mapping.put("CN_18", "贵州");
		mapping.put("CN_19", "辽宁");
		mapping.put("CN_20", "内蒙古");
		mapping.put("CN_21", "宁夏");
		mapping.put("CN_22", "北京");
		mapping.put("CN_23", "上海");
		mapping.put("CN_24", "山西");
		mapping.put("CN_25", "山东");
		mapping.put("CN_26", "陕西");
		mapping.put("CN_27", "unknown");
		mapping.put("CN_28", "天津");
		mapping.put("CN_29", "云南");
		mapping.put("CN_30", "广东");
		mapping.put("CN_31", "海南");
		mapping.put("CN_32", "四川");
		mapping.put("CN_33", "重庆");
		mapping.put("TW", "台湾");
		mapping.put("HK", "香港");
		mapping.put("MO", "澳门");
		initial();
	}


	public static void initial() {
		if (cl == null) {
			try {
				cl = new LookupService(GEOIPCITY, LookupService.GEOIP_MEMORY_CACHE);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	public static String getDomain(String host) {
		Location location = AreaHostUtil.cl.getLocation(host);
		if (location == null) return null;
		return location.countryCode;
	}

	public static String getCNArea(String host) {
		Location location = AreaHostUtil.cl.getLocation(host);
		if (location == null) return null;
		String area = null;
		if (location.countryCode.equals("CN")) {
			area = AreaHostUtil.mapping.get(location.countryCode + "_" + location.region);
			if (area == null) {
				area = "未知的";
			}

		}
		if (location.countryCode.equals("TW") || location.countryCode.equals("HK") || location.countryCode.equals("MO")) {
			area = mapping.get(location.countryCode);
		}
		location = null;
		return area;
	}

}

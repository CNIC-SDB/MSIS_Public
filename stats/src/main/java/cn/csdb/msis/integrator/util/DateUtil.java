package cn.csdb.msis.integrator.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @Author jinbao
 * @Date 2019/7/26 16:30
 * @Description 时间获取
 **/
public class DateUtil {
	private static SimpleDateFormat formatHH = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
	private static SimpleDateFormat formatdd = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat formatMM = new SimpleDateFormat("yyyy-MM");
	private static Calendar c;

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
		c = Calendar.getInstance();
	}
	/**
	 * 获取上月份
	 *
	 * @return [2019, 4]
	 */
	public static Integer[] lastMonth() {
		c.setTime(new Date());
		c.add(Calendar.MONTH, -1);
		Date time = c.getTime();
		String[] split = formatMM.format(time).split("-");
		Integer year = new Integer(split[0]);
		Integer month = new Integer(split[1]);
		return new Integer[]{year, month};
	}

	/**
	 * 几小时之前的日期
	 *
	 * @param hours 小时数
	 * @return
	 */
	public static String aFewHoursAgo(int hours) {
		c.setTime(new Date());
		c.add(Calendar.HOUR, -hours);
		return formatHH.format(c.getTime());
	}

	/**
	 * 获取今天的日期
	 *
	 * @return [2019, 7, 31]
	 */
	public static Integer[] today() {
		String[] split = formatdd.format(new Date()).split("-");
		Integer year = new Integer(split[0]);
		Integer month = new Integer(split[1]);
		Integer day = new Integer(split[2]);
		return new Integer[]{year, month, day};
	}

	/**
	 * 时间字符串转时间数组
	 *
	 * @param string "20190412"
	 * @return [2019, 4, 12]
	 */
	public static Integer[] string2Time(String string) {
		Integer time = new Integer(string);
		Integer year = time / 10000;
		Integer month = time % 10000 / 100;
		Integer day = time % 100;
		return new Integer[]{year, month, day};
	}

	public static Date getDateOfDayStats(String source) throws Exception {
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		return df.parse(source);
	}
}

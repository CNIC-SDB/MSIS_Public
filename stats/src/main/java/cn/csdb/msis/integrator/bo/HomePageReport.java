package cn.csdb.msis.integrator.bo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 全站累计统计结果
 */
@Getter
@Setter
public class HomePageReport {

	private String time;

	/**
	 * 独立IP
	 */
	private BigDecimal visitors;

	/**
	 * 访问页面数
	 */
	private BigDecimal pages;

	/**
	 * 总访问人次
	 */
	private BigDecimal visits;

	/**
	 * 总点击量
	 */
	private BigDecimal hits;

	/**
	 * 总下载量
	 */
	private BigDecimal bandwidth;

	/**
	 * 每日访问人数
	 */
	private List<List<BigDecimal>> visitDaily;

	/**
	 * 每日下载量 eg:[[[20190101],[12354213]],[[20190102],[223122]]]
	 */
	private List<List<BigDecimal>> bandwidthDaily;

	/**
	 * 用户来源地址(国别) top10
	 */
	private Map<String, BigDecimal> ipDomain;

	/**
	 * 服务正常率统计
	 */
	private Map<String, Integer> serviceRate;

	/**
	 * 非浏览器访问页面数
	 */
//	private BigDecimal robotPages;

	/**
	 * 非浏览器访问文件数
	 */
//	private BigDecimal robotHits;

	/**
	 * 非浏览器访问流量
	 */
//	private BigDecimal robotBandwidth;

	/**
	 * 访问次数/独立IP
	 */
//	private BigDecimal visitsPerVisitor;

	/**
	 * 访问页面数/访问次数
	 */
//	private BigDecimal pagesPerVisit;

	/**
	 * 文件数/访问次数
	 */
//	private BigDecimal hitsPerVisit;

	/**
	 * 访问流量/访问次数
	 */
//	private BigDecimal bandwidthPerVisit;

	/**
	 * 访问量前十
	 */
	private List<Map<String, Object>> top10Visit;

	/**
	 * 下载量前十
	 */
	private List<Map<String, Object>> top10Bandwidth;


	/**
	 * 服务正常率前十
	 */
	private List<Map<String, Object>> top10Status;
}

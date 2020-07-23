package cn.csdb.msis.integrator.bo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 数据库维度统计结果
 */
@Getter
@Setter
public class DataStatReport {
    /**
     * 时间
     */
    private String time;

    /**
     * 组别id
     */
    private Integer id;

    /**
     * 组别名
     */
    private String name;
    /**
     * 总访问人次
     */
    private BigDecimal sumTotalVisits;
    /**
     * 独立IP
     */
    private BigDecimal sumTotalUnique;

    /**
     * 访问页面数
     */
    private BigDecimal pages;

    /**
     * 点击量
     */
    private BigDecimal hits;

    /**
     * 下载量
     */
    private BigDecimal bandwidth;

    /**
     * 非浏览器访问页面数
     */
    private BigDecimal robotPages;

    /**
     * 非浏览器访问文件数
     */
    private BigDecimal robotHits;

    /**
     * 非浏览器访问流量
     */
    private BigDecimal robotBandwidth;

    /**
     * 每日访问人数
     */
    private List<List<BigDecimal>> visitDaily;

    /**
     * 每日下载量
     */
    private List<List<BigDecimal>> downDaily;


    /**
     * 近一个月服务正常率
     */
    private List<List<BigDecimal>> normalRate;


    /**
     * 数据来源
     */
    private List<List<String>> DataFrom;

    /**
     * 每月统计IP
     */
    private List<List<BigDecimal>> IPMonth;

    /**
     * 每月统计访问人次
     */
    private List<List<BigDecimal>> VisitMonth;

    /**
     * 每月统计页面访问数
     */
    private List<List<BigDecimal>> PageMonth;

    /**
     * 每月统计请求数
     */
    private List<List<BigDecimal>> HitsMonth;

    /**
     * 每月统计下载量
     */
    private List<List<BigDecimal>> BandwidthMonth;

    /**
     * 每日统计访问人次
     */
    private List<List<BigDecimal>> VisitDay;
    /**
     * 每日统计网页访问数
     */
    private List<List<BigDecimal>> PageDay;
    /**
     * 每日统计请求数
     */
    private List<List<BigDecimal>> HitsDay;
    /**
     * 每日统计下载量
     */
    private List<List<BigDecimal>> BandwidthDay;

    /**
     * 每月表格数据
     */
    List<Map<String, Object>> rowsMonth;

    /**
     * 每日表格数据
     */
    List<Map<String, Object>> rowsDay;


    /**
     * 文件类型列表
     */
    private List<Map<String, Object>> fileTypeList;

    /**
     * 站点状态
     */
    private List<Map<String, Object>> siteStatusList;

    /**
     * 组别服务正常率
     */
    private List<List<BigDecimal>> groupRate;

    /**
     * 组别服务正常率列表
     */
    private List<Map<String, Object>> groupRateList;


    /**
     * 国家地区统计
     */
    private List<Map<String, Object>> countryCount;

}

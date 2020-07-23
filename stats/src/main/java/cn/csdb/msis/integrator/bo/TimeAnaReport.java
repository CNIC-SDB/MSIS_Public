package cn.csdb.msis.integrator.bo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 数据库维度时段分析
 */
@Getter
@Setter
public class TimeAnaReport {
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

}

package cn.csdb.msis.integrator.bo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author jinbao
 * @Date 2019/7/31 16:01
 * @Description 微对象用于映射bootstrapTable表格字段
 **/
@Getter
@Setter
public class Field implements Serializable {
	private static final long serialVersionUID = 3947086136680186638L;
	private Integer id;
	private String name;

	// 访问人次
	private BigDecimal visit = new BigDecimal(0);
	// 访问页面数量
	private BigDecimal page = new BigDecimal(0);
	// 访问下载量
	private BigDecimal bandwidth = new BigDecimal(0);
	// 独立IP访问人
	private BigDecimal visitor = new BigDecimal(0);
	// 机器访问人次
	private BigDecimal robotVisit = new BigDecimal(0);
	// 机器访问页面数量
	private BigDecimal robotPage = new BigDecimal(0);
	// 机器访问下载量
	private BigDecimal robotBandwidth = new BigDecimal(0);


	// 列表统计-科教用户统计
	// 科技网
	private BigDecimal ac = new BigDecimal(0);
	// 教育网
	private BigDecimal edu = new BigDecimal(0);
	// 科技网+教育网
	private BigDecimal both = new BigDecimal(0);
	//其他网
	private BigDecimal other = new BigDecimal(0);
	// 比率
	private BigDecimal rate = new BigDecimal(0);
}

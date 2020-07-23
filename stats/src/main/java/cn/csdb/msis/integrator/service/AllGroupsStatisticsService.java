package cn.csdb.msis.integrator.service;

import cn.csdb.msis.integrator.bo.Field;
import cn.csdb.msis.integrator.bo.GroupRankReport;
import cn.csdb.msis.stats.repository.AwAchostRepository;
import cn.csdb.msis.stats.repository.GroupsRepository;
import cn.csdb.msis.util.ParamUtil;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.Tuple;
import java.math.BigDecimal;
import java.util.*;

/**
 * @Author jinbao
 * @Date 2019/7/31 11:32
 * @Description 列表统计
 **/
@Service
public class AllGroupsStatisticsService {

	@Resource
	private GroupsRepository groupsDao;

	@Resource
	private AwAchostRepository awAchostDao;

	public GroupRankReport groupsRank(String beginTime, String endTime, String stringSort) {
		GroupRankReport groupRankReport = new GroupRankReport();
		Sort sort = new Sort(Sort.Direction.DESC, stringSort);
		Map<String, Object> chartData = new HashMap<>();
		List<String> chartYAxisName = new LinkedList<>();
		List<BigDecimal> chartSeries = new LinkedList<>();

		List<Field> tableData = new LinkedList<>();
		List<Tuple> groupsRank = groupsDao.groupsRank(Integer.valueOf(beginTime), Integer.valueOf(endTime), sort);
		for (Tuple tuple : groupsRank) {
			String gid = tuple.get("gid").toString();
			String gname = tuple.get("gname").toString();
			BigDecimal visit = new BigDecimal(tuple.get("visit").toString());
			BigDecimal page = new BigDecimal(tuple.get("page").toString());
			BigDecimal bandwidth = new BigDecimal(tuple.get("bandwidth").toString());

			// 表格数据
			Field f = new Field();
			f.setId(Integer.valueOf(gid));
			f.setName(gname);
			f.setVisit(visit);
			f.setPage(page);
			f.setBandwidth(bandwidth);
			tableData.add(f);

			// 图表数据
			chartYAxisName.add(gname);
			if ("visit".equalsIgnoreCase(stringSort)) {
				chartSeries.add(visit);
			} else if ("bandwidth".equalsIgnoreCase(stringSort)) {
				chartSeries.add(bandwidth);
			}


		}
		chartData.put("chartYAxisName", chartYAxisName);
		chartData.put("chartSeries", chartSeries);
		groupRankReport.setChartData(chartData);
		groupRankReport.setTableData(tableData);
		return groupRankReport;
	}

	public GroupRankReport groupsHostRank(String beginTime, String endTime, String statistic, String stringSort, String rankType) {
		List<GroupRankReport.UserRankReport> listUserRankReport = new ArrayList<>(10);
		GroupRankReport groupRankReport = new GroupRankReport();
		List<Tuple> tuples = awAchostDao.groupHostRank(Integer.valueOf(beginTime), Integer.valueOf(endTime));
		Map<String, GroupRankReport.UserRankReport> map = new LinkedHashMap<>();
		// 赋值基础属性
		userRankBasicField(tuples, map);
		// 计算其他属性
		userRankComputedField(listUserRankReport, map);
		// 用户数据排序
		userSort(statistic, stringSort, rankType, listUserRankReport);

		// 格式化数据适应页面
		Map<String, Object> chartData = new HashMap<>();
		List<String> chartYAxisName = new LinkedList<>();
		List<BigDecimal> chartSeries = new LinkedList<>();
		List<Field> tableData = new LinkedList<>();
		formatUserRankData(statistic, stringSort, rankType, listUserRankReport, chartYAxisName, chartSeries, tableData);

		chartData.put("chartYAxisName", chartYAxisName);
		chartData.put("chartSeries", chartSeries);
		groupRankReport.setChartData(chartData);
		groupRankReport.setTableData(tableData);
		return groupRankReport;
	}

	/**
	 * 根据查询参数格式化数据
	 *
	 * @param statistic          统计维度
	 * @param stringSort         排序字段
	 * @param rankType           排序字段
	 * @param listUserRankReport 排序结果集
	 * @param chartYAxisName     图表Y轴名称
	 * @param chartSeries        图表对应数据
	 * @param tableData          表格数据
	 */
	private void formatUserRankData(String statistic, String stringSort, String rankType, List<GroupRankReport.UserRankReport> listUserRankReport, List<String> chartYAxisName, List<BigDecimal> chartSeries, List<Field> tableData) {
		boolean rankIsValue = "value".equalsIgnoreCase(stringSort);
		for (GroupRankReport.UserRankReport userRankReport : listUserRankReport) {
			Field f = new Field();
			f.setId(userRankReport.getGroupId());
			f.setName(userRankReport.getGroupName());
			if ("page".equalsIgnoreCase(statistic)) {
				f.setAc(userRankReport.getAcPages());
				f.setEdu(userRankReport.getEduPages());
				f.setBoth(userRankReport.getBothPages());
				f.setOther(userRankReport.getOtherPages());
				if ("ac".equalsIgnoreCase(rankType)) {
					if (rankIsValue) {
						chartSeries.add(userRankReport.getAcPages());
					} else {
						chartSeries.add(userRankReport.getAcPagePercent());
					}
					f.setRate(userRankReport.getAcPagePercent());
				} else if ("edu".equalsIgnoreCase(rankType)) {
					if (rankIsValue) {
						chartSeries.add(userRankReport.getEduPages());
					} else {
						chartSeries.add(userRankReport.getEduPagePercent());
					}
					f.setRate(userRankReport.getEduPagePercent());
				} else {
					if (rankIsValue) {
						chartSeries.add(userRankReport.getBothPages());
					} else {
						chartSeries.add(userRankReport.getBothPagePercent());
					}
					f.setRate(userRankReport.getBothPagePercent());
				}
			} else {
				f.setAc(userRankReport.getAcBandwidth());
				f.setEdu(userRankReport.getEduBandwidth());
				f.setBoth(userRankReport.getBothBandwidth());
				f.setOther(userRankReport.getOtherBandwidth());
				if ("ac".equalsIgnoreCase(rankType)) {
					if (rankIsValue) {
						chartSeries.add(userRankReport.getAcBandwidth());
					} else {
						chartSeries.add(userRankReport.getAcBandwidthPercent());
					}
					f.setRate(userRankReport.getAcBandwidthPercent());
				} else if ("edu".equalsIgnoreCase(rankType)) {
					if (rankIsValue) {
						chartSeries.add(userRankReport.getEduBandwidth());
					} else {
						chartSeries.add(userRankReport.getEduBandwidthPercent());
					}
					f.setRate(userRankReport.getEduBandwidthPercent());
				} else {
					if (rankIsValue) {
						chartSeries.add(userRankReport.getBothBandwidth());
					} else {
						chartSeries.add(userRankReport.getBothBandwidthPercent());
					}
					f.setRate(userRankReport.getBothBandwidthPercent());
				}
			}

			chartYAxisName.add(userRankReport.getGroupName());

			tableData.add(f);
		}
	}

	/**
	 * 对结果集排序
	 *
	 * @param statistic          统计的维度字段 page:页面访问量 bandwidth:下载量
	 * @param stringSort         排序的字段 value:绝对值 rate:比率
	 * @param rankType           排序字段 ac:科技网 edu:教育网 other:其他网
	 * @param listUserRankReport 排序结果集
	 */
	private void userSort(String statistic, String stringSort, String rankType, List<GroupRankReport.UserRankReport> listUserRankReport) {
		if ("value".equalsIgnoreCase(stringSort)) {

			if ("page".equalsIgnoreCase(statistic)) {

				if ("ac".equalsIgnoreCase(rankType)) {
					listUserRankReport.sort(new GroupRankReport().new URAcPagesComparator());
				} else if ("edu".equalsIgnoreCase(rankType)) {
					listUserRankReport.sort(new GroupRankReport().new UREduPagesComparator());
				} else {
					listUserRankReport.sort(new GroupRankReport().new URBothPagesComparator());

				}
			} else {
				if ("ac".equalsIgnoreCase(rankType)) {
					listUserRankReport.sort(new GroupRankReport().new URAcBandwidthComparator());
				} else if ("edu".equalsIgnoreCase(rankType)) {
					listUserRankReport.sort(new GroupRankReport().new UREduBandwidthComparator());
				} else {
					listUserRankReport.sort(new GroupRankReport().new URBothBandwidthComparator());
				}
			}
		} else {

			if ("page".equalsIgnoreCase(statistic)) {

				if ("ac".equalsIgnoreCase(rankType)) {
					listUserRankReport.sort(new GroupRankReport().new URAcPagePercentComparator());
				} else if ("edu".equalsIgnoreCase(rankType)) {
					listUserRankReport.sort(new GroupRankReport().new UREduPagePercentComparator());
				} else {
					listUserRankReport.sort(new GroupRankReport().new URBothPagePercentComparator());

				}
			} else {
				if ("ac".equalsIgnoreCase(rankType)) {
					listUserRankReport.sort(new GroupRankReport().new URAcBandwidthPercentComparator());
				} else if ("edu".equalsIgnoreCase(rankType)) {
					listUserRankReport.sort(new GroupRankReport().new UREduBandwidthPercentComparator());
				} else {
					listUserRankReport.sort(new GroupRankReport().new URBothBandwidthPercentComparator());
				}
			}
		}
	}

	/**
	 * 查询结果集计算数据
	 *
	 * @param listUserRankReport 计算结果集
	 * @param map                映射实体结果集
	 */
	private void userRankComputedField(List<GroupRankReport.UserRankReport> listUserRankReport, Map<String, GroupRankReport.UserRankReport> map) {
		for (Map.Entry<String, GroupRankReport.UserRankReport> entry : map.entrySet()) {
			// 求和值
			GroupRankReport.UserRankReport y = entry.getValue();
			BigDecimal acBandwidth = y.getAcBandwidth();
			BigDecimal eduBandwidth = y.getEduBandwidth();
			BigDecimal bothBandwidth = acBandwidth.add(eduBandwidth);
			y.setBothBandwidth(bothBandwidth);

			BigDecimal acPage = y.getAcPages();
			BigDecimal eduPage = y.getEduPages();
			BigDecimal bothPage = acPage.add(eduPage);
			y.setBothPages(bothPage);

			BigDecimal allBandwidth = new BigDecimal(0);
			allBandwidth = bothBandwidth.add(y.getOtherBandwidth());
			BigDecimal allPage = new BigDecimal(0);
			allPage = bothPage.add(y.getOtherPages());

			// 所占百分比
			BigDecimal hundred = new BigDecimal(100);
			if (allBandwidth.longValue() != 0) {
				y.setAcBandwidthPercent(acBandwidth.multiply(hundred).divide(allBandwidth, 2, BigDecimal.ROUND_HALF_UP));
				y.setEduBandwidthPercent(eduBandwidth.multiply(hundred).divide(allBandwidth, 2, BigDecimal.ROUND_HALF_UP));
				y.setBothBandwidthPercent(bothBandwidth.multiply(hundred).divide(allBandwidth, 2, BigDecimal.ROUND_HALF_UP));
			}
			if (allPage.longValue() != 0) {
				y.setAcPagePercent(acPage.multiply(hundred).divide(allPage, 2, BigDecimal.ROUND_HALF_UP));
				y.setEduPagePercent(eduPage.multiply(hundred).divide(allPage, 2, BigDecimal.ROUND_HALF_UP));
				y.setBothPagePercent(bothPage.multiply(hundred).divide(allPage, 2, BigDecimal.ROUND_HALF_UP));
			}
			listUserRankReport.add(y);
		}
	}

	/**
	 * 原始结果集映射为实体集合
	 *
	 * @param tuples 原始查询结果集
	 * @param map    映射实体结果集
	 */
	private void userRankBasicField(List<Tuple> tuples, Map<String, GroupRankReport.UserRankReport> map) {
		for (Tuple tuple : tuples) {
			Object objHostType = tuple.get("hostType");
			if (objHostType == null) {
				break;
			}
			String stringHostType = objHostType.toString();
			String gid = tuple.get("gid").toString();
			String gname = tuple.get("gname").toString();
			BigDecimal page = new BigDecimal(ParamUtil.numberString(tuple.get("page").toString()));
			BigDecimal bandwidth = new BigDecimal(ParamUtil.numberString(tuple.get("bandwidth").toString()));

			GroupRankReport.UserRankReport userRankReport = map.get(gid);
			if (userRankReport == null) {
				userRankReport = new GroupRankReport().new UserRankReport();
				userRankReport.setGroupId(Integer.valueOf(gid));
				userRankReport.setGroupName(gname);
			}
			// 0:其他网用户 1:科技网用户 2:教育网用户
			if ("0".equalsIgnoreCase(stringHostType)) {
				userRankReport.setOtherBandwidth(bandwidth);
				userRankReport.setOtherPages(page);
			} else if ("1".equalsIgnoreCase(stringHostType)) {
				userRankReport.setAcBandwidth(bandwidth);
				userRankReport.setAcPages(page);
			} else {
				userRankReport.setEduBandwidth(bandwidth);
				userRankReport.setEduPages(page);
			}
			map.put(gid, userRankReport);
		}
	}
}

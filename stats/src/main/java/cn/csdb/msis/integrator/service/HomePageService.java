package cn.csdb.msis.integrator.service;

import cn.csdb.msis.integrator.bo.HomePageReport;
import cn.csdb.msis.integrator.util.DateUtil;
import cn.csdb.msis.stats.entity.Group;
import cn.csdb.msis.stats.repository.AwDayRepository;
import cn.csdb.msis.stats.repository.AwDomainRepository;
import cn.csdb.msis.stats.repository.GroupsRepository;
import cn.csdb.msis.stats.repository.PercentagesRepository;
import cn.csdb.msis.user.entity.User;
import com.maxmind.util.DomainUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.Tuple;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Collator;
import java.util.*;

/**
 * @Author jinbao
 * @Date 2019/7/22 16:31
 **/
@Service
public class HomePageService {


	@Resource
	AwDayRepository awDayDao;

	@Resource
	AwDomainRepository awDomainDao;

	@Resource
	GroupsRepository groupDao;

	@Resource
	PercentagesRepository percentagesDao;

	/**
	 * 获取首页数据信息
	 *
	 * @return
	 */
    @Cacheable(value = "oneDay", key = "#root.methodName")
    public HomePageReport homePage() {
		HomePageReport homePageReport = new HomePageReport();

		// 统计全站点历史累计数据 访问人次、访问页面数量、下载量
		allStatistic(homePageReport);

		// 全站点 每日访问量、下载量
        dayStatic(homePageReport);

		// 用户来源统计
		domainStatic(homePageReport);

		//服务正常率统计
		allServiceRate(homePageReport);

		// 访问量前十
		Integer[] lastMonth = visitTop10(homePageReport);

		// 下载量前十
		bandwidthTop10(homePageReport, lastMonth);

		// 服务正常率前十
		serviceRateTop10(homePageReport, lastMonth);

		return homePageReport;
	}

	private void serviceRateTop10(HomePageReport homePageReport, Integer[] lastMonth) {
		PageRequest pageRequestService = PageRequest.of(0, 10);
		List<Tuple> top10ServiceRate = percentagesDao.serviceRateOfMonth(lastMonth[0], lastMonth[1], pageRequestService);
		List<Map<String, Object>> listTop10ServiceRate = new LinkedList<>();
		for (Tuple tuple : top10ServiceRate) {
			Map<String, Object> map = new HashMap<>(16);
			String gname = tuple.get("gname").toString();
			BigDecimal gid = new BigDecimal(tuple.get("gid").toString());
			BigDecimal rate = new BigDecimal(tuple.get("rate").toString()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
			map.put("gname", gname);
			map.put("gid", gid);
			map.put("value", rate);
			listTop10ServiceRate.add(map);
		}
		homePageReport.setTop10Status(listTop10ServiceRate);
	}

	private void bandwidthTop10(HomePageReport homePageReport, Integer[] lastMonth) {
		PageRequest pageRequestBandwidth = PageRequest.of(0, 10, Sort.Direction.DESC, "bandwidth");
		List<Tuple> top10Bandwidth = awDayDao.top10BandwidthOfLastMonth(1, pageRequestBandwidth, lastMonth[0], lastMonth[1]);
		List<Map<String, Object>> listTop10Bandwidth = new ArrayList<>(10);
		for (Tuple tuple : top10Bandwidth) {
			HashMap<String, Object> map = new HashMap<>(16);
			String gname = tuple.get("gname").toString();
			String gid = tuple.get("gid").toString();
			String b = tuple.get("bandwidth") == null ? "0" : tuple.get("bandwidth").toString();
			BigDecimal bandwidth = new BigDecimal(b);
			map.put("gname", gname);
			map.put("gid", gid);
			map.put("value", bandwidth);
			listTop10Bandwidth.add(map);
		}
		homePageReport.setTop10Bandwidth(listTop10Bandwidth);
	}

	private Integer[] visitTop10(HomePageReport homePageReport) {
		Integer[] lastMonth = DateUtil.lastMonth();
		PageRequest pageRequestVisit = PageRequest.of(0, 10, Sort.Direction.DESC, "visits");
		List<Tuple> top10Visits = awDayDao.top10VisitsOfLastMonth(1, pageRequestVisit, lastMonth[0], lastMonth[1]);
		List<Map<String, Object>> listTop10Visits = new ArrayList<>(10);
		for (Tuple tuple : top10Visits) {
			HashMap<String, Object> map = new HashMap<>();
			String gname = tuple.get("gname").toString();
			String gid = tuple.get("gid").toString();
			String v = tuple.get("visits") == null ? "0" : tuple.get("visits").toString();
			BigDecimal visits = new BigDecimal(v);
			map.put("gname", gname);
			map.put("gid", gid);
			map.put("value", visits);
			listTop10Visits.add(map);
		}
		homePageReport.setTop10Visit(listTop10Visits);
		return lastMonth;
	}

	private void allServiceRate(HomePageReport homePageReport) {
		Integer[] today = DateUtil.today();
		List<Tuple> serviceRate = percentagesDao.serviceRateOfYear(today[0]);
		Map<String, Integer> mapServiceRate = new LinkedHashMap<>(16);
		int percents0_59 = 0;
		int percents60_69 = 0;
		int percents70_79 = 0;
		int percents90_99 = 0;
		int percents80_89 = 0;
		int percents100 = 0;
		for (Tuple tuple : serviceRate) {
			String rateString = tuple.get("rate") == null ? "0" : tuple.get("rate").toString();
			Double d = new Double(rateString);
			if (d < 60) {
				percents0_59++;
			} else if (d < 70) {
				percents60_69++;
			} else if (d < 80) {
				percents70_79++;
			} else if (d < 90) {
				percents80_89++;
			} else if (d < 100) {
				percents90_99++;
			} else {
				percents100++;
			}

		}
		mapServiceRate.put("0%_60%", percents0_59);
		mapServiceRate.put("60%_70%", percents60_69);
		mapServiceRate.put("70%_80%", percents70_79);
		mapServiceRate.put("80%_90%", percents80_89);
		mapServiceRate.put("90%_100%", percents90_99);
		mapServiceRate.put("100%", percents100);
		if ((percents0_59 + percents60_69 + percents70_79 + percents80_89 + percents90_99 + percents100) == 0) {
			homePageReport.setServiceRate(new HashMap<>());
		} else {
			homePageReport.setServiceRate(mapServiceRate);
		}
	}

	private void domainStatic(HomePageReport homePageReport) {
		List<Tuple> tupleList = awDomainDao.countAll();
		Map<String, BigDecimal> map = new LinkedHashMap<>(16);
		int flag = 0;
		BigDecimal other = new BigDecimal(0);
		for (Tuple tuple : tupleList) {
			String ips = tuple.get("ips").toString() == null ? "0" : tuple.get("ips").toString();
			if (++flag < 10) {
				String domain = tuple.get("domain").toString() == null ? "" : tuple.get("domain").toString();
				String domainCNName = DomainUtil.getDomainCNName(domain);
				BigDecimal value = new BigDecimal(ips);
				map.put(domainCNName, value);
			} else {
				other = other.add(new BigDecimal(ips));
			}

		}
		if (!(flag < 10)) {
			map.put("其他", other);
		}
		homePageReport.setIpDomain(map);
	}

	public void dayStatic(HomePageReport homePageReport) {
		List<Tuple> dataByDay = awDayDao.dataByDay();
		List<List<BigDecimal>> listVisit = new ArrayList<>(10);
		List<List<BigDecimal>> listBandwidth = new ArrayList<>(10);
		for (Tuple tuple : dataByDay) {
			BigDecimal day = new BigDecimal(tuple.get("dayTime").toString());

			ArrayList<BigDecimal> visit = new ArrayList<>();
			BigDecimal bigDecimalVisit = new BigDecimal(tuple.get("totalVisit").toString());
			visit.add(day);
			visit.add(bigDecimalVisit);
			listVisit.add(visit);

			ArrayList<BigDecimal> bandwidth = new ArrayList<>();
			BigDecimal bigDecimalBandwidth = new BigDecimal(tuple.get("bandwidth").toString());
			bandwidth.add(day);
			bandwidth.add(bigDecimalBandwidth);
			listBandwidth.add(bandwidth);
		}
		homePageReport.setVisitDaily(listVisit);

		homePageReport.setBandwidthDaily(listBandwidth);
	}

	private void allStatistic(HomePageReport homePageReport) {
		List<Tuple> summaryData = awDayDao.summaryData();
		BigDecimal totalPage = new BigDecimal(0);
		BigDecimal totalVisit = new BigDecimal(0);
		BigDecimal totalBandwidth = new BigDecimal(0);
		for (Tuple summaryDatum : summaryData) {
			totalPage = totalPage.add(new BigDecimal(summaryDatum.get("totalPage").toString()));
			totalVisit = totalVisit.add(new BigDecimal(summaryDatum.get("totalVisit").toString()));
			totalBandwidth = totalBandwidth.add(new BigDecimal(summaryDatum.get("totalBandwidth").toString()));
		}
		homePageReport.setVisits(totalVisit);
		homePageReport.setPages(totalPage);
		homePageReport.setBandwidth(totalBandwidth);
	}

	/**
	 * 站点群下拉列表
	 *
	 * @return
	 */
    @Cacheable(value = "oneDay", key = "#root.methodName")
    public List<Map<String, String>> groupsList() {
		List<Group> groups = groupDao.findByIsRankAndIsShow(1, 1);
		List<Map<String, String>> listGroup = new ArrayList<>(10);
		Collections.sort(groups, new Comparator<Group>() {
			@Override
			public int compare(Group o1, Group o2) {
				Collator c = Collator.getInstance(Locale.CHINA);
				return c.compare(o1.getCnName(), o2.getCnName());
			}
		});
		for (Group g : groups) {
			HashMap<String, String> group = new HashMap<>(16);
			group.put("id", g.getId().toString());
			group.put("name", g.getCnName());
			listGroup.add(group);
		}
		return listGroup;
	}

    /**
     * 站点群下拉列表(点击后台管理页面)
     *
     * @param groupID
     * @return
     */
	public List<Map<String, String>> getGroupsListManage(Integer groupID, Integer siteId, Integer unitId) {
		List<Tuple> groups = new ArrayList<>();
		if (siteId == -1 && unitId == -1) {
			User user = (User) SecurityUtils.getSubject().getPrincipal();
			Integer id = user.getGroup().getId();
			groups = groupDao.findByIidAndGAndGtypeNot(id);
		} else {
			groups = groupDao.findByManage(groupID);
		}
        List<Map<String, String>> listGroup = new ArrayList<>(10);

        for (Tuple g : groups) {
            HashMap<String, String> group = new HashMap<>(16);
            group.put("id", g.get("id").toString());
            group.put("name", g.get("cnName").toString());
			group.put("isShow", g.get("isShow") == null ? "" : g.get("isShow").toString());
			group.put("gtype", g.get("gtype").toString());
            listGroup.add(group);
        }
        return listGroup;
    }

	public String startTime() {
		StringBuilder sb = new StringBuilder();
		List<Tuple> startTime = awDayDao.findStartTime();
		Tuple tuple = startTime.get(0);
		sb.append(tuple.get("year").toString());
		sb.append("/");
		sb.append(tuple.get("month").toString());
		sb.append("/");
		sb.append(tuple.get("day").toString());
		return sb.toString();
	}
}

package cn.csdb.msis.integrator.web;

import cn.csdb.msis.integrator.bo.GroupRankReport;
import cn.csdb.msis.integrator.service.AllGroupsStatisticsService;
import cn.csdb.msis.util.ParamUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author jinbao
 * @Date 2019/7/30 16:44
 * @Description 所有参与排名的逻辑站点统计
 **/
@Controller
@RequestMapping(value = "/integrator/allGroupsStatistics")
public class AllGroupsStatisticsController {

	@Resource
	AllGroupsStatisticsService groupsStatisticsService;

	@GetMapping(value = "")
	public String visit(@RequestParam(value = "page") String page) {
		if ("visit".equalsIgnoreCase(page)) {
			return "integrator/page/allGroupsStatistics_Visit";
		} else if ("bandwidth".equalsIgnoreCase(page)) {
			return "integrator/page/allGroupsStatistics_Bandwidth";
		} else if ("education".equalsIgnoreCase(page)) {
			return "integrator/page/allGroupsStatistics_Education";
		} else {
			return "integrator/page/allGroupsStatistics_Visit";
		}
	}

	@PostMapping(value = "/visit")
	@ResponseBody
	public GroupRankReport groupRank(@RequestParam(value = "begin") String beginTime,
									 @RequestParam(value = "end", required = false) String endTime,
									 @RequestParam(value = "sort") String sort) {
		if (!ParamUtil.stringParamsNoBlank(beginTime, endTime, sort)) {
			return null;
		}
		GroupRankReport groupRankReport = groupsStatisticsService.groupsRank(beginTime, endTime, sort);
		return groupRankReport;
	}

	@PostMapping(value = "/education")
	@ResponseBody
	public GroupRankReport educationRank(@RequestParam(value = "begin") String beginTime,
										 @RequestParam(value = "end", required = false) String endTime,
										 @RequestParam(value = "statistic") String statistic,
										 @RequestParam(value = "sort") String sort,
										 @RequestParam(value = "rankType") String rankType) {
		if (!ParamUtil.stringParamsNoBlank(beginTime, endTime, statistic, sort, rankType)) {
			return null;
		}
		GroupRankReport groupRankReport = groupsStatisticsService.groupsHostRank(beginTime, endTime, statistic, sort, rankType);
		return groupRankReport;
	}
}

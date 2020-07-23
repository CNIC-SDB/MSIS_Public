package cn.csdb.msis.manager.web;

import cn.csdb.msis.manager.query.MonitorQuery;
import cn.csdb.msis.manager.service.MonitorService;
import cn.csdb.msis.stats.entity.Monitor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @Author jinbao
 * @Date 2019/8/28 14:19
 * @Description
 **/
@Controller
@RequestMapping(value = "/manage/monitor")
//@RequiresRoles(value = "ROLE_ADMIN")
//@RequiresPermissions(value = "ROLE_ADMIN")
public class MonitorController {

	@Resource
	private MonitorService monitorService;

	@PostMapping(value = "/saveOrUpdate")
	@ResponseBody
	public Monitor saveMonitor(MonitorQuery monitorQuery) {
		Monitor monitor = monitorService.saveOrUpdate(monitorQuery);
		return monitor;
	}

	@PostMapping(value = "/findById")
	@ResponseBody
	public Monitor findById(MonitorQuery monitorQuery) {
		Monitor monitor = new Monitor();
		if (monitorQuery.getId() != null) {
			monitor = monitorService.findById(monitorQuery);
		}
		return monitor;
	}

}

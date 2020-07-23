package cn.csdb.msis.integrator.web;

import cn.csdb.msis.integrator.bo.HomePageReport;
import cn.csdb.msis.integrator.service.HomePageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Author jinbao
 * @Date 2019/7/19 14:56
 **/
@Controller
@RequestMapping(value = "/integrator/homePage")
public class HomePageController {
	@Resource
	HomePageService homePageService;

	@GetMapping(value = "")
	public String HomePage(Model model) {
		HomePageReport homePageReport = homePageService.homePage();
		model.addAttribute("homePageReport", homePageReport);
		model.addAttribute("a", "this is string");
		return "integrator/page/homePage";
	}

	@PostMapping(value = "/groupsList")
	@ResponseBody
	public List<Map<String, String>> getGroupsList() {
		List<Map<String, String>> maps = homePageService.groupsList();
		return maps;
	}


    @PostMapping(value = "/groupsListManage")
    @ResponseBody
	public List<Map<String, String>> getGroupsListManage(@RequestParam(name = "groupID", required = false) Integer groupID, @RequestParam(name = "siteId", required = false) Integer siteId, @RequestParam(name = "unitId", required = false) Integer unitId) {
		List<Map<String, String>> maps = homePageService.getGroupsListManage(groupID, siteId, unitId);
        return maps;
    }

}

package cn.csdb.msis.manager.web;

import cn.csdb.msis.manager.query.PageResultSet;
import cn.csdb.msis.manager.query.SiteQuery;
import cn.csdb.msis.manager.query.UserQuery;
import cn.csdb.msis.manager.service.SiteService;
import cn.csdb.msis.stats.entity.Site;
import cn.csdb.msis.stats.entity.Subject;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author jinbao
 * @Date 2019/8/15 15:59
 * @Description
 **/
@Controller
@RequestMapping(value = "/manage/site")
public class SiteController {

	@Resource
	private SiteService siteService;

	@GetMapping(value = "/list")
	public String institutePage() {
		return "manage/page/site";
	}

	/**
	 * 院所信息列表
	 *
	 * @return
	 */
	@PostMapping(value = "/allInstitute")
	@ResponseBody
	public List<UserQuery> findAllInstitute() {
		return siteService.findAllInstitute();
	}

	@PostMapping(value = "/allSubject")
	@ResponseBody
	public List<Subject> findAllSubject() {
		List<Subject> allSubject = siteService.findAllSubject();
		return allSubject;
	}

	/**
	 * 根据站点名称查询
	 *
	 * @param siteQuery 站点查询参数
	 * @return
	 */
	@PostMapping(value = "/pageList")
	@ResponseBody
	public PageResultSet<SiteQuery> findSiteByCnName(SiteQuery siteQuery) {
		return siteService.findSiteByCnNameLike(siteQuery);
	}

	@PostMapping(value = "/list")
	@ResponseBody
	public List<SiteQuery> findSiteByGid(SiteQuery siteQuery) {
		List<SiteQuery> siteByGid = siteService.findSiteByGid(siteQuery);
		return siteByGid;
	}

	/**
	 * 验证实体站点英文名称的唯一性
	 *
	 * @param enName
	 * @return
	 */
	@PostMapping(value = "/validateEnName")
	@ResponseBody
	public Boolean validateEnName(Integer id, String enName) {
		return siteService.validateEnName(id, enName);
	}

	/**
	 * 新增站点信息
	 * @param siteQuery
	 * @return
	 */
	@PostMapping(value = "/save")
	@ResponseBody
	public Site saveSite(SiteQuery siteQuery) {
		Site site = siteService.saveSite(siteQuery);
		return site;
	}

	@PostMapping(value = "/delete")
	@RequiresRoles(value = "ROLE_ADMIN")
	@ResponseBody
	public String deleteSite(SiteQuery siteQuery) {
		siteService.deleteSite(siteQuery);
		return "success";
	}

	@PostMapping(value = "/update")
	@ResponseBody
	public Site updateSite(SiteQuery siteQuery) {
		Site site = siteService.updateSite(siteQuery);
		return site;
	}

	@PostMapping(value = "/deleteGroupSite")
	@ResponseBody
	public void deleteGroupSite(SiteQuery siteQuery) {
		siteService.deleteGroupSite(siteQuery);
	}

	@PostMapping(value = "/saveGroupSite")
	@ResponseBody
	public void saveGroupSite(SiteQuery siteQuery) {
		siteService.saveGroupSite(siteQuery);
	}

	@PostMapping(value = "/validateUriOrEnName")
	@ResponseBody
	public Boolean validateUriOrEnName(SiteQuery siteQuery) {
		Boolean aBoolean = siteService.validateUriOrCnName(siteQuery);
		return aBoolean;
	}
}

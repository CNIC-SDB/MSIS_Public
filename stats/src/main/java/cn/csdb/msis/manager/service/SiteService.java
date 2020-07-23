package cn.csdb.msis.manager.service;

import cn.csdb.msis.manager.query.PageResultSet;
import cn.csdb.msis.manager.query.SiteQuery;
import cn.csdb.msis.manager.query.UserQuery;
import cn.csdb.msis.stats.entity.*;
import cn.csdb.msis.stats.repository.*;
import cn.csdb.msis.user.entity.User;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @Author jinbao
 * @Date 2019/8/16 10:59
 * @Description
 **/
@Service
public class SiteService {

	@Value(value = "${custom.awstatsConfigurationPath}")
	private String awstatsConfigurationPath;

	@Value(value = "${custom.awstatsRootPath}")
	private String awstatsRootPath;

	@Value(value = "${custom.ftpRootPath}")
	private String ftpRootPath;

	@Resource
	private SiteRepository siteDao;

	@Resource
	private GroupsRepository groupDao;

	@Resource
	private GroupSiteRepository groupSiteDao;

	@Resource
	private UserRepository userDao;

	@Resource
	private MonitorRepository monitorDao;

	@Resource
	private SubjectRepository subjectDao;


	public List<UserQuery> findAllInstitute() {
		List<User> allUser = userDao.findAllInstitutes();
		List<UserQuery> institutes = new ArrayList<>(10);
		for (User user : allUser) {
			UserQuery userQuery = new UserQuery();
			userQuery.setCnName(user.getCnName());
			userQuery.setUsername(user.getUsername());
			userQuery.setGid(user.getGroup().getId());
			institutes.add(userQuery);
		}
		return institutes;
	}

	public List<Subject> findAllSubject() {
		List<Subject> all = subjectDao.findAll();
		return all;
	}

	public PageResultSet<SiteQuery> findSiteByCnNameLike(SiteQuery siteQuery) {
		PageRequest pageRequestService = PageRequest.of(siteQuery.getPageNumber() - 1, siteQuery.getPageSize());
		Page<Site> byCnNameLike = null;
		User user = (User) SecurityUtils.getSubject().getPrincipal();
		Integer gid = user.getGroup().getId();
		Integer isRoot = user.getIsroot();
		if (isRoot == 1) {
			byCnNameLike = siteDao.findByCnNameLike(siteQuery.getCnName(), pageRequestService);
		} else {
			byCnNameLike = siteDao.findByCnNameLikeWithCurrentGroup(gid, siteQuery.getCnName(), pageRequestService);
		}
		List<SiteQuery> siteQueryList = new ArrayList<>(20);
		for (Site site : byCnNameLike) {
			SiteQuery sq = new SiteQuery();
			// 站点基础信息
			site2SiteQuery(site, sq);
			siteQueryList.add(sq);
		}
		PageResultSet<SiteQuery> pageResultSetSites = new PageResultSet<>();
		pageResultSetSites.setRows(siteQueryList);
		pageResultSetSites.setTotal(byCnNameLike.getTotalElements());
		pageResultSetSites.setPageNumber(siteQuery.getPageNumber());
		return pageResultSetSites;
	}

	private void site2SiteQuery(Site site, SiteQuery sq) {
		sq.setId(site.getId());
		sq.setGid(site.getGid());
		sq.setCnName(site.getCnName());
		sq.setEnName(site.getEnName());
		sq.setDescription(site.getDescription());
		sq.setUnitId(site.getGroup().getId());
		if (site.getSubject()!=null) {
			sq.setSubject(site.getSubject().getId());
		}
		sq.setInstituteName(groupDao.findById(site.getGid()).get().getCnName());
		if (site.getMonitor() != null) {
			sq.setMid(site.getMonitor().getId());
		}

		// 站点统计设置信息
		sq.setEnName(site.getEnName());
		sq.setStatsNote(site.getStatsNote());
		sq.setFormat(site.getFormat());
		if ("IIS".equals(site.getFormat())) {
			sq.setFormatPersonalized(site.getPattern());
		}
		sq.setUri(site.getUri());
		sq.setLogfiles(site.getLogfiles());

		// 监控信息
		Monitor monitor = site.getMonitor();
		if (monitor != null) {
			sq.setmName(monitor.getName());
			sq.setmUri(monitor.getUri());
			sq.setmDescription(monitor.getDescription());
		}
	}

	public Boolean validateEnName(Integer id, String enName) {
		Site byEnName = siteDao.findByEnName(enName);
		if (byEnName == null) {
			return true;
		}
		return byEnName.getId().intValue() == id.intValue();
	}
	@Transactional
	public Site saveSite(SiteQuery siteQuery) {
		Site site = new Site();
		operateSite(siteQuery, site);

		// 创建日志解析配置文件
		saveOrUpdateAwstatsConfiguration(site);

		// 保存站点信息
		Site saveSite = siteDao.save(site);

		//设置group_site 关联关系 site<->unitgroup 在group_site中的关系
		unitRelation(saveSite);

        // 生成对应的监控站点
		Monitor monitor = new Monitor();
		monitor.setSid(saveSite.getId());
		monitor = saveMonitor(siteQuery, monitor);

		saveSite.setMonitor(monitor);
		return saveSite;
	}

	private void unitRelation(Site saveSite) {
		Set<Group> parentGroup = saveSite.getParentGroup();
		Group group = saveSite.getGroup();
		parentGroup.add(group);
		saveSite.setParentGroup(parentGroup);
		siteDao.save(saveSite);
	}

	private void operateSite(SiteQuery siteQuery, Site site) {
		// 设置subject_site 关联关系
		Optional<Subject> subjectById = subjectDao.findById(siteQuery.getSubject());
		Subject subject = subjectById.get();
		site.setSubject(subject);

		// 设置 group_site 关联关系
		// 当前站点直属用户组
		Optional<Group> groupById = groupDao.findById(siteQuery.getGid());
		Group userGroup = groupById.get();

		// 当前站点所属类目
		Optional<Group> classGroupById = groupDao.findById(userGroup.getPid());
		Group classGroup = classGroupById.get();
		Set<Group> parentGroup = site.getParentGroup();
		parentGroup.add(userGroup);
		parentGroup.add(classGroup);
		site.setParentGroup(parentGroup);

		// 创建site对应的基础group
		if (site.getId() == null) {
			Group g = saveGroup(siteQuery, userGroup);
			site.setGroup(g);
		}

		// site 基础信息
		site.setCnName(siteQuery.getCnName());
		site.setEnName(siteQuery.getEnName());
		site.setGid(siteQuery.getGid());
		site.setStatus(0);
		site.setDescription(siteQuery.getDescription());
		// site 统计信息
		site.setUri(siteQuery.getUri());
		site.setFormat(siteQuery.getFormat());
		site.setPattern(siteQuery.getFormatPersonalized());
		String logfiles = siteQuery.getLogfiles().trim();
		if (logfiles.startsWith("/") || logfiles.startsWith("\\")) {
			logfiles = logfiles.substring(1);
		}
		site.setLogfiles(logfiles);
		site.setStatsNote(siteQuery.getStatsNote());
	}

	private Group saveGroup(SiteQuery siteQuery, Group userGroup) {
		Group g = new Group();
		g.setPid(userGroup.getId());
		g.setCnName(siteQuery.getCnName());
		g.setEnName(siteQuery.getEnName());
		g.setIsRank(0);
		g.setGtype(Group.POINT_UNIT);
		g.setDescription("最小单元");
		return g;
	}

	private Monitor saveMonitor(SiteQuery siteQuery, Monitor m) {
		m.setName(siteQuery.getmName());
		m.setUri(siteQuery.getmUri());
		m.setDescription(siteQuery.getmDescription());
		m = monitorDao.save(m);
		return m;
	}

	@Transactional
	public void deleteSite(SiteQuery siteQuery) {
		Optional<Site> byId = siteDao.findById(siteQuery.getId());
		Site site = byId.get();
		deleteOldAwstatsConfiguration(site);
		groupDao.delete(site.getGroup());
		siteDao.deleteById(siteQuery.getId());
		monitorDao.delete(site.getMonitor());
	}

	@Transactional
	public Site updateSite(SiteQuery siteQuery) {
		Optional<Site> siteOptional = siteDao.findById(siteQuery.getId());
		Site s = siteOptional.get();
		// 删除旧版本配置文件
		deleteOldAwstatsConfiguration(s);
		operateSite(siteQuery, s);
		// 变更日志解析配置文件
		saveOrUpdateAwstatsConfiguration(s);
		saveMonitor(siteQuery, s.getMonitor());
		Site site = siteDao.save(s);
		return site;
	}

	/**
	 * 根据站点设置生成|更新awstats配置文件
	 * 迁移过来的站点不执行
	 * @param site
	 */
	private void saveOrUpdateAwstatsConfiguration(Site site) {

		if (isOldSite(site.getStatsNote())) {
			return;
		}
		String templateName = "webTemplate";
		// 属性赋值
		Context context = new Context();
		StringBuilder sb = new StringBuilder(ftpRootPath);
		sb.append("/");
		Optional<Group> byId = groupDao.findById(site.getGid());
		sb.append(byId.get().getUser().getUsername());
		sb.append("/");
		if ("0".equals(site.getFormat())) {
			sb.append("FTP_");
		} else {
			sb.append("WWW_");
		}
		sb.append(site.getEnName());
		sb.append("/");
		sb.append(site.getLogfiles());
		context.setVariable("logfile", sb.toString());
		context.setVariable("awstatsRootPath", awstatsRootPath);

		// 赋值日志格式以及指定要使用的模板
		if ("FTP".equals(site.getFormat())) {
			templateName = "ftpTemplate";
		}
		if ("IIS".equals(site.getFormat())) {
			StringBuilder logFormat = new StringBuilder("");
			logFormat.append("\"");
			logFormat.append(site.getPattern());
			logFormat.append("\"");
			context.setVariable("logFormat", logFormat.toString());
		} else {
			context.setVariable("logFormat", site.getPattern());
		}

		context.setVariable("siteUri", site.getUri());

		// 模板引擎配置
		ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
		resolver.setPrefix("/templates/awstatsConf/");
		resolver.setSuffix(".txt");
		TemplateEngine templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(resolver);

		// 指定结果文件
		StringBuilder fileName = new StringBuilder("awstats.");
		fileName.append(site.getUri());
		fileName.append(".conf");
		StringBuilder filePath = new StringBuilder(awstatsConfigurationPath);
		filePath.append("/");
		filePath.append(fileName);
		File file = new File(filePath.toString());
		FileWriter fileWriter = null;
		try {
			File parentFile = file.getParentFile();
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			} else {
				if (!file.exists()) {
					file.createNewFile();
				}
			}
			fileWriter = new FileWriter(file);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			// 处理模板生成awstats配置文件到指定目录
			templateEngine.process(templateName, context, fileWriter);
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 判断当前站点是否为迁移过来的站点
	 * @param siteNote
	 * @return
	 */
	private Boolean isOldSite(String siteNote){
		if (siteNote != null && siteNote.contains("@@:")) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	private void deleteOldAwstatsConfiguration(Site site) {
		StringBuilder fileName = new StringBuilder(awstatsConfigurationPath);
		fileName.append("/awstats.");
		fileName.append(site.getUri());
		fileName.append(".conf");
		File file = new File(fileName.toString());
		file.delete();
	}

	public List<SiteQuery> findSiteByGid(SiteQuery siteQuery) {
		Optional<Group> byId = groupDao.findById(siteQuery.getGid());
		Group group = byId.get();
		Set<Site> siteSet = group.getSites();
		List<SiteQuery> siteQueryList = new ArrayList<>(10);
		for (Site site : siteSet) {
			SiteQuery sq = new SiteQuery();
			site2SiteQuery(site, sq);
			siteQueryList.add(sq);
		}
		return siteQueryList;
	}

	@Transactional
	public void deleteGroupSite(SiteQuery siteQuery) {
		Integer[] ids = siteQuery.getIds();
		Integer gid = siteQuery.getGid();
		List<GroupSite> groupSiteList = groupSiteDao.findInSidAndGid(ids, gid);
		for (GroupSite groupSite : groupSiteList) {
			groupSiteDao.delete(groupSite);
		}
	}

	@Transactional
	public void saveGroupSite(SiteQuery siteQuery) {
		Integer[] ids = siteQuery.getIds();
		Integer gid = siteQuery.getGid();
		for (Integer id : ids) {
			GroupSite gs = new GroupSite();
			gs.setSid(id);
			gs.setGid(gid);
			GroupSite save = groupSiteDao.save(gs);
		}

	}

	public Boolean validateUriOrCnName(SiteQuery siteQuery) {
		Site site = siteDao.findByUriOrCnName(siteQuery.getUri(), siteQuery.getCnName());
		return site == null || Objects.equals(site.getId(), siteQuery.getId());
	}
}

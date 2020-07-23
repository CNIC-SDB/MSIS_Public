package cn.csdb.msis.manager.service;

import cn.csdb.msis.manager.query.GroupQuery;
import cn.csdb.msis.manager.query.PageResultSet;
import cn.csdb.msis.stats.entity.Group;
import cn.csdb.msis.stats.entity.GroupSite;
import cn.csdb.msis.stats.repository.GroupSiteRepository;
import cn.csdb.msis.stats.repository.GroupsRepository;
import cn.csdb.msis.user.entity.User;
import org.apache.shiro.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GroupService {

	@Resource
	private GroupsRepository groupDao;

	@Resource
	private GroupSiteRepository groupSiteDao;

	public GroupQuery groupTree() {
		User user = (User) SecurityUtils.getSubject().getPrincipal();
		Optional<Group> byId = groupDao.findById(user.getGroup().getId());
		@SuppressWarnings("OptionalGetWithoutIsPresent") Group adminGroup = byId.get();
//		GroupQuery groupQuery = groupTreeNodes(new GroupQuery(), adminGroup);
		GroupQuery groupQuery = groupRankTree(new GroupQuery(), adminGroup);
		return groupQuery;
	}

	private GroupQuery groupRankTree(GroupQuery groupQuery, Group group) {
		groupQuery.setId(group.getId());
		groupQuery.setCnName(group.getCnName());
		groupQuery.setDescription(group.getDescription());
		groupQuery.setGtype(group.getGtype());
		groupQuery.setOpen(true);
		List<GroupQuery> groupQueryList = new ArrayList<>(10);
		for (Group childGroup : group.getChildGroups()) {
			if (childGroup.getGtype() != 40) {
				continue;
			}
			GroupQuery g = new GroupQuery();
			g.setId(childGroup.getId());
			g.setCnName(childGroup.getCnName());
			g.setShortName(childGroup.getShortName());
			g.setDescription(childGroup.getDescription());
			g.setIid(childGroup.getIid());
			g.setOrderNum(childGroup.getOrderNum());
			g.setIsShow(childGroup.getIsShow());
			groupQueryList.add(g);
		}
		groupQueryList.sort(new GroupQuery().new GroupQueryCompare());
		groupQuery.setChildren(groupQueryList);
		return groupQuery;
	}

	/**
	 * 新增逻辑分组 （rank）
	 *
	 * @param groupQuery
	 * @return
	 */
	public Group addGroup(GroupQuery groupQuery) {
		Group group = new Group();
		group.setPid(groupQuery.getPid());
		group.setGtype(40);
		group.setIsRank(1);
		group.setCnName(groupQuery.getCnName());
		group.setShortName(groupQuery.getShortName());
		group.setDescription(groupQuery.getDescription());
		group.setIsShow(groupQuery.getIsShow());
		group.setIid(groupQuery.getIid());
		group.setOrderNum(groupQuery.getOrderNum());
		group = groupDao.save(group);
		return group;
	}

	public Group editGroup(GroupQuery groupQuery) {
		Optional<Group> byId = groupDao.findById(groupQuery.getId());
		Group group = byId.get();
		if (groupQuery.getCnName() != null) {
			group.setCnName(groupQuery.getCnName());
		}
		if (groupQuery.getShortName() != null) {
			group.setShortName(groupQuery.getShortName());
		}
		if (groupQuery.getDescription() != null) {
			group.setDescription(groupQuery.getDescription());
		}
		group.setIid(groupQuery.getIid());
		group.setIsShow(groupQuery.getIsShow());
		group.setOrderNum(groupQuery.getOrderNum());
		group = groupDao.save(group);
		return group;
	}

	@Transactional
	public Group deleteGroup(GroupQuery groupQuery) {
		Optional<Group> byId = groupDao.findById(groupQuery.getId());
		Group group = new Group();
		if (byId.isPresent()) {
			group = byId.get();
			Integer gid = group.getId();
			List<GroupSite> groupSiteList = groupSiteDao.findByGid(gid);
			for (GroupSite groupSite : groupSiteList) {
				groupSiteDao.delete(groupSite);
			}
			groupDao.delete(group);
		}
		return group;
	}

	public PageResultSet<Group> pageList(GroupQuery groupQuery) {
		User user = (User) SecurityUtils.getSubject().getPrincipal();
		Group group = user.getGroup();
		Integer groupId = group.getId();

		PageRequest pageRequest = PageRequest.of(groupQuery.getPageNumber() - 1, groupQuery.getPageSize(), Sort.Direction.ASC, "id");
		String cnName = "%" + groupQuery.getCnName() + "%";
		Page<Group> byPid = groupDao.findByIidAndGtypeNotAndCnNameLike(groupId, 50, cnName, pageRequest);

		PageResultSet<Group> result = new PageResultSet<>();
		result.setPageNumber(groupQuery.getPageNumber());
		result.setRows(byPid.getContent());
		result.setTotal(byPid.getTotalElements());
		return result;
	}

	public Boolean validateCnName(Integer pid, Integer id, String cnName) {
		List<Group> byPidAndCnName = groupDao.findByPidAndCnName(pid, cnName);
		if (id == null) {
			return byPidAndCnName.size() == 0;
		} else {
			if (byPidAndCnName.size() > 1) {
				return false;
			} else {
				return byPidAndCnName.get(0).getId().intValue() == id.intValue();
			}
		}
	}
}

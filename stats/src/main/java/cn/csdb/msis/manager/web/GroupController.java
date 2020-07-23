package cn.csdb.msis.manager.web;

import cn.csdb.msis.manager.query.GroupQuery;
import cn.csdb.msis.manager.query.PageResultSet;
import cn.csdb.msis.manager.service.GroupService;
import cn.csdb.msis.stats.entity.Group;
import cn.csdb.msis.user.entity.User;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping(value = "/manage/group")
public class GroupController {

    @Resource
    GroupService groupService;

    @PostMapping(value = "/groupTree")
    @ResponseBody
    public GroupQuery groupTree() {
        GroupQuery GroupQuery = groupService.groupTree();
        return GroupQuery;
    }

    @PostMapping(value = "/pageList")
    @ResponseBody
    public PageResultSet<Group> pageList(GroupQuery groupQuery) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        PageResultSet<Group> groupPageResultSet = groupService.pageList(groupQuery);
        return groupPageResultSet;
    }

    @PostMapping(value = "/validateCnName")
    @ResponseBody
    public Boolean validateCnName(Integer pid, Integer id, String cnName) {
        return groupService.validateCnName(pid, id, cnName);
    }

    @PostMapping(value = "/addGroup")
    @ResponseBody
    public Group addGroup(GroupQuery groupQuery) {
        Group group;
        group = groupService.addGroup(groupQuery);
        return group;
    }

    @PostMapping(value = "/editGroup")
    @ResponseBody
    public Group editGroup(GroupQuery groupQuery) {
        Group group = groupService.editGroup(groupQuery);
        return group;
    }

    @PostMapping(value = "/deleteGroup")
    @ResponseBody
    public Group deleteGroup(GroupQuery groupQuery) {
        if (groupQuery.getId() == null) {
            return new Group();
        }
        Group group = groupService.deleteGroup(groupQuery);
        return group;
    }
}

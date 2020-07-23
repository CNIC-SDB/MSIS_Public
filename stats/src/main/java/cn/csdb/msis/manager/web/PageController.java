package cn.csdb.msis.manager.web;

import cn.csdb.msis.user.entity.User;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author jinbao
 * @Date 2019/8/21 16:09
 * @Description 管理后台页面控制器
 **/
@Controller
@RequestMapping(value = "/manage")
public class PageController {

    @GetMapping(value = "/login")
    public String login() {
		User user = (User) SecurityUtils.getSubject().getPrincipal();
		if (user == null) {
			return "manage/login";
		} else {
			return "redirect:/manage/";
		}
    }

    @GetMapping(value = {"/", ""})
    public String index() {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if (user == null) {
            return "manage/login";
        }
        return "manage/main";
    }

    @GetMapping(value = "/user/list")
    public String institutePage() {
        return "manage/page/user";
    }

	@GetMapping(value = "/user/editPassword")
	public String editPassword() {
		return "manage/page/user-editPassword";
	}

	@GetMapping(value = "/user/changeMailStatus")
	public String changeMailStatus() {
		return "manage/page/user-changeMailStatus";
	}

    @GetMapping(value = "/group/tree")
    public String groupTree() {
        return "manage/page/group-tree";
    }

    @GetMapping(value = "group/node")
    public String GroupNode() {
        return "manage/page/group-node";
    }

	@GetMapping(value = "group/institute")
	public String GroupInstitute() {
		return "manage/page/group-institute";
	}
}

package cn.csdb.msis.manager.web;

import cn.csdb.msis.manager.query.PageResultSet;
import cn.csdb.msis.manager.query.UserQuery;
import cn.csdb.msis.manager.service.UserService;
import cn.csdb.msis.user.entity.User;
import cn.csdb.msis.util.ParamUtil;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @Author jinbao
 * @Date 2019/8/13 11:03
 * @Description
 **/
@Controller
@RequestMapping(value = "/manage/user")
public class UserController {
	@Resource
	private UserService userService;

	@PostMapping(value = "/currentUser")
	@RequiresRoles(value = {"ROLE_USER"})
	@ResponseBody
	public User currentUser(String username) {
		return userService.findByUsername(username);
	}

	/**
	 * 用户信息分页查询
	 *
	 * @param userQuery 查询参数包装类
	 * @return
	 */
	@PostMapping(value = "/pageList")
	//@RequiresRoles(value = "ROLE_ADMIN")
	@ResponseBody
	public PageResultSet<User> findInstitutesByCnName(UserQuery userQuery) {
		PageResultSet<User> institutes = userService.findInstitutesByCnName(userQuery);
		return institutes;
	}

	@PostMapping(value = "/validateCnName")
	@ResponseBody
	public Boolean validateCnName(String cnName) {
		return userService.cnNameIsExist(cnName);
	}

	@PostMapping(value = "/validateEnName")
	@ResponseBody
	public Boolean validateEnName(String enName) {
		return userService.enNameIsExist(enName);
	}

	@PostMapping(value = "/validateUsername")
	@ResponseBody
	public Boolean validateUsername(String username, @RequestParam(name = "id", required = false) Integer id) {
		return userService.usernameIsExist(username, id);
	}

	@PostMapping(value = "/validatePassword")
	@ResponseBody
	public Boolean validatePassword(String username, String oldPassword) {
		return userService.findByUsernameAndPassword(username, oldPassword);
	}

	/**
	 * 新增用户
	 *
	 * @param userQuery
	 * @return
	 */
	@PostMapping(value = "/save")
	@ResponseBody
	public User saveInstitute(UserQuery userQuery) {
		User users = null;
		if (!ParamUtil.stringParamsNoBlank(userQuery.getCnName(), userQuery.getEnName(),
				userQuery.getUsername(), userQuery.getPassword(),
				userQuery.getEmail())) {

		} else {
			users = userService.saveInstitute(userQuery);
		}
		return users;
	}

	/**
	 * 删除用户
	 *
	 * @param userQuery
	 * @return
	 */
	@PostMapping(value = "/delete")
	@RequiresRoles(value = "ROLE_ADMIN")
	@ResponseBody
	public UserQuery deleteUserByUsername(UserQuery userQuery) {

//		userService.deleteUser(userQuery);

		return userQuery;
	}

	@PostMapping(value = "/update")
	@ResponseBody
	public User updateUserByUsername(UserQuery userQuery) {

		User user = userService.updateUser(userQuery);

		return user;
	}
}

package cn.csdb.msis.manager.shiro;

import cn.csdb.msis.manager.service.UserService;
import cn.csdb.msis.user.entity.Role;
import cn.csdb.msis.user.entity.User;
import org.apache.logging.log4j.util.Strings;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author jinbao
 * @Date 2019/8/8 9:40
 * @Description 自定义登录、鉴权类
 **/
public class MyShiroRealm extends AuthorizingRealm {
	// 加上@Lazy注解 否则Bean注入不进来
	@Lazy
	@Resource
	private UserService userService;

	// 授权
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
		User user = (User) principals.getPrimaryPrincipal();
		User byUsername = userService.findByUsername(user.getUsername());
		Set<String> roles = new HashSet<>();
		for (Role role : byUsername.getRoles()) {
			roles.add(role.getRolename());
		}
		authorizationInfo.setRoles(roles);
		return authorizationInfo;
	}

	// 认证
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
		String username = usernamePasswordToken.getUsername();
		User user = null;
		if (!Strings.isBlank(username)) {
			user = userService.findByUsername(username);
		}
		if (user == null) {
			throw new UnknownAccountException();
		}

		SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
				user, //用户
				user.getPassword(), //密码
				getName()  //realm name
		);
		return authenticationInfo;
	}
}

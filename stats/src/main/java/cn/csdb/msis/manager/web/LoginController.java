package cn.csdb.msis.manager.web;

import cn.csdb.msis.util.MD5Util;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author jinbao
 * @Date 2019/8/9 14:06
 * @Description 登录控制器
 **/
@Controller
@RequestMapping(value = "/manage")
@PropertySource(value = "classpath:application-${spring.profiles.active}.yml")
public class LoginController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value(value = "${custom.salt}")
	private String salt;

	@PostMapping(value = {"/login"})
	public String login(@RequestParam(value = "username") String username,
						@RequestParam(value = "password") String password,
						HttpServletRequest request) {
		String exceptionClassName = (String) request.getAttribute("shiroLoginFailure");
		UsernamePasswordToken token = new UsernamePasswordToken(username, MD5Util.encryptPassword(username, password, salt));
		Subject current = SecurityUtils.getSubject();
		try {
			current.login(token);
			return "redirect:/manage/";
		} catch (UnknownAccountException uae) {
			logger.warn("用户帐号不正确");

		} catch (IncorrectCredentialsException ice) {
			logger.warn("用户密码不正确");

		} catch (LockedAccountException lae) {
			logger.warn("用户帐号被锁定");

		} catch (AuthenticationException ae) {
			logger.warn("登录出错");
		}
		return "manage/login";

	}


}

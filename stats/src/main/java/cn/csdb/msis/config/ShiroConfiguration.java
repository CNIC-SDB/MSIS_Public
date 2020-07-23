package cn.csdb.msis.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import cn.csdb.msis.manager.shiro.LoginFilter;
import cn.csdb.msis.manager.shiro.MyShiroRealm;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author jinbao
 * @Date 2019/8/9 10:23
 * @Description shiro配置类
 **/
@Configuration
public class ShiroConfiguration {

	/**
	 * AOP代理设置
	 *
	 * @return
	 */
	@Bean
	public static DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
		creator.setUsePrefix(true);
		return creator;
	}

	/**
	 * 权限验证设置
	 *
	 * @param securityManager
	 * @return
	 */
	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(org.apache.shiro.mgt.SecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
		authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
		return authorizationAttributeSourceAdvisor;
	}

	@Bean
	public MyShiroRealm myShiroRealm() {
		return new MyShiroRealm();
	}

	@Bean
	public DefaultWebSecurityManager securityManager() {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setSessionManager(sessionManager());
		securityManager.setRememberMeManager(rememberMeManager());
		securityManager.setRealm(myShiroRealm());
		return securityManager;
	}

	/**
	 * 这里统一做鉴权，即判断哪些请求路径需要用户登录，哪些请求路径不需要用户登录。
	 * 这里只做鉴权，不做权限控制，因为权限用注解来做。
	 *
	 * @return
	 */
	@Bean
	public ShiroFilterFactoryBean filterFactoryBean() {
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		shiroFilterFactoryBean.setSecurityManager(securityManager());
		//身份认证失败、没有登录的用户请求需要登录的页面时、自动跳转到登录页面，不是必须的属性。默认web项目根目录下的”/login.jsp”页面。
		shiroFilterFactoryBean.setLoginUrl("/manage/login");
		//登录成功默认跳转页面，不配置则跳转至”/”。如果登陆前点击的一个需要登录的页面，则在登录自动跳转到那个需要登录的页面。不跳转到此。
		shiroFilterFactoryBean.setSuccessUrl("/manage/");
		//没有权限默认跳转的页面
		shiroFilterFactoryBean.setUnauthorizedUrl("/error/404");

		//shiro自定义过滤器
		Map<String, Filter> filters = new LinkedHashMap<>();
		LoginFilter loginFilter = new LoginFilter();
		filters.put("myAccessControlFilter", loginFilter);
		shiroFilterFactoryBean.setFilters(filters);

		Map<String, String> shiroFilterChainMap = new LinkedHashMap<>();
		shiroFilterChainMap.put("/manage/logout", "logout");
		shiroFilterChainMap.put("/manage/login", "authc");
		shiroFilterChainMap.put("/manage/**", "authc");
		shiroFilterChainMap.put("/**", "anon");
		shiroFilterChainMap.put("/manage/**", "myAccessControlFilter");
		shiroFilterFactoryBean.setFilterChainDefinitionMap(shiroFilterChainMap);
		return shiroFilterFactoryBean;
	}


	@Bean(name = "sessionManager")
	public DefaultWebSessionManager sessionManager() {
		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
		sessionManager.setGlobalSessionTimeout(1800000);
		sessionManager.setSessionIdUrlRewritingEnabled(false);
		sessionManager.setDeleteInvalidSessions(true);
		sessionManager.setSessionValidationSchedulerEnabled(true);
		sessionManager.setSessionIdCookieEnabled(true);
		sessionManager.setSessionIdCookie(sessionIdCookie());
		return sessionManager;
	}

	/**
	 * 会话Cookie模板
	 */
	@Bean
	public SimpleCookie sessionIdCookie() {
		SimpleCookie simpleCookie = new SimpleCookie("sid");
		simpleCookie.setHttpOnly(true);
		simpleCookie.setMaxAge(-1);
		return simpleCookie;
	}

	/**
	 * 自动登陆自动登陆cookie
	 */
	@Bean
	public SimpleCookie rememberMeCookie() {
		SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
		simpleCookie.setHttpOnly(true);
		simpleCookie.setMaxAge(2592000);
		return simpleCookie;
	}

	/**
	 * rememberMe管理器
	 */
	@Bean
	public CookieRememberMeManager rememberMeManager() {
		CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
		cookieRememberMeManager.setCipherKey(Base64.decode("4AvVhm2LUs0KTA3Kprsdag+=="));
		cookieRememberMeManager.setCookie(rememberMeCookie());
		return cookieRememberMeManager;
	}

	/**
	 * 在thymeleaf中使用shiro标签
	 *
	 * @return
	 */
	@Bean
	public ShiroDialect shiroDialect() {
		return new ShiroDialect();
	}
}

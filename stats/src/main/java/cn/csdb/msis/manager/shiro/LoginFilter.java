package cn.csdb.msis.manager.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.session.mgt.WebSessionKey;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;

/**
 * @Author jinbao
 * @Date 2019/10/21 18:47
 * @Description shiro过滤器链
 **/
public class LoginFilter extends AccessControlFilter {
	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
		boolean status = false;

		Serializable sessionID = SecurityUtils.getSubject().getSession().getId();
		SessionKey key = new WebSessionKey(sessionID, request, response);
		try {
			Session se = SecurityUtils.getSecurityManager().getSession(key);
			Object obj = se.getAttribute(DefaultSubjectContext.AUTHENTICATED_SESSION_KEY);
			//通过返回TRUE，通过前台的统一AJAX接受头部设置的sessionstatus参数，判断是否跳转到登录页面
			//FALSE  Session失效，切实非AJAX请求，验证是否，调用onAccessDenied跳转到登录页面
			if (obj == null && ShiroFilterUtils.isAjax(request)) {
				ShiroFilterUtils.out(response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Session se = null;
			Object obj = null;
		}

		return Boolean.TRUE;
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		saveRequestAndRedirectToLogin(request, response);
		return false;
	}
}

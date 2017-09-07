package com.socool.soft.filter;

import com.socool.soft.constant.Constants;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class LoginFilter implements Filter {

	public LoginFilter() {
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		String userAgent = httpServletRequest.getHeader("user-agent");
		String servletPath = httpServletRequest.getServletPath();
 		if (userAgent != null&&!userAgent.equals("okhttp/3.8.0") && httpServletRequest.getSession().getAttribute(Constants.USER_ID_IN_SESSION_KEY) == null
				&& httpServletRequest.getSession().getAttribute(Constants.MERCHANT_USER_ID_IN_SESSION_KEY) == null
				&& httpServletRequest.getSession().getAttribute(Constants.VENDOR_USER_ID_IN_SESSION_KEY) == null
				&& !servletPath.equals("/login.jsp") && !servletPath.startsWith("/loginRegister/")
				&& !servletPath.startsWith("/prod/content") && !servletPath.startsWith("/help/content")
				&& !servletPath.equals("/help/help.html") && !servletPath.equals("/user/validateCode")
				&& !servletPath.startsWith("/static") && !servletPath.startsWith("/restaurant/")
				&& !servletPath.startsWith("/page/")&& !servletPath.startsWith("/merchant/")) {
			String requestUri = httpServletRequest.getRequestURI();
			String queryString = httpServletRequest.getQueryString();
			httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/login.jsp?url=" + requestUri + "?" + queryString);
			return;
		}
		chain.doFilter(request, response);
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}
}

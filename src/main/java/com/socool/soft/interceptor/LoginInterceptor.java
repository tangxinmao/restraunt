package com.socool.soft.interceptor;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.socool.soft.bo.RcMenu;
import com.socool.soft.bo.RcMerchantMenu;
import com.socool.soft.bo.RcMerchantUser;
import com.socool.soft.bo.RcUser;
import com.socool.soft.bo.RcVendorUser;
import com.socool.soft.bo.constant.RoleIdEnum;
import com.socool.soft.bo.constant.YesOrNoEnum;
import com.socool.soft.constant.Constants;
import com.socool.soft.service.IMenuService;
import com.socool.soft.service.IMerchantMenuService;
import com.socool.soft.service.IMerchantRoleService;
import com.socool.soft.service.IMerchantUserService;
import com.socool.soft.service.IRoleService;
import com.socool.soft.service.IUserService;
import com.socool.soft.service.IVendorUserService;

/** 
 * Created by Administrator on 2016/7/30.
 */
public class LoginInterceptor implements HandlerInterceptor {
	@Autowired
	private IUserService userService;
	@Autowired
	private IMerchantUserService merchantUserService;
	@Autowired
	private IVendorUserService vendorUserService;
	@Autowired
	private IMenuService menuService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IMerchantMenuService merchantMenuService;
	@Autowired
	private IMerchantRoleService merchantRoleService;

   

	/**
	 * 是否跳转到访问的请求
	 *
	 * @param httpServletRequest
	 * @param httpServletResponse
	 * @param o
	 * @return
	 * @throws Exception
	 */
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o)
			throws Exception {
//		String userAgent = httpServletRequest.getHeader("user-agent");
//		if (userAgent == null){
//			return true;
//		}
//		if (httpServletRequest.getSession().getAttribute(Constants.USER_ID_IN_SESSION_KEY) != null ||
//				httpServletRequest.getSession().getAttribute(Constants.MERCHANT_USER_ID_IN_SESSION_KEY) != null ||
//				httpServletRequest.getSession().getAttribute(Constants.VENDOR_USER_ID_IN_SESSION_KEY) != null) {
//			return true;
//		} else {
//			httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/login.jsp");
//			return false;
//		}
		return true;
	}

	/**
	 * 返回修改
	 *
	 * @param httpServletRequest
	 * @param httpServletResponse
	 * @param o
	 * @param modelAndView
	 * @throws Exception
	 */
	public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
			ModelAndView modelAndView) throws Exception {
		String userAgent = httpServletRequest.getHeader("user-agent");
		if (userAgent == null || userAgent.equals("okhttp/3.8.0")) {
			return;
		}
		String servletPath = httpServletRequest.getServletPath();
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		// 返回登录用户类型，用于判断操作权限,controller中不要使用loginMember
		if (modelAndView != null) {
			Integer userId = (Integer)httpServletRequest.getSession().getAttribute(Constants.USER_ID_IN_SESSION_KEY);
			if(userId != null) {
				RcUser user = userService.findUserById(userId);
				if(user.getIsSuper() == YesOrNoEnum.YES.getValue()) {
					user.setRoles(Arrays.asList(roleService.findRoleById(RoleIdEnum.ADMINISTRATOR.getValue())));
				} else {
					user.setRoles(roleService.findRolesByUserId(userId));
				}
				user.setPassword(null);
				modelAndView.addObject("loginMember", 
						objectMapper.writeValueAsString(user));
				modelAndView.addObject("loginMemberInfo", user);

				// 获取有效菜单
				RcMenu menu = menuService.findMenuByLocation(servletPath);
				if (menu != null) {
					RcMenu parantMenu = menuService.findMenuById(menu.getParentId());
					modelAndView.addObject("rcMenuItem", 
						objectMapper.writeValueAsString(menu));
					modelAndView.addObject("rcMenuParent", 
						objectMapper.writeValueAsString(parantMenu));
				}
			} else {
				Integer merchantUserId = (Integer)httpServletRequest.getSession().getAttribute(Constants.MERCHANT_USER_ID_IN_SESSION_KEY);
				if(merchantUserId != null) {
					RcMerchantUser merchantUser = merchantUserService.findMerchantUserById(merchantUserId);
					merchantUser.setRoles(Arrays.asList(merchantRoleService.findMerchantRoleById(RoleIdEnum.STORE_SELLER.getValue())));
					merchantUser.setPassword(null);
					modelAndView.addObject("loginMember", 
							objectMapper.writeValueAsString(merchantUser));
					modelAndView.addObject("loginMemberInfo", merchantUser);

					// 获取有效菜单
					RcMerchantMenu menu = merchantMenuService.findMerchantMenuByLocation(servletPath);
					if (menu != null) {
						RcMerchantMenu parantMenu = merchantMenuService.findMerchantMenuById(menu.getParentId());
						modelAndView.addObject("rcMenuItem", 
							objectMapper.writeValueAsString(menu));
						modelAndView.addObject("rcMenuParent", 
							objectMapper.writeValueAsString(parantMenu));
					}
				} else {
					Integer vendorUserId = (Integer)httpServletRequest.getSession().getAttribute(Constants.VENDOR_USER_ID_IN_SESSION_KEY);
					if(vendorUserId != null) {
						RcVendorUser vendorUser = vendorUserService.findVendorUserById(vendorUserId);
						vendorUser.setRoles(Arrays.asList(roleService.findRoleById(RoleIdEnum.PRINCIPAL_MANAGER.getValue())));
						vendorUser.setPassword(null);
						modelAndView.addObject("loginMember", 
								objectMapper.writeValueAsString(vendorUser));
						modelAndView.addObject("loginMemberInfo", vendorUser);

						// 获取有效菜单
						RcMenu menu = menuService.findMenuByLocation(servletPath);
						if (menu != null) {
							RcMenu parantMenu = menuService.findMenuById(menu.getParentId());
							modelAndView.addObject("rcMenuItem", 
								objectMapper.writeValueAsString(menu));
							modelAndView.addObject("rcMenuParent", 
								objectMapper.writeValueAsString(parantMenu));
						}
					}
				}
			}
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
	}
}

package com.socool.soft.controller;

import com.socool.soft.bo.*;
import com.socool.soft.bo.constant.YesOrNoEnum;
import com.socool.soft.constant.Constants;
import com.socool.soft.exception.SystemException;
import com.socool.soft.service.*;
import com.socool.soft.util.CheckUtil;
import com.socool.soft.vo.Page;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping(value = "roleMerchant")
@Controller
public class RoleMerchantController extends BaseController {
    @Autowired
    private IMerchantRoleService merchantRoleService;
    @Autowired
    private IMemberService memberService;
    /*	@Autowired
        private IUserMerchantService userMerchantService;*/
    @Autowired
    private IMerchantUserService merchantUserService;

    @Autowired
    private IMerchantMenuService menuMerchantService;
    @Autowired
    private IMerchantUserRoleService merchantUserRoleService;
    @Autowired
    private IMerchantMenuRoleService merchantMenuRoleService;

    /**
     * 后台角色页面
     *
     * @param model
     * @param currentPage
     * @return
     */
    @RequestMapping(value = "roleMemuRelation")
    public String roleMemuRelation(Model model, Integer currentPage) {
        boolean flag = false;
        if (currentPage == null) {
            currentPage = 1;
            flag = true;
        }
        // 分页
        Page page = new Page();
        // 初始化时第一页
        page.setPagination(true);
        page.setPageSize(10);
        page.setCurrentPage(currentPage);
        model.addAttribute("page", page);
        List<RcMerchantRole> roles = merchantRoleService.findAllPagedMerchantRoles(page);
        model.addAttribute("page", page);
        model.addAttribute("list", roles);
        if (flag) {
            return "roleMemuRelation_merchant";
        } else {
            return "roleMemuRelation_merchant_inner";
        }
    }

    /**
     * 后台用户页面
     *
     * @param model
     * @param currentPage
     * @return
     */
    @RequestMapping(value = "roleMemberRelation")
    public String roleMemberRelation(Model model, Integer currentPage, Integer memberId, String account, Integer roleId, String email) {
        List<RcMerchantRole> roles = merchantRoleService.findAllMerchantRoles();
        boolean flag = false;
        if (currentPage == null) {
            currentPage = 1;
            flag = true;
        }
        Page page = new Page();
        page.setPagination(true);
        page.setCurrentPage(currentPage);
        page.setPageSize(10);
        List<RcMerchantUser> users = merchantUserService.findPagedUsersWithRoles(memberId, account, roleId, email, page);
        model.addAttribute("page", page);
        model.addAttribute("rcRoles", roles);
        model.addAttribute("list", users);
        if (flag) {
            return "roleMemberRelation_merchant";
        } else {
            return "roleMemberRelation_merchant_inner";
        }
    }

    /**
     * 后台菜单页面
     *
     * @return
     */
    @RequestMapping(value = "menuIndex")
    public String menuIndex() {
        return "menuIndex_merchant";
    }

    /**
     * 修改角色
     *
     * @return
     */
    @RequestMapping(value = "updateRole")
    @ResponseBody
    public String updateRole(Integer roleId, String name, String description) {
        RcMerchantRole role = new RcMerchantRole();
        role.setRoleId(roleId);
        role.setName(name);
        role.setDescription(description);
        int result = 0;
        if (roleId == null) {
            result = merchantRoleService.addMerchantRole(role);
        } else {
            result = merchantRoleService.modifyMerchantRole(role);
        }
        if (result > 0) {
            return "{\"code\":\"1\"}";
        }
        return "{\"code\":\"0\"}";
    }

    /**
     * 获取所有菜单
     *
     * @return
     */
    @RequestMapping(value = "menu")
    @ResponseBody
    public List<RcMerchantMenu> menu() {
        List<RcMerchantMenu> menus = menuMerchantService.findAllMerchantMenus();
        return menus;
    }

    /**
     * 查询所有菜单通过角色
     *
     * @param roleId
     * @return
     */
    @RequestMapping(value = "menuByRole")
    @ResponseBody
    public List<RcMerchantMenu> menuByRole(int roleId) {
        List<RcMerchantMenu> menus = menuMerchantService.findSortedMerchantMenusByRoleId(roleId);
        return menus;
    }

    /**
     * 查询可用菜单通过角色
     *
     * @param roleId
     * @return
     */
    @RequestMapping(value = "menuCheckedByRole")
    @ResponseBody
    public List<RcMerchantMenu> menuCheckedByRole(int roleId) {
        List<RcMerchantMenu> allMenus = menuMerchantService.findAllMerchantMenus();
        List<RcMerchantMenu> roleMenus = menuMerchantService.findMerchantMenusByRoleId(roleId);
        if (!CollectionUtils.isEmpty(roleMenus)) {
            for (RcMerchantMenu menu : allMenus) {
                for (RcMerchantMenu roleMenu : roleMenus) {
                    if (menu.getMenuId().equals(roleMenu.getMenuId())) {
                        menu.setChecked(YesOrNoEnum.YES.getValue());
                        break;
                    }
                }
            }
        }
        return allMenus;
    }

    @RequestMapping(value = "saveMenu")
    @ResponseBody
    public String saveMenu(Integer menuId, Integer parentId, String name, String location, int seq) throws SystemException {
        CheckUtil.tooLong(name, 20, "Menu Name");
        CheckUtil.tooLong(location, 100, "location");

        RcMerchantMenu menu = new RcMerchantMenu();
        menu.setMenuId(menuId);
        menu.setParentId(parentId);
        menu.setName(name);
        menu.setLocation(location);
        menu.setSeq(seq);
        int result = 0;
        if (menuId == null) {
            result = menuMerchantService.addMerchantMenu(menu);
        } else {
            result = menuMerchantService.modifyMerchantMenu(menu);
        }
        if (result > 0) {
            return "{\"code\":\"1\"}";
        }
        return "{\"code\":\"0\"}";
    }

    @RequestMapping(value = "deleteMenu")
    @ResponseBody
    public String deleteMenu(int menuId) {
        int result = menuMerchantService.removeMerchantMenu(menuId);
        if (result > 0) {
            return "{\"code\":\"1\",\"result\":\"Done.\"}";
        }
        return "{\"code\":\"0\",\"result\":\"Fail.\"}";
    }

    /**
     * 保存角色菜单
     *
     * @return
     */
    @RequestMapping(value = "menuRole")
    @ResponseBody
    public String menuRole(int roleId, int[] menuIds) {
        merchantMenuRoleService.removeMenuRolesByRoleId(roleId);
        if (menuIds != null) {
            for (int menuId : menuIds) {
                RcMerchantMenuRole menuRole = new RcMerchantMenuRole();
                menuRole.setRoleId(roleId);
                menuRole.setMenuId(menuId);
                merchantMenuRoleService.addMenuRole(menuRole);
            }
        }

//		//iRoleService.saveMenuRole(list, roleId);
//		List<Item> items = menuMerchantService.findItemsByRoleId(roleId);
//		result.setData(items);
//		
//		// 手动去失效当前角色的所在登录用户session 重新获取角色分配菜单
//		Vector<HttpSession> httpSessions = SessionListener.httpSessions;
//		List<RcUserRole> rcMemberRoles = memberRoleService.findMemberRolesByRoleId(roleId);
//		List<Integer> memberIds = new ArrayList<Integer>();
//		String memberId = httpServletRequest.getSession().getAttribute("memberId").toString();
//		for (RcUserRole rcMemberRole : rcMemberRoles) {
//			if (!memberId.equals(rcMemberRole.getMemberId())) 
//				memberIds.add(rcMemberRole.getMemberId());
//		
//		}
//		 for (HttpSession httpSession : httpSessions) {
//			 try{
//			if(memberIds.contains(httpSession.getAttribute("memberId"))){
//				httpSession.invalidate();
//			}
//			 }catch(Exception e){
//				 
//			 }
//			}

        return "{\"code\":\"1\"}";
    }

    @RequestMapping(value = "saveMemberRole")
    @ResponseBody
    public String saveMemberRole(Integer memberId, String account, String password,
                                 String email, String description, int roleId, HttpServletRequest httpServletRequest) throws SystemException {
        CheckUtil.tooLong(account, 50, "Username");
        CheckUtil.tooLong(email, 50, "Email");
        CheckUtil.tooLong(description, 500, "Description");
        RcMerchantUser user = new RcMerchantUser();
        user.setMemberId(memberId);
       // user.setAccount(account);
        user.setName(account);
        user.setPassword(password);
        user.setEmail(email);
        user.setDescription(description);
        user.setIsSuper(YesOrNoEnum.NO.getValue());
        if (memberId == null) {
//            RcMember oldMember = memberService.findMemberByAccount(account);
//            if (oldMember != null) {
//                return "{\"code\":\"-1\",\"result\":\"account has been used\"}";
//
//            }
        	RcMember oldMember = memberService.findMemberByEmail(email);
            if (oldMember != null) {
                return "{\"code\":\"-1\",\"result\":\"email has been used\"}";

            }
            user.setPassword(DigestUtils.md5Hex(user.getPassword()));
            RcMerchantUser merchantUser = (RcMerchantUser) httpServletRequest.getSession().getAttribute(Constants.MEMBER_INFO_IN_SESSION_KEY);
            user.setMerchantId(merchantUser.getMerchantId());

            memberId = merchantUserService.addUser(user);
            if (memberId > 0) {
                RcMerchantUserRole userRole = new RcMerchantUserRole();
                userRole.setMemberId(memberId);
                userRole.setRoleId(roleId);
                merchantUserRoleService.addMerchantUserRole(userRole);
            }
        } else {
//            RcMember oldMember = memberService.findMemberByAccount(account);
//            if (oldMember != null && !memberId.equals(oldMember.getMemberId())) {
//                return "{\"code\":\"-1\",\"result\":\"account has been used\"}";
//
//            }
        	RcMember oldMember = memberService.findMemberByEmail(email);
            if (oldMember != null && !memberId.equals(oldMember.getMemberId())) {
                return "{\"code\":\"-1\",\"result\":\"email has been used\"}";

            }
            if (StringUtils.isNotBlank(user.getPassword())) {
                user.setPassword(DigestUtils.md5Hex(user.getPassword()));
            } else {
                user.setPassword(null);
            }
            int result = merchantUserService.modifyUser(user);
            if (result > 0) {
                merchantUserRoleService.removeMerchantUserRolesByUserId(user.getMemberId());
                RcMerchantUserRole userRole = new RcMerchantUserRole();
                userRole.setMemberId(memberId);
                userRole.setRoleId(roleId);
                merchantUserRoleService.addMerchantUserRole(userRole);
            }
        }
        return "{\"code\":\"1\"}";
    }

    @RequestMapping(value = "deleteMemberAndMemberRole")
    @ResponseBody
    public String deleteMemberAndMemberRole(int memberId) {
        int result = merchantUserService.removeUser(memberId);
        return "{\"code\":\"" + result + "\"}";
    }

    @RequestMapping(value = "enableMemberAndMemberRole")
    @ResponseBody
    public String enableMemberAndMemberRole(int memberId) {
        RcMerchantUser rcUser = merchantUserService.findUserById(memberId);
        rcUser.setDelFlag((byte) 0);
        merchantUserService.modifyUser(rcUser);
        return "{\"code\":\"" + 1 + "\"}";
    }
}

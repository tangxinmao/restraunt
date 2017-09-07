package com.socool.soft.service.impl;

import com.socool.soft.bo.RcMerchantMenu;
import com.socool.soft.bo.RcMerchantMenuRole;
import com.socool.soft.dao.RcMerchantMenuMapper;
import com.socool.soft.service.IMerchantMenuRoleService;
import com.socool.soft.service.IMerchantMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class MerchantMenuServiceImpl implements IMerchantMenuService {
	@Autowired
	private RcMerchantMenuMapper merchantMenuMapper;
	@Autowired
	private IMerchantMenuRoleService merchantMenuRoleService;

	@Override
	public int removeMerchantMenu(int menuId) {
		int result = merchantMenuMapper.deleteByPrimaryKey(menuId);
		if(result > 0) {
			merchantMenuRoleService.removeMerchantMenuRolesByMenuId(menuId);
			List<RcMerchantMenu> childMenus = findMerchantMenusByParentId(menuId);
			for(RcMerchantMenu childMenu : childMenus) {
				removeMerchantMenu(childMenu.getMenuId());
			}
		}
		return result;
	}

	@Override
	public List<RcMerchantMenu> findAllMerchantMenus() {
		RcMerchantMenu param = new RcMerchantMenu();
		return merchantMenuMapper.select(param);
	}

	@Override
	public RcMerchantMenu findMerchantMenuById(int menuId) {
		return merchantMenuMapper.selectByPrimaryKey(menuId);
	}

	@Override
	public List<RcMerchantMenu> findMerchantMenusByParentId(int parentId) {
		RcMerchantMenu param = new RcMerchantMenu();
		param.setParentId(parentId);
		return merchantMenuMapper.select(param);
	}

	@Override
	public int modifyMerchantMenu(RcMerchantMenu menu) {
		return merchantMenuMapper.updateByPrimaryKey(menu);
	}

	@Override
	public int addMerchantMenu(RcMerchantMenu menu) {
		if(menu.getParentId() == null) {
			menu.setParentId(0);
		}
		return merchantMenuMapper.insert(menu);
	}

	@Override
	public List<RcMerchantMenu> findSortedMerchantMenusByRoleId(int roleId) {
		List<RcMerchantMenuRole> menuRoles = merchantMenuRoleService.findMerchantMenuRolesByRoleId(roleId);
		if(CollectionUtils.isEmpty(menuRoles)){
			return new ArrayList<RcMerchantMenu>();
		}
		List<Integer> menuIds = new ArrayList<Integer>();
		for(RcMerchantMenuRole menuRole : menuRoles) {
			menuIds.add(menuRole.getMenuId());
		}
		RcMerchantMenu param = new RcMerchantMenu();
		param.setMenuIds(menuIds);
		param.setOrderBy("SEQ ASC");
		return merchantMenuMapper.select(param);
	}

	@Override
	public List<RcMerchantMenu> findMerchantMenusByRoleId(int roleId) {
		List<RcMerchantMenuRole> menuRoles = merchantMenuRoleService.findMerchantMenuRolesByRoleId(roleId);
		if(CollectionUtils.isEmpty(menuRoles)){
			return new ArrayList<RcMerchantMenu>();
		}
		List<Integer> menuIds = new ArrayList<Integer>();
		for(RcMerchantMenuRole menuRole : menuRoles) {
			menuIds.add(menuRole.getMenuId());
		}
		RcMerchantMenu param = new RcMerchantMenu();
		param.setMenuIds(menuIds);
		return merchantMenuMapper.select(param);
	}

	@Override
	public RcMerchantMenu findMerchantMenuByLocation(String location) {
		RcMerchantMenu param = new RcMerchantMenu();
		param.setLocation(location);
		return merchantMenuMapper.selectOne(param);
	}
}

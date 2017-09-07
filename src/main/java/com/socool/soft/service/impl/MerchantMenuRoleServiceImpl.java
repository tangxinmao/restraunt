package com.socool.soft.service.impl;

import com.socool.soft.bo.RcMerchantMenuRole;
import com.socool.soft.dao.RcMerchantMenuRoleMapper;
import com.socool.soft.service.IMerchantMenuRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantMenuRoleServiceImpl implements IMerchantMenuRoleService {
	@Autowired
	private RcMerchantMenuRoleMapper merchantMenuRoleMapper;

	@Override
	public int addMerchantMenuRole(RcMerchantMenuRole menuRole) {
		if(merchantMenuRoleMapper.insert(menuRole) > 0) {
			return menuRole.getMenuRoleId();
		}
		return 0;
	}

	@Override
	public List<RcMerchantMenuRole> findMerchantMenuRolesByRoleId(int roleId) {
		RcMerchantMenuRole param = new RcMerchantMenuRole();
		param.setRoleId(roleId);
		return merchantMenuRoleMapper.select(param);
	}

	@Override
	public int removeMerchantMenuRolesByRoleId(int roleId) {
		RcMerchantMenuRole param = new RcMerchantMenuRole();
		param.setRoleId(roleId);
		return merchantMenuRoleMapper.delete(param);
	}

	@Override
	public int removeMerchantMenuRolesByMenuId(int menuId) {
		RcMerchantMenuRole param = new RcMerchantMenuRole();
		param.setMenuId(menuId);
		return merchantMenuRoleMapper.delete(param);
	}

	@Override
	public int addMenuRole(RcMerchantMenuRole menuRole) {
		if(merchantMenuRoleMapper.insert(menuRole) > 0) {
			return menuRole.getMenuRoleId();
		}
		return 0;
	}

	@Override
	public int removeMenuRolesByRoleId(int roleId) {
		RcMerchantMenuRole param = new RcMerchantMenuRole();
		param.setRoleId(roleId);
		return merchantMenuRoleMapper.delete(param);
	}

	@Override
	public List<RcMerchantMenuRole> findMenuRolesByRoleId(int roleId) {
		RcMerchantMenuRole param = new RcMerchantMenuRole();
		param.setRoleId(roleId);
		return merchantMenuRoleMapper.select(param);
	}

	@Override
	public int removeMenuRolesByMenuId(int menuId) {
		RcMerchantMenuRole param = new RcMerchantMenuRole();
		param.setMenuId(menuId);
		return merchantMenuRoleMapper.delete(param);
	}
}

package com.socool.soft.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.socool.soft.bo.RcMerchantRole;
import com.socool.soft.bo.RcMerchantUserRole;
import com.socool.soft.dao.RcMerchantRoleMapper;
import com.socool.soft.service.IMerchantRoleService;
import com.socool.soft.service.IMerchantUserRoleService;
import com.socool.soft.vo.Page;
import com.socool.soft.vo.PageContext;

@Service
public class MerchantRoleServiceImpl implements IMerchantRoleService {
	@Autowired
	private RcMerchantRoleMapper merchantRoleMapper;
	@Autowired
	private IMerchantUserRoleService merchantUserRoleService;

	@Override
	public List<RcMerchantRole> findAllMerchantRoles() {
		return merchantRoleMapper.select(null);
	}

	@Override
	public List<RcMerchantRole> findAllPagedMerchantRoles(Page page) {
		PageContext.setPage(page);
		return merchantRoleMapper.select(null);
	}

	@Override
	public int addMerchantRole(RcMerchantRole role) {
		if(merchantRoleMapper.insert(role) > 0) {
			return role.getRoleId();
		}
		return 0;
	}

	@Override
	public RcMerchantRole findMerchantRoleById(int roleId) {
		return merchantRoleMapper.selectByPrimaryKey(roleId); 
	}

	@Override
	public int modifyMerchantRole(RcMerchantRole role) {
		if(merchantRoleMapper.updateByPrimaryKey(role) > 0) {
			return role.getRoleId();
		}
		return 0;
	}

	@Override
	public List<RcMerchantRole> findMerchantRolesByUserId(int userId) {
		List<RcMerchantUserRole> userRoles = merchantUserRoleService.findMerchantUserRolesByUserId(userId);
		if(CollectionUtils.isEmpty(userRoles)) {
			return new ArrayList<RcMerchantRole>();
		}
		List<Integer> roleIds = new ArrayList<Integer>();
		for(RcMerchantUserRole userRole : userRoles) {
			roleIds.add(userRole.getRoleId());
		}
		RcMerchantRole param = new RcMerchantRole();
		param.setRoleIds(roleIds);
		return merchantRoleMapper.select(param);
	}
}

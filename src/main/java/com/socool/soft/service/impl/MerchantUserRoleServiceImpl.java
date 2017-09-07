package com.socool.soft.service.impl;

import com.socool.soft.bo.RcMerchantUserRole;
import com.socool.soft.dao.RcMerchantUserRoleMapper;
import com.socool.soft.service.IMerchantUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantUserRoleServiceImpl implements IMerchantUserRoleService {
	@Autowired
	private RcMerchantUserRoleMapper merchantUserRoleMapper;

	@Override
	public int addMerchantUserRole(RcMerchantUserRole userRole) {
		if(merchantUserRoleMapper.insert(userRole) > 0) {
			return userRole.getMemberRoleId();
		}
		return 0;
	}

	@Override
	public List<RcMerchantUserRole> findMerchantUserRolesByRoleId(int roleId) {
		RcMerchantUserRole param = new RcMerchantUserRole();
		param.setRoleId(roleId);
		return merchantUserRoleMapper.select(param);
	}

	@Override
	public int removeMerchantUserRolesByUserId(int userId) {
		RcMerchantUserRole param = new RcMerchantUserRole();
		param.setMemberId(userId);
		return merchantUserRoleMapper.delete(param);
	}

//	@Override
//	public RcMerchantUserRole findMemberRoleByMemberId(int memberId) {
//		RcMerchantUserRole param = new RcMerchantUserRole();
//		param.setMemberId(memberId);
//		return memberRoleMapper.selectOne(param);
//	}

//	@Override
//	public int removeMemberRolesByRoleId(int roleId) {
//		RcMerchantUserRole param = new RcMerchantUserRole();
//		param.setRoleId(roleId);
//		return memberRoleMapper.delete(param);
//	}

	@Override
	public List<RcMerchantUserRole> findMerchantUserRolesByUserId(int userId) {
		RcMerchantUserRole param = new RcMerchantUserRole();
		param.setMemberId(userId);
		return merchantUserRoleMapper.select(param);
	}
}

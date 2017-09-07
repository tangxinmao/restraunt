package com.socool.soft.service;

import com.socool.soft.bo.RcUserRole;

import java.util.List;

public interface IUserRoleService {
	/**
	 * @return
	 */
	int addUserRole(RcUserRole userRole);

	/**
	 * 角色id查询角色和用户关系
	 * 
	 * @param roleId
	 * @return
	 */
	List<RcUserRole> findUserRolesByRoleId(int roleId);
	
//	RcMemberRole findMemberRoleByMemberId(int memberId);
	
	List<RcUserRole> findUserRolesByUserId(int userId);

	/**
	 * 删除用户角色
	 * @param memberId
	 * @return
	 */
	int removeUserRolesByUserId(int userId);
	
//	int removeMemberRolesByRoleId(int roleId);

}

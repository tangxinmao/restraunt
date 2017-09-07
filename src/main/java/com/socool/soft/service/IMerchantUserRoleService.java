package com.socool.soft.service;

import com.socool.soft.bo.RcMerchantUserRole;

import java.util.List;

public interface IMerchantUserRoleService {
    /**
     * @return
     */
    int addMerchantUserRole(RcMerchantUserRole userRole);

    /**
     * 角色id查询角色和用户关系
     *
     * @param roleId
     * @return
     */
    List<RcMerchantUserRole> findMerchantUserRolesByRoleId(int roleId);

//	RcMemberRole findMemberRoleByMemberId(int memberId);

    List<RcMerchantUserRole> findMerchantUserRolesByUserId(int userId);

    /**
     * 删除用户角色
     * @return
     */
    int removeMerchantUserRolesByUserId(int userId);
}

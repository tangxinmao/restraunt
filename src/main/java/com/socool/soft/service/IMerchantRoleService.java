package com.socool.soft.service;

import com.socool.soft.bo.RcMerchantRole;
import com.socool.soft.vo.Page;

import java.util.List;

public interface IMerchantRoleService {
    /**
     * 查询所有角色
     *
     * @return
     */
    List<RcMerchantRole> findAllMerchantRoles();
    List<RcMerchantRole> findAllPagedMerchantRoles(Page page);

    /**
     * 保存角色
     *
     * @return
     */
    int addMerchantRole(RcMerchantRole role);

    /**
     *
     * @param roleId
     * @return
     */
    RcMerchantRole findMerchantRoleById(int roleId);

    /**
     *
     * @return
     */
    int modifyMerchantRole(RcMerchantRole role);

    /**
     
     * @return
     */

    List<RcMerchantRole> findMerchantRolesByUserId(int userId);
}

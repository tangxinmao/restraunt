package com.socool.soft.service;

import com.socool.soft.bo.RcMerchantMenuRole;

import java.util.List;

public interface IMerchantMenuRoleService {
    int addMerchantMenuRole(RcMerchantMenuRole menuRole);

    List<RcMerchantMenuRole> findMerchantMenuRolesByRoleId(int roleId);

    int removeMerchantMenuRolesByRoleId(int roleId);

    int removeMerchantMenuRolesByMenuId(int menuId);

    int addMenuRole(RcMerchantMenuRole menuRole);

    int removeMenuRolesByRoleId(int roleId);


    List<RcMerchantMenuRole> findMenuRolesByRoleId(int roleId);

    int removeMenuRolesByMenuId(int menuId);
}

package com.socool.soft.service;

import com.socool.soft.bo.RcMerchantMenu;

import java.util.List;

public interface IMerchantMenuService {

    /**
     *
     * @param menuId
     * @return
     */
    int removeMerchantMenu(int menuId);

    /**
     * 查询所有item
     *
     * @return
     */
    List<RcMerchantMenu> findAllMerchantMenus();

    /**
     * @param id
     * @return
     */
    RcMerchantMenu findMerchantMenuById(int id);

    /**
     * 查询菜单子节点
     * @return
     */
    List<RcMerchantMenu> findMerchantMenusByParentId(int parentId);

    /**
     *
     * @param menu
     * @return
     */
    int modifyMerchantMenu(RcMerchantMenu menu);

    /**
     *
     * @param menu
     * @return
     */
    int addMerchantMenu(RcMerchantMenu menu);

    /**
     * 查询所有菜单并根据roleid标记checked字段
     *
     * @param roleId
     * @return
     */
    List<RcMerchantMenu> findSortedMerchantMenusByRoleId(int roleId);

    List<RcMerchantMenu> findMerchantMenusByRoleId(int roleId);

    RcMerchantMenu findMerchantMenuByLocation(String location);
}

package com.socool.soft.bo;

import java.io.Serializable;

public class RcMerchantMenuRole extends QueryParam implements Serializable {

	private static final long serialVersionUID = -1920360329451625110L;
	
	// db
	private Integer menuRoleId;
	private Integer menuId;
	private Integer roleId;

	public Integer getMenuRoleId() {
		return menuRoleId;
	}

	public void setMenuRoleId(Integer menuRoleId) {
		this.menuRoleId = menuRoleId;
	}

	public Integer getMenuId() {
		return menuId;
	}

	public void setMenuId(Integer menuId) {
		this.menuId = menuId;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
}
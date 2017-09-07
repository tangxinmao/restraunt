package com.socool.soft.bo;

import java.io.Serializable;
import java.util.List;

public class RcMerchantRole extends QueryParam implements Serializable {

	private static final long serialVersionUID = 325240798722416377L;
	
	// db
	private Integer roleId;
	private String name;
	private String description;
	
	// rel
	private List<RcMerchantMenu> menus;
	
	// se
	private List<Integer> roleIds;

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<RcMerchantMenu> getMenus() {
		return menus;
	}

	public void setMenus(List<RcMerchantMenu> menus) {
		this.menus = menus;
	}

	public List<Integer> getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(List<Integer> roleIds) {
		this.roleIds = roleIds;
	}
}
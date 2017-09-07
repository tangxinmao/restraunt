package com.socool.soft.bo;

import java.io.Serializable;

public class RcMerchantUserRole extends QueryParam implements Serializable {

	private static final long serialVersionUID = 5533397821436399419L;
	
	// db
	private Integer memberRoleId;
	private Integer memberId;
	private Integer roleId;

	public Integer getMemberRoleId() {
		return memberRoleId;
	}

	public void setMemberRoleId(Integer memberRoleId) {
		this.memberRoleId = memberRoleId;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
}
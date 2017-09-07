package com.socool.soft.dao;

import com.socool.soft.bo.RcMerchantUserRole;

import java.util.List;

public interface RcMerchantUserRoleMapper {

//	int deleteByPrimaryKey(int memberRoleId);

	int insert(RcMerchantUserRole record);

//	RcMerchantUserRole selectByPrimaryKey(int memberRoleId);

//	int updateByPrimaryKey(RcMerchantUserRole record);

	List<RcMerchantUserRole> select(RcMerchantUserRole record);

	RcMerchantUserRole selectOne(RcMerchantUserRole record);

	int delete(RcMerchantUserRole record);
}
package com.socool.soft.dao;

import com.socool.soft.bo.RcMerchantRole;

import java.util.List;

public interface RcMerchantRoleMapper {

	int deleteByPrimaryKey(int roleId);

	int insert(RcMerchantRole record);

	RcMerchantRole selectByPrimaryKey(int roleId);

	int updateByPrimaryKey(RcMerchantRole record);

	List<RcMerchantRole> select(RcMerchantRole record);

//	RcMerchantRole selectOne(RcMerchantRole record);

//	int delete(RcMerchantRole record);
}
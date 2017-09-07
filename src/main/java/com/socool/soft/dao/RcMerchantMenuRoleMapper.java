package com.socool.soft.dao;

import com.socool.soft.bo.RcMerchantMenuRole;

import java.util.List;

public interface RcMerchantMenuRoleMapper {

//	int deleteByPrimaryKey(int menuRoleId);

	int insert(RcMerchantMenuRole record);

//	RcMerchantMenuRole selectByPrimaryKey(int menuRoleId);

//	int updateByPrimaryKey(RcMerchantMenuRole record);

	List<RcMerchantMenuRole> select(RcMerchantMenuRole record);

//	RcMerchantMenuRole selectOne(RcMerchantMenuRole record);

	int delete(RcMerchantMenuRole record);
}
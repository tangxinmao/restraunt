package com.socool.soft.dao;

import java.util.List;

import com.socool.soft.bo.RcMerchantUser;

public interface RcMerchantUserMapper {

//	int deleteByPrimaryKey(int memberId);

	int insert(RcMerchantUser record);

	RcMerchantUser selectByPrimaryKey(int memberId);

	int updateByPrimaryKey(RcMerchantUser record);

	List<RcMerchantUser> select(RcMerchantUser record);

	RcMerchantUser selectOne(RcMerchantUser record);

//	int delete(RcMerchantUser record);
}
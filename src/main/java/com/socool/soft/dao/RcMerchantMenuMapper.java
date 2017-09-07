package com.socool.soft.dao;

import com.socool.soft.bo.RcMerchantMenu;

import java.util.List;

public interface RcMerchantMenuMapper {

	int deleteByPrimaryKey(int menuId);

	int insert(RcMerchantMenu record);

	RcMerchantMenu selectByPrimaryKey(int menuId);

	int updateByPrimaryKey(RcMerchantMenu record);

	List<RcMerchantMenu> select(RcMerchantMenu record);

	RcMerchantMenu selectOne(RcMerchantMenu record);

	int delete(RcMerchantMenu record);
}
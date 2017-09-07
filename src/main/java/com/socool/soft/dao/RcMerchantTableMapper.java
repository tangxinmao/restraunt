package com.socool.soft.dao;

import java.util.List;

import com.socool.soft.bo.RcMerchantTable;


public interface RcMerchantTableMapper {

	int deleteByPrimaryKey(int tableId);

	int insert(RcMerchantTable record);

	RcMerchantTable selectByPrimaryKey(int tableId);

	int updateByPrimaryKey(RcMerchantTable record);

	List<RcMerchantTable> select(RcMerchantTable record);

	RcMerchantTable selectOne(RcMerchantTable record);

	int delete(RcMerchantTable record);
}
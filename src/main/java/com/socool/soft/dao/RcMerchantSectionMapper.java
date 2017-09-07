package com.socool.soft.dao;

import java.util.List;

import com.socool.soft.bo.RcMerchantSection;

public interface RcMerchantSectionMapper {

	int deleteByPrimaryKey(int sectionId);

	int insert(RcMerchantSection record);

	RcMerchantSection selectByPrimaryKey(int sectionId);

	int updateByPrimaryKey(RcMerchantSection record);

	List<RcMerchantSection> select(RcMerchantSection record);

//	RcHelp selectOne(RcHelp record);

	int delete(RcMerchantSection record);
}
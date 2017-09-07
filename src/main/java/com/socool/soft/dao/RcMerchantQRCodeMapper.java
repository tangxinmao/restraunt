package com.socool.soft.dao;

import java.util.List;

import com.socool.soft.bo.RcMerchantQRCode;

public interface RcMerchantQRCodeMapper {

//	int deleteByPrimaryKey(int merchantQRCodeId);

	int insert(RcMerchantQRCode record);

//	RcMerchantQRCode selectByPrimaryKey(int merchantQRCodeId);

//	int updateByPrimaryKey(RcMerchantQRCode record);

	List<RcMerchantQRCode> select(RcMerchantQRCode record);

//	RcMerchantQRCode selectOne(RcMerchantQRCode record);

	int delete(RcMerchantQRCode record);
}
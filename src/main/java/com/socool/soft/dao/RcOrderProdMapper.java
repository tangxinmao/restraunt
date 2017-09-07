package com.socool.soft.dao;

import com.socool.soft.bo.RcOrderProd;

import java.util.List;

public interface RcOrderProdMapper {

//	int deleteByPrimaryKey(long orderPropId);

	int insert(RcOrderProd record);

	RcOrderProd selectByPrimaryKey(long orderProdId);

	int updateByPrimaryKey(RcOrderProd record);

	List<RcOrderProd> select(RcOrderProd record);

    int deleteByPrimaryKey(Long orderProdId);

//	RcOrderProd selectOne(RcOrderProd record);

//	int delete(RcOrderProd record);
}
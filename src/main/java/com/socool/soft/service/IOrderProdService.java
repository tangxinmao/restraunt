package com.socool.soft.service;

import com.socool.soft.bo.RcOrderProd;
import com.socool.soft.bo.RcProd;

import java.util.List;

public interface IOrderProdService {
	/**
	 * 批量保存orderProd
	 * @param rcOrderProds
	 */
//	void addOrderProds(List<RcOrderProd> orderProds);
	/**
	 * 保存orderProd
	 * @return
	 */
	long addOrderProd(RcOrderProd orderProd);
	/**
	 * 通过订单id查询相关商品
	 * @param orderId
	 * @return
	 */
	List<RcOrderProd> findOrderProdsByOrderId(long orderId);
	
	List<RcProd> findProdsByOrderId(long orderId);
	
	List<RcOrderProd> findOrderProds(RcOrderProd param);
	
	RcOrderProd findOrderProdById(long orderProdId);
	
	long modifyOrderProdById(RcOrderProd orderProd);

    void remove(RcOrderProd orderProd);
}

package com.socool.soft.service.merchant;

import java.util.List;

import com.socool.soft.bo.RcMerchantSection;
import com.socool.soft.bo.RcMerchantTable;
import com.socool.soft.bo.RcOrder;
import com.socool.soft.bo.RcProdCat;
import com.socool.soft.exception.SystemException;
import com.socool.soft.vo.Result;

public interface IOrderService {
	
	// 根据餐厅id查询区域
	List<RcMerchantSection> findSectionsByMerchantId(int merchantId);

	// 根据餐厅id及区域id查询所有餐桌，包含餐桌状态及订单数
	List<RcMerchantTable> findTablesByMerchantId(int merchantId, List<Integer> sectionIds);
	
	// 根据餐桌号查询订单列表
	List<RcOrder> findUnfinishedOrdersBySectionAndTable(int merchantId, int tableId);
	
	// 根据订单号查询订单
	RcOrder findOrderById(long orderId);
	
	// 接单
	void grabOrder(long orderId, int merchantUserId);
	
	// 全量更新订单
	void modifyOrder(RcOrder param, int merchantUserId);
	
//	// 增量更新订单
//	void modifyOrderProds(List<RcOrderProd> param, int merchantUserId);
	
	// 取消订单
	void cancelOrder(long orderId, int merchantUserId, String operationMemo);
	
	// 确认订单
	void confirmOrder(long orderId, int merchantUserId);
	
	// 结算
	void checkOutOrder(long orderId, int merchantUserId);
	
	// 查询菜单
	List<RcProdCat> findProdCats(RcProdCat param);
    //查询区域和餐桌
	List<RcMerchantSection> findSectionsWithTablesByMerchantId(int merchantId);

	/**
	 * 新增订单
	 * @param order
	 * @param merchantUserId
	 */
	Result<RcOrder> addOrder(RcOrder order, int merchantUserId) throws SystemException;

	/**
	 * 查询菜单
	 * @param merchantId
	 * @return
	 */
	List<RcProdCat> findProdCatsWithProds(int merchantId);
}

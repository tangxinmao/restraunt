package com.socool.soft.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.socool.soft.bo.RcOrderProd;
import com.socool.soft.bo.RcProd;
import com.socool.soft.bo.constant.OrderProdStatusEnum;
import com.socool.soft.dao.RcOrderProdMapper;
import com.socool.soft.service.IOrderProdService;
import com.socool.soft.service.IOrderProdSkuPropInfoService;
import com.socool.soft.service.IProdService;

@Service
public class OrderProdServiceImpl implements IOrderProdService {
	@Autowired
	private RcOrderProdMapper orderProdMapper;
	@Autowired
	private IProdService prodService;
	@Autowired
	private IOrderProdSkuPropInfoService orderProdSkuPropInfoService;

//	@Override
//	public void addOrderProds(List<RcOrderProd> orderProds) {
//		for(RcOrderProd orderProd : orderProds) {
//			orderProdMapper.insert(orderProd);
//		}
//	}

	@Override
	public long addOrderProd(RcOrderProd orderProd) {
		orderProd.setStatus(OrderProdStatusEnum.UNCONFIRMED.getValue());
		if(StringUtils.isBlank(orderProd.getProdSkuId())) {
			orderProd.setProdSkuId(String.valueOf(orderProd.getProdId()));
		}
		if(orderProdMapper.insert(orderProd) > 0) {
			return orderProd.getOrderProdId();
		}
		return 0;
	}

	@Override
	public List<RcOrderProd> findOrderProdsByOrderId(long orderId) {
		RcOrderProd param = new RcOrderProd();
		param.setOrderId(orderId);
		return orderProdMapper.select(param);
	}

	@Override
	public List<RcProd> findProdsByOrderId(long orderId) {
		List<RcProd> prods = new ArrayList<RcProd>();
		List<RcOrderProd> orderProds = findOrderProdsByOrderId(orderId);
		for(RcOrderProd orderProd : orderProds) {
			prods.add(prodService.findProdById(orderProd.getProdId()));
		}
		return prods;
	}

	@Override
	public List<RcOrderProd> findOrderProds(RcOrderProd param) {
		return orderProdMapper.select(param);
	}

	@Override
	public RcOrderProd findOrderProdById(long orderProdId) {
		return orderProdMapper.selectByPrimaryKey(orderProdId);
	}

	@Override
	public long modifyOrderProdById(RcOrderProd orderProd) {
		if(orderProdMapper.updateByPrimaryKey(orderProd) > 0) {
			return orderProd.getOrderProdId();
		}
		return 0;
	}

	@Override
	public void remove(RcOrderProd orderProd) {
		orderProdMapper.deleteByPrimaryKey(orderProd.getOrderProdId());
		orderProdSkuPropInfoService.removeOrderProdSkuPropInfosByOrderProdId(orderProd.getOrderProdId());
	}
}

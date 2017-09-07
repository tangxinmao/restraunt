package com.socool.soft.service.merchant.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.socool.soft.bo.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.socool.soft.bo.constant.CouponDiscountTypeEnum;
import com.socool.soft.bo.constant.CouponStatusEnum;
import com.socool.soft.bo.constant.MerchantMealStyleEnum;
import com.socool.soft.bo.constant.OrderBuyerStatusEnum;
import com.socool.soft.bo.constant.OrderEatTypeEnum;
import com.socool.soft.bo.constant.OrderFeeTypeEnum;
import com.socool.soft.bo.constant.OrderOperationTypeEnum;
import com.socool.soft.bo.constant.OrderSellerStatusEnum;
import com.socool.soft.bo.constant.ProdPriceMannerEnum;
import com.socool.soft.bo.constant.ProdStatusEnum;
import com.socool.soft.bo.constant.YesOrNoEnum;
import com.socool.soft.constant.PropertyConstants;
import com.socool.soft.exception.ErrorValue;
import com.socool.soft.exception.SystemException;
import com.socool.soft.service.IBuyerService;
import com.socool.soft.service.ICouponService;
import com.socool.soft.service.IMerchantSectionService;
import com.socool.soft.service.IMerchantService;
import com.socool.soft.service.IMerchantTableService;
import com.socool.soft.service.IOrderFeeService;
import com.socool.soft.service.IOrderOperationService;
import com.socool.soft.service.IOrderProdService;
import com.socool.soft.service.IOrderProdSkuPropInfoService;
import com.socool.soft.service.IProdCatService;
import com.socool.soft.service.IProdImgService;
import com.socool.soft.service.IProdService;
import com.socool.soft.service.IProdSkuPropInfoService;
import com.socool.soft.service.IProdSkuService;
import com.socool.soft.service.merchant.IOrderService;
import com.socool.soft.vo.Result;
import com.socool.soft.vo.constant.MerchantTableVOStatusEnum;

@Service
public class OrderMerchantServiceImpl implements IOrderService {
    @Autowired
    private IMerchantSectionService merchantSectionService;
    @Autowired
    private IMerchantTableService merchantTableService;
    @Autowired
    com.socool.soft.service.IOrderService orderService;
    @Autowired
    private IBuyerService buyerServive;
    @Autowired
    private IMerchantService merchantService;
    @Autowired
    private IBuyerService buyerService;
    @Autowired
    private IProdImgService prodImgService;
    @Autowired
    private PropertyConstants propertyConstants;
    @Autowired
    private IProdSkuService prodSkuService;
    @Autowired
    private IOrderProdService orderProdService;
    @Autowired
    private IOrderProdSkuPropInfoService orderProdSkuPropInfoService;
    @Autowired
    private IProdService prodService;
    @Autowired
    private IOrderOperationService orderOperationService;
    @Autowired
    private IProdCatService prodCatService;
    @Autowired
    private ICouponService couponService;
    @Autowired
    private IProdSkuPropInfoService prodSkuPropInfoService;
    @Autowired
    private IOrderFeeService orderFeeService;

    @Override
    public List<RcMerchantSection> findSectionsByMerchantId(int merchantId) {
        RcMerchant merchant = merchantService.findMerchantById(merchantId);
        if(merchant.getMealStyle() == MerchantMealStyleEnum.EAT_IN.getValue()) {
            return merchantSectionService.findMerchantSectionsByMerchantId(merchantId);
        }
        if(merchant.getMealStyle() == MerchantMealStyleEnum.TAKE_OUT.getValue()) {
            RcMerchantSection rcMerchantSection = new RcMerchantSection();
            rcMerchantSection.setName("TAKE OUT");
            rcMerchantSection.setMerchantId(merchantId);
            rcMerchantSection.setSectionId(0);
            return Arrays.asList(rcMerchantSection);
        }
        if(merchant.getMealStyle() == MerchantMealStyleEnum.EAT_IN_AND_TAKE_OUT.getValue()) {
        	List<RcMerchantSection> rcMerchantSectionList = merchantSectionService.findMerchantSectionsByMerchantId(merchantId);
            RcMerchantSection rcMerchantSection = new RcMerchantSection();
            rcMerchantSection.setName("TAKE OUT");
            rcMerchantSection.setMerchantId(merchantId);
            rcMerchantSection.setSectionId(0);
            rcMerchantSectionList.add(rcMerchantSection);
            return rcMerchantSectionList;
        }
        return new ArrayList<RcMerchantSection>();
    }

    @Override
    public List<RcMerchantTable> findTablesByMerchantId(int merchantId, List<Integer> sectionIds) {
        List<RcMerchantTable> rcMerchantTableList = merchantTableService.findByMerchantIdAndSectionIds(merchantId, sectionIds);
        for (RcMerchantTable rcMerchantTable : rcMerchantTableList) {
            RcMerchantSection rcMerchantSection = merchantSectionService.findMerchantSectionById(rcMerchantTable.getSectionId());
            rcMerchantTable.setSectionName(rcMerchantSection.getName());
        }
        List<RcOrder> rcOrders = orderService.findByMerchantIdAndSectionIds(merchantId, sectionIds);
        HashMap<Integer, List<RcOrder>> hashMap = new HashMap<Integer, List<RcOrder>>();
        if (!CollectionUtils.isEmpty(rcOrders))
            for (RcOrder rcOrder : rcOrders) {
                if (hashMap.keySet().contains(rcOrder.getTableId())) {
                    List<RcOrder> rcOrderList = hashMap.get(rcOrder.getTableId());
                    rcOrderList.add(rcOrder);
                } else {
                    hashMap.put(rcOrder.getTableId(), new ArrayList<RcOrder>());
                    hashMap.get(rcOrder.getTableId()).add(rcOrder);
                }
            }
        for (RcMerchantTable rcMerchantTable : rcMerchantTableList) {
        	List<RcOrder> orders = hashMap.get(rcMerchantTable.getTableId());
            if (!CollectionUtils.isEmpty(orders)) {
                rcMerchantTable.setOrderNum(orders.size());
                rcMerchantTable.setHasUnreceived(YesOrNoEnum.NO.getValue());
                rcMerchantTable.setStatus(MerchantTableVOStatusEnum.EMPTY.getValue());
                //判断订单状态逻辑
                for(RcOrder order : orders) {
                	if(order.getSellerStatus() == OrderSellerStatusEnum.CONFIRMED.getValue()) {
                		if(rcMerchantTable.getStatus() == MerchantTableVOStatusEnum.EMPTY.getValue()) {
            				rcMerchantTable.setStatus(MerchantTableVOStatusEnum.EATING.getValue());
                		}
                		continue;
                	}
                	if(order.getSellerStatus() == OrderSellerStatusEnum.GRABBED.getValue()) {
                		if(rcMerchantTable.getStatus() == MerchantTableVOStatusEnum.EMPTY.getValue() || rcMerchantTable.getStatus() == MerchantTableVOStatusEnum.EATING.getValue()) {
                            rcMerchantTable.setStatus(MerchantTableVOStatusEnum.UNCONFIRMED.getValue());
                		}
                		continue;
                	}
                	if(order.getSellerStatus() == OrderSellerStatusEnum.UNCONFIRMED.getValue()) {
                        rcMerchantTable.setHasUnreceived(YesOrNoEnum.YES.getValue());
                        rcMerchantTable.setStatus(MerchantTableVOStatusEnum.UNCONFIRMED.getValue());
                        break;
                	}
                }
            }else{
                rcMerchantTable.setOrderNum(0);
                rcMerchantTable.setStatus(MerchantTableVOStatusEnum.EMPTY.getValue());
            }
        }
        //构造一个take out
        if (sectionIds.contains(0)) {
            RcMerchantTable rcMerchantTable = new RcMerchantTable();
            rcMerchantTable.setSectionId(0);
            rcMerchantTable.setTableId(0);
            rcMerchantTable.setTableNumber(0);
            rcMerchantTable.setStatus(MerchantTableVOStatusEnum.EMPTY.getValue());
            rcMerchantTable.setMerchantId(merchantId);
        	List<RcOrder> orders = hashMap.get(0);
            if (!CollectionUtils.isEmpty(orders)) {
                rcMerchantTable.setOrderNum(orders.size());
                //判断订单状态逻辑
                for(RcOrder order : orders) {
                	if(order.getSellerStatus() == OrderSellerStatusEnum.CONFIRMED.getValue()) {
                		if(rcMerchantTable.getStatus() == MerchantTableVOStatusEnum.EMPTY.getValue()) {
            				rcMerchantTable.setStatus(MerchantTableVOStatusEnum.UNPACKING.getValue());
                		}
                		continue;
                	}
                	if(order.getSellerStatus() == OrderSellerStatusEnum.GRABBED.getValue()) {
                		if(rcMerchantTable.getStatus() == MerchantTableVOStatusEnum.EMPTY.getValue() || rcMerchantTable.getStatus() == MerchantTableVOStatusEnum.EATING.getValue()) {
                            rcMerchantTable.setStatus(MerchantTableVOStatusEnum.UNCONFIRMED.getValue());
                		}
                		continue;
                	}
                	if(order.getSellerStatus() == OrderSellerStatusEnum.UNCONFIRMED.getValue()) {
                        rcMerchantTable.setHasUnreceived(YesOrNoEnum.YES.getValue());
                        rcMerchantTable.setStatus(MerchantTableVOStatusEnum.UNCONFIRMED.getValue());
                        break;
                	}
                }
            }else{
                {
                    rcMerchantTable.setOrderNum(0);
                    rcMerchantTable.setStatus(MerchantTableVOStatusEnum.EMPTY.getValue());
                }
            }
            rcMerchantTableList.add(0, rcMerchantTable);
        }
        return rcMerchantTableList;
    }

    @Override
    public List<RcOrder> findUnfinishedOrdersBySectionAndTable(int merchantId, int tableId) {
        List<RcOrder> rcOrderList = orderService.findByMerchantIdAndTableId(merchantId, tableId);
        for (RcOrder rcOrder : rcOrderList) {
            RcBuyer rcBuyer = buyerServive.findBuyerById(rcOrder.getMemberId());
            if(rcBuyer==null)
                rcOrder.setMember(new RcBuyer());
            else
            rcOrder.setMember(rcBuyer);
        }
        return rcOrderList;
    }

    @Override
    public RcOrder findOrderById(long orderId) {
        RcOrder order = orderService.findOrderById(orderId);
        // order.setMerchant(merchantService.findMerchantById(order.getMerchantId()));
        // order.setMember(buyerService.findBuyerById(order.getMemberId()));
        order.setOrderProds(orderProdService.findOrderProdsByOrderId(orderId));
         if(order.getSectionId().equals(0)){
            order.setSectionName("Take Out");
        }else{
        order.setSectionName(merchantSectionService.findMerchantSectionById(order.getSectionId()).getName());
         order.setTableNumber(merchantTableService.findOneByTableId(order.getTableId()).getTableNumber());}
        for (RcOrderProd orderProd : order.getOrderProds()) {
            // orderProd.setProd(prodService.findProdById(orderProd.getProdId()));
            RcProdImg prodImg = prodImgService.findFirstProdImgByProdSkuId(orderProd.getProdSkuId());
            if (prodImg == null) {
                prodImg = prodImgService.findFirstProdImgByProdSkuId(String.valueOf(orderProd.getProdId()));
            }
            orderProd.setProdImgUrl(prodImg.getImgUrl());
            if (!orderProd.getProdSkuId().equals(String.valueOf(orderProd.getProdId()))) {
                List<RcOrderProdSkuPropInfo> orderProdSkuPropInfos = orderProdSkuPropInfoService.findOrderProdSkuPropInfosByOrderProdId(orderProd.getOrderProdId());
                orderProd.setOrderProdSkuPropInfos(orderProdSkuPropInfos);
            }
        }
        //加入服务员
        if (order.getSellerStatus() != OrderSellerStatusEnum.UNCONFIRMED.getValue()) {
            List<RcOrderOperation> rcOrderOperationList = orderOperationService.findOrderOperationsByOrderId(orderId);
            for (RcOrderOperation rcOrderOperation : rcOrderOperationList) {
                if (rcOrderOperation.getOperationType() == OrderOperationTypeEnum.GRAB.getValue()) {
                    order.setMerchantUserId(rcOrderOperation.getMemberId());
                }
            }
        }
        
        List<RcOrderFee> orderFees = orderFeeService.findOrderFeesByOrderId(orderId);
      //  RcMerchant merchant = merchantService.findMerchantById(order.getMerchantId());
     /*   order.setTaxRate(merchant.getTaxRate());
        order.setServiceFeeRate(merchant.getServiceCharge());*/
        for(RcOrderFee orderFee : orderFees) {
 			if(orderFee.getType() == OrderFeeTypeEnum.MODIFICATION.getValue()) {
 				order.setRounding(orderFee.getAmount().abs());
 			}
 			if(orderFee.getType() == OrderFeeTypeEnum.TAX.getValue()) {
 				order.setTax(orderFee.getAmount());
 				order.setTaxRate(orderFee.getFeeRate());
 			}
 			if(orderFee.getType() == OrderFeeTypeEnum.SERVICE_FEE.getValue()) {
 				order.setServiceFee(orderFee.getAmount());
 				order.setServiceFeeRate(orderFee.getFeeRate());
 			}
 		}
        return order;
    }


    @Override
    public void grabOrder(long orderId, int merchantUserId) {
        RcOrder rcOrder = orderService.findOrderById(orderId);
        rcOrder.setSellerStatus(OrderSellerStatusEnum.GRABBED.getValue());
        //流程操作
        RcOrderOperation rcOrderOperation = new RcOrderOperation();
        rcOrderOperation.setOperationTime(new Date());
        rcOrderOperation.setOrderId(orderId);
        rcOrderOperation.setMemberId(merchantUserId);
        rcOrderOperation.setOperationType(OrderOperationTypeEnum.GRAB.getValue());
        orderOperationService.addOrderOperation(rcOrderOperation);
        orderService.modifyOrder(rcOrder);
    }

    @Override
    public void modifyOrder(RcOrder param, int merchantUserId) {
    	RcOrder order = orderService.findOrderById(param.getOrderId());
        order.setCustomerCount(param.getCustomerCount());
        order.setBuyerMemo(param.getBuyerMemo());
        order.setTableId(param.getTableId());
        order.setSectionName(param.getSectionName());
        order.setSectionId(param.getSectionId());
        order.setEatType(param.getEatType());
        RcMerchant merchant = merchantService.findMerchantById(order.getMerchantId());
    	List<RcOrderProd> orderProds = orderProdService.findOrderProdsByOrderId(param.getOrderId());
        order.setOrderProds(orderProds);
        
        RcCoupon couponParam = new RcCoupon();
        couponParam.setReceiveEndTimeFrom(new Date());
        couponParam.setNeedGet(YesOrNoEnum.NO.getValue());
        couponParam.setStatus(CouponStatusEnum.PUBLISHED.getValue());
        couponParam.setDelFlag(YesOrNoEnum.NO.getValue());
        couponParam.setMerchantId(merchant.getMerchantId());
        List<RcCoupon> coupons = couponService.findCoupons(couponParam);
		for(RcOrderProd newOrderProd : param.getOrderProds()) {
			boolean isFound = false;
			Iterator<RcOrderProd> iter = orderProds.iterator();
			while(iter.hasNext()) {
				RcOrderProd orderProd = iter.next();
				if(orderProd.getOrderProdId().equals(newOrderProd.getOrderProdId())) {
					if(newOrderProd.getQuantity() == 0) {
						orderProdService.remove(orderProd);
						if(orderProd.getQuantity() != null) {
			    	        order.setProductPrice(order.getProductPrice().subtract(orderProd.getAmount()));
			            }
					} else if(!newOrderProd.getQuantity().equals(orderProd.getQuantity())) {
				        BigDecimal oldAmount = orderProd.getAmount() == null ? new BigDecimal(0) : orderProd.getAmount();
				        orderProd.setQuantity(newOrderProd.getQuantity());
//    				        if (orderProd.getPriceManner() == ProdPriceMannerEnum.BY_WEIGHT.getValue()) {
//    				            orderProd.setAmount(orderProd.getProdPrice().multiply(new BigDecimal(String.valueOf(orderProd.getQuantity())).divide(new BigDecimal(orderProd.getMeasureUnitCount()), 2, RoundingMode.HALF_UP)));
//    				        } else {
				            orderProd.setAmount(orderProd.getProdPrice().multiply(new BigDecimal(String.valueOf(orderProd.getQuantity()))));
//    				        }
				        order.setProductPrice(order.getProductPrice().subtract(oldAmount).add(orderProd.getAmount()));
						orderProdService.modifyOrderProdById(orderProd);
					}
					isFound = true;
					iter.remove();
		            break;
				}
			}
			if(!isFound) {
				RcProd prod = prodService.findProdById(newOrderProd.getProdId());
				newOrderProd.setPriceManner(prod.getPriceManner());
				newOrderProd.setMeasureUnit(prod.getMeasureUnit());
				newOrderProd.setMeasureUnitCount(prod.getMeasureUnitCount());
	            if(prod.getStatus() == ProdStatusEnum.SOLD_OUT.getValue()) {
	                if (prod.getDelFlag() == YesOrNoEnum.YES.getValue()) {
	                    return;
	                } else {
	                    return;
	                }
	            }
	            if(prod.getStatus() == ProdStatusEnum.NOT_SELLING.getValue()) {
	                if (prod.getDelFlag() == YesOrNoEnum.YES.getValue()) {
	                    return;
	                } else {
	                    return;
	                }
	            }
	            if (StringUtils.isBlank(newOrderProd.getProdSkuId()) || !newOrderProd.getProdSkuId().contains("_")) {
	            	newOrderProd.setProdPrice(prod.getPrice());
	            	newOrderProd.setProdOriginPrice(prod.getOriginPrice());
	            } else {
	                RcProdSku prodSku = prodSkuService.findProdSkuByProdSkuId(newOrderProd.getProdSkuId());
	                newOrderProd.setProdOriginPrice(prodSku.getOriginPrice());
	                newOrderProd.setProdPrice(prodSku.getPrice());
	                List<RcOrderProdSkuPropInfo> orderProdSkuPropInfos = new ArrayList<RcOrderProdSkuPropInfo>();
	                String[] prodPropEnumIds = newOrderProd.getProdSkuId().split("_");
	                for(int i = 1; i < prodPropEnumIds.length; i++) {
	                    RcProdSkuPropInfo prodSkuPropInfo = prodSkuPropInfoService.findProdSkuPropInfoByProdIdAndProdSkuPropEnumId(prod.getProdId(), Integer.parseInt(prodPropEnumIds[i]));
	                    RcOrderProdSkuPropInfo orderProdSkuPropInfo = new RcOrderProdSkuPropInfo();
	                    orderProdSkuPropInfo.setProdPropName(prodSkuPropInfo.getProdPropName());
	                    orderProdSkuPropInfo.setProdPropVal(prodSkuPropInfo.getProdPropVal());
	                    orderProdSkuPropInfos.add(orderProdSkuPropInfo);
	                }
	                newOrderProd.setOrderProdSkuPropInfos(orderProdSkuPropInfos);
	            }

	            List<RcCoupon> prodCoupons = new ArrayList<RcCoupon>();
	            List<RcCoupon> prodCatCoupons = new ArrayList<RcCoupon>();
	            for(RcCoupon coupon : coupons) {
	                if(coupon.getProdId() > 0 && coupon.getProdId().equals(prod.getProdId())) {
	                    prodCoupons.add(coupon);
	                    continue;
	                }
	                if(coupon.getProdId() == 0 && (coupon.getProdCatId() == 0 || coupon.getProdCatId() > 0 && coupon.getProdCatId().equals(prod.getProdCatId()))) {
	                    prodCatCoupons.add(coupon);
	                    continue;
	                }
	            }

	            if(!CollectionUtils.isEmpty(prodCoupons)) {
	                RcCoupon coupon = prodCoupons.get(0);
	                newOrderProd.setProdOriginPrice(newOrderProd.getProdPrice());
	                if(coupon.getDiscountType() == CouponDiscountTypeEnum.PERCENTAGE.getValue()) {
	                	newOrderProd.setProdPrice(newOrderProd.getProdPrice().multiply(coupon.getAmount()));
	                } else if(coupon.getDiscountType() == CouponDiscountTypeEnum.SUBSTRACT.getValue()) {
	                	newOrderProd.setProdPrice(newOrderProd.getProdPrice().subtract(coupon.getAmount()));
	                } else if(coupon.getDiscountType() == CouponDiscountTypeEnum.FIXED.getValue()) {
	                	newOrderProd.setProdPrice(coupon.getAmount());
	                }
	            } else if(!CollectionUtils.isEmpty(prodCatCoupons)) {
	                RcCoupon coupon = prodCatCoupons.get(0);
	                newOrderProd.setProdOriginPrice(newOrderProd.getProdPrice());
	                if(coupon.getDiscountType() == CouponDiscountTypeEnum.PERCENTAGE.getValue()) {
	                	newOrderProd.setProdPrice(newOrderProd.getProdPrice().multiply(coupon.getAmount()));
	                } else if(coupon.getDiscountType() == CouponDiscountTypeEnum.SUBSTRACT.getValue()) {
	                	newOrderProd.setProdPrice(newOrderProd.getProdPrice().subtract(coupon.getAmount()));
	                } else if(coupon.getDiscountType() == CouponDiscountTypeEnum.FIXED.getValue()) {
	                	newOrderProd.setProdPrice(coupon.getAmount());
	                }
	            }
//    	            if(prod.getPriceManner() == ProdPriceMannerEnum.BY_WEIGHT.getValue()) {
//    	            	newOrderProd.setAmount(newOrderProd.getProdPrice().multiply(new BigDecimal(String.valueOf(newOrderProd.getQuantity())).divide(new BigDecimal(prod.getMeasureUnitCount()), 2, RoundingMode.HALF_UP)));
//    	            } else {
	            	newOrderProd.setAmount(newOrderProd.getProdPrice().multiply(new BigDecimal(String.valueOf(newOrderProd.getQuantity()))));
//    	            }
	            order.setProductPrice(order.getProductPrice().add(newOrderProd.getAmount()));

	            newOrderProd.setProdName(prod.getName());
	            
	            newOrderProd.setOrderId(order.getOrderId());
                long orderProdId = orderProdService.addOrderProd(newOrderProd);
                List<RcOrderProdSkuPropInfo> orderProdSkuPropInfos = newOrderProd.getOrderProdSkuPropInfos();
                if(!CollectionUtils.isEmpty(orderProdSkuPropInfos)) {
                    for(RcOrderProdSkuPropInfo orderProdSkuPropInfo : orderProdSkuPropInfos) {
                        orderProdSkuPropInfo.setOrderProdId(orderProdId);
                        orderProdSkuPropInfoService.addOrderProdSkuPropInfo(orderProdSkuPropInfo);
                    }
                }
			}
		}
		Iterator<RcOrderProd> iter = orderProds.iterator();
		while(iter.hasNext()) {
			RcOrderProd orderProd = iter.next();
			orderProdService.remove(orderProd);
			if(orderProd.getQuantity() != null) {
    	        order.setProductPrice(order.getProductPrice().subtract(orderProd.getAmount()));
            }
		}
		
		order.setServiceFee(order.getProductPrice().multiply(new BigDecimal(String.valueOf(merchant.getServiceCharge()))).setScale(0, RoundingMode.HALF_UP));//服务费
        RcOrderFee serviceFee = orderFeeService.findOrderFeeByType(order.getOrderId(), OrderFeeTypeEnum.SERVICE_FEE.getValue());
        if(serviceFee == null) {
        	serviceFee = new RcOrderFee();
        	serviceFee.setOrderId(order.getOrderId());
        	serviceFee.setAmount(order.getServiceFee());
        	serviceFee.setType(OrderFeeTypeEnum.SERVICE_FEE.getValue());
        	serviceFee.setAddToTotal(YesOrNoEnum.YES.getValue());
        	serviceFee.setFeeRate(merchant.getServiceCharge());
            orderFeeService.addOrderFee(serviceFee);
        } else {
        	serviceFee.setAmount(order.getServiceFee());
        	serviceFee.setFeeRate(merchant.getServiceCharge());
            orderFeeService.modifyOrderFee(serviceFee);
        }
        
        order.setTax(order.getProductPrice().add(order.getServiceFee()).multiply(new BigDecimal(String.valueOf(merchant.getTaxRate()))).setScale(0, RoundingMode.HALF_UP));//税金
        RcOrderFee tax = orderFeeService.findOrderFeeByType(order.getOrderId(), OrderFeeTypeEnum.TAX.getValue());
        if(tax == null) {
        	tax = new RcOrderFee();
        	tax.setOrderId(order.getOrderId());
            tax.setAmount(order.getTax());
            tax.setType(OrderFeeTypeEnum.TAX.getValue());
            tax.setAddToTotal(YesOrNoEnum.YES.getValue());
            tax.setFeeRate(merchant.getTaxRate());
            orderFeeService.addOrderFee(tax);
        } else {
            tax.setAmount(order.getTax());
            tax.setFeeRate(merchant.getTaxRate());
            orderFeeService.modifyOrderFee(tax);
        }
        
        order.setPayPrice(order.getProductPrice().add(order.getTax()).add(order.getServiceFee()));//实际支付金额
        orderService.modifyOrder(order);
        RcOrderOperation orderOperation = new RcOrderOperation();
        orderOperation.setOperationType(OrderOperationTypeEnum.MODIFY.getValue());
        orderOperation.setOrderId(order.getOrderId());
        orderOperation.setMemberId(merchantUserId);
        orderOperationService.addOrderOperation(orderOperation);
    }

//    @Override
//    public void modifyOrderProds(List<RcOrderProd> param, int merchantUserId) {
//
//    }

    @Override
    public void cancelOrder(long orderId, int merchantUserId, String operationMemo) {
        RcOrder rcOrder = orderService.findOrderById(orderId);
        rcOrder.setSellerStatus(OrderSellerStatusEnum.CANCELED.getValue());
        rcOrder.setBuyerStatus(OrderBuyerStatusEnum.CANCELED.getValue());
        //流程操作
        RcOrderOperation rcOrderOperation = new RcOrderOperation();
        rcOrderOperation.setOrderId(orderId);
        rcOrderOperation.setOperationMemo(operationMemo);
        rcOrderOperation.setMemberId(merchantUserId);
        rcOrderOperation.setOperationType(OrderOperationTypeEnum.CANCEL.getValue());
        orderOperationService.addOrderOperation(rcOrderOperation);
        orderService.modifyOrder(rcOrder);
    }

    @Override
    public void confirmOrder(long orderId, int merchantUserId) {
        RcOrder rcOrder = orderService.findOrderById(orderId);
        rcOrder.setSellerStatus(OrderSellerStatusEnum.CONFIRMED.getValue());
        //流程操作
        RcOrderOperation rcOrderOperation = new RcOrderOperation();
        rcOrderOperation.setOperationTime(new Date());
        rcOrderOperation.setOrderId(orderId);
        rcOrderOperation.setMemberId(merchantUserId);
        rcOrderOperation.setOperationType(OrderOperationTypeEnum.CONFIRM.getValue());
        orderOperationService.addOrderOperation(rcOrderOperation);
        orderService.modifyOrder(rcOrder);
    }

    @Override
    public void checkOutOrder(long orderId, int merchantUserId) {
        RcOrder rcOrder = orderService.findOrderById(orderId);
        rcOrder.setOrderProds(orderProdService.findOrderProdsByOrderId(orderId));
        for (RcOrderProd orderProd : rcOrder.getOrderProds()) {
        	RcProd prod = prodService.findProdById(orderProd.getProdId());
        	prod.setSoldNum(prod.getSoldNum().add(new BigDecimal(String.valueOf(prod.getPriceManner() == ProdPriceMannerEnum.BY_WEIGHT.getValue() ? 1 : orderProd.getQuantity()))));
			prodService.modifyProd(prod);
        }
        
        BigDecimal roudingScale = new BigDecimal(500);
        BigDecimal rounding = rcOrder.getPayPrice().divide(roudingScale, 0, RoundingMode.HALF_UP).multiply(roudingScale).subtract(rcOrder.getPayPrice());
        RcOrderFee orderFee = new RcOrderFee();
		orderFee.setOrderId(orderId);
		orderFee.setType(OrderFeeTypeEnum.MODIFICATION.getValue());
		orderFee.setAmount(rounding);
		orderFee.setAddToTotal(YesOrNoEnum.YES.getValue());
		orderFeeService.addOrderFee(orderFee);

		rcOrder.setBuyerStatus(OrderBuyerStatusEnum.FINISHED.getValue());
        rcOrder.setSellerStatus(OrderSellerStatusEnum.FINISHED.getValue());
        //流程操作
        RcOrderOperation rcOrderOperation = new RcOrderOperation();
        rcOrderOperation.setOperationTime(new Date());
        rcOrderOperation.setOrderId(orderId);
        rcOrderOperation.setMemberId(merchantUserId);
        rcOrderOperation.setOperationType(OrderOperationTypeEnum.CHECK_OUT.getValue());
        orderOperationService.addOrderOperation(rcOrderOperation);
        rcOrder.setPayPrice(rcOrder.getPayPrice().add(rounding));
        orderService.modifyOrder(rcOrder);
    }

    @Override
    public List<RcProdCat> findProdCats(RcProdCat param) {

        return null;
    }

    @Override
    public List<RcMerchantSection> findSectionsWithTablesByMerchantId(int merchantId) {
        List<RcMerchantSection> rcMerchantSections = merchantSectionService.findMerchantSectionsByMerchantId(merchantId);
        for (RcMerchantSection rcMerchantSection : rcMerchantSections) {
            rcMerchantSection.setTables(merchantTableService.findBySectionId(rcMerchantSection.getSectionId()));
        }
        return rcMerchantSections;
    }

    @Override
    public Result<RcOrder> addOrder(RcOrder order, int merchantUserId) throws SystemException {
        Result<RcOrder> result = new Result<RcOrder>();
        RcBuyer buyer = null;
        if (StringUtils.isNotBlank(order.getMember().getMobile())) {
            buyer = buyerService.findBuyerByMobile(order.getMember().getMobile());
            if (buyer == null) {
                buyer = new RcBuyer();
                buyer.setMobile(order.getMember().getMobile());
                buyer.setName(order.getMember().getName());
                buyer.setPassword(DigestUtils.md5Hex("111111"));
                buyerService.addBuyer(buyer);
            }
        }
        RcMerchant merchant = merchantService.findMerchantById(order.getMerchantId());
        order.setMerchantId(order.getMerchantId());
        order.setMerchant(merchant);
        order.setCityId(merchant.getCityId());
        order.setVendorId(merchant.getVendorId());
        order.setOrderId(generateOrderId());
        order.setMemberId(buyer == null ? 0 : buyer.getMemberId());
        order.setProductPrice(new BigDecimal(0));
        order.setBuyerStatus(OrderBuyerStatusEnum.UNCONFIRMED.getValue());
        order.setSellerStatus(OrderSellerStatusEnum.UNCONFIRMED.getValue());
        if (order.getEatType() == OrderEatTypeEnum.TAKE_OUT.getValue()) {
        	order.setSectionId(0);
        	order.setTableId(0);
        	order.setSectionName(null);
        	order.setTableNumber(null);
        } else {
        	RcMerchantTable table = merchantSectionService.findMerchantTableById(order.getTableId());
        	if(table != null) {
        		order.setSectionId(table.getSectionId());
        	} else {
            	order.setSectionId(0);
        	}
        }
        order.setTaxRate(merchant.getTaxRate());
        RcCoupon couponParam = new RcCoupon();
        couponParam.setReceiveEndTimeFrom(new Date());
        couponParam.setNeedGet(YesOrNoEnum.NO.getValue());
        couponParam.setStatus(CouponStatusEnum.PUBLISHED.getValue());
        couponParam.setDelFlag(YesOrNoEnum.NO.getValue());
        couponParam.setMerchantId(merchant.getMerchantId());
        List<RcCoupon> coupons = couponService.findCoupons(couponParam);
        for (RcOrderProd orderProd : order.getOrderProds()) {
            RcProd prod = prodService.findProdById(orderProd.getProdId());
            orderProd.setPriceManner(prod.getPriceManner());
            orderProd.setMeasureUnit(prod.getMeasureUnit());
            orderProd.setMeasureUnitCount(prod.getMeasureUnitCount());
            if (prod.getStatus() == ProdStatusEnum.SOLD_OUT.getValue()) {
                if (prod.getDelFlag() == YesOrNoEnum.YES.getValue()) {
                    result.setCode(ErrorValue.GOODS_DELETED.getStr());
                    result.setResult("Sorry，" + prod.getName() + " has been sold out.");
                    return result;
                } else {
                    result.setCode(ErrorValue.GOODS_SHELF.getStr());
                    result.setResult("Sorry，" + prod.getName() + " has been sold out.");
                    return result;
                }
            }
            if (prod.getStatus() == ProdStatusEnum.NOT_SELLING.getValue()) {
                if (prod.getDelFlag() == YesOrNoEnum.YES.getValue()) {
                    result.setCode(ErrorValue.GOODS_DELETED.getStr());
                    result.setResult("Sorry，" + prod.getName() + " has been sold out.");
                    return result;
                } else {
                    result.setCode(ErrorValue.GOODS_SHELF.getStr());
                    result.setResult("Sorry，" + prod.getName() + " has been sold out.");
                    return result;
                }
            }
            if (StringUtils.isBlank(orderProd.getProdSkuId())||!orderProd.getProdSkuId().contains("_")) {
                orderProd.setProdPrice(prod.getPrice());
                orderProd.setProdOriginPrice(prod.getOriginPrice());
            } else {
                RcProdSku prodSku = prodSkuService.findProdSkuByProdSkuId(orderProd.getProdSkuId());
                orderProd.setProdOriginPrice(prodSku.getOriginPrice());
                orderProd.setProdPrice(prodSku.getPrice());
                List<RcOrderProdSkuPropInfo> orderProdSkuPropInfos = new ArrayList<RcOrderProdSkuPropInfo>();
                String[] prodPropEnumIds = orderProd.getProdSkuId().split("_");
                for (int i = 1; i < prodPropEnumIds.length; i++) {
                    RcProdSkuPropInfo prodSkuPropInfo = prodSkuPropInfoService.findProdSkuPropInfoByProdIdAndProdSkuPropEnumId(prod.getProdId(), Integer.parseInt(prodPropEnumIds[i]));
                    RcOrderProdSkuPropInfo orderProdSkuPropInfo = new RcOrderProdSkuPropInfo();
                    orderProdSkuPropInfo.setProdPropName(prodSkuPropInfo.getProdPropName());
                    orderProdSkuPropInfo.setProdPropVal(prodSkuPropInfo.getProdPropVal());
                    orderProdSkuPropInfos.add(orderProdSkuPropInfo);
                }
                orderProd.setOrderProdSkuPropInfos(orderProdSkuPropInfos);
            }

            List<RcCoupon> prodCoupons = new ArrayList<RcCoupon>();
            List<RcCoupon> prodCatCoupons = new ArrayList<RcCoupon>();
            for (RcCoupon coupon : coupons) {
                if (coupon.getProdId() > 0 && coupon.getProdId().equals(prod.getProdId())) {
                    prodCoupons.add(coupon);
                    continue;
                }
                if (coupon.getProdId() == 0 && (coupon.getProdCatId() == 0 || coupon.getProdCatId() > 0 && coupon.getProdCatId().equals(prod.getProdCatId()))) {
                    prodCatCoupons.add(coupon);
                    continue;
                }
            }

            if(!CollectionUtils.isEmpty(prodCoupons)) {
                RcCoupon coupon = prodCoupons.get(0);
                orderProd.setProdOriginPrice(orderProd.getProdPrice());
                if (coupon.getDiscountType() == CouponDiscountTypeEnum.PERCENTAGE.getValue()) {
                    orderProd.setProdPrice(orderProd.getProdPrice().multiply(coupon.getAmount()));
                } else if (coupon.getDiscountType() == CouponDiscountTypeEnum.SUBSTRACT.getValue()) {
                    orderProd.setProdPrice(orderProd.getProdPrice().subtract(coupon.getAmount()));
                } else if (coupon.getDiscountType() == CouponDiscountTypeEnum.FIXED.getValue()) {
                    orderProd.setProdPrice(coupon.getAmount());
                }
            } else if(!CollectionUtils.isEmpty(prodCatCoupons)) {
                RcCoupon coupon = prodCatCoupons.get(0);
                orderProd.setProdOriginPrice(orderProd.getProdPrice());
                if (coupon.getDiscountType() == CouponDiscountTypeEnum.PERCENTAGE.getValue()) {
                    orderProd.setProdPrice(orderProd.getProdPrice().multiply(coupon.getAmount()));
                } else if (coupon.getDiscountType() == CouponDiscountTypeEnum.SUBSTRACT.getValue()) {
                    orderProd.setProdPrice(orderProd.getProdPrice().subtract(coupon.getAmount()));
                } else if (coupon.getDiscountType() == CouponDiscountTypeEnum.FIXED.getValue()) {
                    orderProd.setProdPrice(coupon.getAmount());
                }
            }
            if (prod.getPriceManner() == ProdPriceMannerEnum.BY_WEIGHT.getValue()) {
                orderProd.setQuantity(null);
//              orderProd.setAmount(orderProd.getProdPrice().multiply(new BigDecimal(orderProd.getQuantity()).divide(new BigDecimal(prod.getMeasureUnitCount()), 2, RoundingMode.HALF_UP)));
            } else {
                orderProd.setAmount(orderProd.getProdPrice().multiply(new BigDecimal(String.valueOf(orderProd.getQuantity()))));
                order.setProductPrice(order.getProductPrice().add(orderProd.getAmount()));
            }

            orderProd.setProdName(prod.getName());
        }
        order.setServiceFee(order.getProductPrice().multiply(new BigDecimal(String.valueOf(merchant.getServiceCharge()))).setScale(0, RoundingMode.HALF_UP));//服务费
        order.setTax(order.getProductPrice().add(order.getServiceFee()).multiply(new BigDecimal(String.valueOf(merchant.getTaxRate()))).setScale(0, RoundingMode.HALF_UP));//税金
        order.setPayPrice(order.getProductPrice().add(order.getTax()).add(order.getServiceFee()));//实际支付金额

        RcOrderOperation orderOperation = new RcOrderOperation();
        orderOperation.setOperationType(OrderOperationTypeEnum.CREATE.getValue());
        orderOperation.setOrderId(order.getOrderId());
        orderOperation.setMemberId(order.getMerchantUserId());
        orderOperation.setOperationTime(new Date());
        order.setOrderOperations(Arrays.asList(orderOperation));
        orderService.addOrder(order);

        RcOrderFee tax = new RcOrderFee();
        tax.setOrderId(order.getOrderId());
        tax.setAmount(order.getTax());
        tax.setType(OrderFeeTypeEnum.TAX.getValue());
        tax.setAddToTotal(YesOrNoEnum.YES.getValue());
        tax.setFeeRate(merchant.getTaxRate());
        orderFeeService.addOrderFee(tax);
        RcOrderFee serviceFee = new RcOrderFee();
        serviceFee.setOrderId(order.getOrderId());
        serviceFee.setAmount(order.getServiceFee());
        serviceFee.setType(OrderFeeTypeEnum.SERVICE_FEE.getValue());
        serviceFee.setAddToTotal(YesOrNoEnum.YES.getValue());
        serviceFee.setFeeRate(merchant.getServiceCharge());
        orderFeeService.addOrderFee(serviceFee);

        if(!CollectionUtils.isEmpty(order.getOrderProds())) {
            for(RcOrderProd orderProd : order.getOrderProds()) {
                orderProd.setOrderId(order.getOrderId());
                long orderProdId = orderProdService.addOrderProd(orderProd);
//                RcProd prod = prodService.findProdById(orderProd.getProdId());
//                prod.setSoldNum(prod.getSoldNum() + (prod.getPriceManner() == ProdPriceMannerEnum.BY_WEIGHT.getValue() ? 1 : orderProd.getQuantity()));
//                prodService.modifyProd(prod);
                List<RcOrderProdSkuPropInfo> orderProdSkuPropInfos = orderProd.getOrderProdSkuPropInfos();
                if(!CollectionUtils.isEmpty(orderProdSkuPropInfos)) {
                    for(RcOrderProdSkuPropInfo orderProdSkuPropInfo : orderProdSkuPropInfos) {
                        orderProdSkuPropInfo.setOrderProdId(orderProdId);
                        orderProdSkuPropInfoService.addOrderProdSkuPropInfo(orderProdSkuPropInfo);
                    }
                }
            }
        }

        if(!CollectionUtils.isEmpty(order.getOrderOperations())) {
            for(RcOrderOperation operation : order.getOrderOperations()) {
                orderOperationService.addOrderOperation(operation);
            }
        }
        result.setData(order);
        return result;
    }

    private long generateOrderId() throws SystemException {
        StringBuilder sb = new StringBuilder("");
        sb.append(propertyConstants.systemMachineId).append(propertyConstants.systemServerId);
        int hashCode = UUID.randomUUID().toString().hashCode();
        if (hashCode < 0) {
            hashCode = -hashCode;
            sb.append(1);
        } else {
            sb.append(0);
        }
        DecimalFormat format = new DecimalFormat("0000000000");
        sb.append(format.format(hashCode));
        return Long.parseLong(sb.toString());
    }

    @Override
    public List<RcProdCat> findProdCatsWithProds(int merchantId) {
        RcProdCat prodCat = new RcProdCat();
        prodCat.setMerchantId(merchantId);
        List<RcProdCat> list = prodCatService.findProdCatsWithProds(prodCat);
        return list;
    }
}

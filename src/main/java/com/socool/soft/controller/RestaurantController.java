package com.socool.soft.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.socool.soft.service.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.socool.soft.bo.RcBuyer;
import com.socool.soft.bo.RcCoupon;
import com.socool.soft.bo.RcMerchant;
import com.socool.soft.bo.RcMerchantTable;
import com.socool.soft.bo.RcOrder;
import com.socool.soft.bo.RcOrderEvaluation;
import com.socool.soft.bo.RcOrderFee;
import com.socool.soft.bo.RcOrderOperation;
import com.socool.soft.bo.RcOrderProd;
import com.socool.soft.bo.RcOrderProdSkuPropInfo;
import com.socool.soft.bo.RcProd;
import com.socool.soft.bo.RcProdImg;
import com.socool.soft.bo.RcProdSku;
import com.socool.soft.bo.RcProdSkuPropInfo;
import com.socool.soft.bo.constant.CouponDiscountTypeEnum;
import com.socool.soft.bo.constant.CouponStatusEnum;
import com.socool.soft.bo.constant.OrderBuyerStatusEnum;
import com.socool.soft.bo.constant.OrderEatTypeEnum;
import com.socool.soft.bo.constant.OrderFeeTypeEnum;
import com.socool.soft.bo.constant.OrderOperationTypeEnum;
import com.socool.soft.bo.constant.OrderSellerStatusEnum;
import com.socool.soft.bo.constant.ProdPriceMannerEnum;
import com.socool.soft.bo.constant.ProdStatusEnum;
import com.socool.soft.bo.constant.YesOrNoEnum;
import com.socool.soft.constant.Constants;
import com.socool.soft.constant.PropertyConstants;
import com.socool.soft.exception.ErrorValue;
import com.socool.soft.exception.SystemException;
import com.socool.soft.vo.Page;
import com.socool.soft.vo.Result;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping(value="page")
public class RestaurantController extends BaseController {


    @Autowired
    private IOrderService orderService;
    @Autowired
    private IOrderFeeService orderFeeService;
	@Autowired
	private IMerchantService merchantService;
	@Autowired
	private IMerchantSectionService merchantSectionService;
    @Autowired
    private IBuyerService buyerService;
    @Autowired
    private IOrderProdService orderProdService;
    @Autowired
    private IOrderOperationService orderOperationService;
    @Autowired
    private IRedisService redisService;
    @Autowired
    private IProdService prodService;
    @Autowired
    private IProdSkuService prodSkuService;
    @Autowired
    private PropertyConstants propertyConstants;
    @Autowired
    private IProdSkuPropInfoService prodSkuPropInfoService;
    @Autowired
    private IOrderProdSkuPropInfoService orderProdSkuPropInfoService;
    @Autowired
    private IProdImgService prodImgService;
    @Autowired
    private ICouponService couponService;
    @Autowired
    private IOrderEvaluationService orderEvaluationService;
    @Autowired
    private IPushService pushService;


   /* @RequestMapping(value = "menu")
    public String menu() {
        return "/restaurant/menu";
    }
*/
    @RequestMapping(value = "submitOrder")
    public String submitOrder(HttpServletRequest request, String json, int merchantId, Integer tableId, String sectionName, Integer tableNumber, Model model) throws SystemException {
    	Cookie[] cookies = request.getCookies();
		if(cookies != null) {
			for(Cookie cookie : cookies){
			    if(cookie.getName().equals("orderId")) {
			    	long orderId = Long.parseLong(cookie.getValue());
			    	RcOrder order = orderService.findOrderById(orderId);
			    	if(order.getBuyerStatus() == OrderBuyerStatusEnum.UNCONFIRMED.getValue() || order.getBuyerStatus() == OrderBuyerStatusEnum.CONFIRMED.getValue()) {
			    		return "redirect:order?orderId=" + orderId;
			    	}
			    }
			}
		}
		
		RcMerchant merchant = merchantService.findMerchantById(merchantId);
        JSONArray jsonArray = JSONArray.fromObject(json);
        List<RcProd> prods = new ArrayList<>();
        RcOrder order = new RcOrder();
        order.setProductPrice(new BigDecimal(0));
        order.setTaxRate(merchant.getTaxRate());
        order.setServiceFeeRate(merchant.getServiceCharge());

		RcCoupon couponParam = new RcCoupon();
		couponParam.setReceiveEndTimeFrom(new Date());
		couponParam.setNeedGet(YesOrNoEnum.NO.getValue());
		couponParam.setStatus(CouponStatusEnum.PUBLISHED.getValue());
		couponParam.setDelFlag(YesOrNoEnum.NO.getValue());
		couponParam.setMerchantId(merchantId);
		List<RcCoupon> coupons = couponService.findCoupons(couponParam);
        for (Object object : jsonArray) {
            JSONObject jsonObject = JSONObject.fromObject(object);
            int prodId = jsonObject.getInt("prodId");
            String prodSkuId = jsonObject.getString("prodSkuId");
            int quantity = jsonObject.getInt("quantity");

            RcProd prod = prodService.findProdById(prodId);
            if(prod.getStatus() == ProdStatusEnum.SOLD_OUT.getValue()) {
				if (prod.getDelFlag() == YesOrNoEnum.YES.getValue()) {
					return "redirect:/page/menu?merchantId=" + merchantId + (tableId == null ? "" : ("&tableId=" + tableId + "&sectionName=" + sectionName + "&tableNumber=" + tableNumber + "&result=1"));
				} else {
					return "redirect:/page/menu?merchantId=" + merchantId + (tableId == null ? "" : ("&tableId=" + tableId + "&sectionName=" + sectionName + "&tableNumber=" + tableNumber + "&result=1"));
				}
			}
            if(prod.getStatus() == ProdStatusEnum.NOT_SELLING.getValue()) {
				if (prod.getDelFlag() == YesOrNoEnum.YES.getValue()) {
					return "redirect:/page/menu?merchantId=" + merchantId + (tableId == null ? "" : ("&tableId=" + tableId + "&sectionName=" + sectionName + "&tableNumber=" + tableNumber + "&result=3"));
				} else {
					return "redirect:/page/menu?merchantId=" + merchantId + (tableId == null ? "" : ("&tableId=" + tableId + "&sectionName=" + sectionName + "&tableNumber=" + tableNumber + "&result=3"));
				}
			}
            prod.setProdSkuId(prodSkuId);
            order.setMerchantId(prod.getMerchantId());
            if (StringUtils.isBlank(prodSkuId)) {
                RcProdImg  prodImg = prodImgService.findFirstProdImgByProdSkuId(String.valueOf(prodId));
                prod.setProdImgUrl(prodImg.getImgUrl());
			} else {
				RcProdSku prodSku = prodSkuService.findProdSkuByProdSkuId(prodSkuId);

                    RcProdImg prodImg = prodImgService.findFirstProdImgByProdSkuId(prodSkuId);
                    if(prodImg!=null)
                    prod.setProdImgUrl(prodImg.getImgUrl());
                    else
                        prod.setProdImgUrl(prodImgService.findFirstProdImgByProdSkuId(String.valueOf(prodId)).getImgUrl());
                    //获取sku信息
                    String[] prodPropEnumIds = prodSkuId.split("_");
                    prod.setProdSkuPropInfos(new ArrayList<RcProdSkuPropInfo>());
                    for(int i = 1; i < prodPropEnumIds.length; i++) {
                        RcProdSkuPropInfo prodSkuPropInfo = prodSkuPropInfoService.findProdSkuPropInfoByProdIdAndProdSkuPropEnumId(prodId, Integer.parseInt(prodPropEnumIds[i]));
                        prod.getProdSkuPropInfos().add(prodSkuPropInfo);
                    }

				prod.setOriginPrice(prodSku.getOriginPrice());
				prod.setPrice(prodSku.getPrice());
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
            	prod.setOriginPrice(prod.getPrice());
				if(coupon.getDiscountType() == CouponDiscountTypeEnum.PERCENTAGE.getValue()) {
					prod.setPrice(prod.getPrice().multiply(coupon.getAmount()));
				} else if(coupon.getDiscountType() == CouponDiscountTypeEnum.SUBSTRACT.getValue()) {
					prod.setPrice(prod.getPrice().subtract(coupon.getAmount()));
				} else if(coupon.getDiscountType() == CouponDiscountTypeEnum.FIXED.getValue()) {
					prod.setPrice(coupon.getAmount());
				}
            } else if(!CollectionUtils.isEmpty(prodCatCoupons)) {
            	RcCoupon coupon = prodCatCoupons.get(0);
            	prod.setOriginPrice(prod.getPrice());
				if(coupon.getDiscountType() == CouponDiscountTypeEnum.PERCENTAGE.getValue()) {
					prod.setPrice(prod.getPrice().multiply(coupon.getAmount()));
				} else if(coupon.getDiscountType() == CouponDiscountTypeEnum.SUBSTRACT.getValue()) {
					prod.setPrice(prod.getPrice().subtract(coupon.getAmount()));
				} else if(coupon.getDiscountType() == CouponDiscountTypeEnum.FIXED.getValue()) {
					prod.setPrice(coupon.getAmount());
				}
            }
            
            BigDecimal productPrice = new BigDecimal(0);

            if(prod.getPriceManner() == ProdPriceMannerEnum.BY_WEIGHT.getValue()) {
//            	prod.setQuantity(quantity * prod.getMeasureUnitCount());
//            	productPrice = productPrice.add(prod.getPrice().multiply(new BigDecimal(prod.getQuantity()).divide(new BigDecimal(prod.getMeasureUnitCount()), 2, RoundingMode.HALF_UP)));
            } else {
		        prod.setQuantity(quantity);
				productPrice = productPrice.add(prod.getPrice().multiply(new BigDecimal(prod.getQuantity())));
			}
            
            order.setProductPrice(order.getProductPrice().add(productPrice));
            prods.add(prod);
        }
        order.setServiceFee(order.getProductPrice().multiply(new BigDecimal(String.valueOf(merchant.getServiceCharge()))).setScale(0, RoundingMode.HALF_UP));//服务费
        order.setTax(order.getProductPrice().add(order.getServiceFee()).multiply(new BigDecimal(String.valueOf(merchant.getTaxRate()))).setScale(0, RoundingMode.HALF_UP));//税金
        order.setPayPrice(order.getProductPrice().add(order.getTax()).add(order.getServiceFee()));//实际支付金额
        order.setTableId(tableId);
        order.setTableNumber(tableNumber);
        order.setSectionName(sectionName);
        String orderToken = UUID.randomUUID().toString();
        redisService.sadd(Constants.REDIS_ORDER_TOKEN, orderToken);
        boolean weightFlag = false;
        for(RcProd prod:prods){
        	if(prod.getPriceManner()==2){
        		weightFlag = true;
        		break;
        	}
        }
        model.addAttribute("rcOrder", order);
        model.addAttribute("rcProds", prods);
        model.addAttribute("orderToken", orderToken);
        model.addAttribute("weightFlag",weightFlag);
        return "/restaurant/submitOrder";
    }

    @RequestMapping(value = "order", method = RequestMethod.POST)
    @ResponseBody
    public Result<RcOrder> order( RcOrder order, String verificationCode, String orderToken, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException, SystemException {
        Result<RcOrder> result = new Result<RcOrder>();
        if (StringUtils.isBlank(order.getMember().getMobile())) {
            result.setCode("0");
            result.setResult("Please enter the correct phone number.");
            return result;

        }
		if (order.getMember().getMobile().length()<8) {
			result.setCode("0");
			result.setResult("Please fill in 8-15 digits of numbers as your phone number");
			return result;

		}
		if (! StringUtils.isNumeric(order.getMember().getMobile())) {
			result.setCode("0");
			result.setResult("mobile format  is error.");
			return result;

		}
        if (StringUtils.isBlank(order.getMember().getName())) {
            result.setCode("0");
            result.setResult("Please enter the correct your name.");
            return result;

        }
		if (order.getMember().getName().length()<3) {
			result.setCode("0");
			result.setResult("Please fill in 3-18 digits of letters or numbers as your name");
			return result;

		}


        if(!redisService.sismember(Constants.REDIS_ORDER_TOKEN, orderToken)) {
        	result.setCode("0");
            result.setResult("expired.");
            return result;
        }
//        if (StringUtils.isEmpty(verificationCode)) {
//            result.setCode("0");
//            result.setResult("verification code is request.");
//            return result;
//
//        }
        //验证码校验
//        Code code = (Code) redisService.hgetObject("code", rcOrder.getMobile());
//        redisService.hdelObject("code", rcOrder.getMobile());
      /*  if (code==null||!code.getCode().equals(verificationCode)) {
            result.setCode("0");
            result.setResult("verification code is error.");
            return result;
        }*/


        RcBuyer buyer = buyerService.findBuyerByMobile(order.getMember().getMobile());
        if (buyer == null) {
            buyer = new RcBuyer();
            buyer.setMobile(order.getMember().getMobile());
            buyer.setName(order.getMember().getName());
            buyer.setPassword(DigestUtils.md5Hex("111111"));
            buyerService.addBuyer(buyer);
        }
        if(order.getTableId() == null) {
        	order.setEatType(OrderEatTypeEnum.TAKE_OUT.getValue());
        }
        RcMerchant merchant = merchantService.findMerchantById(order.getMerchantId());
		order.setMerchant(merchant);
		order.setCityId(merchant.getCityId());
		order.setVendorId(merchant.getVendorId());
        order.setOrderId(generateOrderId());
        order.setMemberId(buyer.getMemberId());
        order.setProductPrice(new BigDecimal(0));
        order.setBuyerStatus(OrderBuyerStatusEnum.UNCONFIRMED.getValue());
        order.setSellerStatus(OrderSellerStatusEnum.UNCONFIRMED.getValue());
        if(order.getEatType() == OrderEatTypeEnum.TAKE_OUT.getValue()) {
        	order.setSectionId(0);
        	order.setTableId(0);
        	order.setSectionName(null);
        	order.setTableNumber(null);
        } else {
        	if(order.getTableId() != null) {
	        	RcMerchantTable table = merchantSectionService.findMerchantTableById(order.getTableId());
	        	if(table != null) {
	        		order.setSectionId(table.getSectionId());
					order.setTableNumber(table.getTableNumber());
	        	} else {
	            	order.setSectionId(0);
	        	}
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
            order.setMerchantId(prod.getMerchantId());
            order.setVendorId(prod.getVendorId());
            orderProd.setPriceManner(prod.getPriceManner());
            orderProd.setMeasureUnit(prod.getMeasureUnit());
            orderProd.setMeasureUnitCount(prod.getMeasureUnitCount());
            if(prod.getStatus() == ProdStatusEnum.SOLD_OUT.getValue()) {
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
            if(prod.getStatus() == ProdStatusEnum.NOT_SELLING.getValue()) {
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
            if (StringUtils.isBlank(orderProd.getProdSkuId())) {
				orderProd.setProdPrice(prod.getPrice());
				orderProd.setProdOriginPrice(prod.getOriginPrice());
			} else {
				RcProdSku prodSku = prodSkuService.findProdSkuByProdSkuId(orderProd.getProdSkuId());
				orderProd.setProdOriginPrice(prodSku.getOriginPrice());
				orderProd.setProdPrice(prodSku.getPrice());
				List<RcOrderProdSkuPropInfo> orderProdSkuPropInfos = new ArrayList<RcOrderProdSkuPropInfo>();
				String[] prodPropEnumIds = orderProd.getProdSkuId().split("_");
				for(int i = 1; i < prodPropEnumIds.length; i++) {
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
            	orderProd.setProdOriginPrice(orderProd.getProdPrice());
				if(coupon.getDiscountType() == CouponDiscountTypeEnum.PERCENTAGE.getValue()) {
					orderProd.setProdPrice(orderProd.getProdPrice().multiply(coupon.getAmount()));
				} else if(coupon.getDiscountType() == CouponDiscountTypeEnum.SUBSTRACT.getValue()) {
					orderProd.setProdPrice(orderProd.getProdPrice().subtract(coupon.getAmount()));
				} else if(coupon.getDiscountType() == CouponDiscountTypeEnum.FIXED.getValue()) {
					orderProd.setProdPrice(coupon.getAmount());
				}
            } else if(!CollectionUtils.isEmpty(prodCatCoupons)) {
            	RcCoupon coupon = prodCatCoupons.get(0);
            	orderProd.setProdOriginPrice(orderProd.getProdPrice());
				if(coupon.getDiscountType() == CouponDiscountTypeEnum.PERCENTAGE.getValue()) {
					orderProd.setProdPrice(orderProd.getProdPrice().multiply(coupon.getAmount()));
				} else if(coupon.getDiscountType() == CouponDiscountTypeEnum.SUBSTRACT.getValue()) {
					orderProd.setProdPrice(orderProd.getProdPrice().subtract(coupon.getAmount()));
				} else if(coupon.getDiscountType() == CouponDiscountTypeEnum.FIXED.getValue()) {
					orderProd.setProdPrice(coupon.getAmount());
				}
            }
			if(prod.getPriceManner() == ProdPriceMannerEnum.BY_WEIGHT.getValue()) {
				orderProd.setQuantity(null);
//				orderProd.setAmount(orderProd.getProdPrice().multiply(new BigDecimal(String.valueOf(orderProd.getQuantity())).divide(new BigDecimal(prod.getMeasureUnitCount()), 2, RoundingMode.HALF_UP)));
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
		orderOperation.setMemberId(buyer.getMemberId());
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
//				RcProd prod = prodService.findProdById(orderProd.getProdId());
//				prod.setSoldNum(prod.getSoldNum() + (prod.getPriceManner() == ProdPriceMannerEnum.BY_WEIGHT.getValue() ? 1 : orderProd.getQuantity()));
//				prodService.modifyProd(prod);
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
        redisService.srem(Constants.REDIS_ORDER_TOKEN, orderToken);

        try {
            // 推送新订单
            pushService.newOrder(order);
        } catch (SystemException e) {
            e.printStackTrace();
        }
        return result;
    }


    @RequestMapping(value = "order", method = RequestMethod.GET)
    public String order(Long orderId,Model model){
        RcOrder order=orderService.findBuyerOrderByOrderId(orderId);
 		order.setRounding(new BigDecimal(0));
 		order.setTax(new BigDecimal(0));
 		order.setServiceFee(new BigDecimal(0));
        List<RcOrderFee> orderFees = orderFeeService.findOrderFeesByOrderId(orderId);
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
 		RcMerchant merchant = merchantService.findMerchantById(order.getMerchantId());
        order.setTaxRate(merchant.getTaxRate());
        order.setServiceFeeRate(merchant.getServiceCharge());
//        order.setTax(order.getProductPrice().multiply(new BigDecimal(String.valueOf(order.getTaxRate()))).setScale(0, RoundingMode.HALF_UP));//税金
        RcOrderEvaluation orderEvaluation = orderEvaluationService.findOrderEvaluationById(order.getOrderId());
        model.addAttribute("eval", orderEvaluation);
        model.addAttribute("rcOrder",order);
        return "/restaurant/order";
    }
    
    @RequestMapping(value="history")
    public String history(Integer memberId,Model model,Integer currentPage){
    	boolean flag = false;
		if (currentPage == null) {
			currentPage = 1;
			flag = true;
		}
		// 分页
		Page page = new Page();
		// 初始化时第一页
		page.setPagination(true);
		page.setPageSize(10);
		page.setCurrentPage(currentPage);
		model.addAttribute("page", page);
    	List<RcOrder> list = orderService.findPagedOrdersByBuyerId(memberId, page);
    	model.addAttribute("list", list);
    	model.addAttribute("memberId", memberId);
    	if(flag){
    		return "restaurant/history";
    	}
    	else{
    		return "restaurant/history_inner";
    	}
    }
    
    /**
     * 发送短信验证码
     * @return
     * @throws IOException
     * @throws SystemException
     */
/*
    @RequestMapping(value = "smsreguler", method = RequestMethod.POST)
    @ResponseBody
    public Result<String> smsReguler(String mobile, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, SystemException {
        Result<String> result = new Result<String>();
        SentSmsRequst sentSmsRequst = new SentSmsRequst();
        List<SendingData> sendingDatas = new ArrayList<SendingData>();
        SendingData sendingData = new SendingData();
        sendingDatas.add(sendingData);
        sentSmsRequst.setSending_data(sendingDatas);
        sendingData.setApikey("4dd671d14ac6581665467af44caf72c7");
        sendingData.setCallbackurl(httpServletRequest.getServerName() + httpServletRequest.getContextPath() + "/SMS/callbackurl");
        List<Datapacket> datapackets = new ArrayList<Datapacket>();
        sendingData.setDatapacket(datapackets);
        Datapacket datapacket = new Datapacket();
        Packet packet = new Packet();
        // 随机生成4位验证码
        String randomAlphanumeric = RandomStringUtils.randomNumeric(6);
        packet.setMessage(randomAlphanumeric);
        packet.setNumber(mobile);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        packet.setSendingdatetime(simpleDateFormat.format(new Date()));
        datapacket.setPacket(packet);
        datapackets.add(datapacket);
        result = ismsService.smsreguler(httpServletRequest, sentSmsRequst);
        if (result.getCode().equals("1")) {
            Code code = new Code();
            code.setCode(packet.getMessage());
            result.setResult(packet.getMessage());
            code.setCreateDate(new Date());
            redisService.hsetObject(Constants.REDIS_MOBILE_VERIFICATION_CODE, mobile, code);
        }
        return result;
    }
*/
    @RequestMapping(value = "orderdetail")
    public String orderDetail(Long orderId,Model model){
        RcOrder order=orderService.findBuyerOrderByOrderId(orderId);
 		order.setRounding(new BigDecimal(0));
 		order.setTax(new BigDecimal(0));
 		order.setServiceFee(new BigDecimal(0));
        List<RcOrderFee> orderFees = orderFeeService.findOrderFeesByOrderId(orderId);
 		for(RcOrderFee orderFee : orderFees) {
 			if(orderFee.getType() == OrderFeeTypeEnum.MODIFICATION.getValue()) {
 				order.setRounding(orderFee.getAmount());
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
 		RcMerchant merchant = merchantService.findMerchantById(order.getMerchantId());
        order.setTaxRate(merchant.getTaxRate());
        order.setServiceFeeRate(merchant.getServiceCharge());
//        order.setTax(order.getProductPrice().multiply(new BigDecimal(String.valueOf(order.getTaxRate()))).setScale(0, RoundingMode.HALF_UP));//税金
        RcOrderEvaluation orderEvaluation = orderEvaluationService.findOrderEvaluationById(order.getOrderId());
        model.addAttribute("eval", orderEvaluation);
        model.addAttribute("rcOrder",order);
        return "/restaurant/orderDetail";
    }
    
    @RequestMapping(value="/review")
    public String review(Long orderId,Model model,Integer memberId){
    	RcOrder order  = orderService.findOrderById(orderId);
    	RcMerchant merchant = merchantService.findMerchantById(order.getMerchantId());
    	model.addAttribute("order", order);
    	model.addAttribute("merchant", merchant);
    	model.addAttribute("memberId", memberId);
    	return "/restaurant/review";
    }
    
    @RequestMapping(value="/reviewdetail")
    public String reviewDetail(Long orderId,Model model){
    	RcOrder order  = orderService.findOrderById(orderId);
    	RcMerchant merchant = merchantService.findMerchantById(order.getMerchantId());
    	RcOrderEvaluation orderEvaluation = orderEvaluationService.findOrderEvaluationById(orderId);
    	model.addAttribute("merchant", merchant);
    	model.addAttribute("review", orderEvaluation);
    	model.addAttribute("orderId", orderId);
    	return "/restaurant/reviewDetail";
    }
    
    @RequestMapping(value="toreview")
    @ResponseBody
    public String toReview(RcOrderEvaluation orderEvaluation){
    	orderEvaluation.setDelFlag(YesOrNoEnum.NO.getValue());
    	long orderId = orderEvaluationService.addOrderEvaluation(orderEvaluation);
    	if(orderId>0){
    		return "{\"code\":\"1\"}";
    	}
    	return "{\"code\":\"0\"}";
    }

    /**
     *
     * @return
     * @throws SystemException
     */
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
}

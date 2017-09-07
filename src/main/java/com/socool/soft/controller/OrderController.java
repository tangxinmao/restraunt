package com.socool.soft.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.GeneralSecurityException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.socool.soft.service.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.socool.soft.bo.RcBuyer;
import com.socool.soft.bo.RcCoupon;
import com.socool.soft.bo.RcMerchant;
import com.socool.soft.bo.RcMerchantSection;
import com.socool.soft.bo.RcMerchantTable;
import com.socool.soft.bo.RcMerchantUser;
import com.socool.soft.bo.RcOrder;
import com.socool.soft.bo.RcOrderCoupon;
import com.socool.soft.bo.RcOrderFee;
import com.socool.soft.bo.RcOrderOperation;
import com.socool.soft.bo.RcOrderProd;
import com.socool.soft.bo.RcOrderProdSkuPropInfo;
import com.socool.soft.bo.RcProd;
import com.socool.soft.bo.RcProdImg;
import com.socool.soft.bo.RcProdSku;
import com.socool.soft.bo.RcProdSkuPropInfo;
import com.socool.soft.bo.RcVendorUser;
import com.socool.soft.bo.constant.BuyerCouponStatusEnum;
import com.socool.soft.bo.constant.CouponDiscountTypeEnum;
import com.socool.soft.bo.constant.CouponStatusEnum;
import com.socool.soft.bo.constant.OrderBuyerStatusEnum;
import com.socool.soft.bo.constant.OrderFeeTypeEnum;
import com.socool.soft.bo.constant.OrderOperationTypeEnum;
import com.socool.soft.bo.constant.OrderProdStatusEnum;
import com.socool.soft.bo.constant.OrderSellerStatusEnum;
import com.socool.soft.bo.constant.ProdPriceMannerEnum;
import com.socool.soft.bo.constant.ProdStatusEnum;
import com.socool.soft.bo.constant.YesOrNoEnum;
import com.socool.soft.constant.Constants;
import com.socool.soft.exception.ErrorValue;
import com.socool.soft.util.ExportUtil;
import com.socool.soft.util.VOConversionUtil;
import com.socool.soft.vo.Page;
import com.socool.soft.vo.RcOrderVo;
import com.socool.soft.vo.Result;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@RequestMapping(value = {"order"})
@Controller
public class OrderController extends BaseController {
    //	public static Logger logger = LogManager.getLogger(OrderController.class.getName());
    @Autowired
    private IOrderService orderService;
	@Autowired
	private IOrderFeeService orderFeeService;

    @Autowired
    private IProdSkuService prodSkuService;
//    @Autowired
//    private IOrderEvaluationService orderEvaluationService;
    @Autowired
    private IBuyerCouponService buyerCouponService;
    @Autowired
    private IVendorService vendorService;
    @Autowired
    private IVendorUserService vendorUserService;
    @Autowired
    private ICityService cityService;


    @Autowired
    private IProdImgService prodImgService;

//    @Autowired
//    private IBuyerConsigneeService buyerConsigneeService;
    @Autowired
    private IMerchantService merchantService;
    @Autowired
    private IMerchantUserService merchantUserService;
    @Autowired
    private IMerchantSectionService merchantSectionService;
    @Autowired
    private IOrderCouponService orderCouponService;
    @Autowired
    private ICouponService couponService;
    @Autowired
    private IBuyerService buyerService;
    @Autowired
    private IOrderProdService orderProdService;
//    @Autowired
//    private IOrderProdEvaluationService orderProdEvaluationService;
    @Autowired
    private IProdSkuPropInfoService prodSkuPropInfoService;
    @Autowired
    private IOrderOperationService orderOperationService;
    @Autowired
    private IOrderConsigneeService orderConsigneeService;
    @Autowired
    private IProdService prodService;
    /*	@Autowired
        private IRechargeService iRechargeService;*/
    @Autowired
    private IOrderDeliveryService orderDeliveryService;
    @Autowired
    private IOrderProdSkuPropInfoService orderProdSkuPropInfoService;
    @Autowired
    private IMerchantTableService merchantTableService;

//    @Autowired
//    private PropertyConstants propertyConstants;

//	@RequestMapping("updateOrderPayPrice")
//	@ResponseBody
//	public Result<String> updateOrderPayPrice(RcOrder param, int userId) {
//		Result<String> result = new Result<String>();
//		if (param.getPayPrice() == null) {
//			result.setResult("not null.");
//			result.setCode("-1");
//			return result;
//		}
//		if (param.getPayPrice().compareTo(new BigDecimal(0)) < 0) {
//			result.setResult("not negative.");
//			result.setCode("-1");
//			return result;
//		}
//		RcOrder order = orderService.findOrderById(param.getOrderId());
//		BigDecimal sub = param.getPayPrice().subtract(order.getPayPrice());
//		RcOrderFee orderFee = orderFeeService.findOrderFeeByType(param.getOrderId(), OrderFeeTypeEnum.MODIFICATION.getValue());
//		if(orderFee == null) {
//			orderFee = new RcOrderFee();
//			orderFee.setOrderId(param.getOrderId());
//			orderFee.setType(OrderFeeTypeEnum.MODIFICATION.getValue());
//			orderFee.setAmount(sub);
//			orderFee.setAddToTotal(YesOrNoEnum.YES.getValue());
//			if(orderFee.getAmount().compareTo(new BigDecimal(0)) > 0) {
//				orderFeeService.addOrderFee(orderFee);
//			}
//		} else {
//			orderFee.setAmount(orderFee.getAmount().add(sub));
//			if(orderFee.getAmount().compareTo(new BigDecimal(0)) > 0) {
//				orderFeeService.modifyOrderFee(orderFee);
//			}
//		}
//		order.setPayPrice(param.getPayPrice());
//		order.setSellerMemo(param.getSellerMemo());
//		orderService.modifyOrder(order);
//		
//		RcOrderOperation orderOperation = new RcOrderOperation();
//		orderOperation.setOrderId(param.getOrderId());
//		orderOperation.setOperationType(OrderOperationTypeEnum.AMOUNT_MODIFY.getValue());
//		orderOperation.setMemberId(userId);
//		orderOperation.setOperationTime(new Date());
//		orderOperation.setOperationMemo(param.getSellerMemo());
//		orderOperationService.addOrderOperation(orderOperation);
//		return result;
//	}

    /**
     * 查询订单列表
     *
     * @param request
     * @param model
     * @param currentPage
     * @param rcOrder
     * @param isSuccess
     * @param upDown
     * @return
     */
    /*
     * @RequestMapping("order_list") public String memberList(HttpServletRequest
	 * request, Model model, String currentPage, RcOrderVo rcOrder,
	 * 
	 * @RequestParam(value = "is_success", required = false) Boolean isSuccess,
	 * 
	 * @RequestParam(value = "up_down", required = false) String upDown) {
	 * boolean flag = false; if (currentPage == null || currentPage.equals("")
	 * || currentPage.equals("undefined")) { currentPage = "1"; flag = true; }
	 * // 分页 Page page = new Page(); // 初始化时第一页 page.setPagination(true);
	 * page.setPageSize(10); page.setCurrentPage(Integer.parseInt(currentPage));
	 * PageContext.setPage(page); model.addAttribute("page", page);
	 * List<RcOrderVo> rcOrders = iOrderService.findOrderVOs(rcOrder);
	 * model.addAttribute("list", rcOrders); model.addAttribute("order",
	 * rcOrder); model.addAttribute("is_success", isSuccess);
	 * model.addAttribute("up_down", upDown); if (flag) return "orderList"; else
	 * return "orderList_inner"; }
	 */

    /**
     * 订单详情
     *
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("orderDetail")
    public String orderDetail(long orderId, Model model) throws UnsupportedEncodingException {

        RcOrder order = orderService.findOrderById(orderId);
        order.setMerchant(merchantService.findMerchantById(order.getMerchantId()));
        order.getMerchant().setVendor(vendorService.findVendorById(order.getMerchant().getVendorId()));
        order.setMember(buyerService.findBuyerById(order.getMemberId()));
//		order.setOrderConsignee(orderConsigneeService.findOrderConsigneeById(orderId));
        order.setCityName(cityService.findCityById(order.getCityId()).getName());
//		order.getOrderConsignee().setCity(cityService.findCityById(order.getOrderConsignee().getCityId()));
        order.setOrderProds(orderProdService.findOrderProdsByOrderId(orderId));
//		order.setOrderDelivery(orderDeliveryService.findOrderDeliveryById(orderId));
//        List<RcOrderOperation> orderOperations = orderOperationService.findOrderOperationsByOrderId(orderId);
//        for (RcOrderOperation orderOperation : orderOperations) {
//            if (orderOperation.getOperationType() == OrderOperationTypeEnum.PAY.getValue()) {
//                order.setPayTime(orderOperation.getOperationTime());
//            }
//            if (orderOperation.getOperationType() == OrderOperationTypeEnum.RECEIVE.getValue()) {
//                order.setReceiveTime(orderOperation.getOperationTime());
//            }
//            if (orderOperation.getOperationType() == OrderOperationTypeEnum.DELIVER.getValue()) {
//                order.setDeliveryTime(orderOperation.getOperationTime());
//            }
//        }
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
        for (RcOrderProd orderProd : order.getOrderProds()) {
//        	if(orderProd.getPriceManner() == ProdPriceMannerEnum.BY_QUANTITY.getValue()) {
//        		orderProd.setShowQuantity(new BigDecimal(String.valueOf(orderProd.getQuantity())));
//        	} else {
//        		if(orderProd.getQuantity() != null) {
//	        		BigDecimal showQuantity = new BigDecimal(String.valueOf(orderProd.getQuantity())).divide(new BigDecimal(orderProd.getMeasureUnitCount()), 2, RoundingMode.HALF_UP);
//	        		orderProd.setShowQuantity(showQuantity);
//        		}
//        	}
        	orderProd.setProd(prodService.findProdById(orderProd.getProdId()));
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
        model.addAttribute("order", order);
        RcMerchantTable rcMerchantTable = merchantTableService.findOneByTableId(order.getTableId());
        List<RcMerchantTable> rcMerchantTableList = merchantTableService.findBySectionId(rcMerchantTable.getSectionId());
        List<RcMerchantSection> rcMerchantSectionList = merchantSectionService.findMerchantSectionsByMerchantId(order.getMerchantId());
        model.addAttribute("tables", rcMerchantTableList);
        model.addAttribute("sections", rcMerchantSectionList);
        return "orderDetail";
    }
    @RequestMapping("tablesBySectionId")
    @ResponseBody
    public List<RcMerchantTable> tablesBySectionId(int sectionId) {
        List<RcMerchantTable> rcMerchantTableList = merchantTableService.findBySectionId(sectionId);
        return  rcMerchantTableList;
    }
    /**
     * 查询订单checkout信息
     *
     * @param orderId
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("orderCheckOut")
    @ResponseBody
    public RcOrder orderCheckOut(long orderId) throws UnsupportedEncodingException {

        RcOrder order = orderService.findOrderById(orderId);
        order.setMerchant(merchantService.findMerchantById(order.getMerchantId()));
        order.getMerchant().setVendor(vendorService.findVendorById(order.getMerchant().getVendorId()));
        order.setMember(buyerService.findBuyerById(order.getMemberId()));
        order.setOrderConsignee(orderConsigneeService.findOrderConsigneeById(orderId));
        order.setCityName(cityService.findCityById(order.getCityId()).getName());
        order.getOrderConsignee().setCity(cityService.findCityById(order.getOrderConsignee().getCityId()));
        order.setOrderProds(orderProdService.findOrderProdsByOrderId(orderId));
        order.setOrderDelivery(orderDeliveryService.findOrderDeliveryById(orderId));
//        List<RcOrderOperation> orderOperations = orderOperationService.findOrderOperationsByOrderId(orderId);
//        for (RcOrderOperation orderOperation : orderOperations) {
//            if (orderOperation.getOperationType() == OrderOperationTypeEnum.PAY.getValue()) {
//                order.setPayTime(orderOperation.getOperationTime());
//            }
//            if (orderOperation.getOperationType() == OrderOperationTypeEnum.RECEIVE.getValue()) {
//                order.setReceiveTime(orderOperation.getOperationTime());
//            }
//            if (orderOperation.getOperationType() == OrderOperationTypeEnum.DELIVER.getValue()) {
//                order.setDeliveryTime(orderOperation.getOperationTime());
//            }
//        }
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
			}
			if(orderFee.getType() == OrderFeeTypeEnum.SERVICE_FEE.getValue()) {
				order.setServiceFee(orderFee.getAmount());
			}
		}
        for (RcOrderProd orderProd : order.getOrderProds()) {
            orderProd.setProd(prodService.findProdById(orderProd.getProdId()));
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

        return order;
    }

    /**
     * 结算
     *
     * @param orderId
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("checkOut")
    @ResponseBody
    public Result<List<RcOrderVo>> checkOut(HttpServletRequest request, long orderId) throws UnsupportedEncodingException {
        int merchantUserId = getMerchantUserId(request);
        Result<List<RcOrderVo>> result = new Result<List<RcOrderVo>>();
        RcOrder order = orderService.findOrderById(orderId);
        order.setBuyerStatus(OrderBuyerStatusEnum.FINISHED.getValue());
        order.setSellerStatus(OrderSellerStatusEnum.FINISHED.getValue());
        order.setOrderProds(orderProdService.findOrderProdsByOrderId(orderId));
        for (RcOrderProd orderProd : order.getOrderProds()) {
        	RcProd prod = prodService.findProdById(orderProd.getProdId());
        	prod.setSoldNum(prod.getSoldNum().add(new BigDecimal(String.valueOf(prod.getPriceManner() == ProdPriceMannerEnum.BY_WEIGHT.getValue() ? 1 : orderProd.getQuantity()))));
			prodService.modifyProd(prod);
        }
        
        BigDecimal roudingScale = new BigDecimal(500);
        BigDecimal rounding = order.getPayPrice().divide(roudingScale, 0, RoundingMode.HALF_UP).multiply(roudingScale).subtract(order.getPayPrice());
        RcOrderFee orderFee = new RcOrderFee();
		orderFee.setOrderId(orderId);
		orderFee.setType(OrderFeeTypeEnum.MODIFICATION.getValue());
		orderFee.setAmount(rounding);
		orderFee.setAddToTotal(YesOrNoEnum.YES.getValue());
		orderFeeService.addOrderFee(orderFee);
        
//        if(discount != null && discount > 0) {
//        	RcOrderFee orderFee = new RcOrderFee();
//			orderFee.setOrderId(orderId);
//			orderFee.setType(OrderFeeTypeEnum.MODIFICATION.getValue());
//			orderFee.setAmount(new BigDecimal(-discount));
//			orderFee.setAddToTotal(YesOrNoEnum.YES.getValue());
//			orderFeeService.addOrderFee(orderFee);
//			
//			RcOrderOperation orderOperation = new RcOrderOperation();
//	        orderOperation.setOrderId(order.getOrderId());
//	        orderOperation.setOperationType(OrderOperationTypeEnum.AMOUNT_MODIFY.getValue());
//	        orderOperation.setMemberId(merchantUserId);
//	        orderOperationService.addOrderOperation(orderOperation);
//        }
        // 订单操作流畅
        RcOrderOperation orderOperation = new RcOrderOperation();
        orderOperation.setOrderId(order.getOrderId());
        orderOperation.setOperationType(OrderOperationTypeEnum.CHECK_OUT.getValue());
        orderOperation.setMemberId(merchantUserId);
        orderOperationService.addOrderOperation(orderOperation);
//        order.setCouponAmount(order.getPayPrice().subtract(order.getPayPrice().divide(new BigDecimal(100), 0, RoundingMode.DOWN).multiply(new BigDecimal(100))));
        order.setPayPrice(order.getPayPrice().add(rounding));
        orderService.modifyOrder(order);
        return result;

    }

    /**
     * 通知厨房
     *
     * @param orderId
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("confirm")
    @ResponseBody
    public Result<List<RcOrderVo>> confirm(long orderId) throws UnsupportedEncodingException {
        Result<List<RcOrderVo>> result = new Result<List<RcOrderVo>>();
        RcOrder order = orderService.findOrderById(orderId);
        order.setBuyerStatus(OrderBuyerStatusEnum.CONFIRMED.getValue());
        order.setSellerStatus(OrderSellerStatusEnum.CONFIRMED.getValue());
        // 订单操作流畅
        RcOrderOperation orderOperation = new RcOrderOperation();
        orderOperation.setOrderId(order.getOrderId());
        orderOperation.setOperationType(OrderOperationTypeEnum.CONFIRM.getValue());
        orderOperation.setMemberId(order.getMemberId());
        orderOperationService.addOrderOperation(orderOperation);
        orderService.modifyOrder(order);
        return result;

    }

    /**
     * 修改桌号
     *
     * @param orderId
     * @param sectionId
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("table")
    @ResponseBody
    public Result<String> table(long orderId, int sectionId, int tableNumber) throws UnsupportedEncodingException {
        Result<String> result = new Result<String>();
        RcMerchantSection section = merchantSectionService.findMerchantSectionById(sectionId);
        if(section == null) {
        	result.setCode("0");
        	return result;
        }
        RcMerchantTable table = merchantSectionService.findMerchantTablesBySectionIdAndTableNumber(sectionId, tableNumber);
        if(table == null) {
        	result.setCode("0");
        	return result;
        }
        RcOrder order = orderService.findOrderById(orderId);
        order.setSectionId(sectionId);
        order.setTableId(table.getTableId());
        order.setSectionName(section.getName());
        order.setTableNumber(tableNumber);
        orderService.modifyOrder(order);
        return result;

    }

    /**
     * 就餐人数
     *
     * @param orderId
     * @param meals
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("meals")
    @ResponseBody
    public Result<String> meals(long orderId, Integer meals) throws UnsupportedEncodingException {
        Result<String> result = new Result<String>();
        RcOrder order = orderService.findOrderById(orderId);
        order.setCustomerCount(meals);
        orderService.modifyOrder(order);
        return result;

    }

    /**
     * 加菜页面
     *
     * @param orderId
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("selectOrderProd")
    @ResponseBody
    public Result<List<RcProd>> selectOrderProd(long orderId) {
        Result<List<RcProd>> result = new Result<>();
        RcOrder order = orderService.findOrderById(orderId);
    /*      List<RcOrderProd> rcOrderProdList = orderProdService.findOrderProdsByOrderId(orderId);
        RcProdCat prodCat = new RcProdCat();
        prodCat.setMerchantId(order.getMerchantId());
        List<RcProdCat> list = prodCatService.findPagedTopProdCatsWithChildren(prodCat, null);*/
        //获取当前店铺商品
        RcProd prodParam = new RcProd();
        prodParam.setDelFlag(YesOrNoEnum.NO.getValue());
        prodParam.setMerchantId(order.getMerchantId());
        prodParam.setStatuses(Arrays.asList(ProdStatusEnum.SELLING.getValue()));
        List<RcProd> prods = prodService.findProds(prodParam);
        RcCoupon couponParam = new RcCoupon();
		couponParam.setReceiveEndTimeFrom(new Date());
		couponParam.setNeedGet(YesOrNoEnum.NO.getValue());
		couponParam.setStatus(CouponStatusEnum.PUBLISHED.getValue());
		couponParam.setDelFlag(YesOrNoEnum.NO.getValue());
		couponParam.setMerchantId(order.getMerchantId());
		List<RcCoupon> coupons = couponService.findCoupons(couponParam);
        for (RcProd rcProd : prods) {
        	List<RcCoupon> prodCoupons = new ArrayList<RcCoupon>();
            List<RcCoupon> prodCatCoupons = new ArrayList<RcCoupon>();
            for(RcCoupon coupon : coupons) {
            	if(coupon.getProdId() > 0 && coupon.getProdId().equals(rcProd.getProdId())) {
            		prodCoupons.add(coupon);
            		continue;
            	}
            	if(coupon.getProdId() == 0 && (coupon.getProdCatId() == 0 || coupon.getProdCatId() > 0 && coupon.getProdCatId().equals(rcProd.getProdCatId()))) {
            		prodCatCoupons.add(coupon);
            		continue;
            	}
            }
            
            if(!CollectionUtils.isEmpty(prodCoupons)) {
            	RcCoupon coupon = prodCoupons.get(0);
            	rcProd.setOriginPrice(rcProd.getPrice());
				if(coupon.getDiscountType() == CouponDiscountTypeEnum.PERCENTAGE.getValue()) {
					rcProd.setPrice(rcProd.getPrice().multiply(coupon.getAmount()));
				} else if(coupon.getDiscountType() == CouponDiscountTypeEnum.SUBSTRACT.getValue()) {
					rcProd.setPrice(rcProd.getPrice().subtract(coupon.getAmount()));
				} else if(coupon.getDiscountType() == CouponDiscountTypeEnum.FIXED.getValue()) {
					rcProd.setPrice(coupon.getAmount());
				}
            } else if(!CollectionUtils.isEmpty(prodCatCoupons)) {
            	RcCoupon coupon = prodCatCoupons.get(0);
            	rcProd.setOriginPrice(rcProd.getPrice());
				if(coupon.getDiscountType() == CouponDiscountTypeEnum.PERCENTAGE.getValue()) {
					rcProd.setPrice(rcProd.getPrice().multiply(coupon.getAmount()));
				} else if(coupon.getDiscountType() == CouponDiscountTypeEnum.SUBSTRACT.getValue()) {
					rcProd.setPrice(rcProd.getPrice().subtract(coupon.getAmount()));
				} else if(coupon.getDiscountType() == CouponDiscountTypeEnum.FIXED.getValue()) {
					rcProd.setPrice(coupon.getAmount());
				}
            }
            
            List<RcProdSku> prodSkuList = prodSkuService.findProdSkusByProdId(rcProd.getProdId());
            for (RcProdSku rcProdSku : prodSkuList) {
            	if(!CollectionUtils.isEmpty(prodCoupons)) {
                	RcCoupon coupon = prodCoupons.get(0);
                	rcProdSku.setOriginPrice(rcProdSku.getPrice());
        			if(coupon.getDiscountType() == CouponDiscountTypeEnum.PERCENTAGE.getValue()) {
        				rcProdSku.setPrice(rcProdSku.getPrice().multiply(coupon.getAmount()));
        			} else if(coupon.getDiscountType() == CouponDiscountTypeEnum.SUBSTRACT.getValue()) {
        				rcProdSku.setPrice(rcProdSku.getPrice().subtract(coupon.getAmount()));
        			} else if(coupon.getDiscountType() == CouponDiscountTypeEnum.FIXED.getValue()) {
        				rcProdSku.setPrice(coupon.getAmount());
        			}
                } else if(!CollectionUtils.isEmpty(prodCatCoupons)) {
                	RcCoupon coupon = prodCatCoupons.get(0);
                	rcProdSku.setOriginPrice(rcProdSku.getPrice());
        			if(coupon.getDiscountType() == CouponDiscountTypeEnum.PERCENTAGE.getValue()) {
        				rcProdSku.setPrice(rcProdSku.getPrice().multiply(coupon.getAmount()));
        			} else if(coupon.getDiscountType() == CouponDiscountTypeEnum.SUBSTRACT.getValue()) {
        				rcProdSku.setPrice(rcProdSku.getPrice().subtract(coupon.getAmount()));
        			} else if(coupon.getDiscountType() == CouponDiscountTypeEnum.FIXED.getValue()) {
        				rcProdSku.setPrice(coupon.getAmount());
        			}
                }
            	
                List<RcProdSkuPropInfo> prodSkuPropInfoList = prodSkuPropInfoService.findProdSkuPropInfosByProdSkuId(rcProdSku.getProdSkuId());
                rcProdSku.setProdSkuPropInfos(prodSkuPropInfoList);
            }
            rcProd.setProdSkus(prodSkuList);

        }
        //过滤订单中的菜品
       /* Iterator<RcProdCat> rcProdCatIterator = list.iterator();
        while (rcProdCatIterator.hasNext()) {
            RcProdCat rcProdCat = rcProdCatIterator.next();
            List<RcProd> rcProdList = rcProdCat.getProds();
            Iterator<RcProd> rcProdIterator = rcProdList.iterator();
            while (rcProdIterator.hasNext()) {
                RcProd rcProd = rcProdIterator.next();
                for (RcOrderProd rcOrderProd : rcOrderProdList) {
                    if (StringUtils.isNotEmpty(rcOrderProd.getProdSkuId()) && StringUtils.isNotEmpty(rcProd.getProdSkuId())) {
                        if(rcOrderProd.getProdSkuId().equals(rcProd.getProdSkuId())){
                           // rcProdIterator.remove();
                             }
                    } else {
                        if(rcOrderProd.getProdId().equals(rcProd.getProdId()))
                            rcProdIterator.remove();
                    }
                }
            }
            if(org.apache.commons.collections.CollectionUtils.isEmpty(rcProdList)){
                rcProdCatIterator.remove();
            }
        }*/

        result.setData(prods);
        return result;
    }
    
    @RequestMapping("printchecklist")
    @ResponseBody
    public Result<RcOrder> printCheckList(long orderId) {
    	Result<RcOrder> result = new Result<RcOrder>();
    	RcOrderProd param = new RcOrderProd();
    	param.setOrderId(orderId);
    	param.setStatus(OrderProdStatusEnum.UNCONFIRMED.getValue());
    	List<RcOrderProd> orderProds = orderProdService.findOrderProds(param);
    	for(RcOrderProd orderProd : orderProds) {
    		orderProd.setStatus(OrderProdStatusEnum.CONFIRMED.getValue());
    		orderProdService.modifyOrderProdById(orderProd);
    	}
    	return result;
    }

    /**
     * 加菜
     *
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("addOrderProd")
    @ResponseBody
    public Result<RcOrder> addOrderProd(@RequestBody RcOrder order, HttpServletRequest httpServletRequest) {
        Result<RcOrder> result = new Result<RcOrder>();
        RcMerchantUser rcMerchantUser= (RcMerchantUser) httpServletRequest.getSession().getAttribute(Constants.MEMBER_INFO_IN_SESSION_KEY);
        //数据库订单
        RcOrder orderDb = orderService.findBuyerOrderByOrderId(order.getOrderId());
        RcMerchant merchant = merchantService.findMerchantById(orderDb.getMerchantId());
		RcCoupon couponParam = new RcCoupon();
		couponParam.setReceiveEndTimeFrom(new Date());
		couponParam.setNeedGet(YesOrNoEnum.NO.getValue());
		couponParam.setStatus(CouponStatusEnum.PUBLISHED.getValue());
		couponParam.setDelFlag(YesOrNoEnum.NO.getValue());
		couponParam.setMerchantId(merchant.getMerchantId());
		List<RcCoupon> coupons = couponService.findCoupons(couponParam);
		if (!CollectionUtils.isEmpty(order.getOrderProds())) {
			for (RcOrderProd orderProd : order.getOrderProds()) {
	            RcProd prod = prodService.findProdById(orderProd.getProdId());
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
	            
	            boolean isFound = false;
				for (RcOrderProd orderProdDb : orderDb.getOrderProds()) {
					if(orderProdDb.getStatus() == OrderProdStatusEnum.CONFIRMED.getValue()) {
						continue;
					}
					float oldQuantity = orderProdDb.getQuantity() == null ? 0 : orderProdDb.getQuantity();
					BigDecimal oldAmount = orderProdDb.getAmount() == null ? new BigDecimal(0) : orderProdDb.getAmount();
	                if (StringUtils.isNotBlank(orderProdDb.getProdSkuId()) && StringUtils.isNotBlank(orderProd.getProdSkuId())) {
	                    if (orderProdDb.getProdSkuId().equals(orderProd.getProdSkuId())) {
	                        orderProdDb.setQuantity(oldQuantity + orderProd.getQuantity());
//	                        if (orderProdDb.getPriceManner() == ProdPriceMannerEnum.BY_WEIGHT.getValue()) {
//	                        	orderProdDb.setAmount(orderProdDb.getProdPrice().multiply(new BigDecimal(String.valueOf(orderProdDb.getQuantity())).divide(new BigDecimal(orderProdDb.getMeasureUnitCount()), 2, RoundingMode.HALF_UP)));
//	                        } else {
	                        	orderProdDb.setAmount(orderProdDb.getProdPrice().multiply(new BigDecimal(String.valueOf(orderProdDb.getQuantity()))));
//	                        }
	                        orderProdService.modifyOrderProdById(orderProdDb);
	                        BigDecimal sub = orderProdDb.getAmount().subtract(oldAmount);
	                        orderDb.setProductPrice(orderDb.getProductPrice().add(sub));
	                        isFound = true;
	                        continue;
	                    }
	                }
	                if (StringUtils.isBlank(orderProdDb.getProdSkuId()) && StringUtils.isBlank(orderProd.getProdSkuId())) {
	                    if (orderProdDb.getProdId().equals(orderProd.getProdId())) {
	                        orderProdDb.setQuantity(oldQuantity + orderProd.getQuantity());
//	                        if (orderProdDb.getPriceManner() == ProdPriceMannerEnum.BY_WEIGHT.getValue()) {
//	                        	orderProdDb.setAmount(orderProdDb.getProdPrice().multiply(new BigDecimal(String.valueOf(orderProdDb.getQuantity())).divide(new BigDecimal(orderProdDb.getMeasureUnitCount()), 2, RoundingMode.HALF_UP)));
//	                        } else {
	                        	orderProdDb.setAmount(orderProdDb.getProdPrice().multiply(new BigDecimal(String.valueOf(orderProdDb.getQuantity()))));
//	                        }
	                        orderProdService.modifyOrderProdById(orderProdDb);
	                        BigDecimal sub = orderProdDb.getAmount().subtract(oldAmount);
	                        orderDb.setProductPrice(orderDb.getProductPrice().add(sub));
	                        isFound = true;
	                        continue;
	                    }
	                }
	            }

				if(!isFound) {
					orderProd.setPriceManner(prod.getPriceManner());
		            orderProd.setMeasureUnit(prod.getMeasureUnit());
		            orderProd.setMeasureUnitCount(prod.getMeasureUnitCount());
	                if (StringUtils.isBlank(orderProd.getProdSkuId())) {
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
//	                if (prod.getPriceManner() == ProdPriceMannerEnum.BY_WEIGHT.getValue()) {
//	                    orderProd.setAmount(orderProd.getProdPrice().multiply(new BigDecimal(String.valueOf(orderProd.getQuantity())).divide(new BigDecimal(prod.getMeasureUnitCount()), 2, RoundingMode.HALF_UP)));
//	                } else {
	                    orderProd.setAmount(orderProd.getProdPrice().multiply(new BigDecimal(String.valueOf(orderProd.getQuantity()))));
//	                }
	                orderProd.setProdName(prod.getName());
	                orderProd.setOrderId(orderDb.getOrderId());
	                long orderProdId = orderProdService.addOrderProd(orderProd);
	                
	                List<RcOrderProdSkuPropInfo> orderProdSkuPropInfos = orderProd.getOrderProdSkuPropInfos();
	                if (!CollectionUtils.isEmpty(orderProdSkuPropInfos)) {
	                    for (RcOrderProdSkuPropInfo orderProdSkuPropInfo : orderProdSkuPropInfos) {
	                        orderProdSkuPropInfo.setOrderProdId(orderProdId);
	                        orderProdSkuPropInfoService.addOrderProdSkuPropInfo(orderProdSkuPropInfo);
	                    }
	                }
	                orderDb.setProductPrice(orderDb.getProductPrice().add(orderProd.getAmount()));//修改商品金额
				}
			}
		        
            orderDb.setServiceFee(orderDb.getProductPrice().multiply(new BigDecimal(String.valueOf(merchant.getServiceCharge()))).setScale(0, RoundingMode.HALF_UP));//服务费
            RcOrderFee serviceFee = orderFeeService.findOrderFeeByType(order.getOrderId(), OrderFeeTypeEnum.SERVICE_FEE.getValue());
	        if(serviceFee == null) {
	        	serviceFee = new RcOrderFee();
	        	serviceFee.setOrderId(orderDb.getOrderId());
	        	serviceFee.setAmount(orderDb.getServiceFee());
	        	serviceFee.setType(OrderFeeTypeEnum.SERVICE_FEE.getValue());
	        	serviceFee.setAddToTotal(YesOrNoEnum.YES.getValue());
	        	serviceFee.setFeeRate(merchant.getServiceCharge());
	            orderFeeService.addOrderFee(serviceFee);
	        } else {
	        	serviceFee.setAmount(orderDb.getServiceFee());
	        	serviceFee.setFeeRate(merchant.getServiceCharge());
	            orderFeeService.modifyOrderFee(serviceFee);
	        }
			
			orderDb.setTax(orderDb.getProductPrice().add(orderDb.getServiceFee()).multiply(new BigDecimal(String.valueOf(merchant.getTaxRate()))).setScale(0, RoundingMode.HALF_UP));//税金
			RcOrderFee tax = orderFeeService.findOrderFeeByType(order.getOrderId(), OrderFeeTypeEnum.TAX.getValue());
	        if(tax == null) {
	        	tax = new RcOrderFee();
	        	tax.setOrderId(orderDb.getOrderId());
	            tax.setAmount(orderDb.getTax());
	            tax.setType(OrderFeeTypeEnum.TAX.getValue());
	            tax.setAddToTotal(YesOrNoEnum.YES.getValue());
	            tax.setFeeRate(merchant.getTaxRate());
	            orderFeeService.addOrderFee(tax);
	        } else {
	            tax.setAmount(orderDb.getTax());
	            tax.setFeeRate(merchant.getTaxRate());
	            orderFeeService.modifyOrderFee(tax);
	        }
		        
            orderDb.setPayPrice(orderDb.getProductPrice().add(orderDb.getTax()).add(orderDb.getServiceFee()));//实际支付金额
	        
	        RcOrderOperation orderOperation = new RcOrderOperation();
	        orderOperation.setOperationType(OrderOperationTypeEnum.MODIFY.getValue());
	        orderOperation.setOrderId(orderDb.getOrderId());
	        if(rcMerchantUser!=null)
	        orderOperation.setMemberId(rcMerchantUser.getMemberId());
	        orderOperationService.addOrderOperation(orderOperation);
	        orderService.modifyOrder(orderDb);
		}

        result.setData(orderDb);
        return result;
    }

//    private void orderCouponAmount(RcOrder orderDb, RcOrderProd orderProd, RcOrderProd orderProdDb) {
//        if (orderProdDb.getDiscountType() != null) {
//            if (orderProdDb.getDiscountType() == DiscountTypeEnum.PERCENT.getValue()) {
//                orderDb.setCouponAmount(orderDb.getCouponAmount().add(orderProd.getAmount().multiply(new BigDecimal(1).subtract(orderProdDb.getCouponAmount()))));
//            } else {
//                if (orderProdDb.getPriceManner() == ProdPriceMannerEnum.BY_WEIGHT.getValue())
//                    orderDb.setCouponAmount(orderDb.getCouponAmount().add(orderProdDb.getCouponAmount().multiply(new BigDecimal(orderProd.getQuantity()).divide(new BigDecimal(orderProdDb.getMeasureUnitCount()), 2, RoundingMode.HALF_UP))));
//                else
//                    orderDb.setCouponAmount(orderDb.getCouponAmount().add(orderProdDb.getCouponAmount().multiply(new BigDecimal(orderProd.getQuantity()))));
//            }
//        }
//    }

    /**
     * 减菜
     *
     * @param orderProdId
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("deleteOrderProd")
    @ResponseBody
    public Result<String> deleteOrderProd(long orderProdId, HttpServletRequest httpServletRequest) throws UnsupportedEncodingException {
        Result<String> result = new Result<String>();
        RcMerchantUser rcMerchantUser= (RcMerchantUser) httpServletRequest.getSession().getAttribute(Constants.MEMBER_INFO_IN_SESSION_KEY);
        RcOrderProd orderProd = orderProdService.findOrderProdById(orderProdId);
//        if(orderProd.getQuantity() == null) {
        	orderProdService.remove(orderProd);
        	
//        	RcOrderOperation orderOperation = new RcOrderOperation();
//            orderOperation.setOperationType(OrderOperationTypeEnum.MODIFY.getValue());
//            orderOperation.setOrderId(orderProd.getOrderId());
//            if(rcMerchantUser!=null)
//            orderOperation.setMemberId(rcMerchantUser.getMemberId());
//            orderOperationService.addOrderOperation(orderOperation);
            
//        	return result;
//        }
        
        if(orderProd.getQuantity() != null) {
	        RcOrder order = orderService.findOrderById(orderProd.getOrderId());
	        order.setProductPrice(order.getProductPrice().subtract(orderProd.getAmount()));
	        RcMerchant merchant = merchantService.findMerchantById(order.getMerchantId());
	
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
        }
//        orderProd.setQuantity(-orderProd.getQuantity());
//        orderProd.setAmount(orderProd.getAmount().negate());
//        orderProd.setStatus(OrderProdStatusEnum.UNCONFIRMED.getValue());
//        long newOrderProdId = orderProdService.addOrderProd(orderProd);
//        List<RcOrderProdSkuPropInfo> orderProdSkuPropInfos = orderProdSkuPropInfoService.findOrderProdSkuPropInfosByOrderProdId(orderProdId);
//        if (!CollectionUtils.isEmpty(orderProdSkuPropInfos)) {
//            for (RcOrderProdSkuPropInfo orderProdSkuPropInfo : orderProdSkuPropInfos) {
//                orderProdSkuPropInfo.setOrderProdId(newOrderProdId);
//                orderProdSkuPropInfoService.addOrderProdSkuPropInfo(orderProdSkuPropInfo);
//            }
//        }
        //orderProdService.modifyOrderProdById(orderProd);
        RcOrderOperation orderOperation = new RcOrderOperation();
        orderOperation.setOperationType(OrderOperationTypeEnum.MODIFY.getValue());
        orderOperation.setOrderId(orderProd.getOrderId());
        if(rcMerchantUser!=null)
        orderOperation.setMemberId(rcMerchantUser.getMemberId());
        orderOperationService.addOrderOperation(orderOperation);
        
        return result;
    }

    @RequestMapping("modifyprodquantity")
    @ResponseBody
    public Result<String> modifyProdQuantity(long orderProdId, float quantity,HttpServletRequest httpServletRequest) throws UnsupportedEncodingException {
      RcMerchantUser rcMerchantUser= (RcMerchantUser) httpServletRequest.getSession().getAttribute(Constants.MEMBER_INFO_IN_SESSION_KEY);
        Result<String> result = new Result<String>();
        RcOrderProd orderProd = orderProdService.findOrderProdById(orderProdId);
        if(quantity == 0) {
        	orderProdService.remove(orderProd);
        	if(orderProd.getQuantity() != null) {
    	        RcOrder order = orderService.findOrderById(orderProd.getOrderId());
    	        order.setProductPrice(order.getProductPrice().subtract(orderProd.getAmount()));
    	        RcMerchant merchant = merchantService.findMerchantById(order.getMerchantId());
    	
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
            }
            RcOrderOperation orderOperation = new RcOrderOperation();
            orderOperation.setOperationType(OrderOperationTypeEnum.MODIFY.getValue());
            orderOperation.setOrderId(orderProd.getOrderId());
            if(rcMerchantUser!=null)
            orderOperation.setMemberId(rcMerchantUser.getMemberId());
            orderOperationService.addOrderOperation(orderOperation);
            
            return result;
        }
        
        float oldQuantity = orderProd.getQuantity() == null ? 0 : orderProd.getQuantity();
        BigDecimal oldAmount = orderProd.getAmount() == null ? new BigDecimal(0) : orderProd.getAmount();
        if(oldQuantity == quantity) {
        	RcOrderOperation orderOperation = new RcOrderOperation();
            orderOperation.setOperationType(OrderOperationTypeEnum.MODIFY.getValue());
            orderOperation.setOrderId(orderProd.getOrderId());
            if(rcMerchantUser!=null)
            orderOperation.setMemberId(rcMerchantUser.getMemberId());
            orderOperationService.addOrderOperation(orderOperation);
            
        	return result;
        }
        RcOrder order = orderService.findOrderById(orderProd.getOrderId());
        RcMerchant merchant = merchantService.findMerchantById(order.getMerchantId());
        orderProd.setQuantity(quantity);
        order.setTaxRate(merchant.getTaxRate());
//        if (orderProd.getPriceManner() == ProdPriceMannerEnum.BY_WEIGHT.getValue()) {
//            orderProd.setAmount(orderProd.getProdPrice().multiply(new BigDecimal(String.valueOf(orderProd.getQuantity())).divide(new BigDecimal(orderProd.getMeasureUnitCount()), 2, RoundingMode.HALF_UP)));
//        } else {
            orderProd.setAmount(orderProd.getProdPrice().multiply(new BigDecimal(String.valueOf(orderProd.getQuantity()))));
//        }
        order.setProductPrice(order.getProductPrice().subtract(oldAmount).add(orderProd.getAmount()));
	        
        order.setServiceFee(order.getProductPrice().multiply(new BigDecimal(String.valueOf(merchant.getServiceCharge()))).setScale(0, RoundingMode.HALF_UP));//服务费
        RcOrderFee serviceFee = orderFeeService.findOrderFeeByType(order.getOrderId(), OrderFeeTypeEnum.SERVICE_FEE.getValue());
        if(serviceFee == null) {
        	serviceFee = new RcOrderFee();
        	serviceFee.setOrderId(order.getOrderId());
        	serviceFee.setAmount(order.getServiceFee());
        	serviceFee.setType(OrderFeeTypeEnum.SERVICE_FEE.getValue());
        	serviceFee.setAddToTotal(YesOrNoEnum.YES.getValue());
            orderFeeService.addOrderFee(serviceFee);
        } else {
        	serviceFee.setAmount(order.getServiceFee());
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
            orderFeeService.addOrderFee(tax);
        } else {
            tax.setAmount(order.getTax());
            orderFeeService.modifyOrderFee(tax);
        }
        
        order.setPayPrice(order.getProductPrice().add(order.getTax()).add(order.getServiceFee()));//实际支付金额
        orderService.modifyOrder(order);
        orderProdService.modifyOrderProdById(orderProd);
        RcOrderOperation orderOperation = new RcOrderOperation();
        orderOperation.setOperationType(OrderOperationTypeEnum.MODIFY.getValue());
        orderOperation.setOrderId(order.getOrderId());
        if(rcMerchantUser!=null)
        orderOperation.setMemberId(rcMerchantUser.getMemberId());
        orderOperationService.addOrderOperation(orderOperation);
        
        return result;
    }

	/*
     * @ModelAttribute("rcOrderVo") public RcOrderVo setMerchantId(RcOrderVo
	 * rcOrderVo,HttpServletRequest httpServletRequest){
	 * if(httpServletRequest.getAttribute("merchantId")!=null)
	 * rcOrderVo.setMerchantId(httpServletRequest.getAttribute("merchantId").
	 * toString()); if(httpServletRequest.getAttribute("vendorId")!=null)
	 * rcOrderVo.setVendorId(httpServletRequest.getAttribute("vendorId").
	 * toString()); return rcOrderVo; }
	 */
	/**
	 * 查询订单列表
	 * 
	 * @param request
	 * @param model
	 * @param currentPage
	 * @param param
	 * @param isSuccess
	 * @param upDown
	 * @param httpServletRequest 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("orderIndex")
	public String orderIndex(HttpServletRequest request, Model model, Integer currentPage, RcOrder param,String sellerStatusStr,
			@RequestParam(value = "is_success", required = false) Boolean isSuccess,
			@RequestParam(value = "up_down", required = false) String upDown, HttpServletRequest httpServletRequest)
			throws UnsupportedEncodingException {
		if(StringUtils.isNotBlank(sellerStatusStr)) {
			String[] sellerStatusStrs = StringUtils.split(sellerStatusStr, ",");
			param.setSellerStatuses(new ArrayList<Integer>());
			for(String sellerStatus : sellerStatusStrs) {
				param.getSellerStatuses().add(Integer.parseInt(sellerStatus));
			}
		}
		Integer merchantUserId = getMerchantUserId(httpServletRequest);
		if(merchantUserId != null) {
			RcMerchantUser merchantUser = merchantUserService.findMerchantUserById(merchantUserId);
			param.setMerchantId(merchantUser.getMerchantId());
		}
		Integer vendorUserId = getVendorUserId(httpServletRequest);
		if(vendorUserId != null) {
			RcVendorUser vendorUser = vendorUserService.findVendorUserById(vendorUserId);
			param.setVendorId(vendorUser.getVendorId());
		}
		if(param.getCreateTimeTo() != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(param.getCreateTimeTo());
			cal.add(Calendar.DATE, 1);
			cal.add(Calendar.SECOND, -1);
			param.setCreateTimeTo(cal.getTime());
		}
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
		List<RcOrder> orders = orderService.findPagedOrders(param, page);
		for (RcOrder order : orders) {
           /* if(order.getTableId()!=null&&order.getTableId()>0){
                order.setTableNumber(merchantTableService.findOneByTableId(order.getTableId()).getTableNumber());
            }*/
			order.setMember(buyerService.findBuyerById(order.getMemberId()));
			order.setMerchant(merchantService.findMerchantById(order.getMerchantId()));
			if(!CollectionUtils.isEmpty(order.getOrderProds())) {
				for (RcOrderProd orderProd : order.getOrderProds()) {
					orderProd.setProd(prodService.findProdById(orderProd.getProdId()));
					RcProdImg prodImg = prodImgService.findFirstProdImgByProdSkuId(orderProd.getProdSkuId());
					if(prodImg == null) {
						prodImg = prodImgService.findFirstProdImgByProdSkuId(String.valueOf(orderProd.getProdId()));
					}
					orderProd.setProdImgUrl(prodImg.getImgUrl());
					if(!orderProd.getProdSkuId().equals(String.valueOf(orderProd.getProdId()))) {
						List<RcOrderProdSkuPropInfo> orderProdSkuPropInfos = orderProdSkuPropInfoService.findOrderProdSkuPropInfosByOrderProdId(orderProd.getOrderProdId());
						orderProd.setOrderProdSkuPropInfos(orderProdSkuPropInfos);
					}
				}
			}
			order.setRounding(new BigDecimal(0));
			order.setTax(new BigDecimal(0));
			order.setServiceFee(new BigDecimal(0));
			List<RcOrderFee> orderFees = orderFeeService.findOrderFeesByOrderId(order.getOrderId());
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
		}
		model.addAttribute("list", orders);
		model.addAttribute("is_success", isSuccess);
		model.addAttribute("up_down", upDown);
		if (flag) {
			return "orderIndex";
		} else {
			return "orderIndex_inner";
		}
	}

	@RequestMapping("orderIndexExcel")
	public void orderIndexExcel(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,String sellerStatusStr,String orderIdds,
			Model model, Integer currentPage, RcOrder param,
			@RequestParam(value = "is_success", required = false) Boolean isSuccess,
			@RequestParam(value = "up_down", required = false) String upDown) throws IOException {
		if(StringUtils.isNotBlank(orderIdds)){
			String[] orderIdsStrs=StringUtils.split(orderIdds, ",,,");
			//order.setorde(new ArrayList<Integer>());
			param.setOrderIds(new ArrayList<Long>());
			for (String sellerStatus : orderIdsStrs) {
				param.getOrderIds().add(Long.parseLong(sellerStatus));
			}
		}
		if(StringUtils.isNotBlank(sellerStatusStr)){
			String[] sellerStatusStrs=StringUtils.split(sellerStatusStr, ",");
			param.setSellerStatuses(new ArrayList<Integer>());
			for (String sellerStatus : sellerStatusStrs) {
				param.getSellerStatuses().add(Integer.parseInt(sellerStatus));
			}
		}
		Integer merchantUserId = getMerchantUserId(httpServletRequest);
		if(merchantUserId != null) {
			RcMerchantUser merchantUser = merchantUserService.findMerchantUserById(merchantUserId);
			param.setMerchantId(merchantUser.getMerchantId());
		}
		Integer vendorUserId = getVendorUserId(httpServletRequest);
		if(vendorUserId != null) {
			RcVendorUser vendorUser = vendorUserService.findVendorUserById(vendorUserId);
			param.setVendorId(vendorUser.getVendorId());
		}
		if(param.getCreateTimeTo() != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(param.getCreateTimeTo());
			cal.add(Calendar.DATE, 1);
			cal.add(Calendar.SECOND, -1);
			param.setCreateTimeTo(cal.getTime());
		}
	
		Page page =	new Page();
		page.setPageSize(Integer.MAX_VALUE);
		List<RcOrder> orders = orderService.findPagedOrders(param, page);
	
		for (RcOrder order : orders) {
			order.setMerchant(merchantService.findMerchantById(order.getMerchantId()));
			order.setMember(buyerService.findBuyerById(order.getMemberId()));
			order.setOrderConsignee(orderConsigneeService.findOrderConsigneeById(order.getOrderId()));
			order.setOrderProds(orderProdService.findOrderProdsByOrderId(order.getOrderId()));
			for (RcOrderProd orderProd : order.getOrderProds()) {
				orderProd.setProd(prodService.findProdById(orderProd.getProdId()));
				RcProdImg prodImg = prodImgService.findFirstProdImgByProdSkuId(orderProd.getProdSkuId());
				if(prodImg == null) {
					prodImg = prodImgService.findFirstProdImgByProdSkuId(String.valueOf(orderProd.getProdId()));
				}
				orderProd.setProdImgUrl(prodImg.getImgUrl());
				if(!orderProd.getProdSkuId().equals(String.valueOf(orderProd.getProdId()))) {
					List<RcOrderProdSkuPropInfo> orderProdSkuPropInfos = orderProdSkuPropInfoService.findOrderProdSkuPropInfosByOrderProdId(orderProd.getOrderProdId());
					orderProd.setOrderProdSkuPropInfos(orderProdSkuPropInfos);
				}
			}
			order.setRounding(new BigDecimal(0));
			order.setTax(new BigDecimal(0));
			order.setServiceFee(new BigDecimal(0));
			List<RcOrderFee> orderFees = orderFeeService.findOrderFeesByOrderId(order.getOrderId());
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
		}
	
		/*
		 * for (RcOrderVo rcOrderVo2 : rcOrderVoss) { if
		 * (rcOrderVo2.getBuyerMemo() != null)
		 * rcOrderVo2.setOrderMemberMemoStr(new
		 * String(rcOrderVo2.getBuyerMemo(), "UTF-8")); List<RcOrderProdVo>
		 * rcOrderProdVos =
		 * iOrderService.findOrderProdVOsByOrderId(rcOrderVo2.getOrderId());
		 * 
		 * for (RcOrderProdVo rcOrderProdVo : rcOrderProdVos) {
		 * List<RcProdSkuPropInfo> prodSkuPropInfos = iProdSkuPropInfoService
		 * .selectProdSkuPropInfo(rcOrderProdVo.getProdSkuId());
		 * ArrayList<String> prodProp = new ArrayList<String>(); for
		 * (RcProdSkuPropInfo rcProdSkuPropInfo : prodSkuPropInfos) {
		 * prodProp.add(rcProdSkuPropInfo.getProdPropVal()); }
		 * rcOrderProdVo.setProdProp(prodProp);
		 * 
		 * }
		 * 
		 * rcOrderVo2.setProduct(rcOrderProdVos); }
		 */

        createExcel(httpServletRequest, httpServletResponse, orders);
    }

    public void createExcel(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                            List<RcOrder> rcOrders) throws IOException {
        String[] title = {"Order ID", "Order Time", "Order Price", "Discount", "Freight", "Pay Amount", "Store ID",
                "Store", "Buyer ID", "Buyer Username", "Buyer Phone", "Product", "Consignee Name", "Consignee Phone",
                "Consignee Address"};
        // 创建Excel工作簿

        Workbook workbook = new SXSSFWorkbook(100);
        // 创建一个工作表sheet
        Sheet sheet = workbook.createSheet();
        // 创建第一行
        Row row = sheet.createRow(0);

        ExportUtil exportUtil = new ExportUtil(workbook, sheet);
        CellStyle head = exportUtil.getHeadStyle();
        CellStyle body = exportUtil.getBodyStyle();
        // 插入第一行数据 id,name,sex
        for (int i = 0; i < title.length; i++) {
            sheet.setColumnWidth(i, 30 * 256);
            Cell cell = row.createCell(i);
            cell.setCellStyle(head);
            cell.setCellValue(title[i]);
        }
        // 追加数据
        for (int i = 1; i <= rcOrders.size(); i++) {
            Row rownext = sheet.createRow(i);
            RcOrder rcOrderVo = rcOrders.get(i - 1);
            Cell cell = rownext.createCell(0);
            cell.setCellStyle(body);
            cell.setCellValue(rcOrderVo.getOrderId());
            cell = rownext.createCell(1);
            cell.setCellValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rcOrderVo.getCreateTime()));
            cell = rownext.createCell(2);
            cell.setCellValue(rcOrderVo.getProductPrice().doubleValue());
            cell = rownext.createCell(3);
            cell.setCellValue(rcOrderVo.getProductPrice().add(rcOrderVo.getFreight())
                    .subtract(rcOrderVo.getProductPrice()).doubleValue());
            cell = rownext.createCell(4);
            cell.setCellValue(rcOrderVo.getFreight().doubleValue());
            cell = rownext.createCell(5);
            cell.setCellValue(rcOrderVo.getPayPrice().doubleValue());
            cell = rownext.createCell(6);
            cell.setCellValue(rcOrderVo.getMerchantId());
            cell = rownext.createCell(7);
            cell.setCellValue(rcOrderVo.getMerchant().getName());
            cell = rownext.createCell(8);
            cell.setCellValue(rcOrderVo.getMemberId());
            cell = rownext.createCell(9);
            cell.setCellValue(rcOrderVo.getMember().getName());
            cell = rownext.createCell(10);
            cell.setCellValue(rcOrderVo.getOrderConsignee().getMobile());
            cell = rownext.createCell(11);
            List<RcOrderProd> rcOrderProds = rcOrderVo.getOrderProds();
            StringBuffer sb = new StringBuffer();
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(rcOrderProds))
                for (RcOrderProd rcOrderProd : rcOrderProds) {
                    sb.append(rcOrderProd.getProd().getName() + "_");
                    List<RcOrderProdSkuPropInfo> List = rcOrderProd.getOrderProdSkuPropInfos();
                    if(!CollectionUtils.isEmpty(List)) {
	                    for (RcOrderProdSkuPropInfo rcProdSkuPropInfo : List) {
	                        sb.append(rcProdSkuPropInfo.getProdPropVal() + "_");
	                    }
                    }
				/*
				 * String prop =
				 * StringUtils.join(rcOrderProd.getRcProdSkuPropInfos(), "_");
				 * sb.append(prop);
				 */
                    if (!sb.toString().endsWith("_")) {
                        sb.append("_");
                    }
                    sb.append("x" + rcOrderProd.getQuantity() + ";");
                }
            cell.setCellValue(sb.toString());
            cell = rownext.createCell(12);
            cell.setCellValue(rcOrderVo.getOrderConsignee().getName());
            cell = rownext.createCell(13);
            cell.setCellValue(rcOrderVo.getOrderConsignee().getMobile());
            cell = rownext.createCell(14);
            cell.setCellValue(rcOrderVo.getOrderConsignee().getAddress());
        }
        httpServletResponse.setHeader("Content-disposition", "attachment; filename=orderExcel.xlsx");// 组装附件名称和格式
        workbook.write(httpServletResponse.getOutputStream());
        IOUtils.closeQuietly(httpServletResponse.getOutputStream());
    }

	/*
	 * @RequestMapping("orderList")
	 * 
	 * @ResponseBody public DataGrid<RcOrderVo> orderList(int page, int rows,
	 * RcOrderVo rcOrderVo) throws UnsupportedEncodingException { Page pager =
	 * new Page(); // 初始化时第一页 pager.setPagination(true);
	 * pager.setPageSize(rows); pager.setCurrentPage(page);
	 * PageContext.setPage(pager); List<RcOrderVo> rcOrderVoss =
	 * iOrderService.findOrderVOs(rcOrderVo); for (RcOrderVo rcOrderVo2 :
	 * rcOrderVoss) { if (rcOrderVo2.getBuyerMemo() != null)
	 * rcOrderVo2.setOrderMemberMemoStr(new String(rcOrderVo2.getBuyerMemo(),
	 * "UTF-8")); } DataGrid<RcOrderVo> rDataGrid = new DataGrid<RcOrderVo>();
	 * rDataGrid.setRows(rcOrderVoss); rDataGrid.setTotal((int)
	 * pager.getTotalRows()); return rDataGrid; }
	 */

    @SuppressWarnings("unchecked")
    @RequestMapping("verifyInventory")
    @ResponseBody
    public Result<List<RcProdSku>> verifyInventory(String data, String timestamp, String nonceStr, String product,
                                                   String signature, HttpServletRequest httpServletRequest, HttpServletResponse response)
            throws AddressException, GeneralSecurityException, MessagingException {
        Result<List<RcProdSku>> result = new Result<List<RcProdSku>>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
        }

        int cityId = json.getInt("cityId");
        JSONArray prodIdArray = json.getJSONArray("prodIds");
        List<String> prodIds = new ArrayList<String>(JSONArray.toCollection(prodIdArray, String.class));
        JSONArray prodSkuIdArray = json.getJSONArray("prodSkuIds");
        List<String> prodSkuIds = new ArrayList<String>(JSONArray.toCollection(prodSkuIdArray, String.class));
        List<RcProdSku> prodStorages = new ArrayList<RcProdSku>();
        for (String prodId : prodIds) {
            int pId = Integer.parseInt(prodId);
            RcProd prod = prodService.findProdById(pId);
            if (prod.getCityId() != cityId) {
                RcProdSku prodSku = new RcProdSku();
                prodSku.setProdId(pId);
                prodSku.setProdSkuId(prodId);
                prodSku.setStorage(0);
                prodStorages.add(prodSku);
            } else {
                RcProdSku prodSku = new RcProdSku();
                prodSku.setProdId(pId);
                prodSku.setProdSkuId(prodId);
                prodSku.setStorage(prod.getInventory());
                VOConversionUtil.Entity2VO(prodSku, new String[]{"prodId", "prodSkuId", "storage"}, null);
                prodStorages.add(prodSku);
            }
        }
        for (String prodSkuId : prodSkuIds) {
            int pId = Integer.parseInt(prodSkuId.split("_")[0]);
            RcProd prod = prodService.findProdById(pId);
            if (prod.getCityId() != cityId) {
                RcProdSku prodSku = new RcProdSku();
                prodSku.setProdId(pId);
                prodSku.setProdSkuId(prodSkuId);
                prodSku.setStorage(0);
                prodStorages.add(prodSku);
            } else {
                RcProdSku prodSku = prodSkuService.findProdSkuByProdSkuId(prodSkuId);
                prodSku.setStorage(prodSku.getInventory());
                VOConversionUtil.Entity2VO(prodSku, new String[]{"prodId", "prodSkuId", "storage"}, null);
                prodStorages.add(prodSku);
            }
        }
        result.setData(prodStorages);
        return result;
    }

//    private long generateOrderId() throws SystemException {
//        StringBuilder sb = new StringBuilder("");
//        sb.append(propertyConstants.systemMachineId).append(propertyConstants.systemServerId);
//        int hashCode = UUID.randomUUID().toString().hashCode();
//        if (hashCode < 0) {
//            hashCode = -hashCode;
//            sb.append(1);
//        } else {
//            sb.append(0);
//        }
//        DecimalFormat format = new DecimalFormat("0000000000");
//        sb.append(format.format(hashCode));
//        return Long.parseLong(sb.toString());
//    }

    /**
     * 下订单
     *
     * @param data
     * @param timestamp
     * @param nonceStr
     * @param product
     * @param signature
     * @param response
     * @return
     * @throws MessagingException
     * @throws GeneralSecurityException
     * @throws AddressException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws SystemException
     * @throws Exception
     */
//    @RequestMapping("placingOrder")
//    @ResponseBody
//    public Result<List<RcOrder>> placingOrder(String data, String timestamp, String nonceStr, String product,
//                                              String signature, HttpServletRequest httpServletRequest, HttpServletResponse response)
//            throws AddressException, GeneralSecurityException, MessagingException, IOException, ClassNotFoundException, SystemException {
//        Date now = new Date();
//        Result<List<RcOrder>> result = new Result<List<RcOrder>>();
//        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
//        if (json == null) {
//            result.setCode("-1");
//            result.setResult("token验证失败！");
//        }
//
//        int buyerConsigneeId = json.getInt("consigneeId");// 收货信息
//        JSONArray orderJsonArray = json.getJSONArray("merchants");// 所有商家
//        int buyerId = json.getInt("memberId");// 用户id
//
////		if (lockService.lock(redisTemplate, memberId)) {
////			try {
//				RcBuyerConsignee buyerConsignee = buyerConsigneeService.findBuyerConsigneeById(buyerConsigneeId);
//				List<RcOrder> orders = new ArrayList<RcOrder>();
//				
//				for (Object orderObj : orderJsonArray) {
//					long orderId = generateOrderId();
//					JSONObject orderJsonObj = (JSONObject) orderObj;
//					RcOrder order = new RcOrder();
//					order.setOrderId(orderId);
//					order.setPayPrice(new BigDecimal(0));
//					order.setProductPrice(new BigDecimal(0));
//					order.setCouponAmount(new BigDecimal(0));
//					order.setOrderFees(new ArrayList<RcOrderFee>());
//					BigDecimal couponAmount = new BigDecimal(0);
//					
////					if(order.getFreight().compareTo(new BigDecimal(0)) > 0) {
////						RcOrderFee orderFee = new RcOrderFee();
////						orderFee.setOrderId(orderId);
////						orderFee.setType(OrderFeeTypeEnum.FREIGHT.getValue());
////						orderFee.setAmount(order.getFreight());
////						orderFee.setAddToTotal(YesOrNoEnum.YES.getValue());
////						order.getOrderFees().add(orderFee);
////					}
//					
////					try {
////						Integer couponId = orderJsonObj.getInt("couponId");// coupon
////						if(couponId != null) {
////							RcCoupon coupon = couponService.findCouponByCouponId(couponId);
////							couponAmount = coupon.getAmount();
////							RcOrderCoupon orderCoupon = new RcOrderCoupon();
////							orderCoupon.setOrderId(orderId);
////							orderCoupon.setMemberId(buyerId);
////							orderCoupon.setCouponId(couponId);
////							orderCoupon.setCouponAmount(couponAmount);
////							order.setCouponOrders(Arrays.asList(orderCoupon));
////							RcOrderFee orderFee = new RcOrderFee();
////							orderFee.setOrderId(orderId);
////							orderFee.setType(OrderFeeTypeEnum.COUPON.getValue());
////							orderFee.setAmount(couponAmount.negate());
////							orderFee.setAddToTotal(YesOrNoEnum.YES.getValue());
////							order.getOrderFees().add(orderFee);
////							order.setCouponAmount(couponAmount);
////						}
////					} catch (Exception e) {
////					}
//					
//					int merchantId = orderJsonObj.getInt("merchantId");
//					RcMerchant merchant = merchantService.findMerchantById(merchantId);
////					RcMember merchantMember = memberService.findMemberByMemberId(merchant.getMemberId());
////					merchant.setMember(merchantMember);
//
//            order.setMerchantId(merchantId);
//            order.setMerchant(merchant);
//            order.setMemberId(buyerId);
//            order.setCityId(merchant.getCityId());
//            order.setVendorId(merchant.getVendorId());
//            order.setBuyerStatus(OrderBuyerStatusEnum.UNPAID.getValue());
//            order.setSellerStatus(OrderSellerStatusEnum.UNPAID.getValue());
//            order.setDeliveryType(orderJsonObj.getInt("isDelivery"));
////					order.setPaymentInterface(orderJsonObj.getInt("paymentInterface"));
////					order.setPaymentType(orderJsonObj.getInt("paymentType"));
//            order.setBuyerMemo(orderJsonObj.getString("orderMemberMemo"));
//
//            RcOrderConsignee orderConsignee = new RcOrderConsignee();
//            orderConsignee.setOrderId(orderId);
//            BeanUtils.copyProperties(buyerConsignee, orderConsignee);
//            order.setOrderConsignee(orderConsignee);
//
//            order.setOrderProds(new ArrayList<RcOrderProd>());
//            JSONArray orderProdJsonArray = (JSONArray) orderJsonObj.get("prods");
//            for (Object orderProdObj : orderProdJsonArray) {
//                JSONObject orderProdJsonObj = (JSONObject) orderProdObj;
//                RcOrderProd orderProd = new RcOrderProd();
//
//                int prodId = orderProdJsonObj.getInt("prodId");
//                orderProd.setProdId(prodId);
//                orderProd.setOrderId(orderId);
//
//                int quantity = orderProdJsonObj.getInt("prodNum");
//                orderProd.setQuantity(quantity);
//
//                String prodSkuId = orderProdJsonObj.getString("prodSkuId");
//                if (StringUtils.isBlank(prodSkuId)) {
//                    orderProd.setProdSkuId(String.valueOf(prodId));
//                } else {
//                    orderProd.setProdSkuId(prodSkuId);
//                }
//
//                order.getOrderProds().add(orderProd);
//
//                RcProd prod = prodService.findProdById(prodId);
//                orderProd.setProdName(prod.getName());
//                if (prod.getStatus() == ProdStatusEnum.SOLD_OUT.getValue()) {
//                    if (prod.getDelFlag() == YesOrNoEnum.YES.getValue()) {
//                        result.setCode(ErrorValue.GOODS_DELETED.getStr());
//                        return result;
//                    } else {
//                        result.setCode(ErrorValue.GOODS_SHELF.getStr());
//                        return result;
//                    }
//                }
//
//                if (StringUtils.isBlank(prodSkuId)) {
//                    if (prod.getInventory() < quantity) {
//                        result.setResult("Stock shortage.");
//                        result.setCode(ErrorValue.UNDER_STOCK.getStr());
//                        return result;
//                    }
//
//                    orderProd.setProdPrice(prod.getPrice());
//                    orderProd.setProdOriginPrice(prod.getOriginPrice());
//                } else {
//                    RcProdSku prodSku = prodSkuService.findProdSkuByProdSkuId(prodSkuId);
//                    if (prodSku.getInventory() < quantity) {
//                        result.setResult("Stock shortage.");
//                        result.setCode(ErrorValue.UNDER_STOCK.getStr());
//                        return result;
//                    }
//
//                    // 商品总金额
//                    orderProd.setProdOriginPrice(prodSku.getOriginPrice());
//                    orderProd.setProdPrice(prodSku.getPrice());
//
//                    List<RcOrderProdSkuPropInfo> orderProdSkuPropInfos = new ArrayList<RcOrderProdSkuPropInfo>();
//                    String[] prodPropEnumIds = prodSkuId.split("_");
//                    for (int i = 1; i < prodPropEnumIds.length; i++) {
//                        RcProdSkuPropInfo prodSkuPropInfo = prodSkuPropInfoService.findProdSkuPropInfoByProdIdAndProdSkuPropEnumId(prodId, Integer.parseInt(prodPropEnumIds[i]));
//                        RcOrderProdSkuPropInfo orderProdSkuPropInfo = new RcOrderProdSkuPropInfo();
//                        orderProdSkuPropInfo.setProdPropName(prodSkuPropInfo.getProdPropName());
//                        orderProdSkuPropInfo.setProdPropVal(prodSkuPropInfo.getProdPropVal());
//                        orderProdSkuPropInfos.add(orderProdSkuPropInfo);
//                    }
//                    orderProd.setOrderProdSkuPropInfos(orderProdSkuPropInfos);
//                }
//                if (prod.getPriceManner() == ProdPriceMannerEnum.BY_WEIGHT.getValue()) {
//                    orderProd.setAmount(orderProd.getProdPrice().multiply(new BigDecimal(quantity).divide(new BigDecimal(prod.getMeasureUnitCount()), 2, RoundingMode.HALF_UP)));
//                } else {
//                    orderProd.setAmount(orderProd.getProdPrice().multiply(new BigDecimal(quantity)));
//                }
//                order.setProductPrice(order.getProductPrice().add(orderProd.getAmount()));
//            }
//
//            order.setPayPrice(order.getProductPrice().subtract(couponAmount).add(order.getFreight()));
//					/*
//					 * synchronized (this) { if (isDelivery == 0) { String hex =
//					 * Long.toHexString(new Date().getTime()); String hex8 =
//					 * hex.substring(hex.length() - 8, hex.length());
//					 * rcOrder.setOrderCode(exec(hex8)); } }
//					 */
//					
//					//保存操作流程
//					RcOrderOperation orderOperation = new RcOrderOperation();
//					orderOperation.setOperationType(OrderOperationTypeEnum.CREATE.getValue());
//					orderOperation.setOrderId(orderId);
//					orderOperation.setMemberId(buyerId);
//					orderOperation.setOperationTime(now);
//					order.setOrderOperations(Arrays.asList(orderOperation));
//					
//					orders.add(order);
//				}
//				
//				for(RcOrder order : orders) {
//					orderService.addOrder(order);
//					RcOrderConsignee orderConsignee = order.getOrderConsignee();
//					orderConsigneeService.addOrderConsignee(orderConsignee);
//					List<RcOrderCoupon> orderCoupons = order.getCouponOrders();
//					if(!CollectionUtils.isEmpty(orderCoupons)) {
//						for(RcOrderCoupon orderCoupon : orderCoupons) {
//							orderCouponService.addOrderCoupon(orderCoupon);
//							buyerCouponService.modifyBuyerCouponStatus(orderCoupon.getMemberId(), orderCoupon.getCouponId(), BuyerCouponStatusEnum.USED.getValue());
//						}
//					}
//					if(!CollectionUtils.isEmpty(order.getOrderFees())) {
//						for(RcOrderFee orderFee : order.getOrderFees()) {
//							orderFeeService.addOrderFee(orderFee);
//						}
//					}
//					//List<RcOrderProd> orderProds = new ArrayList<RcOrderProd>();
//					
//					if(!CollectionUtils.isEmpty(order.getOrderProds())) {
//						for(RcOrderProd orderProd : order.getOrderProds()) {
//							long orderProdId = orderProdService.addOrderProd(orderProd);
////							RcProd prod = prodService.findProdById(orderProd.getProdId());
////							prod.setSoldNum(prod.getSoldNum() + orderProd.getQuantity());
////							prodService.modifyProd(prod);
//							List<RcOrderProdSkuPropInfo> orderProdSkuPropInfos = orderProd.getOrderProdSkuPropInfos();
//							if(!CollectionUtils.isEmpty(orderProdSkuPropInfos)) {
//								for(RcOrderProdSkuPropInfo orderProdSkuPropInfo : orderProdSkuPropInfos) {
//									orderProdSkuPropInfo.setOrderProdId(orderProdId);
//									orderProdSkuPropInfoService.addOrderProdSkuPropInfo(orderProdSkuPropInfo);
//								}
//							}
//						}
//					}
//					
//					if(!CollectionUtils.isEmpty(order.getOrderOperations())) {
//						for(RcOrderOperation orderOperation : order.getOrderOperations()) {
//							orderOperationService.addOrderOperation(orderOperation);
//						}
//					}
//					
////					MailVo mailVo = new MailVo();
////					mailVo.setVelocity("order.vm");
////					mailVo.setHttpServletRequest(httpServletRequest);
////					mailVo.setSubject("one order sucess");
////					mailVo.setTo(order.getMerchant().getMember().getEmail());
////					mailVo.getContext().put("order", order);
////					mailService.sendMail(mailVo);
//        }
//
//        result.setData(orders);
//        return result;
////			} finally {
////				lockService.unlock(redisTemplate, memberId);
////				// iLockService.unLockProduct(redisTemplate, productIds);
////			}
////		} else {
////			result.setCode(ErrorValue.SYSTEM_BUSY.getStr());
////			result.setResult("System is busy.");
////			return result;
////		}
//    }

    /**
     * 验证支付密码
     *
     * @param data
     * @param timestamp
     * @param nonceStr
     * @param product
     * @param signature
     * @return
     */
    @RequestMapping("verifyPaymentPassword")
    @ResponseBody
    public Result<Object> verifyPaymentPassword(String data, String timestamp, String nonceStr, String product,
                                                String signature, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        Result<Object> result = new Result<Object>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
        }
        int buyerId = json.getInt("memberId");
        RcBuyer buyer = buyerService.findBuyerById(buyerId);
        String payPwd = json.getString("payPwd");

        if (buyer.getPassword() == null) {
            result.setCode(ErrorValue.PAYMENT_PASSWORD_NOT_SET.getStr());
            result.setResult("Payment password has not been set");
            return result;
        } else {
            if (!DigestUtils.md5Hex(payPwd).equals(buyer.getWalletPwd())) {
                result.setCode(ErrorValue.ERROR_PASSWORD.getStr());
                result.setResult("Payment password error.");
                return result;
            }
        }


//        JSONArray orders = json.getJSONArray("order");

//		Set<Integer> merchantIds = new HashSet<Integer>();
        //	List<RcOrder> rOrders = new ArrayList<RcOrder>();// 获取order
//		List<MailVo> mailVos = new ArrayList<MailVo>();// 获取order
//		List<String> sendSkuId = new ArrayList<String>();
        // 锁定操作用户和购买商品防止并发
//		Set<String> productIds = new HashSet<String>();
//		for (Object object : order) {
//			JSONObject jo = (JSONObject) object;
//			Long orderId = jo.getLong("orderId");
//			RcOrder rcOrder2 = orderService.findOrderByOrderId(orderId);
//			rcOrder2.setPaymentType(PaymentTypeEnum.BALANCE.getValue());
//			RcOrderConsignee rcConsignee = orderConsigneeService.findOrderConsignee(rcOrder2.getOrderId());
//			List<RcOrderProdVo> rcOrderProdVos = new ArrayList<RcOrderProdVo>();// iOrderProdService.findOrderProdVosByOrderId(rcOrder2.getOrderId());
//			for (RcOrderProdVo rcOrderProdVo : rcOrderProdVos) {
//				if (rcOrderProdVo.getProdSkuId() == null) {
//					productIds.add(rcOrderProdVo.getProdId() + "" + rcConsignee.getCityId());
//				} else {
//					productIds.add(rcOrderProdVo.getProdSkuId() + "" + rcConsignee.getCityId());
//				}
//			}

//			rOrders.add(rcOrder2);
//			merchantIds.add(rcOrder2.getMerchantId());
//		}
//		List<RcOrder> rcOrders = new ArrayList<RcOrder>();
        // List<RcMerchantWallet> merchantWallets = new
        // ArrayList<RcMerchantWallet>();
//		RcBuyerTrade rcMemberTrade = new RcBuyerTrade();
        BigDecimal orerPayPriceTotal = null;
        try {
            orerPayPriceTotal = new BigDecimal(json.getString("orerPayPrices"));
        } catch (Exception e) {
        }

        // 互斥乐观锁
        //if (lockService.lockMemberAndProduct(redisTemplate, memberId, productIds)) {
//			try {
//				List<RcProdStorage> rcProdStorages = new ArrayList<RcProdStorage>();
				/*
				 * RcMemberWallet RrcMemberWallet =
				 * iMemberWalletService.selectByMemberId(memberId);
				 */

        if (buyer.getWalletAmount() == null || buyer.getWalletAmount().compareTo(orerPayPriceTotal) < 0) {
            result.setCode(ErrorValue.INSUFFICIENT_AMOUNT.getStr());
            result.setResult("Balance is not enough.");
            return result;
        } else {
//					List<Integer> listProdIds = new ArrayList<Integer>();
//					Map<Object, Integer> storageMap = new HashMap<Object, Integer>();
//					Map<Object, RcOrderProdVo> skuProd = new HashMap<Object, RcOrderProdVo>();
//            for (Object order : orders) {
//                JSONObject jo = (JSONObject) order;
//						Long orderId = jo.getLong("orderId");
                //Long orderId = object.getOrderId();
//                orderService.completePayment(jo.getLong("orderId"), PaymentInterfaceEnum.WALLET.getValue(), PaymentTypeEnum.BALANCE.getValue());
//            }
//						RcOrder rcOrder = orderService.findOrderByOrderId(orderId);
//						List<RcOrderProdVo> rcOrderProdVos = new ArrayList<RcOrderProdVo>();// iOrderProdService.findOrderProdVosByOrderId(orderId);
//						RcOrderConsignee rcConsignee = orderConsigneeService
//								.findOrderConsignee(object.getOrderId());
//						RcMerchant rcMerchant = merchantService.findMerchantByMerchantId(rcOrder.getMerchantId());
//						RcMember rcMemberMerchantDb = memberService.findMemberByMemberId(rcMerchant.getMemberId());
//						for (RcOrderProdVo rcOrderProdVo : rcOrderProdVos) {
//							RcProdStorage rcProdStorage = null;
//
//							if (rcOrderProdVo.getProdSkuId() != null) {
//								/*
//								 * listProdIds.add(rcOrderProdVo.getProdSkuId())
//								 * ;
//								 */
//							//	storageMap.put(rcOrderProdVo.getProdSkuId(), rcOrderProdVo.getProdBuyAmt());
//							//	rcProdStorage = prodStorageService.findProdStorageByProdSkuId(rcOrderProdVo.getProdSkuId());
//							} else {
//
//								storageMap.put(rcOrderProdVo.getProdId(), rcOrderProdVo.getProdBuyAmt());
//								rcProdStorage = prodStorageService.findProdStorageByProdId(
//										rcOrderProdVo.getProdId());
//							}
//							listProdIds.add(rcOrderProdVo.getProdId());
//
//							if (rcProdStorage == null) {
//								result.setResult("Stock shortage.");
//								result.setCode(ErrorValue.UNDER_STOCK.getStr());
//								return result;
//							}
//							rcProdStorage.setStorage(rcProdStorage.getStorage() - rcOrderProdVo.getProdBuyAmt());
//							if (rcProdStorage.getStorage() < 0) {
//								result.setResult("Stock shortage.");
//								result.setCode(ErrorValue.UNDER_STOCK.getStr());
//								return result;
//							}
//							RcProd rcProd = prodService.findProductById(rcOrderProdVo.getProdId());
//							if (rcProd.getDelFlag() == YesOrNoEnum.YES.getValue()) {
//								result.setCode(ErrorValue.GOODS_DELETED.getStr());
//								/* result.setCode("-6"); */
//								return result;
//							}
//							if (rcProd.getStatus() == RcProdStatusEnum.SOLDOUT.getValue()) {
//								result.setCode(ErrorValue.GOODS_SHELF.getStr());
//								/* result.setCode("-5"); */
//								return result;
//							}
//							/**
//							 * 库存不足邮件发送
//							 */
//							if (rcProdStorage.getWarningStatus() == YesOrNoEnum.NO.getValue()
//									&& rcProdStorage.getStorage() <= rcProd.getStockWarning()) {
//								// 构建库存不足邮件
//								MailVo mailVo1 = new MailVo();
//								/* sendSkuId.add(map2.get("skuId")); */
//								mailVo1.setHttpServletRequest(httpServletRequest);
//								mailVo1.setTo(rcMemberMerchantDb.getEmail());
//								mailVo1.setVelocity("inventory.vm");
//								mailVo1.setSubject("one stock warning");
//								/*
//								 * map2.put("prodStorage",
//								 * Integer.valueOf(map2.get("prodStorage").
//								 * toString()) -
//								 * Integer.valueOf(storageMap.get(map2.get(
//								 * "skuId")).toString()));
//								 * mailVo1.getContext().put("prod",map2 );
//								 */
//								mailVos.add(mailVo1);
//							}
//
//							rcProdStorages.add(rcProdStorage);
//
//							List<RcProdSkuPropInfo> prodSkuPropInfos =null;// prodSkuPropInfoService.findProdSkuPropInfoByProdSkuId(rcOrderProdVo.getProdSkuId());
//							ArrayList<String> prodProp = new ArrayList<String>();
//							for (RcProdSkuPropInfo rcProdSkuPropInfo : prodSkuPropInfos) {
//								prodProp.add(rcProdSkuPropInfo.getProdPropVal());
//							}
//							rcOrderProdVo.setProdProp(prodProp);
//							if (rcOrderProdVo.getProdSkuId() != null) {
//								skuProd.put(rcOrderProdVo.getProdSkuId(), rcOrderProdVo);
//							} else {
//								skuProd.put(rcOrderProdVo.getProdId(), rcOrderProdVo);
//							}
//						}
//
//						if (object.getBuyerStatus() != RcOrderBuyerStatusEnum.UNPAID.getValue()) {
//							result.setCode(ErrorValue.ORDER_HAS_BEEN_PAID.getStr());
//							result.setResult("order has been payMent");
//							return result;
//						}
//
//						/*
//						 * if (object.getIsDelivery() == 0)
//						 * object.setOrderStatus(2); else
//						 */
//						object.setBuyerStatus(RcOrderBuyerStatusEnum.UNRECEIVED.getValue());
//						RcOrderOperation rcOrderOperation = new RcOrderOperation();
//						rcOrderOperation.setOperationTime(new Date());
//						rcOrderOperation.setOperationType(RcOrderOperationTypeEnum.PAY.getValue());
//						/* object.setOrderPayTime(new Date()); */
//						RcMember rcMemberInfo = memberService.findMemberByMemberId(rcMerchant.getMemberId());
//
//						/*
//						 * RcMerchantWallet rcMerchantWallet = iMerchantService
//						 * .getMerchantWallet(object.getMerchantId());
//						 * rcMerchantWallet.setWalletVal((rcMerchantWallet
//						 * .getWalletVal() == null ? new BigDecimal(0) :
//						 * rcMerchantWallet.getWalletVal())
//						 * .add(object.getOrderPayPrice()));
//						 */
//						/* merchantWallets.add(rcMerchantWallet); */
//						/*
//						 * String merchantName =rcMerchant.getMerchantName();
//						 */
//						rcMemberTrade.setOrderId(orderId);
//						/*
//						 * rcMemberTrade.setProdName(rcMerchant.getName() +
//						 * (rcMemberTrade.getProdName() == null ? "" : "," +
//						 * rcMemberTrade.getProdName()));
//						 */
//						object.setPaymentType(PaymentTypeEnum.BALANCE.getValue());
//						object.setSellerStatus(RcOrderSellerStatusEnum.UNDELIVERED.getValue());
//						object.setBuyerStatus(RcOrderBuyerStatusEnum.UNRECEIVED.getValue());
//						rcOrders.add(object);
//						/*
//						 * RcOrderVo rcOrderVo = new RcOrderVo();
//						 * rcOrderVo.setOrderId(orderId);
//						 */
//						// 构架订单邮件
//						RcOrder rcOrderVo = orderService.findOrderByOrderId(orderId);
//						// 构建邮件
//						MailVo mailVo = new MailVo();
//						mailVo.setHttpServletRequest(httpServletRequest);
//						mailVo.setTo(rcMemberInfo.getEmail());
//						/*
//						 * rcOrderVo.setOrderTimeStr( new
//						 * SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(
//						 * rcOrderVo.getCreateTime()));
//						 */
//						mailVo.getContext().put("rcOrderVo", rcOrderVo);
//						mailVo.getContext().put("products", rcOrderProdVos);
//						mailVo.setVelocity("order.vm");
//						mailVo.setSubject("one order sucess");
//						mailVos.add(mailVo);
        }
        // 批量验证库存和
					/*
					 * List<RcProd> rcProds =
					 * iProdService.findProdByProdIds(listProdIds); if
					 * (CollectionUtils.isNotEmpty(rcProds)) { for (RcProd map :
					 * rcProds) { if
					 * (map.getDelFlag()==YesOrNoEnum.YES.getValue()) {
					 * result.setCode(ErrorValue.GOODS_DELETED.getStr());
					 * result.setCode("-6"); return result; } if
					 * (Integer.valueOf(map.get("prodStorage").toString()) <
					 * Integer
					 * .valueOf(storageMap.get(map.get("prodId")).toString())) {
					 * result.setResult("Stock shortage.");
					 * result.setCode(ErrorValue.UNDER_STOCK.getStr()); return
					 * result; } if (map.getStatus() ==
					 * RcProdStatusEnum.SOLDOUT.getValue()) {
					 * result.setCode(ErrorValue.GOODS_SHELF.getStr());
					 * result.setCode("-5"); return result; } } } else {
					 * result.setResult("Stock shortage.");
					 * result.setCode(ErrorValue.UNDER_STOCK.getStr()); return
					 * result; }
					 */

        // 构建库存不足邮件
					/*
					 * List<Map> maps =
					 * iProdStorageService.selectStockWarningList(listProdIds);
					 */
					/*
					 * for (RcProd map2 : storageMap) {
					 * 
					 * }
					 */
//					rcMember.setWalletAmount(rcMember.getWalletAmount().subtract(orerPayPriceTotal));
//					rcMemberTrade.setTradeId(serialGeneratorService.getSerialKey("tradeIdSerial"));
//					rcMemberTrade.setAmount(orerPayPriceTotal);
//					rcMemberTrade.setType(RcMemberTradeTypeEnum.EXPENSE.getValue());
//					rcMemberTrade.setPaymentType(PaymentTypeEnum.BALANCE.getValue());
//					rcMemberTrade.setPaymentInterface(PaymentInterfaceEnum.WALLET.getValue());
//					rcMemberTrade.setMemberId(memberId);
//					rcMemberTrade.setBalance(rcMember.getWalletAmount());
//				}
				/*
				 * //发送库存邮件 List<Map> maps =
				 * iProdStorageService.selectStockWarningList();
				 * Map<Object,List<Map>> mapMerchant = new HashMap(); List list
				 * = null; String merchantId = null; for (Map map : maps) { if
				 * (map.get("merchantId").equals(merchantId)) { list.add(map); }
				 * else { if(list!=null) mapMerchant.put(map.get("merchantId"),
				 * list); list=new ArrayList(); list.add(map); }
				 * 
				 * } Set<Entry<Object, List<Map>>> set = mapMerchant.entrySet();
				 * for ( Map.Entry object : set) { MailVo mailVo = new MailVo();
				 * mailVo.setSubject("one stock warning"); List<Map>
				 * listValue=(List) object.getValue();
				 * mailVo.setTo(listValue.get(0).get("email").toString());
				 * mailVo.setVelocity("inventory.vm");
				 * mailVo.getContext().put("inventory", object.getValue()); try
				 * { iMailService.sendMail(mailVo); } catch (Exception e) { //
				 * TODO Auto-generated catch block e.printStackTrace(); } }
				 */
//				for (RcProdStorage rcProdStorage2 :  rcProdStorages) {
//					if (sendSkuId.contains(rcProdStorage2.getProdId()))
//						rcProdStorage2.setWarningStatus(YesOrNoEnum.YES.getValue());
//				}
//				orderService.modifyOrders(rcOrders);
//				memberTradeService.addMemberTrade(rcMemberTrade);
//				prodStorageService.addProdStorages(rcProdStorages);
        // iOrderService.updateValletVal(rcOrders, rcMemberTrade,
        // merchantWallets, rcProdStorages);
        // 更新solr
				/*
				 * try { for (RcOrder ro : rcOrders) { List<RcOrderProdVo> list
				 * = iOrderService.selectRcOrderProdVos(ro.getOrderId()); for
				 * (RcOrderProdVo rcOrderProdVo : list) {
				 * iSearchService.updateIndexForOrderSuccess(rcOrderProdVo.
				 * getProdId()); } } } catch (Exception e) { }
				 */
//				for (MailVo mailVo : mailVos) {
//					try {
//						mailService.sendMail(mailVo);
//					} catch (Exception e) {
//						System.out.println();
////						logger.error("error mail", e);
//					}
//				}

        return result;

//			} finally {
////				lockService.unlock(redisTemplate, memberId);
////				lockService.unLockProduct(redisTemplate, productIds);
//				// iLockService.unLockMerchant(redisTemplate, merchantIds);
//			}

//		} else {
//			result.setCode(ErrorValue.SYSTEM_BUSY.getStr());
//			result.setResult("System is busy.");
//			return result;
//		}
    }

//    @RequestMapping("myorder")
//    @ResponseBody
//    public Result<List<RcOrder>> myorder(String data, String timestamp, String nonceStr, String product,
//                                         String signature, HttpServletRequest request, HttpServletResponse response) {
//        Result<List<RcOrder>> result = new Result<List<RcOrder>>();
//        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
//        if (json == null) {
//            result.setCode("-1");
//            result.setResult("token验证失败！");
//        }
//        int buyerId = json.getInt("memberId");
//        Integer buyerStatus = null;
//        try {
//            buyerStatus = json.getInt("status");
//        } catch (Exception e) {
//        }
//        if (buyerStatus == 13) {
//            buyerStatus = 5;
//        } else if (buyerStatus == 3) {
//            buyerStatus = 2;
//        } else if (buyerStatus == 4) {
//            buyerStatus = 6;
//        } else if (buyerStatus == 5) {
//            buyerStatus = null;
//        }
//        Integer page = null;
//        try {
//            page = json.getInt("currentPage");
//        } catch (Exception e) {
//            page = 1;
//        }
//        Page pager = new Page();
//        // 初始化时第一页
//        pager.setPagination(true);
//        pager.setPageSize(5);
//        pager.setCurrentPage(page);
//        List<RcOrder> orders = orderService.findPagedBuyerOrders(buyerId, buyerStatus, pager);
//        result.setData(orders);
//        return result;
//    }
//    @RequestMapping("myorderdetail")
//    @ResponseBody
//    public Result<RcOrder> myorderdetail(String data, String timestamp, String nonceStr, String product,
//                                         String signature, HttpServletRequest request, HttpServletResponse response) {
//        Result<RcOrder> result = new Result<>();
//        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
//        if (json == null) {
//            result.setCode("-1");
//            result.setResult("token验证失败！");
//        }
//        long orderId = json.getLong("orderId");
//        RcOrder rcOrder = orderService.findSellerOrderById(orderId);
//       /* List<RcOrder> orders = orderService.findPagedBuyerOrders(buyerId, buyerStatus, pager);*/
//        result.setData(rcOrder);
//        return result;
//    }
	/*
	 * @RequestMapping("orderProductList")
	 * 
	 * @ResponseBody public DataGrid<RcOrderProdVo> orderProductList(Integer
	 * page, Integer rows, Long orderId) {
	 * 
	 * Page pager = new Page(); // 初始化时第一页 pager.setPagination(true);
	 * pager.setPageSize(rows); pager.setCurrentPage(page);
	 * PageContext.setPage(pager);
	 * 
	 * List<RcOrderProdVo> rcOrderVoss =
	 * iOrderService.findOrderProdVOsByOrderId(orderId);
	 * 
	 * for (RcOrderProdVo rcOrderProdVo : rcOrderVoss) { List<RcProdSkuPropInfo>
	 * prodSkuPropInfos = iProdSkuPropInfoService
	 * .selectProdSkuPropInfo(rcOrderProdVo.getProdSkuId()); ArrayList<String>
	 * prodProp = new ArrayList<String>(); for (RcProdSkuPropInfo
	 * rcProdSkuPropInfo : prodSkuPropInfos) {
	 * prodProp.add(rcProdSkuPropInfo.getProdPropVal()); }
	 * rcOrderProdVo.setProdProp(prodProp);
	 * 
	 * }
	 * 
	 * DataGrid<RcOrderProdVo> rDataGrid = new DataGrid<RcOrderProdVo>();
	 * rDataGrid.setRows(rcOrderVoss); return rDataGrid; }
	 */

    /**
     * 取消订单
     *
     * @param data
     * @param timestamp
     * @param nonceStr
     * @param product
     * @param signature
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("cancelOrder")
    @ResponseBody
    public Result<String> cancelOrder(String data, String timestamp, String nonceStr, String product,
                                      String signature, HttpServletRequest request, HttpServletResponse response) {
        Result<String> result = new Result<String>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
        }
        long orderId = json.getLong("orderId");
        int buyerId = json.getInt("memberId");
        RcOrder order = orderService.findOrderById(orderId);
        order.setBuyerStatus(OrderBuyerStatusEnum.CANCELED.getValue());
        order.setSellerStatus(OrderSellerStatusEnum.CANCELED.getValue());
        orderService.modifyOrder(order);

        RcOrderCoupon orderCoupon = orderCouponService.findOrderCouponByOrderId(order.getOrderId());
        if (orderCoupon != null) {
            buyerCouponService.modifyBuyerCouponStatus(buyerId, orderCoupon.getCouponId(), BuyerCouponStatusEnum.UNUSED.getValue());
        }

        RcOrderOperation orderOperation = new RcOrderOperation();
        orderOperation.setOrderId(orderId);
        orderOperation.setOperationType(OrderOperationTypeEnum.CANCEL.getValue());
        orderOperation.setMemberId(buyerId);
        orderOperationService.addOrderOperation(orderOperation);

//		List<RcOrderProd> orderProds = orderProdService.findOrderProdsByOrderId(orderId);
		/*for(RcOrderProd orderProd : orderProds) {
			RcProdStorage prodStorage = prodStorageService.findProdStorage(orderProd.getProdId(), orderProd.getProdSkuId());
			prodStorage.setStorage(prodStorage.getStorage() + orderProd.getQuantity());
			prodStorageService.modifyProdStorages(prodStorage);
		}*/

        return result;
    }

    /**
     * 评论订单
     *
     * @param data
     * @param timestamp
     * @param nonceStr
     * @param product
     * @param signature
     * @param request
     * @param response
     * @return
     * @throws UnsupportedEncodingException
     */
//    @RequestMapping("addevaluation")
//    @ResponseBody
//    public Result<String> addevaluation(String data, String timestamp, String nonceStr, String product,
//                                        String signature, HttpServletRequest request, HttpServletResponse response)
//            throws UnsupportedEncodingException {
//        Result<String> result = new Result<String>();
//        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
//        if (json == null) {
//            result.setCode("-1");
//            result.setResult("token验证失败！");
//            return result;
//        }
//        long orderId = json.getLong("orderId");
//        RcOrder rcOrder = orderService.findOrderById(orderId);
////		RcMerchant rcMerchant=merchantService.findMerchantByMerchantId(rcOrder.getMerchantId());
////	List<RcOrderProd> rcOrderProds=orderProdService.findOrderProdsByOrderId(orderId);
//        int memberId = json.getInt("memberId");
//        int deliveryScore = json.getInt("deliveryScore");
//        int serviceScore = json.getInt("serviceScore");
//        RcOrderEvaluation orderEvaluation = new RcOrderEvaluation();
//        orderEvaluation.setOrderId(orderId);
//        orderEvaluation.setDeliveryScore(deliveryScore);
//        orderEvaluation.setServiceScore(serviceScore);
//        orderEvaluation.setMerchantId(rcOrder.getMerchantId());
//        orderEvaluation.setVendorId(rcOrder.getVendorId());
//        JSONArray evaluationArray = json.getJSONArray("products");
//        for (Object object : evaluationArray) {
//            JSONObject evaluationObj = (JSONObject) object;
//            long orderProdId = evaluationObj.getLong("orderProdId");
//            int prodId = evaluationObj.getInt("prodId");
//            String content = evaluationObj.getString("content");
//            String prodSkuId = evaluationObj.getString("prodSkuId");
//            ;
//            int score = evaluationObj.getInt("score");
//            int isAnoy = evaluationObj.getInt("isAnoy");
//            RcOrderProdEvaluation evaluation = new RcOrderProdEvaluation();
//            evaluation.setCreateTime(new Date());
//            evaluation.setMemberId(memberId);
//            evaluation.setContent(content);
//            evaluation.setProdSkuId(prodSkuId);
//            evaluation.setIsAnoy((byte) isAnoy);
//            evaluation.setOrderId(orderId);
//            evaluation.setProdId(prodId);
//            evaluation.setScore(score);
//            evaluation.setVendorId(rcOrder.getVendorId());
//            evaluation.setMerchantId(rcOrder.getMerchantId());
//            evaluation.setOrderProdId(orderProdId);
//            orderProdEvaluationService.addOrderProdEvaluation(evaluation);
//        }
//        orderEvaluationService.addOrderEvaluation(orderEvaluation);
//
//        RcOrderOperation orderOperation = new RcOrderOperation();
//        orderOperation.setOrderId(orderId);
//        orderOperation.setOperationType(OrderOperationTypeEnum.EVALUATE.getValue());
//        orderOperation.setMemberId(memberId);
//        orderOperationService.addOrderOperation(orderOperation);
//
//        RcOrder order = orderService.findOrderById(orderId);
//        order.setBuyerStatus(OrderBuyerStatusEnum.FINISHED.getValue());
//        order.setSellerStatus(OrderSellerStatusEnum.FINISHED.getValue());
//        orderService.modifyOrder(order);
//
//        return result;
//    }

    /**
     * 确认收货
     *
     * @param data
     * @param timestamp
     * @param nonceStr
     * @param product
     * @param signature
     * @param request
     * @param response
     * @return
     */
//    @RequestMapping("deliveryConfirmation")
//    @ResponseBody
//    public Result<String> deliveryConfirmation(String data, String timestamp, String nonceStr, String product,
//                                               String signature, HttpServletRequest request, HttpServletResponse response) {
//        Result<String> result = new Result<String>();
//        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
//        if (json == null) {
//            result.setCode("-1");
//            result.setResult("token验证失败！");
//        }
//        long orderId = json.getLong("orderId");
//        RcOrder order = orderService.findOrderById(orderId);
//        order.setBuyerStatus(OrderBuyerStatusEnum.UNEVALUATED.getValue());
//        order.setSellerStatus(OrderSellerStatusEnum.RECEIVED.getValue());
//
//        RcOrderOperation orderOperation = new RcOrderOperation();
//        orderOperation.setOperationType(OrderOperationTypeEnum.RECEIVE.getValue());
//        orderOperation.setOrderId(orderId);
//        orderOperation.setMemberId(order.getMemberId());
//        orderOperationService.addOrderOperation(orderOperation);
//
//        orderService.modifyOrder(order);
//
//        // 更新solr
////		List<RcOrderProd> orderProds = orderProdService.findOrderProdsByOrderId(orderId); 
////		for(RcOrderProd orderProd : orderProds) {
////			searchService.updateIndexForOrderSuccess(orderProd.getProdId()); 
////		}
//
//        return result;
//    }

    /**
     * 发货
     *
     * @param orderId
     * @return
     */
	/*
	 * @RequestMapping("delivery")
	 * 
	 * @ResponseBody public Result delivery(Long orderId) {
	 * Result<List<RcOrderVo>> result = new Result<List<RcOrderVo>>(); RcOrder
	 * rcOrder = iOrderService.findOrderByOrderId(orderId);
	 * iOrderOperationService.addOrderOperationByOrderAndOrderOperationType(
	 * RcOrderOperationTypeEnum.DELIVER, rcOrder);
	 * iOrderService.modifyOrder(rcOrder); return result; }
	 */

    /**
     * 发货
     *
     * @return
     */
//    @RequestMapping("deliveryForm")
//    @ResponseBody
//    public Result<String> deliveryForm(String orderIds, RcOrderDelivery rcOrderDeliveryParam) {
//        Result<String> result = new Result<String>();
//        String[] orderIdds = StringUtils.split(orderIds, ",,,");
//        for (String orderId : orderIdds) {
//            RcOrder order = orderService.findOrderById(Long.valueOf(orderId));
//            if (order.getSellerStatus() != OrderSellerStatusEnum.UNDELIVERED.getValue() && order.getSellerStatus() != OrderSellerStatusEnum.UNPICKEDUP.getValue()) {
//                continue;
//            }
//
//            //	RcOrderOperation rcOrderOperationDb = iOrderOperationService.findOrderOperation(rcOrderOperation);
//            if (order.getSellerStatus() == OrderSellerStatusEnum.UNDELIVERED.getValue()) {
//
//                order.setBuyerStatus(OrderBuyerStatusEnum.UNRECEIVED.getValue());
//                order.setSellerStatus(OrderSellerStatusEnum.DELIVERED.getValue());
//                // 更新订单状态
//                orderService.modifyOrder(order);
//                // 保存操作方式
//                RcOrderOperation rcOrderOperation = new RcOrderOperation();
//                rcOrderOperation.setOrderId(order.getOrderId());
//                rcOrderOperation.setOperationType(OrderOperationTypeEnum.DELIVER.getValue());
//                rcOrderOperation.setMemberId(order.getMemberId());
//                orderOperationService.addOrderOperation(rcOrderOperation);
//                // 保存收货信息
//                rcOrderDeliveryParam.setDeliveryTime(new Date());
//                rcOrderDeliveryParam.setOrderId(order.getOrderId());
//                orderDeliveryService.addOrderDelivery(rcOrderDeliveryParam);
//                // iOrderService.saveOrderDelivery(rcOrder, rcOrderDelivery);
//            }
//        }
//        result.setResult("Done.");
//        return result;
//    }

	/**
	 * 退款
	 * 
	 * @return
	 */
//	@RequestMapping("refundForm")
//	@ResponseBody
//	public Result<String> refundForm(String orderId, String refundReason) {
//		Result<String> result = new Result<String>();
//		String[] orderIdArray = orderId.split(",,,");
//		for (String orderIdStr : orderIdArray) {
//			RcOrder order = orderService.findOrderById(Long.valueOf(orderIdStr));
////			RcBuyer buyer = buyerService.findBuyerById(order.getMemberId());
//			if (order.getSellerStatus() != OrderSellerStatusEnum.UNDELIVERED.getValue() && order.getSellerStatus() != OrderSellerStatusEnum.UNPICKEDUP.getValue()) {
//				continue;
//			}
////			if (lockService.lock(redisTemplate, rcOrderDb.getMemberId())) {
////				try {
//					/* rcOrderDb.setRefundTime(new Date()); */
//					RcOrderOperation orderOperation = new RcOrderOperation();
//					orderOperation.setOrderId(order.getOrderId());
//					orderOperation.setOperationType(OrderOperationTypeEnum.REJECT.getValue());
//					orderOperation.setMemberId(order.getMemberId());
//					orderOperation.setOperationMemo(refundReason);
//					orderOperationService.addOrderOperation(orderOperation);
//					order.setBuyerStatus(OrderBuyerStatusEnum.REJECTED.getValue());
//					order.setSellerStatus(OrderSellerStatusEnum.REJECTED.getValue());
//					/*
//					 * String[] refundReasonss =
//					 * StringUtils.split(refundReasons, ","); if
//					 * (refundReasonss.length == 1)
//					 * rcOrderDb.setRefundReason(rcOrder.getRefundReason());
//					 * elses rcOrderDb.setRefundReason(refundReasons[1]);
//					 */
//					/*
//					 * RcMemberWallet rcMemberWallet =
//					 * iMemberWalletService.selectByMemberId(rcOrderDb.
//					 * getMemberId());
//					 * rcMemberWallet.setWalletVal(rcMemberWallet.getWalletVal()
//					 * .add(rcOrderDb.getOrderPayPrice()));
//					 */
////					buyer.setWalletAmount(buyer.getWalletAmount().add(order.getPayPrice()));
////					buyerService.modifyBuyer(buyer);
////					RcBuyerTrade buyerTrade = new RcBuyerTrade();
////					/*
////					 * rcMemberTrade.setTradeId(serialGeneratorService.
////					 * getSerialKey("tradeIdSerial"));
////					 */
////					buyerTrade.setAmount(order.getPayPrice());
////					buyerTrade.setBalance(buyer.getWalletAmount());
////					buyerTrade.setType(BuyerTradeTypeEnum.REFUND.getValue());
////					buyerTrade.setPaymentType(PaymentTypeEnum.BALANCE.getValue());
////					buyerTrade.setPaymentInterface(PaymentInterfaceEnum.WALLET.getValue());
////					buyerTrade.setOrderId(order.getOrderId());
////					buyerTrade.setBuyerId(order.getMemberId());
////					buyerTradeService.addBuyerTrade(buyerTrade);
//					/*
//					 * rcMemberTrade.setBalance(rcMemberWallet.getWalletVal());
//					 */
//            // 退回卡券
//            RcOrderCoupon orderCoupon = orderCouponService.findOrderCouponByOrderId(order.getOrderId());
//
//					if (orderCoupon != null) {
//						buyerCouponService.modifyBuyerCouponStatus(order.getMemberId(), orderCoupon.getCouponId(), BuyerCouponStatusEnum.UNUSED.getValue());
//					}
//					orderService.modifyOrder(order);
//					
//					List<RcOrderProd> orderProds = orderProdService.findOrderProdsByOrderId(order.getOrderId());
//					for(RcOrderProd orderProd : orderProds) {
//						RcProd prod = prodService.findProdById(orderProd.getProdId());
//						if(!orderProd.getProdSkuId().equals(String.valueOf(orderProd.getProdId()))) {
//							RcProdSku prodSku = prodSkuService.findProdSkuByProdSkuId(orderProd.getProdSkuId());
//							//prodSku.setInventory(prodSku.getInventory() + orderProd.getQuantity());
//							prodSkuService.modifyProdSku(prodSku);
//						}
//						//prod.setInventory(prod.getInventory() + orderProd.getQuantity());
//						prod.setSoldNum(prod.getSoldNum() - orderProd.getQuantity());
//						prodService.modifyProd(prod);
//					}
//
//					/*
//					 * RcMerchant rcMerchant =
//					 * iMerchantService.getMerchantById(rcOrderDb.getMerchantId(
//					 * ));
//					 * rcMemberTrade.setProdName(rcMerchant.getMerchantName() +
//					 * (rcMemberTrade.getProdName() == null ? "" : "," +
//					 * rcMemberTrade.getProdName()));
//					 * iOrderService.updateOrderAndWallet(rcOrderDb,
//					 * rcMemberWallet, rcMemberTrade, rcCouponMember);
//					 */
////					return result;
////				} finally {
////					lockService.unlock(redisTemplate, rcOrderDb.getMemberId());
////				}
////
////			} else {
////				result.setCode(ErrorValue.SYSTEM_BUSY.getStr());
////				result.setResult("System is busy.");
////				return result;
////			}
//        }
//        result.setResult("Done.");
//        return result;
//    }

    @RequestMapping("orderReceiving")
    @ResponseBody
    public Result<List<RcOrderVo>> orderReceiving(long orderId,HttpServletRequest httpServletRequest) {
        Result<List<RcOrderVo>> result = new Result<List<RcOrderVo>>();
        Integer memberId= (Integer) httpServletRequest.getSession().getAttribute(Constants.MERCHANT_USER_ID_IN_SESSION_KEY);
        RcOrder order = orderService.findOrderById(orderId);
        order.setBuyerStatus(OrderBuyerStatusEnum.UNCONFIRMED.getValue());
        order.setSellerStatus(OrderSellerStatusEnum.GRABBED.getValue());
       // order.set
        // 订单操作流畅
        RcOrderOperation orderOperation = new RcOrderOperation();
        orderOperation.setOrderId(order.getOrderId());
        orderOperation.setOperationType(OrderOperationTypeEnum.GRAB.getValue());
        orderOperation.setMemberId(memberId);
        orderOperationService.addOrderOperation(orderOperation);
        orderService.modifyOrder(order);
        return result;
    }
    /**
     * 取消订单
     *
     * @param orderId
     */
    @RequestMapping("cancel")
    @ResponseBody
    public Result<List<RcOrderVo>> cancel(long orderId,String refundReason) {
        Result<List<RcOrderVo>> result = new Result<List<RcOrderVo>>();
        RcOrder order = orderService.findOrderById(orderId);
        order.setBuyerStatus(OrderBuyerStatusEnum.CANCELED.getValue());
        order.setSellerStatus(OrderSellerStatusEnum.CANCELED.getValue());
        // 订单操作流畅
        RcOrderOperation orderOperation = new RcOrderOperation();
        orderOperation.setOrderId(order.getOrderId());
        orderOperation.setOperationMemo(refundReason);
        orderOperation.setOperationType(OrderOperationTypeEnum.CANCEL.getValue());
        orderOperation.setMemberId(order.getMemberId());
        orderOperationService.addOrderOperation(orderOperation);
        orderService.modifyOrder(order);
        return result;
    }

//	@RequestMapping("pickup")
//	@ResponseBody
//	public Result<Object> pickup(RcOrder rcOrderParam) {
//		Result<Object> result = new Result<Object>();
//		RcOrder rcOrder = orderService.findOrderByOrderId(rcOrderParam.getOrderId());
//		Set<Integer> merchantIds = new HashSet<Integer>();
//		merchantIds.add(rcOrder.getMerchantId());
//		if (lockService.lockMerchant(redisTemplate, rcOrder.getMerchantId())) {
//			try {
//				if (!rcOrder.getPickupCode().endsWith(rcOrderParam.getPickupCode())) {
//					result.setCode(ErrorValue.DELIVERY_ERROR.getStr());
//					result.setResult("order code is error.");
//					return result;
//				}
//				rcOrder.setBuyerStatus(RcOrderBuyerStatusEnum.UNEVALUATED.getValue());
//				rcOrder.setBuyerStatus(RcOrderSellerStatusEnum.PICKEDUP.getValue());
//				
//				RcOrderOperation orderOperation = new RcOrderOperation();
//				orderOperation.setOrderId(rcOrder.getOrderId());
//				orderOperation.setOperationType(RcOrderOperationTypeEnum.PICKUP_CONFIRM.getValue());
//				orderOperation.setMemberId(rcOrder.getMemberId());
//				orderOperationService.addOrderOperation(orderOperation);
//				
//				/* rcOrder.setOrderReceiveTime(new Date()); */
//				result.setResult("Done.");
//				/*
//				 * RcMerchant rcMerchant =
//				 * iMerchantService.findMerchantByMerchantId(rcOrder.
//				 * getMerchantId());
//				 */
//				/*
//				 * if (rcMerchant == null) { rcMerchantWallet = new
//				 * RcMerchantWallet();
//				 * rcMerchantWallet.setMerchantId(rcOrder.getMerchantId());
//				 * rcMerchantWallet.setWalletVal(rcOrder.getOrderPayPrice()); }
//				 * else {
//				 */
//				/*
//				 * rcMerchantWallet.setWalletVal(rcMerchant.get).add(rcOrder.
//				 * getOrderPayPrice()));
//				 */
//				/* } */
//				orderService.modifyOrder(rcOrder);
//				// iOrderService.updateOrderAndMerchantWallet(rcOrder,
//				// rcMerchantWallet);
//				// 更新solr
//				/*
//				 * List<RcOrderProdVo> list =
//				 * iOrderService.selectRcOrderProdVos(rcOrder.getOrderId()); for
//				 * (RcOrderProdVo rcOrderProdVo : list) {
//				 * iSearchService.updateIndexForOrderSuccess(rcOrderProdVo.
//				 * getProdId()); }
//				 */
//				return result;
//			} finally {
//				lockService.unLockMerchant(redisTemplate, merchantIds);
//			}
//		} else {
//			result.setCode(ErrorValue.SYSTEM_BUSY.getStr());
//			result.setResult("System is busy.");
//			return result;
//		}
//	}

    /**
     * 商家备注
     *
     * @param param
     * @return
     */
    @RequestMapping("orderMerchantMemo")
    @ResponseBody
    public Result<Object> orderMerchantMemo(RcOrder param) {
        Result<Object> result = new Result<Object>();
        RcOrder order = orderService.findOrderById(param.getOrderId());
        order.setSellerMemo(param.getSellerMemo());
        result.setResult("Done.");
        orderService.modifyOrder(order);
        return result;
    }

    public static String exec(String str) {
        int i = str.length();
        while (i > 4) {
            str = str.substring(0, i - 4) + "-" + str.substring(i - 4);
            i -= 4;
        }

        return str;
    }


    /**
     * 测试邮件接口
     *
     * @param httpServletRequest
     * @throws GeneralSecurityException
     */
/*	@RequestMapping("test") 
	public void test(){
	mailService.sendMail(new MailVo());
	}*/
	/*
	 * @RequestMapping("test") public void test(HttpServletRequest
	 * httpServletRequest) throws GeneralSecurityException { // 收件人电子邮箱 String
	 * to = "1051477653@qq.com"; // 发件人电子邮箱 String from = "1051477653@qq.com";
	 * // 指定发送邮件的主机为 smtp.qq.com String host = "smtp.qq.com"; // QQ 邮件服务器 //
	 * 获取系统属性 Properties properties = System.getProperties();
	 * MailSSLSocketFactory sf = new MailSSLSocketFactory();
	 * sf.setTrustAllHosts(true); properties.put("mail.smtp.ssl.enable",
	 * "true"); properties.put("mail.smtp.ssl.socketFactory", sf); // 设置邮件服务器
	 * properties.setProperty("mail.smtp.host", host);
	 * properties.put("mail.smtp.auth", "true"); // 获取默认session对象 Session
	 * session = Session.getInstance(properties, new Authenticator() { public
	 * PasswordAuthentication getPasswordAuthentication() { return new
	 * PasswordAuthentication("1051477653@qq.com", "wadpwrohigxhbdcd"); //
	 * 发件人邮件用户名、密码 } }); try { // 创建默认的 MimeMessage 对象 MimeMessage message = new
	 * MimeMessage(session); // Set From: 头部头字段 message.setFrom(new
	 * InternetAddress(from));
	 * 
	 * // Set To: 头部头字段 message.addRecipient(Message.RecipientType.TO, new
	 * InternetAddress(to));
	 * 
	 * // Set Subject: 头部头字段 message.setSubject("这是一封测试邮件！");
	 * 
	 * // 设置消息体 String realPath =
	 * httpServletRequest.getSession().getServletContext().getRealPath("/");
	 * Properties p = new Properties();
	 * p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, realPath +
	 * "WEB-INF\\views\\velocity\\template"); Velocity.init(p); VelocityContext
	 * context = new VelocityContext(); context.put("root", "http://" +
	 * httpServletRequest.getServerName() +
	 * httpServletRequest.getContextPath()); StringWriter writer = new
	 * StringWriter(); org.apache.velocity.Template template =
	 * Velocity.getTemplate("order.vm", "utf-8"); template.merge(context,
	 * writer); String s = writer.toString(); message.setContent(s,
	 * "text/html"); // 发送消息 Transport.send(message);
	 * System.out.println("Sent message successfully"); } catch
	 * (MessagingException mex) { logger.error("emailError", mex);
	 * 
	 * } }
	 */
}

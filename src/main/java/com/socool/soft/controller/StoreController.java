package com.socool.soft.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.socool.soft.bo.RcBuyer;
import com.socool.soft.bo.RcCoupon;
import com.socool.soft.bo.RcMerchant;
import com.socool.soft.bo.RcOrder;
import com.socool.soft.bo.RcOrderFee;
import com.socool.soft.bo.RcOrderOperation;
import com.socool.soft.bo.RcOrderProd;
import com.socool.soft.bo.RcOrderProdSkuPropInfo;
import com.socool.soft.bo.RcProd;
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
import com.socool.soft.constant.PropertyConstants;
import com.socool.soft.exception.ErrorValue;
import com.socool.soft.exception.SystemException;
import com.socool.soft.service.IBuyerService;
import com.socool.soft.service.ICouponService;
import com.socool.soft.service.IMerchantService;
import com.socool.soft.service.IOrderFeeService;
import com.socool.soft.service.IOrderOperationService;
import com.socool.soft.service.IOrderProdService;
import com.socool.soft.service.IOrderProdSkuPropInfoService;
import com.socool.soft.service.IOrderService;
import com.socool.soft.service.IProdService;
import com.socool.soft.service.IProdSkuPropInfoService;
import com.socool.soft.service.IProdSkuService;
import com.socool.soft.vo.Result;

import net.sf.json.JSONObject;

@RequestMapping(value = "store")
@Controller
public class StoreController extends BaseController {
    @Autowired
    private IBuyerService buyerService;
    @Autowired
    private IMerchantService merchantService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private IOrderFeeService orderFeeService;
    @Autowired
    private IOrderOperationService orderOperationService;
    @Autowired
    private PropertyConstants propertyConstants;
    @Autowired
    private IProdService prodService;
    @Autowired
    private IProdSkuService prodSkuService;
    @Autowired
    private IProdSkuPropInfoService prodSkuPropInfoService;
    @Autowired
    private IOrderProdService orderProdService;
    @Autowired
    private IOrderProdSkuPropInfoService orderProdSkuPropInfoService;
    @Autowired
    private ICouponService couponService;


    @RequestMapping(value = "addOrder")
    @ResponseBody
    public Result<RcOrder> addOrder(String data, String timestamp, String nonceStr, String product, String signature,
                                    HttpServletRequest httpServletRequest, HttpServletResponse response) throws SystemException {
        Result<RcOrder> result = new Result<>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
        }

        RcOrder order= (RcOrder) JSONObject.toBean(json,RcOrder.class);
        RcBuyer buyer = null;
        if(StringUtils.isNotBlank(order.getMember().getMobile())) {
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
        order.setMemberId(buyer == null ? 0 :buyer.getMemberId());
        order.setProductPrice(new BigDecimal(0));
        order.setBuyerStatus(OrderBuyerStatusEnum.UNCONFIRMED.getValue());
        order.setSellerStatus(OrderSellerStatusEnum.UNCONFIRMED.getValue());
        if(order.getEatType() == OrderEatTypeEnum.TAKE_OUT.getValue()) {
        	order.setTableId(0);
        	order.setSectionName(null);
        	order.setTableNumber(null);
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


  /*  @RequestMapping(value = "user/changeLogo")
    @ResponseBody
    public Result<RcMember> userChangeLogo(String data, String timestamp,
                                           String nonceStr, String product, String signature,
                                           HttpServletRequest httpServletRequest, HttpServletResponse response) {
        Result<RcMember> result = new Result<RcMember>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product,
                signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
        }
        return result;
    }*/

    /**
     * 商户端登录
     *
     * @return
     */
 /*   @RequestMapping(value = "/user/login")
    @ResponseBody
    public Result<RcMerchantUser> userLogin(String data, String timestamp,
                                            String nonceStr, String product, String signature,
                                            HttpServletRequest httpServletRequest, HttpServletResponse response) {
        Result<RcMerchantUser> result = new Result<RcMerchantUser>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product,
                signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
        }
        String username = json.getString("userAccount");
        String password = json.getString("password");
        RcMerchantUser merchantUser = merchantUserService.findMerchantUserByAccount(username);
        if (merchantUser != null) {
            if (DigestUtils.md5Hex(password).equals(merchantUser.getPassword())) {
                return getMerchantUserResult(result, merchantUser);
            } else {
                result.setCode(ErrorValue.ERROR_PASSWORD_OR_USER_NAME
                        .getValue() + "");
                return result;
            }
        } else {
            merchantUser = merchantUserService.findMerchantUserByEmail(username);
            if (merchantUser != null) {
                if (DigestUtils.md5Hex(password).equals(merchantUser.getPassword())) {
                    return getMerchantUserResult(result, merchantUser);
                } else {
                    result.setCode(ErrorValue.ERROR_PASSWORD_OR_USER_NAME
                            .getValue() + "");
                    return result;
                }
            } else {
                merchantUser = merchantUserService.findMerchantUserByMobile(username);
                if (merchantUser != null) {
                    if (DigestUtils.md5Hex(password).equals(merchantUser.getPassword())) {
                        return getMerchantUserResult(result, merchantUser);
                    } else {
                        result.setCode(ErrorValue.ERROR_PASSWORD_OR_USER_NAME
                                .getValue() + "");
                        return result;
                    }
                } else {
                    result.setCode(ErrorValue.ERROR_PASSWORD_OR_USER_NAME
                            .getValue() + "");
                    return result;
                }

            }
        }

    }*/

  /*  private Result<RcMerchantUser> getMerchantUserResult(Result<RcMerchantUser> result,
                                                         RcMerchantUser merchantUser) {
        RcMerchant merchant = merchantService.findMerchantById(merchantUser.getMerchantId());
        merchantUser.setMerchantName(merchant.getName());
        RcCity city = cityService.findCityById(merchant.getCityId());
        merchantUser.setRegion(city.getName() + "," + city.getProvinceName());
        result.setData(merchantUser);
        return result;
    }*/

    ;

    /**
     * 发送验证码
     *
     * @param data
     * @param timestamp
     * @param nonceStr
     * @param product
     * @param signature
     * @param httpServletRequest
     * @param httpServletResponse
     * @return
     * @throws UnsupportedEncodingException
     * @throws JsonProcessingException
     * @throws ClientProtocolException
     * @throws IOException
     * @throws SystemException
     */
 /*   @RequestMapping(value = "user/verification")
    @ResponseBody
    public Result<String> userVerification(String data, String timestamp,
                                           String nonceStr, String product, String signature,
                                           HttpServletRequest httpServletRequest,
                                           HttpServletResponse httpServletResponse)
            throws UnsupportedEncodingException, JsonProcessingException,
            ClientProtocolException, IOException, SystemException {
        Result<String> result = new Result<String>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product,
                signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
            return result;
        }
        String mobile = json.getString("mobile");
        SentSmsRequst sentSmsRequst = new SentSmsRequst();
        List<SendingData> sendingDatas = new ArrayList<SendingData>();
        SendingData sendingData = new SendingData();
        sendingDatas.add(sendingData);
        sentSmsRequst.setSending_data(sendingDatas);
        sendingData.setApikey("4dd671d14ac6581665467af44caf72c7");
        sendingData.setCallbackurl(httpServletRequest.getServerName()
                + httpServletRequest.getContextPath() + "/SMS/callbackurl");
        List<Datapacket> datapackets = new ArrayList<Datapacket>();
        sendingData.setDatapacket(datapackets);
        Datapacket datapacket = new Datapacket();
        Packet packet = new Packet();
        // 随机生成4位验证码
        String randomAlphanumeric = RandomStringUtils.randomNumeric(6);
        packet.setMessage(randomAlphanumeric);
        packet.setNumber(mobile);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        packet.setSendingdatetime(simpleDateFormat.format(new Date()));
        datapacket.setPacket(packet);
        datapackets.add(datapacket);
        result = ismsService.smsreguler(httpServletRequest, sentSmsRequst);
        *//* if (result.getCode().equals("1")) { *//*
        Code code = new Code();
        code.setCode("1111");
        result.setResult(packet.getMessage());
        code.setCreateDate(new Date());
        redisService.hsetObject(Constants.REDIS_MOBILE_VERIFICATION_CODE, mobile, code);

        return result;
    }*/

    /**
     * 商户端重置密码
     *
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws SystemException
     */
  /*  @RequestMapping(value = "user/resetpwd")
    @ResponseBody
    public Result<RcMerchant> userResetpwd(String data, String timestamp,
                                           String nonceStr, String product, String signature,
                                           HttpServletRequest httpServletRequest, HttpServletResponse response)
            throws IOException, ClassNotFoundException, SystemException {
        Result<RcMerchant> result = new Result<RcMerchant>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product,
                signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
        }
        String verificationCode = json.getString("verificationCode");
        String mobile = json.getString("mobile");
        String password = json.getString("password");
        RcMerchantUser merchantUser = merchantUserService.findMerchantUserByMobile(mobile);
        if (merchantUser == null) {
            result.setCode(ErrorValue.ACCOUNT_NOT_EXISTS.getStr());
            result.setResult("account not exists");
            return result;
        }
        // 验证码逻辑
        Code codeRedis = (Code) redisService.hgetObject(Constants.REDIS_MOBILE_VERIFICATION_CODE, merchantUser.getMobile());
        // 验证成功销毁验证码
        redisService.hdelObject(Constants.REDIS_MOBILE_VERIFICATION_CODE, merchantUser.getMobile());
        if (codeRedis != null) {
            if (new Date().getTime() - codeRedis.getCreateDate().getTime() > 3 * 60 * 1000) {
                result.setCode(ErrorValue.VERIFICATION_CODE_FAILURE.getStr());
                result.setResult("Verification code has expired");
                return result;
            } else if (!codeRedis.getCode().equals(verificationCode)) {
                result.setCode(ErrorValue.ERROR_VERIFICATION_CODE.getStr());
                result.setResult("Incorrect verification code");
                return result;
            } else {
                merchantUser.setPassword(DigestUtils.md5Hex(password));
                merchantUserService.modifyMerchantUser(merchantUser);
            }
        } else {
            result.setCode(ErrorValue.ERROR_VERIFICATION_CODE.getStr());
            result.setResult("并未发送验证码！");
            return result;
        }
        return result;

    }*/

    /**
     * 商户端上传头像
     *
     * @param data
     * @param timestamp
     * @param nonceStr
     * @param product
     * @param signature
     * @param request
     * @param response
     * @return
     * @throws IllegalStateException
     * @throws IOException
     */
 /*   @RequestMapping("/user/myhead")
    @ResponseBody
    public String userLogoout(
            String data,
            String timestamp,
            String nonceStr,
            String product,
            String signature,
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = true, value = "image") MultipartFile multipartFile)
            throws IllegalStateException, IOException {
        JSONObject json = parseParam(data, timestamp, nonceStr, product,
                signature);
        if (json == null) {
            return "{\"code\":\"-1\",\"result\":\"token验证失败！\"}";
        }
        int memberId = json.getInt("memberId");
        if (multipartFile != null) {
            String fileName = multipartFile.getOriginalFilename();
            if (StringUtils.isNotBlank(fileName)) {
                if (multipartFile.getSize() > 5 * 1024 * 1024) {
                    return "{\"code\":\""
                            + ErrorValue.PICTURE_BEYOND_LIMIT.getValue()
                            + "\",\"result\":\"图片太大！\"}";
                } else if (multipartFile.getSize() == 0) {
                    return "{\"code\":\""
                            + ErrorValue.NOT_GET_PICTURE_SIZE.getValue()
                            + "\",\"result\":\"未获取到图片尺寸！\"}";
                } else {
                    String fileInfoId = UUID.randomUUID().toString()
                            .replaceAll("-", "");
                    int pos = fileName.lastIndexOf(".");
                    String originalName = fileName.substring(0, pos);
                    String suffixName = fileName.substring(pos + 1,
                            fileName.length());
                    Pattern pattern = Pattern
                            .compile("(png|PNG|GIF|gif|jpg|JPG|bmp|BMP)");
                    Matcher matcher = pattern.matcher(suffixName);
                    if (!matcher.matches()) {
                        return "{\"code\":\""
                                + ErrorValue.PICTURE_FORMAT_ERROR.getValue()
                                + "\",\"result\":\"图片格式不正确（必须为.jpg/.gif/.bmp/.png图片）！\"}";
                    }
                    String realFileName = fileInfoId;
                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH) + 1;
                    int day = cal.get(Calendar.DATE);
                    String relativePath = year + File.separator + month
                            + File.separator + day + File.separator;
                    String dirPath = propertyConstants.systemFileServerRoot
                            + relativePath;
                    File dir = new File(dirPath);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File file = new File(dirPath + realFileName + "."
                            + suffixName);
                    multipartFile.transferTo(file);
                    RcFileInfo fileInfo = new RcFileInfo();
                    fileInfo.setFileInfoId(fileInfoId);
                    fileInfo.setOriginalName(originalName);
                    fileInfo.setRealName(realFileName);
                    fileInfo.setSuffixName(suffixName);
                    fileInfo.setSize(multipartFile.getSize());
                    fileInfo.setRelativePath(relativePath);
                    fileInfo.setMemberId(memberId);
                    fileInfoService.addFileInfo(fileInfo);
                    RcMerchant merchant = merchantService.findMerchantByMerchantUserId(memberId);
                    merchant.setLogoUrl(propertyConstants.systemFileServerUrl + fileInfo.getPath());
                    merchantService.modifyMerchant(merchant);
//					RcMerchantUser merchantUser = merchantUserService
//							.findMerchantUserById(memberId);
//					merchantUser.setImgUrl(propertyConstants.systemFileServerUrl + fileInfo.getPath());
//					merchantUserService.modifyMerchantUser(merchantUser);
                    return "{\"code\":\"1\",\"result\":\"上传成功！\"}";
                }
            }

        }
        return "{\"code\":\"" + ErrorValue.NOT_GET_PICTURE.getValue()
                + "\",\"result\":\"未获取到图片尺寸！\"}";
    }*/

    /**
     * 获取商户信息
     *
     * @param data
     * @param timestamp
     * @param nonceStr
     * @param product
     * @param signature
     * @param httpServletRequest
     * @param response
     * @return
     */
 /*   @RequestMapping(value = "user/getMerchant")
    @ResponseBody
    public Result<RcMerchant> userGetMerchant(String data, String timestamp,
                                              String nonceStr, String product, String signature,
                                              HttpServletRequest httpServletRequest, HttpServletResponse response) {
        JSONObject json = parseParam(data, timestamp, nonceStr, product,
                signature);
        Result<RcMerchant> result = new Result<RcMerchant>();
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
            return result;
        }
        int merchantId = json.getInt("merchantId");
        RcMerchant merchant = merchantService
                .findMerchantById(merchantId);
        VOConversionUtil.Entity2VO(merchant, new String[]{"merchantId",
                "memberId", "deliveryScore", "serviceScore", "productScore",
                "name", "logoUrl"}, null);

        BigDecimal yIncome = merchantService
                .findYIncomeByMerchantId(merchantId);
        BigDecimal tIncome = merchantService
                .findTIncomeByMerchantId(merchantId);
        // Integer tTrans = merchantService.findTTransByMerchantId(merchantId);
        merchant.setyIncome(yIncome);
        merchant.settIncome(tIncome);
        RcMerchantUser merchantUser = merchantUserService.findMerchantSuperAdmin(merchantId);
        merchant.setAccount(merchantUser.getAccount());

        result.setData(merchant);
        return result;
    }*/

   /* @RequestMapping("order/getordernum")
    @ResponseBody
    public Result<OrderNumVO> getOrderNum(String data, String timestamp,
                                          String nonceStr, String product, String signature,
                                          HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Result<OrderNumVO> result = new Result<OrderNumVO>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product,
                signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
        }
        int merchantId = json.getInt("merchantId");
        OrderNumVO orderNum = orderService
                .findSellerOrderNumByMerchantId(merchantId);
        result.setData(orderNum);
        return result;
    }*/

    /**
     * 获取带处理订单
     *
     * @param data
     * @param timestamp
     * @param nonceStr
     * @param product
     * @param signature
     * @param httpServletRequest
     * @param response
     * @return
     */
/*    @RequestMapping(value = "order/myorder")
    @ResponseBody
    public Result<List<RcOrder>> orderMyorder(String data, String timestamp,
                                              String nonceStr, String product, String signature,
                                              HttpServletRequest httpServletRequest, HttpServletResponse response) {
        Result<List<RcOrder>> result = new Result<List<RcOrder>>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product,
                signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
        }
        int merchantId = json.getInt("merchantId");
        Integer status = null;
        try {
            status = json.getInt("status");
        } catch (Exception e) {
        }
		*//*
		 * if (status == 1) { status =
		 * RcOrderSellerStatusEnum.UNPAID.getValue(); } else if (status == 2) {
		 * status = RcOrderSellerStatusEnum.UNDELIVERED.getValue(); } else if
		 * (status == 3) { status =
		 * RcOrderSellerStatusEnum.DELIVERED.getValue(); } else if (status == 4)
		 * { status = RcOrderSellerStatusEnum.UNREPLIED.getValue(); }
		 *//*
        Integer page = null;
        try {
            page = json.getInt("currentPage");
        } catch (Exception e) {
            page = 1;
        }
        Page pager = new Page();
        // 初始化时第一页
        pager.setPagination(true);
        pager.setPageSize(10);
        pager.setCurrentPage(page);
        List<RcOrder> orders = orderService.findPagedSellerOrders(merchantId,
                status, pager);
        result.setData(orders);
        return result;

    }*/

    /**
     * 订单详情
     *
     * @param data
     * @param timestamp
     * @param nonceStr
     * @param product
     * @param signature
     * @param httpServletRequest
     * @param response
     * @return
     */
/*    @RequestMapping(value = "order/orderDetail")
    @ResponseBody
    public Result<RcOrder> orderDetail(String data, String timestamp,
                                       String nonceStr, String product, String signature,
                                       HttpServletRequest httpServletRequest, HttpServletResponse response) {
        Result<RcOrder> result = new Result<RcOrder>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product,
                signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
        }
        Long orderId = json.getLong("orderId");
        RcOrder order = orderService.findSellerOrderById(orderId);
        result.setData(order);
        return result;

    }*/

    /**
     * 通过商家和买家查询订单
     *
     * @param data
     * @param timestamp
     * @param nonceStr
     * @param product
     * @param signature
     * @param httpServletRequest
     * @param response
     * @return
     */
  /*  @RequestMapping(value = "order/orderOfMember")
    @ResponseBody
    public Result<List<RcOrder>> orderOrderOfMember(String data,
                                                    String timestamp, String nonceStr, String product,
                                                    String signature, HttpServletRequest httpServletRequest,
                                                    HttpServletResponse response) {
        Result<List<RcOrder>> result = new Result<List<RcOrder>>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product,
                signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
        }
        int merchantId = json.getInt("merchantId");
        int buyerId = json.getInt("memberId");
        Integer page = null;
        try {
            page = json.getInt("currentPage");
        } catch (Exception e) {
            page = 1;
        }
        Page pager = new Page();
        // 初始化时第一页
        pager.setPagination(true);
        pager.setPageSize(10);
        pager.setCurrentPage(page);
        List<RcOrder> orders = orderService
                .findPagedSellerOrdersByMerchantIdAndBuyerId(merchantId,
                        buyerId, pager);
        result.setData(orders);
        return result;
    }*/

    /**
     * 通过商品名称或者订单id查询订单
     *
     * @param data
     * @param timestamp
     * @param nonceStr
     * @param product
     * @param signature
     * @param httpServletRequest
     * @param response
     * @return
     */
   /* @RequestMapping(value = "order/searchOrder")
    @ResponseBody
    public Result<List<RcOrder>> orderSearchOrder(String data,
                                                  String timestamp, String nonceStr, String product,
                                                  String signature, HttpServletRequest httpServletRequest,
                                                  HttpServletResponse response) {
        Result<List<RcOrder>> result = new Result<List<RcOrder>>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product,
                signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
        }
        int merchantId = json.getInt("merchantId");
        String keyword = json.getString("keyword");
        Integer page = null;
        try {
            page = json.getInt("currentPage");
        } catch (Exception e) {
            page = 1;
        }
        Page pager = new Page();
        // 初始化时第一页
        pager.setPagination(true);
        pager.setPageSize(10);
        pager.setCurrentPage(page);
        List<RcOrder> orders = orderService
                .findPagedSellerOrdersByMerchantIdAndKeyword(merchantId,
                        keyword, pager);
        result.setData(orders);
        return result;
    }*/

    /**
     * 订单发货
     *
     * @param data
     * @param timestamp
     * @param nonceStr
     * @param product
     * @param signature
     * @param httpServletRequest
     * @param response
     * @return
     */
 /*   @RequestMapping(value = "order/sendOrder")
    @ResponseBody
    public Result<String> orderSendOrder(String data, String timestamp,
                                         String nonceStr, String product, String signature,
                                         HttpServletRequest httpServletRequest, HttpServletResponse response) {
        Result<String> result = new Result<String>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product,
                signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
        }
        Long orderId = json.getLong("orderId");
        Integer deliveryType = json.getInt("deliveryType");
        RcOrderDelivery rcOrderDelivery = new RcOrderDelivery();
        rcOrderDelivery.setDeliveryType(deliveryType);
        if (deliveryType == OrderDeliveryDeliveryTypeEnum.BY_MERCHANT
                .getValue()) {
            String dispatcherMobile = json.getString("dispatcherMobile");
            String dispatcherName = json.getString("dispatcherName");
            rcOrderDelivery.setDispatcherMobile(dispatcherMobile);
            rcOrderDelivery.setDispatcherName(dispatcherName);
        }
        if (deliveryType == OrderDeliveryDeliveryTypeEnum.BY_EXPRESS
                .getValue()) {
            String trackingNumber = json.getString("trackingNumber");
            String logisticsCompany = json.getString("logisticsCompany");
            rcOrderDelivery.setTrackingNumber(trackingNumber);
            rcOrderDelivery.setLogisticsCompany(logisticsCompany);
        }
        rcOrderDelivery.setDeliveryTime(new Date());
        rcOrderDelivery.setOrderId(orderId);
        RcOrder rcOrder = orderService.findOrderById(orderId);
        if (rcOrder.getSellerStatus() != OrderSellerStatusEnum.UNDELIVERED
                .getValue()
                && rcOrder.getSellerStatus() != OrderSellerStatusEnum.UNPICKEDUP
                .getValue()) {
            return result;
        }
        if (rcOrder.getSellerStatus() == OrderSellerStatusEnum.UNDELIVERED
                .getValue()) {
            orderDeliveryService.addOrderDelivery(rcOrderDelivery);
            RcOrderOperation orderOperation = new RcOrderOperation();
            orderOperation.setOperationTime(new Date());
            orderOperation.setOrderId(orderId);
            orderOperation.setMemberId(rcOrder.getMemberId());
            orderOperation.setOperationType(OrderOperationTypeEnum.DELIVER
                    .getValue());
            orderOperationService.addOrderOperation(orderOperation);
            rcOrder.setBuyerStatus(OrderBuyerStatusEnum.UNRECEIVED.getValue());
            rcOrder.setSellerStatus(OrderSellerStatusEnum.DELIVERED
                    .getValue());
            orderService.modifyOrder(rcOrder);
        } else {
            orderDeliveryService.modifyOrderDelivery(rcOrderDelivery);
        }
        return result;
    }*/

    /**
     * 拒绝订单
     *
     * @param data
     * @param timestamp
     * @param nonceStr
     * @param product
     * @param signature
     * @param httpServletRequest
     * @param response
     * @return
     */
     /* @RequestMapping(value = "order/rejectOrder")
    @ResponseBody
  public Result<String> orderRejectOrder(String data, String timestamp,
                                           String nonceStr, String product, String signature,
                                           HttpServletRequest httpServletRequest, HttpServletResponse response) {
        Result<String> result = new Result<String>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product,
                signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
        }
        Long orderId = json.getLong("orderId");
        String refoundReason = json.getString("refoundReason");
        RcOrder order = orderService.findOrderById(orderId);

        RcBuyer buyer = buyerService.findBuyerById(order
                .getMemberId());
        if (order.getSellerStatus() != OrderSellerStatusEnum.UNDELIVERED
                .getValue()
                && order.getSellerStatus() != OrderSellerStatusEnum.UNPICKEDUP
                .getValue()) {
            return result;
        }
        RcOrderOperation rcOrderOperation = new RcOrderOperation();
        rcOrderOperation.setOrderId(order.getOrderId());
        rcOrderOperation.setOperationType(OrderOperationTypeEnum.REFUND
                .getValue());
        rcOrderOperation.setMemberId(order.getMemberId());
        rcOrderOperation.setOperationMemo(refoundReason);
        orderOperationService.addOrderOperation(rcOrderOperation);
        order.setBuyerStatus(OrderBuyerStatusEnum.REFUNDED.getValue());
        order.setSellerStatus(OrderSellerStatusEnum.REFUNDED.getValue());
        buyer.setWalletAmount(buyer.getWalletAmount().add(
                order.getPayPrice()));
        buyerService.modifyBuyer(buyer);
        RcBuyerTrade rcMemberTrade = new RcBuyerTrade();
        rcMemberTrade.setAmount(order.getPayPrice());
        rcMemberTrade.setBalance(buyer.getWalletAmount());
        rcMemberTrade.setType(BuyerTradeTypeEnum.REFUND.getValue());
        rcMemberTrade.setPaymentType(PaymentTypeEnum.BALANCE.getValue());
        rcMemberTrade.setPaymentInterface(PaymentInterfaceEnum.WALLET
                .getValue());
        rcMemberTrade.setOrderId(order.getOrderId());
        rcMemberTrade.setBuyerId(order.getMemberId());
        buyerTradeService.addBuyerTrade(rcMemberTrade);

        // 退回卡券
        RcOrderCoupon orderCoupon = orderCouponService
                .findOrderCouponByOrderId(order.getOrderId());
        if (orderCoupon != null) {
            buyerCouponService.modifyBuyerCouponStatus(
                    order.getMemberId(), orderCoupon.getCouponId(),
                    BuyerCouponStatusEnum.UNUSED.getValue());
        }
        orderService.modifyOrder(order);

        return result;

    }*/

    /**
     * 商家备注
     *
     * @param data
     * @param timestamp
     * @param nonceStr
     * @param product
     * @param signature
     * @param httpServletRequest
     * @param response
     * @return
     */
    /*  @RequestMapping(value = "order/remarkOrder")
    @ResponseBody
  public Result<String> orderRemarkOrder(String data, String timestamp,
                                           String nonceStr, String product, String signature,
                                           HttpServletRequest httpServletRequest, HttpServletResponse response) {
        Result<String> result = new Result<String>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product,
                signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
        }
        Long orderId = json.getLong("orderId");
        String content = json.getString("content");
        RcOrder order = orderService.findOrderById(orderId);
        order.setSellerMemo(content);
        orderService.modifyOrder(order);
        return result;
    }*/

    /**
     * @param data
     * @param timestamp
     * @param nonceStr
     * @param product
     * @param signature
     * @param httpServletRequest
     * @param response
     * @return
     */
/*    @RequestMapping(value = "order/replyOrder")
    @ResponseBody
    public Result<String> orderReplyOrder(String data, String timestamp,
                                          String nonceStr, String product, String signature,
                                          HttpServletRequest httpServletRequest, HttpServletResponse response) {
        Result<String> result = new Result<String>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product,
                signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
        }
        Long orderProdId = json.getLong("evaluationId");
        String content = json.getString("content");
        long orderId = json.getLong("orderId");

        orderProdEvaluationService.replyOrderProdEvaluation(orderProdId, content);
        RcOrderProdEvaluation param = new RcOrderProdEvaluation();
        param.setOrderId(orderId);
        List<RcOrderProdEvaluation> orderProdEvaluations = orderProdEvaluationService.findOrderProdEvaluations(param);
        boolean allReplied = true;
        for (RcOrderProdEvaluation orderProdEvaluation : orderProdEvaluations) {
            if (StringUtils.isBlank(orderProdEvaluation.getMerchantReply())) {
                allReplied = false;
                break;
            }
        }
        if (allReplied) {
            RcOrder order = orderService.findOrderById(orderId);
            order.setBuyerStatus(OrderBuyerStatusEnum.FINISHED.getValue());
            order.setSellerStatus(OrderSellerStatusEnum.FINISHED.getValue());
            orderService.modifyOrder(order);
        }

        return result;
    }*/

    /**
     * @param data
     * @param timestamp
     * @param nonceStr
     * @param product
     * @param signature
     * @param httpServletRequest
     * @param response
     * @return
     */
   /*       @RequestMapping("order/changeOrderAmount")
       @ResponseBody
    public Result<String> orderChangeOrderAmount(String data, String timestamp,
                                                 String nonceStr, String product, String signature,
                                                 HttpServletRequest httpServletRequest, HttpServletResponse response) {
        Result<String> result = new Result<String>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product,
                signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
        }
        Long orderId = json.getLong("orderId");
        String orderPayPrice = json.getString("orderPayPrice");
        RcOrder order = orderService.findOrderById(orderId);
        if (order.getBuyerStatus() == OrderBuyerStatusEnum.UNPAID
                .getValue()) {
            order.setPayPrice(BigDecimal.valueOf(Double
                    .valueOf(orderPayPrice)));
            orderService.modifyOrder(order);
        }

        return result;
    }*/

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

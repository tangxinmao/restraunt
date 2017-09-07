package com.socool.soft.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.socool.soft.bo.RcMerchant;
import com.socool.soft.bo.RcMerchantRole;
import com.socool.soft.bo.RcMerchantSection;
import com.socool.soft.bo.RcMerchantTable;
import com.socool.soft.bo.RcMerchantUser;
import com.socool.soft.bo.RcOrder;
import com.socool.soft.bo.RcOrderProd;
import com.socool.soft.bo.RcProdCat;
import com.socool.soft.bo.RcProdSku;
import com.socool.soft.bo.constant.OrderProdStatusEnum;
import com.socool.soft.bo.constant.OrderSellerStatusEnum;
import com.socool.soft.exception.ErrorValue;
import com.socool.soft.exception.SystemException;
import com.socool.soft.service.IMerchantRoleService;
import com.socool.soft.service.IMerchantSectionService;
import com.socool.soft.service.IMerchantService;
import com.socool.soft.service.IMerchantTableService;
import com.socool.soft.service.IMerchantUserService;
import com.socool.soft.service.IOrderProdService;
import com.socool.soft.service.IProductAppService;
import com.socool.soft.service.IUserGetuiService;
import com.socool.soft.service.merchant.IOrderService;
import com.socool.soft.vo.Result;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@RequestMapping(value = {"orderMerchant"})
@Controller
public class OrderMerchantController extends BaseController {
    @Autowired
    private IOrderService orderService;
    @Autowired
    private IMerchantSectionService merchantSectionService;
    @Autowired
    private IOrderProdService orderProdService;
    @Autowired
    private IProductAppService productAppService;
    @Autowired
    private IMerchantUserService merchantUserService;
    @Autowired
    private IMerchantService merchantService;
    @Autowired
    private IMerchantRoleService merchantRoleService;
    @Autowired
    private IMerchantTableService merchantTableService;

    private IUserGetuiService userGetuiService;


    @RequestMapping("sections")
    @ResponseBody
    public Result<List<RcMerchantSection>> sections(String data, String timestamp, String nonceStr, String product,
                                                    String signature, HttpServletRequest httpServletRequest, HttpServletResponse response) {
        Result<List<RcMerchantSection>> result = new Result<List<RcMerchantSection>>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
            return result;
        }
        int merchantId = json.getInt("merchantId");
        List<RcMerchantSection> merchantSectionList = orderService.findSectionsByMerchantId(merchantId);
        result.setData(merchantSectionList);
        return result;

    }

    @RequestMapping("tables")
    @ResponseBody
    public Result<List<RcMerchantTable>> tables(String data, String timestamp, String nonceStr, String product,
                                                String signature, HttpServletRequest httpServletRequest, HttpServletResponse response) {
        Result<List<RcMerchantTable>> result = new Result<List<RcMerchantTable>>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
            return result;
        }
        int merchantId = json.getInt("merchantId");
        JSONArray sectionIds = json.getJSONArray("sectionIds");
        if (CollectionUtils.isEmpty(sectionIds)) {
            result.setData(new ArrayList<RcMerchantTable>());
            return result;
        }
        List<RcMerchantTable> merchantTableList = orderService.findTablesByMerchantId(merchantId, sectionIds);
        result.setData(merchantTableList);
        return result;

    }

    @RequestMapping("orders")
    @ResponseBody
    public Result<List<RcOrder>> orders(String data, String timestamp, String nonceStr, String product,
                                        String signature, HttpServletRequest httpServletRequest, HttpServletResponse response) {
        Result<List<RcOrder>> result = new Result<List<RcOrder>>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
            return result;
        }
        int merchantId = json.getInt("merchantId");
        int tableId = json.getInt("tableId");
        List<RcOrder> rcOrders = orderService.findUnfinishedOrdersBySectionAndTable(merchantId, tableId);
        result.setData(rcOrders);
        return result;
    }

    @RequestMapping("orderDetail")
    @ResponseBody
    public Result<RcOrder> orderDetail(String data, String timestamp, String nonceStr, String product,
                                       String signature, HttpServletRequest httpServletRequest, HttpServletResponse response) {
        Result<RcOrder> result = new Result<RcOrder>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
            return result;
        }
        long orderId = json.getLong("orderId");
        RcOrder rcOrder = orderService.findOrderById(orderId);
        result.setData(rcOrder);
        return result;

    }

    @RequestMapping("receiveOrder")
    @ResponseBody
    public Result<RcOrder> receiveOrder(String data, String timestamp, String nonceStr, String product,
                                        String signature, HttpServletRequest httpServletRequest, HttpServletResponse response) {
        Result<RcOrder> result = new Result<RcOrder>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
            return result;
        }
        long orderId = json.getLong("orderId");
        int merchantUserId = json.getInt("merchantUserId");
        RcOrder rcOrder = orderService.findOrderById(orderId);
        //订单被接单处理
        if (rcOrder.getSellerStatus() != OrderSellerStatusEnum.UNCONFIRMED.getValue()) {
            result.setCode(ErrorValue.ORDER_GRABBED.getStr());
            return result;
        }
        orderService.grabOrder(orderId, merchantUserId);
        return result;
    }

    @RequestMapping("comfirmOrder")
    @ResponseBody
    public Result<RcOrder> comfirmOrder(String data, String timestamp, String nonceStr, String product,
                                        String signature, HttpServletRequest httpServletRequest, HttpServletResponse response) {
        Result<RcOrder> result = new Result<RcOrder>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
            return result;
        }
        long orderId = json.getLong("orderId");
        int merchantUserId = json.getInt("merchantUserId");
        orderService.confirmOrder(orderId, merchantUserId);
        return result;
    }

    @RequestMapping("cancelOrder")
    @ResponseBody
    public Result<RcOrder> cancelOrder(String data, String timestamp, String nonceStr, String product,
                                       String signature, HttpServletRequest httpServletRequest, HttpServletResponse response) {
        Result<RcOrder> result = new Result<RcOrder>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
            return result;
        }
        long orderId = json.getLong("orderId");
        int merchantUserId = json.getInt("merchantUserId");
        String operationMemo = json.getString("operationMemo");

        RcOrder rcOrder = orderService.findOrderById(orderId);
        //订单完成或者被取消
        if (rcOrder.getSellerStatus() == OrderSellerStatusEnum.FINISHED.getValue() || rcOrder.getSellerStatus() == OrderSellerStatusEnum.CANCELED.getValue()) {
            result.setCode(ErrorValue.ORDER_NOT_CANCEL.getStr());
            return result;
        }
        orderService.cancelOrder(orderId, merchantUserId, operationMemo);
        return result;
    }

    @RequestMapping("modifyOrder")
    @ResponseBody
    public Result<RcOrder> modifyOrder(String data, String timestamp, String nonceStr, String product,
                                       String signature, HttpServletRequest httpServletRequest, HttpServletResponse response) {
        Result<RcOrder> result = new Result<RcOrder>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
            return result;
        }
        int merchantUserId = json.getInt("merchantUserId");
        RcOrder order = (RcOrder) JSONObject.toBean(json, RcOrder.class);
        JSONArray orderProds = json.getJSONArray("orderProds");
        order.setOrderProds( JSONArray.toList(orderProds,RcOrderProd.class));
        if (!order.getTableId().equals(0)) {
            RcMerchantSection section = merchantSectionService.findMerchantSectionById(order.getSectionId());
            if (section == null) {
                result.setCode("0");
                return result;
            }
            RcMerchantTable table = merchantSectionService.findMerchantTablesBySectionIdAndTableNumber(order.getSectionId(), order.getTableNumber());
            if (table == null) {
                result.setCode("0");
                return result;
            }

            order.setTableId(table.getTableId());
            order.setSectionName(section.getName());
        }
     /*   JSONArray orderProds = json.getJSONArray("orderProds");
        List list = JSONArray.toList(orderProds, RcOrderProd.class);
        order.setOrderProds(list);*/
        orderService.modifyOrder(order, merchantUserId);
        return result;
    }
//    @RequestMapping("modifyOrderProd")
//    @ResponseBody
//    public Result modifyOrderProd(String data, String timestamp, String nonceStr, String product,
//                              String signature, HttpServletRequest httpServletRequest, HttpServletResponse response) {
//        Result result = new Result();
//        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
//        if (json == null) {
//            result.setCode("-1");
//            result.setResult("token验证失败！");
//        }
//        int merchantUserId = json.getInt("merchantUserId");
//        RcOrder order= (RcOrder) JSONObject.toBean(json,RcOrder.class);
//        for (RcOrderProd rcOrderProd:order.getOrderProds() ) {
//            rcOrderProd.setOrderId(order.getOrderId());
//        }
//     /*   JSONArray orderProds = json.getJSONArray("orderProds");
//        List list = JSONArray.toList(orderProds, RcOrderProd.class);
//        order.setOrderProds(list);*/
//        orderService.modifyOrderProds(order.getOrderProds(), merchantUserId);
//        return result;
//    }

    @RequestMapping("sectionsAndTables")
    @ResponseBody
    public Result<List<RcMerchantSection>> sectionsAndTables(String data, String timestamp, String nonceStr, String product,
                                                             String signature, HttpServletRequest httpServletRequest, HttpServletResponse response) {
        Result<List<RcMerchantSection>> result = new Result<List<RcMerchantSection>>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
            return result;
        }
        int merchantId = json.getInt("merchantId");
        List<RcMerchantSection> merchantSectionList = orderService.findSectionsWithTablesByMerchantId(merchantId);

        result.setData(merchantSectionList);
        return result;

    }

    @RequestMapping("checkOutOrder")
    @ResponseBody
    public Result<RcOrder> checkOutOrder(String data, String timestamp, String nonceStr, String product,
                                         String signature, HttpServletRequest httpServletRequest, HttpServletResponse response) {
        Result<RcOrder> result = new Result<RcOrder>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
            return result;
        }
        int merchantUserId = json.getInt("merchantUserId");
        long orderId = json.getLong("orderId");
        RcOrder rcOrder = orderService.findOrderById(orderId);
        //订单完成或者被取消
        if (rcOrder.getSellerStatus() == OrderSellerStatusEnum.FINISHED.getValue() || rcOrder.getSellerStatus() == OrderSellerStatusEnum.CANCELED.getValue()) {
            result.setCode(ErrorValue.ORDER_NOT_CHECK_OUT.getStr());
            return result;
        }
        orderService.checkOutOrder(orderId, merchantUserId);
        return result;
    }

    @RequestMapping(value = "addOrder")
    @ResponseBody
    public Result<RcOrder> addOrder(String data, String timestamp, String nonceStr, String product, String signature,
                                    HttpServletRequest httpServletRequest, HttpServletResponse response) throws SystemException {
        Result<RcOrder> result = new Result<>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
            return result;
        }
        int merchantUserId = json.getInt("merchantUserId");
        RcOrder order = (RcOrder) JSONObject.toBean(json, RcOrder.class);
        JSONArray orderProds = json.getJSONArray("orderProds");
        order.setOrderProds( JSONArray.toList(orderProds,RcOrderProd.class));
        return orderService.addOrder(order, merchantUserId);
    }

    @RequestMapping("printchecklist")
    @ResponseBody
    public Result<RcOrder> printCheckList(String data, String timestamp, String nonceStr, String product, String signature,
                                          HttpServletRequest httpServletRequest, HttpServletResponse response) {
        Result<RcOrder> result = new Result<>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
            return result;
        }
        long orderId = json.getLong("orderId");
        RcOrderProd param = new RcOrderProd();
        param.setOrderId(orderId);
        param.setStatus(OrderProdStatusEnum.UNCONFIRMED.getValue());
        List<RcOrderProd> orderProds = orderProdService.findOrderProds(param);
        for (RcOrderProd orderProd : orderProds) {
            orderProd.setStatus(OrderProdStatusEnum.CONFIRMED.getValue());
            orderProdService.modifyOrderProdById(orderProd);
        }
        return result;
    }

    @RequestMapping("menus")
    @ResponseBody
    public Result menus(String data, String timestamp, String nonceStr, String product, String signature,
                        HttpServletRequest httpServletRequest, HttpServletResponse response) {
        Result result = new Result<>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
            return result;
        }
        int merchantId = json.getInt("merchantId");
        List<RcProdCat> list = orderService.findProdCatsWithProds(merchantId);
        result.setData(list);
        return result;
    }

    @RequestMapping(value = "skuinfo")
    @ResponseBody
    public Result skuinfo(String data, String timestamp, String nonceStr, String product, String signature,
                          HttpServletRequest httpServletRequest, HttpServletResponse response) {
        Result result = new Result<>();
        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
        if (json == null) {
            result.setCode("-1");
            result.setResult("token验证失败！");
            return result;
        }
//		 String jsonStr = "{\"prodId\": \"12\",\"city\":\"Jakarta Barat\",\"prodSkuPropInfo\":[{\"prodPropId\":\"3\",\"prodPropEnumId\":\"2\"},{\"prodPropId\":\"4\",\"prodPropEnumId\":\"5\"}]}";
//					JSONObject json = JSONObject.fromObject(jsonStr);
        int prodId = json.getInt("prodId");
        JSONArray prodSkuPropInfos = json.getJSONArray("prodSkuPropInfos");
     /*   List<Integer> prodSkuPropEnumIds = new ArrayList<Integer>();
        for (Object prodSkuPropInfo : prodSkuPropInfos) {
            //JSONObject jo = JSONObject.fromObject(prodSkuPropInfo);
            prodSkuPropEnumIds.add(prodSkuPropInfo.getInt("prodPropEnumId"));
        }*/
        RcProdSku prodSku = productAppService.findSelectedProdSku(prodId, prodSkuPropInfos);
        result.setData(prodSku);
        return result;
    }
    @RequestMapping(value="merchantInfo")
    @ResponseBody
    public Result<Map<String,Object>> merchantInfo(String data, String timestamp, String nonceStr, String product, String signature){
        JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
        Result<Map<String,Object>> result = new Result<Map<String,Object>>();
        if(json==null){
            result.setCode("-1");
            result.setResult("token验证失败！");
            return result;
        }
        Integer merchantUserId = json.getInt("merchantUserId");
        RcMerchantUser merchantUser = merchantUserService.findMerchantUserById(merchantUserId);
        RcMerchant merchant = merchantService.findMerchantById(merchantUser.getMerchantId());
        Map<String,Object> map = new HashMap<String, Object>();
        Boolean value = merchantUser.getIsSuper()>0;
        map.put("isSuper",value);
        map.put("merchantId", merchant.getMerchantId());
        map.put("mobile", merchant.getMobile());
        map.put("merchantUserId", merchantUser.getMemberId());
        map.put("name", merchant.getName());
        if( merchant.getAddress()==null){
            map.put("address", "");
        }
        else{
            map.put("address", merchant.getAddress());
        }
        map.put("mobile", merchant.getMobile());
        if(merchant.getMealStyle()==null){
            map.put("mealStyle", "3");
        }
        else{
            map.put("mealStyle", merchant.getMealStyle());
        }
        if(merchant.getTaxRate()!=null){
            map.put("taxRate", merchant.getTaxRate());
        }
        else{
            map.put("taxRate", 0);

        }
        if(merchant.getServiceCharge()!=null){
            map.put("serviceCharge", merchant.getServiceCharge());
        }
        else{
            map.put("serviceCharge", 0);
        }
        if(StringUtils.isEmpty(merchantUser.getName()) ){
            map.put("nickName", merchantUser.getAccount());
        }
        else{
            map.put("nickName", merchantUser.getName());
        }
        List<RcMerchantRole> roles = merchantRoleService.findMerchantRolesByUserId(merchantUser.getMemberId());
        if(!CollectionUtils.isEmpty(roles)){
            map.put("memberRole", roles.get(0).getName());
        }
        else{
            map.put("memberRole", "shop manager");
        }
        map.put("memberEmail", merchantUser.getEmail());
        if( merchantUser.getMobile() == null){
            map.put("memberMobile", "");
        }
        else{
            map.put("memberMobile", merchantUser.getMobile());
        }
        result.setData(map);
        merchantUser.setLastLoginTime(new Date());
        merchantUserService.modifyMerchantUser(merchantUser);
        return result;
    }
}

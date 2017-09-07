package com.socool.soft.controller;

import com.socool.soft.bo.*;
import com.socool.soft.bo.constant.OrderBuyerStatusEnum;
import com.socool.soft.bo.constant.ProdStatusEnum;
import com.socool.soft.bo.constant.YesOrNoEnum;
import com.socool.soft.service.*;
import com.socool.soft.util.JsonUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value="/page")
public class MenuPageController {

	@Autowired
	private IProdCatService prodCatService;
	
	@Autowired
	private IProdService prodService;
	
	@Autowired
	private IOrderService orderService;
	
	@Autowired
	private IAppProdHotService appProdHotService;
	
	@Autowired
	private IProductAppService productAppService;
	@Autowired
	private IProdSkuService prodSkuService;
	@Autowired
	private IMerchantService merchantService;
	@RequestMapping("/menu")
	public String menuPage(HttpServletRequest request, int merchantId, Integer tableId, String sectionName, Integer tableNumber, Model model){
		RcMerchant merchant = merchantService.findMerchantById(merchantId);
		if(merchant == null || merchant.getDelFlag() == YesOrNoEnum.YES.getValue()) {
    		return "restaurant/not_exists";
    	}
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
		RcProdCat prodCat = new RcProdCat();
		prodCat.setMerchantId(merchantId);
		List<RcProdCat> list = prodCatService.findProdCatsWithProds(prodCat);
		RcAppProdHot param = new RcAppProdHot();
		param.setMerchantId(merchantId);
		List<RcAppProdHot> hotList = appProdHotService.findAppProdHots(param);
		model.addAttribute("hotList", hotList);
		model.addAttribute("catList", list);
		model.addAttribute("merchantId", merchantId);
		model.addAttribute("tableId", tableId);
		model.addAttribute("sectionName", sectionName);
		model.addAttribute("tableNumber", tableNumber);
		return "restaurant/menu";
	}
	
	@RequestMapping(value="/searchByCat")
	@ResponseBody
	public String searchByCat(Integer merchantId, Integer prodCatId){
		RcProd param = new RcProd();
		param.setMerchantId(merchantId);
		param.setProdCatId(prodCatId);
		List<RcProd> list = prodService.findProds(param);
		return JsonUtil.toJson(list);
	}
	@RequestMapping(value="/searchHotProd")
	@ResponseBody
	public String searchByCat(Integer merchantId){
		RcAppProdHot param = new RcAppProdHot();
		param.setMerchantId(merchantId);
		List<RcAppProdHot> list = appProdHotService.findAppProdHots(param);
		return JsonUtil.toJson(list);
	}
	
	/**
	 * 获取SKU信息
	 * 
	 * @return
	 */
	@RequestMapping(value = "/skuinfo")
	@ResponseBody
	public String getSkuInfo(String data) {
		JSONObject json = JSONObject.fromObject(data);
//		 String jsonStr = "{\"prodId\": \"12\",\"city\":\"Jakarta Barat\",\"prodSkuPropInfo\":[{\"prodPropId\":\"3\",\"prodPropEnumId\":\"2\"},{\"prodPropId\":\"4\",\"prodPropEnumId\":\"5\"}]}";
//					JSONObject json = JSONObject.fromObject(jsonStr);
		int prodId = json.getInt("prodId");
		JSONArray prodSkuPropInfos = json.getJSONArray("prodSkuPropInfo");
		List<Integer> prodSkuPropEnumIds = new ArrayList<Integer>();
		for (Object prodSkuPropInfo : prodSkuPropInfos) {
			JSONObject jo = JSONObject.fromObject(prodSkuPropInfo);
			prodSkuPropEnumIds.add(jo.getInt("prodPropEnumId"));
		}
		RcProdSku prodSku = productAppService.findSelectedProdSku(prodId, prodSkuPropEnumIds);
		String result = JsonUtil.toJson(prodSku);
		return result;
	}
	
	@RequestMapping(value="/cartinfo")
	@ResponseBody
	public String cartInfo(String json){
		if(json==null){
			return "";
		}
		JSONArray array = JSONArray.fromObject(json);
		JSONArray result = new JSONArray();
		for(Object o:array){
			JSONObject jo = JSONObject.fromObject(o);
			Integer prodId = jo.getInt("prodId");
			RcProd prod = prodService.findProdById(prodId);
			if(prod.getStatus() == ProdStatusEnum.SOLD_OUT.getValue() || prod.getStatus()== ProdStatusEnum.NOT_SELLING.getValue()){
				continue;
			}
			if(jo.containsKey("prodSkuId")){
				String prodSkuId = jo.getString("prodSkuId");
				RcProdSku prodSku = prodSkuService.findProdSkuWithCoupon(prodSkuId, prod.getMerchantId(), prod.getProdCatId());
				jo.put("price", prodSku.getPrice());
			}
			else{
				jo.put("price", prod.getPrice());
			}
			result.add(jo);
		}
		return result.toString();
	}
}

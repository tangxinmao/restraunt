package com.socool.soft.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.socool.soft.bo.RcMerchant;
import com.socool.soft.bo.RcMerchantRole;
import com.socool.soft.bo.RcMerchantUser;
import com.socool.soft.exception.ErrorValue;
import com.socool.soft.service.IMerchantRoleService;
import com.socool.soft.service.IMerchantService;
import com.socool.soft.service.IMerchantUserService;
import com.socool.soft.vo.Result;

@Controller
@RequestMapping(value="/merchant")
public class MerchantController extends BaseController {

	@Autowired
	private IMerchantUserService merchantUserService;	
	@Autowired
	private IMerchantService merchantService;
	@Autowired
	private IMerchantRoleService merchantRoleService;
	@RequestMapping(value="/login")
	@ResponseBody
	public Result<Map<String,Object>> login(String data,String timestamp,String nonceStr,String product,String signature){
		JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
		Result<Map<String,Object>> result = new Result<Map<String,Object>>();
		if(json==null){
			result.setCode("-1");
			result.setResult("token验证失败！");
			return result;
		}
		String email = json.getString("email");
		String password = json.getString("password");
//		String email = "ben18@ben.com";
//		String password = "111111";
		RcMerchantUser param = new RcMerchantUser();
		param.setEmail(email);
		param.setPassword(DigestUtils.md5Hex(password));
		List<RcMerchantUser> list = merchantUserService.findMerchantUser(param);
		RcMerchantUser merchantUser = null;
		if(CollectionUtils.isEmpty(list)){
			result.setCode(String.valueOf(ErrorValue.ERROR_PASSWORD_OR_USER_NAME.getValue()));
			result.setResult("ERROR PASSWORD OR USERNAME");
			return result;
		}else{
			for (RcMerchantUser rcMerchantUser: list) {
				if(email.equals(rcMerchantUser.getEmail()))
					merchantUser= rcMerchantUser;
			}

		}

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
		if(StringUtils.isEmpty( merchantUser.getName())){
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
	
	@RequestMapping(value="/modify")
	@ResponseBody
	public Result<Map<String,Object>> appModify(String data,String timestamp,String nonceStr,String product,String signature){
		Result<Map<String,Object>> result = new Result<Map<String,Object>>();
		JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
		if(json==null){
			result.setCode("-1");
			result.setResult("token验证失败！");
			return result;
		}
		Integer merchantId = null;
		try{
			merchantId = json.getInt("merchantId");
		}catch(Exception e){
			result.setCode("0");
			result.setResult("merchantId不能为空！");
			return result;
		}
		Integer mealStyle = null;
		try{
			mealStyle = json.getInt("mealStyle");
		}catch(Exception e){
		}
		Float taxRate = null;
		try{
			String taxRateStr = json.getString("taxRate");
			taxRate = Float.parseFloat(taxRateStr);
		}catch(Exception e){
		}
		Float serviceCharge = null;
		try{
			String serviceChargeStr = json.getString("serviceCharge");
			serviceCharge = Float.parseFloat(serviceChargeStr);
		}catch(Exception e){
		}
		String name = null;
		try{
			name = json.getString("name");
		}catch(Exception e){
		}
		String address = null;
		try{
			address = json.getString("address");
		}catch(Exception e){
		}
		RcMerchant merchant = new RcMerchant();
		merchant.setMerchantId(merchantId);
		merchant.setTaxRate(taxRate);
		merchant.setServiceCharge(serviceCharge);
		merchant.setName(name);
		merchant.setAddress(address);
		merchant.setMealStyle(mealStyle);
		merchantService.modifyMerchant(merchant);
		result.setCode("1");
		result.setResult("success!");
		return result;
	}
	
	@RequestMapping(value="/modifyuserinfo")
	public Result<Map<String,Object>> modifyUserinfo(String data,String timestamp,String nonceStr,String product,String signature){
		Result<Map<String,Object>> result = new Result<Map<String,Object>>();
		JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
		if(json==null){
			result.setCode("-1");
			result.setResult("token验证失败！");
			return result;
		}
		Integer merchantUserId = null;
		try{
			merchantUserId = json.getInt("merchantUserId");
		}catch(Exception e){
			result.setCode("0");
			result.setResult("merchantUserId不能为空！");
			return result;
		}
		String nickName = null;
		try{
			nickName = json.getString("nickName");
		}catch(Exception e){
		}
		String mobile = null;
		try{
			mobile = json.getString("mobile");
		}catch(Exception e){
		}
		RcMerchantUser merchantUser = new RcMerchantUser();
		merchantUser.setMemberId(merchantUserId);
		merchantUser.setName(nickName);
		merchantUser.setMobile(mobile);
		merchantUserService.modifyMerchantUser(merchantUser);
		result.setCode("1");
		result.setResult("success!");
		return result;
	}
	
	@RequestMapping(value="/logout")
	public Result<Map<String,Object>> logout(String data,String timestamp,String nonceStr,String product,String signature){
		Result<Map<String,Object>> result = new Result<Map<String,Object>>();
		result.setCode("1");
		result.setResult("success!");
		return result;
	}
}

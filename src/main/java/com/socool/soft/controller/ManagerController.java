package com.socool.soft.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.socool.soft.bo.RcAppVersion;
import com.socool.soft.bo.RcCity;
import com.socool.soft.bo.RcFileInfo;
import com.socool.soft.bo.RcMember;
import com.socool.soft.bo.RcMerchant;
import com.socool.soft.bo.RcMerchantQRCode;
import com.socool.soft.bo.RcMerchantUser;
import com.socool.soft.bo.RcVendor;
import com.socool.soft.bo.constant.YesOrNoEnum;
import com.socool.soft.constant.PropertyConstants;
import com.socool.soft.service.IAppVersionService;
import com.socool.soft.service.ICityService;
import com.socool.soft.service.IFileInfoService;
import com.socool.soft.service.IMemberService;
import com.socool.soft.service.IMerchantService;
import com.socool.soft.service.IMerchantUserService;
import com.socool.soft.service.IVendorService;
import com.socool.soft.util.CheckUtil;
import com.socool.soft.util.JsonUtil;
import com.socool.soft.vo.Page;

import net.sf.json.JSONObject;

@RequestMapping(value="/manager")
@Controller
public class ManagerController extends BaseController {

	@Autowired
	private IFileInfoService fileInfoService;
	@Autowired
	private IMemberService memberService;
	@Autowired
	private IMerchantService merchantService;
	@Autowired
	private IMerchantUserService merchantUserService;
	@Autowired
	private IAppVersionService appVersionService;
	@Autowired
	private ICityService cityService;
	@Autowired
	private IVendorService vendorService;
	@Autowired
	private PropertyConstants propertyConstants;
	
	@RequestMapping(value="/addmerchant")
	public String addMerchant(Model model){
		List<RcVendor> vendors = vendorService.findAllVendors();
		List<String> provinces = cityService.findProvinces();
		if(!CollectionUtils.isEmpty(provinces)){
			List<RcCity> cities = cityService.findCitiesByProvinceName(provinces.get(0));
			model.addAttribute("cityList", cities);
		}
		model.addAttribute("vendorList", vendors);
		model.addAttribute("provinceList", provinces);
		return "addMerchant";
	}
	
	@RequestMapping(value="/changeprovince")
	@ResponseBody
	public String changeProvince(String provinceName){
		List<RcCity> cities = cityService.findCitiesByProvinceName(provinceName);
		Iterator<RcCity> iterator = cities.iterator();
		while (iterator.hasNext()){
			RcCity next = iterator.next();
			if(!next.getProvinceName().equals(provinceName)){
				iterator.remove();
			}
		}
		return JsonUtil.toJson(cities);
	}
	
	@RequestMapping(value="uploadcover")
	public String uploadCover(HttpServletRequest request,Model model) throws IllegalStateException, IOException{
		Integer memberId = getUserId(request);
		if(memberId == null) {
			memberId = getVendorUserId(request);
		}
		if(memberId == null) {
			memberId = getMerchantUserId(request);
		}
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        if(multipartResolver.isMultipart(request)) {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            Iterator<String> iter = multipartRequest.getFileNames();
            while(iter.hasNext()) {
                MultipartFile multipartFile = multipartRequest.getFile(iter.next());
                if(multipartFile != null) {
                    String fileName = multipartFile.getOriginalFilename();
                    if(StringUtils.isNotBlank(fileName)) {
                    	String imgUrl = "";
                    	if(multipartFile.getSize() > 5 * 1024 * 1024){
                			imgUrl = "001";
                			model.addAttribute("success",false);
                		} else if(multipartFile.getSize() == 0){
                			imgUrl = "002";
                			model.addAttribute("success",false);
                		} else {
                			String fileInfoId = UUID.randomUUID().toString().replaceAll("-", "");
                			int pos = fileName.lastIndexOf(".");
                			String originalName = fileName.substring(0, pos);
                			String suffixName = fileName.substring(pos + 1, fileName.length());
                			Pattern pattern = Pattern.compile("(png|PNG|GIF|gif|jpg|JPG|bmp|BMP)");
                			Matcher matcher = pattern.matcher(suffixName);
                			if(!matcher.matches()){
                				return "{\"code\":\"0\",\"result\":\"图片格式不正确（必须为.jpg/.gif/.bmp/.png图片）！\"}";
                			}
                			String realFileName = fileInfoId;
                			Calendar cal = Calendar.getInstance();
                			int year = cal.get(Calendar.YEAR);
                			int month = cal.get(Calendar.MONTH) + 1;
                			int day = cal.get(Calendar.DATE);
                			String relativePath = year + File.separator + month + File.separator + day + File.separator;
                			String dirPath = propertyConstants.systemFileServerRoot + relativePath;
                			File dir = new File(dirPath);
                			if(!dir.exists()) {
                				dir.mkdirs();
                			}
                			File file = new File(dirPath + realFileName + "." + suffixName);
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
                			imgUrl = propertyConstants.systemFileServerUrl + fileInfo.getPath();
                			model.addAttribute("imgUrl", imgUrl);
                			model.addAttribute("fileid", realFileName);
                			model.addAttribute("success", true);
                		}
                    }
                }
            }
        }
		return "subpage";
	}
	
	@RequestMapping(value="/savemerchant")
	@ResponseBody
	public String saveMerchant(RcMerchantUser merchantUser, RcMerchant merchant,String merchantName,String merchantDesc,String memberDesc) throws Exception{
		if(merchant.getTaxRate() != null){
			merchant.setTaxRate(new BigDecimal(merchant.getTaxRate()).divide(new BigDecimal(100), 4, RoundingMode.HALF_UP).floatValue());
		}
		if(merchant.getServiceCharge() != null){
			merchant.setServiceCharge(new BigDecimal(merchant.getServiceCharge()).divide(new BigDecimal(100), 4, RoundingMode.HALF_UP).floatValue());
		}
        CheckUtil.tooLong(merchantName, 50, "Restaurant Name");
        CheckUtil.tooLong(merchantDesc, 500, "Description");
        CheckUtil.tooLong(merchant.getEmail(), 50, "Email");

		merchant.setName(merchantName);
		merchant.setDescription(merchantDesc);
//		merchant.setEmail(null);
		merchantUser.setDescription(memberDesc);
		merchantUser.setMobile(null);
		RcMember checkAccount = memberService.findMemberByAccount(merchantUser.getAccount());
		if(checkAccount != null){
			return "{\"code\":\"2\"}";
		}
		RcMember checkEmail = memberService.findMemberByEmail(merchantUser.getEmail());
		if(checkEmail != null){
			return "{\"code\":\"3\"}";
		}
//		RcMember checkMobile = memberService.findMemberByMobile(merchantUser.getMobile());
//		if(checkMobile != null){
//			return "{\"code\":\"3\"}";
//		}
		merchantUser.setPassword(DigestUtils.md5Hex(merchantUser.getPassword()));
		merchantUser.setIsSuper(YesOrNoEnum.YES.getValue());
		int merchantId = merchantService.addMerchant(merchant);
		merchantUser.setMerchantId(merchantId);
		merchantUserService.addMerchantUser(merchantUser);
		//注册环信IM账号
//		imService.regist(propertyConstants.huanxinSellerAccountPrefix + "_" + merchantId + "_" + merchantUserId, merchant.getName());
		return "{\"code\":\"1\"}";
	}
	@RequestMapping(value="/merchantlist")
	public String merchantList(HttpServletRequest request,Model model,Integer currentPage, Integer storeId, 
			String storeName, String merchantContact, String phone, String email, String city){
		Integer vendorId = getVendorUserId(request);
		boolean flag = false;
		if (currentPage == null) {
			currentPage = 1;
			flag = true;
		}
		RcMerchant param = new RcMerchant();
		param.setMerchantId(storeId);
		param.setName(storeName);
		param.setContactPerson(merchantContact);
		param.setVendorId(vendorId);
		param.setCityName(city);
		param.setMobile(phone);
		param.setEmail(email);
		param.setOrderBy("MERCHANT_ID DESC");
		// 分页
		Page page = new Page();
		// 初始化时第一页
		page.setPagination(true);
		page.setPageSize(10);
		page.setCurrentPage(currentPage);
		model.addAttribute("page", page);
		List<RcMerchant> merchants = merchantService.findPagedMerchants(param, page);
		
		List<RcVendor> vendors = vendorService.findAllVendors();
		List<String> provinces = cityService.findProvinces();
		model.addAttribute("vendorList", vendors);
		model.addAttribute("provinceList", provinces);
		model.addAttribute("list", merchants);
		if(flag) {
			return "merchantList";
		} else {
			return "merchantList_inner";
		}
	}
	
	@RequestMapping(value="/modifymerchant")
	@ResponseBody
	public String modifyMerchant(int merchantId, String merchantName, String logoUrl, String merchantDesc, String mobile,Integer vendorId,Integer cityId,String address,String contact,Float taxRate,Float serviceCharge,Integer mealStyle){
		RcMerchant merchant = merchantService.findMerchantById(merchantId);
		merchant.setDescription(merchantDesc);
		merchant.setLogoUrl(logoUrl);
		merchant.setMobile(mobile);
		merchant.setName(merchantName);
		merchant.setVendorId(vendorId);
		merchant.setContactPerson(contact);
		merchant.setAddress(address);
		merchant.setCityId(cityId);
		merchant.setMealStyle(mealStyle);
		if(taxRate != null){
			merchant.setTaxRate(new BigDecimal(taxRate).divide(new BigDecimal(100), 4, RoundingMode.HALF_UP).floatValue());
		}
		if(serviceCharge != null){
			merchant.setServiceCharge(new BigDecimal(serviceCharge).divide(new BigDecimal(100), 4, RoundingMode.HALF_UP).floatValue());
		}
		merchantService.modifyMerchant(merchant);
//		if(StringUtils.isNotBlank(mobile)) {
//			RcMerchantUser merchantUserDb=	merchantUserService.findMerchantUserByMobile(mobile);
//			RcMerchantUser merchantUser = merchantUserService.findMerchantSuperAdmin(merchantId);
//			if(merchantUserDb!=null&&!merchantUserDb.getMemberId().equals(merchantUser.getMemberId())){
//				return "{\"code\":\"0\",\"result\":\"mobile is exist.\"}";
//			}
//			merchantUser.setMobile(mobile);
//			RcMember member = memberService.findMemberByMobile(merchantUser.getMobile());
//			if(member!=null&&!member.getMemberId().equals(merchantUser.getMemberId()))
//				return "{\"code\":\"0\",\"result\":\"mobile is exist.\"}";
//			else
//					merchantUserService.modifyMerchantUser(merchantUser);
//
//		}
		return "{\"code\":\"1\"}";
	}
	
	@RequestMapping(value="/deleteone")
	@ResponseBody
	public String deleteOne(int merchantId){
		merchantService.removeMerchant(merchantId);
		return "{\"code\":\"1\"}"; 
	}
	@RequestMapping(value="/enableOne")
	@ResponseBody
	public String enableOne(int merchantId){
		merchantService.coverMerchant(merchantId);
		return "{\"code\":\"1\"}";
	}

	
//	@RequestMapping(value="/authbrandchosebox")
//	public String authBrandChoseBox(Model model,int merchantId){
//		RcMerchant merchant = merchantService.findMerchantByMerchantId(merchantId);
//		List<List<RcProdBrand>> list = merchantService.findMerchantBrands(merchantId);
//		setData(model, new String[]{"merchant","originList","list"}, new Object[]{merchant,list.get(0),list.get(1)});
//		return "merchantAuthBrandChoseBox";
//	}
	
	@RequestMapping(value="/checkmemberinfo")
	@ResponseBody
	public String authBrandInfo(int merchantId){
		RcMerchantUser merchantUser = merchantUserService.findMerchantSuperAdmin(merchantId);
		return "{\"code\":\"1\",\"account\":\""+merchantUser.getAccount()+"\",\"email\":\""+merchantUser.getEmail()+"\",\"memberDesc\":\""+merchantUser.getDescription()+"\"}";
	}
	
	@RequestMapping(value="/modifyaccount")
	@ResponseBody
	public String modifyAccount(int merchantId, String memberPwd, String email,String mobile,String memberDesc){
		RcMerchantUser merchantUser = merchantUserService.findMerchantSuperAdmin(merchantId);
		RcMember member = memberService.findMemberByEmail(email);
		if(member != null && !member.getMemberId().equals(merchantUser.getMemberId())){
			return "{\"code\":\"3\"}";
		}
//		member = memberService.findMemberByMobile(mobile);
//		if(member != null && !member.getMemberId().equals(merchantUser.getMemberId())){
//			return "{\"code\":\"3\"}";
//		}
//		if(memberService.findMemberByMobile(mobile) != null){
//			return "{\"code\":\"3\"}";
//		}
		merchantUser.setEmail(email);
//		merchantUser.setMobile(mobile);
		if(StringUtils.isNotBlank(memberPwd)) {
			merchantUser.setPassword(DigestUtils.md5Hex(memberPwd));
		}
		merchantUser.setDescription(memberDesc);
		merchantUserService.modifyMerchantUser(merchantUser);
		if(StringUtils.isNotBlank(email) || StringUtils.isNotBlank(mobile)) {
			RcMerchant merchant = merchantService.findMerchantById(merchantId);
			merchant.setMobile(mobile);
			merchant.setEmail(email);
			merchantService.modifyMerchant(merchant);
		}
		return "{\"code\":\"1\"}";
	}
	
//	@RequestMapping(value="/authbrandinfo")
//	@ResponseBody
//	public String authBrandInfo(int merchantId,String prodBrandIds){
//		prodBrandService.removeMerchantBrands(merchantId);
//		String[] prodBrandIdArray = prodBrandIds.split(",,,");
//		for(String prodBrandId : prodBrandIdArray){
//			RcProdBrandRel prodBrandRel = new RcProdBrandRel();
//			prodBrandRel.setMerchantId(merchantId);
//			prodBrandRel.setProdBrandId(Integer.parseInt(prodBrandId));
//			prodBrandService.addProdBrandRel(prodBrandRel);
//		}
//		return "{\"code\":\"1\"}";
//	}
	
	@RequestMapping(value="/check")
	@ResponseBody
	public String check(String data,String timestamp,String nonceStr,String product,String signature){
		JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
		String curVersion = json.getString("curVersion");
		int system = json.getInt("system");
		RcAppVersion newAppVersion = appVersionService.findAppVersionBySystemAndVersion(system, curVersion);
		if(newAppVersion == null) {
			return "{\"code\":\"1\",\"result\":\"已经是最新版本!\",\"data\":{\"forceFlag\":2}}";
		} else {
			if(newAppVersion.getForceFlag()==YesOrNoEnum.YES.getValue()) {
				return "{\"code\":\"1\",\"data\":{\"forceFlag\":1,\"verInfo\":\""+newAppVersion.getVerInfo()+"\",\"downloadUrl\":\""+newAppVersion.getDownloadUrl()+"\",\"verNo\":\""+newAppVersion.getVerNo()+"\"}}";
			} else{
				return "{\"code\":\"1\",\"data\":{\"forceFlag\":0,\"verInfo\":\""+newAppVersion.getVerInfo()+"\",\"downloadUrl\":\""+newAppVersion.getDownloadUrl()+"\",\"verNo\":\""+newAppVersion.getVerNo()+"\"}}";
			}
		}
	}
	
	/**
	 * 版本列表
	 * @param request
	 * @param currentPage
	 * @param model
	 * @return
	 */
	@RequestMapping(value="versionlist")
	public String smokeversionlist(HttpServletRequest request,Integer currentPage,Model model){
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
		List<RcAppVersion> versions = appVersionService.findAllPagedAppVersions(page);
		model.addAttribute("list",versions);
		if(flag) {
			return "versionlist";
		} else {
			return "versionlist_inner";
		}
	}
	
	@RequestMapping(value="addversion")
	@ResponseBody
	public String addVersion(HttpServletRequest request,Model model,String vernum,byte flag,String vesInfo,int system){
		RcAppVersion appVersion = appVersionService.findAppVersionBySystem(system);
		if(appVersion == null){
			return "{\"code\":\"0\",\"result\":\"no system version info！\"}";
		}
		appVersion.setVerInfo(vesInfo);
		appVersion.setForceFlag(flag);
		appVersion.setVerInfo(vesInfo);
		appVersion.setVerNo(vernum);
		appVersionService.modifyAppVersion(appVersion);
		return "{\"code\":\"1\":\"result\":\"update success！\"}";
	}
	
	@RequestMapping(value="uploadvesfile")
	@ResponseBody
	public String uploadVesFile(HttpServletRequest request,Integer system,HttpServletResponse response) throws IllegalStateException, IOException{
		int userId = getUserId(request);
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        if(multipartResolver.isMultipart(request)) {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            Iterator<String> iter = multipartRequest.getFileNames();
            while(iter.hasNext()) {
                MultipartFile multipartFile = multipartRequest.getFile(iter.next());
                if(multipartFile != null) {
                    String fileName = multipartFile.getOriginalFilename();
                    if(StringUtils.isNotBlank(fileName)) {
            			String fileInfoId = UUID.randomUUID().toString().replaceAll("-", "");
            			int pos = fileName.lastIndexOf(".");
            			String originalName = fileName.substring(0, pos);
            			String suffixName = fileName.substring(pos + 1, fileName.length());
            			String realFileName = originalName;
            			Calendar cal = Calendar.getInstance();
            			int year = cal.get(Calendar.YEAR);
            			int month = cal.get(Calendar.MONTH) + 1;
            			int day = cal.get(Calendar.DATE);
            			String relativePath = year + File.separator + month + File.separator + day + File.separator;
            			String dirPath = propertyConstants.systemFileServerRoot + relativePath;
            			File dir = new File(dirPath);
            			if(!dir.exists()) {
            				dir.mkdirs();
            			}
            			File file = new File(dirPath + realFileName + "." + suffixName);
            			multipartFile.transferTo(file);
            			RcFileInfo fileInfo = new RcFileInfo();
            			fileInfo.setFileInfoId(fileInfoId);
            			fileInfo.setOriginalName(originalName);
            			fileInfo.setRealName(realFileName);
            			fileInfo.setSuffixName(suffixName);
            			fileInfo.setSize(multipartFile.getSize());
            			fileInfo.setRelativePath(relativePath);
            			fileInfo.setMemberId(userId);
            			fileInfoService.addFileInfo(fileInfo);
            			RcAppVersion version = appVersionService.findAppVersionBySystem(system);
                    	version.setDownloadUrl(propertyConstants.systemFileServerUrl + fileInfo.getPath());
                    	version.setUpdateTime(new Date());
                    	appVersionService.modifyAppVersion(version);
                    }
                }
            }
        }
		return "";
	}
	
//	@RequestMapping(value="/editmobile")
//	@ResponseBody
//	public String editMobile(HttpServletRequest request, String mobile){
//		if(memberService.findMemberByMobile(mobile) != null){
//			return "{\"code\":\"3\"}";
//		}
//		Integer userId = getUserId(request);
//		if(userId != null) {
//			RcUser user = userService.findUserById(userId);
//			user.setMobile(mobile);
//			userService.modifyUser(user);
//			return "{\"code\":\"1\"}";
//		} else {
//			Integer merchantUserId = getMerchantUserId(request);
//			if(merchantUserId != null) {
//				RcMerchantUser merchantUser = merchantUserService.findMerchantUserById(merchantUserId);
//				merchantUser.setMobile(mobile);
//				merchantUserService.modifyMerchantUser(merchantUser);
//				return "{\"code\":\"1\"}";
//			} else {
//				Integer vendorUserId = getVendorUserId(request);
//				if(vendorUserId != null) {
//					RcVendorUser vendorUser = vendorUserService.findVendorUserById(vendorUserId);
//					vendorUser.setMobile(mobile);
//					vendorUserService.modifyVendorUser(vendorUser);
//					return "{\"code\":\"1\"}";
//				}
//			}
//		}
//		return "{\"code\":\"0\"}";
//	}
//	@RequestMapping(value="/editEmail")
//	@ResponseBody
//	public String editEmial(HttpServletRequest request, String email){
//		if(memberService.findMemberByEmail(email) != null){
//			return "{\"code\":\"3\"}";
//		}
//		Integer userId = getUserId(request);
//		if(userId != null) {
//			RcUser user = userService.findUserById(userId);
//			user.setEmail(email);
//			userService.modifyUser(user);
//			return "{\"code\":\"1\"}";
//		} else {
//			Integer merchantUserId = getMerchantUserId(request);
//			if(merchantUserId != null) {
//				RcMerchantUser merchantUser = merchantUserService.findMerchantUserById(merchantUserId);
//				merchantUser.setEmail(email);
//				merchantUserService.modifyMerchantUser(merchantUser);
//				return "{\"code\":\"1\"}";
//			} else {
//				Integer vendorUserId = getVendorUserId(request);
//				if(vendorUserId != null) {
//					RcVendorUser vendorUser = vendorUserService.findVendorUserById(vendorUserId);
//					vendorUser.setEmail(email);
//					vendorUserService.modifyVendorUser(vendorUser);
//					return "{\"code\":\"1\"}";
//				}
//			}
//		}
//		return "{\"code\":\"0\"}";
//	}
	
	@RequestMapping(value="/checkmerchantbankinfo")
	@ResponseBody
	public String checkMerchantBankinfo(int merchantId){
		RcMerchant merchant = merchantService.findMerchantById(merchantId);
		if(merchant==null){
			return "{\"code\":\"0\"}";
		}
		return "{\"code\":\"1\",\"bankName\":\""+merchant.getBankName()+"\",\"bankAccount\":\""+merchant.getBankAccount()+"\",\"bankAccountName\":\""+merchant.getBankAccountName()+"\"}";
	}
	
	@RequestMapping(value="/savebankinfo")
	@ResponseBody
	public String saveBankinfo(RcMerchant merchant){
		int result = merchantService.modifyMerchant(merchant);
		if(result==0) {
			return "{\"code\":\"0\",\"result\":\"fail\"}";
		} else {
			return "{\"code\":\"1\",\"result\":\"success\"}";
		}
	}
	
	@RequestMapping(value="/test")
	@ResponseBody
	public String test() throws Exception{
//		List<RcMerchant> list = merchantService.findAllMerchants();
//		for(RcMerchant merchant:list){
//			RcMerchantUser merchantUser = merchantUserService.findMerchantSuperAdmin(merchant.getMerchantId());
//			//注册环信IM账号
//			imService.regist(propertyConstants.huanxinSellerAccountPrefix + "_" + merchant.getMerchantId() + "_" + merchantUser.getMemberId(), merchant.getName());
//		}
//		
		return "";
	}
	
	@RequestMapping(value="/generateqrcode")
	@ResponseBody
	public String generateQRCode(HttpServletRequest request, int merchantId) {
		merchantService.removeMerchantQRCode(merchantId);
		int result = merchantService.addMerchantQRCodes(merchantId, request.getSession().getServletContext().getRealPath("/") + "static/images/KS_QR_code.png".replace("/", File.separator));
		if(result > 1) {
			return "{\"code\":\"1\"}";
		}
		return "{\"code\":\"0\", \"result\":\"Fail, please config section first\"}";
	}
	
	@RequestMapping(value="/downloadqrcode")
	@ResponseBody
	public String downloadQRCode(HttpServletResponse response, int merchantId) throws IOException {
		List<RcMerchantQRCode> merchantQRCodes = merchantService.findMerchantQRCodesByMerchantId(merchantId);
		if(CollectionUtils.isEmpty(merchantQRCodes)) {
			return "{\"code\":\"0\"}";
		}
		RcMerchant merchant = merchantService.findMerchantById(merchantId);
		response.setContentType("application/x-download");
		String fileName = merchant.getName() + " QR Code.zip";
        response.setHeader("Content-Disposition", "attachment;filename=\"" + new String(fileName.getBytes("UTF-8"),"ISO-8859-1") + "\"");
		ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());
		int start = propertyConstants.systemFileServerUrl.length();
		byte[] buffer = new byte[1024];
		for(RcMerchantQRCode merchantQRCode : merchantQRCodes) {
			File merchantQRCodeFile = new File(propertyConstants.systemFileServerRoot + merchantQRCode.getImgUrl().substring(start));
			FileInputStream fis = new FileInputStream(merchantQRCodeFile);
			if(merchantQRCode.getTableId() == 0) {
				zipOutputStream.putNextEntry(new ZipEntry(merchant.getName() + ".png"));
			} else {
				zipOutputStream.putNextEntry(new ZipEntry(merchant.getName() + "_" + merchantQRCode.getSectionName() + "_" + merchantQRCode.getTableNumber() + ".png"));
			}
			int len = 0;
			while ((len = fis.read(buffer)) > 0) {
				zipOutputStream.write(buffer, 0, len);
			}
			zipOutputStream.closeEntry();
			fis.close();
		}
        zipOutputStream.flush();
        zipOutputStream.close();
		return "{\"code\":\"1\"}";
	}
}

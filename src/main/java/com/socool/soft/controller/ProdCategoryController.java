package com.socool.soft.controller;

import com.socool.soft.bo.RcFileInfo;
import com.socool.soft.bo.RcMerchantUser;
import com.socool.soft.bo.RcProd;
import com.socool.soft.bo.RcProdCat;
import com.socool.soft.constant.PropertyConstants;
import com.socool.soft.exception.SystemException;
import com.socool.soft.service.IFileInfoService;
import com.socool.soft.service.IMerchantUserService;
import com.socool.soft.service.IProdCatService;
import com.socool.soft.service.IProdService;
import com.socool.soft.util.CheckUtil;
import com.socool.soft.vo.Page;
import com.socool.soft.vo.Result;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequestMapping(value="/category")
@Controller
public class ProdCategoryController extends BaseController {

	@Autowired
	private IProdCatService prodCatService;
	@Autowired
	private IMerchantUserService merchantUserService;
	
	@Autowired
	private IFileInfoService fileInfoService;
	@Autowired
	private PropertyConstants propertyConstants;
	@Autowired
	private IProdService prodService;
	
	@RequestMapping(value="/list")
	public String listAll(HttpServletRequest request, Model model,Integer currentPage){
		Integer merchantUserId = getMerchantUserId(request);
		RcMerchantUser merchantUser = merchantUserService.findMerchantUserById(merchantUserId);
		RcProdCat param = new RcProdCat();
		if(merchantUser!=null){
			param.setMerchantId(merchantUser.getMerchantId());
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
		
		List<RcProdCat> prodCats = prodCatService.findPagedTopProdCatsWithChildren(param, page);
		model.addAttribute("list", prodCats);
		if(flag) {
			return "categoryList";
		} else {
			return "categoryList_inner";
		}
	}
	
	@RequestMapping(value="uploadcover")
	public String uploadCover(HttpServletRequest request,Model model) throws IllegalStateException, IOException{
		Integer userId = getUserId(request);
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
                			fileInfo.setMemberId(userId);
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
	
	@RequestMapping(value="/addtoplevel")
	@ResponseBody
	public String addTopLevel(HttpServletRequest request, String name, String icon, Integer seq) throws SystemException {
	    CheckUtil.isBlank(name,"Category Name");
	    CheckUtil.tooLong(name, 50, "Category Name");

		Integer merchantUserId = getMerchantUserId(request);
		RcMerchantUser merchantUser = merchantUserService.findMerchantUserById(merchantUserId);
		RcProdCat prodCat = new RcProdCat();
		prodCat.setName(name);
		prodCat.setIcon(icon);
		prodCat.setSeq(seq);
		if(merchantUser!=null){
			prodCat.setMerchantId(merchantUser.getMerchantId());
		}
		int result = prodCatService.addProdCat(prodCat);
		if(result > 0) {
			return "{\"code\":\"1\"}";
		}
		return "{\"code\":\"0\"}";
	}
	
	@RequestMapping(value="/addnextlevel")
	@ResponseBody
	public String addNextLevel(HttpServletRequest request, int parentId, String name, String icon, Integer seq){
		Integer merchantUserId = getMerchantUserId(request);
		RcMerchantUser merchantUser = merchantUserService.findMerchantUserById(merchantUserId);
		RcProdCat prodCat = new RcProdCat();
		prodCat.setParentId(parentId);
		prodCat.setName(name);
		prodCat.setIcon(icon);
		prodCat.setSeq(seq);
		if(merchantUser!=null){
			prodCat.setMerchantId(merchantUser.getMerchantId());
		}
		int result = prodCatService.addProdCat(prodCat);
		if(result > 0) {
			return "{\"code\":\"1\"}";
		}
		return "{\"code\":\"0\"}";
	}
	
	@RequestMapping(value="uploadchildcover")
	public String uploadChildCover(HttpServletRequest request,Model model) throws IllegalStateException, IOException{
		Integer userId = getUserId(request);
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
                			fileInfo.setMemberId(userId);
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
	
	@RequestMapping(value="/deleteChild")
	@ResponseBody
	public String deleteChild(int prodCatId){
		int result = prodCatService.removeProdCatAndChildrenById(prodCatId);
		if(result > 0) {
			return "{\"code\":\"1\"}";
		}
		return "{\"code\":\"0\"}";
	}
	
	@RequestMapping(value="/deleteDad")
	@ResponseBody
	public String deleteDad(int prodCatId){
	List<RcProd> rcProdList= prodService.findProdsByProdCatId(prodCatId);
		if(CollectionUtils.isNotEmpty(rcProdList))
			return "{\"code\":\"0\",\"result\":\"Can't delete this category because there are some dishes associated with it.\"}";
		int result = prodCatService.removeProdCatAndChildrenById(prodCatId);

		if(result > 0) {
			return "{\"code\":\"1\"}";
		}
		return "{\"code\":\"0\"}";
	}
	
	@RequestMapping(value="/service")
	@ResponseBody
	public Result<List<RcProdCat>> service(String data,String timestamp,String nonceStr,String product,String signature){
		JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
		Result<List<RcProdCat>>result=new Result<List<RcProdCat>> ();
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
		RcProdCat param = new RcProdCat();
		param.setMerchantId(merchantId);
		List<RcProdCat> prodCats = prodCatService.findprodCatsForApp(param);
		result.setData(prodCats);
		return result;
	}
	
	@RequestMapping(value="/modifydad")
	@ResponseBody
	public String modifydad(int prodCatId, String name, Integer seq) throws SystemException {
        CheckUtil.isBlank(name, "Category Name");
        CheckUtil.tooLong(name, 50, "Category Name");

		RcProdCat prodCat = new RcProdCat();
		prodCat.setProdCatId(prodCatId);
		prodCat.setName(name);
		prodCat.setSeq(seq);
		int result = prodCatService.modifyProdCat(prodCat);
		if(result > 0) {
			return "{\"code\":\"1\"}";
		}
		return "{\"code\":\"0\"}";
	}
	
	@RequestMapping(value="/appmodify")
	@ResponseBody
	public Result<List<RcProdCat>> appModify(String data,String timestamp,String nonceStr,String product,String signature){
		JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
		Result<List<RcProdCat>>result=new Result<List<RcProdCat>> ();
		if(json==null){
			result.setCode("-1");
			result.setResult("token验证失败！");
			return result;
		}
		Integer prodCatId = null;
		try{
			prodCatId = json.getInt("prodCatId");
		}catch(Exception e){
			result.setCode("0");
			result.setResult("prodCatId不能为空！");
			return result;
		}
		String name = null;
		try{
			name = json.getString("name");
		}catch(Exception e){
		}
		Integer seq = null;
		try{
			seq = json.getInt("seq");
		}catch(Exception e){
		}
		RcProdCat prodCat = new RcProdCat();
		prodCat.setProdCatId(prodCatId);
		prodCat.setName(name);
		prodCat.setSeq(seq);
		prodCatService.modifyProdCat(prodCat);
		return result;
	}
	
	@RequestMapping(value="/appremove")
	@ResponseBody
	public Result<List<RcProdCat>> appRemove(String data,String timestamp,String nonceStr,String product,String signature){
		JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
		Result<List<RcProdCat>>result=new Result<List<RcProdCat>> ();
		if(json==null){
			result.setCode("-1");
			result.setResult("token验证失败！");
			return result;
		}
		Integer prodCatId = null;
		try{
			prodCatId = json.getInt("prodCatId");
		}catch(Exception e){
			result.setCode("0");
			result.setResult("prodCatId不能为空！");
			return result;
		}
		prodCatService.removeProdCatAndChildrenById(prodCatId);
		return result;
	}
	
	@RequestMapping(value="/appadd")
	@ResponseBody
	public Result<Map<String,Object>> appAdd(String data,String timestamp,String nonceStr,String product,String signature){
		JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
		Result<Map<String,Object>>result=new Result<Map<String,Object>> ();
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
		String name = null;
		try{
			name = json.getString("name");
		}catch(Exception e){
		}
		Integer seq = 999;
		try{
			seq = json.getInt("seq");
		}catch(Exception e){
		}
		
		RcProdCat prodCat = new RcProdCat();
		prodCat.setName(name);
		prodCat.setSeq(seq);
		prodCat.setMerchantId(merchantId);
		prodCatService.addProdCat(prodCat);
		return result;
	}
	
	@RequestMapping(value="/listprods")
	@ResponseBody
	public Result<Map<String,Object>> listProds(String data,String timestamp,String nonceStr,String product,String signature){
		JSONObject json = parseParam(data, timestamp, nonceStr, product, signature);
		Result<Map<String,Object>>result=new Result<Map<String,Object>> ();
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
//		Integer merchantId = 53;
		Map<String,Object> map = prodCatService.findProdCatsWithProdsForApp(merchantId);
		result.setData(map);
		return result;
	}
}

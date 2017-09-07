package com.socool.soft.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.socool.soft.bo.RcMerchantSection;
import com.socool.soft.bo.RcMerchantUser;
import com.socool.soft.service.IMerchantSectionService;
import com.socool.soft.service.IMerchantUserService;
import com.socool.soft.vo.Page;

@Controller
@RequestMapping(value="/table")
public class SectionAndTableController extends BaseController {

	@Autowired
	private IMerchantSectionService merchantSectionService;
	@Autowired
	private IMerchantUserService merchantUserService;
	
	@RequestMapping(value="/sectionlist")
	public String sectionList(HttpServletRequest request,Integer currentPage,Model model){
		Integer merchantUserId = getMerchantUserId(request);
		RcMerchantUser merchantUser = merchantUserService.findMerchantUserById(merchantUserId);
		RcMerchantSection param = new RcMerchantSection();
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
		List<RcMerchantSection> list = merchantSectionService.findMerchantSections(param);
		model.addAttribute("list", list);
		if(flag)
			return "sectionList";
		else
			return "sectionList_inner";
	}
	
	@RequestMapping(value="/save")
	@ResponseBody
	public String save(HttpServletRequest request,String name,Integer tableNumber){
		Integer merchantUserId = getMerchantUserId(request);
		RcMerchantUser merchantUser = merchantUserService.findMerchantUserById(merchantUserId);
		if(merchantUser==null){
			return "{\"code\":\"0\"}";
		}
		merchantSectionService.addMerchantSectionAndTables(merchantUser.getMerchantId(), name, tableNumber);
		return "{\"code\":\"1\"}";
	}
	
	@RequestMapping(value="/modify")
	@ResponseBody
	public String modify(HttpServletRequest request,Integer sectionId, String name,Integer tableNumber){
		Integer merchantUserId = getMerchantUserId(request);
		RcMerchantUser merchantUser = merchantUserService.findMerchantUserById(merchantUserId);
		if(merchantUser==null){
			return "{\"code\":\"0\"}";
		}
		merchantSectionService.modifyMerchantSectionAndTables(sectionId, name, tableNumber,merchantUser.getMerchantId());
		return "{\"code\":\"1\"}";
	}
	
	@RequestMapping(value="/delete")
	@ResponseBody
	public String modify(Integer sectionId){
		merchantSectionService.removeMerchantSectionAndTables(sectionId);
		return "{\"code\":\"1\"}";
	}
}

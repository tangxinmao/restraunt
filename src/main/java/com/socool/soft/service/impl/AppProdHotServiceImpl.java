package com.socool.soft.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.socool.soft.bo.RcAppProdHot;
import com.socool.soft.bo.RcCoupon;
import com.socool.soft.bo.RcMerchant;
import com.socool.soft.bo.RcProd;
import com.socool.soft.bo.RcProdSku;
import com.socool.soft.bo.RcProdSkuProp;
import com.socool.soft.bo.RcProdSkuPropEnum;
import com.socool.soft.bo.RcProdSkuPropInfo;
import com.socool.soft.bo.constant.CouponDiscountTypeEnum;
import com.socool.soft.bo.constant.CouponStatusEnum;
import com.socool.soft.bo.constant.YesOrNoEnum;
import com.socool.soft.dao.RcAppProdHotMapper;
import com.socool.soft.service.IAppProdHotService;
import com.socool.soft.service.ICouponService;
import com.socool.soft.service.IMerchantService;
import com.socool.soft.service.IProdImgService;
import com.socool.soft.service.IProdService;
import com.socool.soft.service.IProdSkuPropInfoService;
import com.socool.soft.service.IProdSkuService;
import com.socool.soft.vo.Page;
import com.socool.soft.vo.PageContext;

@Service
public class AppProdHotServiceImpl implements IAppProdHotService {
	@Autowired
	private RcAppProdHotMapper appProdHotMapper;
	@Autowired
	private IProdService prodService;
	@Autowired
	private IMerchantService merchantService;
	@Autowired
	private IProdImgService prodImgService;
	@Autowired
	private IProdSkuService prodSkuService;
	@Autowired
	private IProdSkuPropInfoService prodSkuPropInfoService;
	@Autowired
	private ICouponService couponService;

	@Override
	public List<RcAppProdHot> findAppProdHotsByCityId(int cityId) {
		RcAppProdHot param = new RcAppProdHot();
		param.setCityId(cityId);
		param.setOrderBy("SEQ ASC");
		return appProdHotMapper.select(param);
	}
	
	@Override
	public List<RcAppProdHot> findAppProdHotsByCityId(int cityId, int limit) {
		RcAppProdHot param = new RcAppProdHot();
		param.setCityId(cityId);
		param.setOrderBy("SEQ ASC");
		param.setLimit(limit);
		return appProdHotMapper.select(param);
	}

	@Override
	public int addAppProdHot(RcAppProdHot appProdHot) {
		if(appProdHotMapper.insert(appProdHot) > 0) {
			return appProdHot.getAppProdHotId();
		}
		return 0;
	}

	@Override
	public int modifyAppProdHot(RcAppProdHot appProdHot) {
		if(appProdHotMapper.updateByPrimaryKey(appProdHot) > 0) {
			return appProdHot.getAppProdHotId();
		}
		return 0;
	}

	@Override
	public int removeAppProdHot(int appProdHotId) {
		return appProdHotMapper.deleteByPrimaryKey(appProdHotId);
	}

	@Override
	public List<RcAppProdHot> findPagedAppProdHotsByCityId(RcAppProdHot param, Page page) {
		PageContext.setPage(page);
		param.setOrderBy("SEQ ASC");
		List<RcAppProdHot> appProdHots = appProdHotMapper.select(param);
		for(RcAppProdHot appProdHot : appProdHots){
			RcProd prod = prodService.findProdById(appProdHot.getProdId());
			prod.setProdImgUrl(prodImgService.findFirstProdImgByProdSkuId(String.valueOf(prod.getProdId())).getImgUrl());
			RcMerchant merchant = merchantService.findMerchantById(prod.getMerchantId());
			prod.setMerchant(merchant);
			appProdHot.setProd(prod);
//			RcCity city = cityService.findCityById(appProdHot.getCityId());
//			appProdHot.setCity(city);
		}
		return appProdHots;
	}

	@Override
	public List<RcAppProdHot> findAppProdHots(RcAppProdHot param) {
		param.setOrderBy("SEQ ASC");
		List<RcAppProdHot> appProdHots = appProdHotMapper.select(param);
		if(!CollectionUtils.isEmpty(appProdHots)) {
			RcCoupon couponParam = new RcCoupon();
			couponParam.setReceiveEndTimeFrom(new Date());
			couponParam.setNeedGet(YesOrNoEnum.NO.getValue());
			couponParam.setStatus(CouponStatusEnum.PUBLISHED.getValue());
			couponParam.setDelFlag(YesOrNoEnum.NO.getValue());
			couponParam.setMerchantId(param.getMerchantId());
			List<RcCoupon> coupons = couponService.findCoupons(couponParam);
			for(RcAppProdHot appProdHot : appProdHots){
				RcProd prod = prodService.findProdById(appProdHot.getProdId());
	            
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
				List<RcProdSku> prodSkus = prodSkuService.findProdSkusByProdId(prod.getProdId());
				
				List<RcProdSkuPropInfo> prodSkuPropInfos = prodSkuPropInfoService.findProdSkuPropInfosByProdId(prod.getProdId());
				Map<Integer, List<RcProdSkuPropInfo>> prodSkuPropInfoGroups = new HashMap<Integer, List<RcProdSkuPropInfo>>();
				for(RcProdSkuPropInfo prodSkuPropInfo : prodSkuPropInfos) {
					if(!prodSkuPropInfoGroups.containsKey(prodSkuPropInfo.getProdPropId())) {
						prodSkuPropInfoGroups.put(prodSkuPropInfo.getProdPropId(), new ArrayList<RcProdSkuPropInfo>());
					}
					prodSkuPropInfoGroups.get(prodSkuPropInfo.getProdPropId()).add(prodSkuPropInfo);
				}
				
				List<RcProdSkuProp> prodSkuInfos = new ArrayList<RcProdSkuProp>();
				Iterator<Entry<Integer, List<RcProdSkuPropInfo>>> iter = prodSkuPropInfoGroups.entrySet().iterator();
				while(iter.hasNext()) {
					Entry<Integer, List<RcProdSkuPropInfo>> entry = iter.next();
					Integer prodSkuPropId = entry.getKey();
					List<RcProdSkuPropInfo> skuInfos = entry.getValue();
					if(CollectionUtils.isEmpty(skuInfos)) {
						continue;
					}
					
					RcProdSkuProp prodSkuProp = new RcProdSkuProp();
					prodSkuProp.setProdPropId(prodSkuPropId);
					prodSkuProp.setName(skuInfos.get(0).getProdPropName());
					prodSkuProp.setHasImg(skuInfos.get(0).getHasImg());
					
					List<RcProdSkuPropEnum> prodSkuPropEnums = new ArrayList<RcProdSkuPropEnum>();
					Map<Integer, String> skuPropEnums = new HashMap<Integer, String>();
					for(RcProdSkuPropInfo skuInfo : skuInfos) {
						skuPropEnums.put(skuInfo.getProdPropEnumId(), skuInfo.getProdPropVal());
					}
					Iterator<Entry<Integer, String>> skuPropEnumIter = skuPropEnums.entrySet().iterator();
					while(skuPropEnumIter.hasNext()) {
						Entry<Integer, String> skuPropEnum = skuPropEnumIter.next();
						RcProdSkuPropEnum prodSkuPropEnum = new RcProdSkuPropEnum();
						prodSkuPropEnum.setProdPropEnumId(skuPropEnum.getKey());
						prodSkuPropEnum.setProdPropEnum(skuPropEnum.getValue());
						prodSkuPropEnums.add(prodSkuPropEnum);
					}
					prodSkuProp.setProdPropEnums(prodSkuPropEnums);
					
					prodSkuInfos.add(prodSkuProp);
				}
				prod.setSkuCount(prodSkus.size());
				prod.setProdSkuInfos(prodSkuInfos);
				prod.setProdImgUrl(prodImgService.findFirstProdImgByProdSkuId(String.valueOf(prod.getProdId())).getImgUrl());
				RcMerchant merchant = merchantService.findMerchantById(prod.getMerchantId());
				prod.setMerchant(merchant);
				appProdHot.setProd(prod);
//				RcCity city = cityService.findCityById(appProdHot.getCityId());
//				appProdHot.setCity(city);
			}
		}
		return appProdHots;
	}

	@Override
	public int removeAppProdHots(RcAppProdHot param) {
		return appProdHotMapper.delete(param);
	}

	@Override
	public List<RcAppProdHot> findBaseAppProdHots(RcAppProdHot param) {
		return appProdHotMapper.select(param);
	}
}

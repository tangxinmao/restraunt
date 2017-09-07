package com.socool.soft.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.socool.soft.bo.RcAppProdHot;
import com.socool.soft.bo.RcCoupon;
import com.socool.soft.bo.RcProd;
import com.socool.soft.bo.RcProdCat;
import com.socool.soft.bo.constant.CouponDiscountTypeEnum;
import com.socool.soft.bo.constant.CouponStatusEnum;
import com.socool.soft.bo.constant.ProdStatusEnum;
import com.socool.soft.bo.constant.YesOrNoEnum;
import com.socool.soft.dao.RcProdCatMapper;
import com.socool.soft.service.IAppProdHotService;
import com.socool.soft.service.ICouponService;
import com.socool.soft.service.IProdCatService;
import com.socool.soft.service.IProdService;
import com.socool.soft.util.VOConversionUtil;
import com.socool.soft.vo.Page;
import com.socool.soft.vo.PageContext;

@Service
public class ProdCatServiceImpl implements IProdCatService {
	@Autowired
	private RcProdCatMapper prodCatMapper;
	@Autowired
	private IProdService prodService;
	@Autowired
	private ICouponService couponService;
	@Autowired
	private IAppProdHotService appProdHotService;

	@Override
	public List<RcProdCat> findProdCats(RcProdCat param) {
		// TODO Auto-generated method stub
		param.setOrderBy("SEQ ASC");
		return prodCatMapper.select(param);
	}
	
	@Override
	public List<RcProdCat> findTopProdCats() {
		return findChildProdCats(0);
	}

	@Override
	public List<RcProdCat> findPagedTopProdCatsWithChildren(RcProdCat param, Page page) {
		PageContext.setPage(page);
		param.setParentId(0);
		param.setOrderBy("SEQ ASC");
		List<RcProdCat> prodCats = prodCatMapper.select(param);
		for(RcProdCat prodCat : prodCats){
			List<RcProdCat> childProdCats = findChildProdCats(prodCat.getProdCatId());
			prodCat.setChildProdCats(childProdCats);
		}
		return prodCats;
	}

	@Override
	public List<RcProdCat> findProdCatsWithProds(RcProdCat param) {
		param.setParentId(0);
		param.setOrderBy("SEQ ASC");
		List<RcProdCat> prodCats = prodCatMapper.select(param);
		if(!CollectionUtils.isEmpty(prodCats)) {
			RcCoupon couponParam = new RcCoupon();
			couponParam.setReceiveEndTimeFrom(new Date());
			couponParam.setNeedGet(YesOrNoEnum.NO.getValue());
			couponParam.setStatus(CouponStatusEnum.PUBLISHED.getValue());
			couponParam.setDelFlag(YesOrNoEnum.NO.getValue());
			couponParam.setMerchantId(param.getMerchantId());
			List<RcCoupon> coupons = couponService.findCoupons(couponParam);
			for(RcProdCat prodCat : prodCats){
				RcProd prodParam = new RcProd();
				prodParam.setDelFlag(YesOrNoEnum.NO.getValue());
				prodParam.setProdCatId(prodCat.getProdCatId());
				prodParam.setMerchantId(prodCat.getMerchantId());
				prodParam.setStatuses(Arrays.asList(ProdStatusEnum.SELLING.getValue(), ProdStatusEnum.SOLD_OUT.getValue()));
				List<RcProd> prods = prodService.findProds(prodParam);
				for(RcProd prod :prods){
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
				}
				prodCat.setProds(prods);
			}
		}
		return prodCats;
	}
	
	@Override
	public Map<String,Object> findProdCatsWithProdsForApp(int merchantId) {
		RcProdCat prodCatParam = new RcProdCat();
		prodCatParam.setMerchantId(merchantId);
		prodCatParam.setParentId(0);
		prodCatParam.setOrderBy("SEQ ASC");
		List<RcProdCat> prodCats = prodCatMapper.select(prodCatParam);
		if(!CollectionUtils.isEmpty(prodCats)) {
			for(RcProdCat prodCat : prodCats){
				RcProd prodParam = new RcProd();
				prodParam.setDelFlag(YesOrNoEnum.NO.getValue());
				prodParam.setProdCatId(prodCat.getProdCatId());
				prodParam.setMerchantId(prodCat.getMerchantId());
				List<RcProd> prods = prodService.findProds(prodParam);
				for(RcProd prod :prods){
					prod.setProdName(prod.getName());
					RcAppProdHot paramHot = new RcAppProdHot();
					paramHot.setProdId(prod.getProdId());
					List<RcAppProdHot> appProdHot = appProdHotService.findAppProdHots(paramHot);
					if(CollectionUtils.isEmpty(appProdHot)){
						prod.setIsHot(0);
					}
					else{
						prod.setIsHot(1);
					}
					VOConversionUtil.Entity2VO(prod, null, new String[] {"originPrice",
							"seq","merchantId","prodCatId","type","delFlag","score","createTime",
							"updateTime","putawayTime","soldoutTime","vendorId","cityId","baseProdId",
							"prodBrandId","prodSkus","prodSkuInfos","measureUnitCount","prodBrandName","skuCount"});
				}
				VOConversionUtil.Entity2VO(prodCat, null, new String[] {"seq","icon","parentId","merchantId"});
				prodCat.setProdCount(prods.size());
				prodCat.setProds(prods);
			}
		}
		RcAppProdHot hotParam = new RcAppProdHot();
		hotParam.setMerchantId(merchantId);
		List<RcAppProdHot> hotlist = appProdHotService.findBaseAppProdHots(hotParam);
		List<RcProd> hots = new ArrayList<RcProd>();
		for(RcAppProdHot hot: hotlist){
			RcProd prod = prodService.findProdById(hot.getProdId());
			prod.setIsHot(YesOrNoEnum.YES.getValue());
			VOConversionUtil.Entity2VO(prod, null, new String[] {"originPrice",
					"seq","merchantId","prodCatId","type","delFlag","score","createTime",
					"updateTime","putawayTime","soldoutTime","vendorId","cityId","baseProdId",
					"prodBrandId","prodSkus","prodSkuInfos","measureUnitCount","prodBrandName","skuCount"});
			hots.add(prod);
		}
		Map<String,Object> result = new HashMap<String, Object>();
		result.put("hots", hots);
		result.put("hotMenu", "MAKANAN HOT");
		result.put("category", prodCats);
		return result;
	}

	@Override
	public List<RcProdCat> findChildProdCats(int parentId) {
		RcProdCat param = new RcProdCat();
		param.setParentId(parentId);
		param.setOrderBy("SEQ ASC");
		return prodCatMapper.select(param);
	}

	@Override
	public int addProdCat(RcProdCat prodCat) {
		if(prodCat.getParentId() == null) {
			prodCat.setParentId(0);
		}
		if(prodCat.getSeq() == null) {
			prodCat.setSeq(99);
		}
		if(prodCatMapper.insert(prodCat) > 0) {
			return prodCat.getProdCatId();
		}
		return 0;
	}

	@Override
	public int modifyProdCat(RcProdCat prodCat) {
		if(prodCatMapper.updateByPrimaryKey(prodCat) > 0) {
			return prodCat.getProdCatId();
		}
		return 0;
	}

	@Override
	public int removeProdCatAndChildrenById(int prodCatId) {
		int result = prodCatMapper.deleteByPrimaryKey(prodCatId);
		if(result > 0) {
			RcProdCat param = new RcProdCat();
			param.setParentId(prodCatId);
			prodCatMapper.delete(param);
		}
		return result;
	}

	@Override
	public RcProdCat findProdCatById(int prodCatId) {
		return prodCatMapper.selectByPrimaryKey(prodCatId);
	}

	@Override
	public List<RcProdCat> findTopProdCatsWithChildrenForAndroid() {
		List<RcProdCat> prodCats = findTopProdCats();
		for(RcProdCat prodCat : prodCats){
			List<RcProdCat> childProdCats = findChildProdCats(prodCat.getProdCatId());
			for(RcProdCat childProdCat : childProdCats){
				VOConversionUtil.Entity2VO(childProdCat, null, new String[] {"seq"});
			}
			prodCat.setChildProdCats(childProdCats);
			VOConversionUtil.Entity2VO(prodCat, null, new String[] {"seq"});
		}
		return prodCats;
	}

	@Override
	public List<Integer> findProdCatIdsByProdIds(Collection<Integer> prodIds) {
		List<Integer> prodCatIds = new ArrayList<Integer>();
		for(Integer prodId : prodIds) {
			RcProd prod = prodService.findProdById(prodId);
			int prodCatId = prod.getProdCatId();
			if(prod != null && !prodCatIds.contains(prodCatId)) {
				prodCatIds.add(prodCatId);
			}
		}
		return prodCatIds;
	}

	@Override
	public List<RcProdCat> findprodCatsForApp(RcProdCat param) {
		param.setOrderBy("SEQ ASC");
		List<RcProdCat> list = prodCatMapper.select(param);
		for(RcProdCat e:list){
			VOConversionUtil.Entity2VO(e, null, new String[] {"icon","parentId","merchantId"});
		}
		return prodCatMapper.select(param);
	}

}

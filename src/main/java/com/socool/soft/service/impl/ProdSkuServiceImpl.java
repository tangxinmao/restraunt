package com.socool.soft.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.socool.soft.bo.RcCoupon;
import com.socool.soft.bo.RcProdSku;
import com.socool.soft.bo.constant.CouponDiscountTypeEnum;
import com.socool.soft.bo.constant.CouponStatusEnum;
import com.socool.soft.bo.constant.YesOrNoEnum;
import com.socool.soft.dao.RcProdSkuMapper;
import com.socool.soft.service.ICouponService;
import com.socool.soft.service.IProdSkuService;

@Service
public class ProdSkuServiceImpl implements IProdSkuService {
	@Autowired
	private RcProdSkuMapper prodSkuMapper;
	@Autowired
	private ICouponService couponService;
	@Override
	public RcProdSku findProdSkuByProdSkuId(String prodSkuId) {
		return prodSkuMapper.selectByPrimaryKey(prodSkuId);
		
	}

	@Override
	public List<RcProdSku> findProdSkusByProdId(int prodId) {
		RcProdSku param = new RcProdSku();
		param.setProdId(prodId);
		return prodSkuMapper.select(param);
	}

	@Override
	public String addProdSku(RcProdSku prodSku) {
		if(prodSkuMapper.insert(prodSku) > 0) {
			return prodSku.getProdSkuId();
		}
		return null;
	}

	@Override
	public int removeProdSkusByProdId(int prodId) {
		RcProdSku param = new RcProdSku();
		param.setProdId(prodId);
		return prodSkuMapper.delete(param);
	}

	@Override
	public String modifyProdSku(RcProdSku prodSku) {
		if(prodSkuMapper.updateByPrimaryKey(prodSku) > 0) {
			return prodSku.getProdSkuId();
		}
		return null;
	}

	@Override
	public RcProdSku findProdSkuWithCoupon(String prodSkuId, int merchantId, int prodCatId) {
		RcProdSku prodSku = findProdSkuByProdSkuId(prodSkuId);
		RcCoupon couponParam = new RcCoupon();
		couponParam.setReceiveEndTimeFrom(new Date());
		couponParam.setNeedGet(YesOrNoEnum.NO.getValue());
		couponParam.setStatus(CouponStatusEnum.PUBLISHED.getValue());
		couponParam.setDelFlag(YesOrNoEnum.NO.getValue());
		couponParam.setMerchantId(merchantId);
		List<RcCoupon> coupons = couponService.findCoupons(couponParam);

        List<RcCoupon> prodCoupons = new ArrayList<RcCoupon>();
        List<RcCoupon> prodCatCoupons = new ArrayList<RcCoupon>();
        for(RcCoupon coupon : coupons) {
        	if(coupon.getProdId() > 0 && coupon.getProdId().equals(prodSku.getProdId())) {
        		prodCoupons.add(coupon);
        		continue;
        	}
        	if(coupon.getProdId() == 0 && (coupon.getProdCatId() == 0 || coupon.getProdCatId() > 0 && coupon.getProdCatId().equals(prodCatId))) {
        		prodCatCoupons.add(coupon);
        		continue;
        	}
        }
        
        if(!CollectionUtils.isEmpty(prodCoupons)) {
        	RcCoupon coupon = prodCoupons.get(0);
        	prodSku.setOriginPrice(prodSku.getPrice());
			if(coupon.getDiscountType() == CouponDiscountTypeEnum.PERCENTAGE.getValue()) {
				prodSku.setPrice(prodSku.getPrice().multiply(coupon.getAmount()));
			} else if(coupon.getDiscountType() == CouponDiscountTypeEnum.SUBSTRACT.getValue()) {
				prodSku.setPrice(prodSku.getPrice().subtract(coupon.getAmount()));
			} else if(coupon.getDiscountType() == CouponDiscountTypeEnum.FIXED.getValue()) {
				prodSku.setPrice(coupon.getAmount());
			}
        } else if(!CollectionUtils.isEmpty(prodCatCoupons)) {
        	RcCoupon coupon = prodCatCoupons.get(0);
        	prodSku.setOriginPrice(prodSku.getPrice());
			if(coupon.getDiscountType() == CouponDiscountTypeEnum.PERCENTAGE.getValue()) {
				prodSku.setPrice(prodSku.getPrice().multiply(coupon.getAmount()));
			} else if(coupon.getDiscountType() == CouponDiscountTypeEnum.SUBSTRACT.getValue()) {
				prodSku.setPrice(prodSku.getPrice().subtract(coupon.getAmount()));
			} else if(coupon.getDiscountType() == CouponDiscountTypeEnum.FIXED.getValue()) {
				prodSku.setPrice(coupon.getAmount());
			}
        }
		return prodSku;
	}
}

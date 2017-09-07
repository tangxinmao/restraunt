package com.socool.soft.service.impl;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.socool.soft.bo.RcCity;
import com.socool.soft.bo.RcCoupon;
import com.socool.soft.bo.RcMerchant;
import com.socool.soft.bo.RcProd;
import com.socool.soft.bo.RcProdBrand;
import com.socool.soft.bo.RcProdCat;
import com.socool.soft.bo.RcProdSku;
import com.socool.soft.bo.RcProdSkuProp;
import com.socool.soft.bo.RcProdSkuPropEnum;
import com.socool.soft.bo.RcProdSkuPropInfo;
import com.socool.soft.bo.constant.CouponDiscountTypeEnum;
import com.socool.soft.bo.constant.CouponStatusEnum;
import com.socool.soft.bo.constant.ProdStatusEnum;
import com.socool.soft.bo.constant.YesOrNoEnum;
import com.socool.soft.dao.RcProdMapper;
import com.socool.soft.service.ICityService;
import com.socool.soft.service.ICouponService;
import com.socool.soft.service.IMerchantService;
import com.socool.soft.service.IProdBrandService;
import com.socool.soft.service.IProdCatService;
import com.socool.soft.service.IProdImgService;
import com.socool.soft.service.IProdService;
import com.socool.soft.service.IProdSkuPropInfoService;
import com.socool.soft.service.IProdSkuService;
import com.socool.soft.service.IVendorService;
import com.socool.soft.vo.Page;
import com.socool.soft.vo.PageContext;

@Service
public class ProdServiceImpl implements IProdService {
    @Autowired
    private RcProdMapper prodMapper;
    @Autowired
    private IProdBrandService prodBrandService;
    @Autowired
    private IMerchantService merchantService;
    @Autowired
    private IProdCatService prodCatService;
    @Autowired
    private IProdSkuService prodSkuService;
    @Autowired
    private ICityService cityService;
    @Autowired
    private IProdImgService prodImgService;
    @Autowired
    private IVendorService vendorService;
    @Autowired
    private IProdSkuPropInfoService prodSkuPropInfoService;
    @Autowired
    private ICouponService couponService;


    @Override
    public RcProd findProdById(int prodId) {
    	RcProd prod = prodMapper.selectByPrimaryKey(prodId);
    	RcCoupon couponParam = new RcCoupon();
		couponParam.setReceiveEndTimeFrom(new Date());
		couponParam.setNeedGet(YesOrNoEnum.NO.getValue());
		couponParam.setStatus(CouponStatusEnum.PUBLISHED.getValue());
		couponParam.setDelFlag(YesOrNoEnum.NO.getValue());
		couponParam.setMerchantId(prod.getMerchantId());
		List<RcCoupon> coupons = couponService.findCoupons(couponParam);
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
    	if(prod!=null)
	    prod.setProdImgUrl(prodImgService.findFirstProdImgByProdSkuId(String.valueOf(prod.getProdId())).getImgUrl());
        return prod;
    }

//	@Override
//	public RcProdCat findProdCatById(int prodCatId) {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	@Override
//	public RcProdImgRel findFirstImgByProdId(int prodId) {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	@Override
//	public List<RcProd> findProdByProdIds(List<Integer> prodIds) {
//		// TODO Auto-generated method stub
//		return null;
//	}

    @Override
    public int modifyProd(RcProd prod) {
        prod.setUpdateTime(new Date());
        if (prodMapper.updateByPrimaryKey(prod) > 0) {
            return prod.getProdId();
        }
        return 0;
    }

    @Override
    public int removeProd(int prodId) {
        RcProd param = new RcProd();
        param.setProdId(prodId);
        param.setStatus(ProdStatusEnum.NOT_SELLING.getValue());
        param.setDelFlag(YesOrNoEnum.YES.getValue());
        if (modifyProd(param) > 0) {
            return 1;
        }
        return 0;
    }


    @Override
    public List<RcProd> findProds(RcProd param) {
        param.setDelFlag(YesOrNoEnum.NO.getValue());
        List<RcProd> list = prodMapper.select(param);
        for (RcProd prod : list) {
        	
            prod.setProdImgUrl(prodImgService.findFirstProdImgByProdSkuId(String.valueOf(prod.getProdId())).getImgUrl());
            List<RcProdSku> prodSkus = prodSkuService.findProdSkusByProdId(prod.getProdId());

            List<RcProdSkuPropInfo> prodSkuPropInfos = prodSkuPropInfoService.findProdSkuPropInfosByProdId(prod.getProdId());
            Map<Integer, List<RcProdSkuPropInfo>> prodSkuPropInfoGroups = new HashMap<Integer, List<RcProdSkuPropInfo>>();
            for (RcProdSkuPropInfo prodSkuPropInfo : prodSkuPropInfos) {
                if (!prodSkuPropInfoGroups.containsKey(prodSkuPropInfo.getProdPropId())) {
                    prodSkuPropInfoGroups.put(prodSkuPropInfo.getProdPropId(), new ArrayList<RcProdSkuPropInfo>());
                }
                prodSkuPropInfoGroups.get(prodSkuPropInfo.getProdPropId()).add(prodSkuPropInfo);
            }

            List<RcProdSkuProp> prodSkuInfos = new ArrayList<RcProdSkuProp>();
            Iterator<Entry<Integer, List<RcProdSkuPropInfo>>> iter = prodSkuPropInfoGroups.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<Integer, List<RcProdSkuPropInfo>> entry = iter.next();
                Integer prodSkuPropId = entry.getKey();
                List<RcProdSkuPropInfo> skuInfos = entry.getValue();
                if (CollectionUtils.isEmpty(skuInfos)) {
                    continue;
                }

                RcProdSkuProp prodSkuProp = new RcProdSkuProp();
                prodSkuProp.setProdPropId(prodSkuPropId);
                prodSkuProp.setName(skuInfos.get(0).getProdPropName());
                prodSkuProp.setHasImg(skuInfos.get(0).getHasImg());

                List<RcProdSkuPropEnum> prodSkuPropEnums = new ArrayList<RcProdSkuPropEnum>();
                Map<Integer, String> skuPropEnums = new HashMap<Integer, String>();
                for (RcProdSkuPropInfo skuInfo : skuInfos) {
                    skuPropEnums.put(skuInfo.getProdPropEnumId(), skuInfo.getProdPropVal());
                }
                Iterator<Entry<Integer, String>> skuPropEnumIter = skuPropEnums.entrySet().iterator();
                while (skuPropEnumIter.hasNext()) {
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
        }
        return list;
    }


    @Override
    public List<RcProd> findPagedProds(RcProd param, Page page) {
        if (StringUtils.isNotBlank(param.getMerchantName()) || param.getVendorId() != null) {
            RcMerchant merchantParam = new RcMerchant();
            merchantParam.setName(param.getMerchantName());
            merchantParam.setVendorId(param.getVendorId());
            List<RcMerchant> merchants = merchantService.findMerchants(merchantParam);
            if (CollectionUtils.isEmpty(merchants)) {
                return new ArrayList<RcProd>();
            }
            List<Integer> merchantIds = new ArrayList<Integer>();
            for (RcMerchant merchant : merchants) {
                merchantIds.add(merchant.getMerchantId());
            }
            param.setMerchantIds(merchantIds);
        }
        if (StringUtils.isNotBlank(param.getCityName())) {
            List<RcCity> cities = cityService.findCitiesByName(param.getCityName());
            if (CollectionUtils.isEmpty(cities)) {
                return new ArrayList<RcProd>();
            }
            List<Integer> cityIds = new ArrayList<Integer>();
            for (RcCity city : cities) {
                cityIds.add(city.getCityId());
            }
            param.setCityIds(cityIds);
        }
        param.setDelFlag(YesOrNoEnum.NO.getValue());
        PageContext.setPage(page);
        List<RcProd> prods = null;
        if (param.getProdId() != null) {
            RcProd prod = findProdById(param.getProdId());
            if (prod == null) {
                prods = new ArrayList<RcProd>();
            } else {
                if((param.getStatus()==null||prod.getStatus().equals(param.getStatus()))&&(StringUtils.isEmpty(param.getName())||prod.getName().equals(param.getName())))
                prods = Arrays.asList(prod);
                else
                    prods = new ArrayList<RcProd>();
            }
        } else {
            prods = prodMapper.select(param);
        }
        for (RcProd prod : prods) {
            prod.setMerchant(merchantService.findMerchantById(prod.getMerchantId()));
            prod.setCity(cityService.findCityById(prod.getCityId()));
            prod.setVendor(vendorService.findVendorById(prod.getVendorId()));
            prod.setProdImgUrl(prodImgService.findFirstProdImgByProdSkuId(String.valueOf(prod.getProdId())).getImgUrl());
            prod.setProdSkus(prodSkuService.findProdSkusByProdId(prod.getProdId()));
            prod.setProdBrand(prodBrandService.findProdBrandById(prod.getProdBrandId()));
        }
        return prods;
    }

    @Override
    public int addProd(RcProd prod) {
        prod.setCreateTime(new Date());
        if (prodMapper.insert(prod) > 0) {
            return prod.getProdId();
        }
        return 0;
    }

    @Override
    public RcProd findProdForSolr(int prodId) {
        RcProd prod = findProdById(prodId);
        if (prod.getProdBrandId() > 0) {
            RcProdBrand prodBrand = prodBrandService.findProdBrandById(prod.getProdBrandId());
            prod.setProdBrand(prodBrand);
        }
        RcMerchant merchant = merchantService.findMerchantById(prod.getMerchantId());
        prod.setMerchant(merchant);
        RcProdCat prodCat = prodCatService.findProdCatById(prod.getProdCatId());
        prod.setProdCat(prodCat);
        List<RcProdSku> prodSkus = prodSkuService.findProdSkusByProdId(prodId);
        prod.setSkuCount(prodSkus.size());
        RcCity city = cityService.findCityById(prod.getCityId());
        prod.setCity(city);
        prod.setProdImgUrl(prodImgService.findFirstProdImgByProdSkuId(String.valueOf(prod.getProdId())).getImgUrl());
        return prod;
    }

    @Override
    public List<RcProd> findProdsForSolr() {
        RcProd param = new RcProd();
        param.setStatus(ProdStatusEnum.SELLING.getValue());
        param.setDelFlag(YesOrNoEnum.NO.getValue());
        List<RcProd> prods = prodMapper.select(param);
        for (RcProd prod : prods) {
            if (prod.getProdBrandId() > 0) {
                RcProdBrand prodBrand = prodBrandService.findProdBrandById(prod.getProdBrandId());
                prod.setProdBrand(prodBrand);
            }
            RcMerchant merchant = merchantService.findMerchantById(prod.getMerchantId());
            prod.setMerchant(merchant);
            RcProdCat prodCat = prodCatService.findProdCatById(prod.getProdCatId());
            prod.setProdCat(prodCat);
            List<RcProdSku> prodSkus = prodSkuService.findProdSkusByProdId(prod.getProdId());
            prod.setSkuCount(prodSkus.size());
            RcCity city = cityService.findCityById(prod.getCityId());
            prod.setCity(city);
            prod.setProdImgUrl(prodImgService.findFirstProdImgByProdSkuId(String.valueOf(prod.getProdId())).getImgUrl());
        }
        return prods;
    }

    @Override
    public RcProd findOneByProdIdAndProdSkuId(Integer prodId, String prodSkuId) {
 /*       RcProd rcProd= findProdById(prodId);
       //获取图片
        if(StringUtils.isEmpty(prodSkuId)){
            RcProdImg  prodImg = prodImgService.findFirstProdImgByProdSkuId(String.valueOf(prodId));
            rcProd.setProdImgUrl(prodImg.getImgUrl());
        }else{
            RcProdImg prodImg = prodImgService.findFirstProdImgByProdSkuId(prodSkuId);
            rcProd.setProdImgUrl(prodImg.getImgUrl());
            //获取sku信息
            String[] prodPropEnumIds = prodSkuId.split("_");
            rcProd.setProdSkuPropInfos(new ArrayList<>());
            for(int i = 1; i < prodPropEnumIds.length; i++) {
                RcProdSkuPropInfo prodSkuPropInfo = prodSkuPropInfoService.findProdSkuPropInfoByProdIdAndProdSkuPropEnumId(prodId, Integer.parseInt(prodPropEnumIds[i]));
                rcProd.getProdSkuPropInfos().add(prodSkuPropInfo);
            }
        }
*/


        //假数据
        RcProd rcProd = new RcProd();
        rcProd.setMerchantId(1);
        rcProd.setPrice(new BigDecimal(100));
        rcProd.setVendorId(1);
        rcProd.setOriginPrice(new BigDecimal(0));
        rcProd.setName("124");
        return rcProd;
    }

    @Override
    public List<RcProd> findProdsByProdCatId(int prodCatId) {
        RcProd rcProd=new RcProd();
        rcProd.setProdCatId(prodCatId);
        rcProd.setDelFlag(YesOrNoEnum.NO.getValue());
        List<RcProd> rcProdList=prodMapper.select(rcProd);
        return rcProdList;
    }
}

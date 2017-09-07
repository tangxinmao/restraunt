package com.socool.soft.bo;

import java.io.Serializable;
import java.math.BigDecimal;

public class RcAppProdHot extends QueryParam implements Serializable {

	private static final long serialVersionUID = 8046740645837459846L;
	
	// db
	private Integer appProdHotId;
	private Integer prodId;
	private Integer seq;
	private Integer cityId;
	private Integer merchantId;
	
	// rel
	private RcProd prod;
	private RcCity city;
	
	// vo
	private String prodCatName;
	private String prodImgUrl;
	private String prodBrandLogoUrl;
	private String prodBrandName;
	private String prodName;
	private BigDecimal originPrice;
	private BigDecimal price;
	private Integer type;
	private Float score;
	private String merchantName;

	public Integer getAppProdHotId() {
		return appProdHotId;
	}

	public void setAppProdHotId(Integer appProdHotId) {
		this.appProdHotId = appProdHotId;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Integer getProdId() {
		return prodId;
	}

	public void setProdId(Integer prodId) {
		this.prodId = prodId;
	}

	public RcProd getProd() {
		return prod;
	}

	public void setProd(RcProd prod) {
		this.prod = prod;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public RcCity getCity() {
		return city;
	}

	public void setCity(RcCity city) {
		this.city = city;
	}

	public String getProdCatName() {
		return prodCatName;
	}

	public void setProdCatName(String prodCatName) {
		this.prodCatName = prodCatName;
	}

	public String getProdImgUrl() {
		return prodImgUrl;
	}

	public void setProdImgUrl(String prodImgUrl) {
		this.prodImgUrl = prodImgUrl;
	}

	public String getProdBrandLogoUrl() {
		return prodBrandLogoUrl;
	}

	public void setProdBrandLogoUrl(String prodBrandLogoUrl) {
		this.prodBrandLogoUrl = prodBrandLogoUrl;
	}

	public String getProdBrandName() {
		return prodBrandName;
	}

	public void setProdBrandName(String prodBrandName) {
		this.prodBrandName = prodBrandName;
	}

	public String getProdName() {
		return prodName;
	}

	public void setProdName(String prodName) {
		this.prodName = prodName;
	}

	public BigDecimal getOriginPrice() {
		return originPrice;
	}

	public void setOriginPrice(BigDecimal originPrice) {
		this.originPrice = originPrice;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Float getScore() {
		return score;
	}

	public void setScore(Float score) {
		this.score = score;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public Integer getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Integer merchantId) {
		this.merchantId = merchantId;
	}
	
	
}
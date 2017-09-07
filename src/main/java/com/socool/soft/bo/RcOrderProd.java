package com.socool.soft.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.socool.soft.jsonSerializer.BigDecimalSerializer;

public class RcOrderProd extends QueryParam implements Serializable {

	private static final long serialVersionUID = -7560212661915280496L;
	
	// db
	private Long orderProdId;
	private Long orderId;
	private Integer prodId;
	private Float quantity;
	@JsonSerialize(using = BigDecimalSerializer.class)
	private BigDecimal amount;
	@JsonSerialize(using = BigDecimalSerializer.class)
	private BigDecimal prodOriginPrice;
	@JsonSerialize(using = BigDecimalSerializer.class)
	private BigDecimal prodPrice;
	private String prodSkuId;
	private String prodName;
	private Integer priceManner;//类型 2 计量 1计件
	private String measureUnit;
	private Integer measureUnitCount;
	private Integer status;

	// rel
	private RcProd prod;
	private RcProdSku prodSku;
	private List<RcOrderProdSkuPropInfo> orderProdSkuPropInfos;
	private RcOrderProdEvaluation evaluation;
	
	//vo
	private String prodImgUrl;
	private List<String> prodPropVals;
	private Integer score;
	private String merchantReply;
	private String content;
	private BigDecimal couponAmount;//卡券 优惠
	private Integer discountType;//1=按百分比折扣；2=按价格折扣',
	private Integer couponType;//coupon类型1=按分类折扣；2=按商品折扣',
	
	// se
	private List<Long> orderIds;
	private List<Integer> prodIds;

	public BigDecimal getCouponAmount() {
		return couponAmount;
	}

	public void setCouponAmount(BigDecimal couponAmount) {
		this.couponAmount = couponAmount;
	}

	public Integer getDiscountType() {
		return discountType;
	}

	public void setDiscountType(Integer discountType) {
		this.discountType = discountType;
	}

	public Integer getCouponType() {
		return couponType;
	}

	public void setCouponType(Integer couponType) {
		this.couponType = couponType;
	}

	public Long getOrderProdId() {
		return orderProdId;
	}

	public void setOrderProdId(Long orderProdId) {
		this.orderProdId = orderProdId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Float getQuantity() {
		return quantity;
	}

	public void setQuantity(Float quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getProdOriginPrice() {
		return prodOriginPrice;
	}

	public void setProdOriginPrice(BigDecimal prodOriginPrice) {
		this.prodOriginPrice = prodOriginPrice;
	}

	public BigDecimal getProdPrice() {
		return prodPrice;
	}

	public void setProdPrice(BigDecimal prodPrice) {
		this.prodPrice = prodPrice;
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

	public String getProdSkuId() {
		return prodSkuId;
	}

	public void setProdSkuId(String prodSkuId) {
		this.prodSkuId = prodSkuId;
	}

	public RcProdSku getProdSku() {
		return prodSku;
	}

	public void setProdSku(RcProdSku prodSku) {
		this.prodSku = prodSku;
	}

	public List<RcOrderProdSkuPropInfo> getOrderProdSkuPropInfos() {
		return orderProdSkuPropInfos;
	}

	public void setOrderProdSkuPropInfos(
			List<RcOrderProdSkuPropInfo> orderProdSkuPropInfos) {
		this.orderProdSkuPropInfos = orderProdSkuPropInfos;
	}

	public RcOrderProdEvaluation getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(RcOrderProdEvaluation evaluation) {
		this.evaluation = evaluation;
	}

	public List<Long> getOrderIds() {
		return orderIds;
	}

	public void setOrderIds(List<Long> orderIds) {
		this.orderIds = orderIds;
	}

	public String getProdName() {
		return prodName;
	}

	public void setProdName(String prodName) {
		this.prodName = prodName;
	}

	public String getProdImgUrl() {
		return prodImgUrl;
	}

	public void setProdImgUrl(String prodImgUrl) {
		this.prodImgUrl = prodImgUrl;
	}

	public List<String> getProdPropVals() {
		return prodPropVals;
	}

	public void setProdPropVals(List<String> prodPropVals) {
		this.prodPropVals = prodPropVals;
	}

	public List<Integer> getProdIds() {
		return prodIds;
	}

	public void setProdIds(List<Integer> prodIds) {
		this.prodIds = prodIds;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public String getMerchantReply() {
		return merchantReply;
	}

	public void setMerchantReply(String merchantReply) {
		this.merchantReply = merchantReply;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getPriceManner() {
		return priceManner;
	}

	public void setPriceManner(Integer priceManner) {
		this.priceManner = priceManner;
	}

	public String getMeasureUnit() {
		return measureUnit;
	}

	public void setMeasureUnit(String measureUnit) {
		this.measureUnit = measureUnit;
	}

	public Integer getMeasureUnitCount() {
		return measureUnitCount;
	}

	public void setMeasureUnitCount(Integer measureUnitCount) {
		this.measureUnitCount = measureUnitCount;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}
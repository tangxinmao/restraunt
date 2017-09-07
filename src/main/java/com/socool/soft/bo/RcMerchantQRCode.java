package com.socool.soft.bo;

import java.io.Serializable;

public class RcMerchantQRCode extends QueryParam implements Serializable {

	private static final long serialVersionUID = 7695122201490657977L;
	
	// db
	private Long merchantQRCodeId;
	private Integer merchantId;
	private Integer tableId;
	private String sectionName;
	private Integer tableNumber;
	private String imgUrl;

	public Long getMerchantQRCodeId() {
		return merchantQRCodeId;
	}

	public void setMerchantQRCodeId(Long merchantQRCodeId) {
		this.merchantQRCodeId = merchantQRCodeId;
	}

	public Integer getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Integer merchantId) {
		this.merchantId = merchantId;
	}

	public Integer getTableId() {
		return tableId;
	}

	public void setTableId(Integer tableId) {
		this.tableId = tableId;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public Integer getTableNumber() {
		return tableNumber;
	}

	public void setTableNumber(Integer tableNumber) {
		this.tableNumber = tableNumber;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
}
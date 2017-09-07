package com.socool.soft.bo;

import java.io.Serializable;
import java.util.List;

public class RcMerchantTable extends QueryParam implements Serializable {
	
	private static final long serialVersionUID = -480888190628772183L;
	
	// db
	private Integer tableId;
	private Integer sectionId;
	private Integer maxCustomer;
	private Integer tableNumber;
	private Integer merchantId;
	
	// vo
	private String sectionName;
	private Byte hasUnreceived;
	private Integer status;
	private Integer orderNum;
	private List<Integer> sectionIds;

	public Integer getTableId() {
		return tableId;
	}

	public void setTableId(Integer tableId) {
		this.tableId = tableId;
	}

	public Integer getSectionId() {
		return sectionId;
	}

	public void setSectionId(Integer sectionId) {
		this.sectionId = sectionId;
	}

	public Integer getMaxCustomer() {
		return maxCustomer;
	}

	public void setMaxCustomer(Integer maxCustomer) {
		this.maxCustomer = maxCustomer;
	}

	public Integer getTableNumber() {
		return tableNumber;
	}

	public void setTableNumber(Integer tableNumber) {
		this.tableNumber = tableNumber;
	}

	public Integer getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Integer merchantId) {
		this.merchantId = merchantId;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}

	public void setSectionIds(List<Integer> sectionIds) {
		this.sectionIds = sectionIds;
	}

	public List<Integer> getSectionIds() {
		return sectionIds;
	}

	public Byte getHasUnreceived() {
		return hasUnreceived;
	}

	public void setHasUnreceived(Byte hasUnreceived) {
		this.hasUnreceived = hasUnreceived;
	}
}

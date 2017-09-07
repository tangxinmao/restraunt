package com.socool.soft.bo;

import java.io.Serializable;
import java.util.List;

public class RcMerchantSection extends QueryParam implements Serializable {
	
	private static final long serialVersionUID = 1243034989905333458L;
	
	// db
	private Integer sectionId;
	private String name;
	private Integer merchantId;
	
	// rel
	private List<RcMerchantTable> tables;

	public Integer getSectionId() {
		return sectionId;
	}

	public void setSectionId(Integer sectionId) {
		this.sectionId = sectionId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Integer merchantId) {
		this.merchantId = merchantId;
	}

	public List<RcMerchantTable> getTables() {
		return tables;
	}

	public void setTables(List<RcMerchantTable> tables) {
		this.tables = tables;
	}
	
}

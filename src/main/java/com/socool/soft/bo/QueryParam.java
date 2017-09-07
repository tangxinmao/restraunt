package com.socool.soft.bo;

import java.io.Serializable;

public class QueryParam implements Serializable{

	private String orderBy;
	private Integer limit;

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}
}

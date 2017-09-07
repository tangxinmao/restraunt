package com.socool.soft.bo.constant;

public enum OrderProdStatusEnum {
	UNCONFIRMED(1), //待确认
	CONFIRMED(2);  //已确认
	
	private int value;
	
	private OrderProdStatusEnum(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
}

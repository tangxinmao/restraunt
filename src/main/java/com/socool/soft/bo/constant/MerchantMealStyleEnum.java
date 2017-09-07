package com.socool.soft.bo.constant;

public enum MerchantMealStyleEnum {
	EAT_IN(1), //堂吃
	TAKE_OUT(2), //外带
	EAT_IN_AND_TAKE_OUT(3); //堂吃和外带
	
	private int value;
	
	private MerchantMealStyleEnum(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
}

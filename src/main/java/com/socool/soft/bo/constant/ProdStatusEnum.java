package com.socool.soft.bo.constant;

public enum ProdStatusEnum {
	SOLD_OUT(1), //售罄
	SELLING(2), //售卖中
	NOT_SELLING(3); //不售卖
	
	private int value;
	
	private ProdStatusEnum(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
}

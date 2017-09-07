package com.socool.soft.bo.constant;

public enum PaymentInterfaceEnum {
	WALLET(1), //钱包
	DOKU(2), //DOKU
	CASH(3); //现金
	
	private int value;
	
	private PaymentInterfaceEnum(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
}

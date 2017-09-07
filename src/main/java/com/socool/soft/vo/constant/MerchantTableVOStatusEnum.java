package com.socool.soft.vo.constant;

public enum MerchantTableVOStatusEnum {
	EMPTY(1), //空桌
	UNCONFIRMED(2), //未确认
	EATING(3), //用餐中
	UNPACKING(4); //未打包
	
	private int value;
	
	private MerchantTableVOStatusEnum(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
}

package com.socool.soft.bo.constant;

public enum OrderSellerStatusEnum {
	UNCONFIRMED(1), //待确认
	GRABBED(2), //已接单
	CONFIRMED(3),  //已确认
	FINISHED(4),  //完成
	CANCELED(5); //取消
	
	private int value;
	
	private OrderSellerStatusEnum(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
}

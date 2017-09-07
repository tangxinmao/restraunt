package com.socool.soft.bo.constant;

public enum OrderBuyerStatusEnum {
	UNCONFIRMED(1), //待确认
	CONFIRMED(2), //已确认
	FINISHED(3),  //完成
	CANCELED(4); //取消
	
	private int value;
	
	private OrderBuyerStatusEnum(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}

}

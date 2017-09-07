package com.socool.soft.bo.constant;

public enum OrderOperationTypeEnum {
	CREATE(1), //创建
	GRAB(2), //修改
	MODIFY(3), //修改
	AMOUNT_MODIFY(4), //修改金额
	CONFIRM(5), //确认提货
	CHECK_OUT(6), //点评
	CANCEL(7); //取消
	
	private int value;
	
	private OrderOperationTypeEnum(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
}

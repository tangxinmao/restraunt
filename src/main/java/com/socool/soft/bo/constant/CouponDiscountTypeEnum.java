package com.socool.soft.bo.constant;

public enum CouponDiscountTypeEnum {
	PERCENTAGE(1), //打折
    SUBSTRACT(2), //立减
    FIXED(3); //指定价

    private int value;

    private CouponDiscountTypeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}

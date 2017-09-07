package com.socool.soft.bo.constant;

/**
 * Created by tangxinmao on 2017/7/18.
 */
public enum OrderEatTypeEnum {
    EAT_IN(1), //堂吃
    TAKE_OUT(2); //外带
	
    private int value;

    private OrderEatTypeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

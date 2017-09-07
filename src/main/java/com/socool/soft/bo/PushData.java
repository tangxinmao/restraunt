package com.socool.soft.bo;

import java.util.Date;

/**
 * 推送数据
 */
public class PushData {
    // 推送类型
    // ====卖家端====
    // 1:新订单
    private Byte type;
    // 订单id
    private Long orderId;
    // 时间(和type对应, 发货时间,支付成功时间,拒绝时间等)
    private Date time;
    // 桌子信息
    private RcMerchantTable table;

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public RcMerchantTable getTable() {
        return table;
    }

    public void setTable(RcMerchantTable table) {
        this.table = table;
    }

    public enum Type{
        NEW_ORDER((byte)1), // 新订单

        UNKNOWN((byte)99);
        private byte type;
        Type(byte type){
            this.type = type;
        }
        public byte get() {
            return type;
        }
    }
}

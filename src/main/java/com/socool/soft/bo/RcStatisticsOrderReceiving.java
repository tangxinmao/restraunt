package com.socool.soft.bo;

import java.io.Serializable;
import java.util.Date;

public class RcStatisticsOrderReceiving implements Serializable {
	
	private static final long serialVersionUID = 6355180771728163290L;

	private Long statisticsOrderReceivingId;

    private Integer merchantId;

    private Integer merchantUserId;

    private Integer orderReceivingCount;

    private Date createTime;

    public Long getStatisticsOrderReceivingId() {
        return statisticsOrderReceivingId;
    }

    public void setStatisticsOrderReceivingId(Long statisticsOrderReceivingId) {
        this.statisticsOrderReceivingId = statisticsOrderReceivingId;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public Integer getMerchantUserId() {
        return merchantUserId;
    }

    public void setMerchantUserId(Integer merchantUserId) {
        this.merchantUserId = merchantUserId;
    }

    public Integer getOrderReceivingCount() {
        return orderReceivingCount;
    }

    public void setOrderReceivingCount(Integer orderReceivingCount) {
        this.orderReceivingCount = orderReceivingCount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
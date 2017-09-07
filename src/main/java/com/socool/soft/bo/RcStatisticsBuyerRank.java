package com.socool.soft.bo;

import java.io.Serializable;

public class RcStatisticsBuyerRank implements Serializable {
	
	private static final long serialVersionUID = 3267824991576351761L;

	private Long statisticsBuyerRankId;

    private Integer merchantId;

    private Integer buyerId;

    private Integer orderCount;

    public Long getStatisticsBuyerRankId() {
        return statisticsBuyerRankId;
    }

    public void setStatisticsBuyerRankId(Long statisticsBuyerRankId) {
        this.statisticsBuyerRankId = statisticsBuyerRankId;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public Integer getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Integer buyerId) {
        this.buyerId = buyerId;
    }

    public Integer getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }
}
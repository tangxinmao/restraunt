package com.socool.soft.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单统计
 */
public class RcStatisticsOrder implements Serializable {
	
	private static final long serialVersionUID = -3711987258123650937L;

	private Long statisticsOrderId;

    private Integer merchantId;
    /**点单数*/
    private Integer orderCount;
    /**翻台率*/
    private Float tableTurnoverRate;
    /**销售额*/
    private BigDecimal saleSum;
    /**统计时间*/
    private Date createTime;

    public Long getStatisticsOrderId() {
        return statisticsOrderId;
    }

    public void setStatisticsOrderId(Long statisticsOrderId) {
        this.statisticsOrderId = statisticsOrderId;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public Integer getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }

    public Float getTableTurnoverRate() {
        return tableTurnoverRate;
    }

    public void setTableTurnoverRate(Float tableTurnoverRate) {
        this.tableTurnoverRate = tableTurnoverRate;
    }

    public BigDecimal getSaleSum() {
        return saleSum;
    }

    public void setSaleSum(BigDecimal saleSum) {
        this.saleSum = saleSum;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
package com.socool.soft.bo;

import java.io.Serializable;
import java.util.Date;

public class RcStatisticsProdSale implements Serializable {
	
	private static final long serialVersionUID = -8766353678403195380L;

	private Long statisticsProdSaleId;

    private Integer merchantId;

    private Integer prodId;

    private Integer saleSum;

    private Date createTime;

    public Long getStatisticsProdSaleId() {
        return statisticsProdSaleId;
    }

    public void setStatisticsProdSaleId(Long statisticsProdSaleId) {
        this.statisticsProdSaleId = statisticsProdSaleId;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public Integer getProdId() {
        return prodId;
    }

    public void setProdId(Integer prodId) {
        this.prodId = prodId;
    }

    public Integer getSaleSum() {
        return saleSum;
    }

    public void setSaleSum(Integer saleSum) {
        this.saleSum = saleSum;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
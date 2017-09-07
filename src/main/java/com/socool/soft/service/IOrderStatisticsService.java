package com.socool.soft.service;

/**
 * 订单相关统计Service
 */
public interface IOrderStatisticsService {

    /**
     * 统计商户 每天的点单数,翻台率,销售额
     */
    void orderStatistics();

    /**
     * 统计服务员每天的接单数
     */
    void orderReceivingStatistics();

    /**
     * 统计饭店用户点单数(饭店用户订单排行)
     */
    void buyerOrderRankStatistics();

}

package com.socool.soft.task;

import com.socool.soft.service.IOrderStatisticsService;
import com.socool.soft.service.IProdStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 统计相关定时任务
 */
@Component
@EnableScheduling
public class StatisticsTask {
    @Autowired
    private IOrderStatisticsService orderStatisticsService;
    @Autowired
    private IProdStatisticsService prodStatisticsService;

    /**
     * 统计商户 每天的点单数,翻台率,销售额
     * 每天的凌晨0:10执行
     */
    @Scheduled(cron = "0 10 0 * * ?")
//    @Scheduled(cron = "0/10 * * * * ?")
    public void orderStatistics() {
        orderStatisticsService.orderStatistics();
    }

    /**
     * 统计菜品每天的销量
     * 每天的凌晨0:10执行
     */
    @Scheduled(cron = "0 10 0 * * ?")
    public void prodSaleStatistics() {
        prodStatisticsService.prodSaleStatistics();
    }

    /**
     * 统计服务员每天的接单数
     */
    @Scheduled(cron = "0 10 0 * * ?")
    public void orderReceivingStatistics() {
        orderStatisticsService.orderReceivingStatistics();
    }

    /**
     * 统计饭店用户点单数(饭店用户订单排行)
     */
    @Scheduled(cron = "0 10 0 * * ?")
    public void buyerOrderRankStatistics() {
        orderStatisticsService.buyerOrderRankStatistics();
    }
}

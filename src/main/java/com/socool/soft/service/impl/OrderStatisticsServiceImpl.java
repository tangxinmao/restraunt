package com.socool.soft.service.impl;

import com.socool.soft.dao.RcStatisticsBuyerRankMapper;
import com.socool.soft.dao.RcStatisticsOrderReceivingMapper;
import com.socool.soft.dao.RcStatisticsOrderMapper;
import com.socool.soft.service.IOrderStatisticsService;
import com.socool.soft.util.DateUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class OrderStatisticsServiceImpl implements IOrderStatisticsService {
    private static Logger log = Logger.getLogger(OrderStatisticsServiceImpl.class);
    // 昨天0点
    private static Date startTime = new Date(DateUtil.getYesterdayBeginTime());
//    private static Date startTime = new Date(1483200000000L); //1月1号
    // 今天0点
    private static Date endTime = new Date(DateUtil.getTodayBeginTime());

    @Autowired
    private RcStatisticsOrderMapper statisticsOrderMapper;
    @Autowired
    private RcStatisticsOrderReceivingMapper statisticsOrderReceivingMapper;
    @Autowired
    private RcStatisticsBuyerRankMapper statisticsBuyerRankMapper;

    /**
     * 统计订单
     * 根据商户统计每天的 点单数,翻台率,销售额
     */
    @Override
    public void orderStatistics() {
        log.info("orderStatistics -> startTime: " + startTime.getTime() + "    endTime: " + endTime.getTime());
        try {
            statisticsOrderMapper.insertAndStatistics(startTime, endTime);
        } catch (DuplicateKeyException e) {
            log.error("重复执行,统计记录已存在了......");
        }
    }

    /**
     * 服务员接单统计
     */
    @Override
    public void orderReceivingStatistics() {
        log.info("orderReceivingStatistics -> startTime: " + startTime.getTime() + "    endTime: " + endTime.getTime());
        try {
            statisticsOrderReceivingMapper.insertAndStatistics(startTime, endTime);
        } catch (DuplicateKeyException e) {
            log.error("重复执行,统计记录已存在了......");
        }
    }

    /**
     * 饭店用户点单数统计(饭店用户订单排行)
     */
    @Override
    public void buyerOrderRankStatistics() {
        log.info("buyerOrderRankStatistics...");
        try {
            statisticsBuyerRankMapper.deleteAll();
            // 统计所有时间的订单量
            statisticsBuyerRankMapper.insertAndStatistics();
        } catch (DuplicateKeyException e) {
            log.error("重复执行,统计记录已存在了......");
        }
    }
}

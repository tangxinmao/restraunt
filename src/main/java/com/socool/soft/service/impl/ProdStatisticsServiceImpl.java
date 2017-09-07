package com.socool.soft.service.impl;

import com.socool.soft.dao.RcStatisticsProdSaleMapper;
import com.socool.soft.service.IProdStatisticsService;
import com.socool.soft.util.DateUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class ProdStatisticsServiceImpl implements IProdStatisticsService {
    private static Logger log = Logger.getLogger(ProdStatisticsServiceImpl.class);
    // 昨天0点
    private static Date startTime = new Date(DateUtil.getYesterdayBeginTime());
//    private static Date startTime = new Date(1483200000000L); //1月1号
    // 今天0点
    private static Date endTime = new Date(DateUtil.getTodayBeginTime());

    @Autowired
    private RcStatisticsProdSaleMapper statisticsProdSaleMapper;

    /**
     * 统计菜品每天的销量
     */
    @Override
    public void prodSaleStatistics() {
        log.info("prodSaleStatistics -> startTime: " + startTime.getTime() + "    endTime: " + endTime.getTime());
        try {
            statisticsProdSaleMapper.insertAndStatistics(startTime, endTime);
        } catch (DuplicateKeyException e) {
            log.error("重复执行,统计记录已存在了......");
        }
    }
}

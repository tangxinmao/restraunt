package com.socool.soft.dao;

import com.socool.soft.bo.RcStatisticsOrderReceiving;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface RcStatisticsOrderReceivingMapper {
    int deleteByPrimaryKey(Long orderReceivingStatisticsId);

    int insert(RcStatisticsOrderReceiving record);

    int insertSelective(RcStatisticsOrderReceiving record);

    RcStatisticsOrderReceiving selectByPrimaryKey(Long orderReceivingStatisticsId);

    int updateByPrimaryKeySelective(RcStatisticsOrderReceiving record);

    int updateByPrimaryKey(RcStatisticsOrderReceiving record);

    /**
     * 服务员接单统计
     * @param startTime
     * @param endTime
     * @return
     */
    int insertAndStatistics(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    List<RcStatisticsOrderReceiving> select(RcStatisticsOrderReceiving orderReceivingStatistics);
}
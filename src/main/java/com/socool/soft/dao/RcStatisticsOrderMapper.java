package com.socool.soft.dao;

import com.socool.soft.bo.RcStatisticsOrder;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface RcStatisticsOrderMapper {
    int deleteByPrimaryKey(Long orderStatisticsId);

    int insert(RcStatisticsOrder record);

    int insertSelective(RcStatisticsOrder record);

    RcStatisticsOrder selectByPrimaryKey(Long orderStatisticsId);

    int updateByPrimaryKeySelective(RcStatisticsOrder record);

    int updateByPrimaryKey(RcStatisticsOrder record);

    /**
     * 统计 点单数,翻台率[（餐桌使用次数-总台位数）÷总台位数×100%],销售额 翻台率
     * @param startTime
     * @param endTime
     * @return
     */
    int insertAndStatistics(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    List<RcStatisticsOrder> select(RcStatisticsOrder orderStatistics);
}
package com.socool.soft.dao;

import com.socool.soft.bo.RcStatisticsProdSale;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface RcStatisticsProdSaleMapper {
    int deleteByPrimaryKey(Long prodSaleStatisticsId);

    int insert(RcStatisticsProdSale record);

    int insertSelective(RcStatisticsProdSale record);

    RcStatisticsProdSale selectByPrimaryKey(Long prodSaleStatisticsId);

    int updateByPrimaryKeySelective(RcStatisticsProdSale record);

    int updateByPrimaryKey(RcStatisticsProdSale record);

    /**
     * 菜品销量统计
     * @param startTime
     * @param endTime
     * @return
     */
    int insertAndStatistics(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    List<RcStatisticsProdSale> select(RcStatisticsProdSale prodSaleStatistics);
}
package com.socool.soft.dao;

import com.socool.soft.bo.RcStatisticsBuyerRank;

import java.util.List;

public interface RcStatisticsBuyerRankMapper {
    int deleteByPrimaryKey(Long buyerRankStatisticsId);

    int insert(RcStatisticsBuyerRank record);

    int insertSelective(RcStatisticsBuyerRank record);

    RcStatisticsBuyerRank selectByPrimaryKey(Long buyerRankStatisticsId);

    int updateByPrimaryKeySelective(RcStatisticsBuyerRank record);

    int updateByPrimaryKey(RcStatisticsBuyerRank record);

    /**
     * 饭店用户点单数统计
     * @return
     */
    int insertAndStatistics();

    List<RcStatisticsBuyerRank> select(RcStatisticsBuyerRank buyerRankStatistics);

    void deleteAll();
}
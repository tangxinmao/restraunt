package com.socool.soft.service;

import com.socool.soft.bo.RcMerchantTable;

import java.util.List;

public interface IMerchantTableService {

    List<RcMerchantTable> findByMerchantIdAndSectionIds(int merchantId, List<Integer> sectionIds);

    List<RcMerchantTable> findBySectionId(Integer sectionId);

    RcMerchantTable findOneByTableId(Integer tableId);
}

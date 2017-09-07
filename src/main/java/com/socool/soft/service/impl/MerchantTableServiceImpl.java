package com.socool.soft.service.impl;

import com.socool.soft.bo.RcMerchantTable;
import com.socool.soft.dao.RcMerchantTableMapper;
import com.socool.soft.service.IMerchantTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantTableServiceImpl  implements IMerchantTableService{
    @Autowired
    private RcMerchantTableMapper merchantTableMapper;
    @Override
    public List<RcMerchantTable> findByMerchantIdAndSectionIds(int merchantId, List<Integer> sectionIds) {
        RcMerchantTable rcMerchantTable=new RcMerchantTable();
        rcMerchantTable.setSectionIds(sectionIds);
        rcMerchantTable.setMerchantId(merchantId);
        rcMerchantTable.setOrderBy("SECTION_ID ASC, TABLE_ID ASC");
        List<RcMerchantTable> rcMerchantTableList = merchantTableMapper.select(rcMerchantTable);
        return rcMerchantTableList;
    }

    @Override
    public List<RcMerchantTable> findBySectionId(Integer sectionId) {
        RcMerchantTable rcMerchantTable = new RcMerchantTable();
        rcMerchantTable.setSectionId(sectionId);
        rcMerchantTable.setOrderBy("TABLE_NUMBER ASC");
        return merchantTableMapper.select(rcMerchantTable);
    }

    @Override
    public RcMerchantTable findOneByTableId(Integer tableId) {
        return merchantTableMapper.selectByPrimaryKey(tableId);
    }
}

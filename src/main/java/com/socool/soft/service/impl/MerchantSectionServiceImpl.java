package com.socool.soft.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.socool.soft.bo.RcMerchantSection;
import com.socool.soft.bo.RcMerchantTable;
import com.socool.soft.dao.RcMerchantSectionMapper;
import com.socool.soft.dao.RcMerchantTableMapper;
import com.socool.soft.service.IMerchantSectionService;

@Service
public class MerchantSectionServiceImpl implements IMerchantSectionService {

    @Autowired
    private RcMerchantSectionMapper merchantSectionMapper;

    @Autowired
    private RcMerchantTableMapper merchantTableMapper;

    @Override
    public List<RcMerchantSection> findMerchantSections(RcMerchantSection param) {
        List<RcMerchantSection> list = merchantSectionMapper.select(param);
        for (RcMerchantSection e : list) {
            RcMerchantTable table = new RcMerchantTable();
            table.setSectionId(e.getSectionId());
            List<RcMerchantTable> tableList = merchantTableMapper.select(table);
            e.setTables(tableList);
        }
        return list;
    }

    @Override
    public int addMerchantSection(RcMerchantSection section) {
        if (merchantSectionMapper.insert(section) > 0) {
            return section.getSectionId();
        }
        return 0;
    }

    @Override
    public int addMerchantTable(RcMerchantTable table) {
        if (merchantTableMapper.insert(table) > 0) {
            return table.getTableId();
        }
        return 0;
    }

    @Override
    public int addMerchantSectionAndTables(int merchantId, String name, int tableNumber) {
        RcMerchantSection section = new RcMerchantSection();
        section.setMerchantId(merchantId);
        section.setName(name);
        int sectionId = addMerchantSection(section);
        for (int i = 1; i <= tableNumber; i++) {
            RcMerchantTable table = new RcMerchantTable();
            table.setMerchantId(merchantId);
            table.setSectionId(sectionId);
            table.setTableNumber(i);
            addMerchantTable(table);
        }
        return sectionId;
    }

    @Override
    public int modifyMerchantSection(RcMerchantSection section) {
        return merchantSectionMapper.updateByPrimaryKey(section);
    }

    @Override
    public int removeMerchantTable(RcMerchantTable table) {
        return merchantTableMapper.delete(table);
    }

    @Override
    public int removeMerchantSectionAndTables(int sectionId) {
        merchantSectionMapper.deleteByPrimaryKey(sectionId);
        RcMerchantTable table = new RcMerchantTable();
        table.setSectionId(sectionId);
        merchantTableMapper.delete(table);
        return 1;
    }

	@Override
	public RcMerchantTable findMerchantTableById(int tableId) {
		return merchantTableMapper.selectByPrimaryKey(tableId);
	}

	@Override
	public RcMerchantTable findMerchantTablesBySectionIdAndTableNumber(int sectionId, int tableNumber) {
		RcMerchantTable param = new RcMerchantTable();
		param.setSectionId(sectionId);
		param.setTableNumber(tableNumber);
		return merchantTableMapper.selectOne(param);
	}

	@Override
	public RcMerchantSection findMerchantSectionById(int sectionId) {
        if(sectionId==0){
            RcMerchantSection rcMerchantSection=new RcMerchantSection();
            rcMerchantSection.setSectionId(0);
            rcMerchantSection.setName("Take Out");
            return rcMerchantSection;
        }
		return merchantSectionMapper.selectByPrimaryKey(sectionId);
	}

    @Override
    public int modifyMerchantSectionAndTables(int sectionId, String name,
                                              int tableNumber, int merchantId) {
        RcMerchantSection section = new RcMerchantSection();
        section.setSectionId(sectionId);
        section.setName(name);
        merchantSectionMapper.updateByPrimaryKey(section);
        RcMerchantTable table1 = new RcMerchantTable();
        table1.setSectionId(sectionId);
        merchantTableMapper.delete(table1);
        for (int i = 1; i <= tableNumber; i++) {
            RcMerchantTable table = new RcMerchantTable();
            table.setMerchantId(merchantId);
            table.setSectionId(sectionId);
            table.setTableNumber(i);
            addMerchantTable(table);
        }
        return 1;
    }

    @Override
    public List<RcMerchantSection> findMerchantSectionsByMerchantId(int merchantId) {
        RcMerchantSection param = new RcMerchantSection();
        param.setMerchantId(merchantId);
        param.setOrderBy("NAME ASC");
        List<RcMerchantSection> list = merchantSectionMapper.select(param);
        return list;
    }
}

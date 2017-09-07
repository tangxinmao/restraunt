package com.socool.soft.service;

import java.util.List;

import com.socool.soft.bo.RcMerchantSection;
import com.socool.soft.bo.RcMerchantTable;

public interface IMerchantSectionService {
	
	List<RcMerchantSection> findMerchantSections(RcMerchantSection param);
	
	int addMerchantSection(RcMerchantSection section);
	
	int addMerchantTable(RcMerchantTable table);
	
	int addMerchantSectionAndTables(int merchantId, String name, int tableNumber);
	
	int modifyMerchantSection(RcMerchantSection section);
	
	int removeMerchantTable(RcMerchantTable table);
	
	int removeMerchantSectionAndTables(int sectionId);
	
	int modifyMerchantSectionAndTables(int sectionId,String name,int tableNumber,int merchantId);

    List<RcMerchantSection> findMerchantSectionsByMerchantId(int merchantId);
    
    RcMerchantTable findMerchantTableById(int tableId);
    
    RcMerchantTable findMerchantTablesBySectionIdAndTableNumber(int sectionId, int tableNumber);

    RcMerchantSection findMerchantSectionById(int sectionId);
}

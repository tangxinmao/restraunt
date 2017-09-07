package com.socool.soft.service;

import java.util.List;

import com.socool.soft.bo.RcAppProdHot;
import com.socool.soft.vo.Page;

public interface IAppProdHotService {

	List<RcAppProdHot> findAppProdHotsByCityId(int cityId);

	List<RcAppProdHot> findAppProdHotsByCityId(int cityId, int limit);
	
	List<RcAppProdHot> findPagedAppProdHotsByCityId(RcAppProdHot param, Page page);
	
	int addAppProdHot(RcAppProdHot appProdHot);
	
	int modifyAppProdHot(RcAppProdHot appProdHot);
	
	int removeAppProdHot(int appProdHotId);
	
	List<RcAppProdHot> findAppProdHots(RcAppProdHot param);
	
	List<RcAppProdHot> findBaseAppProdHots(RcAppProdHot param);
	
	int removeAppProdHots(RcAppProdHot param);
}

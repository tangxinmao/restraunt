package com.socool.soft.service;

import com.socool.soft.bo.RcProd;
import com.socool.soft.vo.Page;

import java.util.List;

public interface IProdService {

	RcProd findProdById(int prodId);
	
//	RcProdCat findProdCatById(int prodCatId);
	
//	RcProdImgRel findFirstImgByProdId(int prodId);
/**
 * @return
 */
//	List<RcProd> findProdByProdIds( List<Integer> prodIds);
	
	List<RcProd> findProds(RcProd param);
	List<RcProd> findPagedProds(RcProd param, Page page);
	
	int modifyProd(RcProd prod);
	
	/**
	 * 软删除商品
	 * @param prodId
	 * @return
	 */
	int removeProd(int prodId);
	
	int addProd(RcProd prod);
	
	RcProd findProdForSolr(int prodId);
	
	List<RcProd> findProdsForSolr();

	/**
	 * 通过prodId  和 prodSkuId 查询商品详情
	 * @param prodId
	 * @param prodSkuId
	 * @return
	 */
    RcProd findOneByProdIdAndProdSkuId(Integer prodId, String prodSkuId);

    List<RcProd> findProdsByProdCatId(int prodCatId);
}

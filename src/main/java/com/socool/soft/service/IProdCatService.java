package com.socool.soft.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.socool.soft.bo.RcProdCat;
import com.socool.soft.vo.Page;

public interface IProdCatService {
	
	List<RcProdCat> findProdCats(RcProdCat param);
	
	/**
	 * 列出一级分类
	 * @return
	 */
	List<RcProdCat> findTopProdCats();
	
	List<RcProdCat> findPagedTopProdCatsWithChildren(RcProdCat param, Page page);
	
	List<RcProdCat> findProdCatsWithProds(RcProdCat param);
	
	Map<String,Object> findProdCatsWithProdsForApp(int merchantId);
	
	/**
	 * 查询二级商品分类
	 * @param parentId
	 * @return
	 */
	List<RcProdCat> findChildProdCats(int parentId);
	
	/**
	 * 添加商品分类
	 * @param prodCat
	 * @return
	 */
	int addProdCat(RcProdCat prodCat);
	
	/**
	 * 修改商品分类
	 * @param prodCat
	 * @return
	 */
	int modifyProdCat(RcProdCat prodCat);
	
	/**
	 * 根据分类ID删除商品分类
	 * @param prodCatId
	 * @return
	 */
	int removeProdCatAndChildrenById(int prodCatId);
	
	RcProdCat findProdCatById(int prodCatId);
	
	List<RcProdCat> findTopProdCatsWithChildrenForAndroid();
	
	List<Integer> findProdCatIdsByProdIds(Collection<Integer> prodIds);
	
	List<RcProdCat> findprodCatsForApp(RcProdCat param);
}

package com.socool.soft.service;

import com.socool.soft.bo.RcBuyer;
import com.socool.soft.vo.Page;

import java.util.List;

public interface IBuyerService {
	/**
	 * 查询用户信息 排序注册时间和最后登录时间排序
	 * @return
	 */
	List<RcBuyer> findBuyers(RcBuyer param);
	List<RcBuyer> findPagedBuyers(RcBuyer param, Page page);

	int addBuyer(RcBuyer buyer);

	/**
	 * 通过memberId查询member
	 * 
	 * @param buyerId
	 * @return
	 */
	RcBuyer findBuyerById(int buyerId);


	/**
	 * 通过email
	 * 
	 * @param email
	 * @return
	 */
	RcBuyer findBuyerByEmail(String email);

	RcBuyer findBuyerByMobile(String mobile);

	/**
	 * 修改用户
	 */

	int modifyBuyer(RcBuyer buyer);

	/**
	 * 删除用户
	 * 
	 * @param buyerId
	 * @return
	 */
	int removeBuyer(int buyerId);

	/**
	 * 根据账户查询用户
	 * @param account
	 * @return
	 */
	RcBuyer findBuyerByAccount(String account);
}

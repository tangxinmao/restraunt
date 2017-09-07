package com.socool.soft.service;

import com.socool.soft.bo.RcMerchantUser;
import com.socool.soft.vo.Page;

import java.util.List;

public interface IMerchantUserService {
	/**
	 * 查询用户信息 排序注册时间和最后登录时间排序
	 * @return
	 */
//	List<RcMerchantUser> findMerchantUsers(RcMerchantUser merchantUser);
//	List<RcMerchantUser> findPagedMerchantUsers(RcMerchantUser merchantUser, Page page);
	List<RcMerchantUser> findMerchantUsersByMerchantId(int merchantId);

	/**
	 * 保存用户信息
	 * @return
	 */
	int addMerchantUser(RcMerchantUser merchantUser);
	
	/**
	 * 通过memberId查询member
	 * 
	 * @param merchantUserId
	 * @return
	 */
	RcMerchantUser findMerchantUserById(int merchantUserId);


	/**
	 * 通过email
	 * 
	 * @param email
	 * @return
	 */
	RcMerchantUser findMerchantUserByEmail(String email);

	/**
	 * 通过email

	 * @return
	 */
	RcMerchantUser findMerchantUserByMobile(String mobile);
	
	/**
	 * 修改用户
	 */

	int modifyMerchantUser(RcMerchantUser merchantUser);

	/**
	 * 查询用户
	 * 
	 * @param rcMember
	 * @return
	 */
//	RcMerchantUser findMerchantUser(RcMerchantUser merchantUser);

	/**
	 * 删除用户
	 * 
	 * @param merchantUserId
	 * @return
	 */
	int removeMerchantUser(int merchantUserId);

	/**
	 * 根据账户查询用户
	 * @param account
	 * @return
	 */
	RcMerchantUser findMerchantUserByAccount(String account);
	
	RcMerchantUser findMerchantSuperAdmin(int merchantId);

	List<RcMerchantUser> findPagedMerchantUsersWithRoles(Integer memberId, String account, Integer roleId, String email, Page page);

    List<RcMerchantUser> findPagedUsersWithRoles(Integer memberId, String account, Integer roleId, String email, Page page);

	Integer addUser(RcMerchantUser user);

	int modifyUser(RcMerchantUser user);

	RcMerchantUser findUserById(int memberId);

	int removeUser(int memberId);
	
	List<RcMerchantUser> findMerchantUser(RcMerchantUser param);
}

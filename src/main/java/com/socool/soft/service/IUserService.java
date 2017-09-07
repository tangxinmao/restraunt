package com.socool.soft.service;

import com.socool.soft.bo.RcUser;
import com.socool.soft.vo.Page;

import java.util.List;

public interface IUserService {
	/**
	 * 查询用户信息 排序注册时间和最后登录时间排序
	 * @return
	 */
//	List<RcUser> findUsers(RcUser param);
//	List<RcUser> findPagedUsers(RcUser param, Page page);

//	List<RcUser> findMembersWithRoles(RcUser user);
	
	List<RcUser> findPagedUsersWithRoles(Integer userId, String account, Integer roleId, String email, Page page);

	/**
	 * 保存用户信息
	 * @return
	 */
	int addUser(RcUser user);

	/**
	 * 通过memberId查询member
	 * 
	 * @param userId
	 * @return
	 */
	RcUser findUserById(int userId);


	/**
	 * 通过email
	 * 
	 * @param email
	 * @return
	 */
	RcUser findUserByEmail(String email);

	/**
	 * 通过email
	 * @return
	 */
	RcUser findUserByMobile(String mobile);

	/**
	 * 修改用户
	 */

	int modifyUser(RcUser user);

	/**
	 * 查询用户
	 * 
	 * @param rcMember
	 * @return
	 */
//	RcUser findUser(RcUser user);

	/**
	 * 删除用户
	 * 
	 * @param userId
	 * @return
	 */
	int removeUser(int userId);

	/**
	 * 查询未使用的充值人员
	 * 
	 * @return
	 */
	List<RcUser> findUnboundRechargeOperators(Integer rechargeStationId);

	/**
	 * 根据账户查询用户
	 * @param account
	 * @return
	 */
	RcUser findUserByAccount(String account);
}

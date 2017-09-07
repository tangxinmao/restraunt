package com.socool.soft.service.impl;

import com.socool.soft.bo.*;
import com.socool.soft.bo.constant.YesOrNoEnum;
import com.socool.soft.dao.RcMerchantUserMapper;
import com.socool.soft.service.*;
import com.socool.soft.vo.Page;
import com.socool.soft.vo.PageContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class MerchantUserServiceImpl implements IMerchantUserService {
	@Autowired
	private RcMerchantUserMapper merchantUserMapper;
	@Autowired
	private IMemberService memberService;
	@Autowired
	private IMerchantRoleService merchantRoleService;
	@Autowired
	private IMerchantUserRoleService merchantUserRoleService;

//	@Override
//	public List<RcMerchantUser> findMerchantUsers(RcMerchantUser param) {
//		if(param.getMemberId() != null) {
//			RcMerchantUser member = findMerchantUserById(param.getMemberId());
//			if(member == null) {
//				return new ArrayList<RcMerchantUser>();
//			}
//			return Arrays.asList(member);
//		}
//		return merchantUserMapper.select(param);
//	}
//	
//	@Override
//	public List<RcMerchantUser> findPagedMerchantUsers(RcMerchantUser param, Page page) {
//		PageContext.setPage(page);
//		return findMerchantUsers(param);
//	}

	@Override
	public int addMerchantUser(RcMerchantUser merchantUser) {
		RcMember member = new RcMember();
		member.setAccount(merchantUser.getAccount());
		member.setEmail(merchantUser.getEmail());
		member.setMobile(merchantUser.getMobile());
		int merchantUserId = memberService.addMember(member);
		merchantUser.setMemberId(merchantUserId);
		merchantUser.setSignUpTime(new Date());
		if(merchantUserMapper.insert(merchantUser) > 0) {
			return merchantUser.getMemberId();
		}
		return 0;
	}

	@Override
	public RcMerchantUser findMerchantUserById(int merchantUserId) {
		return merchantUserMapper.selectByPrimaryKey(merchantUserId);
	}

	@Override
	public RcMerchantUser findMerchantUserByAccount(String account) {
		RcMerchantUser param = new RcMerchantUser();
		param.setAccount(account);
		return merchantUserMapper.selectOne(param);
	}

	@Override
	public RcMerchantUser findMerchantUserByEmail(String email) {
		RcMerchantUser param = new RcMerchantUser();
		param.setEmail(email);
		return merchantUserMapper.selectOne(param);
	}

	@Override
	public int modifyMerchantUser(RcMerchantUser merchantUser) {
		if(merchantUserMapper.updateByPrimaryKey(merchantUser) > 0) {
			if(StringUtils.isNotBlank(merchantUser.getAccount()) || StringUtils.isNotBlank(merchantUser.getEmail()) || StringUtils.isNotBlank(merchantUser.getMobile())) {
				RcMember member = new RcMember();
				member.setMemberId(merchantUser.getMemberId());
				member.setAccount(merchantUser.getAccount());
				member.setEmail(merchantUser.getEmail());
				member.setMobile(merchantUser.getMobile());
				memberService.modifyMember(member);
			}
			return merchantUser.getMemberId();
		}
		return 0;
	}

//	@Override
//	public RcMerchantUser findMerchantUser(RcMerchantUser param) {
//		if(param.getMemberId() != null) {
//			return findMerchantUserById(param.getMemberId());
//		} else {
//			return merchantUserMapper.selectOne(param);
//		}
//	}

	@Override
	public int removeMerchantUser(int merchantUserId) {
		RcMerchantUser param = new RcMerchantUser();
		param.setMemberId(merchantUserId);
		param.setDelFlag(YesOrNoEnum.YES.getValue());
		return modifyMerchantUser(param);
	}

	@Override
	public RcMerchantUser findMerchantUserByMobile(String mobile) {
		RcMerchantUser param = new RcMerchantUser();
		param.setMobile(mobile);
		return merchantUserMapper.selectOne(param);
	}

	@Override
	public RcMerchantUser findMerchantSuperAdmin(int merchantId) {
		RcMerchantUser param = new RcMerchantUser();
		param.setMerchantId(merchantId);
		param.setIsSuper(YesOrNoEnum.YES.getValue());
		return merchantUserMapper.selectOne(param);
	}

	@Override
	public List<RcMerchantUser> findMerchantUsersByMerchantId(int merchantId) {
		RcMerchantUser param = new RcMerchantUser();
		param.setMerchantId(merchantId);
		return merchantUserMapper.select(param);
	}

	@Override
	public List<RcMerchantUser> findPagedMerchantUsersWithRoles(Integer userId, String account, Integer roleId, String email, Page page) {
		List<RcMerchantUser> users = null;
		if(userId != null) {
			RcMerchantUser user = findMerchantUserById(userId);
			if(user == null || user.getIsSuper() == YesOrNoEnum.YES.getValue()) {
				return new ArrayList<RcMerchantUser>();
			}
			users = Arrays.asList(user);
		} else {
			RcMerchantUser param = new RcMerchantUser();
			if(StringUtils.isNotBlank(account)) {
				param.setAccount(account);
			}
			if(StringUtils.isNotBlank(email)) {
				param.setEmail(email);
			}
			if(roleId != null) {
				List<RcMerchantUserRole> userRoles = merchantUserRoleService.findMerchantUserRolesByRoleId(roleId);
				if(CollectionUtils.isEmpty(userRoles)) {
					return new ArrayList<RcMerchantUser>();
				}
				List<Integer> userIds = new ArrayList<Integer>();
				for(RcMerchantUserRole userRole : userRoles) {
					userIds.add(userRole.getMemberId());
				}
				param.setMemberIds(userIds);
			}
			param.setIsSuper(YesOrNoEnum.NO.getValue());
			PageContext.setPage(page);
			users = merchantUserMapper.select(param);
		}
		for(RcMerchantUser user : users) {
			user.setRoles(merchantRoleService.findMerchantRolesByUserId(user.getMemberId()));
		}
		return users;
	}

	@Override
	public List<RcMerchantUser> findPagedUsersWithRoles(Integer memberId, String account, Integer roleId, String email, Page page) {
		List<RcMerchantUser> users = null;
		if(memberId != null) {
			RcMerchantUser user = findUserById(memberId);
			if(user == null || user.getIsSuper() == YesOrNoEnum.YES.getValue()) {
				return new ArrayList<RcMerchantUser>();
			}else{
				if((StringUtils.isEmpty(account)||user.getName().equals(account))&&(StringUtils.isEmpty(email)||user.getEmail().equals(email))){
					List<RcMerchantRole> rcMerchantRoleList = merchantRoleService.findMerchantRolesByUserId(user.getMemberId());
					if(roleId==null||rcMerchantRoleList.get(0).getRoleId().equals(roleId))
						users = Arrays.asList(user);
				}
			}

		} else {
			RcMerchantUser param = new RcMerchantUser();
			if(StringUtils.isNotBlank(account)) {
				param.setName(account);
			}
			if(StringUtils.isNotBlank(email)) {
				param.setEmail(email);
			}
			if(roleId != null) {
				List<RcMerchantUserRole> userRoles = merchantUserRoleService.findMerchantUserRolesByRoleId(roleId);
				if(CollectionUtils.isEmpty(userRoles)) {
					return new ArrayList<RcMerchantUser>();
				}
				List<Integer> userIds = new ArrayList<Integer>();
				for(RcMerchantUserRole userRole : userRoles) {
					userIds.add(userRole.getMemberId());
				}
				param.setMemberIds(userIds);
			}
			param.setIsSuper(YesOrNoEnum.NO.getValue());
			PageContext.setPage(page);
			users = merchantUserMapper.select(param);
		}
		if(org.apache.commons.collections.CollectionUtils.isNotEmpty(users))
		for(RcMerchantUser user : users) {
			user.setRoles(merchantRoleService.findMerchantRolesByUserId(user.getMemberId()));
		}
		return users;
	}

	@Override
	public Integer addUser(RcMerchantUser user) {
		RcMember member = new RcMember();
		member.setAccount(user.getAccount());
		member.setEmail(user.getEmail());
		member.setMobile(user.getMobile());
		int userId = memberService.addMember(member);
		user.setMemberId(userId);
		user.setSignUpTime(new Date());
		if(merchantUserMapper.insert(user) > 0) {
			return user.getMemberId();
		}
		return 0;
	}

	@Override
	public int modifyUser(RcMerchantUser user) {
		if(merchantUserMapper.updateByPrimaryKey(user) > 0) {
			if(StringUtils.isNotBlank(user.getAccount()) || StringUtils.isNotBlank(user.getEmail()) || StringUtils.isNotBlank(user.getMobile())) {
				RcMember member = new RcMember();
				member.setMemberId(user.getMemberId());
				member.setAccount(user.getAccount());
				member.setEmail(user.getEmail());
				member.setMobile(user.getMobile());
				memberService.modifyMember(member);
			}
			return user.getMemberId();
		}
		return 0;
	}

	@Override
	public RcMerchantUser findUserById(int memberId) {
		return merchantUserMapper.selectByPrimaryKey(memberId);
	}

	@Override
	public int removeUser(int memberId) {
		RcMerchantUser param = new RcMerchantUser();
		param.setMemberId(memberId);
		param.setDelFlag(YesOrNoEnum.YES.getValue());
		if(modifyUser(param) > 0) {
			//userRoleService.removeUserRolesByUserId(userId);
			return 1;
		}
		return 0;
	}

	@Override
	public List<RcMerchantUser> findMerchantUser(RcMerchantUser param) {
		// TODO Auto-generated method stub
		return merchantUserMapper.select(param);
	}
}

package com.socool.soft.service;

import com.socool.soft.bo.RcUserGetui;
import com.socool.soft.exception.SystemException;

public interface IUserGetuiService {
    /**
     * 用户 推送cid绑定
     * @param memberId
     * @param cid
     */
	void addUserPushBind(Integer memberId, String cid) throws SystemException;

    /**
     * 根据memberId获取对象
     * @param memberId
     * @return
     */
	RcUserGetui getByMemberId(Integer memberId);
}

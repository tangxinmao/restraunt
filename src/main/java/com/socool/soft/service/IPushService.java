package com.socool.soft.service;

import com.socool.soft.bo.RcOrder;
import com.socool.soft.exception.SystemException;

public interface IPushService {

    //==================卖家端=================//
    /**
     * 通知卖家 - 新订单
     * @param order
     * @throws SystemException
     */
    void newOrder(RcOrder order) throws SystemException;

}

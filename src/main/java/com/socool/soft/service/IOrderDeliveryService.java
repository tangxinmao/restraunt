package com.socool.soft.service;

import com.socool.soft.bo.RcOrderDelivery;

public interface IOrderDeliveryService {
    /**
     * @return
     */
    long addOrderDelivery(RcOrderDelivery orderDelivery);

    RcOrderDelivery findOrderDeliveryById(long orderId);

    long modifyOrderDelivery(RcOrderDelivery orderDelivery);
}

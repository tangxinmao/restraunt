package com.socool.soft.service.impl;

import com.socool.soft.bo.PushData;
import com.socool.soft.bo.RcMerchantTable;
import com.socool.soft.bo.RcOrder;
import com.socool.soft.bo.RcUserGetui;
import com.socool.soft.dao.RcMerchantTableMapper;
import com.socool.soft.exception.ErrorCode;
import com.socool.soft.exception.SystemException;
import com.socool.soft.push.GetuiClient;
import com.socool.soft.service.IPushService;
import com.socool.soft.service.IUserGetuiService;
import com.socool.soft.util.JSONObjectUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PushServiceImpl implements IPushService {
    private Logger log = Logger.getLogger(getClass());

    @Autowired
    private GetuiClient getuiClient;
    @Autowired
    private IUserGetuiService userGetuiService;
    @Autowired
    private RcMerchantTableMapper merchantTableMapper;


    /**
     * 通知卖家 - 新订单
     *
     * @param order
     * @throws SystemException
     */
    @Override
    public void newOrder(RcOrder order) throws SystemException {
        // 根据商户id获取推送cid
        RcUserGetui userCid = userGetuiService.getByMemberId(order.getMerchantId());
        if (userCid == null) {
            throw new SystemException(ErrorCode.SYSTEM_ERROR, "无CID");
        }

        // 获取桌子信息
        RcMerchantTable table = merchantTableMapper.selectByPrimaryKey(order.getTableId());
        if (table == null) {
            throw new SystemException(ErrorCode.SYSTEM_ERROR, "merchantTableId:[" + order.getTableId() + "], merchantTable对象不存在");
        }

        PushData pushData = new PushData();
        pushData.setOrderId(order.getOrderId());
        pushData.setType(PushData.Type.NEW_ORDER.get());
        pushData.setTime(new Date());
        pushData.setTable(table);

        // push json数据
        String dataJson = JSONObjectUtil.fromObject(pushData);
        log.info("push data: " + dataJson + "  merchantId: " + order.getMerchantId());
        try {
            getuiClient.pushTransMsgByCID(dataJson, userCid.getCid());
        } catch (Exception e) {
            throw new SystemException(ErrorCode.SYSTEM_ERROR, "push error: " + e.getMessage());
        }
        log.info("push success.");
    }

}

package com.socool.soft.push;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.ListMessage;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.gexin.rp.sdk.template.style.Style0;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 个推 客户端
 */
@Component
public class GetuiClient {
    private Logger log = Logger.getLogger(getClass());

    @Value("${push.appId}")
    private String appId;
//    private String appId = "9EeF8bIE266qgnft6ePw15";

    @Value("${push.appKey}")
    private String appKey;
//    private String appKey = "Zk1JjnKjYh56K9O40dFXL8";

    @Value("${push.MasterSecret}")
    private String masterSecret;
//    private String masterSecret = "BeAJBgD0hsAsVOw00Qe4p";

    private static final String host = "http://sdk.open.api.igexin.com/apiex.htm";
    // 离线有效时间，单位为毫秒
    private int msgExpireTime = 2 * 3600 * 1000;

    /**
     * 初始化个推客户端
     * @return
     */
    private IGtPush initGt() {
        return new IGtPush(host, appKey, masterSecret);
    }

    /**
     * 获取通知栏+透传消息
     * @param title
     * @param text
     * @param data
     * @return
     */
    private NotificationTemplate getNotificationTemplate(String title, String text, String data) {
        NotificationTemplate template = new NotificationTemplate();
        // 设置APPID与APPKEY
        template.setAppId(appId);
        template.setAppkey(appKey);

        Style0 style = new Style0();
        // 设置通知栏标题与内容
        style.setTitle(title);
        style.setText(text);
        // 设置通知是否响铃，震动，或者可清除
        style.setRing(true);
        style.setVibrate(true);
        style.setClearable(true);
        template.setStyle(style);

        // 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
        template.setTransmissionType(2);
        template.setTransmissionContent(data);
        return template;
    }

    /**
     * 获取透传消息
     * @param msg
     * @return
     * @throws Exception
     */
    private TransmissionTemplate getTransTemplate(String msg) throws Exception {
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);
        template.setTransmissionContent(msg);
        template.setTransmissionType(2); // 这个Type为int型，填写1则自动启动app
        return template;
    }

    /**
     * 根据cid获取target对象
     * @param cid
     * @return
     */
    private Target getTarget(String cid) {
        Target t = new Target();
        t.setAppId(appId);
        t.setClientId(cid);
        return t;
    }

    /**
     * 根据cid通知
     * @param title
     * @param text
     * @param data
     * @param cid
     */
    public void pushNoticeByCID(String title, String text, String data, String cid) throws Exception {
        IGtPush push = initGt();
        NotificationTemplate template = getNotificationTemplate(title, text, data);
        SingleMessage message = new SingleMessage();
        // 设置消息离线
        message.setOffline(true);
        message.setOfflineExpireTime(msgExpireTime);
        message.setData(template);
        message.setPushNetWorkType(0);
        Target target = new Target();
        target.setAppId(appId);
        target.setClientId(cid);
        IPushResult res;
        try {
            res = push.pushMessageToSingle(message, target);
        } catch (RequestException e) {
            e.printStackTrace();
            throw new Exception("push notice error");
        }
        if (res != null) {
            log.info("success ->" + res.getResponse());
        } else {
            throw new Exception("push notice error");
        }
    }

    /**
     * 根据cid列表通知
     * @param title
     * @param text
     * @param data
     * @param cidList
     * @throws Exception
     */
    public void pushNoticeByCIDList(String title, String text, String data, List<String> cidList) throws Exception {
        IGtPush push = initGt();
        NotificationTemplate template = getNotificationTemplate(title, text, data);
        ListMessage message = new ListMessage();
        // 设置消息离线
        message.setOffline(true);
        message.setOfflineExpireTime(msgExpireTime);
        message.setData(template);
        message.setPushNetWorkType(0);

        List<Target> targetList = new ArrayList();
        for(String cid : cidList) {
            targetList.add(getTarget(cid));
        }

        IPushResult res;
        try {
            res = push.pushMessageToList(push.getContentId(message), targetList);
        } catch (RequestException e) {
            e.printStackTrace();
            throw new Exception("push notice error");
        }
        if (res != null) {
            log.info("success ->" + res.getResponse());
        } else {
            throw new Exception("push notice error");
        }
    }

    /**
     * 根据cid发送透传消息
     * @param data
     * @param cid
     * @throws Exception
     */
    public void pushTransMsgByCID(String data, String cid) throws Exception {
        IGtPush push = initGt();
        SingleMessage message = new SingleMessage();
        // 设置消息离线
        message.setOffline(true);
        message.setOfflineExpireTime(msgExpireTime);
        message.setData(getTransTemplate(data));

        IPushResult res;
        try {
            res = push.pushMessageToSingle(message, getTarget(cid));
        } catch (RequestException e) {
            e.printStackTrace();
            throw new Exception("push trans error");
        }
        if (res != null) {
            log.info("success ->" + res.getResponse());
        } else {
            throw new Exception("push trans error");
        }
    }

    /**
     * 根据cid列表发送透传消息
     * @param data
     * @param cidList
     * @throws Exception
     */
    public void pushTransMsgByCIDList(String data, List<String> cidList) throws Exception {
        IGtPush push = initGt();
        ListMessage message = new ListMessage();
        // 设置消息离线
        message.setOffline(true);
        message.setOfflineExpireTime(msgExpireTime);
        message.setData(getTransTemplate(data));
        message.setPushNetWorkType(0);

        List<Target> targetList = new ArrayList();
        for(String cid : cidList) {
            targetList.add(getTarget(cid));
        }

        IPushResult res;
        try {
            res = push.pushMessageToList(push.getContentId(message), targetList);
        } catch (RequestException e) {
            e.printStackTrace();
            throw new Exception("push trans error");
        }
        if (res != null) {
            log.info("success ->" + res.getResponse());
        } else {
            throw new Exception("push trans error");
        }
    }


    public static void main(String [] args) throws Exception {
        GetuiClient getuiTemplate = new GetuiClient();
        String cid = "60cab9794205ddbe132ae2f945cc03fd";

        // 通知+透传消息
//        getuiTemplate.pushNoticeByCID("12","abc","hello", cid);
//        getuiTemplate.pushNoticeByCIDList("list123", "test123", "oo", Arrays.asList(cid,cid2,cid3,cid4));

        // 透传消息
//        getuiTemplate.pushTransMsgByCID("透传消息", cid);
        getuiTemplate.pushTransMsgByCIDList("0990", Arrays.asList(cid));

    }

}

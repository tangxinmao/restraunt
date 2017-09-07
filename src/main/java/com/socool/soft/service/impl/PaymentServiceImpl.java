package com.socool.soft.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.socool.soft.bo.RcOrder;
import com.socool.soft.bo.constant.PaymentTypeEnum;
import com.socool.soft.constant.PropertyConstants;
import com.socool.soft.service.IOrderService;
import com.socool.soft.service.IPaymentService;

import net.sf.json.JSONObject;

@Service
public class PaymentServiceImpl implements IPaymentService {

	private final static Log log = LogFactory.getLog(PaymentServiceImpl.class);
	
	@Autowired
	private IOrderService orderService;
	@Autowired
	private PropertyConstants propertyConstants;
	
	@Override
	public String toPay(String data,String req_payment_channel) {
		String path = propertyConstants.dokuPaymentUrl;
		int paymentType = 0;
		if("15".equals(req_payment_channel)){
			paymentType = PaymentTypeEnum.CREDIT_CARD.getValue();
		}
		else if("02".equals(req_payment_channel)){
			paymentType = PaymentTypeEnum.DEBIT_CARD.getValue();
		}
		else{
			paymentType = PaymentTypeEnum.BALANCE.getValue();
		}
		String result = sentRequest(path,data);
		try{
			JSONObject json = JSONObject.fromObject(result);
			if("0000".equals(json.getString("res_response_code"))){
				String orderIds = json.getString("res_trans_id_merchant");
				String ids[] = orderIds.split(",");
				for(String orderId:ids){
//					orderService.completePayment(Long.parseLong(orderId),PaymentInterfaceEnum.DOKU.getValue(), paymentType);
				}
			}
		}catch(Exception e){
			log.debug(e.getMessage());
		}
		return result;
	}
	
	@Override
	public String generateCode(String orderIds,String data) {
		String path = propertyConstants.dokuNagerageCodeVa;
		String result = sentRequest(path,data);
		try{
			JSONObject json = JSONObject.fromObject(result);
			if("0000".equals(json.getString("res_response_code"))){
				String ids[] = orderIds.split(",");
				for(String orderId:ids){
					RcOrder order = orderService.findOrderById(Long.parseLong(orderId));
//					order.setPayCode(json.getString("res_pay_code"));
					orderService.modifyOrder(order);
				}
			}
		}catch(Exception e){
			log.debug(e.getMessage());
		}
		return result;
	}
	
	@Override
	public String mandiry(String data,String req_payment_channel) {
		String path = propertyConstants.dokuDirectPaymentUrl;
		int paymentType = 0;
		if("15".equals(req_payment_channel)){
			paymentType = PaymentTypeEnum.CREDIT_CARD.getValue();
		}
		else if("02".equals(req_payment_channel)){
			paymentType = PaymentTypeEnum.DEBIT_CARD.getValue();
		}
		else{
			paymentType = PaymentTypeEnum.BALANCE.getValue();
		}
		String result = sentRequest(path,data);
		try{
			JSONObject json = JSONObject.fromObject(result);
			if("0000".equals(json.getString("res_response_code"))){
				String orderIds = json.getString("res_trans_id_merchant");
				String ids[] = orderIds.split(",");
				for(String orderId:ids){
//					orderService.completePayment(Long.parseLong(orderId),PaymentInterfaceEnum.DOKU.getValue(), paymentType);
				}
			}
		}catch(Exception e){
			log.debug(e.getMessage());
		}
		return result;
	}

	private String sentRequest(String path,String data1) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(path);
		httpPost.addHeader(HTTP.CONTENT_TYPE,
				"application/x-www-form-urlencoded; charset=UTF-8");
		CloseableHttpResponse response = null;
		HttpEntity entity = null;
		try {
			StringEntity reqEntity = new StringEntity("data="+data1);
			reqEntity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");    
			httpPost.setEntity(reqEntity);
			response = httpClient.execute(httpPost);
			entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200) {// 如果状态码为200,就是正常返回
				byte[] data = readInputStream(entity.getContent());
				String tempStr = new String(data,"UTF-8");
				return tempStr;
			}
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			try {
				EntityUtils.consume(entity);
				if(response != null) {
					response.close();
				}
			} catch (IOException e) {
			}
		}
		return "{\"res_response_code\":\"999999\"}";
	}
		
	public static byte[] readInputStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		inStream.close();
		return outStream.toByteArray();
	}
}
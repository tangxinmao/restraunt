package com.socool.soft.service;

public interface IPaymentService {

	public String toPay(String data,String req_payment_channel);
	
	public String generateCode(String orderIds, String data);
	
	public String mandiry(String data,String req_payment_channel);
}

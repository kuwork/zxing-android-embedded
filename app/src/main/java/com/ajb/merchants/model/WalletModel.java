package com.ajb.merchants.model;

/**
 * 钱包
 * @author chenming
 *
 */
public class WalletModel {

	/**
	 *余额
	 */
	private  String wBalance;
	
	/**
	 * 积分
	 */
	private String wIntegration;

	public String getwBalance() {
		return wBalance;
	}

	public void setwBalance(String wBalance) {
		this.wBalance = wBalance;
	}

	public String getwIntegration() {
		return wIntegration;
	}

	public void setwIntegration(String wIntegration) {
		this.wIntegration = wIntegration;
	}
	
	
	

}

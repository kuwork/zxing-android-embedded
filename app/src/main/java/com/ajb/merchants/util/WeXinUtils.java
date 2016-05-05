package com.ajb.merchants.util;

import org.apache.http.NameValuePair;

import java.util.List;
import java.util.Random;

public class WeXinUtils {

	public static  String genNonceStr() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000))
				.getBytes());
	}

	public static long genTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}

	public static String genOutTradNo() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000))
				.getBytes());
	}

	// sign签名
	public static  String genAppSign(List<NameValuePair> params,String API_KEY) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(API_KEY);
		String appSign = MD5.getMessageDigest(sb.toString().getBytes());
		return appSign;
	}

}

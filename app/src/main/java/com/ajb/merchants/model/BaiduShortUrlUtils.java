package com.ajb.merchants.model;

import com.ajb.merchants.others.MyApplication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

public class BaiduShortUrlUtils implements Serializable {

	public class BaiduShortUrlResult {
		private String err_msg;
		private int status;
		private String longurl;
		private String tinyurl;
	}

	public BaiduShortUrlResult result;
	public OnGetShortUrlListener onGetShortUrlListener;
	private HttpUtils http;
	private RequestParams params;
	private String url;

	public interface OnGetShortUrlListener {
		void onResult(String shortUrl);
	}

	public BaiduShortUrlUtils(String url, OnGetShortUrlListener listener) {
		this.onGetShortUrlListener = listener;
		this.url = url;
		this.http = MyApplication.getHttpUtils();
		this.params = new RequestParams("utf-8");
		this.params.addBodyParameter("url", url);// 流形式的
	}

	public void start2() {

	}

	public void start() {
		if (http == null || params == null) {
			if (onGetShortUrlListener != null) {
				onGetShortUrlListener.onResult(null);
			}
		}
		http.send(HttpMethod.POST, "http://dwz.cn/create.php", params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						Gson gson = new Gson();
						try {
							result = gson.fromJson(responseInfo.result,
									new TypeToken<BaiduShortUrlResult>() {
									}.getType());
							if (onGetShortUrlListener != null) {
								if (result.status == 0) {
									onGetShortUrlListener
											.onResult(result.tinyurl);
								} else {
									onGetShortUrlListener.onResult(null);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
							onGetShortUrlListener.onResult(null);
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						onGetShortUrlListener.onResult(null);
					}
				});
	}

	public static JSONObject httpRequest(String requestUrl,
			String requestMethod, String outputStr) {
		OutputStream outputStream = null;
		BufferedReader bufferedReader = null;
		InputStreamReader inputStreamReader = null;
		InputStream inputStream = null;
		JSONObject jsonObject = null;
		StringBuffer buffer = new StringBuffer();

		try {

			URL url = new URL(requestUrl);
			HttpURLConnection httpUrlConn = (HttpURLConnection) url
					.openConnection();

			httpUrlConn.setDoOutput(true);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);
			// 设置请求方式（opt/POST）
			httpUrlConn.setRequestMethod(requestMethod);

			if ("opt".equalsIgnoreCase(requestMethod))
				httpUrlConn.connect();

			// 当有数据需要提交时
			if (null != outputStr) {
				outputStream = httpUrlConn.getOutputStream();
				// 注意编码格式，防止中文乱码
				outputStream.write(outputStr.getBytes("UTF-8"));
			}

			// 将返回的输入流转换成字符串
			inputStream = httpUrlConn.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			bufferedReader = new BufferedReader(inputStreamReader);

			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			httpUrlConn.disconnect();

			if (null == buffer.toString() || "".equals(buffer.toString())) {
				return null;
			}

			jsonObject = new JSONObject(buffer.toString());
		} catch (ConnectException ce) {
		} catch (Exception e) {
		} finally {
			if (null != outputStream) {
				try {
					outputStream.close();
				} catch (IOException e) {
				}
			}
			if (null != bufferedReader) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
				}
			}
			if (null != inputStreamReader) {
				try {
					inputStreamReader.close();
				} catch (IOException e) {
				}
			}
			if (null != inputStream) {
				try {
					inputStream.close();
					inputStream = null;
				} catch (IOException e) {
				}
			}
		}
		return jsonObject;
	}
}

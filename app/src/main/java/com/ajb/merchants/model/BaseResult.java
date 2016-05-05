package com.ajb.merchants.model;

import java.io.Serializable;

/**
 * @ClassName BaseResult
 * @Description 接口基本返回实体
 * @author jerry
 * @date 2015年7月6日 下午3:16:00
 */
public class BaseResult<T> implements Serializable {
	public String msg;// 提示信息
	public boolean status;// 状态
	public String code;// 错误代码
	public T data;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}

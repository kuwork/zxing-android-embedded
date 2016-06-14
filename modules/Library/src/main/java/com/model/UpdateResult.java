package com.model;

import java.io.Serializable;

public class UpdateResult implements Serializable {

	public final static int CHECK_SUCCESS = 1000;
	public final static int ERROR_DB_CONNECT_FAIL = 1001;
	public final static int ERRPR_NO_PRODUCT_ID = 1002;

	public String msg;// 提示信息
	/**
	 * @Fields code : 除了数据库异常和产品不存在，其他都返回无更新
	 */
	public int code;// 错误代码
	public UpdateInfo updateInfo;

	public UpdateResult() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UpdateResult(String msg, int code, UpdateInfo updateInfo) {
		super();
		this.msg = msg;
		this.code = code;
		this.updateInfo = updateInfo;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public UpdateInfo getUpdateInfo() {
		return updateInfo;
	}

	public void setUpdateInfo(UpdateInfo updateInfo) {
		this.updateInfo = updateInfo;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UpdateResult [msg=");
		builder.append(msg);
		builder.append(", code=");
		builder.append(code);
		builder.append(", updateInfo=");
		builder.append(updateInfo);
		builder.append("]");
		return builder.toString();
	}
}

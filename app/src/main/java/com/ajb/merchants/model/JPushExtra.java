package com.ajb.merchants.model;

import java.io.Serializable;

import com.model.UpdateInfo;

/**
 * @ClassName JPushExtra
 * @Description 极光推送额外信息
 * @author jerry
 * @date 2015年9月7日 上午11:13:13
 */
public class JPushExtra implements Serializable {
	/**
	 * @Fields actionType : 信息类型 
	 * 1:普通通知; 
	 * 2:网页通告;（携带urlStr） 
	 * 3:提现审核推送; 
	 * 4:升级app版本;
	 * 5:进出场推送之后，点击才有相应的事件; 
	 * 6:订单状态变更。 
	 * 其他推送暂时不做处理 
	 * 跳转处理:1.跳到消息中心页面
	 *         3。提现跳到个人帐户页面 5.进出场跳到停车记录页面 6.消费明细
	 */
	public String actionType;
	/**
	 * @Fields actionId : 信息ID （1-2：消息ID，3。订单ID，5：停车记录ID。6.订单ID）
	 */
	public String actionId;

	/**
	 * @Fields urlStr : 网页地址
	 */
	public String urlStr;
	public String versionCode; // 版本推送用到的版本号
	public String versionName; // 版本推送用到的版本名
	public String title; // 标题

	public JPushExtra() {
		super();
	}

}

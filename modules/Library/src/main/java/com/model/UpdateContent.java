/**   
 * @Title UpdateInfo.java 
 * @Package bean.entity 
 * @Description  
 * @author 陈国宏
 * @date 2013年11月30日 下午1:58:49 
 * @version V1.0   
 */
package com.model;

import java.io.Serializable;

/** 
* @ClassName UpdateContent 
* @Description 版本更新内容
* @author jerry
* @date 2015年8月28日 下午1:12:34  
*/
public class UpdateContent implements Serializable {

	int versionCode;// 版本号
	String versionName;// 版本名
	String updateContent;// 更新内容

	public UpdateContent(int versionCode, String versionName,
			String updateContent) {
		super();
		this.versionCode = versionCode;
		this.versionName = versionName;
		this.updateContent = updateContent;
	}

	public UpdateContent() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getUpdateContent() {
		return updateContent;
	}

	public void setUpdateContent(String updateContent) {
		this.updateContent = updateContent;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UpdateContent [versionCode=");
		builder.append(versionCode);
		builder.append(", versionName=");
		builder.append(versionName);
		builder.append(", updateContent=");
		builder.append(updateContent);
		builder.append("]");
		return builder.toString();
	}

}
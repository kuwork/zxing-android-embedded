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
import java.util.ArrayList;
import java.util.List;

import android.R.integer;

/**
 * @ClassName UpdateInfo
 * @Description
 * @author 陈国宏
 * @date 2013年11月30日 下午1:58:49
 */
public class UpdateInfo implements Serializable {

	public final static String FULL_UPDATE = "full";
	public final static String INC_UPDATE = "inc";
	public final static String NO_UPDATE = "noupdate";

	String name;
	String updateType;
	int versionCode;// 版本号
	String versionName;// 版本名
	long size;// 包的大小
	long sizeOriginal;// 原来包的大小
	String newSHA;// 新版本的校验码
	String increaseSHA;// 增量包的校验码
	String url;// 下载的链接
	List<UpdateContent> updateList;// 历次更新内容

	public UpdateInfo() {
		super();

	}

	public UpdateInfo(String name, String updateType, int versionCode,
			String versionName, long size, long sizeOriginal, String newSHA,
			String increaseSHA, String url) {
		super();
		this.name = name;
		this.updateType = updateType;
		this.versionCode = versionCode;
		this.versionName = versionName;
		this.size = size;
		this.sizeOriginal = sizeOriginal;
		this.newSHA = newSHA;
		this.increaseSHA = increaseSHA;
		this.url = url;
		this.updateList = new ArrayList<UpdateContent>();
	}

	public UpdateInfo(String name, String updateType, int versionCode,
			String versionName, long size, long sizeOriginal, String newSHA,
			String increaseSHA, String url, List<UpdateContent> updateList) {
		super();
		this.name = name;
		this.updateType = updateType;
		this.versionCode = versionCode;
		this.versionName = versionName;
		this.size = size;
		this.sizeOriginal = sizeOriginal;
		this.newSHA = newSHA;
		this.increaseSHA = increaseSHA;
		this.url = url;
		this.updateList = updateList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUpdateType() {
		return updateType;
	}

	public void setUpdateType(String updateType) {
		this.updateType = updateType;
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

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getNewSHA() {
		return newSHA;
	}

	public void setNewSHA(String newSHA) {
		this.newSHA = newSHA;
	}

	public String getIncreaseSHA() {
		return increaseSHA;
	}

	public void setIncreaseSHA(String increaseSHA) {
		this.increaseSHA = increaseSHA;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UpdateInfo [name=");
		builder.append(name);
		builder.append(", updateType=");
		builder.append(updateType);
		builder.append(", versionCode=");
		builder.append(versionCode);
		builder.append(", versionName=");
		builder.append(versionName);
		builder.append(", size=");
		builder.append(size);
		builder.append(", newSHA=");
		builder.append(newSHA);
		builder.append(", increaseSHA=");
		builder.append(increaseSHA);
		builder.append(", url=");
		builder.append(url);
		builder.append("]");
		return builder.toString();
	}

	public List<UpdateContent> getUpdateList() {
		return updateList;
	}

	public void setUpdateList(List<UpdateContent> updateList) {
		this.updateList = updateList;
	}

	public void addUpdateContent(UpdateContent updateContent) {
		if (updateList != null) {
			this.updateList.add(updateContent);
		}
	}

	public void removeUpdateContent(UpdateContent updateContent) {
		if (updateList != null) {
			int index = Integer.MAX_VALUE;
			for (int i = 0; i < this.updateList.size(); i++) {
				if (this.updateList.get(i).versionCode == updateContent.versionCode) {
					index = i;
					break;
				}
			}
			if (index < Integer.MAX_VALUE) {
				this.updateList.remove(index);
			}
		}
	}

	public long getSizeOriginal() {
		return sizeOriginal;
	}

	public void setSizeOriginal(long sizeOriginal) {
		this.sizeOriginal = sizeOriginal;
	}

}
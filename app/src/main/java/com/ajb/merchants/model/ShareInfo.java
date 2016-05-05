package com.ajb.merchants.model;

import java.io.Serializable;

/**
 * Created by jerry on 16/1/8.
 */
public class ShareInfo implements Serializable {

    public final static String WEIXIN = "1";
    public final static String PENGYOUQUAN = "2";
    public final static String SMS = "3";
    /**
     * type : 类型
     * content : 内容
     * url : 链接
     * imageUrl : 图片
     * title : 标题
     */

    private String type;
    private String title;
    private String content;
    private String imageUrl;
    private String url;

    public ShareInfo() {
    }

    public ShareInfo(String type, String title, String content, String imageUrl, String url) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.url = url;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "ShareInfo{" +
                "type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}

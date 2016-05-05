package com.ajb.merchants.model;

import java.io.Serializable;

/**
 * @Description:
 * @author: 李庆育
 * @date: 2016/3/18 14:58
 */
public class AliPayInfo implements Serializable {

    /**
     * sign : NvpL8aCfCKRAQ+HY9UrQ8BTdouI7rtuOCXvCgpzz8IujToMWmKIuECxbIL7mp9WmiQoLpIO2zX4XanOi3LWVoiG9t/jQW2arVsp2ALEaZK5ZFT+UZAikvXBJd8ttaH2PzHjVyJo0lMauIdgmSI2sawCTrXGm7p9cJplxEZQ7BmE=
     * body : 停车支付
     * _input_charset : utf-8
     * total_fee : 0.01
     * subject : 停车缴费
     * sign_type : RSA
     * notify_url : http://localhost:80//aliAppCallback
     * service : create_direct_pay_by_user
     * seller_id : 2088011896577952
     * partner : 2088011896577952
     * out_trade_no : 11cba2b1fb5446e6a1ad502cb275040d
     * payment_type : 1
     */

    private String sign;
    private String body;
    private String _input_charset;
    private String total_fee;
    private String subject;
    private String sign_type;
    private String notify_url;
    private String service;
    private String seller_id;
    private String partner;
    private String out_trade_no;
    private String payment_type;
    private String orderInfo;
    private String orderId;

    public void setSign(String sign) {
        this.sign = sign;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void set_input_charset(String _input_charset) {
        this._input_charset = _input_charset;
    }

    public void setTotal_fee(String total_fee) {
        this.total_fee = total_fee;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public String getSign() {
        return sign;
    }

    public String getBody() {
        return body;
    }

    public String get_input_charset() {
        return _input_charset;
    }

    public String getTotal_fee() {
        return total_fee;
    }

    public String getSubject() {
        return subject;
    }

    public String getSign_type() {
        return sign_type;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public String getService() {
        return service;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public String getPartner() {
        return partner;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}

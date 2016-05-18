package com.ajb.merchants.util;

/**
 * @author chenming 用于数据缓存，图片信息地址
 */
public class Constant {

    /**
     * 各种请求码
     */
    private static final int REQ_CODE_BASE = 1000;
    public static final int REQ_CODE_WECHAT = REQ_CODE_BASE + 1;    //startActivityForResult中分享微信填写该请求码
    public static final int REQ_CODE_WECHAT_MOMENT = REQ_CODE_BASE + 2; //startActivityForResult中分享微信朋友圈填写该请求码
    public static final int REQ_CODE_SMS = REQ_CODE_BASE + 3; //startActivityForResult中短信分享填写该请求码
    //	扫描
    public static final int REQ_CODE_SCAN = REQ_CODE_BASE + 4;
    //登陆
    public static final int REQ_CODE_LOGIN = REQ_CODE_BASE + 5;
    public static final int REQ_CODE_SHARE = REQ_CODE_BASE + 6;
    public static final int REQ_CODE_LOGOUT = REQ_CODE_BASE + 7;    //登出
    //权限访问码
    public static final int PM_CAMERA = 1;//摄像头权限
    public static final int PM_LOCATION = 2;//GPS权限
    //接口地址
//    public static final String SERVER_URL = "http://app.eanpa-gz-manager.com/";//正式环境
//    public static final String SERVER_URL = "http://115.29.211.91:28082/";//正式库测试环境
    public static final String SERVER_URL = "http://172.16.35.6:18080/";// 开发
    //    public static final String NEWSERVER_URL = "http://218.244.141.147:13901";// 心跳开发
    public static final String APPSEARCHCARCOUNT = "appSearchCarCount";//(新)查询计费接口
    public static final String APPPAYV2 = "appPayV2";// (新)停车缴费-微信支付接口（调用微信统一下单接口）
    public static final String APPGETRENEWINFO = "appGetRenewInfo";// 获取会员激活预支付订单
    public static final String APPSAVECHANNELID = "appSaveChannelId";// 用户模块-保存channelId接口
    public static final String APPDELCHANNELID = "appDelChannelId";// 用户模块-取消channelId接口
    public static final String RENEWSINGNIN = "renewSignIn";// 签到接口
    public static final String APPUSERCOUPONSV2 = "/zoneCode/appUserCouponsV2";// 使用优惠券抵扣
    public static final String APPPAYFORALIV2 = "appPayForAliV2";// 扫卡支付
    public static final String APPQUREYAD = "appQureyAd";   //广告
    public static final String APPPAYFEEDBACK = "appPayFeedback";//支付反馈接口
    public static final String APPPAYFEEDBACKSAVE = "appPayFeedbackSave";//支付反馈保存接口
    public static final String APPORDERDETAIL = "appOrderDetail";//根据订单查询计费信息
    /**
     * 商户端接口
     */
    public static final String PK_LOGIN = "merchantLog/login";// 登录
    public static final String PK_LOGOUT = "merchantLog/logout";// 登出
    public static final String PK_MAIN_SETTING = "merchantFunction/homeMaxPort";//主页设置信息
    public static final String PK_RESET_PASSWORD = "merchantLog/resetPassword";//重置密码
    public static final String PK_MSG_PUSH = "merchantLog/infoPush";//获取短信随机码

    // app应用ID
    public static final String APP_ID = "wx494442457975748a";
    //应用存储文件夹
    public static final String APP_FOLDER_NAME = "ZTB";
    //Bundle KEY
    public static final String KEY_URL = "URL";
    public static final String KEY_TITLE = "TITLE";
    public static final String KEY_DESC = "DESC";
    public static final String KEY_CARINPARKING = "carInParking";
    public static final String KEY_PAYSUCCESS = "paySuccess";
    public static final String KEY_BASEMODEL = "baseModel";
    public static final String KEY_ACCOUNT_INFO = "AccountInfo";
    //绑定车辆所要判断的key，1为我的车辆，2为社区车辆
    public static final String KEY_MODE = "mode";
    public static final String KEY_CARDID = "cardId";

    public static final String KEY_FROM = "from";
    /**
     * 广告类型
     */
    public static final String AD_TYPE_SPLASH = "1";    //启动页3秒广告
    public static final String AD_TYPE_HOME = "2";  //首页广告（兼容原广告接口 图片比例宽：720 高：260）
    public static final String AD_TYPE_HOME_DROP_DOWN = "3";    //首页自动下弹广告
    public static final String AD_TYPE_ACTION = "4";    //首页活动弹窗广告(图片比例应该是宽：840 高：980)
    public static final String AD_TYPE_HOME_SLIDE_MENU = "5";   //左侧菜单广告（图片比例宽：720 高：200）
    public static final String AD_TYPE_PAY_WECHAT = "6";   //支付页面广告 微信支付
    public static final String AD_TYPE_PAY_ALIPAY = "7";   //支付页面广告 支付宝支付
    public static final String AD_TYPE_PAY = "8";   //支付页面广告 APP支付需要加8

    /**
     * 接口参数
     *
     * @author chenming
     */
    public static class InterfaceParam {
        public static final String PRODUCTID = "productId";
        // 卡片ID
        public static final String CARSN = "carSN";
        // 车场号
        public static final String PARKCODE = "parkCode";
        // 车牌号牌
        public static final String CARNO = "carNo";
        // 用户名
        public static final String USERNAME = "userName";
        public static final String TYPE = "type";
        public static final String PAYTYPE = "payType";
        // 设备号
        public static final String DEVIDEINFO = "deviceInfo";
        // 企业编号
        public static final String ITDCODE = "ltdCode";
        // 交易类型
        public static final String TRADETYPE = "tradeType";
        // 商品描述
        public static final String BODY = "body";
        // 订单总金额
        public static final String TOTALFRR = "totalFee";
        // 企业号ID
        public static final String LTDCODE = "ltdCode";
        public static final String CHANNELID = "channelId";
        // 手机系统 3表示Android，4表示IOS
        public static final String PHONETYPE = "phoneType";
        // 卡片ID
        public static final String CARDID = "cardId";
        public static final String MOBILEPHONE = "mobilePhone";
        public static final String IDS = "ids";
        public static final String CARINTIME = "carInTime";
        //计费编码;当为4444时，都是无需缴费，其中值是需要缴费
        public static final String NO_NEED_PAY = "4444";
        public static final String PAY_SUCCESS = "8888"; // 代表已缴费成功
        public static final String PAY_TYPE_FEEDBACK = "1"; //错误反馈
        public static final String PAY_TYPE_ORDER_DOUBT = "2";  //订单疑问
        public static final String DATA = "data";  //data
        public static final String APPPAYFEEDBACKID = "appPayFeedbackId";  //反馈id
        public static final String CONTENT = "content";  //其他手写的反馈内容
        public static final String ORDERNO = "orderNo";  //订单主键ID或微信预支付订单prepayid的值
        public static final String APPORDERID = "appOrderId";  //只有是订单反馈时，就存在这个订单id;

        public static final String ACCOUNT = "account";
        public static final String PASSWORD = "password";

        public static final String TOKEN = "token";

    }

    /**
     * 网络错误码
     *
     * @author chenming
     */
    public static class ERRORCODE {
        public static final int UNKNOWN = 10000;
        public static final int JSON_PRASE_EXCEPTION = 10004;
        public static final int HTTP_EXCEPTION = 10005;
        public static final int DOWNLOAD_ERROR = 10008;
        public static final int OUT_OF_MEMORY = 10009;
        public static final int IO_EXCEPTION = 10010;
        public static final int NAME_NOT_FOUND_EXCEPTION = 10011;
        public static final int NO_NETWORK = 10013;
        public static final int DEFAULT = 10014;
    }

    /**
     * 广播标志
     */
    public static class BROADCAST {
        public static final String ACCOUNT_INFO = "com.ajb.merchants.BROADCAST.ACCOUNT_INFO";
    }
}

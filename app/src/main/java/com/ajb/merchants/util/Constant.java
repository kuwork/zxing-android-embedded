package com.ajb.merchants.util;

import com.util.PathManager;

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
    //拍照requestCode
    public static final int REQ_CODE_CAPTURE = REQ_CODE_BASE + 4;
    //	扫描
    public static final int REQ_CODE_SCAN = REQ_CODE_BASE + 5;
    //我的车辆绑定车牌
    public static final int REQ_CODE_MINE_BIND_CARNO = REQ_CODE_BASE + 6;
    //社区服务车辆绑定车牌
    public static final int REQ_CODE_SOCIETY_BIND_CARNO = REQ_CODE_BASE + 7;
    //登陆
    public static final int REQ_CODE_LOGIN = REQ_CODE_BASE + 9;
    public static final int REQ_CODE_SHARE = REQ_CODE_BASE + 10;
    public static final int REQ_CODE_NAV = REQ_CODE_BASE + 11;
    //附近车场 定位按钮
    public static final int REQ_CENTER_LOCATION = REQ_CODE_BASE + 12;
    public static final int REQ_NAV_LOCATION = REQ_CODE_BASE + 13;
    public static final int REQ_CODE_STOP_SCAN = REQ_CODE_BASE + 14;
    public static final int REQ_CODE_START_SCAN = REQ_CODE_BASE + 15;
    //权限访问码
    public static final int PM_CAMERA = 1;//摄像头权限
    public static final int PM_LOCATION = 2;//GPS权限
    //接口地址
//    public static final String SERVER_URL = "http://app.eanpa-gz-manager.com/";//正式环境
    public static final String SERVER_URL = "http://115.29.211.91:28082/";//正式库测试环境
    //    public static final String SERVER_URL = "http://218.244.141.147:13100/";// 开发
    //    public static final String NEWSERVER_URL = "http://218.244.141.147:13901";// 心跳开发
    public static final String PK_LOGIN = "appLogin";// 登录
    public static final String ChangePassword = "appChangePassword";// 修改密码
    public static final String APPSUGGEST = "appSuggest";// 意见反馈
    public static final String PK_APPCODE = "appCode";// 获取验证码
    public static final String APPFORGET = "appForget";// 忘记密码
    public static final String APPREGISTER = "appRegister";// 忘记密码

    public static final String APPCHANGECARNO = "appChangeCarNo";// 更改车牌绑定
    public static final String APPSEARCH = "appSearch";// 搜索附近停车场
    public static final String APPNEWSEARCH = "appNewSearch";// 搜索附近停车场NEW
    public static final String APPMORECAR_LIST = "appMoreCar/list";// 获取绑定车位车牌列表(社区服务)

    public static final String APPMORECAR_ADD = "appMoreCar/add";// 添加绑定车位车牌列表(社区服务)
    public static final String APPMORECAR_EDIT = "appMoreCar/edit";// 编辑绑定车位车牌列表(社区服务)
    public static final String APPMORECAR_DELETE = "appMoreCar/delete";// 删除绑定车位车牌列表(社区服务)
    public static final String APPGETCARNO = "appGetCarNo";// 获取绑定车牌号
    public static final String APPCARCOUNT = "appCarCount";// 车牌计费
    public static final String APPCARDCOUNT = "appCardCount";// 扫卡支付
    public static final String APPSEARCHCARCOUNT = "appSearchCarCount";//(新)查询计费接口
    public static final String APPPAY = "appPay";// 扫卡支付
    public static final String APPPAYV2 = "appPayV2";// (新)停车缴费-微信支付接口（调用微信统一下单接口）
    public static final String APPCARNO = "appCarNo";// 扫卡支付

    public static final String APPRENEWSHOULDPAY = "appRenewShouldPay";// 获取会员一个月多少钱费用
    public static final String APPRENEWPAY = "appRenewPay";// 获取会员激活预支付订单
    public static final String USERRECHARGE = "userRecharge";// 获取会员激活预支付订单
    public static final String APPGETRENEWINFO = "appGetRenewInfo";// 获取会员激活预支付订单
    public static final String ORDERPARKING = "appParkingSpaceQry";// 获取用户分享车位列表信息、
    public static final String PARKINGNUMBER = "parkingDetails";// 查询某个以分享车场详细信息接口
    public static final String SAVEORDERIF = "bookParking";// 预定车位订单信息的保存接口
    public static final String APPPARKINGSPACEQRY = "shareCarport/list";// 查询所有已分享停车场信息接口接口
    public static final String SHARECARPORTADD = "shareCarport/add";// 新增分享停车场信息的接口
    public static final String SHARECARPORTMYLIST = "shareCarport/mylist";// 我的分享列表接口
    public static final String SHARECARPORTGETSHARE = "shareCarport/getShare";// 获取用户分享设置的接口
    public static final String SHARECARPORTGETMODIFYSHARE = "shareCarport/modifyShare";// 保存分享设置的接口
    public static final String ORDERLIST = "shareCarport/mylist";// 用户已预约的订单列表
    public static final String ORDER = "shareCarport/getShare";// 查看某个预约订单

    public static final String ZONECODELIST = "zoneCode/list";// 用户已预约的订单列表
    public static final String SYNCTOAPP = "syncToApp";// 心跳

    public static final String PARENTCODE = "parentCode";// 获取区域码和城市码
    public static final String ADDORDER = "addOrder";// 获取区域码和城市码

    public static final String RENEWSIGNIN = "renewSignIn";// 获取区域码和城市码
    public static final String APPSAVECHANNELID = "appSaveChannelId";// 用户模块-保存channelId接口

    public static final String APPDELCHANNELID = "appDelChannelId";// 用户模块-取消channelId接口

    public static final String BILL = "orderList";// 消费记录-我的账单接口

    public static final String RENEWSINGNIN = "renewSignIn";// 签到接口

    public static final String DELETEORDER = "cancelOrder";// 取消订单接口

    public static final String MONTHCARD = "monthCard";// 是否月租卡/monthCard?userName=13416171757&t=1
    public static final String RENEWALMONTHCARD = "renewalMonthCard";// 给月租卡续期缴费
    public static final String CANCELCHECK = "shareCarport/cancelShareCarport";// 取消审核状态
    public static final String USERCARPORTINCOME = "shareCarport/userCarportIncome";// 收益列表
    public static final String USERINCOME = "shareCarport/userIncome";// 车位收益列详情
    public static final String ZoneCodeVersion = "/zoneCode/appUserVersion";// 区域代码版本
    public static final String APPQRYCOUPONS = "/zoneCode/appQryCoupons";// 优惠券
    public static final String APPUSERCOUPONS = "/zoneCode/appUserCoupons";// 使用优惠券抵扣
    public static final String APPUSERCOUPONSV2 = "/zoneCode/appUserCouponsV2";// 使用优惠券抵扣
    public static final String COUPONCODE = "/zoneCode/appUserInvite";// 使用优惠码

    public static final String APPMEMBERCOUPONS = "/zoneCode/appMemberCoupons";// 使用优惠码
    public static final String APPPAYFORALI = "appPayForAli";// 扫卡支付
    public static final String APPPAYFORALIV2 = "appPayForAliV2";// 扫卡支付
    public static final String APPQUREYAD = "appQureyAd";   //广告
    public static final String QRY_PARKING_RECORD = "qryParkingRecord";//停车记录
    public static final String APPPAYFEEDBACK = "appPayFeedback";//支付反馈接口
    public static final String APPPAYFEEDBACKSAVE = "appPayFeedbackSave";//支付反馈保存接口
    public static final String APPORDERDETAIL = "appOrderDetail";//根据订单查询计费信息
    public static final String APP_USER_INVITE_CHECK = "appUserInviteCheck";//有奖优惠设置
    public static final String VERSION = "1.0";
    public static String outTradeNo;

    public static final Boolean DEBUGMODE = false;// 是否是调试模式

    public static final String AUTH = "auth";

    public static final int RQF_PAY = 1;

    public static final int RQF_LOGIN = 2;
    // SDCard路径
    public static final String SD_PATH = PathManager.getSdcardDir();

    // 图片存储路径
    public static final String BASE_PATH = SD_PATH + "/com.ajb.pk/";
    // 缓存图片路径
    public static final String BASE_IMAGE_CACHE = BASE_PATH + "cache/images/";
    // app应用ID
    public static final String APP_ID = "wx494442457975748a";

    // APP秘钥
    public static final String MCH_ID = "1230274101";// yum
    public static final String API_KEY = "5799fba4d7c2df619ef75cd3181a4550";// yum

    public static final int FROMSEARCHPARKING = 1001;// searchparking页面跳转至city页面

    public static final int MONTHPAY = 1002;// 绑定月租缴费页面页面跳转至city页面
    public static final int IEMONTHPAY = 1003;// 代付月租缴费页面页面跳转至city页面
    public static final int RENEWALS = 1004;// 续费
    //应用存储文件夹
    public static final String APP_FOLDER_NAME = "ZTB";

    //Bundle KEY
    public static final String KEY_URL = "URL";
    public static final String KEY_TITLE = "TITLE";
    public static final String KEY_CARPARK = "CARPARK";
    public static final String KEY_LATITUDE = "LATITUDE";
    public static final String KEY_LONGITUDE = "LONGITUDE";
    public static final String KEY_REQPARAMS = "reqParams";
    public static final String KEY_CARINPARKING = "carInParking";
    public static final String KEY_PAYSUCCESS = "paySuccess";
    public static final String KEY_BASEMODEL = "baseModel";
    public static final String KEY_ACCOUNT_INFO = "AccountInfo";

    //绑定车辆所要判断的key，1为我的车辆，2为社区车辆
    public static final String KEY_MODE = "mode";
    public static final String KEY_CARDID = "cardId";
    public static final int ENABLE_OPEN = 1;    //社区车辆月租服务开启
    public static final int ENABLE_CLOSE = 0;   //社区车辆月租服务关闭

    /**
     * 首页广告广播的Action
     */
    public static final String BANNER_ACTION_HOME = "com.ajb.ztb.banner";
    //微信支付广播action
    public static final String WECHAT_PAY_ACTION = "com.ajb.ztb.wechat.pay";

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
        //优惠码
        public static final String INVITE = "invite";
        //被邀请人
        public static final String INVITEUSER = "inviteUser";
        // 卡片ID
        public static final String CARSN = "carSN";
        // 车场号
        public static final String PARKCODE = "parkCode";
        // 搜索距离
        public static final String DISTANCE = "distance";
        // 纬度
        public static final String CENTERLAT = "centerLat";
        // 经度
        public static final String CENTERLON = "centerLon";
        // 验证码
        public static final String CODE = "code";
        // 车牌号牌
        public static final String CARNO = "carNo";
        // 手机号码
        public static final String PHONENO = "phoneNo";
        // 意见
        public static final String SUGGESTION = "suggestion";
        // 满意度
        public static final String SATISFACTION = "satisfaction";
        // 用户
        public static final String USERACCOUNT = "userAccount";
        // 确认新密码
        public static final String CONFIRMPASSWORD = "confirmPassword";
        // 新密码
        public static final String NEWPASSWORD = "newPassword";
        // 旧密码
        public static final String OLDPASSWORD = "oldPassword";
        // 用户名
        public static final String USERNAME = "userName";
        // 密码
        public static final String PASSWORD = "password";
        // 秘钥
        public static final String OPENKEY = "openKey";
        // 操作系统
        public static final String OSNAME = "OSName";
        // 软件版本号
        public static final String VERSIONNUM = "versionNum";
        // 车牌操作类型
        public static final String CAHNGECARTYPE = "type";
        public static final String TYPE = "type";
        public static final String PAYTYPE = "payType";

        public static final String CAHNGECARTYPEEDIT = "EDIT";
        public static final String CAHNGECARTYPEDEL = "DEL";
        public static final String CAHNGECARTYPEADD = "ADD";
        // 设备号
        public static final String DEVIDEINFO = "deviceInfo";
        // noncestr微信支付用的到随机串
        public static final String WXNONCESTR = "noncestr";
        // 企业编号
        public static final String ITDCODE = "ltdCode";
        // 交易类型
        public static final String TRADETYPE = "tradeType";

        // 用于微信财付通加密
        public static final String PARTNERKEY = "partnerkey";
        // 商品描述
        public static final String BODY = "body";
        // 订单总金额
        public static final String TOTALFRR = "totalFee";
        public static final String MOBLIE = "moblie";
        // 状态
        public static final String STATUS = "status";
        // 月份
        public static final String MONTHS = "months";
        // 会员等级
        public static final String LEVEL = "level";
        // 车场ID
        public static final String PARKINGID = "parkingId";

        // 车位号
        public static final String CARPORTNO = "carportNo";

        // 分享id
        public static final String SHAREID = "shareId";

        // 分享id(取消分享的审核)
        public static final String SHAREID2 = "id";

        // 订单id
        public static final String ORDERID = "id";

        // 1:预约车位,2：拼车位
        public static final String SHAREWAY = "shareWay";

        // 省份
        public static final String PROVINCE = "province";

        // 城市
        public static final String CITY = "city";
        // 乡镇
        public static final String TOWN = "town";
        // 单价排序
        public static final String PARKINGPRICE = "parkingPrice";
        // 空车位排序
        public static final String CARPORTKEEPCOUNT = "carportKeepCount";

        // 当前页
        public static final String PAGE = "page";

        public static final String STATE = "state";

        // 当页记录数
        public static final String ROWS = "rows";
        //
        public static final String OPER = "oper";

        public static final String REFRESH = "refresh";
        public static final String CURRENTTIME = "currentTime";
        // 车位ID
        public static final String CARPORTCODE = "carportCode";

        // 企业号ID
        public static final String LTDCODE = "ltdCode";

        // 车场类型 member为1的时候是会员车场，2是折扣车场，3全部车场
        public static final String MEMBER = "member";
        public static final String PARENTCODE = "parentCode";
        public static final String CHANNELID = "channelId";
        // 手机系统 3表示Android，4表示IOS
        public static final String PHONETYPE = "phoneType";

        // 城市编码
        public static final String CITYCODE = "cityCode";

        // 省份编码
        public static final String PROVINCECODE = "provinceCode";
        // 区域编码
        public static final String AREACODE = "areaCode";
        // 车场名
        public static final String NAME = "name";

        public static final String ORDERNAME = "orderName";

        public static final String ORDERBY = "orderBy";

        // 支付宝帐号
        public static final String ALIPAYACCOUT = "alipayAccount";
        // 支付宝姓名
        public static final String ALIPAYREALNAME = "alipayRealname";
        // 金钱
        public static final String MONEY = "money";
        // 卡片ID
        public static final String CARDID = "cardId";
        public static final String MOBILEPHONE = "mobilePhone";
        public static final String IDS = "ids";
        public static final String ID = "id";
        public static final String TOTALFEE = "totalFee";
        public static final String CARINTIME = "carInTime";

        public static final String ENABLE = "enable";

        public static final String PREPAYID = "prepayId";

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
        public static final String ACCOUNT_INFO = "com.ajb.anjubao.intelligent.BROADCAST.ACCOUNT_INFO";

    }
}

package com.weiwei.base.dataprovider;

import android.content.res.Resources;
import android.os.Environment;

import com.hwtx.dududh.R;
import com.weiwei.base.application.VsApplication;

import java.io.File;

public class DfineAction {

    /**
     * 资源常量
     */
    public final static Resources RES = VsApplication.getContext().getResources();
    /**
     * 广告返回头文件名
     */
    public final static String image_head = RES.getString(R.string.brandid) + "adspace://";
    public final static String brand_id = RES.getString(R.string.brandid);
    /**
     * 包名
     */
    public final static String packagename = VsApplication.getContext().getResources().getString(R.string.packagename);

    /**
     * 分享商品地址
     */
    public final static String shareUrl = VsApplication.getContext().getResources().getString(R.string.url_share);
    /**
     * 启动拉取商品配置是否成功
     */
    public static boolean IsStartGoodsConfig = false;
    /**
     * 消息信息更新广播。在更多界面动态跟新提醒数字
     */
    public final static String ACTION_UPDATENOTICENUM = packagename + ".updatenoticenum";
    /**
     * 电话文件存放目录
     */
    public final static String mWldhRootFilePath = Environment.getExternalStorageDirectory() + File.separator;
    /**
     * 电话文件存放目录
     */
    public final static String mWldhFilePath = Environment.getExternalStorageDirectory() + File.separator + "data" + File.separator + "wldh" + File.separator;
    /**
     * 升级图片存放目录
     */
    public final static String updateFilePath = Environment.getExternalStorageDirectory() + File.separator + "data" + File.separator + "wldh" + File.separator + "update" + File
            .separator;
    /**
     * 下载完后启动第三方未安装apk广播
     */
    public final static String VS_ACTION_STARTPLUGIN = packagename + "_startplugin";

    /**
     * 授权者
     */
    public static String projectAUTHORITY = packagename + ".provider.customprovider";
    /**
     * 有帐号就使用uid加密。没有就使用key加密方式
     */
    public static final String authType_AUTO = "auto";
    /**
     * UID加密方式
     */
    public static final String authType_UID = "uid";
    /**
     * 融云appkey
     * <p>
     * 融云1.3.4之后的SDK, key配置放在AndroidManifest.xml
     */
    public final static String app_key_ry = "sfci50a7cr1oi";//z3v5yqkbvw440 --- AMS |  0vnjpoadnxoqz --- IMServer//key配置放在AndroidManifest.xml
    //
    /**
     * key加密方式
     */
    public static final String authType_Key = "key";
    /**
     * 注册送套餐广播
     */
    public final static String ACTION_REGSENDMONEY = packagename + ".regsendmoeny";
    public static String NETWORK_INVISIBLE = "当前网络不可以用，请检查网络!";
    /**
     * 修改个人资料成功
     */
    public final static String ACTION_PUSHINFOSUCCESS = packagename + ".infosuccess";
    /**
     * 返回生日日期
     */
    public final static String ACTION_GETTIME = packagename + ".gettimesuccess";
    /**
     * 显示通话记录
     */
    public final static String ACTION_SHOW_CALLLOG = packagename + ".show.calllog";
    /**
     * 更新通话记录
     */
    public final static String ACTION_UPDATE_CALLLOG = packagename + ".update.calllog";
    public final static String ACTION_DIAL_CALL = packagename + ".dial.call"; // 有拨号操作时通知DialActivity的广播
    public static String phoneNumberUrl = "http://wap.uuwldh.com/location/PhoneNumberQuery.dat";// 归属地下载地址
    public final static String CURRENT_LOGD_CONTACTLISTACTION = packagename + ".currentloadcontentlistaction"; // 当前正在加载联系人
    public final static String REFERSHLISTACTION = packagename + ".refreshlistaction"; // 重新刷新联系人列表
    public final static String ACTION_LOAD_NOTICE = packagename + ".loadnotice"; // 加载消息数据
    public final static String ACTION_SHOW_NOTICE = packagename + ".shownotice"; // 显示消息数据
    public final static String ACTION_SHOW_MESSAGE = packagename + ".showmessage"; // 显示消息数据
    public final static String ACTION_QUERY_MESSAGE = packagename + ".querymessage"; // 查找
    public final static String ACTION_SHOW_NOTICEACTIVITY = "android.intent.action." + packagename + ".noticeactivity"; // 通知栏点击后弹出界面
    /**
     * 新消息信息更新广播。动态跟新提醒数字
     */
    public final static String ACTION_UPDA_MESSAGE_COME = packagename + ".update.message.come";
    /**
     * 两个包名
     */
    public final static String PACKAGE_FIRST = packagename + "";
    public final static String PACKAGE_SECOND = packagename + ".dh";
    public static String InviteFriendInfo = "分享嘟嘟电话";//,下载地址：http://120.24.181.188:9290
    /**
     * QQ登录用到的appid
     */
    public static final String QqAppId = "1105818982";// jxw:1104475687  old:1102351939//androidManifest.xml也需要同步修改
    /**
     * QQ分享用到的KEy
     */
    public static final String QqAppKey = "crWql6x9q2wHd0EX";// jxw:kJGpxlRtjLL0AByn  old:FcKNdEwvSF7gCmTV

    public final static String com_wap = "http://app.ydtjhy.com";
    public final static String phone_wap = "http://app.ydtjhy.com";

    public final static String scheme_head = "vsadspace://";
    public final static String pv = "android";

    public final static String brandid = VsApplication.getContext().getResources().getString(R.string.brandid); // vs

    public static String uri_verson = "1.0";

    /**
     * 加密key
     */
    public final static String passwad_key = "abd762f7ce28c36e";
    public final static String md_passwad_key = "1h3l56789o!@#$%^";

    public final static String key0 = "d3^8~5Oa5f$Ic6?d";
    public final static String key1 = "81f%d65a9Q;Xc*nm";
    public final static String key2 = "hd@w1nd)eTk=sz?a";
    public final static String key3 = "uc3#mbr0_nol!6vk";
    public final static String key4 = "ec.v03GP(DTS+Yow";
    public final static String key5 = "ms<6btw:col7r-2i";
    public final static String key6 = "kit@c5wml!u9aro~";
    public final static String key7 = "vz,1OL?97Ntb>XQ0";
    public final static String key8 = "a&c0mlv;uiF6d/r3";
    public final static String key9 = "2sp#bz^a6c,d1int";


    /**
     * 保存SD帐号密码的加密key
     */
    public final static String passwad_key2 = "keepc_mobilephone^pwd";
    public static String AUTO_REG_MARK = "k";// 自动注册来源标志
    public final static String WAPURI = "http://app.ydtjhy.com";
    public static String invite = "2";
    public static String partner = "l";
    public final static String defaultResult = "{\"result\":-99,\"reason\":\"连接服务器失败!\"}";
    // 首页面action
    public static String goMainAction = "android.intent.action.weishuo";
    /**
     * 默认充值方式
     */
    public final static String[][] defaultRegType = {{"移动充值卡", "8", "701"}, {"联通充值卡", "10", "702"}, {"支付宝安全支付", "35", "704"}, {"微信支付", "800", "800"}, {"支付宝网页快捷支付", "236",
            "707"}, {"银行卡/信用卡支付", "223", "705"}};
    //    public static final String WEIXIN_APPID = "wx8e7098b41e7ace89"; // 微信分配的appid
    public static String WEIXIN_MCH_ID = "1423406302"; // 商户ID
    public static String WEIXIN_APPID = "wxefb0bdad32cbbb70"; // 最新的AppID, 支持微信支付功能.
    public static String WEIXIN_APPSECRET = "861bce36600c35d8f8e1bfd85dc2700a"; // 最新的AppSecret, 支持微信支付功能.API秘钥的值
    public static String WEIXIN_API_KEY = "8888777766665555DDDDCCCCbbbbaaaa"; // 秘钥

    public static String GOODS_WEIXIN_MCH_ID = "1423406302"; // 商户ID
    public static String GOODS_WEIXIN_APPID = "wxefb0bdad32cbbb70"; // 最新的AppID, 支持微信支付功能.
    public static String GOODS_WEIXIN_APPSECRET = "861bce36600c35d8f8e1bfd85dc2700a"; // 最新的AppSecret, 支持微信支付功能.API秘钥的值
    public static String GOODS_WEIXIN_API_KEY = "8888777766665555DDDDCCCCbbbbaaaa"; // 秘钥



    public static final String FIR_API_TOKEN = "64f60aae30b93b0b2e3b07040b3aac50"; // FIR升级token
    /**
     * 微信公众平台
     */
    public static final String WEIXIN_SHARE_APPID = "wxefb0bdad32cbbb70";

    public static final String WEIXIN_SHARE_APPSECRET = "861bce36600c35d8f8e1bfd85dc2700a";

    public static final String WEIXIN_SHARE_API_KEY = "8888777766665555DDDDCCCCbbbbaaaa";
    /**
     * 直拨设置文字广播
     */
    public final static String VS_CALLING_END = packagename + "callingend";
    /**
     * 直拨设置时间更新
     */
    public final static String VS_CALLING_TIME = packagename + "callingtime";
    /**
     * 直拨设置响铃文字
     */
    public final static String VS_CALLING_RING = packagename + "callingring";

    /**
     * 客服appkey
     */
    public final static String ZHICHI_APP_KEY = "fc347cc491174281ba625d21d3032ce0";

    /**
     * 客服appkey  zxp
     */
    public final static String CESHI_ZHICHI_APP_KEY = "6b3df06ec1f645999398e54882511ea5";

    public final static String VS_SHAARE_URL = shareUrl + "/?appId=" + brand_id;
    public final static String VS_SHAARE_URL_END = "#/";
    public final static String SHARE_CONTENT = "我在嘟嘟商城发现了一款特别优惠的商品，赶快来看看吧!";
    public final static String REFUEL_SHARE_CONTENT = "我发现了一款特别优惠的加油app，赶快来看看吧!";
    public final static String UPDATE_TO_BUY_MSG = "升级成为会员才有权限购买";
    public final static String VIPACTIVITY_CLASSNAME = "com.weiwei.account.VipMemberActivity";
    public final static String RECHARGE_CLASSNAME = "com.weiwei.base.activity.me.VsRechargeActivity";
    public final static String MAINACTIVITY_CLASSNAME = "com.weiwei.netphone.VsMainActivity";

    /**
     * 环信 easemob 相关参数
     */
    public final static String EASEMOB_TAGNAME = "huanxin_msg";
    public final static String EASEMOB_APPKEY = "1482190107068709#kefuchannelapp63003";
    public final static String EASEMOB_TENANTID = "63003";
    public final static String EASEMOB_CLIENTID = "YXA6cIyboBIfEemHFB24dykeLQ";
    public final static String EASEMOB_CLIENTSECRET = "YXA6eXemjfuZKQ-QkepaEoaoB9xUaFc";
    public final static String EASEMOB_IMSERVER = "kefuchannelimid_084119";
    public final static String EASEMOB_DEFAULT_PASSWORD = "123456";
    public final static String COMPANY_NAME = "深圳市易道科技有限公司";
}

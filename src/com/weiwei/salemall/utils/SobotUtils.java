package com.weiwei.salemall.utils;

import android.content.Context;
import com.weiwei.base.application.VsApplication;
import com.weiwei.base.dataprovider.VsUserConfig;
import com.weiwei.salemall.bean.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import static com.weiwei.base.dataprovider.DfineAction.ZHICHI_APP_KEY;

/**
 * @author: EDZ
 * @description:开启客服工具类
 */
public class SobotUtils {
    private static SobotUtils instance;
    private Context mContext;

    private SobotUtils(Context context) {
        this.mContext = context;
    }

    public synchronized static SobotUtils getInstance(Context context) {
        if (instance == null) {
            instance = new SobotUtils(context);
        }
        return instance;
    }

    public void startChat() {
//        Information info = new Information();
//        info.setAppkey(ZHICHI_APP_KEY);
//        //仅人工客服
//        info.setInitModeType(2);
//        //拿到用户信息
//        Map<String, String> customInfo = new HashMap<String, String>();
//        customInfo.put("phone", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber));
//        customInfo.put("uid", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId));
//        info.setCustomInfo(customInfo);
//        //返回是否显示客服评价
//        info.setShowSatisfaction(true);
//        info.setTel(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber));
//        info.setUid(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId));
//        //设置用户头像
//        info.setFace("http://qiniu.edawtech.com/head/logo_1.jpg");
//        VsApplication.getInstance().setNoReadMsg(0);
//        sendEventBusMsg();
//        SobotApi.startSobotChat(mContext, info);
    }

    private void sendEventBusMsg() {
        MessageEvent bean = new MessageEvent();
        bean.setMessage("noReadMsg");
        EventBus.getDefault().post(bean);
    }
}

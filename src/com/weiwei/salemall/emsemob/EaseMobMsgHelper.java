package com.weiwei.salemall.emsemob;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.hyphenate.helpdesk.easeui.util.Config;
import com.hyphenate.helpdesk.easeui.util.IntentBuilder;
import com.hyphenate.helpdesk.model.AgentIdentityInfo;
import com.hyphenate.helpdesk.model.ContentFactory;
import com.hyphenate.helpdesk.model.OrderInfo;
import com.hyphenate.helpdesk.model.QueueIdentityInfo;
import com.hyphenate.helpdesk.model.VisitorInfo;
import com.hyphenate.helpdesk.model.VisitorTrack;
import com.weiwei.base.dataprovider.VsUserConfig;

import static com.weiwei.base.application.VsApplication.mContext;

/**
 * @author Created by EDZ on 2019/1/7.
 *         Describe   环信客服辅助
 */

public class EaseMobMsgHelper {

    /**
     * 获取访客信息
     *
     * @return
     */
    public static VisitorInfo createVisitorInfo(String productNo) {
        VisitorInfo info = ContentFactory.createVisitorInfo(null);
        info.nickName(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId)).phone(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber)).description
                (productNo);
        return info;
    }

    /**
     * 获取访客轨迹
     *
     * @param context
     * @param
     * @return
     */
    public static VisitorTrack createVisitorTrack(Context context, String title, String price, String desc, String imageUrl, String itemUrl) {
        VisitorTrack track = ContentFactory.createVisitorTrack(null);
        track.title(title).price(price).desc(desc).imageUrl(imageUrl).itemUrl(itemUrl);
        return track;
    }

    /**
     * 获取订单信息
     *
     * @param context
     * @param
     * @return
     */
    public static OrderInfo createOrderInfo(Context context, String title, String price, String desc, String imageUrl, String itemUrl) {
        OrderInfo info = ContentFactory.createOrderInfo(null);
        info.title(title).orderTitle(title).price("价格：¥" + price).desc(desc).imageUrl(imageUrl).itemUrl(itemUrl);
        return info;
    }

    public static AgentIdentityInfo createAgentIdentity(String agentName) {
        if (TextUtils.isEmpty(agentName)) {
            return null;
        }
        AgentIdentityInfo info = ContentFactory.createAgentIdentityInfo(null);
        info.agentName(agentName);
        return info;
    }

    public static QueueIdentityInfo createQueueIdentity(String queueName) {
        if (TextUtils.isEmpty(queueName)) {
            return null;
        }
        QueueIdentityInfo info = ContentFactory.createQueueIdentityInfo(null);
        info.queueName(queueName);
        return info;
    }

    public static void skipToCustomFragment(Context context, Class<? extends Activity> cls, String easemobImserver) {
        Intent intent = new Intent(context, cls);
        Bundle bundle = new Bundle();
        bundle.putString(Config.EXTRA_SERVICE_IM_NUMBER, easemobImserver);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void startChat(Context context, String easemobImserver) {
        Intent intent = new IntentBuilder(context).setServiceIMNumber(easemobImserver).build();
        context.startActivity(intent);
    }
}

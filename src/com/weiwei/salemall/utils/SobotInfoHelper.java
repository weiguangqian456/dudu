package com.weiwei.salemall.utils;
import com.weiwei.base.dataprovider.VsUserConfig;

import java.util.HashMap;
import java.util.Map;

import static com.weiwei.base.application.VsApplication.mContext;
import static com.weiwei.base.dataprovider.DfineAction.ZHICHI_APP_KEY;

/**
 * @author Created by EDZ on 2019/1/7.
 *         Describe   智齿客服辅助
 */
public class SobotInfoHelper {

//    public static Information createInformation(String productNo, String productName, String productImage, String shareUrl) {
//        Information info = new Information();
//        info.setAppkey(ZHICHI_APP_KEY);
//        //仅人工客服
//        info.setInitModeType(2);
//        info.setCustomInfo(getCustomInfo(productNo));
//        info.setConsultingContent(getConsultingContent(productName, productImage, shareUrl));
//        //返回是否显示客服评价
//        info.setShowSatisfaction(true);
//        info.setTel(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber));
//        info.setUid(VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId));
//        //设置用户头像
//        info.setFace("http://qiniu.edawtech.com/head/logo_1.jpg");
//        return info;
//    }

//    private static ConsultingContent getConsultingContent(String productName, String productImage, String shareUrl) {
//        ConsultingContent consultingContent = new ConsultingContent();
//        consultingContent.setSobotGoodsTitle(productName);
//        consultingContent.setSobotGoodsImgUrl(productImage);
//        consultingContent.setSobotGoodsFromUrl(shareUrl);
//        consultingContent.setSobotGoodsDescribe(productName);
//        consultingContent.setSobotGoodsLable("");
//        return consultingContent;
//    }

    private static Map<String, String> getCustomInfo(String productNo) {
        Map<String, String> customInfo = new HashMap<String, String>();
        customInfo.put("phone", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_PhoneNumber));
        customInfo.put("uid", VsUserConfig.getDataString(mContext, VsUserConfig.JKey_KcId));
        customInfo.put("productNo", productNo);
        return customInfo;
    }
}

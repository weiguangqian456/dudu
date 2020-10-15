package com.weiwei.home.test;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.listener.SocializeListeners;

/**
 * @author : hc
 * @date : 2019/3/22.
 * @description: 实现分享方法
 */

public abstract class ShareListener2 implements SocializeListeners.SnsPostListener{
    @Override
    public void onStart() {

    }

    @Override
    public void onComplete(SHARE_MEDIA share_media, int i, SocializeEntity socializeEntity) {

    }
}

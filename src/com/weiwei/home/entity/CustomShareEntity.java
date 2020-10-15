package com.weiwei.home.entity;

import android.graphics.Bitmap;

/**
 * @author : hc
 * @date : 2019/3/21.
 * @description: 通用型-分享参数
 */

public class CustomShareEntity {
    private String shareContent;
    private String shareUrl;
    private String shareImage;
    private String shareTitle;
    private Bitmap shareBitmap;

    public CustomShareEntity(String shareContent, String shareUrl, String shareImage, String
            shareTitle) {
        this.shareContent = shareContent;
        this.shareUrl = shareUrl;
        this.shareImage = shareImage;
        this.shareTitle = shareTitle;
    }

    public CustomShareEntity(String shareContent, String shareUrl, String shareTitle, Bitmap
            shareBitmap) {
        this.shareContent = shareContent;
        this.shareUrl = shareUrl;
        this.shareTitle = shareTitle;
        this.shareBitmap = shareBitmap;
    }

    public String getShareContent() {
        return shareContent;
    }

    public void setShareContent(String shareContent) {
        this.shareContent = shareContent;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getShareImage() {
        return shareImage;
    }

    public void setShareImage(String shareImage) {
        this.shareImage = shareImage;
    }

    public String getShareTitle() {
        return shareTitle;
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public Bitmap getShareBitmap() {
        return shareBitmap;
    }

    public void setShareBitmap(Bitmap shareBitmap) {
        this.shareBitmap = shareBitmap;
    }
}

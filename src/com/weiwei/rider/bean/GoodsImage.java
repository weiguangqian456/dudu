package com.weiwei.rider.bean;

public class GoodsImage {

    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "GoodsImage{" +
                "imageUrl='" + imageUrl + '\'' +
                '}';
    }
}

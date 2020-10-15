package com.weiwei.rider.bean;

public class OrderImage {

    private String imagePath;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "OrderImage{" +
                "imagePath='" + imagePath + '\'' +
                '}';
    }
}

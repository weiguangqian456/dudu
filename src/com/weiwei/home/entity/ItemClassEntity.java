package com.weiwei.home.entity;

/**
 * @author : hc
 * @date : 2019/5/5.
 * @description:
 */

public class ItemClassEntity {

    String title = "标题";
    String imageUrl = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1556428906174&di=e9c79b4758d49e58c099875b9bec5f57&imgtype=0&src=http%3A%2F%2Fpic3.zhimg.com%2Fv2-e91f34627a3a98a07a41a3e11dda31f6_b.jpg";


    public ItemClassEntity(String title, String imageUrl) {
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

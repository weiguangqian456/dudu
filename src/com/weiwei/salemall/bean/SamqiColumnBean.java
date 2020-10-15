package com.weiwei.salemall.bean;

import android.text.TextUtils;

import com.weiwei.home.entity.ReadyItemRecommendEntity;

/**
 * @author Created by EDZ on 2018/7/28.
 *         商企栏目实体
 */

public class SamqiColumnBean implements ReadyItemRecommendEntity {
    private String id;
    private String appId;
    private String columnName;
    private String subTitle;

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    @Override
    public String getName() {
        return columnName;
    }


    @Override
    public String getExplain() {
        if(TextUtils.isEmpty(subTitle)){
            switch (columnName){
                case "全部":
                    subTitle = "精选好货";
                    break;
                case "品牌专区":
                    subTitle = "大牌优选";
                    break;
                case "食品酒水":
                    subTitle = "";
                    break;
                case "个护清洁":
                    subTitle = "";
                    break;
                case "美妆个护":
                    subTitle = "越来越美";
                    break;
                case "运动户外":
                    subTitle = "健康旅行";
                    break;
                case "服装配饰":
                    subTitle = "";
                    break;
                case "箱包手袋":
                    subTitle = "便携装扮";
                    break;
                case "家居百货":
                    subTitle = "简居日用";
                    break;
                case "家用电器":
                    subTitle = "科技生活";
                    break;
                case "手机数码":
                    subTitle = "电子设备";
                    break;
                case "红木家具":
                    subTitle = "";
                    break;
            }
        }
        return TextUtils.isEmpty(subTitle) ? columnName : subTitle;
    }
}

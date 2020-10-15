package com.weiwei.home.entity;

/**
 * 用户数据
 */
public class UserEntity {
    /**
     * appId
     */
    private String appId;
    /**
     * 用户id
     */
    private String uid;
    /**
     * 用户等级
     */
    private Integer level;
    /**
     * 等级名称
     */
    private String levelName;
    /**
     * 用户职位
     */
    private String rider;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getRider() {
        return rider;
    }

    public void setRider(String rider) {
        this.rider = rider;
    }
}

package com.weiwei.home.entity;

import java.util.List;

public class SeckillTab {
    public int offset;
    public int limit;
    public int total;
    public int size;
    public int pages;
    public int current;
    public boolean searchCount;
    public boolean openSort;
    public Object ascs;
    public Object descs;
    public Object orderByField;
    public boolean asc;
    public List<Records> records;

    public static class Records {
        public String id;
        public String activityName;
        public long startTime;
        public long endTime;
        public int status;
        public long createTime;
        public String createUser;
        public long updateTime;
        public String updateUser;
        public String appId;
        public boolean check;
        public String phone;
        public String invitationPhone;
        public String actualPhone;
        public String totalAmountGun;
        public long regTime;
        public String payTime;
        public String amountGun;

        @Override
        public String toString() {
            return "Records{" +
                    "id='" + id + '\'' +
                    ", activityName='" + activityName + '\'' +
                    ", startTime=" + startTime +
                    ", endTime=" + endTime +
                    ", status=" + status +
                    ", createTime=" + createTime +
                    ", createUser='" + createUser + '\'' +
                    ", updateTime=" + updateTime +
                    ", updateUser='" + updateUser + '\'' +
                    ", appId='" + appId + '\'' +
                    ", check=" + check +
                    ", phone='" + phone + '\'' +
                    ", invitationPhone='" + invitationPhone + '\'' +
                    ", totalAmountGun='" + totalAmountGun + '\'' +
                    ", regTime=" + regTime +
                    ", payTime='" + payTime + '\'' +
                    ", amountGun='" + amountGun + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "SeckillTab{" +
                "offset=" + offset +
                ", limit=" + limit +
                ", total=" + total +
                ", size=" + size +
                ", pages=" + pages +
                ", current=" + current +
                ", searchCount=" + searchCount +
                ", openSort=" + openSort +
                ", ascs=" + ascs +
                ", descs=" + descs +
                ", orderByField=" + orderByField +
                ", asc=" + asc +
                ", records=" + records +
                '}';
    }
}

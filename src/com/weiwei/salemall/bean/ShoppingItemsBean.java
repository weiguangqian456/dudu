package com.weiwei.salemall.bean;

import com.weiwei.home.Constant;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * @author: Created by EDZ on 2018/7/3.
 * 购物车列表item实体
 */

@Entity
public class ShoppingItemsBean {
    @Id(autoincrement = true)
    private Long _id;
    /**
     * 用户Id
     */
    private String userId;
    /**
     * 是否选中支付
     */
    private boolean ischeck = false;
    /**
     * 商品图
     */
    private String image;
    /**
     * 商品名称
     */
    private String name;
    /**
     * 商品数量
     */
    private int num;
    /**
     * 商城价
     */
    private String mallPrice;

    /**
     * 京东价
     */
    private String jdPrice;
    /**
     * 商品描述
     */
    private String desc;
    /**
     * 商品属性
     */
    private String property;
    /**
     * 商品编号
     */
    private String productNo;

    private String columnId;

    private String conversionPrice;
    private String coupon;
    private String isExchange;

    private String deliveryType;   //配送方式
    private String mImgaeUrl;   //配送方式

    private int logisticsMinNum;  //最低物流配送数量
    private int addNum;           //单次累加数量

    public String getImgaeUrl() {
        return mImgaeUrl;
    }

    public void setImgaeUrl(String mImgaeUrl) {
        this.mImgaeUrl = mImgaeUrl;
    }

    public int getLogisticsMinNum() {
        return logisticsMinNum;
    }

    public void setLogisticsMinNum(int logisticsMinNum) {
        this.logisticsMinNum = logisticsMinNum;
    }

    public int getAddNum() {
        return addNum;
    }

    public void setAddNum(int addNum) {
        this.addNum = addNum;
    }

    public String getDeliveryMsg() {
        return deliveryMsg;
    }

    public void setDeliveryMsg(String deliveryMsg) {
        this.deliveryMsg = deliveryMsg;
    }

    private String deliveryMsg;   //配送方式

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getConversionPrice() {
        return conversionPrice;
    }

    public void setConversionPrice(String conversionPrice) {
        this.conversionPrice = conversionPrice;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public String getIsExchange() {
        return isExchange;
    }

    public void setIsExchange(String isExchange) {
        this.isExchange = isExchange;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    @Generated(hash = 276275590)
    public ShoppingItemsBean() {
    }

    @Generated(hash = 1798249751)
    public ShoppingItemsBean(Long _id, String userId, boolean ischeck, String image, String name, int num, String mallPrice, String jdPrice, String desc, String property,
            String productNo, String columnId, String conversionPrice, String coupon, String isExchange, String deliveryType, String mImgaeUrl, int logisticsMinNum, int addNum,
            String deliveryMsg) {
        this._id = _id;
        this.userId = userId;
        this.ischeck = ischeck;
        this.image = image;
        this.name = name;
        this.num = num;
        this.mallPrice = mallPrice;
        this.jdPrice = jdPrice;
        this.desc = desc;
        this.property = property;
        this.productNo = productNo;
        this.columnId = columnId;
        this.conversionPrice = conversionPrice;
        this.coupon = coupon;
        this.isExchange = isExchange;
        this.deliveryType = deliveryType;
        this.mImgaeUrl = mImgaeUrl;
        this.logisticsMinNum = logisticsMinNum;
        this.addNum = addNum;
        this.deliveryMsg = deliveryMsg;
    }

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isIscheck() {
        return ischeck;
    }

    public void setIscheck(boolean ischeck) {
        this.ischeck = ischeck;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getMallPrice() {
        return mallPrice;
    }

    public void setMallPrice(String mallPrice) {
        this.mallPrice = mallPrice;
    }

    public String getJdPrice() {
        return jdPrice;
    }

    public String getJdPrice2() {
        return Constant.compareFactory[0] + "￥：" + jdPrice;
    }

    public void setJdPrice(String jdPrice) {
        this.jdPrice = jdPrice;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public boolean getIscheck() {
        return this.ischeck;
    }

    public String getMImgaeUrl() {
        return this.mImgaeUrl;
    }

    public void setMImgaeUrl(String mImgaeUrl) {
        this.mImgaeUrl = mImgaeUrl;
    }
}

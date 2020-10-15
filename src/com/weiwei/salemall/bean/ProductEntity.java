package com.weiwei.salemall.bean;

import com.weiwei.home.Constant;
import com.weiwei.home.utils.TextDisposeUtils;

import java.util.List;

/**
 * @author Created by EDZ on 2018/6/20.
 * 商品实体
 */

public class ProductEntity {
    private String productId;
    private String productName;
    private String productNo;
    private String spu;   //库存
    private String jdPrice;
    private String price;
    private String description;
    private String comprehensive;    //星级
    private List<String> images;     //预览图
    private String comments;         //评论
    private int totalComment;
    private String purchaseNote;     //购物说明
    private int type;
    private String picture;          //商品图
    private int contrastSource;     //对比价来源
    private int descType;            //描述类型
    private String jdUrl;            //京东链接Url
    private String sku;
    private String url;
    private String conversionPrice;  //兑换价格
    private String coupon;           //优惠券
    private String isExchange;        //是否兑换

    private List<String> videos;

    public String holidaySeckillProductId;

    /**
     * ================秒杀=====================
     **/
    private String seckillProductId;
    private String seckillPrice;
    private String startTime;
    private String endTime;
    private String now;
    private String stock;
    private String totalStock;
    private String status;
    private int minNum;    //秒杀商品最小购买数量
    private int maxNum;    //秒杀商品最大购买数量

    private int logisticsMinNum = 1;  //物流配送最低购买数量
    private int addNum = 1;           //物流单次累加数量

    public int courierMinNum = 1;  //快递配送最低购买数量
    public int courierMinAddNum = 1;           //快递单次累加数量

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

    public int getMinNum() {
        return minNum;
    }

    public void setMinNum(int minNum) {
        this.minNum = minNum;
    }

    public int getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    private String deliveryMode;

    public String getDeliveryMode() {
        return deliveryMode;
    }

    public void setDeliveryMode(String deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getJdUrl() {
        return jdUrl;
    }

    public void setJdUrl(String jdUrl) {
        this.jdUrl = jdUrl;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getTotalStock() {
        return totalStock;
    }

    public void setTotalStock(String totalStock) {
        this.totalStock = totalStock;
    }

    public String getSeckillProductId() {
        return seckillProductId;
    }

    public void setSeckillProductId(String seckillProductId) {
        this.seckillProductId = seckillProductId;
    }

    public String getSeckillPrice() {
        return seckillPrice;
    }

    public void setSeckillPrice(String seckillPrice) {
        this.seckillPrice = seckillPrice;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getNow() {
        return now;
    }

    public void setNow(String now) {
        this.now = now;
    }

    public String getIsExchange() {
        return isExchange;
    }

    public void setIsExchange(String isExchange) {
        this.isExchange = isExchange;
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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getContrastSource() {
        return contrastSource;
    }

    public void setContrastSource(int contrastSource) {
        this.contrastSource = contrastSource;
    }

    public int getDescType() {
        return descType;
    }

    public void setDescType(int descType) {
        this.descType = descType;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public String getSpu() {
        return spu;
    }

    public void setSpu(String spu) {
        this.spu = spu;
    }

    public String getJdPrice() {
//        return Constant.compareFactory[contrastSource] + ":" + TextDisposeUtils.dispseMoneyText(jdPrice);
        return TextDisposeUtils.dispseMoneyText(jdPrice);
    }

    public String getJdPrice2() {
        return jdPrice;
    }

    public void setJdPrice(String jdPrice) {
        this.jdPrice = jdPrice;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComprehensive() {
        return comprehensive;
    }

    public void setComprehensive(String comprehensive) {
        this.comprehensive = comprehensive;
    }

    public int getTotalComment() {
        return totalComment;
    }

    public void setTotalComment(int totalComment) {
        this.totalComment = totalComment;
    }

    public String getPurchaseNote() {
        return purchaseNote;
    }

    public void setPurchaseNote(String purchaseNote) {
        this.purchaseNote = purchaseNote;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<ProductCommentEntity> getProductCommentEntityList() {
        return productCommentEntityList;
    }

    public void setProductCommentEntityList(List<ProductCommentEntity> productCommentEntityList) {
        this.productCommentEntityList = productCommentEntityList;
    }

    private List<ProductCommentEntity> productCommentEntityList;

    //    private String[] compareFactory = new String[]{"亚马逊", "京东", "淘宝", "天猫", "苏宁", "当当", "国美", "其他"};
    public String getShopSource() {
        return "";
    }

    public List<String> getVideos() {
        return videos;
    }

    public void setVideos(List<String> videos) {
        this.videos = videos;
    }

    @Override
    public String toString() {
        return "ProductEntity{" +
                "productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", productNo='" + productNo + '\'' +
                ", spu='" + spu + '\'' +
                ", jdPrice='" + jdPrice + '\'' +
                ", price='" + price + '\'' +
                ", description='" + description + '\'' +
                ", comprehensive='" + comprehensive + '\'' +
                ", images=" + images +
                ", comments='" + comments + '\'' +
                ", totalComment=" + totalComment +
                ", purchaseNote='" + purchaseNote + '\'' +
                ", type=" + type +
                ", picture='" + picture + '\'' +
                ", contrastSource=" + contrastSource +
                ", descType=" + descType +
                ", jdUrl='" + jdUrl + '\'' +
                ", sku='" + sku + '\'' +
                ", url='" + url + '\'' +
                ", conversionPrice='" + conversionPrice + '\'' +
                ", coupon='" + coupon + '\'' +
                ", isExchange='" + isExchange + '\'' +
                ", videos=" + videos +
                ", holidaySeckillProductId='" + holidaySeckillProductId + '\'' +
                ", seckillProductId='" + seckillProductId + '\'' +
                ", seckillPrice='" + seckillPrice + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", now='" + now + '\'' +
                ", stock='" + stock + '\'' +
                ", totalStock='" + totalStock + '\'' +
                ", status='" + status + '\'' +
                ", minNum=" + minNum +
                ", maxNum=" + maxNum +
                ", logisticsMinNum=" + logisticsMinNum +
                ", addNum=" + addNum +
                ", courierMinNum=" + courierMinNum +
                ", courierMinAddNum=" + courierMinAddNum +
                ", deliveryMode='" + deliveryMode + '\'' +
                ", productCommentEntityList=" + productCommentEntityList +
                '}';
    }
}

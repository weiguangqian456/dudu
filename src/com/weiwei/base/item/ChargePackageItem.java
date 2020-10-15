package com.weiwei.base.item;

import com.weiwei.base.util.image.StringUtils;

/**
 * 充值套餐数据结构
 *
 * @author dell create at 2013-5-29下午12:57:37
 */
public class ChargePackageItem {
    /**
     * 用于排序
     */
    private int sort_id;
    /**
     * 产品类型brandid
     */
    private String bid;
    /**
     * 套餐ID
     */
    private String goods_id;
    /**
     * 是否推荐
     */
    private String recommend_flag;
    /**
     * 套餐名称
     */
    private String goods_name;
    /**
     * 套餐描述
     */
    private String des;
    /**
     * 套餐金额（分）
     */
    private String price;
    /**
     * 购买限制(0：表示无限制)
     */
    private String buy_limit;
    /**
     * 套餐类型
     */
    private String goods_type;
    /**
     * 商品是否支持累计支付的标志
     */
    private String total_flag;

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    /**
     * 折合单价

     */
    private String convert_price;
    /**
     * 新版本读取的商品名称
     */
    private String pure_name;
    /**
     * 新版本读取的赠送商品列表
     */
    private String present;

    public ChargePackageItem() {
    }

    public ChargePackageItem(int sort_id, String bid, String goods_id, String recommend_flag, String goods_name, String des, String price, String buy_limit, String goods_type,
                             String total_flag, String convert_price, String pure_name, String present) {
        this.bid = bid;
        this.goods_id = goods_id;
        this.recommend_flag = recommend_flag;
        this.goods_name = goods_name;
        this.des = des;
        this.price = price;
        this.sort_id = sort_id;
        this.buy_limit = buy_limit;
        this.goods_type = goods_type;
        this.total_flag = total_flag;
        this.convert_price = convert_price;
        this.pure_name = pure_name;
        this.present = present;
    }

    public int getSort_id() {
        return sort_id;
    }

    public void setSort_id(int sort_id) {
        this.sort_id = sort_id;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(String goods_id) {
        this.goods_id = goods_id;
    }

    public String getRecommend_flag() {
        return recommend_flag;
    }

    public void setRecommend_flag(String recommend_flag) {
        this.recommend_flag = recommend_flag;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getBuy_limit() {
        return buy_limit;
    }

    public void setBuy_limit(String buy_limit) {
        this.buy_limit = buy_limit;
    }

    public String getGoods_type() {
        return goods_type;
    }

    public void setGoods_type(String goods_type) {
        this.goods_type = goods_type;
    }

    public String getTotal_flag() {
        return total_flag;
    }

    public void setTotal_flag(String total_flag) {
        this.total_flag = total_flag;
    }

    public String getConvert_price() {
        return convert_price;
    }

    public void setConvert_price(String convert_price) {
        this.convert_price = convert_price;
    }

    public String getPure_name() {
        return pure_name;
    }

    public void setPure_name(String pure_name) {
        this.pure_name = pure_name;
    }

    public String getPresent() {
        return present;
    }

    public void setPresent(String present) {
        this.present = present;
    }
}
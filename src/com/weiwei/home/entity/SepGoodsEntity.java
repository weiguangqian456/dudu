package com.weiwei.home.entity;

import com.weiwei.salemall.bean.ProductBean;
import com.weiwei.salemall.bean.ResultEntity;

import java.util.List;

/**
 * @author Administrator.
 * Description:
 * @date 2019/9/14.
 */

public class SepGoodsEntity{
    /**
     * data : {"offset":0,"limit":2147483647,"total":6,"size":4,"pages":2,"current":1,"searchCount":true,"openSort":true,"ascs":null,"descs":null,"orderByField":null,"records":[{"productId":"240da86579a244bdb16b679e96ca0631","productName":"D-TCL 空气净化器TKJ186F-JMA1","productNo":"201907121117529162","spu":78,"type":0,"picture":"shop/product/1562901389166.jpg","jdPrice":"4200","price":"4200","appId":"dudu","jdUrl":null,"contrastSource":7,"conversionPrice":null,"coupon":null},{"productId":"df0e3d9330244b47855f40adca9f5cb7","productName":"F-龙的（longde） 榨汁机 原汁机不锈钢机身 多功能料理机家用果汁机LD-GZ25A 黑色","productNo":"201907101554186710","spu":96,"type":0,"picture":"shop/product/1562745206122.jpg","jdPrice":"1580","price":"1580","appId":"dudu","jdUrl":null,"contrastSource":7,"conversionPrice":null,"coupon":null},{"productId":"0c93f492d1294fe4a55e821d7ed04c0e","productName":"D-TCL 便携蒸汽熨刷TR-SC008 衣物熨烫机","productNo":"201907111420362103","spu":315,"type":0,"picture":"shop/product/1562825916483.jpg","jdPrice":"960","price":"960","appId":"dudu","jdUrl":null,"contrastSource":7,"conversionPrice":null,"coupon":null},{"productId":"a05ca8f8ab46495ab9a7ad205aa34384","productName":"D-啄木鸟泰国乳胶颗粒枕-两只ZMN-RJZ-01","productNo":"201907131703357028","spu":120,"type":0,"picture":"shop/product/1563008543217.jpg","jdPrice":"2680","price":"2680","appId":"dudu","jdUrl":null,"contrastSource":7,"conversionPrice":null,"coupon":null}],"condition":null,"asc":true}
     * code : 0
     * msg : 获取数据成功
     */

    private DataBean data;
    private int code;
    private String msg;

    public DataBean getData(){
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean {
        /**
         * offset : 0
         * limit : 2147483647
         * total : 6
         * size : 4
         * pages : 2
         * current : 1
         * searchCount : true
         * openSort : true
         * ascs : null
         * descs : null
         * orderByField : null
         * records : [{"productId":"240da86579a244bdb16b679e96ca0631","productName":"D-TCL 空气净化器TKJ186F-JMA1","productNo":"201907121117529162","spu":78,"type":0,"picture":"shop/product/1562901389166.jpg","jdPrice":"4200","price":"4200","appId":"dudu","jdUrl":null,"contrastSource":7,"conversionPrice":null,"coupon":null},{"productId":"df0e3d9330244b47855f40adca9f5cb7","productName":"F-龙的（longde） 榨汁机 原汁机不锈钢机身 多功能料理机家用果汁机LD-GZ25A 黑色","productNo":"201907101554186710","spu":96,"type":0,"picture":"shop/product/1562745206122.jpg","jdPrice":"1580","price":"1580","appId":"dudu","jdUrl":null,"contrastSource":7,"conversionPrice":null,"coupon":null},{"productId":"0c93f492d1294fe4a55e821d7ed04c0e","productName":"D-TCL 便携蒸汽熨刷TR-SC008 衣物熨烫机","productNo":"201907111420362103","spu":315,"type":0,"picture":"shop/product/1562825916483.jpg","jdPrice":"960","price":"960","appId":"dudu","jdUrl":null,"contrastSource":7,"conversionPrice":null,"coupon":null},{"productId":"a05ca8f8ab46495ab9a7ad205aa34384","productName":"D-啄木鸟泰国乳胶颗粒枕-两只ZMN-RJZ-01","productNo":"201907131703357028","spu":120,"type":0,"picture":"shop/product/1563008543217.jpg","jdPrice":"2680","price":"2680","appId":"dudu","jdUrl":null,"contrastSource":7,"conversionPrice":null,"coupon":null}]
         * condition : null
         * asc : true
         */

        private int offset;
        private int limit;
        private int total;
        private int size;
        private int pages;
        private int current;
        private boolean searchCount;
        private boolean openSort;
        private Object ascs;
        private Object descs;
        private Object orderByField;
        private Object condition;
        private boolean asc;
        private List<Object> records;

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public int getCurrent() {
            return current;
        }

        public void setCurrent(int current) {
            this.current = current;
        }

        public boolean isSearchCount() {
            return searchCount;
        }

        public void setSearchCount(boolean searchCount) {
            this.searchCount = searchCount;
        }

        public boolean isOpenSort() {
            return openSort;
        }

        public void setOpenSort(boolean openSort) {
            this.openSort = openSort;
        }

        public Object getAscs() {
            return ascs;
        }

        public void setAscs(Object ascs) {
            this.ascs = ascs;
        }

        public Object getDescs() {
            return descs;
        }

        public void setDescs(Object descs) {
            this.descs = descs;
        }

        public Object getOrderByField() {
            return orderByField;
        }

        public void setOrderByField(Object orderByField) {
            this.orderByField = orderByField;
        }

        public Object getCondition() {
            return condition;
        }

        public void setCondition(Object condition) {
            this.condition = condition;
        }

        public boolean isAsc() {
            return asc;
        }

        public void setAsc(boolean asc) {
            this.asc = asc;
        }

        public List<Object> getRecords() {
            return records;
        }

        public void setRecords(List<Object> records) {
            this.records = records;
        }

    }
}

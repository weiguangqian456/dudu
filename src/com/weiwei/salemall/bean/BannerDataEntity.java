package com.weiwei.salemall.bean;

import java.util.List;

/**
 * @author Created by EDZ on 2018/8/16.
 * Banner Data层
 */

public class BannerDataEntity {
    private List<BannerImageEntity> top;
    public List<BannerImageEntity> store;
    private BannerImageEntity choice;
    private BannerImageEntity welfare;
    private BannerImageEntity seckill;
    private OptimalBean optimal;

    public List<BannerImageEntity> business;       //商企 business  代表数字 7
    public List<BannerImageEntity> optimization;       //特惠百货  optimization 代表数字 8
    public List<BannerImageEntity> preferential;       //品牌特惠 preferential 代表数字 9
    public List<BannerImageEntity> specialSupply;       //嘟嘟特供 specialSupply 代表数字 11
    public List<BannerImageEntity> exchange;       //多彩兑换 exchange 代表数字 12
    public List<BannerImageEntity> holidaySkillBanner;       //嘟嘟秒杀 holidaySkillBanner
    public List<BannerImageEntity> oil;       //加油 oil


    public List<BannerImageEntity> digital;       //数码办公
    public List<BannerImageEntity> electric;       //家用电器
    public List<BannerImageEntity> pet;    //宠物用品

    public List<BannerImageEntity> jiu;       //中外名酒
    public List<BannerImageEntity> jiaju;       //家具百货
    public List<BannerImageEntity> xiangbao;    //精选箱包
    public List<BannerImageEntity> chufang;       //厨房用品
    public List<BannerImageEntity> shiping;       //食品生鲜




    public OptimalBean getOptimal() {
        return optimal;
    }

    public void setOptimal(OptimalBean optimal) {
        this.optimal = optimal;
    }

    public BannerImageEntity getSeckill() {
        return seckill;
    }

    public void setSeckill(BannerImageEntity seckill) {
        this.seckill = seckill;
    }

    public List<BannerImageEntity> getTop() {
        return top;
    }

    public void setTop(List<BannerImageEntity> top) {
        this.top = top;
    }

    public List<BannerImageEntity> getStore() {
        return store;
    }

    public void setStore(List<BannerImageEntity> store) {
        this.store = store;
    }

    public BannerImageEntity getChoice() {
        return choice;
    }

    public void setChoice(BannerImageEntity choice) {
        this.choice = choice;
    }

    public BannerImageEntity getWelfare() {
        return welfare;
    }

    public void setWelfare(BannerImageEntity welfare) {
        this.welfare = welfare;
    }

    public static class OptimalBean {
        /**
         * sec : {"id":"abc3c856d1384623a1b46372867b0dc5","name":"sec","skipType":0,
         * "imageUrl":"shop/banner/1553680511156.png","location":5,
         * "iosClassName":"SimBackActivity.1","androidClassName":"WFMiaoShaZQController",
         * "iosParams":null,"androidParams":null}
         * opt : {"id":"4df536408f614c5890291774bb4675f8","name":"opt","skipType":0,
         * "imageUrl":"shop/banner/1553680662874.png","location":5,
         * "iosClassName":"WFJRYXViewController","androidClassName":"SimBackActivity.3",
         * "iosParams":null,"androidParams":null}
         * jd : {"id":"5e122a14b1cd4c79b40675eb79beb14a","name":"jd","skipType":0,
         * "imageUrl":"shop/banner/1553680588324.png","location":5,
         * "iosClassName":"WFJDYXViewController","androidClassName":"com.weiwei.home.activity
         * .GoodsJdActivity","iosParams":null,"androidParams":null}
         */

        private BannerImageEntity sec;
        private BannerImageEntity opt;
        private BannerImageEntity jd;
        private BannerImageEntity vip;

        private BannerImageEntity one;
        private BannerImageEntity two;
        private BannerImageEntity three;
        private BannerImageEntity four;

        public BannerImageEntity getOne() {
            return one;
        }

        public void setOne(BannerImageEntity one) {
            this.one = one;
        }

        public BannerImageEntity getTwo() {
            return two;
        }

        public void setTwo(BannerImageEntity two) {
            this.two = two;
        }

        public BannerImageEntity getThree() {
            return three;
        }

        public void setThree(BannerImageEntity three) {
            this.three = three;
        }

        public BannerImageEntity getFour() {
            return four;
        }

        public void setFour(BannerImageEntity four) {
            this.four = four;
        }

        public BannerImageEntity getSec() {
            return sec;
        }

        public void setSec(BannerImageEntity sec) {
            this.sec = sec;
        }

        public BannerImageEntity getOpt() {
            return opt;
        }

        public void setOpt(BannerImageEntity opt) {
            this.opt = opt;
        }

        public BannerImageEntity getJd() {
            return jd;
        }

        public void setJd(BannerImageEntity jd) {
            this.jd = jd;
        }

        public BannerImageEntity getVip() {
            return vip;
        }

        public void setVip(BannerImageEntity vip) {
            this.vip = vip;
        }

    }

    @Override
    public String toString() {
        return "BannerDataEntity{" +
                "top=" + top +
                ", store=" + store +
                ", choice=" + choice +
                ", welfare=" + welfare +
                ", seckill=" + seckill +
                ", optimal=" + optimal +
                ", business=" + business +
                ", optimization=" + optimization +
                ", preferential=" + preferential +
                ", specialSupply=" + specialSupply +
                ", exchange=" + exchange +
                ", holidaySkillBanner=" + holidaySkillBanner +
                ", oil=" + oil +
                ", digital=" + digital +
                ", electric=" + electric +
                ", pet=" + pet +
                ", jiu=" + jiu +
                ", jiaju=" + jiaju +
                ", xiangbao=" + xiangbao +
                ", chufang=" + chufang +
                ", shiping=" + shiping +
                '}';
    }
}

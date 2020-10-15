package com.weiwei.salemall.bean;

import java.util.List;

public class RefuelDetail {
    public String source;
    public String gasId;
    public String gasName;
    public List<OilPriceList> oilPriceList;

    public static class OilPriceList {
        public String oilNo;
        public String oilName;
        public String oilType;
        public Double priceYfq;
        public Double priceOfficial;
        public Double priceGun;
        public List<GunNos> gunNos;
        public boolean check;

        public static class GunNos {
            public String gunNo;
            public boolean check;
        }
    }
}

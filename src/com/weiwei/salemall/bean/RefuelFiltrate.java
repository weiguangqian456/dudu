package com.weiwei.salemall.bean;

public class RefuelFiltrate {

    public RefuelFiltrate() {
    }

    public RefuelFiltrate(String filtrateName, String filtrateValue, boolean check) {
        this.filtrateName = filtrateName;
        this.filtrateValue = filtrateValue;
        this.check = check;
    }

    public String filtrateName;
    public String filtrateValue;
    public boolean check;
}

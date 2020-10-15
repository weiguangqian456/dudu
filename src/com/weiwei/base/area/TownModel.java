package com.weiwei.base.area;

public class TownModel {
    private String name;
    private String zipcode;

    public TownModel() {
        super();
    }

    public TownModel(String name, String zipcode) {
        super();
        this.name = name;
        this.zipcode = zipcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    @Override
    public String toString() {
        return "TownModel [name=" + name + ", zipcode=" + zipcode + "]";
    }

}

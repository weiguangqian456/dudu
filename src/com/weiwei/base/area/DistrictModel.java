package com.weiwei.base.area;

import java.util.List;

public class DistrictModel {
    private String name;
    private List<TownModel> townList;

    public DistrictModel() {
        super();
    }

    public DistrictModel(String name, List<TownModel> townList) {
        super();
        this.name = name;
        this.townList = townList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TownModel> getTownList() {
        return townList;
    }

    public void setTownList(List<TownModel> townList) {
        this.townList = townList;
    }

    @Override
    public String toString() {
        return "DistrictModel [name=" + name + ", townList=" + townList + "]";
    }

}

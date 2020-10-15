package com.weiwei.merchant.bean;


import java.util.List;

public class MerchantInfo {

    private String phone;

    private String agentName;

    private List<User> user;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public List<User> getUser() {
        return user;
    }

    public void setUser(List<User> user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "MerchantInfo{" +
                "phone='" + phone + '\'' +
                ", agentName='" + agentName + '\'' +
                ",user='" + user + "\'" +
                '}';
    }
}

package org.songbai.loan.admin.risk.model;

import org.songbai.loan.risk.model.user.UserRiskOrderModel;

public class UserRiskOrderVO extends UserRiskOrderModel {

    private String userName;
    private String mouldName;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMouldName() {
        return mouldName;
    }

    public void setMouldName(String mouldName) {
        this.mouldName = mouldName;
    }
}

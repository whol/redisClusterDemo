package com.example.demo.beans.common;

import com.example.demo.entity.FantasyUser;

public class UserInfo extends GenericBean {
    private static final long serialVersionUID = 1109105654966370300L;
    private boolean isLogin;
    private String mainRole;
    private String userType;
    private FantasyUser user;

    public UserInfo() {
    }

    public UserInfo(boolean isLogin, String mainRole, FantasyUser user) {
        this.isLogin = isLogin;
        this.mainRole = mainRole;
        this.user = user;
    }

    public boolean isLogin() {
        return this.isLogin;
    }

    public void setLogin(boolean login) {
        this.isLogin = login;
    }

    public String getMainRole() {
        return this.mainRole;
    }

    public void setMainRole(String mainRole) {
        this.mainRole = mainRole;
    }

    public FantasyUser getUser() {
        return this.user;
    }

    public void setUser(FantasyUser user) {
        this.user = user;
    }

    public String getUserType() {
        return this.userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
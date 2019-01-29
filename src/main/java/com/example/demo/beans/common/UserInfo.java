package com.example.demo.beans.common;

import com.example.demo.beans.system.User;
import com.example.demo.beans.system.UserFlags;

public class UserInfo extends GenericBean {
    private static final long serialVersionUID = 1109105654966370300L;
    private boolean isLogin;
    private String mainRole;
    private String userType;
    private User user;
    private UserFlags userFlags;

    public UserInfo() {
    }

    public UserInfo(boolean isLogin, String mainRole, User user) {
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

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserType() {
        return this.userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public UserFlags getUserFlags() {
        return this.userFlags;
    }

    public void setUserFlags(UserFlags userFlags) {
        this.userFlags = userFlags;
    }
}
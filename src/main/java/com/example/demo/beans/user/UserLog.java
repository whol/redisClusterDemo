package com.example.demo.beans.user;

import com.example.demo.beans.common.GenericBean;

import java.util.Date;

public class UserLog extends GenericBean {
    private Long logId;
    private String logCode;
    private String logContent;
    private Date logTime;
    private String model;
    private String modelType;
    private String modelName;
    private String versionNumber;
    private String onlineType;
    private String gpsLocation;
    private String loginState;
    private Long userId;
    private String userIp;
    private String userMac;
    private String optChannel;
    private String userCode;
    private String userName;
    private String serialNumber;
    private String userProvice;
    private String userCity;
    private String userArea;
    private String userType;
    private Date registTime;
    private String custManagerNo;
    private String custManagerLevel;
    private String rsrvStr1;
    private String rsrvStr2;
    private String rsrvStr3;
    private String rsrvStr4;
    private String rsrvStr5;
    private String remark;

    public UserLog() {
    }

    public Long getLogId() {
        return this.logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public String getLogCode() {
        return this.logCode;
    }

    public void setLogCode(String logCode) {
        this.logCode = logCode == null ? null : logCode.trim();
    }

    public String getLogContent() {
        return this.logContent;
    }

    public void setLogContent(String logContent) {
        this.logContent = logContent == null ? null : logContent.trim();
    }

    public Date getLogTime() {
        return this.logTime;
    }

    public void setLogTime(Date logTime) {
        this.logTime = logTime;
    }

    public String getModel() {
        return this.model;
    }

    public void setModel(String model) {
        this.model = model == null ? null : model.trim();
    }

    public String getModelType() {
        return this.modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType == null ? null : modelType.trim();
    }

    public String getModelName() {
        return this.modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName == null ? null : modelName.trim();
    }

    public String getVersionNumber() {
        return this.versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber == null ? null : versionNumber.trim();
    }

    public String getOnlineType() {
        return this.onlineType;
    }

    public void setOnlineType(String onlineType) {
        this.onlineType = onlineType == null ? null : onlineType.trim();
    }

    public String getGpsLocation() {
        return this.gpsLocation;
    }

    public void setGpsLocation(String gpsLocation) {
        this.gpsLocation = gpsLocation == null ? null : gpsLocation.trim();
    }

    public String getLoginState() {
        return this.loginState;
    }

    public void setLoginState(String loginState) {
        this.loginState = loginState == null ? null : loginState.trim();
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserIp() {
        return this.userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp == null ? null : userIp.trim();
    }

    public String getUserMac() {
        return this.userMac;
    }

    public void setUserMac(String userMac) {
        this.userMac = userMac == null ? null : userMac.trim();
    }

    public String getOptChannel() {
        return this.optChannel;
    }

    public void setOptChannel(String optChannel) {
        this.optChannel = optChannel == null ? null : optChannel.trim();
    }

    public String getUserCode() {
        return this.userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode == null ? null : userCode.trim();
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getSerialNumber() {
        return this.serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber == null ? null : serialNumber.trim();
    }

    public String getUserProvice() {
        return this.userProvice;
    }

    public void setUserProvice(String userProvice) {
        this.userProvice = userProvice == null ? null : userProvice.trim();
    }

    public String getUserCity() {
        return this.userCity;
    }

    public void setUserCity(String userCity) {
        this.userCity = userCity == null ? null : userCity.trim();
    }

    public String getUserArea() {
        return this.userArea;
    }

    public void setUserArea(String userArea) {
        this.userArea = userArea == null ? null : userArea.trim();
    }

    public String getUserType() {
        return this.userType;
    }

    public void setUserType(String userType) {
        this.userType = userType == null ? null : userType.trim();
    }

    public Date getRegistTime() {
        return this.registTime;
    }

    public void setRegistTime(Date registTime) {
        this.registTime = registTime;
    }

    public String getCustManagerNo() {
        return this.custManagerNo;
    }

    public void setCustManagerNo(String custManagerNo) {
        this.custManagerNo = custManagerNo == null ? null : custManagerNo.trim();
    }

    public String getCustManagerLevel() {
        return this.custManagerLevel;
    }

    public void setCustManagerLevel(String custManagerLevel) {
        this.custManagerLevel = custManagerLevel == null ? null : custManagerLevel.trim();
    }

    public String getRsrvStr1() {
        return this.rsrvStr1;
    }

    public void setRsrvStr1(String rsrvStr1) {
        this.rsrvStr1 = rsrvStr1 == null ? null : rsrvStr1.trim();
    }

    public String getRsrvStr2() {
        return this.rsrvStr2;
    }

    public void setRsrvStr2(String rsrvStr2) {
        this.rsrvStr2 = rsrvStr2 == null ? null : rsrvStr2.trim();
    }

    public String getRsrvStr3() {
        return this.rsrvStr3;
    }

    public void setRsrvStr3(String rsrvStr3) {
        this.rsrvStr3 = rsrvStr3 == null ? null : rsrvStr3.trim();
    }

    public String getRsrvStr4() {
        return this.rsrvStr4;
    }

    public void setRsrvStr4(String rsrvStr4) {
        this.rsrvStr4 = rsrvStr4 == null ? null : rsrvStr4.trim();
    }

    public String getRsrvStr5() {
        return this.rsrvStr5;
    }

    public void setRsrvStr5(String rsrvStr5) {
        this.rsrvStr5 = rsrvStr5 == null ? null : rsrvStr5.trim();
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }
}
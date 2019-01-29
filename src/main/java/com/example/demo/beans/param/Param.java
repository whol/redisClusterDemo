package com.example.demo.beans.param;

import com.example.demo.beans.common.GenericBean;

import java.util.Date;

public class Param extends GenericBean {
    private Long id;
    private String typeId;
    private String typeName;
    private String dataId;
    private String dataName;
    private String pdataId;
    private String subsysCode;
    private String validFlag;
    private String updateUserId;
    private Date updateTime;
    private String rsrvStr1;
    private String rsrvStr2;
    private String rsrvStr3;
    private String rsrvStr4;
    private String rsrvStr5;
    private String remark;

    public Param() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTypeId() {
        return this.typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId == null ? null : typeId.trim();
    }

    public String getTypeName() {
        return this.typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName == null ? null : typeName.trim();
    }

    public String getDataId() {
        return this.dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId == null ? null : dataId.trim();
    }

    public String getDataName() {
        return this.dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName == null ? null : dataName.trim();
    }

    public String getPdataId() {
        return this.pdataId;
    }

    public void setPdataId(String pdataId) {
        this.pdataId = pdataId == null ? null : pdataId.trim();
    }

    public String getSubsysCode() {
        return this.subsysCode;
    }

    public void setSubsysCode(String subsysCode) {
        this.subsysCode = subsysCode == null ? null : subsysCode.trim();
    }

    public String getValidFlag() {
        return this.validFlag;
    }

    public void setValidFlag(String validFlag) {
        this.validFlag = validFlag == null ? null : validFlag.trim();
    }

    public String getUpdateUserId() {
        return this.updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId == null ? null : updateUserId.trim();
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
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


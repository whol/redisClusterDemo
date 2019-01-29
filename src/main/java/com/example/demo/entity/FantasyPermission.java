package com.example.demo.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class FantasyPermission implements Serializable {

    private Integer id;

    private Byte permissionStatus;

    private String permissionName;

    private Integer parentId;

    private String parentIds;

    private String permissionValue;

    private String permissionType;

    private String permissionUrl;

}
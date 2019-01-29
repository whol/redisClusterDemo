package com.example.demo.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class FantasyRole implements Serializable {

    private Integer id;

    private Byte roleStatus;

    private String roleName;

    private String remarks;

}
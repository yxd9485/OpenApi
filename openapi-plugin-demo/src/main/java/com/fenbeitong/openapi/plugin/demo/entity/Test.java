package com.fenbeitong.openapi.plugin.demo.entity;

import lombok.Data;

import javax.persistence.Id;

/**
 * 测试表
 * Created by log.chang on 2019/12/5.
 */
@Data
public class Test {
    @Id
    private Integer id;
    private String name;
    private String prop;

}

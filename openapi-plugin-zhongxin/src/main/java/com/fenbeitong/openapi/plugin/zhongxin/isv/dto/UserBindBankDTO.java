package com.fenbeitong.openapi.plugin.zhongxin.isv.dto;

import lombok.Data;

/**
 * <p>Title:  UserBindBankDTO</p>
 * <p>Description: 新增用户绑定中信银行</p>
 * <p>Company:  中信银行</p>
 *
 * @author: haoqiang.wang
 * @Date: 2021/4/19 下午6:52
 **/
@Data
public class UserBindBankDTO {

    private Long timeStamp;

    private String phoneNum;

    private String companyId;

    private String companyName;

    private String employeeId;

}

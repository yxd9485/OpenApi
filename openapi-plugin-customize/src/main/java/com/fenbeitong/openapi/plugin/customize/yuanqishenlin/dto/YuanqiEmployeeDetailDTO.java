package com.fenbeitong.openapi.plugin.customize.yuanqishenlin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName YuanqiEmployeeDetailDTO
 * @Description 元气森林人员详情查询
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/8/27 上午10:45
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class YuanqiEmployeeDetailDTO {
    //手机号
    private String phoneNumber;
    //员工姓名
    private String name;
    //员工身份证号
    private String identityNumber;
    //员工工号
    private String jobNumber;
    //飞书id
    private String larkId;
    //工作城市
    private String workCity;
    //飞书部门id
    private String larkDeptId;
    //部门名称
    private String deptName;
    //公司主体
    private String corpName;
    //公司主体编码
    private String corpCode;
    //成本中心编码
    private String costCenterCode;
    //银行卡号
    private String bankCardNo;
    //开户银行
    private String bankOfDeposit;
    //员工消费权限
    private String fbtJurisdiction;
}

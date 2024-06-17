package com.fenbeitong.openapi.plugin.lanxin.common.dto.response;

import lombok.Data;

import java.util.List;

/**
 * <p>Title: BaseDTO</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021/12/6 2:44 下午
 */
@Data
public class LanXinUserInfoDTO {
    String staffId;
    String name;
    String orgId;
    String orgname;
    String avatarUrl;
    String avatarId;
    MobilePhoneBean mobilePhone;
    String email;
    String employeeNumber;
    String loginName;
    String externalId;
    List<DepartmentBean> department;

    @Data
    public static class MobilePhoneBean {
        String countryCode;
        String number;
    }

    @Data
    public static class DepartmentBean {
        String id;
        String name;
    }
}

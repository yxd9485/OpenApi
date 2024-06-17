package com.fenbeitong.openapi.plugin.kingdee.common.dto;

import com.fenbeitong.openapi.plugin.kingdee.support.dto.ViewReqDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: JinDie3KCloudConfigDTO</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-10-16 16:14
 */
@Data
@NoArgsConstructor
public class KingDeeK3CloudConfigDTO {
    String url;
    ViewReqDTO employee;
    ViewReqDTO department;
    ViewReqDTO saleInfo;
    ViewReqDTO saler;
    Login login;
    Bills bills;


    @Data
    @NoArgsConstructor
    public static class Bills {
        /**
         * 保存
         */
        String saveStr;
        /**
         * 提交
         */
        String commitStr;
        /**
         * 审核
         */
        String auditStr;
    }

    @Data
    @NoArgsConstructor
    public static class Login {
        /**
         * 金蝶登录的acctID
         */
        public String acctId;
        /**
         * 用户名称
         */
        public String username;
        /**
         * appId
         */
        public String appId;
        /**
         * appSecret
         */
        public String appSecret;
        /**
         * 金蝶登录的lcid  2052：中文
         */
        public String lcid;
        /**
         * 登录密码
         */
        public String password;

    }
}

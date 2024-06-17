package com.fenbeitong.openapi.plugin.ecology.v8.dto;

import lombok.Data;

import java.util.List;

/**
 * 泛微获取审批人信息DTO
 * @Auther zhang.peng
 * @Date 2022/1/5
 */
@Data
public class EcologyApproveUserInfoDTO extends EcologyRestCommonResultDTO {

    private List<UserInfo> data;

    @Data
    public static class UserInfo{
        private String agentorbyagentid;
        private String agenttype;
        private String groupid;
        private String id;
        private String isremark;
        /**
         * 节点名称:申请人、直接上级
         */
        private String nodeName;
        private String nodeid;
        private String operatedate;
        private String operatetime;
        private String preisremark;
        private String receivedate;
        private String receivetime;
        /**
         * 用户名称
         */
        private String userName;
        private String userType;
        /**
         * 用户ID
         */
        private String userid;
        private String viewType;
    }
}

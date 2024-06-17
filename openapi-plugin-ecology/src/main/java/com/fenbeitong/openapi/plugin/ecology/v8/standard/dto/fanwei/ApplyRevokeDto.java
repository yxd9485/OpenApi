package com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.fanwei;

import lombok.Data;

/**
 * 撤销审批单DTO
 * @Auther zhang.peng
 * @Date 2021/11/18
 */
@Data
public class ApplyRevokeDto {
    
    private String companyId;

    private String userId;

    private String msg;

//    @Data
//    public static class Message{
//
//        private String id;
//
//    }

}

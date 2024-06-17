package com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.dto;

import lombok.Data;

import java.util.List;

/**
 * <p>Title: ThipApplyDto</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-12-03 15:50
 */
@Data
public class NingBoWeiLiTripApplyDetailsDto {

    public Integer code;
    public String msg;
    public List<String> ext;
    public Result result;

    @Data
    public static class Result {
        public Integer totalSize;
        public Integer count;
        public List<Records> records;

    }

    @Data
    public static class Records {

        public Long updatedAt;
        // 出差地点
        public String customItem1__c;
        // 单据编号
        public String customItem7__c;
        // 出差事由
        public String customItem5__c;
        // 出厂时间
        public Long customItem2__c;
        // 返厂时间
        public Long customItem3__c;
        // 提交人
        public Long createdBy;
        // dimDepart
        public Long dimDepart;
        // 实际出差人员名
        public String customItem4__c;
        // 标题
        public String name;
        // 出行交通工具
        public List<Integer> customItem6__c;

    }



}

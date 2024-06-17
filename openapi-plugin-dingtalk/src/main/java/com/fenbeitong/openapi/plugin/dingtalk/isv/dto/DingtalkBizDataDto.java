package com.fenbeitong.openapi.plugin.dingtalk.isv.dto;

import lombok.Data;

import java.util.List;

@Data
public class DingtalkBizDataDto {

    private String processInstanceId;

    private String finishTime;

    private List<String> attachedProcessInstanceIds;

    private String syncAction;

    private String businessId;

    private String title;

    private String originatorDeptId;

    private String url;

    private List<OperationRecord> operationRecords;

    private String result;

    private String bizAction;

    private String createTime;

    private String originatorUserid;

    private String processCode;

    private List<FormValueVO> formValueVOS;

    private List<Task> tasks;

    private String originatorDeptName;

    private String status;

    private String syncSeq;

    @Data
    public static class OperationRecord{

        private String date;

        private String result;

        private String type;

        private String userId;
    }

    @Data
    public static class FormValueVO{

        private String componentType;

        private String name;

        private String id;

        private String bizAlias;

        private String value;

        private String extValue;

        private List<FormDetail> details;

    }

    @Data
    public static class FormDetail{

        private List<Detail> details;

        private String id;

    }

    @Data
    public static class Detail{

        private String componentType;

        private String name;

        private String bizAlias;

        private String id;

        private String value;

        private String extValue;
    }

    @Data
    public static class Task{

        private String result;

        private String activityId;

        private String finishTime;

        private String pcUrl;

        private String createTime;

        private String mobileUrl;

        private String userId;

        private String taskId;

        private String status;

    }
}

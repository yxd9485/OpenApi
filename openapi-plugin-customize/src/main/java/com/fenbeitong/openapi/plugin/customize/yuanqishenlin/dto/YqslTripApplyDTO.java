package com.fenbeitong.openapi.plugin.customize.yuanqishenlin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 薪人薪事审批数据接收
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YqslTripApplyDTO {
    @Valid
    @NotNull(message = "审批基础数据[processBasicInfo]不可为空")
    @JsonProperty("processBasicInfo")
    private ProcessBasicInfo processBasicInfo;
    @Valid
    @NotNull(message = "审批表单数据[processMetaInfoList]不可为空")
    @JsonProperty("processMetaInfoList")
    private List<ProcessFormGroupInfoDTO> processMetaInfoList;

    @Data
    public static class ProcessBasicInfo{
        //审批id
        @Valid
        @NotNull(message = "审批id[sid]不可为空")
        @JsonProperty("sid")
        private String sid;
        //审批所属人的员工id
        @Valid
        @NotNull(message = "审批所属人的员工id[ownerId]不可为空")
        @JsonProperty("ownerId")
        private String ownerId;
        //审批所属人姓名
        @JsonProperty("ownerName")
        private String ownerName;
        //发起人姓名
        @JsonProperty("sponsorName")
        private String sponsorName;
        //部门id
        @JsonProperty("departmentId")
        private String departmentId;
        //部门名称
        @JsonProperty("departmentName")
        private String departmentName;
        //审批类型id
        @Valid
        @NotNull(message = "审批类型id[flowTypeId]不可为空")
        @JsonProperty("flowTypeId")
        private int flowTypeId;
        //审批类型名称
        @Valid
        @NotNull(message = "审批类型名称[flowTypeName]不可为空")
        @JsonProperty("flowTypeName")
        private String flowTypeName;
        //备注
        @Valid
        @NotNull(message = "备注[remark]不可为空")
        @JsonProperty("remark")
        private String remark;
        //审批状态
        @JsonProperty("status")
        private int status;
        //离职确认时间
        @JsonProperty("confirmDate")
        private String confirmDate;
        //审批发起时间
        @JsonProperty("addtime")
        private long addtime;
        //审批修改时间
        @JsonProperty("modtime")
        private long modtime;
        //审批最后操作时间
        @JsonProperty("lastStepModtime")
        private long lastStepModtime;
        //审批编号
        @JsonProperty("flowNumber")
        private String flowNumber;
        //工号
        @Valid
        @NotNull(message = "工号[employeeNo]不可为空")
        @JsonProperty("employeeNo")
        private String employeeNo;
    }

    @Data
    public static class ProcessFormGroupInfoDTO {
        //分组名称
        @Valid
        @NotNull(message = "分组名称[groupName]不可为空")
        @JsonProperty("groupName")
        private String groupName;
        //分组详情数据
        @Valid
        @NotNull(message = "分组详情数据[detailInfos]不可为空")
        @JsonProperty("detailInfos")
        private List<ProcessFormDetailInfoDTO> detailInfos;
    }

    @Data
    public  static class ProcessFormDetailInfoDTO {
            //标签名
            @JsonProperty("labName")
            private String labName;
            //标签值
            @JsonProperty("value")
            private String value;
            //数值类型标签调整前的值
            @JsonProperty("oldValue")
            private String oldValue;
            //字段类型
            @JsonProperty("type")
            private int type;
            //数值类型的单位
            @JsonProperty("unit")
            private String unit;
            //文件信息
            @JsonProperty("files")
            private List<FlowFileInfo> files;
            //时间区间开始时间标签名
            @JsonProperty("startLabName")
            private String startLabName;
            //时间区间开始时间的标签值
            @JsonProperty("startValue")
            private String startValue;
            //时间区间结束时间标签名
            @JsonProperty("endLabName")
            private String endLabName;
            //时间区间结束时间的标签值
            @JsonProperty("endValue")
            private String endValue;
            //时间区间申请时长的标签名
            @JsonProperty("longLabName")
            private String longLabName;
            //时间区间申请时长的标签值
            @JsonProperty("longValue")
            private String longValue;
            //双时间区间开始休息的标签名
            @JsonProperty("relaxStartLabName")
            private String relaxStartLabName;
            //双时间区间开始休息的标签值
            @JsonProperty("relaxStartValue")
            private String relaxStartValue;
            //双时间区间结束休息的标签名
             @JsonProperty("relaxEndLabName")
            private String relaxEndLabName;
             //双时间区间结束休息的标签值
            @JsonProperty("relaxEndValue")
            private String relaxEndValue;
            //文本列表类型返回
            @JsonProperty("textListValue")
            private String textListValue;
            //同行人工号
            @JsonProperty("peerEmployeeNo")
            private String peerEmployeeNo;

    }
    @Data
    public static class FlowFileInfo {
        //文件名，仅当类型为文件是返回文件名
        @JsonProperty("files")
        private String files;
        //文件key
        @JsonProperty("fileKey")
        private String fileKey;
    }
}

package com.fenbeitong.openapi.plugin.dingtalk.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 钉钉提交表单数据
 * @Author: xiaohai
 * @Date: 2021/4/17 上午10:31
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DingtalkApprovalFormDTO {

    /**
     * 主流程实例id（变更后的原单id）
     */
    @JsonProperty("mainProcessInstanceId")
    private String mainProcessInstanceId;

    /**
     * 流程id（三方审批单id）
     */
    @JsonProperty("processInstanceId")
    private String processInstanceId;

    /**
     * 操作类型
     */
    @JsonProperty("bizAction")
    private String bizAction;

    @JsonProperty("formValueVOS")
    private List<FormValueVOS> formValueVOS;

    @Data
    public static class FormValueVOS{

        @JsonProperty("id")
        private String id;
        //表单名称
        @JsonProperty("name")
        private String name;
        //值
        @JsonProperty("value")
        private String value;
        //表单类型
        @JsonProperty("componentType")
        private String componentType;
        //别名
        @JsonProperty("bizAlias")
        private String bizAlias;

        //用车城市列表
        @JsonProperty("details")
        private List<CarCity> details;

        @JsonProperty("extValue")
        private String extValue;


    }

    //用车城市
    @Data
    public static class CarCity{

        @JsonProperty("id")
        private String id;

        //用车城市数据
        @JsonProperty("details")
        private List<FormValueVOS> details;
    }


}

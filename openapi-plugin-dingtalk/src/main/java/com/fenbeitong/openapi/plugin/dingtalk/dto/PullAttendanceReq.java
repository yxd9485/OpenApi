package com.fenbeitong.openapi.plugin.dingtalk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * <p>Title: CreateAttendanceReq</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/1/20 3:15 PM
 */
@Data
@ApiModel
public class PullAttendanceReq {

    /**
     * 公司id
     */
    @JsonProperty("company_id")
    private String companyId;

    /**
     * 考勤范围内有效
     */
    @JsonProperty("only_normal_location")
    private String onlyNormalLocation;

    /**
     * 工作日
     */
    @JsonProperty("work_date")
    private String workDate;

    /**
     * 钉钉用户id
     */
    @JsonProperty("dingtalk_user_id")
    private String dingtalkUserId;


    /**
     * 是否检查审批单
     */
    @JsonProperty("is_check_approve")
    private Boolean isCheckApprove;
}

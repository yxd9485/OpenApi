package com.fenbeitong.openapi.plugin.wechat.eia.dto.attendance;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;

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
public class WeChatPullAttendanceReqDTO {

    /**
     * 公司id
     */
    @JsonProperty("company_id")
    private String companyId;

    /**
     * 考勤范围内有效
     */
    @JsonProperty("open_checkin_data_type")
    private int openCheckinDataType;

    /**
     * 工作日
     */
    @JsonProperty("work_date")
    private String workDate;

    /**
     * 微信用户id
     */
    @JsonProperty("wechat_user_id")
    private String weChatUserId;
}

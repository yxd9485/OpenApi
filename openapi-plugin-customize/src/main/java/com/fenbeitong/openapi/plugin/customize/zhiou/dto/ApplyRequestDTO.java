package com.fenbeitong.openapi.plugin.customize.zhiou.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName ApplyRequestDTO
 * @Description 审批单参数
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/8/30
 **/
@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class ApplyRequestDTO {

    /**
     * 申请单类型
     */
    @JsonProperty("apply_type")
    private String applyType;
    /**
     * 申请单状态
     */
    @JsonProperty("apply_state")
    private Integer applyState;
    /**
     * 申请单ID
     */
    @JsonProperty("apply_id")
    private String applyId;
    /**
     * 原申请单ID（多次变更时为最初的申请单id）
     */
    @JsonProperty("root_apply_id")
    private String rootApplyId;

    /**
     * 蓝凌审批推送是否成功
     */
    @JsonProperty("push_landray_success")
    private boolean pushLandraySuccess;

    /**
     * 北森考勤推送是否成功
     */
    @JsonProperty("push_beisen_success")
    private boolean pushBeisenSuccess;

}

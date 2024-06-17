package com.fenbeitong.openapi.plugin.dingtalk.isv.dto;

import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkApprovalFormDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyGuest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description:
 * @Author: xiaohai
 * @Date: 2022/05/22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DingtalkAttendanceDto {

    /**
     * 分贝通公司id
     */
    private String companyId;

    /**
     * 三方企业id
     */
    private String corpId;

    /**
     * 申请人用户id
     */
    private String applicantId;

    /**
     * 钉钉表单数据
     */
    private DingtalkApprovalFormDTO dingtalkIsvTripApprovalFormDTO;

    /**
     * 分贝通审批单id
     */
    private String applyId;

    /**
     * 三方审批单id
     */
    private String thirdApplyId;

    /**
     * 同行人
     */
    private List<CommonApplyGuest> guestList;




}

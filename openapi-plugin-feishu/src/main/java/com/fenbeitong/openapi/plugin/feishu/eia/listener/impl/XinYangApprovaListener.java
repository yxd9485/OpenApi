package com.fenbeitong.openapi.plugin.feishu.eia.listener.impl;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalFormDTO;
import com.fenbeitong.openapi.plugin.feishu.eia.listener.AbstractFeiShuEiaCommon;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApply;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyTrip;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@ServiceAspect
@Service
@Slf4j
public class XinYangApprovaListener extends AbstractFeiShuEiaCommon {

    /**
     * 差旅
     */
    public CommonApplyReqDTO parseFeiShuBusinessForm(String companyId, String corpId, String approvalId, String form) {

        String jsonForm = form.replaceAll("\\\\", "");
        List<FeiShuApprovalFormDTO> list = JsonUtils.toObj(jsonForm, new TypeReference<List<FeiShuApprovalFormDTO>>() {
        });
        CommonApplyReqDTO commonApplyReqDTO = new CommonApplyReqDTO();
        List<CommonApplyTrip> tripList = new ArrayList();
        if (ObjectUtils.isEmpty(list)) {
            return null;
        }
        for (FeiShuApprovalFormDTO feiShuApprovalFormDTO : list) {
            FeiShuApprovalFormDTO.Value value = feiShuApprovalFormDTO.getValue();
            if (ObjectUtils.isEmpty(value)) {
                return null;
            }
            CommonApply commonApply = new CommonApply();
            commonApply.setApplyReason(value.getReason());
            commonApply.setApplyReasonDesc(value.getReason());
            commonApply.setThirdRemark(value.getReason());
            commonApply.setThirdId(approvalId);
            commonApply.setType(1);
            commonApply.setFlowType(4);
            commonApply.setBudget(0);
            commonApplyReqDTO.setApply(commonApply);
            CommonApplyTrip commonApplyFromTrip = new CommonApplyTrip();
            // 预估金额
            commonApplyFromTrip.setEstimatedAmount(0);
            // 单程往返 1:单程;2:往返
            commonApplyFromTrip.setTripType(2);
            List<FeiShuApprovalFormDTO.Schedule> schedules = value.getSchedule();
            if (ObjectUtils.isEmpty(schedules)) {
                return null;
            }
            for (FeiShuApprovalFormDTO.Schedule schedule : schedules) {
                // 开始时间
                commonApplyFromTrip.setStartTime(getDate(schedule.getStart()));
                // 结束时间
                commonApplyFromTrip.setEndTime(getDate(schedule.getEnd()));
                // 出发城市
                commonApplyFromTrip.setStartCityName(addressString(schedule.getDeparture()));
                // 到达城市
                commonApplyFromTrip.setArrivalCityName(addressString((schedule.getDestination())));
                //7:机票 11:酒店
                setTripList(new Integer[]{7, 11}, commonApplyFromTrip, tripList);
            }
        }
        commonApplyReqDTO.setTripList(tripList);

        return commonApplyReqDTO;

    }


}


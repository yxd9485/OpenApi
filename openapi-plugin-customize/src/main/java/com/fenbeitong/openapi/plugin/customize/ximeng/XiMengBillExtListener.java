package com.fenbeitong.openapi.plugin.customize.ximeng;


import com.alibaba.dubbo.config.annotation.Reference;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.func.apply.dto.ApplyOrderDetailDTO;
import com.fenbeitong.openapi.plugin.func.apply.dto.ApplyTripInfoDTO;
import com.fenbeitong.openapi.plugin.func.company.dto.FuncBillExtInfoTransformDTO;
import com.fenbeitong.openapi.plugin.func.company.service.ICompanyBillExtListener;
import com.fenbeitong.openapi.plugin.support.apply.dto.CompanyApplyDetailReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.impl.OpenApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author duhui
 * @Date 2021/8/5
 **/
@Service
@Slf4j
public class XiMengBillExtListener implements ICompanyBillExtListener {

    @Autowired
    private OpenApplyServiceImpl openApplyService;
    @Autowired
    private UserCenterService userCenterService;


    @Override
    public void setBillExt(String companyId, Map<String, Object> srcData, Map<String, Object> resultData, FuncBillExtInfoTransformDTO transformDto) {
        if (!ObjectUtils.isEmpty(transformDto)) {
            String token = userCenterService.getUcSuperAdminToken(companyId);
            // 审批单号
            String applyId = transformDto.getApplyId();
            if (!StringUtils.isBlank(applyId)) {
                try {
                    Map<String, Object> companyApproveDetail = openApplyService.getCompanyApproveDetail(token, CompanyApplyDetailReqDTO.builder().applyId(applyId).build());
                    ApplyOrderDetailDTO applyOrderDetailDto = JsonUtils.toObj(JsonUtils.toJson(companyApproveDetail), ApplyOrderDetailDTO.class);
                    if (!ObjectUtils.isEmpty(applyOrderDetailDto)) {
                        // 三方审批单号
                        resultData.put("thirdApplyId", applyOrderDetailDto.getApply().getThird_id());
                    }
                } catch (Exception e) {
                    log.error("西蒙定制账单获取审批失败", e);
                }
            }
        }
    }

}

package com.fenbeitong.openapi.plugin.customize.rendajincang.service;

import com.fenbeitong.openapi.plugin.support.apply.constant.CustformApplyType;
import com.fenbeitong.openapi.plugin.support.apply.dto.CustformApplyFormDetailDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.AbstractApplyService;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @ClassName AbstractCustformApplyServiceImpl
 * @Description 获取自定义模板审批数据详情
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/10/12
 **/
@Service
@Slf4j
public class AbstractCustformApplyServiceImpl extends AbstractApplyService{
    @Autowired
    private UserCenterService userCenterService;

    /**
     * 获取自定义模板审批数据详情
     * @param applyId 申请单id
     * @param companyId 公司id
     * @return 审批数据详情
     */
    public CustformApplyFormDetailDTO getCustformApplyDetail(String applyId, String companyId) {
        String token = userCenterService.getUcSuperAdminToken(companyId);
        Map<String, Object> applyFormDetailMap = getCustformApproveDetail(token, applyId, CustformApplyType.Web.getKey());
        return JsonUtils.toObj(JsonUtils.toJson(applyFormDetailMap), CustformApplyFormDetailDTO.class);
    }
}

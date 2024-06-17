package com.fenbeitong.openapi.plugin.dingtalk.yida.service.impl;

import com.fenbeitong.openapi.plugin.dingtalk.yida.constant.YiDaApiContant;
import com.fenbeitong.openapi.plugin.dingtalk.yida.dto.YiDaApplyDetailRespDTO;
import com.fenbeitong.openapi.plugin.dingtalk.yida.service.IYiDaApplyService;
import com.fenbeitong.openapi.plugin.dingtalk.yida.util.YiDaPostClientUtil;
import com.fenbeitong.openapi.plugin.etl.constant.EtlScriptType;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.etl.service.impl.EtlUtils;
import com.fenbeitong.openapi.plugin.support.apply.constant.SaasApplyType;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApply;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyTrip;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: YiDaApplyServiceImpl</p>
 * <p>Description: 易搭审批service</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author lizhen
 * @date 2021/8/12 3:40 下午
 */
@Slf4j
@Component
public class YiDaApplyServiceImpl implements IYiDaApplyService {

    @Autowired
    private YiDaPostClientUtil yiDaPostClientUtil;

    @Autowired
    private OpenThirdScriptConfigDao openThirdScriptConfigDao;

    @Override
    public YiDaApplyDetailRespDTO getInstanceById(String processInstanceId, String corpId) {
        Map<String, String> params = new HashMap();
        params.put("processInstanceId", processInstanceId);
        String result = yiDaPostClientUtil.post(YiDaApiContant.GET_INSTANCE_BY_ID, params, corpId);
        return JsonUtils.toObj(result, YiDaApplyDetailRespDTO.class);
    }

    @Override
    public Map<String, Object> tripApplyScript(String companyId, Map<String, Object> applyData) {
        OpenThirdScriptConfig tripConfig = openThirdScriptConfigDao.geConfig(companyId, EtlScriptType.TRIP_APPLY_SYNC.getType());
        if (ObjectUtils.isEmpty(tripConfig)) {
            return null;
        }
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("applyData", applyData);
        }};
        if (StringUtils.isNotBlank(tripConfig.getParamJson()) && JsonUtils.toObj(tripConfig.getParamJson(), Map.class) != null) {
            params.putAll(JsonUtils.toObj(tripConfig.getParamJson(), Map.class));
        }
        Map<String, Object> result = (Map<String, Object>) EtlUtils.execute(tripConfig.getScript(), params);
        log.info("tripApplyScript执行结果：{}", JsonUtils.toJson(result));
        return result;
    }


    @Override
    public CommonApplyReqDTO parseYiDaTripApprovalForm(String companyId, YiDaApplyDetailRespDTO yiDaApplyDetailRespDTO) {
        Map<String, Object> applyData = yiDaApplyDetailRespDTO.getData();
        Map<String, Object> resultMap = tripApplyScript(companyId, applyData);
        CommonApplyReqDTO commonApplyReqDTO = JsonUtils.toObj(JsonUtils.toJson(resultMap), CommonApplyReqDTO.class);
        if (!ObjectUtils.isEmpty(commonApplyReqDTO.getTripList())) {
            for (CommonApplyTrip commonApplyTrip : commonApplyReqDTO.getTripList()) {
                commonApplyTrip.setCityRelationType(4);
                //如果是酒店，出发城市为目的城市
                if (commonApplyTrip.getType() == 11) {
                    commonApplyTrip.setStartCityId(commonApplyTrip.getArrivalCityId());
                }
            }
        }
        CommonApply commonApply = commonApplyReqDTO.getApply();
        commonApply.setThirdRemark(commonApply.getApplyReasonDesc());
        commonApply.setThirdId(yiDaApplyDetailRespDTO.getProcessInstanceId());
        commonApply.setType(SaasApplyType.ChaiLv.getValue());
        commonApply.setFlowType(4);
        commonApply.setCompanyId(companyId);
        commonApplyReqDTO.setApply(commonApply);
        return commonApplyReqDTO;
    }


}

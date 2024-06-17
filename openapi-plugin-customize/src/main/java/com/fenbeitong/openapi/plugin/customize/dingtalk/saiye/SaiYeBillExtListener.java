package com.fenbeitong.openapi.plugin.customize.dingtalk.saiye;


import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.etl.constant.EtlScriptType;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.etl.service.impl.EtlUtils;
import com.fenbeitong.openapi.plugin.func.company.dto.FuncBillExtInfoTransformDTO;
import com.fenbeitong.openapi.plugin.func.company.service.ICompanyBillExtListener;
import com.fenbeitong.openapi.plugin.support.apply.dto.BaseDTO;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.core.json.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @Description
 * @Author duhui
 * @Date 2021/8/5
 **/
@Service
@Slf4j
public class SaiYeBillExtListener implements ICompanyBillExtListener {
    @Value("${host.open_dingtalk}")
    private String openDingtalkHost;
    @Autowired
    OpenThirdScriptConfigDao openThirdScriptConfigDao;

    @Override
    public void setBillExt(String companyId, Map<String, Object> srcData, Map<String, Object> resultData, FuncBillExtInfoTransformDTO transformDto) {
        log.info("------赛业三方字段计入账单start-----{}", JsonUtils.toJson(resultData));
        // 费用归属部门
        if (!MapUtils.isBlank(resultData)) {
            OpenThirdScriptConfig billConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.BILL_INFO_CHANGE);
            if (!ObjectUtils.isEmpty(billConfig)) {
                String costAttributionDeptId = StringUtils.obj2str(resultData.get("costAttributionDeptId"));
                log.info("赛业三方字段计入账单costAttributionDeptId:{}", costAttributionDeptId);
                if (!StringUtils.isBlank(costAttributionDeptId)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("companyId", companyId);
                    param.put("depId", costAttributionDeptId);
                    String url = openDingtalkHost + "/openapi/dingtalk/customize/get_dingtalk_dep_detail";
                    String result = RestHttpUtils.get(url, null, param);
                    BaseDTO agreeResult = JsonUtils.toObj(result, BaseDTO.class);
                    if (agreeResult == null || !agreeResult.success()) {
                        String msg = agreeResult == null ? "" : Optional.ofNullable(agreeResult.getMsg()).orElse("");
                        throw new FinhubException(500, msg);
                    }
                    Map<String, String> bodyMap = JsonUtils.toObj(JsonUtils.toJson(agreeResult.getData()), Map.class);
                    log.info("赛业生物定制账单获取部门编码 bodyMap:{}", bodyMap);
                    billBeforeSyncFilter(billConfig, resultData, bodyMap);
                }
            }
        }
    }

    private void billBeforeSyncFilter(OpenThirdScriptConfig departmentConfig, Map<String, Object> resultData, Map<String, String> bodyMap) {
        Map<String, Object> params = new HashMap<String, Object>(4) {{
            put("resultData", resultData);
            put("bodyMap", bodyMap);
        }};
        EtlUtils.etlFilter(departmentConfig, params);
    }
}

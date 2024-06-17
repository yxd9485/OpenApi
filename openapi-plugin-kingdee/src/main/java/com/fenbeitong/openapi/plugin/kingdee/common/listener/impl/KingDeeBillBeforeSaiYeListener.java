package com.fenbeitong.openapi.plugin.kingdee.common.listener.impl;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.etl.constant.EtlScriptType;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.kingdee.common.dto.KingDeeK3CloudConfigDTO;
import com.fenbeitong.openapi.plugin.kingdee.common.listener.KingDeekBillBeforeListener;
import com.fenbeitong.openapi.plugin.kingdee.common.service.KingDeeService;
import com.fenbeitong.openapi.plugin.kingdee.common.service.impl.KingDeeBaseService;
import com.fenbeitong.openapi.plugin.kingdee.support.dto.ViewReqDTO;
import com.fenbeitong.openapi.plugin.kingdee.support.entity.OpenKingdeeUrlConfig;
import com.fenbeitong.openapi.plugin.support.apply.dto.BaseDTO;
import com.fenbeitong.openapi.plugin.support.callback.entity.OpenBillDetailRecord;
import com.fenbeitong.openapi.plugin.support.callback.entity.OpenThirdBillRecord;
import com.fenbeitong.openapi.plugin.support.project.service.OpenProjectService;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Description 可配置化监听
 * @Author duhui
 * @Date 2020-11-26
 **/
@Slf4j
@Service
public class KingDeeBillBeforeSaiYeListener implements KingDeekBillBeforeListener {

    @Autowired
    private KingDeeService jinDieService;
    @Autowired
    KingDeeBaseService kingDeeBaseService;
    @Autowired
    OpenProjectService openProjectService;
    private static final String King = "open_plugin_beisen_redis_key:{0}";
    @Value("${host.open_dingtalk}")
    private String openDingtalkHost;
    @Autowired
    OpenThirdScriptConfigDao openThirdScriptConfigDao;

    @Override
    public void setThirdData(String companyId, String token, OpenBillDetailRecord openBillDetailRecord,
        OpenThirdBillRecord openThirdBillRecord, OpenKingdeeUrlConfig kingDeeUrlConfig,
        KingDeeK3CloudConfigDTO kingDee3KCloudConfigDTO, Object... objects) {
        try {
            setKingdeeEmployeeInfo(token, openBillDetailRecord, openThirdBillRecord, kingDeeUrlConfig,
                kingDee3KCloudConfigDTO);
        } catch (Exception e) {
            log.error("赛业生物获取人员扩展字段错误", e);
        }
        try {
            // 获取费用归属部门三方字段
            getThirdDepCode(companyId, openThirdBillRecord);
        } catch (Exception e) {
            log.error("赛业生物获取费用归属部门字段错误", e);
        }

    }

    @Override
    public Map<String, Map<String, List<OpenThirdBillRecord>>> dataGroup(
        List<OpenThirdBillRecord> openThirdBillRecordList) {
        Map<String, Map<String, List<OpenThirdBillRecord>>> resData = new HashMap<>();
        Map<String, List<OpenThirdBillRecord>> listMap =
            openThirdBillRecordList.stream().filter(
                t -> !StringUtils.isBlank(t.getField1()) && !StringUtils.isBlank(t.getField3()) && !StringUtils.isBlank(
                    t.getField4()) && !StringUtils.isBlank(t.getField5())).collect(
                Collectors.groupingBy(t -> t.getField1()));
        listMap.forEach((k, v) -> {
            resData.put(k, v.stream().collect(Collectors.groupingBy(
                t -> t.getField3().concat("_").concat(t.getField4()).concat("_").concat(t.getField5()))));

        });
        return resData;
    }

    private void getThirdDepCode(String companyId, OpenThirdBillRecord openThirdBillRecord) {
        OpenThirdScriptConfig billConfig =
            openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.BILL_INFO_CHANGE);
        if (ObjectUtils.isEmpty(billConfig)) {
            OpenBillDetailRecord openBillDetailRecord =
                JsonUtils.toObj(openThirdBillRecord.getBillDetailData(), OpenBillDetailRecord.class);
            Map<String, String> map = JsonUtils.toObj(openBillDetailRecord.getThirdInfo(), Map.class);
            String costAttributionDeptId =
                StringUtils.obj2str(MapUtils.getValueByExpress(map, "costAttributionDeptId"));
            if (ObjectUtils.isEmpty(costAttributionDeptId)) {
                log.info("costAttributionDeptId为空，不再查询钉钉部门信息");
                return;
            }
            Map<String, Object> param = new HashMap<>();
            param.put("companyId", companyId);
            param.put("depId", costAttributionDeptId);
            String url = openDingtalkHost + "/openapi/dingtalk/customize/get_dingtalk_dep_detail";
            String result = RestHttpUtils.get(url, null, param);
            BaseDTO agreeResult = com.finhub.framework.core.json.JsonUtils.toObj(result, BaseDTO.class);
            if (agreeResult == null || !agreeResult.success()) {
                String msg = agreeResult == null ? "" : Optional.ofNullable(agreeResult.getMsg()).orElse("");
                throw new FinhubException(500, msg);
            }
            Map<String, String> bodyMap = JsonUtils.toObj(JsonUtils.toJson(agreeResult.getData()), Map.class);
            if (!ObjectUtils.isEmpty(bodyMap) && !ObjectUtils.isEmpty(bodyMap.get("brief"))) {
                openThirdBillRecord.setField5(bodyMap.get("brief"));
            }
        }
    }

    /**
     * 设置金蝶人员部门编码
     */
    private void setKingdeeEmployeeInfo(String token, OpenBillDetailRecord openBillDetailRecord,
        OpenThirdBillRecord openThirdBillRecord, OpenKingdeeUrlConfig kingDeeUrlConfig,
        KingDeeK3CloudConfigDTO kingDee3KCloudConfigDTO) {
        // 获取配置信息
        ViewReqDTO departReqDTO = kingDee3KCloudConfigDTO.getDepartment();
        ViewReqDTO userReqDTO = kingDee3KCloudConfigDTO.getEmployee();
        String thirdInfo = openBillDetailRecord.getThirdInfo();
        if (StringUtils.isBlank(thirdInfo)) {
            log.info("thirdInfo为空" + JsonUtils.toJson(openBillDetailRecord));
            return;
        }
        Map thirdInfoMap = JsonUtils.toObj(thirdInfo, Map.class);
        if (ObjectUtils.isEmpty(thirdInfoMap)) {
            log.info("thirdInfoMap为空" + JsonUtils.toJson(openBillDetailRecord));
            return;
        }
        String employeeNo = StringUtils.obj2str(thirdInfoMap.get("工号1"));
        // 根据人员编号查询部门编码
        if (StringUtils.isBlank(employeeNo)) {
            log.info("工号1为空" + JsonUtils.toJson(openBillDetailRecord));
            return;
        }
        userReqDTO.getData().setFilterString(String.format(userReqDTO.getData().getFilterString(), employeeNo));
        userReqDTO.getData().setStartRow(0);
        // 人员数据
        List<List> dataUseList = jinDieService.getData(userReqDTO, kingDeeUrlConfig, token);
        if (ObjectUtils.isEmpty(dataUseList)) {
            log.info("查询金蝶人员信息为空" + JsonUtils.toJson(openBillDetailRecord));
            return;
        }
        departReqDTO.getData().setFilterString(
            String.format(departReqDTO.getData().getFilterString(), dataUseList.get(0).get(0)));
        departReqDTO.getData().setStartRow(0);
        // 部门数据
        List<List> dataDepList = jinDieService.getData(departReqDTO, kingDeeUrlConfig, token);
        if (ObjectUtils.isEmpty(dataDepList)) {
            log.info("查询金蝶部门信息为空" + JsonUtils.toJson(openBillDetailRecord));
            return;
        }
        openThirdBillRecord.setField2(dataDepList.get(0).get(0).toString());
    }

}

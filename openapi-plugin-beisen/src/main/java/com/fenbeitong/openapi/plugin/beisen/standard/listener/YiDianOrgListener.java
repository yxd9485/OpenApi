package com.fenbeitong.openapi.plugin.beisen.standard.listener;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.beisen.common.dto.BeiSenContract;
import com.fenbeitong.openapi.plugin.beisen.common.dto.BeiSenCorporation;
import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenParamConfig;
import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenResultEntity;
import com.fenbeitong.openapi.plugin.beisen.standard.service.third.BeisenApiService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenCustonmConstant;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenCustomizeConfigDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenCustomizeConfig;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author duhui
 * @Date 2021/8/5
 **/
@Service
@Slf4j
public class YiDianOrgListener extends DefaultOrgListener {
    @Autowired
    OpenCustomizeConfigDao openCustomizeConfigDao;
    @Autowired
    BeisenApiService beisenApiService;
    @Override
    public List<OpenThirdEmployeeDTO> filterOpenThirdOrgUnitDtoBefore(List<OpenThirdEmployeeDTO> openThirdEmployeeDTOS, String companyId, BeisenParamConfig beisenParamConfige, Object... objects) {
        OpenCustomizeConfig openCustomizeConfig = openCustomizeConfigDao.getOpenCustomizeConfig(companyId, OpenCustonmConstant.open_customize_config_type.EMP_ALL);
        Map map = JsonUtils.toObj(openCustomizeConfig.getExtend(), Map.class);
        String contractData = map.get("contractData").toString();
        String contractUrl = map.get("contractUrl").toString();
        String CorporationData = map.get("CorporationData").toString();
        String CorporationUrl = map.get("CorporationUrl").toString();
        Map<String, BeiSenCorporation> beiSenCorporationMap = new HashMap<>();
        getBeiSenCorporationMap(beisenParamConfige, CorporationUrl, CorporationData, beiSenCorporationMap, "");
        Map<String, String> contractMap = getContractData(openThirdEmployeeDTOS, contractData, contractUrl, beisenParamConfige);
        openThirdEmployeeDTOS.forEach(openThirdEmployeeDTO -> {
            BeiSenCorporation beiSenCorporation = beiSenCorporationMap.get(contractMap.get(openThirdEmployeeDTO.getThirdEmployeeId()) == null ? "-1" : contractMap.get(openThirdEmployeeDTO.getThirdEmployeeId()));
            if (!ObjectUtils.isEmpty(beiSenCorporation)) {
                Map<String, Object> extAttr = new HashMap<>();
                extAttr.put("code", beiSenCorporation.getFields().getCode());
                extAttr.put("name", beiSenCorporation.getFields().getName());
                openThirdEmployeeDTO.setExtAttr(extAttr);
            }
        });
        return openThirdEmployeeDTOS;
    }

    public void getBeiSenCorporationMap(BeisenParamConfig beisenParamConfige, String CorporationUrl, String CorporationData, Map<String, BeiSenCorporation> beiSenCorporationMap, String scrollId) {
        String CorporationResult = beisenApiService.getData(beisenParamConfige, CorporationUrl, String.format(CorporationData, scrollId));
        BeisenResultEntity<List<BeiSenCorporation>> CorporationResultEntiry = JsonUtils.toObj(CorporationResult, new TypeReference<BeisenResultEntity<List<BeiSenCorporation>>>() {
        });
        if (!ObjectUtils.isEmpty(CorporationResultEntiry) && "200".equals(CorporationResultEntiry.getCode().toString())) {
            beiSenCorporationMap.putAll(CorporationResultEntiry.getData().stream().collect(Collectors.toMap(t -> t.getFields().getOId(), Function.identity(), (o, n) -> n)));
            if (!CorporationResultEntiry.getIsLastData()) {
                getBeiSenCorporationMap(beisenParamConfige, CorporationUrl, CorporationData, beiSenCorporationMap, CorporationResultEntiry.getScrollId());
            }
        }
    }

    public Map<String, String> getContractData(List<OpenThirdEmployeeDTO> openThirdEmployeeDTOS, String contractData, String contractUrl, BeisenParamConfig beisenParamConfige) {
        Map<String, String> map = new HashMap<>();
        CollectionUtils.batch(openThirdEmployeeDTOS, 200).forEach(batch -> {
            List<Integer> ids = batch.stream().map(t -> Integer.parseInt(t.getThirdEmployeeId())).collect(Collectors.toList());
            String CorporationResult = beisenApiService.getData(beisenParamConfige, contractUrl, String.format(contractData, JsonUtils.toJson(ids)));
            BeisenResultEntity<Map<String, List<BeiSenContract>>> beisenResultEntity = JsonUtils.toObj(CorporationResult, new TypeReference<BeisenResultEntity<Map<String, List<BeiSenContract>>>>() {
            });
            if (!ObjectUtils.isEmpty(beisenResultEntity) && "200".equals(beisenResultEntity.getCode().toString())) {
                map.putAll(beisenResultEntity.getData().entrySet().stream().filter(t -> t.getValue().size() > 0).collect(Collectors.toMap(t -> t.getKey(), t -> t.getValue().get(0).firstPartyCode, (o, n) -> n)));
            }
        });
        return map;
    }


}

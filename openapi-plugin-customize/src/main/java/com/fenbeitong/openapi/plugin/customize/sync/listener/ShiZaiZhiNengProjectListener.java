package com.fenbeitong.openapi.plugin.customize.sync.listener;

import com.fenbeitong.openapi.plugin.customize.common.service.impl.DefaultCustomProjectListener;
import com.fenbeitong.openapi.plugin.customize.common.vo.OpenThirdProjectVo;
import com.fenbeitong.openapi.plugin.etl.service.IEtlService;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdEmployeeDao;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenCustomizeConfig;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.support.project.dto.SupportUcThirdProjectReqDTO;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author duhui
 * @Date 2021/8/5
 **/
@Service
@Slf4j
public class ShiZaiZhiNengProjectListener extends DefaultCustomProjectListener {

    @Autowired
    OpenEmployeeServiceImpl openEmployeeService;

    @Autowired
    OpenThirdEmployeeDao openThirdEmployeeDao;

    @Autowired
    IEtlService etlService;

    @Override
    public void setHead(Map<String, String> map, String companyId) {
        if (!ObjectUtils.isEmpty(map)) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", "Basic " + Base64.encodeBase64String((map.get("client_id") + ":" + map.get("client_secret")).getBytes(StandardCharsets.UTF_8)));
            String result = RestHttpUtils.postFormUrlEncodeForStr(map.get("url"), httpHeaders, map);
            Map resultMap = JsonUtils.toObj(result, Map.class);
            if (!ObjectUtils.isEmpty(resultMap)) {
                map.clear();
                map.put("Authorization", "Bearer " + resultMap.get("access_token").toString());
            }

        }
    }


    @Override
    public OpenThirdProjectVo getProjectMaping(OpenCustomizeConfig openCustomizeConfig, String respData) {
        OpenThirdProjectVo openThirdProjectVo = new OpenThirdProjectVo();
        List<SupportUcThirdProjectReqDTO> addThirdProjectReqDTO = new ArrayList<>();
        Map<String, Object> data = JsonUtils.toObj(respData, Map.class);
        if (!ObjectUtils.isEmpty(data)) {
            List<Map<String, Object>> mapList = (List<Map<String, Object>>) MapUtils.getValueByExpress(data, "data:records");
            List<Map> list = etlService.transform(openCustomizeConfig.getEtlConfigId(), mapList);
            try {
                list.forEach(t -> {
                    SupportUcThirdProjectReqDTO addThirdProjectReqDTO1 = JsonUtils.toObj(JsonUtils.toJson(t), SupportUcThirdProjectReqDTO.class);
                    addThirdProjectReqDTO.add(addThirdProjectReqDTO1);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            openThirdProjectVo.setAddThirdProjectReqDTO(addThirdProjectReqDTO);
            return openThirdProjectVo;
        }
        return null;
    }

}

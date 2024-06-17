package com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.impl;

import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxkGetCustomDataListReqDTO;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxkGetCustomDataListRespDTO;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: FxkCommonServiceApi</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021/12/15 2:15 下午
 */
@ServiceAspect
@Service
@Slf4j
public class FxkCommonApiService {

    @Autowired
    FxkCustomDataServiceImpl fxkCustomDataService;

    /**
     * 分页获取自定义对像全部数据
     */
    public void getAllCustomData(FxkGetCustomDataListReqDTO req, List<Map<String, Object>> listMap) {
        FxkGetCustomDataListRespDTO fxkGetCustomDataListRespDTO = fxkCustomDataService.getCustomDataListV2(req);
        if (!ObjectUtils.isEmpty(fxkGetCustomDataListRespDTO) && fxkGetCustomDataListRespDTO.getErrorCode().equals(0)) {
            if (!ObjectUtils.isEmpty(fxkGetCustomDataListRespDTO.getData())) {
                List<Map<String, Object>> mapList = new ArrayList<>();
                fxkGetCustomDataListRespDTO.getData().getDataList().forEach(t -> {
                    Map<String, Object> map = new HashMap<>();
                    t.forEach((k, v) -> {
                        map.put(k.toString(), v);
                    });
                    mapList.add(map);
                });
                listMap.addAll(mapList);
                if (fxkGetCustomDataListRespDTO.getData().getLimit() + fxkGetCustomDataListRespDTO.getData().getOffset() < fxkGetCustomDataListRespDTO.getData().getTotal()) {
                    Integer limit = req.getData().getSearchqueryInfo().getLimit();
                    Integer offSet = req.getData().getSearchqueryInfo().getOffset();
                    req.getData().getSearchqueryInfo().setOffset(offSet + limit);
                    getAllCustomData(req, listMap);
                }
            }
        }
    }
}

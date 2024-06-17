package com.fenbeitong.openapi.plugin.dingtalk.yida.service.impl;

import com.fenbeitong.openapi.plugin.dingtalk.yida.constant.YiDaApiContant;
import com.fenbeitong.openapi.plugin.dingtalk.yida.dto.YiDaApplyDetailRespDTO;
import com.fenbeitong.openapi.plugin.dingtalk.yida.dto.YiDaFormDetailRespDTO;
import com.fenbeitong.openapi.plugin.dingtalk.yida.dto.YiDaGetFormIdRespDTO;
import com.fenbeitong.openapi.plugin.dingtalk.yida.service.IYiDaFormService;
import com.fenbeitong.openapi.plugin.dingtalk.yida.util.YiDaPostClientUtil;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.util.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
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
public class YiDaFormServiceImpl implements IYiDaFormService {

    @Autowired
    private YiDaPostClientUtil yiDaPostClientUtil;

    @Autowired
    private OpenThirdScriptConfigDao openThirdScriptConfigDao;

    @Override
    public YiDaFormDetailRespDTO getFormDataById(String processInstanceId, String corpId) {
        Map<String, String> params = new HashMap();
        params.put("formInstId", processInstanceId);
        String result = yiDaPostClientUtil.post(YiDaApiContant.GET_FORM_DATA_BY_ID, params, corpId);
        return JsonUtils.toObj(result, YiDaFormDetailRespDTO.class);
    }

    public YiDaGetFormIdRespDTO searchForm(String formUuid, String corpId, int currentPage, int pageSize) {
        Map<String, String> params = new HashMap();
        params.put("formUuid", formUuid);
        params.put("currentPage", StringUtils.obj2str(currentPage));
        params.put("pageSize", StringUtils.obj2str(pageSize));
        String result = yiDaPostClientUtil.post(YiDaApiContant.SEARCH_FORM_DATA_IDS, params, corpId);
        return JsonUtils.toObj(result, YiDaGetFormIdRespDTO.class);
    }


    @Override
    public List<String> getAllFormIds(String formUuid, String corpId) {
        List<String> result = Lists.newArrayList();
        int pageSize = 100;
        YiDaGetFormIdRespDTO yiDaGetFormIdRespDTO = searchForm(formUuid, corpId, 1, pageSize);
        Integer totalCount = yiDaGetFormIdRespDTO.getTotalCount();
        if (ObjectUtils.isEmpty(totalCount) || totalCount == 0) {
            return result;
        }
        result.addAll(yiDaGetFormIdRespDTO.getData());
        Integer totalPage = totalCount / pageSize + 1;
        for (int i = 2; i <= totalPage; i++) {
            ThreadUtils.sleep(50);
            yiDaGetFormIdRespDTO = searchForm(formUuid, corpId, i, pageSize);
            result.addAll(yiDaGetFormIdRespDTO.getData());
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> listFormDataByFormId(String formUuid, String corpId) {
        List<Map<String, Object>> result = Lists.newArrayList();
        List<String> allFormIds = getAllFormIds(formUuid, corpId);
        if (!ObjectUtils.isEmpty(allFormIds)) {
            for (String formInstId : allFormIds) {
                YiDaFormDetailRespDTO formData = getFormDataById(formInstId, corpId);
                Map<String, Object> data = formData.getFormData();
                data.put("formInstId", formInstId);
                result.add(data);
                ThreadUtils.sleep(50);
            }
        }
        return result;
    }

}

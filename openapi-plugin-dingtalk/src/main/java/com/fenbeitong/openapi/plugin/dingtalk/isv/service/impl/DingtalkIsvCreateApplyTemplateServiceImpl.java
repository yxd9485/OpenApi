package com.fenbeitong.openapi.plugin.dingtalk.isv.service.impl;

import com.alibaba.fastjson.JSON;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiProcessSaveRequest;
import com.dingtalk.api.response.OapiProcessSaveResponse;
import com.fenbeitong.openapi.plugin.dingtalk.isv.dao.DingtalkIsvCompanyDao;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.DingtalkIsvCompany;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyAuthService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.util.DingtalkIsvClientUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Description 审批模板创建
 * @Author duhui
 * @Date 2021-04-09
 **/
@ServiceAspect
@Service
@Slf4j
public class DingtalkIsvCreateApplyTemplateServiceImpl {


    @Autowired
    private IDingtalkIsvCompanyAuthService dingtalkIsvCompanyAuthService;

    @Autowired
    DingtalkIsvCompanyDao dingtalkIsvCompanyDao;

    @Autowired
    private DingtalkIsvClientUtils dingtalkIsvClientUtils;

    @Value("${dingtalk.host}")
    private String dingtalkHost;

    public String createApplyTemplate(String companyId, String companyName, String templateName) {
        DingtalkIsvCompany dingtalkIsvCompany;
        if (!ObjectUtils.isEmpty(companyId) && !ObjectUtils.isEmpty(companyName) && !ObjectUtils.isEmpty(templateName)) {
            Map map = Maps.newHashMap();
            map.put("companyId", companyId);
            map.put("companyName", companyName);
            dingtalkIsvCompany = dingtalkIsvCompanyDao.getDingtalkIsvCompanyByCompanyIdAndName(companyId, companyName);
        } else {
            return "参数错误";
        }
        OapiProcessSaveRequest request = new OapiProcessSaveRequest();
        OapiProcessSaveRequest.SaveProcessRequest saveProcessRequest = new OapiProcessSaveRequest.SaveProcessRequest();
        //saveProcessRequest.setDisableFormEdit(true);
        saveProcessRequest.setName(templateName);
        saveProcessRequest.setAgentid(dingtalkIsvCompany.getAgentid());
        saveProcessRequest.setDescription(templateName);
        // 注意，每种表单组件，对应的componentName是固定的，参照一下示例代码
        List<OapiProcessSaveRequest.FormComponentVo> formComponentList = Lists.newArrayList();
        // 单行文本框
        OapiProcessSaveRequest.FormComponentVo singleInput = new OapiProcessSaveRequest.FormComponentVo();
        singleInput.setComponentName("TextField");
        OapiProcessSaveRequest.FormComponentPropVo singleInputProp = new OapiProcessSaveRequest.FormComponentPropVo();
        singleInputProp.setRequired(true);
        singleInputProp.setLabel("单行输入框");
        singleInputProp.setPlaceholder("请输入");
        singleInputProp.setId("TextField-J78F056R");
        singleInput.setProps(singleInputProp);
        formComponentList.add( // 多行文本框
                singleInput);
        saveProcessRequest.setFormComponentList(formComponentList);
        request.setSaveProcessRequest(saveProcessRequest);
        String url = dingtalkHost + "topapi/process/save";
        OapiProcessSaveResponse response = dingtalkIsvClientUtils.executeWithCorpAccesstoken(url, request, dingtalkIsvCompany.getCorpId());
        if (response.getErrcode() == 0) {
            return JSON.toJSONString(response.getResult());
        } else {
            return response.getErrmsg();
        }

    }

}

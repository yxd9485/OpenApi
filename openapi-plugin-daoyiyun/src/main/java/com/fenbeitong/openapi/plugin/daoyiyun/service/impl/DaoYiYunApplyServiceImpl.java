package com.fenbeitong.openapi.plugin.daoyiyun.service.impl;

import com.finhub.framework.common.service.aspect.ServiceAspect;

import com.fenbeitong.openapi.plugin.daoyiyun.constant.DaoYiYunConstant;
import com.fenbeitong.openapi.plugin.daoyiyun.dto.DaoYiYunMainFormRespDTO;
import com.fenbeitong.openapi.plugin.daoyiyun.service.DaoYiYunApplyService;
import com.fenbeitong.openapi.plugin.daoyiyun.service.DaoYiYunUserService;
import com.fenbeitong.openapi.plugin.daoyiyun.util.DaoYiYunHttpUtil;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.MessageFormat;
import java.util.Map;


/**
 * 审批
 *
 * @author lizhen
 */
@ServiceAspect
@Service
@Slf4j
public class DaoYiYunApplyServiceImpl implements DaoYiYunApplyService {

    @Autowired
    private DaoYiYunHttpUtil httpUtil;

    @Autowired
    private DaoYiYunUserService daoYiYunUserService;

    /**
     * 获取主表数据
     * @param applicationId 应用id
     * @param processInstanceId 实例id
     * @param processCode 模板id
     * @return
     */
    public DaoYiYunMainFormRespDTO.MainForm getApplyMainForm(String applicationId, String processInstanceId, String processCode) {
        String url = DaoYiYunConstant.DAO_YI_YUN_HOST + MessageFormat.format(DaoYiYunConstant.URL_APPLY_MAIN_FORM,
            applicationId, processCode, processInstanceId);
        String result = httpUtil.get(url, null, applicationId);
        DaoYiYunMainFormRespDTO mainFormRespDTO = JsonUtils.toObj(result, DaoYiYunMainFormRespDTO.class);
        DaoYiYunMainFormRespDTO.MainForm mainForm = mainFormRespDTO.getData();
        return mainForm;
    }


    /**
     * 创建表单
     * @param applicationId 应用id
     * @param formModelId 表单id
     */
    @Override
    public String createApplyInstance(String body, String applicationId, String formModelId) {
        String url = DaoYiYunConstant.DAO_YI_YUN_HOST + MessageFormat.format(DaoYiYunConstant.URL_APPLY_MODIFY,
            applicationId, formModelId);
        body = transLoginUserId(body, applicationId);
        String result = httpUtil.post(url, body, applicationId);
        return result;
    }

    /**
     * 转换审批中的loginUserId
     * 以账号换userId
     * @param body
     * @param applicationId
     */
    private String transLoginUserId(String body, String applicationId) {
        Map<String, Object> map = JsonUtils.toObj(body, Map.class);
        String loginUserId = StringUtils.obj2str(map.get("loginUserId"));
        if (!ObjectUtils.isEmpty(loginUserId)) {
            String userId = daoYiYunUserService.getUserId(loginUserId, applicationId);
            map.put("loginUserId", userId);
        }
        return JsonUtils.toJson(map);
    }
}

package com.fenbeitong.openapi.plugin.welink.isv.service;

import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.welink.common.util.WeLinkAesUtils;
import com.fenbeitong.openapi.plugin.welink.common.util.WeLinkIsvMarketEncryptUtils;
import com.fenbeitong.openapi.plugin.welink.isv.constant.WeLinkIsvConstant;
import com.fenbeitong.openapi.plugin.welink.isv.dto.*;
import com.fenbeitong.openapi.plugin.welink.isv.entity.WeLinkIsvCompanyTrial;
import com.fenbeitong.openapi.plugin.welink.isv.entity.WeLinkIsvOrder;
import com.fenbeitong.openapi.plugin.welink.isv.service.job.WeLinkIsvTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by lizhen on 2020/4/17.
 */
@ServiceAspect
@Service
@Slf4j
public class WeLinkIsvCallbackService {

    @Value("${welink.isv.appSecret}")
    private String appSecret;
    @Value("${welink.isv.key}")
    private String key;
    @Autowired
    private WeLinkIsvCompanyAuthService weLinkIsvCompanyAuthService;
    @Autowired
    private WeLinkIsvCallbackService weLinkIsvCallbackService;
    @Autowired
    private WeLinkIsvTaskService weLinkIsvTaskService;
    @Autowired
    private WeLinkIsvCompanyTrialDefinitionService weLinkIsvCompanyTrialDefinitionService;
    @Autowired
    private WeLinkIsvOrderService weLinkIsvOrderService;
    /**
     * command回调处理
     *
     * @param encryptMsg
     * @throws Exception
     */
    public void commandCallback(String encryptMsg) throws Exception {
        Map map = JsonUtils.toObj(encryptMsg, Map.class);
        String encrypt = StringUtils.obj2str(map.get("encrypt"));
        String ivStr = encrypt.substring(0, 24);
        String reqStr = encrypt.substring(24);
        String decryptMsg = WeLinkAesUtils.decryptByGcm(reqStr, appSecret, ivStr);
        log.info("【welink callback】接收到消息回调,解密结果:{}", decryptMsg);
        //体验版
        WeLinkIsvCallbackCompanyAuthTrialDTO weLinkIsvCallbackCompanyAuthTrialDto = JsonUtils.toObj(decryptMsg, WeLinkIsvCallbackCompanyAuthTrialDTO.class);
        String eventType = weLinkIsvCallbackCompanyAuthTrialDto.getEventType();
        if (!StringUtils.isBlank(eventType)) {
            switch (eventType) {
                case WeLinkIsvConstant.CORP_AUTH_EVENT:
                    initGenCorpAuthTask(weLinkIsvCallbackCompanyAuthTrialDto);
                    break;
                case WeLinkIsvConstant.CORP_CANCEL_AUTH_EVENT:
                    initGenCorpCancelAuthTask(weLinkIsvCallbackCompanyAuthTrialDto);
                    break;
                case WeLinkIsvConstant.CORP_EDIT_USER_EVENT:
                    initGenUserEditTask(decryptMsg);
                    break;
                case WeLinkIsvConstant.CORP_DEL_USER_EVENT:
                    initGenUserDelTask(decryptMsg);
                    break;
                case WeLinkIsvConstant.CORP_EDIT_DEPT_EVENT:
                    initGenEditDepartmentTask(decryptMsg);
                    break;
                case WeLinkIsvConstant.CORP_DEL_DEPT_EVENT:
                    initGenDelDepartmentTask(decryptMsg);
                    break;
            }
        }
        //商城版
        WeLinkIsvCallbackMarketCorpBaseDTO weLinkIsvCallbackMarketCorpBaseDTO = JsonUtils.toObj(decryptMsg, WeLinkIsvCallbackMarketCorpBaseDTO.class);
        String activity = weLinkIsvCallbackMarketCorpBaseDTO.getActivity();
        if (!StringUtils.isBlank(activity)) {
            switch (activity) {
                case WeLinkIsvConstant.CORP_ACTIVITY_NEW_INSTANCE:
                    initGenCorpNewInstanceTask(decryptMsg);
                    break;
                case WeLinkIsvConstant.CORP_ACTIVITY_REFRESH_INSTANCE:
                    initGenCorpRefreshTask(decryptMsg);
                    break;
            }
        }

    }

    /**
     * 初始化更新人员task数据
     *
     * @param decryptMsg
     */
    private void initGenUserEditTask(String decryptMsg) {
        WeLinkIsvCallbackEmployeeDTO weLinkIsvCallbackEmployeeDTO = JsonUtils.toObj(decryptMsg, WeLinkIsvCallbackEmployeeDTO.class);
        weLinkIsvCallbackEmployeeDTO.setEventType(TaskType.WELINK_ISV_CORP_EDIT_USER.getKey());
        initGenUserTask(weLinkIsvCallbackEmployeeDTO);
    }

    /**
     * 初始化删除人员task数据
     *
     * @param decryptMsg
     */
    private void initGenUserDelTask(String decryptMsg) {
        WeLinkIsvCallbackEmployeeDTO weLinkIsvCallbackEmployeeDTO = JsonUtils.toObj(decryptMsg, WeLinkIsvCallbackEmployeeDTO.class);
        weLinkIsvCallbackEmployeeDTO.setEventType(TaskType.WELINK_ISV_CORP_DEL_USER.getKey());
        initGenUserTask(weLinkIsvCallbackEmployeeDTO);
    }

    /**
     * 初始化人员task数据
     *
     * @param weLinkIsvCallbackEmployeeDTO
     */
    public void initGenUserTask(WeLinkIsvCallbackEmployeeDTO weLinkIsvCallbackEmployeeDTO) {
        String eventType = weLinkIsvCallbackEmployeeDTO.getEventType();
        String eventTime = weLinkIsvCallbackEmployeeDTO.getTimestamp();
        Map<String, List<WeLinkIsvCallbackEmployeeDTO.CallbackEmployeeInfo>> deptMap = weLinkIsvCallbackEmployeeDTO.getData().stream().collect(Collectors.groupingBy(d -> d.getTenantId()));
        deptMap.forEach((k, v) -> {
            WeLinkIsvCompanyTrial weLinkIsvCompanyTrial = weLinkIsvCompanyTrialDefinitionService.getWelinkIsvCompanyTrialByCorpId(k);
            if (weLinkIsvCompanyTrial == null) {
                log.info("welink 回调corpId不存在，corpId={}", k);
                return;
            }
            List<String> userIds = v.stream().map(WeLinkIsvCallbackEmployeeDTO.CallbackEmployeeInfo::getUserId).collect(Collectors.toList());
            Map<String, Object> eventMsg = new HashMap<>();
            eventMsg.put("EventType", eventType);
            eventMsg.put("CorpId", k);
            eventMsg.put("TimeStamp", eventTime);
            eventMsg.put("UserId", userIds);
            weLinkIsvTaskService.genWeLinkIsvUserTask(eventMsg);
        });
    }

    /**
     * 初始化更新组织机构task数据
     *
     * @param decryptMsg
     */
    public void initGenEditDepartmentTask(String decryptMsg) {
        WeLinkIsvCallbackOrganizationDTO weLinkIsvCallbackOrganizationDTO = JsonUtils.toObj(decryptMsg, WeLinkIsvCallbackOrganizationDTO.class);
        weLinkIsvCallbackOrganizationDTO.setEventType(TaskType.WELINK_ISV_CORP_EDIT_DEPT.getKey());
        initGenDepartmentTask(weLinkIsvCallbackOrganizationDTO);
    }

    /**
     * 初始化删除组织机构task数据
     *
     * @param decryptMsg
     */
    public void initGenDelDepartmentTask(String decryptMsg) {
        WeLinkIsvCallbackOrganizationDTO weLinkIsvCallbackOrganizationDTO = JsonUtils.toObj(decryptMsg, WeLinkIsvCallbackOrganizationDTO.class);
        weLinkIsvCallbackOrganizationDTO.setEventType(TaskType.WELINK_ISV_CORP_DEL_DEPT.getKey());
        initGenDepartmentTask(weLinkIsvCallbackOrganizationDTO);
    }

    /**
     * 初始化组织机构task数据
     *
     * @param weLinkIsvCallbackOrganizationDTO
     */
    public void initGenDepartmentTask(WeLinkIsvCallbackOrganizationDTO weLinkIsvCallbackOrganizationDTO) {
        String eventType = weLinkIsvCallbackOrganizationDTO.getEventType();
        String eventTime = weLinkIsvCallbackOrganizationDTO.getTimestamp();
        Map<String, List<WeLinkIsvCallbackOrganizationDTO.CallbackOrganizationInfo>> deptMap = weLinkIsvCallbackOrganizationDTO.getData().stream().collect(Collectors.groupingBy(d -> d.getTenantId()));
        deptMap.forEach((k, v) -> {
            WeLinkIsvCompanyTrial weLinkIsvCompanyTrial = weLinkIsvCompanyTrialDefinitionService.getWelinkIsvCompanyTrialByCorpId(k);
            if (weLinkIsvCompanyTrial == null) {
                log.info("welink 回调corpId不存在，corpId={}", k);
                return;
            }
            List<String> deptCode = v.stream().map(WeLinkIsvCallbackOrganizationDTO.CallbackOrganizationInfo::getDeptCode).collect(Collectors.toList());
            Map<String, Object> eventMsg = new HashMap<>();
            eventMsg.put("EventType", eventType);
            eventMsg.put("CorpId", k);
            eventMsg.put("TimeStamp", eventTime);
            eventMsg.put("DeptId", deptCode);
            weLinkIsvTaskService.genWeLinkIsvDepartmentTask(eventMsg);
        });
    }

    /**
     * 初始化授权task数据
     *
     * @param weLinkIsvCallbackCompanyAuthTrialDto
     */
    private void initGenCorpAuthTask(WeLinkIsvCallbackCompanyAuthTrialDTO weLinkIsvCallbackCompanyAuthTrialDto) {
        String eventType = TaskType.WELINK_ISV_CORP_AUTH.getKey();
        String eventTime = weLinkIsvCallbackCompanyAuthTrialDto.getTimestamp();
        String corpId = weLinkIsvCallbackCompanyAuthTrialDto.getTenantId();
        Map<String, Object> eventMsg = new HashMap<>();
        eventMsg.put("EventType", eventType);
        eventMsg.put("CorpId", corpId);
        eventMsg.put("TimeStamp", eventTime);
        weLinkIsvTaskService.genWeLinkIsvChangeAuthTask(eventMsg);
    }

    /**
     * 初始化取消授权task数据
     *
     * @param weLinkIsvCallbackCompanyAuthTrialDto
     */
    private void initGenCorpCancelAuthTask(WeLinkIsvCallbackCompanyAuthTrialDTO weLinkIsvCallbackCompanyAuthTrialDto) {
        String eventType = TaskType.WELINK_ISV_CORP_CANCEL_AUTH.getKey();
        String eventTime = weLinkIsvCallbackCompanyAuthTrialDto.getTimestamp();
        String corpId = weLinkIsvCallbackCompanyAuthTrialDto.getTenantId();
        Map<String, Object> eventMsg = new HashMap<>();
        eventMsg.put("EventType", eventType);
        eventMsg.put("CorpId", corpId);
        eventMsg.put("TimeStamp", eventTime);
        weLinkIsvTaskService.genWeLinkIsvChangeAuthTask(eventMsg);
    }

    /**
     * 商城新购商品回调
     */
    private void initGenCorpNewInstanceTask(String decryptMsg) {
        WeLinkIsvCallbackNewInstanceDTO weLinkIsvCallbackNewInstanceDTO = JsonUtils.toObj(decryptMsg, WeLinkIsvCallbackNewInstanceDTO.class);
        String eventType = TaskType.WELINK_ISV_CORP_NEW_INSTANCE.getKey();
        String eventTime = weLinkIsvCallbackNewInstanceDTO.getTimeStamp();
        String orderId = weLinkIsvCallbackNewInstanceDTO.getOrderId();
        WeLinkIsvCallbackNewInstanceDTO.PlatformParams platformParams = weLinkIsvCallbackNewInstanceDTO.getPlatformParams();
        String corpId = "";
        if (platformParams != null) {
            corpId = platformParams.getTennantId();
        } else {
            return;
        }
        //保存订单
        WeLinkIsvOrder weLinkIsvOrder = new WeLinkIsvOrder();
        BeanUtils.copyProperties(weLinkIsvCallbackNewInstanceDTO, weLinkIsvOrder);
        weLinkIsvOrderService.saveWeLinkIsvOrder(weLinkIsvOrder);
        //生成任务
        Map<String, Object> eventMsg = new HashMap<>();
        eventMsg.put("EventType", eventType);
        eventMsg.put("CorpId", corpId);
        eventMsg.put("TimeStamp", eventTime);
        eventMsg.put("DataId", orderId);
        eventMsg.put("DataContent", decryptMsg);
        weLinkIsvTaskService.genWeLinkIsvCorpNewInstanceAndRefreshTask(eventMsg);
    }

    /**
     * 商城续费商品回调
     */
    private void initGenCorpRefreshTask(String decryptMsg) {
        WeLinkIsvCallbackRefreshInstanceDTO weLinkIsvCallbackRechargeDTO = JsonUtils.toObj(decryptMsg, WeLinkIsvCallbackRefreshInstanceDTO.class);
        String eventType = TaskType.WELINK_ISV_CORP_REFRESH_INSTANCE.getKey();
        String eventTime = weLinkIsvCallbackRechargeDTO.getTimeStamp();
        String orderId = weLinkIsvCallbackRechargeDTO.getOrderId();
        Map<String, Object> eventMsg = new HashMap<>();
        eventMsg.put("EventType", eventType);
        eventMsg.put("TimeStamp", eventTime);
        eventMsg.put("DataId", orderId);
        eventMsg.put("DataContent", decryptMsg);
        weLinkIsvTaskService.genWeLinkIsvCorpNewInstanceAndRefreshTask(eventMsg);
    }

    /**
     * 商城回调处理入口
     *
     * @param request
     */
    public Map<String, String> marketCallback(HttpServletRequest request) {
        Map<String, String> result = new HashMap<>();
        //校验参数
        if (!WeLinkIsvMarketEncryptUtils.verificateRequestParams(request, key, 0)) {
            log.info("welink 商城回调参数校验失败");
            result.put("resultCode", "999999");
            result.put("resultMsg", "参数校验失败");
            return result;
        }
        result.put("resultCode", "000000");
        result.put("resultMsg", "success");
        Map<String, String> requestBody = getParams(request);
        String activity = requestBody.get("activity");
        String requestBodyStr = JsonUtils.toJson(requestBody);
        log.info("welink 商城回调:{}", requestBodyStr);
        if (!StringUtils.isBlank(activity)) {
            switch (activity) {
                case WeLinkIsvConstant.CORP_ACTIVITY_NEW_INSTANCE:
                    initGenCorpNewInstanceTask(requestBodyStr);
                    String businessId = request.getParameter("businessId");
                    result.put("instanceId", businessId);
                    break;
                case WeLinkIsvConstant.CORP_ACTIVITY_REFRESH_INSTANCE:
                    //initGenCorpRefreshTask(requestBodyStr);
                    break;
                case WeLinkIsvConstant.CORP_ACTIVITY_EXPIRE_INSTANCE:
                case WeLinkIsvConstant.CORP_ACTIVITY_RELEASE_INSTANCE:
//                    WeLinkIsvCallbackCompanyAuthTrialDTO weLinkIsvCallbackCompanyAuthTrialDto = new WeLinkIsvCallbackCompanyAuthTrialDTO();
//                    weLinkIsvCallbackCompanyAuthTrialDto.setTenantId(requestBody.get(""));
//                    weLinkIsvCallbackCompanyAuthTrialDto.setTimestamp(StringUtils.obj2str(System.currentTimeMillis()));
//                    initGenCorpCancelAuthTask(weLinkIsvCallbackCompanyAuthTrialDto);
                    break;
                default:
                    break;
            }
        }
        return result;
    }


    private Map<String, String> getParams(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = (String) paramNames.nextElement();
            String value = request.getParameter(name);
            if ("saasExtendParams".equals(name)) {
                value = new String(Base64.decodeBase64(value));
            }
            map.put(name, value);
        }
        return map;
    }
}

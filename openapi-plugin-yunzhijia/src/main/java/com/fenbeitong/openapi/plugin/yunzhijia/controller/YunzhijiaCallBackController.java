package com.fenbeitong.openapi.plugin.yunzhijia.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;
import com.fenbeitong.openapi.plugin.support.apply.entity.ThirdApplyDefinition;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.common.YunzhijiaResponseCode;
import com.fenbeitong.openapi.plugin.yunzhijia.constant.YunzhijiaCallbackTagConstant;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaApplyEventDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaEmployeeContactDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.entity.YunzhijiaAddressList;
import com.fenbeitong.openapi.plugin.yunzhijia.entity.YunzhijiaApply;
import com.fenbeitong.openapi.plugin.yunzhijia.enums.YunzhijiaApplyActionType;
import com.fenbeitong.openapi.plugin.yunzhijia.enums.YunzhijiaApplyDataType;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaApplyService;
import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.YunzhijiaCallbackService;
import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.apply.YunzhijiaApplyServiceImpl;
import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.task.YunzhijiaTaskService;
import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.token.YunzhijiaTokenService;
import com.fenbeitong.openapi.plugin.yunzhijia.utils.AESEncryptor;
import com.fenbeitong.openapi.plugin.yunzhijia.utils.WebHookUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
@Slf4j
@EnableAutoConfiguration
@RequestMapping("/yunzhijia/callback/receive")
public class YunzhijiaCallBackController {
    @Autowired
    YunzhijiaCallbackService yunzhijiaCallbackService;
    @Autowired
    YunzhijiaTaskService yunzhijiaTaskService;
    @Autowired
    YunzhijiaTokenService yunzhijiaTokenService;
    @Autowired
    YunzhijiaApplyServiceImpl yunzhijiaApplyService;
    //企业的token
    private String erpToken = "";


    /**
     * 云之家通讯录消息回调接收
     *
     * @param header
     * @param eid
     * @param eventType
     * @param eventId
     * @param createTime
     * @return
     */
    @RequestMapping("/organization")
    public String receive(@RequestHeader Map<String, String> header, @RequestParam(required = false) String eid, @RequestParam(required = false) String eventType,
                          @RequestParam(required = false) String eventId, @RequestParam(required = false) String createTime) {
        log.info("接收到云之家通讯录回调数据");
        //当第一次进行回调地址验证时，直接返回正确信息，不进行数据验证
        if(eventType.equals(YunzhijiaCallbackTagConstant.YUNZHIJIA_CHECK_URL)){
            return "ok";
        }
        String contentBody = "eid=" + eid + "&eventType=" + eventType + "&eventId=" + eventId + "&createTime=" + createTime;
        Map<String, String> paramsMap = new TreeMap<String, String>();
        paramsMap.put("eid", eid);
        paramsMap.put("eventType", eventType);
        paramsMap.put("eventId", eventId);
        paramsMap.put("createTime", createTime);
        //根据不同的eid获取企业的通讯录token，根据数据库表存储关系来获取
        YunzhijiaAddressList yunzhijiaToken = yunzhijiaTokenService.getYunzhijiaToken(eid);
        if (!ObjectUtils.isEmpty(yunzhijiaToken)) {
            erpToken = yunzhijiaToken.getCorpToken();
        }
        contentBody = WebHookUtil.mapToString(paramsMap);
        if (yunzhijiaCallbackService.checkAuth(erpToken, contentBody, header)) {
            log.info("接收到云之家通讯录一个合法推送，内容为： {}", contentBody);
            //处理推送的逻辑写在这里,异步处理，服务端2S超时
            if (StringUtils.isNotBlank(contentBody) && contentBody.length() > 32) {//拼接参数不为空
                contentBody = contentBody.substring(0, 16);
            }
            yunzhijiaTaskService.createYunzhijiaTask(eid, eventId, eventType, contentBody, createTime);
        } else {
            log.info("接收到云之家通讯录一个非法推送，内容为： {}", contentBody);
            return "not ok";
        }
        return "ok";
    }


    /**
     * 云之家审批消息回调接收
     *
     * @return
     */
    @RequestMapping("/apply")
    public void receiveApply(HttpServletRequest request, @RequestParam(required = true) String eid) {
        log.info("接收到云之家审批回调数据");
        //1.解析具体数据
        String line;
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            log.info("读取数据异常 {}");
            e.printStackTrace();
        }
        String contentBody = sb.toString();
        log.info("云之家企业ID: {}", eid);
        log.info("接收到云之家审批回调数据 密文内容为: {}", contentBody);
        if (StringUtils.isBlank(contentBody)) {
            return;
        }
        //根据公司ID查询审批应用的开发者key
        YunzhijiaApply yunzhijiaApply = yunzhijiaApplyService.getYunzhijiaApplyByCorpId(eid);
        if (ObjectUtils.isEmpty(yunzhijiaApply)) {
            throw new OpenApiPluginException((NumericUtils.obj2int(YunzhijiaResponseCode.YUNZHIJIA_CORP_UN_REGIST)));
        }
        //解密数据
        AESEncryptor encryptor = new AESEncryptor(yunzhijiaApply.getAgentKey());
        String plainText = encryptor.decrypt(contentBody);
        log.info("解密回调明文内容： {}", plainText);
        YunzhijiaApplyEventDTO yunzhijiaApplyEventDTO = JsonUtils.toObj(plainText, new TypeReference<YunzhijiaApplyEventDTO>() {
        });
        YunzhijiaApplyEventDTO.YunzhijiaApplyBasicInfoDTO basicInfo = yunzhijiaApplyEventDTO.getData().getBasicInfo();
        if (ObjectUtils.isEmpty(basicInfo)) {
            throw new OpenApiPluginException((NumericUtils.obj2int(YunzhijiaResponseCode.YUNZHIJIA_APPLY_BASIC_IS_NULL)));
        }
        //判断是否是正常数据。如果是，则进行处理
        int dataType = basicInfo.getDataType();
        String key = YunzhijiaApplyDataType.YUNZHIJIA_APPLY_DATA_TYPE_NORMAL.getKey();
        if (key.equals(String.valueOf(dataType))) {
            log.info("接收到正常审批数据");
            //数据验证,查询审批模板是否为分贝通需要
            String formCodeId = basicInfo.getFormCodeId();
            //调用分贝通查看是否配置该模板
            ThirdApplyDefinition thirdApplyDefinitionById = yunzhijiaApplyService.getThirdApplyDefinitionById(formCodeId);
            if (!ObjectUtils.isEmpty(thirdApplyDefinitionById)) {
                log.info("企业审批数据已配置 {}",JsonUtils.toJson(thirdApplyDefinitionById));
                String actionType = basicInfo.getActionType();
                //节点同意后处理
                if (YunzhijiaApplyActionType.YUNZHIJIA_APPLY_ACTION_TYPE_REACH.getKey().equals(actionType)) {
                    long eventTime = basicInfo.getEventTime();
                    String formInstId = basicInfo.getFormInstId();
                    String corpId = basicInfo.getEid();
                    //根据任务ID查询审批单是否已经推送到task表里
                    Example example = new Example(Task.class);
                    example.createCriteria().andEqualTo("eventTime",eventTime).andEqualTo("corpId",corpId).andEqualTo("dataId",formInstId);
                    List<Task> tasks = yunzhijiaTaskService.listTaskByExamplye(example);
                    log.info("根据任务ID和时间戳获取相同任务数据 {}",ObjectUtils.isEmpty(tasks)?"返回数据为空，审批单ID："+formInstId+"添加入库操作":JsonUtils.toJson(tasks));
                    if(ObjectUtils.isEmpty(tasks)){
                        yunzhijiaTaskService.createYunzhijiaTask(corpId, formInstId, TaskType.YUNZHIJIA_APPROVE_CREATE.getKey(), formCodeId, String.valueOf(eventTime));
                    }
                }
            }
        }
    }
}

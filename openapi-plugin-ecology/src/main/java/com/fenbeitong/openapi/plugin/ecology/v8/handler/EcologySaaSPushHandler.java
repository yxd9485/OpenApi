package com.fenbeitong.openapi.plugin.ecology.v8.handler;

import com.fenbeitong.openapi.plugin.ecology.v8.service.IDealApproveService;
import com.fenbeitong.openapi.plugin.event.core.EventHandler;
import com.fenbeitong.openapi.plugin.event.saas.dto.SaasPushEvents;
import com.fenbeitong.openapi.plugin.support.annotation.WebAppEvent;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackType;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackRecordDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackRecord;
import com.fenbeitong.openapi.plugin.support.callback.service.IBusinessDataPushService;
import com.fenbeitong.openapi.plugin.support.company.dao.AuthDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.AuthDefinition;
import com.fenbeitong.openapi.plugin.support.employee.service.BaseEmployeeRefServiceImpl;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeContract;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeRes;
import com.fenbeitong.usercenter.api.service.employee.IThirdEmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * APP PUSH 消息到泛微
 * @Auther zhang.peng
 * @Date 2021/11/15
 */
@Component
@Slf4j
@SuppressWarnings("all")
public class EcologySaaSPushHandler extends EventHandler<SaasPushEvents> {

    @Autowired
    private IBusinessDataPushService businessDataPushService;

    @Autowired
    private ThirdCallbackRecordDao callbackRecordDao;

    @Autowired
    private IDealApproveService dealApproveService;

    @Autowired
    private AuthDefinitionDao authDefinitionDao;

    @Autowired
    private BaseEmployeeRefServiceImpl baseEmployeeRefService;

    // openType = 5 泛微
    @WebAppEvent(type = "app", version = "1.0" , value = 5)
    @Override
    public boolean process(SaasPushEvents kafkaPushMsg, Object... args) {
        log.info("处理泛微消息开始");
        log.info("泛微接收到消息,内部消息的消息体为：{}", kafkaPushMsg);
        String companyId = kafkaPushMsg.getCompanyId();
        String userId = kafkaPushMsg.getUserId();
        String title = kafkaPushMsg.getTitle();
        String content = kafkaPushMsg.getContent();
        String msg = kafkaPushMsg.getMsg();
        Map contentMap = JsonUtils.toObj(content,Map.class);
        Map msgMap = JsonUtils.toObj(msg,Map.class);
        ThirdCallbackRecord record = new ThirdCallbackRecord();
        record.setType(30);
        record.setTypeName("撤销审批单");
        record.setCompanyId(companyId);
        if ( null != msgMap.get("id")){
            record.setApplyId((String)msgMap.get("id"));
        }
        if ( null != msgMap.get("apply_type")){
            record.setApplyType((Integer)msgMap.get("apply_type"));
        }
        AuthDefinition authDefinition = authDefinitionDao.getAuthInfoByAppId(companyId);
        if ( null != authDefinition ){
            record.setCompanyName(authDefinition.getAppName());
        }
        // 1 是FBT员工
        record.setUserName(getEmployNameByEmplyeeId(companyId,userId,"0"));
        record.setCallbackType(CallbackType.ORDER_REVOKE.getType());
        record.setCallbackData(JsonUtils.toJson(kafkaPushMsg));
        callbackRecordDao.saveSelective(record);
        businessDataPushService.pushData(companyId,record,0,4);
        return true;
    }

    private String getEmployNameByEmplyeeId(String companyId , String employeeId , String employeeType){
        ThirdEmployeeContract thirdEmployeeContract = new ThirdEmployeeContract();
        thirdEmployeeContract.setCompanyId(companyId);
        thirdEmployeeContract.setEmployeeId(employeeId);
        thirdEmployeeContract.setType(1);
        thirdEmployeeContract.setUserType(employeeType.equals("0") ? 1 : 2);
        //调用uc接口根据公司ID和人员ID获取手机号
        String userName = "";
        try {
            IThirdEmployeeService thirdEmployeeService = baseEmployeeRefService.getThirdEmployeeService();
            ThirdEmployeeRes thirdEmployeeRes = thirdEmployeeService.queryEmployeeInfo(thirdEmployeeContract);
            userName = null == thirdEmployeeRes ? "" : thirdEmployeeRes.getEmployee().getName();
        } catch (Exception e){
            log.info("查询员工失败 {} ",e.getMessage());
        }
        return userName;
    }

}

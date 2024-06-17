package com.fenbeitong.openapi.plugin.dingtalk.isv.service.impl;

import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.dingtalk.common.service.apply.kit.DingtalkDinnerKitApplyFormParserServiceImpl;
import com.fenbeitong.openapi.plugin.dingtalk.eia.entity.DingtalkApply;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.DingtalkIsvCompany;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.OpenSyncBizDataMedium;
import com.fenbeitong.openapi.plugin.support.apply.dto.DinnerApproveCreateReqDTO;

import com.fenbeitong.openapi.plugin.support.apply.service.impl.OpenDinnerApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.entity.OpenSysConfig;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigType;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>Title: DingtalkCarApplyServiceImpl</p>
 * <p>Description: 用餐审批套件审批单</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaohai
 * @date 2021/12/31 10:57 PM
 */
@Slf4j
@Service
public class DingtalkIsvDinnerkitApplyServiceImpl<psvm> extends AbstractDingtalkIsvApplyService {


    @Autowired
    private DingtalkDinnerKitApplyFormParserServiceImpl formParser;

    @Autowired
    private OpenEmployeeServiceImpl openEmployeeService;

    @Autowired
    OpenDinnerApplyServiceImpl dinnerApplyServiceImpl;
    @Autowired
    private OpenSysConfigDao openSysConfigDao;

    @Override
    public TaskResult processApply(OpenSyncBizDataMedium task, DingtalkIsvCompany dingtalkIsvCompany, DingtalkApply apply, OapiProcessinstanceGetResponse.ProcessInstanceTopVo processInstanceTopVo) {
        LoginResVO loginResVO = openEmployeeService.loginAuthInit(dingtalkIsvCompany.getCompanyId() , processInstanceTopVo.getOriginatorUserid(), "1");
        String ucToken = loginResVO.getLogin_info().getToken();
        //获取部门名称
        String orgName = loginResVO.getCompany_info().getOrg_unit().getName();
        //部门id
        String orgId = "";
        String bizData = task.getBizData();
        //是否使用原有的数据结构，true ：表示使用原有的数据结构，前端还没上线，false：前端已经上线使用最新的数据格式。
        boolean useOriginal = false;
        OpenSysConfig openSysConfig = openSysConfigDao.getOpenSysConfigByTypeCode( OpenSysConfigType.DINGTALK_APPROVE_KIT.getType() , dingtalkIsvCompany.getCompanyId());
        if(openSysConfig != null){
            String value = openSysConfig.getValue();
            if(!StringUtils.isBlank(value) && value.contains("dinner")){
                //说明需要使用原有但数据结构,前端还没上线
                useOriginal = true;
            }
        }
        DinnerApproveCreateReqDTO commonApplyReqDTO = formParser.parserDinnerForm(bizData, task.getBizId(), orgId, orgName , useOriginal);
        commonApplyReqDTO.getApply().setCompanyId( dingtalkIsvCompany.getCompanyId() );
        try {
            dinnerApplyServiceImpl.createDinnerApprove(ucToken , commonApplyReqDTO);
        } catch (Exception e) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_SYNC_APPLY_FAILED, e.getMessage());
        }
        //记录审批实例信息
        saveDingtalkProcessInstance(task, apply, processInstanceTopVo);
        return TaskResult.SUCCESS;
    }

}


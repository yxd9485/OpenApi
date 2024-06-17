package com.fenbeitong.openapi.plugin.dingtalk.isv.service.impl;

import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.fenbeitong.openapi.plugin.dingtalk.common.service.apply.kit.DingtalkCarKitApplyFormParserServiceImpl;
import com.fenbeitong.openapi.plugin.dingtalk.eia.entity.DingtalkApply;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.DingtalkIsvCompany;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.OpenSyncBizDataMedium;
import com.fenbeitong.openapi.plugin.support.apply.dto.CarApproveCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CreateApplyRespDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.entity.OpenSysConfig;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigType;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;


/**
 * <p>Title: DingtalkCarApplyServiceImpl</p>
 * <p>Description: 用车套件审批单</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaohai
 * @date 2021/09/10 10:57 PM
 */
@Slf4j
@Service
public class DingtalkIsvCarkitApplyServiceImpl extends AbstractDingtalkIsvApplyService {


    @Autowired
    private DingtalkCarKitApplyFormParserServiceImpl formParser;

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    @Autowired
    private OpenEmployeeServiceImpl openEmployeeService;

    @Autowired
    private OpenSysConfigDao openSysConfigDao;

    @Override
    public TaskResult processApply(OpenSyncBizDataMedium task, DingtalkIsvCompany dingtalkIsvCompany, DingtalkApply apply, OapiProcessinstanceGetResponse.ProcessInstanceTopVo processInstanceTopVo) {
        LoginResVO loginResVO = openEmployeeService.loginAuthInit(dingtalkIsvCompany.getCompanyId() , processInstanceTopVo.getOriginatorUserid(), "1");
        String ucToken = loginResVO.getLogin_info().getToken();
        //获取部门名称和部门id
        //部门名称
        String orgName = loginResVO.getCompany_info().getOrg_unit().getName();
        //部门id
//        String orgId = loginResVO.getCompany_info().getOrg_unit().getId();
        String orgId = "";
        String bizData = task.getBizData();
        //是否使用原有的数据结构，true ：表示使用原有的数据结构，前端还没上线，false：前端已经上线使用最新的数据格式。
        boolean useOriginal = false;
        OpenSysConfig openSysConfig = openSysConfigDao.getOpenSysConfigByTypeCode( OpenSysConfigType.DINGTALK_APPROVE_KIT.getType() , dingtalkIsvCompany.getCompanyId());
        if(openSysConfig != null){
            String value = openSysConfig.getValue();
            if(!StringUtils.isBlank(value) && value.contains("car")){
                //说明需要使用原有但数据结构,前端还没上线
                useOriginal = true;
            }
        }
        CommonApplyReqDTO commonApplyReqDTO = formParser.parser( ucToken , bizData, task.getBizId() , orgId , orgName , null , useOriginal);
        CarApproveCreateReqDTO carApproveCreateReqDTO = (CarApproveCreateReqDTO) commonApplyService.convertApply(ucToken, commonApplyReqDTO);
        carApproveCreateReqDTO.getApply().setCompanyId(dingtalkIsvCompany.getCompanyId());
        CreateApplyRespDTO feiShuCarApprove = super.createCarApprove(ucToken, carApproveCreateReqDTO);
        if (ObjectUtils.isEmpty(feiShuCarApprove) || com.fenbeitong.openapi.plugin.util.StringUtils.isBlank(feiShuCarApprove.getId())) {
            throw new OpenApiPluginSupportException(SupportRespCode.APPLY_CREATE_ERROR);
        }
        return TaskResult.SUCCESS;
    }



}


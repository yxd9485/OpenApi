package com.fenbeitong.openapi.plugin.ecology.v8.standard.service.impl.fanwei;

import com.fenbeitong.openapi.plugin.ecology.v8.constant.CommonServiceTypeConstant;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.constant.FanWeiErrorCode;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dao.OpenEcologyWorkflowConfigDao;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dao.OpenWorkflowFormInfoDao;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.FanWeiResult;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.fanwei.FanWeiCreateWorkflowReqDTO;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.fanwei.FanWeiFormData;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.fanwei.FanWeiNoticeResultDto;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenEcologyWorkflowConfig;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenWorkflowFormInfo;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.fanwei.EcologyFanWeiCommonApplyService;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.service.fanwei.EcologyFanWeiWorkFlowService;
import com.fenbeitong.openapi.plugin.etl.dao.OpenEtlMappingConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenEtlMappingConfig;
import com.fenbeitong.openapi.plugin.support.apply.dto.CustomReimbursementDto;
import com.fenbeitong.openapi.plugin.support.apply.dto.FenbeitongApproveDto;
import com.fenbeitong.openapi.plugin.support.employee.service.IGetEmployeeInfoFromUcService;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdEmployeeDao;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdEmployee;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeRes;
import com.weaver.v8.workflow.WorkflowRequestInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 泛微通用流程实现
 * @Auther zhang.peng
 * @Date 2021/5/25
 */
@Slf4j
@ServiceAspect
@Service
public class EcologyWorkFlowCommonService {

    private static final String FANWEI_SUBMIT_RESPONSE_SUCCESS = "success";

    @Autowired
    private EcologyFanWeiCommonApplyService ecologyCommonApplyService;

    @Autowired
    private EcologyFanWeiWorkFlowService ecologyFanWeiWorkFlowService;

    @Autowired
    private OpenEcologyWorkflowConfigDao configDao;

    @Autowired
    private OpenThirdEmployeeDao openThirdEmployeeDao;

    @Autowired
    private OpenEtlMappingConfigDao etlMappingConfigDao;

    @Autowired
    private OpenWorkflowFormInfoDao workflowFormInfoDao;

    @Autowired
    private IGetEmployeeInfoFromUcService getEmployeeInfoFromUcService;

    // 通用反向审批
    public FanWeiResult pushCommonReverseApply(String object , String serviceType) {
        // 转换参数
        Map map = ecologyCommonApplyService.checkParam(object);
        // 获取消息通知结果
        FanWeiNoticeResultDto noticeResultDto = ecologyCommonApplyService.buildNoticeDto(serviceType,map);
        // 转换Dto
        FenbeitongApproveDto fenbeitongApproveDto = ecologyCommonApplyService.buildApproveDto(object,serviceType);
        // 人员姓名查询,后期脱敏后,采用这个字段
        buildOpenApiUserName(fenbeitongApproveDto,serviceType);
        // 查询表单信息
        OpenWorkflowFormInfo workflowFormInfo = workflowFormInfoDao.getFormInfoByCompanyIdAndType(fenbeitongApproveDto.getCompanyId(),serviceType);
        //未配置,直接返回
        if ( null == workflowFormInfo ){
            log.warn("未配置泛微表单信息 , companyId : {} ",fenbeitongApproveDto.getCompanyId());
            return FanWeiResult.error("泛微未配置相关表单信息");
        }
        String workFlowId = workflowFormInfo.getWorkflowId();
        // 查询工作流配置
        OpenEcologyWorkflowConfig workflowConfig = configDao.findByCompanyId(fenbeitongApproveDto.getCompanyId());
        // 查询模板详情
        FanWeiCreateWorkflowReqDTO reqDTO = new FanWeiCreateWorkflowReqDTO();
        BeanUtils.copyProperties(workflowFormInfo,reqDTO);
        reqDTO.setWorkflowType(Integer.valueOf(workflowFormInfo.getWorkflowType()));
        List<FanWeiFormData> workflowBaseInfos = ecologyFanWeiWorkFlowService.getFormContent(workflowConfig,reqDTO);
        workflowBaseInfos = workflowBaseInfos.stream().filter(formInfo->workFlowId.equals(formInfo.getWorkflowId())).collect(Collectors.toList());
        workflowBaseInfos.add(new FanWeiFormData());
        // 查询三方员工信息
        // 获取 etl 数据
        List<OpenEtlMappingConfig> mappingConfigList = etlMappingConfigDao.findMainId(Long.parseLong(workflowConfig.getTripFormMethod()));
        // 填充模板
        WorkflowRequestInfo workflowRequestInfo = ecologyCommonApplyService.fillFormData(workflowBaseInfos.get(0),fenbeitongApproveDto,serviceType,noticeResultDto.getApplyId(),mappingConfigList);
        // 创建流程
        // 提交了就可以了 , 不需要 submit
        String requestId = ecologyFanWeiWorkFlowService.doCreateWorkflow(workflowRequestInfo,fenbeitongApproveDto.getThirdEmployeeId(),workflowConfig);
        int requestIdInt = NumericUtils.obj2int(requestId, -1);
        if ( -1 !=requestIdInt ){
            if (FanWeiErrorCode.isError(requestIdInt)){
                FanWeiErrorCode fanWeiErrorCode = FanWeiErrorCode.parse(requestIdInt);
                log.warn("调用泛微接口失败 , error {} , requestId {}  ", fanWeiErrorCode.getDesc() , requestIdInt);
                return FanWeiResult.error(fanWeiErrorCode.getDesc());
            }
        }
        // 存储分贝通审批单ID和第三方审批单ID关系 , 传一个第三方 ID ： requestId
        if (!StringUtils.isBlank(requestId)){
            boolean result = ecologyCommonApplyService.saveFbtOrderApplyInfo(workflowBaseInfos,noticeResultDto,requestId);
            return result ? FanWeiResult.success() : FanWeiResult.error("关联分贝通申请单失败");
        } else {
            log.warn("提交审批失败 , 审批类型 {} , requestId {}" , serviceType , requestId);
            return FanWeiResult.error("创建表单失败");
        }
    }

    public void buildOpenApiUserName(FenbeitongApproveDto fenbeitongApproveDto, String serviceType){
        try {
            if ( null == fenbeitongApproveDto ){
                return;
            }
            if (CommonServiceTypeConstant.REIMBURSE.equals(serviceType)){
                CustomReimbursementDto customReimbursementDto  = (CustomReimbursementDto) fenbeitongApproveDto;
                if ( null == customReimbursementDto.getEmployee() ){
                    return;
                }
                String employeeName = customReimbursementDto.getEmployee().getEmployeeThirdName();
                fenbeitongApproveDto.setOpenApiUserName(employeeName);
                fenbeitongApproveDto.setOrderPerson(employeeName);
                return;
            }
            String employeeId = fenbeitongApproveDto.getThirdEmployeeId();
            if ( StringUtils.isBlank(employeeId) ){
                return;
            }
            ThirdEmployeeRes thirdEmployeeRes = getEmployeeInfoFromUcService.getEmployInfoByEmployeeId(fenbeitongApproveDto.getCompanyId(),fenbeitongApproveDto.getThirdEmployeeId(),"1");
            if ( null == thirdEmployeeRes || null == thirdEmployeeRes.getEmployee()){
                OpenThirdEmployee openThirdEmployee = openThirdEmployeeDao.getEmployeeByThirdId(fenbeitongApproveDto.getCompanyId(),fenbeitongApproveDto.getThirdEmployeeId());
                if ( null == openThirdEmployee){
                    return;
                } else {
                    // uc 查询失败,使用中间表数据
                    fenbeitongApproveDto.setOpenApiUserName(openThirdEmployee.getThirdEmployeeName());
                }
                return;
            }
            fenbeitongApproveDto.setOpenApiUserName(thirdEmployeeRes.getEmployee().getName());
            fenbeitongApproveDto.setOrderPerson(thirdEmployeeRes.getEmployee().getName());
        } catch (Exception e){
            log.info("设置人员信息失败 {}",e.getMessage());
        }
    }


}

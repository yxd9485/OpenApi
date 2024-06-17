package com.fenbeitong.openapi.plugin.feishu.common.service;

import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResponseCode;
import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResponseUtils;
import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResultEntity;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalSimpleFormDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuCreateApprovalInstanceReqDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeishuApplyReqDTO;
import com.fenbeitong.openapi.plugin.feishu.common.exception.OpenApiFeiShuException;
import com.fenbeitong.openapi.plugin.feishu.common.service.common.ApplyFormFactory;
import com.fenbeitong.openapi.plugin.support.apply.dao.OpenOrderApplyDao;
import com.fenbeitong.openapi.plugin.support.apply.dao.ThirdApplyDefinitionDao;
import com.fenbeitong.openapi.plugin.support.apply.entity.ThirdApplyDefinition;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.common.service.CommonService;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.company.service.impl.CommonPluginCorpAppDefinitionService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiaohai
 * @date 2022/07/05
 */
@Slf4j
@ServiceAspect
@Service
public abstract class AbstractPushApplyService {

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    @Autowired
    private ThirdApplyDefinitionDao thirdApplyDefinitionDao;

    @Autowired
    private CommonPluginCorpAppDefinitionService commonPluginCorpAppDefinitionService;

    @Autowired
    private CommonService commonService;

    @Autowired
    OpenOrderApplyDao openOrderApplyDao;

    public FeiShuResultEntity pushApply(String reqObj , String applyType , Integer processType){
        Map map = JsonUtils.toObj(reqObj, Map.class);
        String companyId = (String) map.get("company_id");
        String processcode = getProcesscode(companyId , processType);
        String corpId = getCorpId(  companyId );
        String thirdEmployeeId = (String) map.get("third_employee_id");
        String applyId = (String) map.get("apply_id");
        List<FeiShuApprovalSimpleFormDTO> approvalDefines = getFeiShuApprovalService().getApprovalDefine(processcode, corpId);
        ApplyFormFactory.getStrategyMap( applyType ).parseFormInfo( approvalDefines , reqObj );
        FeiShuCreateApprovalInstanceReqDTO feiShuCreateInstanceReqDTO = new FeiShuCreateApprovalInstanceReqDTO();
        feiShuCreateInstanceReqDTO.setApprovalCode(processcode);
        feiShuCreateInstanceReqDTO.setUserId(thirdEmployeeId);
        feiShuCreateInstanceReqDTO.setOpenId(thirdEmployeeId);
        feiShuCreateInstanceReqDTO.setForm(JsonUtils.toJson(approvalDefines));
        String approvalInstance = getFeiShuApprovalService( thirdEmployeeId , feiShuCreateInstanceReqDTO).createApprovalInstance(feiShuCreateInstanceReqDTO, corpId);
        //存储分贝通审批单ID和第三方审批单ID关系
        boolean b = commonApplyService.saveFbtOrderApply(companyId, thirdEmployeeId, applyId, approvalInstance, getOpenType());
        return b ? FeiShuResponseUtils.success("") : FeiShuResponseUtils.error(-1, "创建飞书审批单失败！");
    }

    public FeiShuResultEntity pushApply(FeishuApplyReqDTO reqDTO){
        String companyId = reqDTO.getCompanyId();
        String processcode = getProcesscode(companyId , reqDTO.getProcessType());
        String corpId = getCorpId( companyId );
        List<FeiShuApprovalSimpleFormDTO> approvalDefines = getFeiShuApprovalService().getApprovalDefine(processcode, corpId);
        ApplyFormFactory.getStrategyMap( StringUtils.obj2str(reqDTO.getApplyType() ) ).parseFormInfo( approvalDefines , reqDTO.getReqObj() );
        boolean b = feishuCreateApplyByThirdId(companyId, reqDTO.getThirdEmployeeId(), reqDTO.getApplyId(), processcode, approvalDefines);
        return b ? FeiShuResponseUtils.success("") : FeiShuResponseUtils.error(-1, "创建飞书审批单失败！");
    }

    private boolean feishuCreateApplyByThirdId( String companyId , String thirdEmployeeId , String  applyId , String processcode, List<FeiShuApprovalSimpleFormDTO> approvalFormList ){
        Map<String, String> applyInfo = new HashMap<String, String>() {{
            put("companyId", companyId );
            put("thirdEmployeeId", thirdEmployeeId);
            put("applyId", applyId);
        }};
        return createApply( applyInfo , processcode , approvalFormList);
    }

    private boolean createApply( Map<String, String> applyInfo , String processCode,  List<FeiShuApprovalSimpleFormDTO> approvalFormList ){
        String companyId = applyInfo.get("companyId");
        String employeeId = applyInfo.get("employeeId");
        String thirdEmployeeId = applyInfo.get("thirdEmployeeId");
        //查询企业授权信息
        PluginCorpDefinition pluginCorp = commonPluginCorpAppDefinitionService.getPluginCorpByCompanyId(companyId);
        if (pluginCorp == null) {
            log.info("【push信息】非飞书 eia企业,companyId:{}", companyId);
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_ISV_COMPANY_UNDEFINED);
        }
        String corpId =  pluginCorp.getThirdCorpId();
        if(StringUtils.isBlank(thirdEmployeeId)){
            thirdEmployeeId = commonService.getThirdEmployeeId(companyId, employeeId);
        }
        String applyId =  applyInfo.get("applyId") ;
        FeiShuCreateApprovalInstanceReqDTO feiShuCreateInstanceReqDTO = new FeiShuCreateApprovalInstanceReqDTO();
        feiShuCreateInstanceReqDTO.setApprovalCode(processCode);
        feiShuCreateInstanceReqDTO.setUserId(thirdEmployeeId);
        feiShuCreateInstanceReqDTO.setOpenId(thirdEmployeeId);
        feiShuCreateInstanceReqDTO.setForm(JsonUtils.toJson(approvalFormList));
        String approvalInstance = getFeiShuApprovalService().createApprovalInstance(feiShuCreateInstanceReqDTO, corpId);
        //存储分贝通审批单ID和第三方审批单ID关系
        return commonApplyService.saveFbtOrderApply(companyId, thirdEmployeeId, applyId, approvalInstance, getOpenType());
    }

    private String getProcesscode(String companyId , int processType){
        ThirdApplyDefinition thirdApply = thirdApplyDefinitionDao.getThirdApply(companyId,  processType );
        if (ObjectUtils.isEmpty(thirdApply)) {
            log.warn("模版信息配置有误，请检查审批模版配置信息！" );
            throw new OpenApiFeiShuException(SupportRespCode.FB_ORDER_APPLY_IS_NULL);
        }
        return thirdApply.getThirdProcessCode();
    }

    /**
     * 获取飞书实现类
     * @return
     */
    protected abstract AbstractFeiShuApprovalService getFeiShuApprovalService();

    /**
     * 获取飞书实现类，并设置员工id
     * @param thirdEmployeeId
     * @param feiShuCreateInstanceReqDTO
     * @return
     */
    protected abstract AbstractFeiShuApprovalService getFeiShuApprovalService(String thirdEmployeeId ,  FeiShuCreateApprovalInstanceReqDTO feiShuCreateInstanceReqDTO);

    /**
     * 获取openType
     * @return
     */
    protected abstract int getOpenType();

    /**
     * 获取corpId
     * @return
     */
    protected abstract String getCorpId( String companyId );

}

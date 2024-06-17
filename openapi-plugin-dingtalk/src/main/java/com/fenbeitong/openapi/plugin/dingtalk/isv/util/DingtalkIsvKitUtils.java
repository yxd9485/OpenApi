package com.fenbeitong.openapi.plugin.dingtalk.isv.util;

import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingTalkKitConstant;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.IFormFieldAliasConstant;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.dingtalk.isv.dto.*;
import com.fenbeitong.openapi.plugin.support.apply.service.IOpenApplyService;
import com.fenbeitong.openapi.plugin.support.common.service.CommonService;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.common.CommonIdDTO;
import com.fenbeitong.usercenter.api.model.dto.company.CompanyRuleDTO;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeSimpleInfoContract;
import com.fenbeitong.usercenter.api.model.dto.employee.OrgUnitListBean;
import com.fenbeitong.usercenter.api.model.dto.privilege.CommonAuthDTO;
import com.fenbeitong.usercenter.api.model.dto.privilege.CompanyAndEmployeeAuthDTO;
import com.fenbeitong.usercenter.api.model.dto.rule.CarPolicyBean;
import com.fenbeitong.usercenter.api.service.company.ICompanyRuleService;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import com.fenbeitong.usercenter.api.service.privilege.IRPrivilegeService;
import com.fenbeitong.usercenter.api.service.rule.IBaseEmployeeTaxiRuleExtService;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiaohai
 * @date 2021/12/09
 */
@ServiceAspect
@Service
@Slf4j
public class DingtalkIsvKitUtils {

    @Autowired
    private IOpenApplyService openApplyService;

    @Autowired
    private CommonService commonService;

    @DubboReference(check = false)
    private IBaseEmployeeExtService baseEmployeeExtService;

    @DubboReference(check = false)
    private IRPrivilegeService privilegeService;

    @DubboReference(check = false)
    private ICompanyRuleService companyRuleService;

    /**
     * 费用归属展示逻辑
     * @param param
     * @param formFieldDTOList
     * @param deptAndProDTO 别名
     * @param applyAttributionCategory  0-不展示 1-展示选填 2-展示必填
     */
    public void getCostAttrbutionCategory(Map<String,Object> param , List<IFormFieldDTO> formFieldDTOList , IFormDeptAndProDTO deptAndProDTO ,String applyAttributionCategory , DingtalkIsvKitReqDTO dingtalkIsvKitReqDTO) {
        String tab = deptAndProDTO.getTab();
        String project = deptAndProDTO.getProject();
        String department = deptAndProDTO.getDepartment();
        //用于判断项目和部门是否显示
        Map<String, Object> queryCostAttrbution = openApplyService.getCostAttribution( param );
        boolean required =  DingTalkKitConstant.CostAttribution.DISPLAY_AND_REQUIRED.equals( StringUtils.obj2str(applyAttributionCategory) ) ? true : false;
        //部门是否显示 true：为不显示 false为显示
        boolean departmentInvisible;
        //项目是否显示 true：为不显示 false为显示
        boolean projectInvisible;
        String costAttributionDeptmentDefault = StringUtils.obj2str( queryCostAttrbution.get("costAttributionDeptmentDefault"));
        String costAttributionDefaultDeptmentChange = StringUtils.obj2str( queryCostAttrbution.get("costAttributionDefaultDeptmentChange"));
        Map<String, Object> directDepartment = setDepartmentExtVal(costAttributionDeptmentDefault, costAttributionDefaultDeptmentChange, dingtalkIsvKitReqDTO);
        if(!DingTalkKitConstant.CostAttribution.NOT_DISPLAY.equals( applyAttributionCategory )){
            Object costAttributionList = queryCostAttrbution.get("costAttributionList");
            List<Map<String,Object>> list = JsonUtils.toObj(JsonUtils.toJson(costAttributionList), List.class);
            //部门是否显示
            departmentInvisible = MapUtils.isBlank( CollectionUtils.filter(list, "costAttributionCategory", Integer.valueOf( DingTalkKitConstant.CostAttribution.COST_ATTRBUTION_DEPATMENT) ));
            //项目是否显示
            projectInvisible = MapUtils.isBlank( CollectionUtils.filter(list, "costAttributionCategory", Integer.valueOf (DingTalkKitConstant.CostAttribution.COST_ATTRBUTION_PROJECT) ));
            //两者都必填，两者选其一  1:两者都必填 2:两者选其一
            String costAttributionScope = StringUtils.obj2str( queryCostAttrbution.get("costAttributionScope") );
            //两者选其一，项目和部门同时显示时，费用归属部门和项目tab切换出现
            if( !departmentInvisible && !projectInvisible && DingTalkKitConstant.CostAttribution.COST_ATTRBUTION_SCOPE.equals(costAttributionScope)){
                //两者选其一, tab出现，默认项目先不展示，切换时前端判断展示
                formFieldDTOList.add( setInvisible( tab,  false));
                projectInvisible = true;
            }else{
                //费用归属项目和部门tab显示
                formFieldDTOList.add( setInvisible( tab ,  true));
            }
            formFieldDTOList.add(setValueAndExtends( department ,  "" ,   JsonUtils.toJson(directDepartment), required , departmentInvisible));
            formFieldDTOList.add( setReqAndInvisible( project  , required , projectInvisible));
        }else{
            //项目归属不展示
            formFieldDTOList.add( setInvisible( tab ,  true));
            formFieldDTOList.add( setValueAndExtends( department ,  "" ,   JsonUtils.toJson(directDepartment), false , true));
            formFieldDTOList.add( setInvisible( project  ,true));
        }
    }

    /**
     *
     * @param costAttributionDeptmentDefault :0-不勾选 1-勾选 选择部门时默认员工直属部门且不可变更
     * @param costAttributionDefaultDeptmentChange :  0-不勾选 1-勾选 默认展示为员工直属部门且支持变更
     */
    private  Map<String,Object> setDepartmentExtVal(String costAttributionDeptmentDefault , String costAttributionDefaultDeptmentChange , DingtalkIsvKitReqDTO dingtalkIsvKitReqDTO){
        Map<String,Object> departmentExtVal = new HashMap<>();
        if(DingTalkKitConstant.CostAttribution.CHECKED.equals(costAttributionDeptmentDefault)){
            //选择部门时默认员工直属部门且不可变更
            departmentExtVal.put("directlyDepartment", true );
            departmentExtVal.put("departmentChange" , false);
            departmentExtVal.put("directlyDepartmentInfo" , directlyDepartment( dingtalkIsvKitReqDTO ) );
        }else if(DingTalkKitConstant.CostAttribution.CHECKED.equals(costAttributionDefaultDeptmentChange)){
            //默认展示为员工直属部门且支持变更
            departmentExtVal.put("directlyDepartment", true );
            departmentExtVal.put("departmentChange" , true );
            departmentExtVal.put("directlyDepartmentInfo" , directlyDepartment( dingtalkIsvKitReqDTO ) );
        }else{
            departmentExtVal.put("directlyDepartment", false );
            departmentExtVal.put("departmentChange" , false);
        }
        return departmentExtVal;
    }

    private OrgUnitListBean directlyDepartment(DingtalkIsvKitReqDTO dingtalkIsvKitReqDTO){
        String companyId = dingtalkIsvKitReqDTO.getCompanyId();
        String thirdEmployeeId = dingtalkIsvKitReqDTO.getUserId();
        String employeeId = getThirdEmployeeId(  companyId ,  thirdEmployeeId );
        EmployeeSimpleInfoContract employeeSimpleInfo = baseEmployeeExtService.getEmployeeSimpleInfo(employeeId, companyId);
        List<OrgUnitListBean> orgUnitlist = employeeSimpleInfo.getOrg_unit_list();
        return orgUnitlist.get(0);
    }


    /**
     * 申请事由
     * @param param
     * @param formFieldDTOList
     * @param applyConfig
     * @param type
     */
    public void getApplyReasonsInfo(Map<String,Object> param , List<IFormFieldDTO> formFieldDTOList , Map<String, Object> applyConfig , String type ) {
       if(StringUtils.isBlank(type)){
           return ;
       }
        Integer typeVal = Integer.valueOf(type);
        param.put("type" , typeVal );
        //查询申请事由列表数据
        Map<String, Object> applyReasons = openApplyService.getApplyReasons( param );
        // 申请事由列表
        Object reasonItems = applyReasons.get("reason_items");
        List<Map<String,Object>> list = JsonUtils.toObj(JsonUtils.toJson(reasonItems), List.class);
        //事由补充内容  0-选填 1-必填
        Object reasonDesc = applyReasons.get("reason_desc");
        boolean reasonDescRequired = (reasonDesc!=null && reasonDesc.equals(1)) ? true : false;
        List<IOptionsDTO> optionsList = new ArrayList<>();
        list.forEach( op -> optionsList.add(IOptionsDTO.builder().key(StringUtils.obj2str(op.get("id"))).value(StringUtils.obj2str(op.get("name"))).build()));
        if( DingTalkKitConstant.KitType.TRAVEL_TYPE.equals(type)){
            //差旅
            Object applyReasonChailv = applyConfig.get("apply_reason_chailv");
            setReasonFormList( IFormFieldAliasConstant.APPLY_SUBJECT  , IFormFieldAliasConstant.SUBJECT_SUPPLEMENT , applyReasonChailv ,  reasonDescRequired , optionsList ,  formFieldDTOList);
        }else if( DingTalkKitConstant.KitType.CAR_TYPE.equals(type)){
            //用车
            Object reason = applyReasons.get("reason");
            setReasonFormList( IFormFieldAliasConstant.CAR_LEAVE_TYEP  , IFormFieldAliasConstant.CAR_SUBTEXTAREA_FIELD , reason ,  reasonDescRequired , optionsList ,  formFieldDTOList);
        }else if( DingTalkKitConstant.KitType.DINNER_TYPE.equals(type)){
            //用餐
            Object reasonDinner = applyConfig.get("apply_reason_dinner");
            setReasonFormList( IFormFieldAliasConstant.DINNER_REASON  , IFormFieldAliasConstant.DINNER_REASON_SUPPLEMENT , reasonDinner ,  reasonDescRequired , optionsList ,  formFieldDTOList);
        }else if( DingTalkKitConstant.KitType.TAKEAWAY_TYPE.equals(type)){
            //外卖
            Object reasonDinner = applyConfig.get("apply_reason_takeaway");
            setReasonFormList( IFormFieldAliasConstant.TAKEAWAY_REASON  , IFormFieldAliasConstant.TAKEAWAY_REASON_SUPPLEMENT , reasonDinner ,  reasonDescRequired , optionsList ,  formFieldDTOList);
        }
    }

    /**
     * 查询是否有用车权限
     * @param formFieldDTOList
     * @param thirdEmployeeId
     * @param companyId
     */
    public void checkTaxiRule(List<IFormFieldDTO> formFieldDTOList , String thirdEmployeeId , String companyId) {
        CompanyRuleDTO companyRuleDTO = companyRuleService.queryByCompanyId(companyId);
        //企业用车场景开关 1:开 0:关
        Integer carRule = companyRuleDTO.getCarRule();
        String employeeId = getThirdEmployeeId(companyId, thirdEmployeeId);
        CompanyAndEmployeeAuthDTO companyAndEmployeeAuthDTO = privilegeService.queryCompanyAndEmployeeAuthDTOByCompanyAndEmployeeId(companyId, employeeId);
        CommonAuthDTO companyAuthDTO = companyAndEmployeeAuthDTO.getCompanyAuthDTO();
        CommonAuthDTO employeeAuthDTO = companyAndEmployeeAuthDTO.getEmployeeAuthDTO();
        Boolean employeeTaxiApplication = employeeAuthDTO.getTaxiApplication();
        Boolean taxiApplication = companyAuthDTO.getTaxiApplication();
        if(carRule.equals(0) || taxiApplication || !employeeTaxiApplication){
            formFieldDTOList.add( setValueAndExtends( IFormFieldAliasConstant.TRAVEL_OR_CAR ,  "否" ,   "{ \"key\": \"2\", \"value\": \"否\" }", false , true));
            return;
        }
        formFieldDTOList.add( setValueAndExtends( IFormFieldAliasConstant.TRAVEL_OR_CAR ,  "否" ,   "{ \"key\": \"2\", \"value\": \"否\" }", false , false));
    }


    /**
     *
     * @param reasonBizAlias 事由别名
     * @param supplementBuzAlias 事由补充别名
     */
    public void setReasonFormList( String reasonBizAlias , String supplementBuzAlias , Object reason , boolean reasonDescRequired ,
                                    List<IOptionsDTO> optionsList , List<IFormFieldDTO> formFieldDTOList){
        boolean reasonRequired = (reason!=null && DingTalkKitConstant.FIELD_REQUIRED.equals( reason ) ) ? true : false;
        formFieldDTOList.add(setOption( reasonBizAlias , optionsList , reasonRequired ));
        formFieldDTOList.add( setValue( supplementBuzAlias ,"" , reasonDescRequired ,null));
    }

    /**
     * 下拉属性赋值
     * @param bizAlias
     * @param options
     * @param required
     * @return
     */
    public IFormFieldDTO setOption( String  bizAlias , List<IOptionsDTO> options , Boolean required ){
        IFormFieldPropsDTO iFormFieldProps = IFormFieldPropsDTO.builder().options(options).required(required).build();
        return IFormFieldDTO.builder().bizAlias( bizAlias ).props(iFormFieldProps).build();
    }

    /**
     * value值赋值
     * @param bizAlias
     * @param value
     * @param required
     * @param invisible
     * @return
     */
    public IFormFieldDTO setValue( String  bizAlias , String value, Boolean required ,Boolean invisible){
        IFormFieldPropsDTO iFormFieldProps = IFormFieldPropsDTO.builder().required(required).invisible(invisible).build();
        return IFormFieldDTO.builder().bizAlias( bizAlias ).value(value).props(iFormFieldProps).build();
    }

    /**
     * required和invisible值赋值
     * @param bizAlias
     * @param required
     * @param invisible
     * @return
     */
    public IFormFieldDTO setReqAndInvisible( String  bizAlias ,Boolean required ,Boolean invisible){
        IFormFieldPropsDTO iFormFieldProps = IFormFieldPropsDTO.builder().required(required).invisible(invisible).build();
        return IFormFieldDTO.builder().bizAlias( bizAlias ).value("").extendValue("").props(iFormFieldProps).build();
    }

    /**
     * value和extendsValue值赋值
     * @param bizAlias
     * @param invisible
     * @return
     */
    public IFormFieldDTO setInvisible( String  bizAlias ,Boolean invisible){
        IFormFieldPropsDTO iFormFieldProps = IFormFieldPropsDTO.builder().required(null).invisible(invisible).build();
        return IFormFieldDTO.builder().bizAlias( bizAlias ).value("").extendValue("").props(iFormFieldProps).build();
    }

    /**
     * value和extendsValue值赋值
     * @param bizAlias
     * @param value
     * @param extendsValues
     * @param required
     * @param invisible
     * @return
     */
    public IFormFieldDTO setValueAndExtends( String  bizAlias , String value,  String extendsValues,Boolean required ,Boolean invisible){
        IFormFieldPropsDTO iFormFieldProps = IFormFieldPropsDTO.builder().required(required).invisible(invisible).build();
        return IFormFieldDTO.builder().bizAlias( bizAlias ).value(value).extendValue(extendsValues).props(iFormFieldProps).build();
    }

    /**
     * extendsValue值赋值
     * @param bizAlias
     * @param extendsValues
     * @return
     */
    public IFormFieldDTO setExtendsValue( String  bizAlias , String extendsValues){
        return IFormFieldDTO.builder().bizAlias( bizAlias ).extendValue( extendsValues ).build();
    }

    /**
     * 通过三方人员id查询分贝通人员id
     * @param companyId
     * @param userId
     * @return
     */
    private String getThirdEmployeeId( String companyId , String userId ){
        List<String> ids = new ArrayList<>();
        ids.add(userId);
        List<CommonIdDTO> commonIdDTOS = commonService.queryIdDTO(companyId, ids, 2, 3);
        if(commonIdDTOS == null || commonIdDTOS.size()<=0) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_USER_NOT_EXISTS);
        }
        return commonIdDTOS.get(0).getThirdId();
    }


}

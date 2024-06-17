package com.fenbeitong.openapi.plugin.dingtalk.common.service.apply.kit;

import com.fenbeitong.openapi.plugin.dingtalk.common.constant.IFormFieldAliasConstant;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkApprovalFormDTO;
import com.fenbeitong.openapi.plugin.support.apply.constant.SaasApplyType;
import com.fenbeitong.openapi.plugin.support.apply.dto.*;
import com.fenbeitong.openapi.plugin.util.*;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class DingtalkTakeawayKitApplyFormParserServiceImpl extends AbstractDingtalkParseApplyService{

    /**
     * 钉钉ISV外卖套件表单解析
     * @param bizData ：表单集合数据
     * @param instanceId
     * @return
     */
    public TakeawayApproveCreateReqDTO parserTakeawayForm(String  bizData , String instanceId , String orgId , String orgName) {
        DingtalkApprovalFormDTO dingtalkIsvApprovalFormDTO = JsonUtils.toObj(bizData, DingtalkApprovalFormDTO.class);
        List<DingtalkApprovalFormDTO.FormValueVOS> formValueVOSList = dingtalkIsvApprovalFormDTO.getFormValueVOS();
        TakeawayApproveCreateReqDTO commonApplyReqDTO = new TakeawayApproveCreateReqDTO();
        List<CostAttributionDTO> costAttributionList =  new ArrayList<>();
        //构建公用审批数据
        TakeawayApproveApply commonApply = buildApply(  instanceId ,  orgId ,  orgName );
        List<TakeawayApproveDetail> takeawayApproveDetails = parseFormInfo(formValueVOSList, commonApply, costAttributionList);
        commonApply.setCostAttributionList( costAttributionList );
        commonApplyReqDTO.setApply( commonApply );
        commonApplyReqDTO.setTripList(takeawayApproveDetails);
        return commonApplyReqDTO;
    }

    /**
     * 审批单基本信息
     * @param instanceId 第三方审批单id
     * @param orgId ：用户所属部门id
     * @param orgName：用户所属部门名称
     * @return
     */
    private TakeawayApproveApply buildApply( String instanceId , String orgId , String orgName ){
        TakeawayApproveApply commonApply = new TakeawayApproveApply();
        commonApply.setThirdId(instanceId);
        commonApply.setType(SaasApplyType.TakeAway.getValue());
        commonApply.setFlowType(4);
        commonApply.setBudget(0);
        commonApply.setCostAttributionId(orgId);
        commonApply.setCostAttributionName(orgName);
        commonApply.setCostAttributionCategory(1);
        return commonApply;
    }

    /**
     *
     * @param formValueVOSList :表单数据
     * @param commonApply ：申请单基本信息
     * @param costAttributionList ：费用归属信息
     */
    private List<TakeawayApproveDetail> parseFormInfo(List<DingtalkApprovalFormDTO.FormValueVOS> formValueVOSList , TakeawayApproveApply commonApply , List<CostAttributionDTO> costAttributionList){
        if(CollectionUtils.isBlank( formValueVOSList )) {
            return null;
        }
        TakeawayApproveDetail detail = new TakeawayApproveDetail();
        Map<String,Object> mapDinnerTime = MapUtils.newHashMap();
        formValueVOSList.forEach( formValueVOS ->  {
            String bizAlias = formValueVOS.getBizAlias();
            if(StringUtils.isBlank(bizAlias)){
                return ;
            }
            switch (bizAlias){
                case IFormFieldAliasConstant.TAKEAWAY_REASON://申请事由
                    commonApply.setApplyReason( formValueVOS.getValue() );
                    break;
                case IFormFieldAliasConstant.TAKEAWAY_REASON_SUPPLEMENT://事由补充
                    commonApply.setApplyReasonDesc( formValueVOS.getValue() );
                    break;
                case IFormFieldAliasConstant.TAKEAWAY_COST_MONEY://外卖总费用
                    String value = formValueVOS.getValue();
                    int estimatedAmount = NumericUtils.obj2int(value) * 100;
                    detail.setEstimatedAmount(estimatedAmount);
                    commonApply.setBudget(estimatedAmount);
                    break;
                case IFormFieldAliasConstant.TAKEAWAY_ADDRESS://送餐地址
                    String addressExtendValue = formValueVOS.getExtValue();
                    Map map = JsonUtils.toObj(addressExtendValue, Map.class);
                    detail.setStartCityId(StringUtils.obj2str( map.get("cityCode") ));
                    detail.setAddressId(StringUtils.obj2str( map.get("id") ));
                    detail.setAddressName(StringUtils.obj2str( formValueVOS.getValue() ));
                    detail.setCompanyAddressId(StringUtils.obj2str(map.get("companyAddressId")));
                    detail.setLat(NumericUtils.obj2double(StringUtils.obj2str(map.get("lat"))));
                    detail.setLng(NumericUtils.obj2double(StringUtils.obj2str(map.get("lng"))));
                    break;
                case IFormFieldAliasConstant.TAKEAWAY_DATE://送餐日期
                    mapDinnerTime.put("takeawayTime" , formValueVOS.getValue());
                    break;
                case IFormFieldAliasConstant.TAKEAWAY_INTERVAL://送餐时段
                    String extValue = formValueVOS.getExtValue();
                    mapDinnerTime.put("takeawayInterval" , extValue);
                    break;
                case IFormFieldAliasConstant.TAKEAWAY_COST_DEPARTMENT://费用归属部门
                    getCostDeaprtmentListNew( formValueVOS.getExtValue() , costAttributionList);
                    break;
                case IFormFieldAliasConstant.TAKEAWAY_COST_PROJECT://费用归属项目
                    getCostProjectList( formValueVOS.getExtValue() , costAttributionList);
                    break;
                default:
                    break;
            }
        });
        List<TakeawayApproveDetail> tripList = Lists.newArrayList();
        buildDinnerTime( mapDinnerTime , detail );
        tripList.add( detail );
        return tripList;
    }


    /**
     * 用餐时段
     */
    private void buildDinnerTime(Map<String,Object> mapDinnerTime , TakeawayApproveDetail detail) {
        String takeawayTime = StringUtils.obj2str( mapDinnerTime.get("takeawayTime") );
        String takeawayInterval = StringUtils.obj2str( mapDinnerTime.get("takeawayInterval") );
        Map map = JsonUtils.toObj(takeawayInterval, Map.class);
        String startTime = takeawayTime + " " +StringUtils.obj2str(map.get("startTime"));
        String endTime = takeawayTime + " " +StringUtils.obj2str(map.get("endTime"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            detail.setStartTime( sdf.parse(startTime) );
            detail.setEndTime( sdf.parse(endTime) );
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    @Override
    public String getCityList(String cityExtendValue){
        if(StringUtils.isBlank(cityExtendValue)){
            return null;
        }
        Map<String,Object> city = JsonUtils.toObj(cityExtendValue,  Map.class);
        String cityId = StringUtils.obj2str( city.get("id") );
        return cityId;
    }

}

package com.fenbeitong.openapi.plugin.dingtalk.common.service.apply.reverse;

import com.dingtalk.api.request.OapiProcessinstanceCreateRequest;
import com.dingtalk.api.response.OapiProcessinstanceCreateResponse;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.OpenOrderApplyEnum;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.dingtalk.common.service.apply.base.DingtalkApplyService;
import com.fenbeitong.openapi.plugin.dingtalk.common.util.DingtalkParseFormUtil;
import com.fenbeitong.openapi.plugin.support.apply.constant.OpenDinnerApplyConstant;
import com.fenbeitong.openapi.plugin.support.apply.constant.OpenMallApplyConstant;
import com.fenbeitong.openapi.plugin.support.apply.constant.OpenTripApplyConstant;
import com.fenbeitong.openapi.plugin.support.apply.dao.OpenOrderApplyDao;
import com.fenbeitong.openapi.plugin.support.apply.dto.*;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.company.entity.OpenCompanySourceType;
import com.fenbeitong.openapi.plugin.support.util.ExceptionRemind;
import com.fenbeitong.openapi.plugin.util.BigDecimalUtils;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Title: DingtalkIntranetPushApplyService<p>
 * <p>Description: 内部项目推送的申请单反向审批服务<p>
 * <p>Company:www.fenbeitong.com<p>
 *
 * @author liuhong
 * @date 2022/07/04 16:01
 */
@Slf4j
@ServiceAspect
@Service
public  class DingtalkIntranetPushApplyService extends AbstractDingTalkPushApplySuper {

    @Autowired
    OpenOrderApplyDao openOrderApplyDao;
    @Autowired
    CommonApplyServiceImpl commonApplyService;
    @Autowired
    ExceptionRemind exceptionRemind;
    @Autowired
    DingtalkApplyService dingtalkApplyService;
    @Autowired
    List<IIntranetParseReverseService> iIntranetParseReverseServices;

    /**
     * 推送反向审批单到钉钉
     *
     * @param baseDTO 内部项目推送申请信息基类
     * @param openType 对接类型（1：钉钉内嵌版，11：钉钉市场版）
     * @param callbackType 回调类型
     * @return 成功或失败
     */
    public boolean pushApply(IntranetApplyBaseDTO baseDTO,Integer openType,Integer callbackType) {
        // 1 数据校验
        if (ObjectUtils.isEmpty(baseDTO)) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_PUSH_DATA_IS_NULL);
        }
        //找到对应的表单解析类
        IIntranetParseReverseService parseService = getCallbackService(callbackType,iIntranetParseReverseServices);
        if (parseService == null){
            log.warn("根据回调类型未找到对应的解析表单实现类，callbackType:{}",callbackType);
            return false;
        }
        //判断公司是否已注册
        OpenCompanySourceType companySourceType = dingtalkApplyService.getCompanySourceByCompanyIdWithException(baseDTO.getCompanyId());
        //判断表单是否已注册
        String thirdProcessCode = dingtalkApplyService.getProcessCodeWithException(baseDTO.getCompanyId(), callbackType);

        // 2 组装数据
        OapiProcessinstanceCreateRequest req = new OapiProcessinstanceCreateRequest();
        // 模板号
        req.setProcessCode(thirdProcessCode);
        // 发起人用户ID
        req.setOriginatorUserId(baseDTO.getThirdEmployeeId());
        // 设置部门id
        req.setDeptId(buildDept(companySourceType.getThirdCompanyId(),baseDTO.getThirdOrgId()));
        // 表单组件构建
        List<OapiProcessinstanceCreateRequest.FormComponentValueVo> formComponentValueVoList = parseService.buildProcessReq(baseDTO);
        //过滤掉value为空的组件
        formComponentValueVoList = formComponentValueVoList.stream().filter(componentVO->!StringUtils.isBlank(componentVO.getValue())).collect(Collectors.toList());
        req.setFormComponentValues(formComponentValueVoList);

        // 3 处理返回结果
        OapiProcessinstanceCreateResponse processCreateResp = execute(openType, req, companySourceType.getThirdCompanyId());
        log.info("钉钉反向订单同步结果:{}", processCreateResp.getBody());
        if (ObjectUtils.isEmpty(processCreateResp) || processCreateResp.getErrcode() != 0) {
            exceptionRemind.remindDingTalk("钉钉订单审批推送失败，companyId:" + baseDTO.getCompanyId() + ",applyId:" + baseDTO.getApplyId());
            return false;
        }
        Map rspMap = JsonUtils.toObj(processCreateResp.getBody(), Map.class);
        if (!("0").equals(rspMap.get("errcode").toString())) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_PUSH_APPLY_ERROR, rspMap.get("errmsg").toString());
        }
        String spNo = rspMap.get("process_instance_id").toString();
        // 存储分贝通审批单ID和第三方审批单ID关系
        return commonApplyService.saveFbtOrderApply(baseDTO.getCompanyId(),
            baseDTO.getThirdEmployeeId(),
            baseDTO.getApplyId(),
            spNo,
            openType);

    }

    /**
     * @param callbackType                  回调类型
     * @param iIntranetParseReverseServices IApplyCallbackService的所有实现类
     * @return
     */
    private IIntranetParseReverseService getCallbackService(Integer callbackType, List<IIntranetParseReverseService> iIntranetParseReverseServices) {
        IIntranetParseReverseService targetService = null;
        if (!CollectionUtils.isBlank(iIntranetParseReverseServices)) {
            targetService = iIntranetParseReverseServices.stream()
                .filter(reverseService -> callbackType.equals(reverseService.getCallBackType()) )
                .findFirst()
                .orElse(null);
        }
        return targetService;
    }


    public boolean pushApply(String object, Integer openType) throws ParseException {
        //1.接收分贝通订单审批数据
        if (StringUtils.isBlank(object)) {
            throw new OpenApiPluginException(SupportRespCode.FB_ORDER_APPLY_IS_NULL);
        }
        Map map = JsonUtils.toObj(object, Map.class);
        if (ObjectUtils.isEmpty(map)) {
            throw new OpenApiPluginException(SupportRespCode.FB_ORDER_APPLY_IS_NULL);
        }

        Map<String, String> map1 = commonApplyService.parseFbtOrderApplyNotice(map);
        String companyId = map1.get("companyId");
        String thirdEmployeeId = map1.get("thirdEmployeeId");
        String thirdProcessCode = map1.get("thirdProcessCode");
        String thirdCorpId = map1.get("thirdCorpId");
        String applyId = map1.get("applyId");
        //3.解析分贝通审批数据
        Map dingtalkApplyMap = Maps.newHashMap();
        dingtalkApplyMap.put("companyId", companyId);
        //4.组装钉钉创建审批数据
        dingtalkApplyMap = commonApplyService.parseFbtOrderApplyDetail(map, dingtalkApplyMap);

        OapiProcessinstanceCreateRequest req = new OapiProcessinstanceCreateRequest();
        // 模板号
        req.setProcessCode(thirdProcessCode);
        // 发起人用户ID
        req.setOriginatorUserId(thirdEmployeeId);
        if (thirdCorpId.equals(map.get("third_dept_id"))) {
            // 所属部门ID，如果是根部门传"-1"
            req.setDeptId(-1L);
        } else {
            buildThirdOrgUnitId(req,map,THIRD_DEPT_ID);
        }

        List<OapiProcessinstanceCreateRequest.FormComponentValueVo> list2 = new ArrayList<OapiProcessinstanceCreateRequest.FormComponentValueVo>();
        OapiProcessinstanceCreateRequest.FormComponentValueVo obj4;

        String reason = "";
        String reasonDesc = "";
        // 1 是超规
        if (dingtalkApplyMap.get("exceedBuyType") != null && 1 == (Integer) dingtalkApplyMap.get("exceedBuyType")) {
            reason = reason + dingtalkApplyMap.get("applyReason");
            reasonDesc = reasonDesc + dingtalkApplyMap.get("applyReasonDesc");
        } else if (dingtalkApplyMap.get("remarkReason") != null) {
            reason = reason + dingtalkApplyMap.get("remarkReason");
        } else if (dingtalkApplyMap.get("remarkDetail") != null) {
            reasonDesc = reasonDesc + dingtalkApplyMap.get("remarkDetail");
        }

        // 申请理由
        obj4 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
        obj4.setName(OpenOrderApplyEnum.REMARK_REASON.msg());
        obj4.setValue(reason);
        // 申请详情
        obj4.setValue(obj4.getValue() + ";" + reasonDesc);
        list2.add(obj4);

        // 申请人
        if (dingtalkApplyMap.get("orderPerson") != null) {
            obj4 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
            obj4.setName(OpenOrderApplyEnum.ORDER_PERSON.msg());
            obj4.setValue(dingtalkApplyMap.get("orderPerson").toString());
            list2.add(obj4);
        }
        // 场景类型
        if (dingtalkApplyMap.get("orderType") != null) {
            obj4 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
            obj4.setName(OpenOrderApplyEnum.ORDER_TYPE.msg());
            obj4.setValue(dingtalkApplyMap.get("orderType").toString());
            list2.add(obj4);
        }
        // 使用人
        if (dingtalkApplyMap.get("guestName") != null) {
            obj4 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
            obj4.setName(OpenOrderApplyEnum.GUEST_NAME.msg());
            obj4.setValue(dingtalkApplyMap.get("guestName").toString());
            list2.add(obj4);
        }
        // 开始日期
        if (dingtalkApplyMap.get("beginDate") != null) {
            obj4 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
            obj4.setName(OpenOrderApplyEnum.BEGIN_DATE.msg());
            String beginDate = DateUtils.toSimpleStr(Long.parseLong(dingtalkApplyMap.get("beginDate").toString()), true);

            obj4.setValue(beginDate);
            list2.add(obj4);
        }
        // 结束日期
        if (dingtalkApplyMap.get("endDate") != null) {
            obj4 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
            obj4.setName(OpenOrderApplyEnum.END_DATE.msg());
            String endDate = DateUtils.toSimpleStr(Long.parseLong(dingtalkApplyMap.get("endDate").toString()), true);
            obj4.setValue(endDate);
            list2.add(obj4);
        }
        // 出发地
        if (dingtalkApplyMap.get("departureName") != null) {
            obj4 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
            obj4.setName(OpenOrderApplyEnum.DEPARTURE_NAME.msg());
            obj4.setValue(dingtalkApplyMap.get("departureName").toString());
            list2.add(obj4);
        }
        // 目的地
        if (dingtalkApplyMap.get("destinationName") != null) {
            obj4 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
            obj4.setName(OpenOrderApplyEnum.DESTINATION_NAME.msg());
            obj4.setValue(dingtalkApplyMap.get("destinationName").toString());
            list2.add(obj4);
        }

        // 订单金额
        if (dingtalkApplyMap.get("orderPrice") != null) {
            obj4 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
            obj4.setName(OpenOrderApplyEnum.ORDER_PRICE.msg());
            obj4.setValue(dingtalkApplyMap.get("orderPrice").toString());
            list2.add(obj4);
        }

        //过滤掉value为空的组件
        list2 = list2.stream().filter(componentVO->!StringUtils.isBlank(componentVO.getValue())).collect(Collectors.toList());
        // 表单参数
        req.setFormComponentValues(list2);

        // 第三方审批单
        OapiProcessinstanceCreateResponse rsp1 = execute(openType, req, thirdCorpId);

        if (ObjectUtils.isEmpty(rsp1) || rsp1.getErrcode() != 0) {
            exceptionRemind.remindDingTalk("钉钉订单审批推送失败，companyId:" + companyId + ",applyId:" + applyId);
            return false;
        }

        Map rspMap = JsonUtils.toObj(rsp1.getBody(), Map.class);
        if (!("0").equals(rspMap.get("errcode").toString())) {
            throw new FinhubException(3, rspMap.get("errmsg").toString());
        }
        String spNo = rspMap.get("process_instance_id").toString();

        //存储分贝通审批单ID和第三方审批单ID关系
        return commonApplyService.saveFbtOrderApply(companyId, thirdEmployeeId, applyId, spNo, openType);
    }

    public boolean pushTripApply(String object, Integer openType) {
        if (StringUtils.isBlank(object)) {
            throw new OpenApiPluginException(SupportRespCode.DATA_NOT_EXISTS);
        }
        Map map = JsonUtils.toObj(object, Map.class);
        if (ObjectUtils.isEmpty(map)) {
            throw new OpenApiPluginException(SupportRespCode.DATA_NOT_EXISTS);
        }
        Map<String, String> map1 = commonApplyService.parseFbtTripApplyNotice(map);
        String companyId = map1.get("companyId");
        String thirdEmployeeId = map1.get("thirdEmployeeId");
        String thirdProcessCode = map1.get("thirdProcessCode");
        String thirdCorpId = map1.get("thirdCorpId");
        String applyId = map1.get("applyId");

        ApplyDTO applyDTO = JsonUtils.toObj(object, ApplyDTO.class);
        if (ObjectUtils.isEmpty(applyDTO)
                || ObjectUtils.isEmpty(applyDTO.getTrip_list())) {
            throw new OpenApiPluginException(SupportRespCode.DATA_NOT_EXISTS);
        }
        commonApplyService.parseFbtTripApplyDetail(applyDTO);

        OapiProcessinstanceCreateRequest req = new OapiProcessinstanceCreateRequest();

        // 模板号
        req.setProcessCode(thirdProcessCode);
        // 发起人用户ID
        req.setOriginatorUserId(thirdEmployeeId);
        if (thirdCorpId.equals(map.get("third_org_id"))) {
            // 所属部门ID，如果是根部门传"-1"
            req.setDeptId(-1L);
        } else {
            buildThirdOrgUnitId(req,map,THIRD_ORG_ID);
        }

        List<OapiProcessinstanceCreateRequest.FormComponentValueVo> formComponentValueVoList = new ArrayList<>();

        OapiProcessinstanceCreateRequest.FormComponentValueVo tripListVO = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
        tripListVO.setName(OpenTripApplyConstant.TRIP_LIST);
        tripListVO.setValue(applyDTO.getTripListStr());
        formComponentValueVoList.add(tripListVO);

       /* OapiProcessinstanceCreateRequest.FormComponentValueVo guestListVO = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
        guestListVO.setName(OpenTripApplyConstant.GUEST_LIST);
        guestListVO.setValue(applyDTO.getGuestListStr());
        formComponentValueVoList.add(guestListVO);*/

        /*OapiProcessinstanceCreateRequest.FormComponentValueVo travelTimeVO = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
        travelTimeVO.setName(OpenTripApplyConstant.TRAVEL_TIME);
        travelTimeVO.setValue(applyDTO.getTravelTimeStr());
        formComponentValueVoList.add(travelTimeVO);*/

        OapiProcessinstanceCreateRequest.FormComponentValueVo whereIsVO = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
        whereIsVO.setName(OpenTripApplyConstant.WHERE_IS);
        whereIsVO.setValue(applyDTO.getCost_attribution_name());
        formComponentValueVoList.add(whereIsVO);

        ApplyTripDTO applyTripDTO = JsonUtils.toObj(object, ApplyTripDTO.class);
        DingtalkParseFormUtil.parseTripInfo( formComponentValueVoList , applyTripDTO);

        formComponentValueVoList = formComponentValueVoList.stream().filter(componentVO->!StringUtils.isBlank(componentVO.getValue())).collect(Collectors.toList());
        // 表单参数表单参数
        req.setFormComponentValues(formComponentValueVoList);

        OapiProcessinstanceCreateResponse rsp1 = execute(openType, req, thirdCorpId);
        log.info("钉钉反向订单同步结果:{}", rsp1.getBody());
        if (ObjectUtils.isEmpty(rsp1) || rsp1.getErrcode() != 0) {
            exceptionRemind.remindDingTalk("钉钉订单审批推送失败，companyId:" + companyId + ",applyId:" + applyId);
            return false;
        }
        Map rspMap = JsonUtils.toObj(rsp1.getBody(), Map.class);
        if (!("0").equals(rspMap.get("errcode").toString())) {
            throw new FinhubException(3, rspMap.get("errmsg").toString());
        }
        String spNo = rspMap.get("process_instance_id").toString();

        //存储分贝通审批单ID和第三方审批单ID关系
        return commonApplyService.saveFbtOrderApply(companyId, thirdEmployeeId, applyId, spNo, openType);
    }


    private Long buildDept(String corpId, String thirdOrgId) {
        if (corpId.equals(thirdOrgId)){
            return -1L;
        }
        return Long.parseLong(thirdOrgId);
    }

    private String getTimeRange(IntranetApplyCarDTO.Trip trip) {
        if (trip == null) {
            return null;
        }
        if (com.fenbeitong.openapi.plugin.util.StringUtils.isBlank(trip.getEndTime())) {
            return trip.getStartTime();
        }
        return trip.getStartTime() + " ~ " + trip.getEndTime();
    }

    public boolean pushMallApply(String object, Integer openType) {
        if (StringUtils.isBlank(object)) {
            throw new OpenApiPluginException(SupportRespCode.DATA_NOT_EXISTS);
        }
        Map map = JsonUtils.toObj(object, Map.class);
        if (ObjectUtils.isEmpty(map)) {
            throw new OpenApiPluginException(SupportRespCode.DATA_NOT_EXISTS);
        }
        Map<String, String> map1 = commonApplyService.parseFbtMallApplyNotice(map);
        String companyId = map1.get("companyId");
        String thirdEmployeeId = map1.get("thirdEmployeeId");
        String thirdProcessCode = map1.get("thirdProcessCode");
        String thirdCorpId = map1.get("thirdCorpId");
        String thirdOrgId = map1.get("third_org_id");
        String applyId = map1.get("applyId");
        ApplyDTO applyDTO = JsonUtils.toObj(object, ApplyDTO.class);
        if (ObjectUtils.isEmpty(applyDTO)
                || ObjectUtils.isEmpty(applyDTO.getTripList())) {
            throw new OpenApiPluginException(SupportRespCode.DATA_NOT_EXISTS);
        }
        commonApplyService.parseFbtMallApplyDetail(applyDTO);

        OapiProcessinstanceCreateRequest req = new OapiProcessinstanceCreateRequest();
        // 模板号
        req.setProcessCode(thirdProcessCode);
        // 发起人用户ID
        req.setOriginatorUserId(thirdEmployeeId);
        if (thirdCorpId.equals(thirdOrgId)) {
            // 所属部门ID，如果是根部门传"-1"
            req.setDeptId(-1L);
        } else {
            buildThirdOrgUnitId(req,map,THIRD_ORG_ID);
        }

        List<OapiProcessinstanceCreateRequest.FormComponentValueVo> formComponentValueVoList = new ArrayList<>();
        OapiProcessinstanceCreateRequest.FormComponentValueVo applyDescVO = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
        applyDescVO.setName(OpenMallApplyConstant.APPLY_DESC);
        applyDescVO.setValue(applyDTO.getApply_desc());
        formComponentValueVoList.add(applyDescVO);

        OapiProcessinstanceCreateRequest.FormComponentValueVo taxiInfoVO = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
        taxiInfoVO.setName(OpenMallApplyConstant.APPLY_TOTAL_PRICE);
        applyDTO.setApply_total_price(BigDecimalUtils.fen2yuan(applyDTO.getApply_total_price()));
        taxiInfoVO.setValue(applyDTO.getApply_total_price().toString());
        formComponentValueVoList.add(taxiInfoVO);

        OapiProcessinstanceCreateRequest.FormComponentValueVo mallInfoVO = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
        mallInfoVO.setName(OpenMallApplyConstant.MALL_INFO);
        mallInfoVO.setValue(applyDTO.getMallInfoStr());
        formComponentValueVoList.add(mallInfoVO);

        formComponentValueVoList = formComponentValueVoList.stream().filter(componentVO->!StringUtils.isBlank(componentVO.getValue())).collect(Collectors.toList());

        // 表单参数
        req.setFormComponentValues(formComponentValueVoList);
        // 第三方审批单
        OapiProcessinstanceCreateResponse rsp1 = execute(openType, req, thirdCorpId);
        log.info("钉钉采购审批同步结果:{}", rsp1.getBody());


        if (ObjectUtils.isEmpty(rsp1) || rsp1.getErrcode() != 0) {
            exceptionRemind.remindDingTalk("钉钉采购审批推送失败，companyId:" + companyId + ",applyId:" + applyId);
            return false;
        }

        Map rspMap = JsonUtils.toObj(rsp1.getBody(), Map.class);
        if (!("0").equals(rspMap.get("errcode").toString())) {
            throw new FinhubException(3, rspMap.get("errmsg").toString());
        }
        String spNo = rspMap.get("process_instance_id").toString();

        // 存储分贝通审批单ID和第三方审批单ID关系
        return commonApplyService.saveFbtOrderApply(companyId, thirdEmployeeId, applyId, spNo, openType);
    }

    public boolean pushApplyChange(String object, Integer openType) throws ParseException {
        //1.接收分贝通订单审批数据
        if (StringUtils.isBlank(object)) {
            throw new OpenApiPluginException(SupportRespCode.FB_ORDER_APPLY_IS_NULL);
        }
        Map map = JsonUtils.toObj(object, Map.class);
        if (ObjectUtils.isEmpty(map)) {
            throw new OpenApiPluginException(SupportRespCode.FB_ORDER_APPLY_IS_NULL);
        }

        Map<String, String> map1 = commonApplyService.parseFbtOrderChangeApplyNotice(map);
        String companyId = map1.get("companyId");
        String thirdEmployeeId = map1.get("thirdEmployeeId");
        String thirdProcessCode = map1.get("thirdProcessCode");
        String thirdCorpId = map1.get("thirdCorpId");
        String applyId = map1.get("applyId");
        //3.解析分贝通审批数据
        commonApplyService.parseChangeOrderInfo(map);

        OapiProcessinstanceCreateRequest req = new OapiProcessinstanceCreateRequest();
        // 模板号
        req.setProcessCode(thirdProcessCode);
        // 发起人用户ID
        req.setOriginatorUserId(thirdEmployeeId);
        if (thirdCorpId.equals(map.get("third_org_id"))) {
            // 所属部门ID，如果是根部门传"-1"
            req.setDeptId(-1L);
        } else {
            buildThirdOrgUnitId(req,map,THIRD_ORG_ID);
        }

        List<OapiProcessinstanceCreateRequest.FormComponentValueVo> list2 = new ArrayList<>();
        OapiProcessinstanceCreateRequest.FormComponentValueVo obj4;

        // 审批单类型
        if (map.get("changeType") != null) {
            obj4 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
            obj4.setName(OpenOrderApplyEnum.CHANGE_TYPE.msg());
            obj4.setValue(map.get("changeType").toString());
            list2.add(obj4);
        }
        // 关联审批单
//        if (map.get("oldThirdApply") != null) {
//            obj4 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
//            obj4.setName(OpenOrderApplyEnum.OLD_THIRD_APPLY.msg());
//            obj4.setValue(JsonUtils.toJson(map.get("oldThirdApply")));
//            list2.add(obj4);
//        }
        // 改签理由
        if (map.get("changeReason") != null) {
            obj4 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
            obj4.setName(OpenOrderApplyEnum.CHANGE_REASON.msg());
            obj4.setValue(map.get("changeReason").toString());
            // 申请详情
            if (map.get("changeDetail") != null) {
                obj4.setValue(obj4.getValue() + ";" + map.get("changeDetail").toString());
            }
            list2.add(obj4);
        }
        // 订单金额
        if (map.get("orderChangeFee") != null) {
            obj4 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
            obj4.setName(OpenOrderApplyEnum.ORDER_CHANGE_FEE.msg());
            obj4.setValue(map.get("orderChangeFee").toString());
            list2.add(obj4);
        }
        // 原订单信息
        if (map.get("oldOrderInfo") != null) {
            obj4 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
            obj4.setName(OpenOrderApplyEnum.OLD_ORDER_INFO.msg());
            obj4.setValue((String) map.get("oldOrderInfo"));
            list2.add(obj4);
        }
        // 改签订单信息
        if (map.get("orderInfo") != null) {
            obj4 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
            obj4.setName(OpenOrderApplyEnum.ORDER_INFO.msg());
            obj4.setValue((String) map.get("orderInfo"));
            list2.add(obj4);
        }

        list2 = list2.stream().filter(componentVO->!StringUtils.isBlank(componentVO.getValue())).collect(Collectors.toList());
        // 表单参数
        req.setFormComponentValues(list2);

        // 第三方审批单
        OapiProcessinstanceCreateResponse rsp1 = execute(openType, req, thirdCorpId);
        log.info("钉钉反向订单同步结果:{}", rsp1.getBody());


        if (ObjectUtils.isEmpty(rsp1) || rsp1.getErrcode() != 0) {
            exceptionRemind.remindDingTalk("钉钉订单审批推送失败，companyId:" + companyId + ",applyId:" + applyId);
            return false;
        }

        Map rspMap = JsonUtils.toObj(rsp1.getBody(), Map.class);
        if (!("0").equals(rspMap.get("errcode").toString())) {
            throw new FinhubException(3, rspMap.get("errmsg").toString());
        }
        String spNo = rspMap.get("process_instance_id").toString();

        //存储分贝通审批单ID和第三方审批单ID关系
        return commonApplyService.saveFbtOrderApply(companyId, thirdEmployeeId, applyId, spNo, openType);
    }

    public boolean pushApplyRefund(String object, Integer openType) {
        //1.接收分贝通订单审批数据
        if (StringUtils.isBlank(object)) {
            throw new OpenApiPluginException(SupportRespCode.FB_ORDER_APPLY_IS_NULL);
        }
        Map map = JsonUtils.toObj(object, Map.class);
        if (ObjectUtils.isEmpty(map)) {
            throw new OpenApiPluginException(SupportRespCode.FB_ORDER_APPLY_IS_NULL);
        }

        Map<String, String> map1 = commonApplyService.parseFbtOrderRefundApplyNotice(map);
        String companyId = map1.get("companyId");
        String thirdEmployeeId = map1.get("thirdEmployeeId");
        String thirdProcessCode = map1.get("thirdProcessCode");
        String thirdCorpId = map1.get("thirdCorpId");
        String applyId = map1.get("applyId");
        //3.解析分贝通审批数据
        commonApplyService.parseRefundOrderInfo(map);

        OapiProcessinstanceCreateRequest req = new OapiProcessinstanceCreateRequest();
        // 模板号
        req.setProcessCode(thirdProcessCode);
        // 发起人用户ID
        req.setOriginatorUserId(thirdEmployeeId);
        if (thirdCorpId.equals(map.get("third_org_id"))) {
            // 所属部门ID，如果是根部门传"-1"
            req.setDeptId(-1L);
        } else {
            buildThirdOrgUnitId(req,map,THIRD_ORG_ID);
        }

        List<OapiProcessinstanceCreateRequest.FormComponentValueVo> list2 = new ArrayList<>();
        OapiProcessinstanceCreateRequest.FormComponentValueVo obj4;

        // 审批单类型
        if (map.get("changeType") != null) {
            obj4 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
            obj4.setName(OpenOrderApplyEnum.CHANGE_TYPE.msg());
            obj4.setValue(map.get("changeType").toString());
            list2.add(obj4);
        }
        // 退订理由
        if (map.get("refundReason") != null) {
            obj4 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
            obj4.setName(OpenOrderApplyEnum.REFUND_REASON.msg());
            obj4.setValue(map.get("refundReason").toString());
            if (map.get("refundDetail") != null) {
                obj4.setValue(obj4.getValue() + ";" + map.get("refundDetail").toString());
            }
            list2.add(obj4);
        }
        // 原订单信息
        if (map.get("oldOrderInfo") != null) {
            obj4 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
            obj4.setName(OpenOrderApplyEnum.OLD_ORDER_INFO.msg());
            obj4.setValue(map.get("oldOrderInfo").toString());
            list2.add(obj4);
        }
        // 关联审批单
//        if (map.get("oldThirdApply") != null) {
//            obj4 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
//            obj4.setName(OpenOrderApplyEnum.OLD_THIRD_APPLY.msg());
//            obj4.setValue(JsonUtils.toJson(map.get("oldThirdApply")));
//            list2.add(obj4);
//        }
        list2 = list2.stream().filter(componentVO->!StringUtils.isBlank(componentVO.getValue())).collect(Collectors.toList());
        // 表单参数
        req.setFormComponentValues(list2);
        // 第三方审批单
        OapiProcessinstanceCreateResponse rsp1 = execute(openType, req, thirdCorpId);
        log.info("钉钉反向订单同步结果:{}", rsp1.getBody());
        if (ObjectUtils.isEmpty(rsp1) || rsp1.getErrcode() != 0) {
            exceptionRemind.remindDingTalk("钉钉退订订单审批推送失败，companyId:" + companyId + ",applyId:" + applyId);
            return false;
        }

        Map rspMap = JsonUtils.toObj(rsp1.getBody(), Map.class);
        if (!("0").equals(rspMap.get("errcode").toString())) {
            throw new FinhubException(3, rspMap.get("errmsg").toString());
        }
        String spNo = rspMap.get("process_instance_id").toString();

        //存储分贝通审批单ID和第三方审批单ID关系
        return commonApplyService.saveFbtOrderApply(companyId, thirdEmployeeId, applyId, spNo, openType);
    }


    public boolean pushDinnerApply(String object, Integer openType) {
        if (StringUtils.isBlank(object)) {
            throw new OpenApiPluginException(SupportRespCode.DATA_NOT_EXISTS);
        }
        Map map = JsonUtils.toObj(object, Map.class);
        if (ObjectUtils.isEmpty(map)) {
            throw new OpenApiPluginException(SupportRespCode.DATA_NOT_EXISTS);
        }
        Map<String, String> map1 = commonApplyService.parseFbtDinnerApplyNotice(map);
        String companyId = map1.get("companyId");
        String thirdEmployeeId = map1.get("thirdEmployeeId");
        String thirdProcessCode = map1.get("thirdProcessCode");
        String thirdCorpId = map1.get("thirdCorpId");
        String applyId = map1.get("applyId");

        ApplyDinnerDTO applyDinnerDTO = JsonUtils.toObj(object, ApplyDinnerDTO.class);
        if (ObjectUtils.isEmpty(applyDinnerDTO)
                || ObjectUtils.isEmpty(applyDinnerDTO.getTripList())) {
            throw new OpenApiPluginException(SupportRespCode.DATA_NOT_EXISTS);
        }

        OapiProcessinstanceCreateRequest req = new OapiProcessinstanceCreateRequest();

        // 模板号
        req.setProcessCode(thirdProcessCode);
        // 发起人用户ID
        req.setOriginatorUserId(thirdEmployeeId);
        if (thirdCorpId.equals(map.get("third_org_id"))) {
            // 所属部门ID，如果是根部门传"-1"
            req.setDeptId(-1L);
        } else {
            buildThirdOrgUnitId(req,map,THIRD_ORG_ID);
        }

        List<OapiProcessinstanceCreateRequest.FormComponentValueVo> formComponentValueVoList = new ArrayList<>();

        // 申请事由
        OapiProcessinstanceCreateRequest.FormComponentValueVo field1 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
        field1.setName(OpenDinnerApplyConstant.APPLY_PERSON);
        field1.setValue(applyDinnerDTO.getApplyReason());
        formComponentValueVoList.add(field1);

        // 申请人
        OapiProcessinstanceCreateRequest.FormComponentValueVo field2 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
        field2.setName(OpenDinnerApplyConstant.ORDER_PERSON);
        field2.setValue(applyDinnerDTO.getApplyName());
        formComponentValueVoList.add(field2);

        // 用餐金额
        OapiProcessinstanceCreateRequest.FormComponentValueVo field3 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
        field3.setName(OpenDinnerApplyConstant.DINNER_PRICE);
        field3.setValue(applyDinnerDTO.getTripList().get(0).getEstimatedAmount().toString());
        formComponentValueVoList.add(field3);

        // 用餐人数据
        OapiProcessinstanceCreateRequest.FormComponentValueVo field4 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
        field4.setName(OpenDinnerApplyConstant.person_number);
        field4.setValue(applyDinnerDTO.getTripList().get(0).getPersonCount());
        formComponentValueVoList.add(field4);

        // 用餐开始时间
        OapiProcessinstanceCreateRequest.FormComponentValueVo field5 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
        field5.setName(OpenDinnerApplyConstant.START_IME);
        field5.setValue(applyDinnerDTO.getTripList().get(0).getStartTime());
        formComponentValueVoList.add(field5);


        // 用餐结束时间
        OapiProcessinstanceCreateRequest.FormComponentValueVo field6 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
        field6.setName(OpenDinnerApplyConstant.EEND_TIME);
        field6.setValue(applyDinnerDTO.getTripList().get(0).getEndTime());
        formComponentValueVoList.add(field6);

        // 用餐城市
        OapiProcessinstanceCreateRequest.FormComponentValueVo field7 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
        field7.setName(OpenDinnerApplyConstant.CITY);
        field7.setValue(applyDinnerDTO.getTripList().get(0).getStartCityName());
        formComponentValueVoList.add(field7);

        formComponentValueVoList = formComponentValueVoList.stream().filter(componentVO->!StringUtils.isBlank(componentVO.getValue())).collect(Collectors.toList());
        // 表单参数
        req.setFormComponentValues(formComponentValueVoList);

        OapiProcessinstanceCreateResponse rsp1 = execute(openType, req, thirdCorpId);
        log.info("钉钉反向订单同步结果:{}", rsp1.getBody());
        if (ObjectUtils.isEmpty(rsp1) || rsp1.getErrcode() != 0) {
            exceptionRemind.remindDingTalk("钉钉用餐审批推送失败，companyId:" + companyId + ",applyId:" + applyId);
            return false;
        }
        Map rspMap = JsonUtils.toObj(rsp1.getBody(), Map.class);
        if (!("0").equals(rspMap.get("errcode").toString())) {
            throw new FinhubException(3, rspMap.get("errmsg").toString());
        }
        String spNo = rspMap.get("process_instance_id").toString();

        //存储分贝通审批单ID和第三方审批单ID关系
        return commonApplyService.saveFbtOrderApply(companyId, thirdEmployeeId, applyId, spNo, openType);
    }

    private void buildThirdOrgUnitId(OapiProcessinstanceCreateRequest req , Map map , String keyName){
        Object thirdId = map.get(keyName);
        try {
            req.setDeptId(Long.parseLong(thirdId.toString()));
        } catch (Exception e){
            log.info("三方 id 值为 {} 类型不是 Long , 请检查参数类型 ",thirdId);
            throw new OpenApiPluginException(SupportRespCode.DEPARTMENT_PARAM_ERROR,"三方部门ID字段类型不是Long");
        }
    }

    private static final String THIRD_DEPT_ID = "third_dept_id";
    private static final String THIRD_ORG_ID = "third_org_id";

}

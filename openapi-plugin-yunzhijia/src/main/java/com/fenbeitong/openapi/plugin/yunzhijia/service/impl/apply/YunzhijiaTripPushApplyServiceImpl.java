package com.fenbeitong.openapi.plugin.yunzhijia.service.impl.apply;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;
import com.fenbeitong.openapi.plugin.support.apply.dao.OpenOrderApplyDao;
import com.fenbeitong.openapi.plugin.support.apply.dao.ThirdApplyDefinitionDao;
import com.fenbeitong.openapi.plugin.support.apply.dto.FenbeitongApproveDto;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.revert.apply.dto.CommonNoticeResultDto;
import com.fenbeitong.openapi.plugin.support.revert.apply.service.CommonRevertApplyService;
import com.fenbeitong.openapi.plugin.util.*;
import com.fenbeitong.openapi.plugin.yunzhijia.constant.YunzhijiaResourceLevelConstant;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaAccessTokenReqDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaApplyRespDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.ticket.FenbeitongTripApproveDto;
import com.fenbeitong.openapi.plugin.yunzhijia.entity.YunzhijiaApply;
import com.fenbeitong.openapi.plugin.yunzhijia.enums.FbtTripType;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaFormService;
import com.fenbeitong.openapi.plugin.yunzhijia.service.impl.IYunzhijiaPushApplyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther zhang.peng
 * @Date 2021/4/28
 */
@ServiceAspect
@Service
@Slf4j
public class YunzhijiaTripPushApplyServiceImpl implements IYunzhijiaPushApplyService {

    @Autowired
    ThirdApplyDefinitionDao thirdApplyDefinitionDao;
    @Autowired
    PluginCorpDefinitionDao pluginCorpDefinitionDao;
    @Autowired
    YunzhijiaRemoteApplyService yunzhijiaRemoteApplyService;
    @Autowired
    YunzhijiaApplyServiceImpl yunzhijiaApplyService;
    @Autowired
    OpenOrderApplyDao openOrderApplyDao;
    @Autowired
    CommonApplyServiceImpl commonApplyService;

    @Autowired
    private CommonRevertApplyService commonRevertApplyService;

    @Autowired
    private IYunzhijiaFormService formDataService;

    @Override
    public boolean pushTripApply(String object) {
        //1.接收分贝通订单审批数据
        if (StringUtils.isBlank(object)) {
            throw new OpenApiPluginException(SupportRespCode.FB_TRIP_APPLY_IS_NULL);
        }
        Map map = JsonUtils.toObj(object, Map.class);
        FenbeitongTripApproveDto fenbeitongTripApproveDto = JsonUtils.toObj(object, FenbeitongTripApproveDto.class);
        if (ObjectUtils.isEmpty(map)) {
            throw new OpenApiPluginException(SupportRespCode.FB_TRIP_APPLY_IS_NULL);
        }
        //2.解析分贝通反向审批通知
        Map<String, String> map1 = commonApplyService.parseFbtTripApplyNotice(map);
        String companyId = map1.get("companyId");
        String thirdEmployeeId = map1.get("thirdEmployeeId");
        String thirdProcessCode = map1.get("thirdProcessCode");
        String thirdCorpId = map1.get("thirdCorpId");
        String applyId = map1.get("applyId");

        String employeeName = fenbeitongTripApproveDto.getEmployeeName();
        //3.解析分贝通审批数据
        Map<String,Object> yunzhijiaApplyMap = new HashMap<>();
        yunzhijiaApplyMap.put("companyId", companyId);
        //4.组装云之家创建审批数据
        Map<String,Object> yunzhijiaApplyReqMap = new HashMap<>();
        List<FenbeitongTripApproveDto.Trip> tripList = fenbeitongTripApproveDto.getTripList();
        StringBuilder tripInfoBuilder = new StringBuilder();
        if ( CollectionUtils.isNotBlank(tripList) ){
            tripList.stream().forEach(trip -> {
                //交通工具类型
                String tripType = null == FbtTripType.parse(trip.getType()) ? "无" : FbtTripType.parse(trip.getType()).getValue();
                //出发地-目的地-金额
                String tripCityInfo = "";
                if (FbtTripType.HOTEL.getCode() == trip.getType() ){
                    tripCityInfo = trip.getStartCityName() + " ¥" + trip.getEstimatedAmount();
                } else {
                    tripCityInfo = trip.getStartCityName() + "-" + trip.getArrivalCityName() + " ¥" + trip.getEstimatedAmount();
                }
                //出发时间
                String startTime = "开始时间：" + trip.getStartTime();
                //返回时间
                String endTime = "结束时间：" + (StringUtils.isBlank(trip.getEndTime()) ? "" : trip.getEndTime());
                tripInfoBuilder.append(tripType + "\n" + tripCityInfo + "\n" + startTime + "\n" + endTime + "\n");
            });
        }
        String tripFinalInfo = tripInfoBuilder.toString().substring(0,tripInfoBuilder.lastIndexOf("\n"));
        yunzhijiaApplyReqMap.put("_S_TITLE", employeeName + "的分贝通差旅审批单");
        yunzhijiaApplyReqMap.put("Ta_0",fenbeitongTripApproveDto.getCostAttributionName());//部门
        yunzhijiaApplyReqMap.put("Ta_1",fenbeitongTripApproveDto.getApplyReason() + "\n" + (StringUtils.isBlank(fenbeitongTripApproveDto.getApplyReasonDesc()) ? "" : fenbeitongTripApproveDto.getApplyReasonDesc()));//事由
        yunzhijiaApplyReqMap.put("Ta_2",tripFinalInfo);//行程列表
        yunzhijiaApplyReqMap.put("Ta_3",fenbeitongTripApproveDto.getCostAttributionName());//行程列表
        YunzhijiaApply yunzhijiaApplyByCorpId = yunzhijiaApplyService.getYunzhijiaApplyByCorpId(thirdCorpId);
        String agentId = yunzhijiaApplyByCorpId.getAgentId();
        String agentSecret = yunzhijiaApplyByCorpId.getAgentSecret();
        YunzhijiaAccessTokenReqDTO build = YunzhijiaAccessTokenReqDTO.builder()
                .eid(thirdCorpId)
                .appId(agentId)
                .secret(agentSecret)
                .timestamp(System.currentTimeMillis())
                .scope(YunzhijiaResourceLevelConstant.TEAM)
                .build();
        //5.创建云之家审批
        YunzhijiaApplyRespDTO yunzhijiaRemoteApply = yunzhijiaRemoteApplyService.createYunzhijiaRemoteApply(build, yunzhijiaApplyReqMap, thirdProcessCode, thirdEmployeeId);
        //分贝通申请单ID
        if (!ObjectUtils.isEmpty(yunzhijiaRemoteApply)) {//创建云之家审批成功
            Integer errorCode = yunzhijiaRemoteApply.getErrorCode();
            if (0 == errorCode) {
                YunzhijiaApplyRespDTO.YunzhijiaApplyData data = yunzhijiaRemoteApply.getData();
                //返回的云之家审批单ID
                String formInstId = data.getFormInstId();
                //存储分贝通审批单ID和第三方审批单ID关系
                return commonApplyService.saveFbtOrderApply(companyId, thirdEmployeeId, applyId, formInstId, OpenType.YUNZHIJIA.getType());
            }
        }
        //7.返回分贝通成功标识
        return false;
    }

    @Override
    public boolean pushCarApply(String object) {
        if (StringUtils.isBlank(object)) {
            throw new OpenApiPluginException(SupportRespCode.DATA_NOT_EXISTS);
        }
        Map map = JsonUtils.toObj(object, Map.class);
        FenbeitongTripApproveDto fenbeitongTripApproveDto = JsonUtils.toObj(object, FenbeitongTripApproveDto.class);
        if (ObjectUtils.isEmpty(map)) {
            throw new OpenApiPluginException(SupportRespCode.DATA_NOT_EXISTS);
        }
        Map<String, String> map1 = commonApplyService.parseFbtTaxiApplyNotice(map);
        String companyId = map1.get("companyId");
        String thirdEmployeeId = map1.get("thirdEmployeeId");
        String thirdProcessCode = map1.get("thirdProcessCode");
        String thirdCorpId = map1.get("thirdCorpId");
        String applyId = map1.get("applyId");
        //4.组装云之家创建审批数据
        List<FenbeitongTripApproveDto.Trip> tripList = fenbeitongTripApproveDto.getTripList();
        StringBuilder carInfoBuilder = new StringBuilder();
        StringBuilder ruleInfoBuilder = new StringBuilder();
        if ( CollectionUtils.isNotBlank(tripList) ){
            tripList.stream().forEach(trip -> {
                //申请用车信息：城市列表
                String cityList = CollectionUtils.isNotBlank(trip.getStartCityNameList()) ? trip.getStartCityNameList().toString() : "";
                //日期信息
                //出发时间,返回时间
                String startTime = DateUtils.toStr(DateUtils.toDate(trip.getStartTime()),"yyyy年MM月dd日");
                String endTime = DateUtils.toStr(DateUtils.toDate(trip.getEndTime()),"yyyy年MM月dd日");
                carInfoBuilder.append(cityList + "\n" + startTime + "-" + endTime);
                //管理员配置申请用车权限
                List<FenbeitongTripApproveDto.RuleInfo> ruleInfos = trip.getRuleInfos();
                if (CollectionUtils.isNotBlank(ruleInfos)){
                    ruleInfos.stream().forEach(ruleInfo -> {
                        ruleInfoBuilder.append(ruleInfo.getKey() + ":" + ruleInfo.getValue() + "\n");
                    });
                }
            });
        }
        String ruleFinalInfo = ruleInfoBuilder.toString().substring(0,ruleInfoBuilder.lastIndexOf("\n"));
        Map<String,Object> yunzhijiaApplyReqMap = new HashMap<>();
        String employeeName = fenbeitongTripApproveDto.getEmployeeName();
        yunzhijiaApplyReqMap.put("_S_TITLE", employeeName + "的分贝通用车审批单");
        yunzhijiaApplyReqMap.put("Ta_0",fenbeitongTripApproveDto.getCostAttributionName());//部门
        yunzhijiaApplyReqMap.put("Ta_1",fenbeitongTripApproveDto.getApplyReason() + "\n" + (StringUtils.isBlank(fenbeitongTripApproveDto.getApplyReasonDesc()) ? "" : fenbeitongTripApproveDto.getApplyReasonDesc()));//事由
        yunzhijiaApplyReqMap.put("Ta_2",carInfoBuilder.toString());//申请用车信息
        yunzhijiaApplyReqMap.put("Ta_3",ruleFinalInfo);//管理员配置申请用车权限
        YunzhijiaApply yunzhijiaApplyByCorpId = yunzhijiaApplyService.getYunzhijiaApplyByCorpId(thirdCorpId);
        String agentId = yunzhijiaApplyByCorpId.getAgentId();
        String agentSecret = yunzhijiaApplyByCorpId.getAgentSecret();
        YunzhijiaAccessTokenReqDTO build = YunzhijiaAccessTokenReqDTO.builder()
                .eid(thirdCorpId)
                .appId(agentId)
                .secret(agentSecret)
                .timestamp(System.currentTimeMillis())
                .scope(YunzhijiaResourceLevelConstant.TEAM)
                .build();
        //5.创建云之家审批
        YunzhijiaApplyRespDTO yunzhijiaRemoteApply = yunzhijiaRemoteApplyService.createYunzhijiaRemoteApply(build, yunzhijiaApplyReqMap, thirdProcessCode, thirdEmployeeId);
        //分贝通申请单ID
        if (!ObjectUtils.isEmpty(yunzhijiaRemoteApply)) {//创建云之家审批成功
            Integer errorCode = yunzhijiaRemoteApply.getErrorCode();
            if (0 == errorCode) {
                YunzhijiaApplyRespDTO.YunzhijiaApplyData data = yunzhijiaRemoteApply.getData();
                //返回的云之家审批单ID
                String formInstId = data.getFormInstId();
                //存储分贝通审批单ID和第三方审批单ID关系
                return commonApplyService.saveFbtOrderApply(companyId, thirdEmployeeId, applyId, formInstId, OpenType.YUNZHIJIA.getType());
            }
        }
        //7.返回分贝通成功标识
        return false;
    }

    public boolean pushCommonApply(String object , String serviceType) {
        // 转换参数
        Map map = commonRevertApplyService.checkParam(object);
        // 获取消息通知结果
        CommonNoticeResultDto noticeResultDto = commonRevertApplyService.buildNoticeDto(serviceType,map);
        // 转换Dto
        FenbeitongApproveDto fenbeitongApproveDto = JsonUtils.toObj(object, FenbeitongApproveDto.class);
        // 填充对应模板
        Map<String,Object> yunzhijiaApplyReqMap = formDataService.fillFormData(serviceType,fenbeitongApproveDto);
        // 查询云之家 token
        YunzhijiaAccessTokenReqDTO build = buildTokenReq(noticeResultDto.getThirdCorpId());
        // 创建云之家审批
        YunzhijiaApplyRespDTO yunzhijiaRemoteApply = formDataService.createApply(build,yunzhijiaApplyReqMap,noticeResultDto);
        // 执行分贝通逻辑
        return afterCreateYunzhijiaApply(yunzhijiaRemoteApply,noticeResultDto);
    }

    public YunzhijiaAccessTokenReqDTO buildTokenReq(String thirdCorpId){
        YunzhijiaApply yunzhijiaApplyByCorpId = yunzhijiaApplyService.getYunzhijiaApplyByCorpId(thirdCorpId);
        String agentId = yunzhijiaApplyByCorpId.getAgentId();
        String agentSecret = yunzhijiaApplyByCorpId.getAgentSecret();
        YunzhijiaAccessTokenReqDTO build = YunzhijiaAccessTokenReqDTO.builder()
                .eid(thirdCorpId)
                .appId(agentId)
                .secret(agentSecret)
                .timestamp(System.currentTimeMillis())
                .scope(YunzhijiaResourceLevelConstant.TEAM)
                .build();
        return build;
    }

    public boolean afterCreateYunzhijiaApply(YunzhijiaApplyRespDTO yunzhijiaRemoteApply , CommonNoticeResultDto noticeResultDto){
        if (!ObjectUtils.isEmpty(yunzhijiaRemoteApply)) {//创建云之家审批成功
            Integer errorCode = yunzhijiaRemoteApply.getErrorCode();
            if (0 == errorCode) {
                YunzhijiaApplyRespDTO.YunzhijiaApplyData data = yunzhijiaRemoteApply.getData();
                //返回的云之家审批单ID
                String formInstId = data.getFormInstId();
                //存储分贝通审批单ID和第三方审批单ID关系
                return commonApplyService.saveFbtOrderApply(noticeResultDto.getCompanyId(), noticeResultDto.getThirdEmployeeId(), noticeResultDto.getApplyId(), formInstId, OpenType.YUNZHIJIA.getType());
            }
        }
        return false;
    }

}

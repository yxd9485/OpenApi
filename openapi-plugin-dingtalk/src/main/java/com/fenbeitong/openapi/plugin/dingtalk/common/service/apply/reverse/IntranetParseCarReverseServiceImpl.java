package com.fenbeitong.openapi.plugin.dingtalk.common.service.apply.reverse;

import cn.hutool.core.util.ObjectUtil;
import com.dingtalk.api.request.OapiProcessinstanceCreateRequest;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingtalkPunctuationConstant;
import com.fenbeitong.openapi.plugin.support.apply.constant.OpenTaxiApplyConstant;
import com.fenbeitong.openapi.plugin.support.apply.constant.OpenTripApplyConstant;
import com.fenbeitong.openapi.plugin.support.apply.constant.ProcessTypeConstant;
import com.fenbeitong.openapi.plugin.support.apply.dto.IntranetApplyBaseDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.IntranetApplyCarDTO;
import com.fenbeitong.openapi.plugin.util.BigDecimalUtils;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.usercenter.api.model.enums.company.CostAttributionTypeEnum;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * <p>Title: IntranetParseCarReverseServiceImpl<p>
 * <p>Description: 钉钉用车反向审批表单解析<p>
 * <p>Company:www.fenbeitong.com<p>
 *
 * @author: liuhong
 * @date: 2022/7/5 14:02
 */
@Slf4j
@ServiceAspect
@Service
public class IntranetParseCarReverseServiceImpl implements IIntranetParseReverseService{
    @Override
    public Integer getCallBackType() {
       return ProcessTypeConstant.CAR_REVERSE;
    }

    @Override
    public List<OapiProcessinstanceCreateRequest.FormComponentValueVo> buildProcessReq(IntranetApplyBaseDTO baseDTO) {
        IntranetApplyCarDTO intranetApplyCarDTO = (IntranetApplyCarDTO)baseDTO;
        List<OapiProcessinstanceCreateRequest.FormComponentValueVo> formComponentValueVoList = new ArrayList<>();
        //申请用车信息
        OapiProcessinstanceCreateRequest.FormComponentValueVo taxiInfoVO = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
        taxiInfoVO.setName(OpenTaxiApplyConstant.APPLY_TAXI_INFO);
        taxiInfoVO.setValue(buildTaxiInfo(intranetApplyCarDTO));
        formComponentValueVoList.add(taxiInfoVO);
        //管理员配置申请用车权限
        OapiProcessinstanceCreateRequest.FormComponentValueVo ruleInfoVO = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
        ruleInfoVO.setName(OpenTaxiApplyConstant.RULE_INFO);
        ruleInfoVO.setValue(buildRuleInfo(intranetApplyCarDTO));
        formComponentValueVoList.add(ruleInfoVO);

        Optional<IntranetApplyCarDTO.Trip> tripOptional = Optional.ofNullable(intranetApplyCarDTO.getTripList()).flatMap(trips -> trips.stream().findFirst());
        //申请事由
        OapiProcessinstanceCreateRequest.FormComponentValueVo applyReasonVO = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
        applyReasonVO.setName(OpenTripApplyConstant.CarApply.APPLY_REASON);
        applyReasonVO.setValue(intranetApplyCarDTO.getApplyReason());
        formComponentValueVoList.add(applyReasonVO);
        //补充事由
        OapiProcessinstanceCreateRequest.FormComponentValueVo reasonDescVO = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
        reasonDescVO.setName(OpenTripApplyConstant.CarApply.APPLY_REASON_DESC);
        reasonDescVO.setValue(intranetApplyCarDTO.getApplyReasonDesc());
        formComponentValueVoList.add(reasonDescVO);

        //用车城市
        OapiProcessinstanceCreateRequest.FormComponentValueVo startCityVO = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
        startCityVO.setName(OpenTripApplyConstant.CarApply.START_CITY);
        String startCityListStr = tripOptional
            .map(IntranetApplyCarDTO.Trip::getStartCityNameList)
            .map(cityList-> String.join(",", cityList))
            .orElse(null);
        startCityVO.setValue(startCityListStr);
        formComponentValueVoList.add(startCityVO);
        //用车日期
        OapiProcessinstanceCreateRequest.FormComponentValueVo timeRangeVO = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
        String timeRange = tripOptional.map(this::getTimeRange).orElse(null);
        timeRangeVO.setName(OpenTripApplyConstant.CarApply.TIME_RANGE);
        timeRangeVO.setValue(timeRange);
        formComponentValueVoList.add(timeRangeVO);
        //用车次数
        OapiProcessinstanceCreateRequest.FormComponentValueVo personCountVO = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
        String personCount = buildPersonCount(tripOptional);
        personCountVO.setName(OpenTripApplyConstant.CarApply.PERSON_COUNT);
        personCountVO.setValue(personCount);
        formComponentValueVoList.add(personCountVO);
        //用车费用
        OapiProcessinstanceCreateRequest.FormComponentValueVo estimatedAmountVO = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
        String estimatedAmount = buildEstimatedAmount(tripOptional);
        estimatedAmountVO.setName(OpenTripApplyConstant.CarApply.ESTIMATED_AMOUNT);
        estimatedAmountVO.setValue(estimatedAmount);
        formComponentValueVoList.add(estimatedAmountVO);
        //费用归属部门
        OapiProcessinstanceCreateRequest.FormComponentValueVo costDeptVO = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
        String costDept = getCostAttribution(intranetApplyCarDTO.getCostAttributionList(), CostAttributionTypeEnum.ORG_UNIT.getKey());
        costDeptVO.setName(OpenTripApplyConstant.CarApply.COST_ATTRIBUTION_DEPT);
        costDeptVO.setValue(costDept);
        formComponentValueVoList.add(costDeptVO);
        //费用归属项目
        OapiProcessinstanceCreateRequest.FormComponentValueVo costProjectVO = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
        String costProject = getCostAttribution(intranetApplyCarDTO.getCostAttributionList(), CostAttributionTypeEnum.PROJECT.getKey());
        costProjectVO.setName(OpenTripApplyConstant.CarApply.COST_ATTRIBUTION_PROJECT);
        costProjectVO.setValue(costProject);
        formComponentValueVoList.add(costProjectVO);
        return formComponentValueVoList;
    }
    /**
     * 构建用车费用
     *
     * @param tripOptional 用车信息
     * @return 用车费用
     */
    private static String buildEstimatedAmount(Optional<IntranetApplyCarDTO.Trip> tripOptional) {
        Integer priceLimitFlag = tripOptional.map(IntranetApplyCarDTO.Trip::getPriceLimitFlag).orElse(null);
        if (priceLimitFlag == null) {
            return null;
        }
        String estimatedAmount = tripOptional.map(IntranetApplyCarDTO.Trip::getEstimatedAmount).map(BigDecimal::toString).orElse(null);
        if (Integer.valueOf(0).equals(priceLimitFlag)) {
            return "不限制";
        }
        // 1 限制 3 按城市级别 4 指定城市
        if (Integer.valueOf(1).equals(priceLimitFlag) || Integer.valueOf(2).compareTo(priceLimitFlag) < 0) {
            return "管理员已做限制";
        }
        if (Integer.valueOf(2).equals(priceLimitFlag)) {
            BigDecimal estimatedAmountBig = BigDecimalUtils.obj2big(estimatedAmount, null);
            return ObjectUtils.isEmpty(estimatedAmountBig) ? null : "¥" + estimatedAmountBig.setScale(2, RoundingMode.HALF_UP).toString();
        }
        return null;
    }

    private String buildTaxiInfo(IntranetApplyCarDTO intranetApplyCarDTO) {
        StringBuilder stringBuilder = new StringBuilder();
        if (ObjectUtil.isNull(intranetApplyCarDTO) && CollectionUtils.isBlank(intranetApplyCarDTO.getTripList())) {
            stringBuilder.append("无");
            return stringBuilder.toString();
        }
        intranetApplyCarDTO.getTripList().forEach(trip -> {
            stringBuilder.append(trip.getStartCityNameList());
            stringBuilder.append(DingtalkPunctuationConstant.LINE_FEED);
            stringBuilder.append(convertDate(trip.getStartTime()));
            stringBuilder.append(DingtalkPunctuationConstant.DASH);
            stringBuilder.append(convertDate(trip.getEndTime()));
            stringBuilder.append(DingtalkPunctuationConstant.LINE_FEED);
            // 次数限制时
            if (com.luastar.swift.base.utils.ObjUtils.toInteger(trip.getPersonCount()) > -1) {
                stringBuilder.append("限").append(trip.getPersonCount()).append("次").append("|");
            }
            // 金额限制类型 0-不限制 1-限制 2-员工填写
            if (trip.getPriceLimitFlag() == 1) {
                if (trip.getDayPriceLimit().compareTo(BigDecimal.valueOf(-1)) > 0) {
                    stringBuilder.append("单次").append(trip.getPriceLimit()).append("元");
                }
            }
            if (trip.getPriceLimitFlag() == 2) {
                if (trip.getEstimatedAmount().compareTo(BigDecimal.valueOf(-1)) > 0) {
                    stringBuilder.append("总额").append(trip.getEstimatedAmount()).append("元");
                }
            }
        });
        return stringBuilder.toString();
    }

    /**
     * 将2020-01-01或20200101转换为2020年1月1日
     *
     * @param sourceDateStr 源日期字符串
     * @return 目标日期字符串
     */
    private String convertDate(String sourceDateStr) {
        Date date = null;
        try {
            if (sourceDateStr.contains(DingtalkPunctuationConstant.DASH.trim())) {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(sourceDateStr);
            } else {
                date = new SimpleDateFormat("yyyyMMdd").parse(sourceDateStr);
            }
        } catch (ParseException e) {
            return null;
        }
        return new SimpleDateFormat("yyyy.MM.dd").format(date);
    }

    private String buildRuleInfo(IntranetApplyCarDTO carApplyDetailDTO) {
        StringBuilder stringBuilder = new StringBuilder();
        if (ObjectUtil.isNull(carApplyDetailDTO) && CollectionUtils.isBlank(carApplyDetailDTO.getTripList())) {
            stringBuilder.append("无");
            return stringBuilder.toString();
        }
        carApplyDetailDTO.getTripList().forEach(trip -> {
            trip.getRuleInfo().forEach(t -> {
                stringBuilder.append(t.getKey()).append("：").append(t.getValue());
                stringBuilder.append(DingtalkPunctuationConstant.LINE_FEED);
            });
        });
        return stringBuilder.toString();
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

    /**
     * 构建用车次数
     *
     * @param tripOptional 用车信息
     * @return 用车次数
     */
    private static String buildPersonCount(Optional<IntranetApplyCarDTO.Trip> tripOptional) {
        Integer timesLimitFlag = tripOptional.map(IntranetApplyCarDTO.Trip::getTimesLimitFlag).orElse(null);
        String personCount = tripOptional.map(IntranetApplyCarDTO.Trip::getPersonCount).orElse(null);
        if (Integer.valueOf(0).equals(timesLimitFlag)) {
            return "不限制";
        }
        if (Integer.valueOf(1).equals(timesLimitFlag)) {
            return "管理员已做限制";
        }
        if (Integer.valueOf(2).equals(timesLimitFlag)) {
            if (String.valueOf(-1).equals(personCount)) {
                return "不限制使用次数";
            } else {
                return personCount;
            }
        }
        return null;
    }
    private String getCostAttribution(List<IntranetApplyCarDTO.CostAttribution> costAttributionList, int costAttributionCategory) {
        if (CollectionUtils.isBlank(costAttributionList)) {
            return null;
        }
        return costAttributionList.stream()
            .filter(costAttribution -> Integer.valueOf(costAttributionCategory).equals(costAttribution.getCostAttributionCategory()))
            .findAny().map(IntranetApplyCarDTO.CostAttribution::getCostAttributionName).orElse(null);

    }
}

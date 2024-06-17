package com.fenbeitong.openapi.plugin.wechat.eia.listener;

import com.fenbeitong.openapi.plugin.support.citycode.dto.CityBaseInfo;
import com.fenbeitong.openapi.plugin.support.citycode.service.impl.CityCodeServiceImpl;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenTemplateConfigDao;
import com.fenbeitong.openapi.plugin.wechat.common.dto.CarApprovalInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.Map;

/**
 * <p>Title: DingTalkCommon</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-11-10 13:28
 */
@Slf4j
@Primary
public abstract class AbstractWeChatCommon {

    @Autowired
    CityCodeServiceImpl cityCodeService;

    @Autowired
    OpenTemplateConfigDao openTemplateConfigDao;

    @Autowired
    OpenSysConfigDao openSysConfigDao;


    public interface car {
        int HOTEL_AREA_TYPE_CITY = 2;
        String USE_CAR_CITY = "用车城市";
        String CAR_COUNT = "用车次数";
        String CAR_MONEY = "用车费用";
        String BEGIN_DATE = "开始日期";
        String END_DATE = "结束日期";
        String APPLY_REASON = "申请事由";
        String APPLY_USER_CAR = "审批用车";
        String KEY_START_CITY = "出发城市";
        String KEY_ARRIVAL_CITY = "目的城市";
    }


    /**
     * 创建审批用车规则信息
     *
     * @return
     */
    public List<CarApprovalInfo.CarApplyRule> buildUseCarRuleList() {
        return Lists.newArrayList(
                CarApprovalInfo.CarApplyRule.builder().type("taxi_scheduling_fee").value(-1).build(),
                CarApprovalInfo.CarApplyRule.builder().type("allow_same_city").value(false).build(),
                CarApprovalInfo.CarApplyRule.builder().type("allow_called_for_other").value(true).build(),
                CarApprovalInfo.CarApplyRule.builder().type("price_limit").value(-1).build(),
                CarApprovalInfo.CarApplyRule.builder().type("day_price_limit").value(-1).build(),
                CarApprovalInfo.CarApplyRule.builder().type("times_limit_flag").value(2).build()
        );
    }

    /**
     * 根据城市名称获取code
     *
     * @param stationName stationName
     * @return
     */
    public String getCityCodeByStationName(String stationName) {
        Map<String, CityBaseInfo> trainCodeMap = cityCodeService.getCarCode(Lists.newArrayList(stationName));
        CityBaseInfo cityBaseInfo = trainCodeMap.get(stationName);
        return cityBaseInfo == null ? null : cityBaseInfo.getId();
    }


}

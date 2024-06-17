package com.fenbeitong.openapi.plugin.yunzhijia.service.impl.apply;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;
import com.fenbeitong.openapi.plugin.support.apply.dao.OpenOrderApplyDao;
import com.fenbeitong.openapi.plugin.support.apply.dao.ThirdApplyDefinitionDao;
import com.fenbeitong.openapi.plugin.support.apply.entity.OpenOrderApply;
import com.fenbeitong.openapi.plugin.support.apply.entity.ThirdApplyDefinition;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskState;
import com.fenbeitong.openapi.plugin.util.BigDecimalUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.common.YunzhijiaResponseCode;
import com.fenbeitong.openapi.plugin.yunzhijia.constant.YunzhijiaResourceLevelConstant;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaAccessTokenReqDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaApplyRespDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.entity.YunzhijiaApply;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

@Slf4j
@ServiceAspect
@Service
public class YunzhijiaPushApplyService {
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

    public boolean pushApply(String object) throws ParseException {
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
        Map yunzhijiaApplyMap = Maps.newHashMap();
        yunzhijiaApplyMap.put("companyId", companyId);
        //4.组装云之家创建审批数据
        yunzhijiaApplyMap = commonApplyService.parseFbtOrderApplyDetail(map, yunzhijiaApplyMap);
        Map yunzhijiaApplyReqMap = Maps.newHashMap();
        //场景类型
        yunzhijiaApplyReqMap.put("_S_TITLE", map.get("order_person") + "的分贝通订单审批");
        yunzhijiaApplyReqMap.put("Te_0", yunzhijiaApplyMap.get("orderType"));
        yunzhijiaApplyReqMap.put("Te_1", yunzhijiaApplyMap.get("guestName").toString());
        yunzhijiaApplyReqMap.put("Te_2", yunzhijiaApplyMap.get("departureName"));
        yunzhijiaApplyReqMap.put("Te_3", yunzhijiaApplyMap.get("destinationName"));
        yunzhijiaApplyReqMap.put("Mo_0", yunzhijiaApplyMap.get("orderPrice") + "");
        long beginDate = (long) yunzhijiaApplyMap.get("beginDate");
        long endDate = (long) yunzhijiaApplyMap.get("endDate");
        List<Long> timeList = Lists.newArrayList(beginDate, endDate);
        yunzhijiaApplyReqMap.put("Dr_0", timeList);

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


}

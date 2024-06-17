package com.fenbeitong.openapi.plugin.wechat.eia.service.apply;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;
import com.fenbeitong.openapi.plugin.support.apply.constant.OpenMallApplyConstant;
import com.fenbeitong.openapi.plugin.support.apply.constant.OpenOrderApplyConstant;
import com.fenbeitong.openapi.plugin.support.apply.constant.OpenTaxiApplyConstant;
import com.fenbeitong.openapi.plugin.support.apply.constant.OpenTripApplyConstant;
import com.fenbeitong.openapi.plugin.support.apply.dao.OpenOrderApplyDao;
import com.fenbeitong.openapi.plugin.support.apply.dto.ApplyDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WeChatApprovalTemplateDetailRespDTO;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WeChatCreateApprovalReqDTO;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WeChatCreateApprovalRespDTO;
import com.fenbeitong.openapi.plugin.wechat.eia.dto.FenbeitongApproveDto;
import com.fenbeitong.openapi.plugin.wechat.eia.dto.FenbeitongOldApproveDto;
import com.fenbeitong.openapi.plugin.wechat.eia.service.wechat.PluginCallWeChatEiaService;
import com.fenbeitong.openapi.plugin.wechat.eia.service.wechat.WechatTokenService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.luastar.swift.base.utils.ObjUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

@Slf4j
@ServiceAspect
@Service
public class WeChatEiaPushApplyService {

    @Autowired
    OpenOrderApplyDao openOrderApplyDao;
    @Autowired
    CommonApplyServiceImpl commonApplyService;
    @Autowired
    PluginCallWeChatEiaService pluginCallWeChatEiaService;
    @Autowired
    WechatTokenService wechatTokenService;

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
        Map wechatApplyMap = Maps.newHashMap();
        wechatApplyMap.put("companyId", companyId);
        //4.组装企业微信创建审批数据
        wechatApplyMap = commonApplyService.parseFbtOrderApplyDetail(map, wechatApplyMap);
        //获取企业微信token
        String weChatToken = wechatTokenService.getWeChatApprovalToken(companyId);
        if (StringUtils.isBlank(weChatToken)) {
            log.info("获取企业微信token失败 {}", weChatToken);
            return false;
        }
        //查询模板详情
        WeChatApprovalTemplateDetailRespDTO weChatTemplateDetail = pluginCallWeChatEiaService.getWeChatTemplateDetail(weChatToken, thirdProcessCode);
        int errcode = weChatTemplateDetail.getErrcode();
        if (0 != errcode) {//查询模板失败
            log.info("获取企业微信审批模板失败 {}", weChatToken);
            return false;
        }
        List<WeChatApprovalTemplateDetailRespDTO.Control> controls = weChatTemplateDetail.getTemplateContent().getControls();
        List<WeChatCreateApprovalReqDTO.Content> contents = Lists.newArrayList();
        for (WeChatApprovalTemplateDetailRespDTO.Control control : controls) {//解析组件详情
            //组件ID
            String id = control.getProperty().getId();
            //具体字段名称 例：出发地
            WeChatApprovalTemplateDetailRespDTO.Text text = control.getProperty().getTitle().get(0);
            String text1 = text.getText();
            String control1 = control.getProperty().getControl();
            WeChatCreateApprovalReqDTO.Title title = null;
            if (text1.equals(OpenOrderApplyConstant.ORDER_PERSON)) {//下单人
                title = WeChatCreateApprovalReqDTO.Title.builder()
                        .text(map.get("order_person") + "")
                        .build();
            } else if (text1.equals(OpenOrderApplyConstant.ORDER_TYPE)) {//场景类型
                title = WeChatCreateApprovalReqDTO.Title.builder()
                        .text(wechatApplyMap.get("orderType") + "")
                        .build();
            } else if (text1.equals(OpenOrderApplyConstant.BEGIN_DATE)) {//开始日期
                Object beginDate = wechatApplyMap.get("beginDate");
                String s = ObjUtils.toString(beginDate);
                String begin = s.substring(0, 10);
                WeChatCreateApprovalReqDTO.Date day = WeChatCreateApprovalReqDTO.Date.builder().type("day").ssTimestamp(begin).build();
                title = WeChatCreateApprovalReqDTO.Title.builder()
                        .date(day)
                        .build();
            } else if (text1.equals(OpenOrderApplyConstant.END_DATE)) {//结束日期
                Object beginDate = wechatApplyMap.get("endDate");
                String s = ObjUtils.toString(beginDate);
                String end = s.substring(0, 10);
                WeChatCreateApprovalReqDTO.Date day1 = WeChatCreateApprovalReqDTO.Date.builder().type("day").ssTimestamp(end).build();
                title = WeChatCreateApprovalReqDTO.Title.builder()
                        .date(day1)
                        .build();
            } else if (text1.equals(OpenOrderApplyConstant.DEPARTURE_NAME)) {//出发地
                title = WeChatCreateApprovalReqDTO.Title.builder()
                        .text(wechatApplyMap.get("departureName") + "")
                        .build();
            } else if (text1.equals(OpenOrderApplyConstant.DESTINATION_NAME)) {//目的地
                title = WeChatCreateApprovalReqDTO.Title.builder()
                        .text(wechatApplyMap.get("destinationName") + "")
                        .build();
            } else if (text1.equals(OpenOrderApplyConstant.ORDER_PRICE)) {//订单金额
                title = WeChatCreateApprovalReqDTO.Title.builder()
                        .newMoney(wechatApplyMap.get("orderPrice") + "")
                        .build();
            } else if (text1.equals(OpenOrderApplyConstant.GUEST_NAME)) {//使用人
                title = WeChatCreateApprovalReqDTO.Title.builder()
                        .text(wechatApplyMap.get("guestName").toString())
                        .build();
            }
            WeChatCreateApprovalReqDTO.Content content = WeChatCreateApprovalReqDTO.Content.builder()
                    .id(id)
                    .control(control1)
                    .value(title).build();
            contents.add(content);
        }

        WeChatCreateApprovalReqDTO.ApplyData applyData = WeChatCreateApprovalReqDTO.ApplyData.builder()
                .contens(contents).build();
        List<WeChatCreateApprovalReqDTO.SummaryInfo> summaryInfoList = Lists.newArrayList();
        List<WeChatCreateApprovalReqDTO.Title> titles = Lists.newArrayList();
        WeChatCreateApprovalReqDTO.Title build = WeChatCreateApprovalReqDTO.Title.builder().text(map.get("order_person") + "提交的分贝通订单审批").lang("zh_CN").build();
        titles.add(build);
        WeChatCreateApprovalReqDTO.SummaryInfo summaryInfo = WeChatCreateApprovalReqDTO.SummaryInfo.builder()
                .summaryInfo(titles)
                .build();
        summaryInfoList.add(summaryInfo);
        WeChatCreateApprovalReqDTO reqDTO = WeChatCreateApprovalReqDTO.builder()
                .creatorUserid(thirdEmployeeId)
                .templateId(thirdProcessCode)
                .useTemplateApprover(1)
                .applyData(applyData)
                .summaryList(summaryInfoList)
                .build();
        //调用企业微信创建审批单接口
        WeChatCreateApprovalRespDTO wechatEiaApproval = pluginCallWeChatEiaService.createWechatEiaApproval(weChatToken, reqDTO);
        if (ObjectUtils.isEmpty(wechatEiaApproval) || wechatEiaApproval.getErrcode() != 0) {
            log.info("创建企业微信审批单失败");
            return false;
        }
        //存储企业微信审批单ID和分贝通审批单ID关系
        //企业微信审批单ID
        String spNo = wechatEiaApproval.getSpNo();
        //存储分贝通审批单ID和第三方审批单ID关系
        return commonApplyService.saveFbtOrderApply(companyId, thirdEmployeeId, applyId, spNo, OpenType.WECHAT_EIA.getType());
    }

    public boolean pushTripApply(String object) throws ParseException {
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
        ApplyDTO applyDTO = JsonUtils.toObj(object, ApplyDTO.class);
        if (ObjectUtils.isEmpty(applyDTO)
                || ObjectUtils.isEmpty(applyDTO.getTrip_list())) {
            throw new OpenApiPluginException(SupportRespCode.DATA_NOT_EXISTS);
        }
        commonApplyService.parseFbtTripApplyDetail(applyDTO);
        //获取企业微信token
        String weChatToken = wechatTokenService.getWeChatApprovalToken(applyDTO.getCompany_id());
        if (StringUtils.isBlank(weChatToken)) {
            log.info("获取企业微信token失败 {}", weChatToken);
            return false;
        }
        //查询模板详情
        WeChatApprovalTemplateDetailRespDTO weChatTemplateDetail = pluginCallWeChatEiaService.getWeChatTemplateDetail(weChatToken, thirdProcessCode);
        int errcode = weChatTemplateDetail.getErrcode();
        if (0 != errcode) {
            log.info("获取企业微信审批模板失败 {}", weChatToken);
            return false;
        }
        List<WeChatApprovalTemplateDetailRespDTO.Control> controls = weChatTemplateDetail.getTemplateContent().getControls();
        List<WeChatCreateApprovalReqDTO.Content> contents = Lists.newArrayList();
        for (WeChatApprovalTemplateDetailRespDTO.Control control : controls) {
            // 组件ID
            String id = control.getProperty().getId();
            WeChatApprovalTemplateDetailRespDTO.Text text = control.getProperty().getTitle().get(0);
            String text1 = text.getText();
            String control1 = control.getProperty().getControl();
            WeChatCreateApprovalReqDTO.Title title = null;
            if (text1.equals(OpenTripApplyConstant.APPLY_PERSON)) {
                title = WeChatCreateApprovalReqDTO.Title.builder()
                        .text(applyDTO.getApplyReasonStr())
                        .build();
            }
            if (text1.equals(OpenTripApplyConstant.TRIP_LIST)) {
                title = WeChatCreateApprovalReqDTO.Title.builder()
                        .text(applyDTO.getTripListStr())
                        .build();
            }
            if (text1.equals(OpenTripApplyConstant.GUEST_LIST)) {
                title = WeChatCreateApprovalReqDTO.Title.builder()
                        .text(applyDTO.getGuestListStr())
                        .build();
            }
            if (text1.equals(OpenTripApplyConstant.TRAVEL_TIME)) {
                title = WeChatCreateApprovalReqDTO.Title.builder()
                        .text(applyDTO.getTravelTimeStr())
                        .build();
            }
            if (text1.equals(OpenTripApplyConstant.WHERE_IS)) {
                title = WeChatCreateApprovalReqDTO.Title.builder()
                        .text(applyDTO.getCost_attribution_name())
                        .build();
            }
            WeChatCreateApprovalReqDTO.Content content = WeChatCreateApprovalReqDTO.Content.builder()
                    .id(id)
                    .control(control1)
                    .value(title).build();
            contents.add(content);
        }

        WeChatCreateApprovalReqDTO.ApplyData applyData = WeChatCreateApprovalReqDTO.ApplyData.builder()
                .contens(contents).build();
        List<WeChatCreateApprovalReqDTO.SummaryInfo> summaryInfoList = Lists.newArrayList();
        List<WeChatCreateApprovalReqDTO.Title> titles = Lists.newArrayList();
        WeChatCreateApprovalReqDTO.Title build = WeChatCreateApprovalReqDTO.Title.builder().text(map.get("employee_name") + "提交的分贝通差旅审批").lang("zh_CN").build();
        titles.add(build);
        WeChatCreateApprovalReqDTO.SummaryInfo summaryInfo = WeChatCreateApprovalReqDTO.SummaryInfo.builder()
                .summaryInfo(titles)
                .build();
        summaryInfoList.add(summaryInfo);
        WeChatCreateApprovalReqDTO reqDTO = WeChatCreateApprovalReqDTO.builder()
                .creatorUserid(thirdEmployeeId)
                .templateId(thirdProcessCode)
                .useTemplateApprover(1)
                .applyData(applyData)
                .summaryList(summaryInfoList)
                .build();
        // 调用企业微信创建审批单接口
        WeChatCreateApprovalRespDTO wechatEiaApproval = pluginCallWeChatEiaService.createWechatEiaApproval(weChatToken, reqDTO);
        if (ObjectUtils.isEmpty(wechatEiaApproval) || wechatEiaApproval.getErrcode() != 0) {
            log.info("创建企业微信审批单失败");
            return false;
        }
        //存储企业微信审批单ID和分贝通审批单ID关系
        //企业微信审批单ID
        String spNo = wechatEiaApproval.getSpNo();
        //存储分贝通审批单ID和第三方审批单ID关系
        return commonApplyService.saveFbtOrderApply(companyId, thirdEmployeeId, applyDTO.getApply_id(), spNo, OpenType.WECHAT_EIA.getType());
    }

    public boolean pushTaxiApply(String object) {
        if (StringUtils.isBlank(object)) {
            throw new OpenApiPluginException(SupportRespCode.DATA_NOT_EXISTS);
        }
        Map map = JsonUtils.toObj(object, Map.class);
        if (ObjectUtils.isEmpty(map)) {
            throw new OpenApiPluginException(SupportRespCode.DATA_NOT_EXISTS);
        }
        Map<String, String> map1 = commonApplyService.parseFbtTaxiApplyNotice(map);
        String companyId = map1.get("companyId");
        String thirdEmployeeId = map1.get("thirdEmployeeId");
        String thirdProcessCode = map1.get("thirdProcessCode");
        ApplyDTO applyDTO = JsonUtils.toObj(object, ApplyDTO.class);
        if (ObjectUtils.isEmpty(applyDTO)
                || ObjectUtils.isEmpty(applyDTO.getTrip_list())) {
            throw new OpenApiPluginException(SupportRespCode.DATA_NOT_EXISTS);
        }
        commonApplyService.parseFbtTaxiApplyDetail(applyDTO);
        //获取企业微信token
        String weChatToken = wechatTokenService.getWeChatApprovalToken(applyDTO.getCompany_id());
        if (StringUtils.isBlank(weChatToken)) {
            log.info("获取企业微信token失败 {}", weChatToken);
            return false;
        }
        //查询模板详情
        WeChatApprovalTemplateDetailRespDTO weChatTemplateDetail = pluginCallWeChatEiaService.getWeChatTemplateDetail(weChatToken, thirdProcessCode);
        int errcode = weChatTemplateDetail.getErrcode();
        if (0 != errcode) {
            log.info("获取企业微信审批模板失败 {}", weChatToken);
            return false;
        }
        List<WeChatApprovalTemplateDetailRespDTO.Control> controls = weChatTemplateDetail.getTemplateContent().getControls();
        List<WeChatCreateApprovalReqDTO.Content> contents = Lists.newArrayList();
        for (WeChatApprovalTemplateDetailRespDTO.Control control : controls) {
            // 组件ID
            String id = control.getProperty().getId();
            WeChatApprovalTemplateDetailRespDTO.Text text = control.getProperty().getTitle().get(0);
            String text1 = text.getText();
            String control1 = control.getProperty().getControl();
            WeChatCreateApprovalReqDTO.Title title = null;
            if (text1.equals(OpenTaxiApplyConstant.APPLY_PERSON)) {
                title = WeChatCreateApprovalReqDTO.Title.builder()
                        .text(applyDTO.getApplyReasonStr())
                        .build();
            }
            if (text1.equals(OpenTaxiApplyConstant.APPLY_TAXI_INFO)) {
                title = WeChatCreateApprovalReqDTO.Title.builder()
                        .text(applyDTO.getTaxiInfoStr())
                        .build();
            }
            if (text1.equals(OpenTaxiApplyConstant.RULE_INFO)) {
                title = WeChatCreateApprovalReqDTO.Title.builder()
                        .text(applyDTO.getRuleInfoStr())
                        .build();
            }
            WeChatCreateApprovalReqDTO.Content content = WeChatCreateApprovalReqDTO.Content.builder()
                    .id(id)
                    .control(control1)
                    .value(title).build();
            contents.add(content);
        }

        WeChatCreateApprovalReqDTO.ApplyData applyData = WeChatCreateApprovalReqDTO.ApplyData.builder()
                .contens(contents).build();
        List<WeChatCreateApprovalReqDTO.SummaryInfo> summaryInfoList = Lists.newArrayList();
        List<WeChatCreateApprovalReqDTO.Title> titles = Lists.newArrayList();
        WeChatCreateApprovalReqDTO.Title build = WeChatCreateApprovalReqDTO.Title.builder().text(map.get("employee_name") + "提交的分贝通用车审批").lang("zh_CN").build();
        titles.add(build);
        WeChatCreateApprovalReqDTO.SummaryInfo summaryInfo = WeChatCreateApprovalReqDTO.SummaryInfo.builder()
                .summaryInfo(titles)
                .build();
        summaryInfoList.add(summaryInfo);
        WeChatCreateApprovalReqDTO reqDTO = WeChatCreateApprovalReqDTO.builder()
                .creatorUserid(thirdEmployeeId)
                .templateId(thirdProcessCode)
                .useTemplateApprover(1)
                .applyData(applyData)
                .summaryList(summaryInfoList)
                .build();

        // 调用企业微信创建审批单接口
        WeChatCreateApprovalRespDTO wechatEiaApproval = pluginCallWeChatEiaService.createWechatEiaApproval(weChatToken, reqDTO);
        if (ObjectUtils.isEmpty(wechatEiaApproval) || wechatEiaApproval.getErrcode() != 0) {
            log.info("创建企业微信审批单失败");
            return false;
        }
        //存储企业微信审批单ID和分贝通审批单ID关系
        //企业微信审批单ID
        String spNo = wechatEiaApproval.getSpNo();
        //存储分贝通审批单ID和第三方审批单ID关系
        return commonApplyService.saveFbtOrderApply(companyId, thirdEmployeeId, applyDTO.getApply_id(), spNo, OpenType.WECHAT_EIA.getType());
    }

    public boolean pushOrderChangeApply(String object) {
        if (StringUtils.isBlank(object)) {
            throw new OpenApiPluginException(SupportRespCode.DATA_NOT_EXISTS);
        }
        Map map = JsonUtils.toObj(object, Map.class);
        if (ObjectUtils.isEmpty(map)) {
            throw new OpenApiPluginException(SupportRespCode.DATA_NOT_EXISTS);
        }
        Map<String, String> map1 = commonApplyService.parseFbtOrderChangeApplyNotice(map);
        String companyId = map1.get("companyId");
        String thirdEmployeeId = map1.get("thirdEmployeeId");
        String thirdProcessCode = map1.get("thirdProcessCode");
        ApplyDTO applyDTO = JsonUtils.toObj(object, ApplyDTO.class);
        if (ObjectUtils.isEmpty(applyDTO)) {
            throw new OpenApiPluginException(SupportRespCode.DATA_NOT_EXISTS);
        }
        commonApplyService.parseChangeOrderInfo(map);
        //获取企业微信token
        String weChatToken = wechatTokenService.getWeChatApprovalToken(applyDTO.getCompany_id());
        if (StringUtils.isBlank(weChatToken)) {
            log.info("获取企业微信token失败 {}", weChatToken);
            return false;
        }
        //查询模板详情
        WeChatApprovalTemplateDetailRespDTO weChatTemplateDetail = pluginCallWeChatEiaService.getWeChatTemplateDetail(weChatToken, thirdProcessCode);
        int errcode = weChatTemplateDetail.getErrcode();
        if (0 != errcode) {
            log.info("获取企业微信审批模板失败 {}", weChatToken);
            return false;
        }
        List<WeChatApprovalTemplateDetailRespDTO.Control> controls = weChatTemplateDetail.getTemplateContent().getControls();
        List<WeChatCreateApprovalReqDTO.Content> contents = Lists.newArrayList();
        for (WeChatApprovalTemplateDetailRespDTO.Control control : controls) {
            // 组件ID
            String id = control.getProperty().getId();
            WeChatApprovalTemplateDetailRespDTO.Text text = control.getProperty().getTitle().get(0);
            String text1 = text.getText();
            String control1 = control.getProperty().getControl();
            WeChatCreateApprovalReqDTO.Title title = null;
            if (text1.equals(OpenOrderApplyConstant.CHANGE_TYPE)) {
                title = WeChatCreateApprovalReqDTO.Title.builder()
                        .text(map.get("changeType").toString())
                        .build();
            }
            if (text1.equals(OpenOrderApplyConstant.CHANGE_REASON)) {
                String changeReason = map.get("changeReason").toString();
                if (map.get("changeDetail") != null) {
                    changeReason = changeReason + map.get("changeDetail");
                }
                title = WeChatCreateApprovalReqDTO.Title.builder()
                        .text(changeReason)
                        .build();
            }
            if (text1.equals(OpenOrderApplyConstant.OLD_ORDER_INFO)) {
                title = WeChatCreateApprovalReqDTO.Title.builder()
                        .text(map.get("oldOrderInfo").toString())
                        .build();
            }
            if (text1.equals(OpenOrderApplyConstant.ORDER_INFO)) {
                title = WeChatCreateApprovalReqDTO.Title.builder()
                        .text(map.get("orderInfo").toString())
                        .build();
            }
            if (text1.equals(OpenOrderApplyConstant.ORDER_CHANGE_FEE)) {
                title = WeChatCreateApprovalReqDTO.Title.builder()
                        .newMoney(map.get("orderChangeFee").toString())
                        .build();
            }
            WeChatCreateApprovalReqDTO.Content content = WeChatCreateApprovalReqDTO.Content.builder()
                    .id(id)
                    .control(control1)
                    .value(title).build();
            contents.add(content);
        }

        WeChatCreateApprovalReqDTO.ApplyData applyData = WeChatCreateApprovalReqDTO.ApplyData.builder()
                .contens(contents).build();
        List<WeChatCreateApprovalReqDTO.SummaryInfo> summaryInfoList = Lists.newArrayList();
        List<WeChatCreateApprovalReqDTO.Title> titles = Lists.newArrayList();
        WeChatCreateApprovalReqDTO.Title build = WeChatCreateApprovalReqDTO.Title.builder().text(map.get("employee_name") + "提交的分贝通订单改签审批").lang("zh_CN").build();
        titles.add(build);
        WeChatCreateApprovalReqDTO.SummaryInfo summaryInfo = WeChatCreateApprovalReqDTO.SummaryInfo.builder()
                .summaryInfo(titles)
                .build();
        summaryInfoList.add(summaryInfo);
        WeChatCreateApprovalReqDTO reqDTO = WeChatCreateApprovalReqDTO.builder()
                .creatorUserid(thirdEmployeeId)
                .templateId(thirdProcessCode)
                .useTemplateApprover(1)
                .applyData(applyData)
                .summaryList(summaryInfoList)
                .build();

        // 调用企业微信创建审批单接口
        WeChatCreateApprovalRespDTO wechatEiaApproval = pluginCallWeChatEiaService.createWechatEiaApproval(weChatToken, reqDTO);
        if (ObjectUtils.isEmpty(wechatEiaApproval) || wechatEiaApproval.getErrcode() != 0) {
            log.info("创建企业微信审批单失败");
            return false;
        }
        //存储企业微信审批单ID和分贝通审批单ID关系
        //企业微信审批单ID
        String spNo = wechatEiaApproval.getSpNo();
        //存储分贝通审批单ID和第三方审批单ID关系
        return commonApplyService.saveFbtOrderApply(companyId, thirdEmployeeId, applyDTO.getApply_id(), spNo, OpenType.WECHAT_EIA.getType());
    }

    public boolean pushOrderRefundApply(String object) {
        if (StringUtils.isBlank(object)) {
            throw new OpenApiPluginException(SupportRespCode.DATA_NOT_EXISTS);
        }
        Map map = JsonUtils.toObj(object, Map.class);
        if (ObjectUtils.isEmpty(map)) {
            throw new OpenApiPluginException(SupportRespCode.DATA_NOT_EXISTS);
        }
        Map<String, String> map1 = commonApplyService.parseFbtOrderRefundApplyNotice(map);
        String companyId = map1.get("companyId");
        String thirdEmployeeId = map1.get("thirdEmployeeId");
        String thirdProcessCode = map1.get("thirdProcessCode");
        ApplyDTO applyDTO = JsonUtils.toObj(object, ApplyDTO.class);
        if (ObjectUtils.isEmpty(applyDTO)) {
            throw new OpenApiPluginException(SupportRespCode.DATA_NOT_EXISTS);
        }
        commonApplyService.parseRefundOrderInfo(map);
        // 获取企业微信token
        String weChatToken = wechatTokenService.getWeChatApprovalToken(applyDTO.getCompany_id());
        if (StringUtils.isBlank(weChatToken)) {
            log.info("获取企业微信token失败 {}", weChatToken);
            return false;
        }
        // 查询模板详情
        WeChatApprovalTemplateDetailRespDTO weChatTemplateDetail = pluginCallWeChatEiaService.getWeChatTemplateDetail(weChatToken, thirdProcessCode);
        int errcode = weChatTemplateDetail.getErrcode();
        if (0 != errcode) {
            log.info("获取企业微信审批模板失败 {}", weChatToken);
            return false;
        }
        List<WeChatApprovalTemplateDetailRespDTO.Control> controls = weChatTemplateDetail.getTemplateContent().getControls();
        List<WeChatCreateApprovalReqDTO.Content> contents = Lists.newArrayList();
        for (WeChatApprovalTemplateDetailRespDTO.Control control : controls) {
            // 组件ID
            String id = control.getProperty().getId();
            WeChatApprovalTemplateDetailRespDTO.Text text = control.getProperty().getTitle().get(0);
            String text1 = text.getText();
            String control1 = control.getProperty().getControl();
            WeChatCreateApprovalReqDTO.Title title = null;
            if (text1.equals(OpenOrderApplyConstant.CHANGE_TYPE)) {
                title = WeChatCreateApprovalReqDTO.Title.builder()
                        .text(map.get("changeType").toString())
                        .build();
            }
            if (text1.equals(OpenOrderApplyConstant.REFUND_REASON)) {
                String refundReason = map.get("refundReason").toString();
                if (map.get("refundDetail") != null) {
                    refundReason = refundReason + map.get("refundDetail");
                }
                title = WeChatCreateApprovalReqDTO.Title.builder()
                        .text(refundReason)
                        .build();
            }
            if (text1.equals(OpenOrderApplyConstant.OLD_ORDER_INFO)) {
                title = WeChatCreateApprovalReqDTO.Title.builder()
                        .text(map.get("oldOrderInfo").toString())
                        .build();
            }
            WeChatCreateApprovalReqDTO.Content content = WeChatCreateApprovalReqDTO.Content.builder()
                    .id(id)
                    .control(control1)
                    .value(title).build();
            contents.add(content);
        }

        WeChatCreateApprovalReqDTO.ApplyData applyData = WeChatCreateApprovalReqDTO.ApplyData.builder()
                .contens(contents).build();
        List<WeChatCreateApprovalReqDTO.SummaryInfo> summaryInfoList = Lists.newArrayList();
        List<WeChatCreateApprovalReqDTO.Title> titles = Lists.newArrayList();
        WeChatCreateApprovalReqDTO.Title build = WeChatCreateApprovalReqDTO.Title.builder().text(map.get("employee_name") + "提交的分贝通订单退订审批").lang("zh_CN").build();
        titles.add(build);
        WeChatCreateApprovalReqDTO.SummaryInfo summaryInfo = WeChatCreateApprovalReqDTO.SummaryInfo.builder()
                .summaryInfo(titles)
                .build();
        summaryInfoList.add(summaryInfo);
        WeChatCreateApprovalReqDTO reqDTO = WeChatCreateApprovalReqDTO.builder()
                .creatorUserid(thirdEmployeeId)
                .templateId(thirdProcessCode)
                .useTemplateApprover(1)
                .applyData(applyData)
                .summaryList(summaryInfoList)
                .build();

        // 调用企业微信创建审批单接口
        WeChatCreateApprovalRespDTO wechatEiaApproval = pluginCallWeChatEiaService.createWechatEiaApproval(weChatToken, reqDTO);
        if (ObjectUtils.isEmpty(wechatEiaApproval) || wechatEiaApproval.getErrcode() != 0) {
            log.info("创建企业微信审批单失败");
            return false;
        }
        //存储企业微信审批单ID和分贝通审批单ID关系
        //企业微信审批单ID
        String spNo = wechatEiaApproval.getSpNo();
        //存储分贝通审批单ID和第三方审批单ID关系
        return commonApplyService.saveFbtOrderApply(companyId, thirdEmployeeId, applyDTO.getApply_id(), spNo, OpenType.WECHAT_EIA.getType());
    }

    /**
     * 推送采购审批
     * @param object 分贝通采购审批信息
     * @return true : 成功； false : 失败
     */
    public boolean pushMallApply(String object) {
        if (StringUtils.isBlank(object)) {
            throw new OpenApiPluginException(SupportRespCode.DATA_NOT_EXISTS);
        }
        Map map = JsonUtils.toObj(object, Map.class);
        if (ObjectUtils.isEmpty(map)) {
            throw new OpenApiPluginException(SupportRespCode.DATA_NOT_EXISTS);
        }
        FenbeitongOldApproveDto fenbeitongApproveDto = JsonUtils.toObj(object, FenbeitongOldApproveDto.class);
        if (ObjectUtils.isEmpty(fenbeitongApproveDto)) {
            throw new OpenApiPluginException(SupportRespCode.DATA_NOT_EXISTS);
        }
        Map<String, String> fbtMallNoticeInfomap = commonApplyService.parseFbtMallApplyNotice(map);
        String companyId = fbtMallNoticeInfomap.get("companyId");
        String thirdEmployeeId = fbtMallNoticeInfomap.get("thirdEmployeeId");
        String thirdProcessCode = fbtMallNoticeInfomap.get("thirdProcessCode");
        ApplyDTO applyDTO = JsonUtils.toObj(object, ApplyDTO.class);
        if (ObjectUtils.isEmpty(applyDTO)) {
            throw new OpenApiPluginException(SupportRespCode.DATA_NOT_EXISTS);
        }
        commonApplyService.parseFbtMallApplyDetail(applyDTO);
        // 获取企业微信token
        String weChatToken = wechatTokenService.getWeChatApprovalToken(applyDTO.getCompany_id());
        if (StringUtils.isBlank(weChatToken)) {
            log.info("获取企业微信token失败 {}", weChatToken);
            return false;
        }
        // 查询模板详情
        WeChatApprovalTemplateDetailRespDTO weChatTemplateDetail = pluginCallWeChatEiaService.getWeChatTemplateDetail(weChatToken, thirdProcessCode);
        int errcode = weChatTemplateDetail.getErrcode();
        if (0 != errcode) {
            log.info("获取企业微信审批模板失败 {}", weChatToken);
            return false;
        }
        List<WeChatApprovalTemplateDetailRespDTO.Control> controls = weChatTemplateDetail.getTemplateContent().getControls();
        List<WeChatCreateApprovalReqDTO.Content> contents = Lists.newArrayList();
        //采购信息
        StringBuilder mallInfo = new StringBuilder();
        String mallInfoStringValue = buildMallInfo(fenbeitongApproveDto,mallInfo);
        //控件信息
        contents = buildContent(controls,map,mallInfoStringValue,contents);
        //创建审批信息
        WeChatCreateApprovalReqDTO.ApplyData applyData = WeChatCreateApprovalReqDTO.ApplyData.builder()
                .contens(contents).build();
        List<WeChatCreateApprovalReqDTO.SummaryInfo> summaryInfoList = Lists.newArrayList();
        List<WeChatCreateApprovalReqDTO.Title> titles = Lists.newArrayList();
        WeChatCreateApprovalReqDTO.Title build = WeChatCreateApprovalReqDTO.Title.builder().text(map.get("apply_name") + "提交的分贝通采购审批").lang("zh_CN").build();
        titles.add(build);
        WeChatCreateApprovalReqDTO.SummaryInfo summaryInfo = WeChatCreateApprovalReqDTO.SummaryInfo.builder()
                .summaryInfo(titles)
                .build();
        summaryInfoList.add(summaryInfo);
        WeChatCreateApprovalReqDTO reqDTO = WeChatCreateApprovalReqDTO.builder()
                .creatorUserid(thirdEmployeeId)
                .templateId(thirdProcessCode)
                .useTemplateApprover(1)
                .applyData(applyData)
                .summaryList(summaryInfoList)
                .build();

        // 调用企业微信创建审批单接口
        WeChatCreateApprovalRespDTO wechatEiaApproval = pluginCallWeChatEiaService.createWechatEiaApproval(weChatToken, reqDTO);
        if (ObjectUtils.isEmpty(wechatEiaApproval) || wechatEiaApproval.getErrcode() != 0) {
            log.info("创建企业微信采购审批单失败");
            return false;
        }
        //存储企业微信审批单ID和分贝通审批单ID关系
        //企业微信审批单ID
        String spNo = wechatEiaApproval.getSpNo();
        //存储分贝通审批单ID和第三方审批单ID关系
        return commonApplyService.saveFbtOrderApply(companyId, thirdEmployeeId, applyDTO.getApply_id(), spNo, OpenType.WECHAT_EIA.getType());
    }

    public String buildMallInfo(FenbeitongOldApproveDto fenbeitongApproveDto,StringBuilder mallInfo){
        List<FenbeitongOldApproveDto.Trip> tripList = fenbeitongApproveDto.getTripList();
        if (!CollectionUtils.isEmpty(tripList)){
            tripList.stream().forEach(trip -> {
                List<FenbeitongOldApproveDto.Mall> mallList = trip.getMallList();
                if (CollectionUtils.isEmpty(mallList)){
                    return;
                }
                mallList.stream().forEach(mall -> {
                    String mallName = mall.getName();
                    if (mallName.contains(" ")){
                        String[] mallNames = mallName.split(" ");
                        for (String name : mallNames) {
                            mallInfo.append(name + "\n");
                        }
                    } else {
                        mallInfo.append(mall.getName() + "\n");
                    }
                    mallInfo.append("\n");
                });
            });
        }
        String result = mallInfo.toString();
        result = result.contains("\n") ? result.substring(0,result.lastIndexOf("\n")) : "";
        return result;
    }

    public List<WeChatCreateApprovalReqDTO.Content> buildContent(List<WeChatApprovalTemplateDetailRespDTO.Control> controls,Map map,String mallInfo,List<WeChatCreateApprovalReqDTO.Content> contents){
        for (WeChatApprovalTemplateDetailRespDTO.Control control : controls) {
            // 组件ID
            String id = control.getProperty().getId();
            WeChatApprovalTemplateDetailRespDTO.Text text = control.getProperty().getTitle().get(0);
            String text1 = text.getText();
            String control1 = control.getProperty().getControl();
            WeChatCreateApprovalReqDTO.Title title = null;
            //所在部门(自带了，不用填)
            //采购描述
            if (text1.equals(OpenMallApplyConstant.APPLY_DESC)) {
                String applyDesc = map.get("apply_desc").toString();
                title = WeChatCreateApprovalReqDTO.Title.builder()
                        .text(applyDesc)
                        .build();
            }
            //总金额
            if (text1.equals(OpenMallApplyConstant.APPLY_TOTAL_PRICE)) {
                String price = map.get("apply_total_price").toString();
                Double priceDouble = Double.valueOf(price);
                priceDouble = priceDouble/100;
                BigDecimal bd = new BigDecimal(priceDouble);
                BigDecimal result = bd.setScale(2,BigDecimal.ROUND_DOWN);
                title = WeChatCreateApprovalReqDTO.Title.builder()
                        .newMoney(result.toString())
                        .build();
            }
            //采购信息
            if (text1.equals(OpenMallApplyConstant.MALL_INFO)) {
                title = WeChatCreateApprovalReqDTO.Title.builder()
                        .text(mallInfo.toString())
                        .build();
            }
            WeChatCreateApprovalReqDTO.Content content = WeChatCreateApprovalReqDTO.Content.builder()
                    .id(id)
                    .control(control1)
                    .value(title).build();
            contents.add(content);
        }
        return contents;
    }
}

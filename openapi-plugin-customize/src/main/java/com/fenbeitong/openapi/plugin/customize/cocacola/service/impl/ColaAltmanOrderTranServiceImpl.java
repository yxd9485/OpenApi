package com.fenbeitong.openapi.plugin.customize.cocacola.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.noc.api.service.altman.model.vo.AltmanClassifyStereoInfoVO;
import com.fenbeitong.noc.api.service.altman.model.vo.AltmanOrderStereoInfoVO;
import com.fenbeitong.noc.api.service.altman.model.vo.AltmanPriceStereoInfoVO;
import com.fenbeitong.noc.api.service.altman.model.vo.AltmanStereoInfoVO;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.customize.cocacola.dto.ColaAltmanOrderDTO;
import com.fenbeitong.openapi.plugin.customize.cocacola.service.ColaAltmanOrderTranService;
import com.fenbeitong.openapi.plugin.func.order.dto.TrainOrderDetailReqDTO;
import com.fenbeitong.openapi.plugin.func.order.service.FuncTrainOrderServiceImpl;
import com.fenbeitong.openapi.plugin.support.apply.constant.ItemCodeEnum;
import com.fenbeitong.openapi.plugin.support.company.service.impl.UcCompanyServiceImpl;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.util.ExceptionRemind;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 可口可乐万能订单转换
 *
 * @author ctl
 * @date 2021/11/19
 */
@Service
@ServiceAspect
@Slf4j
public class ColaAltmanOrderTranServiceImpl implements ColaAltmanOrderTranService {

    @Autowired
    private FuncTrainOrderServiceImpl trainOrderService;

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;

    @Autowired
    private ExceptionRemind exceptionRemind;

    @Autowired
    private UcCompanyServiceImpl ucCompanyService;

    @Override
    public Map<String, Object> tran(String sourceStr) {
        if (StringUtils.isBlank(sourceStr)) {
            return null;
        }
        Map<String, Object> targetMap = new HashMap<>();
        targetMap.put(ItemCodeEnum.ALTMAN_ORDER_CUSTOM.getCode(), ItemCodeEnum.ALTMAN_ORDER_CUSTOM.getDesc());

        AltmanStereoInfoVO sourceObj = JsonUtils.toObj(sourceStr, AltmanStereoInfoVO.class);

        if (sourceObj != null) {
            AltmanOrderStereoInfoVO orderStereoInfoVO = sourceObj.getOrderStereoInfoVO();
            AltmanPriceStereoInfoVO priceStereoInfoVO = sourceObj.getPriceStereoInfoVO();
            AltmanClassifyStereoInfoVO classifyStereoInfoVO = sourceObj.getClassifyStereoInfoVO();
            Map<String, Object> extMap = new HashMap<>();
            if (orderStereoInfoVO != null) {
                String ext = orderStereoInfoVO.getExt();
                if (!StringUtils.isBlank(ext)) {
                    extMap = JsonUtils.toObj(ext, Map.class) != null ? JsonUtils.toObj(ext, new TypeReference<Map<String, Object>>() {
                    }) : new HashMap<>();
                }
            }

            ColaAltmanOrderDTO colaOrder = buildColaAltmanOrderDTO(orderStereoInfoVO, priceStereoInfoVO, classifyStereoInfoVO, extMap);
            String targetData = JsonUtils.toJson(colaOrder);
            log.info("可口可乐万能订单转换结果:{},", targetData);
            Map<String, Object> tranMap = JsonUtils.toObj(targetData, new TypeReference<Map<String, Object>>() {
            });
            if (!ObjectUtils.isEmpty(tranMap)) {
                targetMap.putAll(tranMap);
            }
        }

        return targetMap;
    }

    @Override
    public void push(ColaAltmanOrderDTO data, String companyId) {
        String companyName = "";
        try {
            if (data == null) {
                throw new OpenApiArgumentException("[data]不能为空");
            }
            if (StringUtils.isBlank(companyId)) {
                throw new OpenApiArgumentException("[companyId]公司id不能为空");
            }
            companyName = ucCompanyService.getCompanyName(companyId);
            List<OpenMsgSetup> openMsgSetups = openMsgSetupDao.listByCompanyIdAndItemCodeList(companyId, Lists.newArrayList(ItemCodeEnum.ALTMAN_ORDER_CUSTOM.getCode()));
            if (ObjectUtils.isEmpty(openMsgSetups)) {
                throw new OpenApiArgumentException("没有配置可乐万能订单开关");
            }
            String url = openMsgSetups.get(0) != null ? openMsgSetups.get(0).getStrVal2() : "";
            if (StringUtils.isBlank(url)) {
                throw new OpenApiArgumentException("没有配置可乐订单接口地址");
            }
            String result = RestHttpUtils.postJson(url, JsonUtils.toJson(data));
            log.info("可乐订单推送结果:{}", result);
            // 解析判断返回结果 如果是失败 抛出异常
            Map<String, Object> map = JsonUtils.toObj(result, new TypeReference<Map<String, Object>>() {
            });
            if (ObjectUtils.isEmpty(map)) {
                throw new FinhubException(9999, "推送结果解析失败，结果:" + result);
            }
            int code = NumericUtils.obj2int(map.get("code"));
            if (code != 1) {
                String msg = StringUtils.obj2str(map.get("msg"));
                throw new FinhubException(9999, "万能订单推送失败:" + msg + "");
            }
        } catch (Exception e) {
            String msg = String.format("公司id【%s】\n公司名称【%s】\n万能订单推送失败 \n原因【%s】", companyId, companyName, (StringUtils.isBlank(e.getMessage()) ? e.toString() : e.getMessage()));
            exceptionRemind.remindDingTalk(msg);
            throw new FinhubException(9999, "公司id:" + companyId + ",万能订单推送失败,原因:" + (StringUtils.isBlank(e.getMessage()) ? e.toString() : e.getMessage()));
        }
    }

    /**
     * 构建可乐万能订单实体
     *
     * @param orderStereoInfoVO
     * @param priceStereoInfoVO
     * @param classifyStereoInfoVO
     * @param extMap
     * @return
     */
    private ColaAltmanOrderDTO buildColaAltmanOrderDTO(AltmanOrderStereoInfoVO
                                                           orderStereoInfoVO, AltmanPriceStereoInfoVO priceStereoInfoVO, AltmanClassifyStereoInfoVO
                                                           classifyStereoInfoVO, Map<String, Object> extMap) {
        ColaAltmanOrderDTO colaOrder = new ColaAltmanOrderDTO();
        colaOrder.setCompanyName(orderStereoInfoVO != null ? orderStereoInfoVO.getCompanyName() : "");
        colaOrder.setCreateTime(orderStereoInfoVO != null ? DateUtils.toSimpleStr(orderStereoInfoVO.getCreateTime(), false) : "");
        colaOrder.setOrderChannelDesc((orderStereoInfoVO != null && orderStereoInfoVO.getOrderChannel() != null) ? orderStereoInfoVO.getOrderChannel().getValue() : "");
        colaOrder.setOrderStatusDesc((orderStereoInfoVO != null && orderStereoInfoVO.getOrderStatus() != null) ? orderStereoInfoVO.getOrderStatus().getValue() : "");
        colaOrder.setBizClassifyName(classifyStereoInfoVO != null ? classifyStereoInfoVO.getOrderTypeClassifyName() : "");
        colaOrder.setBizName(classifyStereoInfoVO != null ? classifyStereoInfoVO.getOrderTypeName() : "");
        colaOrder.setTotalPrice(priceStereoInfoVO != null ? priceStereoInfoVO.getTotalPrice() : BigDecimal.ZERO);
        colaOrder.setCostPrice(priceStereoInfoVO != null ? priceStereoInfoVO.getCostPrice() : BigDecimal.ZERO);
        colaOrder.setOrderTypeDesc("代打费");
        colaOrder.setMainOrderId((!ObjectUtils.isEmpty(extMap) && !ObjectUtils.isEmpty(extMap.get("bizExt1"))) ? extMap.get("bizExt1").toString() : "");
        colaOrder.setOrderId((!ObjectUtils.isEmpty(extMap) && !ObjectUtils.isEmpty(extMap.get("bizOrderId"))) ? extMap.get("bizOrderId").toString() : "");
        colaOrder.setTicketId((!ObjectUtils.isEmpty(extMap) && !ObjectUtils.isEmpty(extMap.get("bizExt2"))) ? extMap.get("bizExt2").toString() : "");
        colaOrder.setTicketNo((!ObjectUtils.isEmpty(extMap) && !ObjectUtils.isEmpty(extMap.get("bizTicketNo"))) ? extMap.get("bizTicketNo").toString() : "");
        colaOrder.setConsumerName((!ObjectUtils.isEmpty(extMap) && !ObjectUtils.isEmpty(extMap.get("bizConsumerName"))) ? extMap.get("bizConsumerName").toString() : "");
        colaOrder.setTravel((!ObjectUtils.isEmpty(extMap) && !ObjectUtils.isEmpty(extMap.get("bizTravel"))) ? extMap.get("bizTravel").toString() : "");
        colaOrder.setTrainNo((!ObjectUtils.isEmpty(extMap) && !ObjectUtils.isEmpty(extMap.get("bizNumber"))) ? extMap.get("bizNumber").toString() : "");
        colaOrder.setTravelStartTime((!ObjectUtils.isEmpty(extMap) && !ObjectUtils.isEmpty(extMap.get("bizTravelTime"))) ? extMap.get("bizTravelTime").toString() : "");
        colaOrder.setProxyFee((!ObjectUtils.isEmpty(extMap) && !ObjectUtils.isEmpty(extMap.get("bizFee"))) ? new BigDecimal(StringUtils.obj2str(extMap.get("bizFee"), "0")) : BigDecimal.ZERO);
        colaOrder.setProxyServiceFee((!ObjectUtils.isEmpty(extMap) && !ObjectUtils.isEmpty(extMap.get("bizServiceFee"))) ? new BigDecimal(StringUtils.obj2str(extMap.get("bizServiceFee"), "0")) : BigDecimal.ZERO);
        colaOrder.setCeId(getTargetCeId(colaOrder.getOrderId(), colaOrder.getTicketId(), orderStereoInfoVO != null ? orderStereoInfoVO.getCompanyId() : ""));
        colaOrder.setImportCeId((!ObjectUtils.isEmpty(extMap) && !ObjectUtils.isEmpty(extMap.get("bizExt3"))) ? extMap.get("bizExt3").toString() : "");
        return colaOrder;
    }

    /**
     * 获取三方审批id
     *
     * @param orderId
     * @param ticketId
     * @param companyId
     * @return
     */
    private String getTargetCeId(String orderId, String ticketId, String companyId) {
        String ceId = "";
        TrainOrderDetailReqDTO trainOrderDetailReqDTO = new TrainOrderDetailReqDTO();
        trainOrderDetailReqDTO.setOrderId(orderId);
        trainOrderDetailReqDTO.setTicketId(ticketId);
        trainOrderDetailReqDTO.setCompanyId(companyId);
        try {
            Map detail = (Map) trainOrderService.detail(trainOrderDetailReqDTO);
            if (!ObjectUtils.isEmpty(detail)) {
                Map thirdMap = (Map) detail.get("third_info");
                if (!ObjectUtils.isEmpty(thirdMap)) {
                    ceId = (String) thirdMap.get("apply_id");
                }
            }
        } catch (Exception e) {
            log.warn("获取火车订单详情失败:{}", e.toString());
        }
        return ceId;
    }
}

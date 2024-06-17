package com.fenbeitong.openapi.plugin.customize.power.service.impl;


import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.customize.power.constant.PowerConstant;
import com.fenbeitong.openapi.plugin.customize.power.dto.PowerOrderDto;
import com.fenbeitong.openapi.plugin.customize.power.dto.PowerCallBackDto;
import com.fenbeitong.openapi.plugin.customize.power.dto.PowerResponseDto;
import com.fenbeitong.openapi.plugin.customize.power.dto.RequestDataDto;
import com.fenbeitong.openapi.plugin.customize.power.service.PowerOrderCallBackService;
import com.fenbeitong.openapi.plugin.customize.yuanqishenlin.constant.OrderTypeEnum;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackType;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackConfDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackConf;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.BigDecimalUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 泛微订单回传
 *
 * @author zhangjindong
 * @Date 2022/1/04
 */
@Slf4j
@ServiceAspect
@Service
public class PowerOrderCallBackServiceImpl implements PowerOrderCallBackService {

    @Autowired
    private ThirdCallbackConfDao callbackConfDao;

    private final static Map<String, String> orderTypeMap = new HashMap<>();

    static {
        orderTypeMap.put(String.valueOf(OrderTypeEnum.OPEN_ORDER_TYPE_INTL_FLIGHT.getTypeId()), PowerConstant.FW_DOMESTIC_AIR_TYPE);
        orderTypeMap.put(String.valueOf(OrderTypeEnum.OPEN_ORDER_TYPE_NATIONAL_FLIGHT.getTypeId()), PowerConstant.FW_INTERNATIONAL_AIR_TYPE);
        orderTypeMap.put(PowerConstant.FBT_CAR_TYPE, PowerConstant.FW_CAR_TYPE);
        orderTypeMap.put(String.valueOf(OrderTypeEnum.OPEN_ORDER_TYPE_HOTEL.getTypeId()), PowerConstant.FW_HOTEL_TYPE);
        orderTypeMap.put(String.valueOf(OrderTypeEnum.OPEN_ORDER_TYPE_TRAIN.getTypeId()), PowerConstant.FW_TRAIN_TYPE);
    }


    @Override
    public Object callBackOrderData(String callbackData, String companyId) {

        PowerOrderDto orderMap = JsonUtils.toObj(callbackData, PowerOrderDto.class);
        if (ObjectUtils.isEmpty(orderMap)) {
            return PowerConstant.ORDER_PARAM_EMPTY;
        }
        Map<String, Object> orderInfo = orderMap.getOrderInfo();
        Map<String, Object> thirdInfo = orderMap.getThirdInfo();
        Map<String, Object> priceInfo = orderMap.getPriceInfo();
        Object orderType = orderInfo.get(PowerConstant.ORDER_CATEGORY_TYPE);
        Object status = orderInfo.get(PowerConstant.ORDER_STATUS);

        if (ObjectUtils.isEmpty(priceInfo) || ObjectUtils.isEmpty(thirdInfo) || ObjectUtils.isEmpty(orderInfo)
            || ObjectUtils.isEmpty(orderType) || ObjectUtils.isEmpty(status)) {
            return PowerConstant.ORDER_PARAM_EMPTY;
        }

        BigDecimal amount;
        BigDecimal taxAmount;
        Integer orderCategoryType = Integer.parseInt(String.valueOf(orderType));
        Integer orderStatus = Integer.parseInt(String.valueOf(status));

        BigDecimal trainAirNum1 = BigDecimalUtils.obj2big(PowerConstant.AIR_TRAIN_NUM1);
        BigDecimal trainAirNum2 = BigDecimalUtils.obj2big(PowerConstant.AIR_TRAIN_NUM2);

        // 计算含税金额
        if (PowerConstant.CAR_TYPE != orderCategoryType) {
            amount = BigDecimalUtils.obj2big(priceInfo.get(PowerConstant.COMPANY_TOTAL_PAY));
        } else {
            amount = BigDecimalUtils.obj2big(priceInfo.get(PowerConstant.TOTAL_PRICE)).subtract(BigDecimalUtils.obj2big(priceInfo.get(PowerConstant.PERSONAL_TOTAL_PAY)));
        }

        // 计算税额
        if (PowerConstant.DOMESTIC_AIR_TYPE == orderCategoryType || PowerConstant.INTERNATIONAL_AIR_TYPE == orderCategoryType) {
            if (PowerConstant.ARI_CHANGE_SUCCESS1.equals(orderStatus) || PowerConstant.ARI_CHANGE_SUCCESS2.equals(orderStatus)) {
                taxAmount = BigDecimalUtils.obj2big(priceInfo.get(PowerConstant.CHANGE_UPGRADE_PRICE)).divide(trainAirNum1, 2, RoundingMode.HALF_UP).multiply(trainAirNum2).setScale(2,RoundingMode.HALF_UP);
            } else {
                taxAmount = BigDecimalUtils.obj2big(priceInfo.get(PowerConstant.TICKET_PRICE)).divide(trainAirNum1, 2, RoundingMode.HALF_UP).multiply(trainAirNum2).setScale(2,RoundingMode.HALF_UP);
            }
        } else if (PowerConstant.TRAIN_TYPE == orderCategoryType) {
            // 改签成功
            if (PowerConstant.TRAIN_CHANGE_SUCCESS.equals(orderStatus)) {
                taxAmount = BigDecimalUtils.obj2big(priceInfo.get(PowerConstant.ENDORSE_DIFF_RATE)).divide(trainAirNum1, 2, RoundingMode.HALF_UP).multiply(trainAirNum2).setScale(2,RoundingMode.HALF_UP);
                amount = BigDecimalUtils.obj2big(priceInfo.get(PowerConstant.ENDORSE_DIFF_RATE)).add((BigDecimalUtils.obj2big(priceInfo.get(PowerConstant.ENDORSE_PRICE))));
            } else {
                taxAmount = BigDecimalUtils.obj2big(priceInfo.get(PowerConstant.PAR_PRICE)).divide(trainAirNum1, 2, RoundingMode.HALF_UP).multiply(trainAirNum2).setScale(2,RoundingMode.HALF_UP);
            }
        } else if (PowerConstant.HOTEL_TYPE == orderCategoryType) {
            taxAmount = BigDecimalUtils.obj2big(priceInfo.get(PowerConstant.COMPANY_TOTAL_PAY)).divide(new BigDecimal(PowerConstant.HOTEL_NUM1), 2,
                RoundingMode.HALF_UP).multiply(new BigDecimal(PowerConstant.HOTEL_NUM2)).setScale(2,RoundingMode.HALF_UP);
        } else {
            taxAmount = new BigDecimal(0);
        }

        PowerCallBackDto powerCallBackDto = new PowerCallBackDto();
        powerCallBackDto.setTaxAmount(taxAmount);

        powerCallBackDto.setCostType(orderTypeMap.get(String.valueOf(orderCategoryType)));
        String createTime = (String) orderInfo.get(PowerConstant.CREATE_TIME);
        if (!StringUtils.isBlank(createTime) && createTime.length() == PowerConstant.ARRAY_LENGTH) {
            String date = createTime.substring(0, 10);
            String time = createTime.substring(11, 16);
            powerCallBackDto.setHappenDate(date);
            powerCallBackDto.setCreateTime(time);
        }

        powerCallBackDto.setAmount(amount);
        powerCallBackDto.setType(1);
        powerCallBackDto.setMark((String) thirdInfo.get(PowerConstant.APPLY_ID));
        powerCallBackDto.setFBTOrderId((String) orderInfo.get(PowerConstant.ORDER_ID));
        powerCallBackDto.setModedatacreatedate(new SimpleDateFormat(PowerConstant.YYYY_MM_DD).format(new Date()));
        powerCallBackDto.setModedatacreatetime(new SimpleDateFormat(PowerConstant.HH_MM_SS).format(new Date()));

        List<String> dataList = new ArrayList<>();
        dataList.add(JsonUtils.toJson(powerCallBackDto));
        RequestDataDto requestDataDto = new RequestDataDto();
        requestDataDto.setDatas(dataList);
        MultiValueMap multiValueMap = new LinkedMultiValueMap();
        multiValueMap.add(PowerConstant.PARAM, JsonUtils.toJson(requestDataDto));


        ThirdCallbackConf thirdCallbackConf = companyId == null ? null : callbackConfDao.queryByCompanyIdAndCallBackType(companyId, CallbackType.ORDER.getType());
        if (!ObjectUtils.isEmpty(thirdCallbackConf) && !StringUtils.isBlank(thirdCallbackConf.getJsonParam())) {
            String url = (String) MapUtils.getValueByExpress(JsonUtils.toObj(thirdCallbackConf.getJsonParam(), Map.class), "url");
            if (!StringUtils.isBlank(url)) {
                String result = RestHttpUtils.postFormUrlEncode(url, null, multiValueMap);
                PowerResponseDto responseDto = JsonUtils.toObj(result, PowerResponseDto.class);
                assert responseDto != null;
                if (PowerConstant.RESPONSE_SUCCESS_CODE == responseDto.getCode() && !ObjectUtils.isEmpty(responseDto.getData())) {
                    return OpenapiResponseUtils.success(result);
                } else {
                    return OpenapiResponseUtils.error(-1, PowerConstant.ERROR_RESPONSE);
                }

            }
        }
        return OpenapiResponseUtils.error(-1, PowerConstant.ERROR_RESPONSE);
    }

}





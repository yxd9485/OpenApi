package com.fenbeitong.openapi.plugin.customize.yixin.service.impl;

import com.fenbeitong.openapi.plugin.customize.yixin.service.YiXinOrderService;
import com.fenbeitong.openapi.plugin.support.apply.constant.OrderSceneType;
import com.fenbeitong.openapi.plugin.support.apply.dto.*;
import com.fenbeitong.openapi.plugin.support.apply.service.SceneServiceImpl;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackType;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackConfDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackConf;
import com.fenbeitong.openapi.plugin.support.callback.service.IBusinessDataPushService;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 宜信订单服务
 * @Auther zhang.peng
 * @Date 2021/12/29
 */
@Service
@Slf4j
@ServiceAspect
public class YiXinOrderServiceImpl implements YiXinOrderService {

    @Autowired
    private SceneServiceImpl sceneService;

    @Value("${host.appgate}")
    private String appgateHost;

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private ThirdCallbackConfDao callbackConfDao;

    @Autowired
    private IBusinessDataPushService businessDataPushService;

    private static final String RETURN_CODE = "0";
    private static final Integer RETURN_CODE_INT = 0;
    private static final String CUSTOM_RETURN_KEY = "key";
    private static final String CUSTOM_RETURN_VALUE = "value";

    @Override
    public Object buildCostAndThirdApplyInfo(OrderApplyDTO apply){
        //订单费用归属
        buildCostInfo(apply);
        //三方审批单和分贝通审批单
        buildThirdApplyInfo(apply.getCompanyId(),apply.getOrderId(),apply,apply.getApplyType());
        log.info(" 通用订单数据 : {}",JsonUtils.toJson(apply));
        String companyId = apply.getCompanyId();
        ThirdCallbackConf thirdCallbackConf = companyId == null ? null : callbackConfDao.queryByCompanyIdAndCallBackType(companyId, CallbackType.APPLY_ORDER_REVERSE_PUSH.getType());
        if ( thirdCallbackConf == null ) {
            log.info("未配置三方接口地址");
            return null;
        }
        String result = RestHttpUtils.postJson(thirdCallbackConf.getCallbackUrl(),JsonUtils.toJson(apply));
        if ( StringUtils.isBlank(result) ){
            log.info("三方接口返回为空");
            return null;
        }
        // 配置的三方返回结果
        String customResult = thirdCallbackConf.getJsonParam();
        return result;
    }

    public void buildCostInfo( OrderApplyDTO orderApplyDTO ){
        List<OrderCostDetailDTO> costInfoResults = sceneService.getOrderCostList(orderApplyDTO.getOrderId());
        if ( CollectionUtils.isEmpty(costInfoResults) || null == costInfoResults.get(0) ){
            return;
        }
        String costCategory = costInfoResults.get(0).getCostCategory();
        List<OrderCostDetailDTO.CostAttributionGroup> costAttributionGroupList = costInfoResults.get(0).getCostAttributionGroupList();
        if ( CollectionUtils.isEmpty(costAttributionGroupList) ){
            return;
        }
        List<OrderApplyDTO.SaasInfo> saasInfoList = new ArrayList<>();
        costAttributionGroupList.stream().forEach(costAttributionGroup -> {
            List<OrderCostDetailDTO.CostAttribution> costAttributionList = costAttributionGroup.getCostAttributionList();
            if (CollectionUtils.isEmpty(costAttributionList)){
                return;
            }
            costAttributionList.stream().forEach(costAttribution->{
                OrderApplyDTO.SaasInfo saasInfo = new OrderApplyDTO.SaasInfo();
                saasInfo.setCostAttributionName(costAttribution.getName());
                saasInfo.setCostAttributionId(costAttribution.getId());
                saasInfo.setCostAttributionCategory(costCategory);
                saasInfo.setCategory(costAttributionGroup.getCategory());
                saasInfoList.add(saasInfo);
            });
        });
        orderApplyDTO.setSaasInfoList(saasInfoList);
    }

    public void buildThirdApplyInfo(String companyId, String orderId, OrderApplyDTO orderApplyDTO, int applyType){
        if (applyType == OrderSceneType.HOTEL.getType()){
            // 酒店
            // 授权负责人
            String token = userCenterService.getUcSuperAdminToken(companyId);
            HotelOrderDetailWithApplyInfoDTO detailWithApplyInfoDTO = sceneService.getHotelOrderDetailInfoWithTripApplyInfo(token,orderId);
            // 酒店详情
            if ( null != detailWithApplyInfoDTO && !CollectionUtils.isEmpty(detailWithApplyInfoDTO.getApplyInfo()) && null != detailWithApplyInfoDTO.getApplyInfo().get(0) ){
                HotelOrderDetailWithApplyInfoDTO.ListApplyInfo listApplyInfo = detailWithApplyInfoDTO.getApplyInfo().get(0);
                HotelOrderDetailWithApplyInfoDTO.InnerApplyInfo innerApplyInfo = listApplyInfo.getApplyInfo();
                // 分贝通差旅申请id
                String applyId = innerApplyInfo.getApplyId();
                orderApplyDTO.setTripApplyId(applyId);
                // 查三方行程
                if (!StringUtils.isBlank(applyId)){
                    SaasApplyCustomFieldRespDTO applyInfo = getApplyCustomFields(companyId, applyId);
                    if ( null != applyInfo && null != applyInfo.getData() ){
                        orderApplyDTO.setThirdTripApplyId(applyInfo.getData().getThirdApplyId());
                    }
                }
            }
        } else {
            // 机票和火车
            List<String> orderIds = new ArrayList<>();
            orderIds.add(orderId);
            List<OrderDetailDTO> orderDetailDTOList = sceneService.getOrderDetailDTOList(orderIds);
            if (CollectionUtils.isEmpty(orderDetailDTOList)){
                try {
                    // 最多 10 秒
                    for ( int i = 0 ; i < 2 ; i++){
                        Thread.sleep(5000L);
                        //延迟查
                        orderDetailDTOList = sceneService.getOrderDetailDTOList(orderIds);
                        if (!CollectionUtils.isEmpty(orderDetailDTOList)){
                            break;
                        }
                    }
                } catch (Exception e){
                    log.info("线程睡眠异常 {}",e.getMessage());
                }
                return;
            }
            OrderDetailDTO orderDetailDTO = orderDetailDTOList.get(0);
            if ( null == orderDetailDTO ){
                return;
            }
            String applyId = orderDetailDTO.getApplyId();
            SaasApplyCustomFieldRespDTO applyInfo = getApplyCustomFields(companyId, applyId);
            if ( null != applyInfo && null != applyInfo.getData() ){
                orderApplyDTO.setThirdTripApplyId(applyInfo.getData().getThirdApplyId());
                orderApplyDTO.setTripApplyId(applyId);
            }
        }
    }

    public SaasApplyCustomFieldRespDTO getApplyCustomFields(String companyId, String applyId) {
        String url = appgateHost + String.format("/saas/apply/third/query_custom_fields?company_id=%s&apply_id=%s", companyId, applyId);
        String result = RestHttpUtils.get(url, Maps.newHashMap());
        return JsonUtils.toObj(result, SaasApplyCustomFieldRespDTO.class);
    }

    @Override
    public Object buildExceedApplyInfo(Map<String,Object> apply){
        // 超规信息
        String companyId = Optional.ofNullable(apply.get("company_id")).orElse("").toString() ;
        String orderId = Optional.ofNullable(apply.get("order_id")).orElse("").toString() ;
        int type = Integer.parseInt(Optional.ofNullable(apply.get("type")).orElse(0).toString());
        buildExceedApplyInfo(companyId,orderId,apply,type);
        log.info(" 通用退改订单数据 : {}",JsonUtils.toJson(apply));
        ThirdCallbackConf thirdCallbackConf = companyId == null ? null : callbackConfDao.queryByCompanyIdAndCallBackType(companyId, CallbackType.CHANGE_REFUND_APPLY_REVERSE_PUSH.getType());
        if ( thirdCallbackConf == null ) {
            log.info("未配置三方接口地址");
            return null;
        }
        String result = RestHttpUtils.postJson(thirdCallbackConf.getCallbackUrl(),JsonUtils.toJson(apply));
        if ( StringUtils.isBlank(result) ){
            log.info("三方接口返回为空");
            return null;
        }
        // 配置的三方返回结果
        String customResult = thirdCallbackConf.getJsonParam();
        return result;
    }

    public void buildExceedApplyInfo(String companyId, String orderId, Map<String,Object> orderApplyDTO, int applyType){
        if (applyType == OrderSceneType.HOTEL.getType()){
            // 酒店
            // 授权负责人
            String token = userCenterService.getUcSuperAdminToken(companyId);
            HotelOrderDetailWithApplyInfoDTO detailWithApplyInfoDTO = sceneService.getHotelOrderDetailInfoWithTripApplyInfo(token,orderId);
            // 酒店详情
            if ( null != detailWithApplyInfoDTO && !CollectionUtils.isEmpty(detailWithApplyInfoDTO.getApplyInfo()) && null != detailWithApplyInfoDTO.getApplyInfo().get(0) ){
                HotelOrderDetailWithApplyInfoDTO.ListApplyInfo listApplyInfo = detailWithApplyInfoDTO.getApplyInfo().get(0);
                HotelOrderDetailWithApplyInfoDTO.ApplyCenterInfo applyCenterInfo = listApplyInfo.getApplyCenterInfo();
                // 超规申请id
                String applyId = applyCenterInfo.getApplyId();
                buildExceedInfo(orderApplyDTO,applyId,orderId,companyId);
            }
        } else {
            // 机票和火车
            List<String> orderIds = new ArrayList<>();
            orderIds.add(orderId);
            List<OrderDetailDTO> orderDetailDTOList = sceneService.getOrderDetailDTOList(orderIds);
            if (CollectionUtils.isEmpty(orderDetailDTOList)){
                try {
                    // 最多 10 秒
                    for ( int i = 0 ; i < 2 ; i++){
                        Thread.sleep(5000L);
                        //延迟查
                        orderDetailDTOList = sceneService.getOrderDetailDTOList(orderIds);
                        if (!CollectionUtils.isEmpty(orderDetailDTOList)){
                            break;
                        }
                    }
                } catch (Exception e){
                    log.info("线程睡眠异常 {}",e.getMessage());
                }
                return;
            }
            OrderDetailDTO orderDetailDTO = orderDetailDTOList.get(0);
            if ( null == orderDetailDTO ){
                return;
            }
            // 超规审批id
            String applyId = orderDetailDTO.getDuringApplyId();
            buildExceedInfo(orderApplyDTO,applyId,orderId,companyId);
        }
    }

    public void buildExceedInfo( Map<String,Object> orderApplyDTO , String applyId , String orderId , String companyId ){
        // 超规审批id
        if ( StringUtils.isBlank(applyId) ){
            orderApplyDTO.put("is_exceed",false);
        } else {
            OrderApplyDetailDTO applyInfo = sceneService.getApplyDetailInfo(orderId,companyId,applyId);
            orderApplyDTO.put("is_exceed",true);
            if ( null != applyInfo ) {
                String applyReasonDesc = applyInfo.getApplyReasonDesc();
                applyReasonDesc = StringUtils.isBlank(applyReasonDesc) ? "" : (";" + applyReasonDesc ) ;
                orderApplyDTO.put("exceed_reason",applyInfo.getApplyReason() + applyReasonDesc);
            }
        }
    }

    /**
     * 兼容多种返回结果
     * @param customResult 配置的自定义返回结果
     * @param thirdResult 三方返回结果
     * @return true 客户接口调用成功 ; false 客户接口调用失败
     */
    public boolean buildReturnResult(String customResult , String thirdResult){
        Map resultMap = JsonUtils.toObj(thirdResult,Map.class);
        if ( null == resultMap ){
            log.info("返回结果不是 json , 特殊处理");
            return thirdResult.equals(customResult);
        }
        Object returnCode = resultMap.get("code");
        // 不配置自定义结果 , 默认客户返回 code=0 这样的结构 : 取 code 值然后比较
        if (StringUtils.isBlank(customResult)){
            if ( returnCode instanceof String ){
                return RETURN_CODE.equals(returnCode);
            }
            if ( returnCode instanceof Integer ){
                return RETURN_CODE_INT.equals(returnCode);
            }
            return false;
        } else {
            // 配置自定义结果 , 取自定义信息
            Map<String,String> customReturnInfo = JsonUtils.toObj(customResult,Map.class);
            if ( null == customReturnInfo ){
                // 客户返回的 code 不是 0 , 取自定义的返回结果
                return returnCode.equals(customResult);
            } else {
                // 如果客户的返回结果不是状态码 , 单独设置返回结果
                String key = customReturnInfo.get(CUSTOM_RETURN_KEY);
                String value = customReturnInfo.get(CUSTOM_RETURN_VALUE);
                return value.equals(resultMap.get(key));
            }
        }
    }
}

package com.fenbeitong.openapi.plugin.feishu.isv.service;

import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.util.RedisDistributionLock;
import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResponseCode;
import com.fenbeitong.openapi.plugin.feishu.common.constant.FeiShuConstant;
import com.fenbeitong.openapi.plugin.feishu.common.exception.OpenApiFeiShuException;
import com.fenbeitong.openapi.plugin.feishu.isv.constant.FeiShuIsvOrderPricePlanType;
import com.fenbeitong.openapi.plugin.feishu.isv.dto.FeiShuIsvCallbackOrderPaidDTO;
import com.fenbeitong.openapi.plugin.feishu.isv.dto.FeiShuIsvGetOrderRespDTO;
import com.fenbeitong.openapi.plugin.feishu.isv.entity.FeishuIsvCompany;
import com.fenbeitong.openapi.plugin.feishu.isv.util.FeiShuIsvHttpUtils;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.order.constant.OpenThirdMarketOrderProcessStatus;
import com.fenbeitong.openapi.plugin.support.order.entity.OpenThirdMarketOrder;
import com.fenbeitong.openapi.plugin.support.order.service.IOpenThirdMarketOrderService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.usercenter.api.model.dto.contracts.ContractInfoDTO;
import com.fenbeitong.usercenter.api.model.dto.contracts.ContractVo;
import com.fenbeitong.usercenter.api.service.company.ICompanyNewInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 飞书订单
 *
 * @author lizhen
 * @date 2020/10/22
 */
@ServiceAspect
@Service
@Slf4j
public class FeiShuIsvOrderService {

    @Value("${feishu.api-host}")
    private String feishuHost;

    @Autowired
    private FeiShuIsvHttpUtils feiShuIsvHttpUtils;


    @DubboReference(check = false)
    private ICompanyNewInfoService iCompanyNewInfoService;

    @Autowired
    private FeiShuIsvCompanyDefinitionService feiShuIsvCompanyDefinitionService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private IOpenThirdMarketOrderService openThirdMarketOrderService;

    /**
     * 处理购买订单
     *
     * @param feiShuIsvCallbackOrderPaidDTO
     */
    public TaskResult processOrderPaid(FeiShuIsvCallbackOrderPaidDTO feiShuIsvCallbackOrderPaidDTO) {
        FeiShuIsvCallbackOrderPaidDTO.Event event = feiShuIsvCallbackOrderPaidDTO.getEvent();
        String orderId = event.getOrderId();
        String corpId = event.getTenantKey();
        String lockKey = MessageFormat.format(RedisKeyConstant.THIRD_MARKET_ORDER, orderId);
        Long lockTime = RedisDistributionLock.lockByTime(lockKey, redisTemplate, 30 * 60 * 1000L);
        OpenThirdMarketOrder openThirdMarketOrder = null;
        if (lockTime > 0) {
            try {
                FeishuIsvCompany feishuIsvCompany = feiShuIsvCompanyDefinitionService.getFeiShuIsvCompanyByCorpId(corpId);
                if (feishuIsvCompany == null) {
                    throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_ISV_COMPANY_UNDEFINED);
                }
                //1.获取飞书订单
                FeiShuIsvGetOrderRespDTO.Order feiShuOrder = getFeiShuOrder(orderId, corpId);
                String status = feiShuOrder.getStatus();
                if (!FeiShuConstant.ORDER_NORMAL_STATUS.equals(status)) {
                    log.info("feishu isv 订单状态不是正常，不处理");
                    return TaskResult.ABORT;
                }
                String pricePlanType = feiShuOrder.getPricePlanType();
                if (!FeiShuIsvOrderPricePlanType.PER_YEAR.getValue().equals(pricePlanType)) {
                    log.info("订单不是年付，不处理");
                    return TaskResult.ABORT;
                }
                //2.查看订单是否已处理，创建订单
                openThirdMarketOrder = openThirdMarketOrderService.getOpenThirdMarketOrder(orderId, corpId);
                if (openThirdMarketOrder != null) {
                    if (OpenThirdMarketOrderProcessStatus.SUCCESS.getStatus() == openThirdMarketOrder.getProcessStatus()) {
                        log.info("feishu isv 订单已处理");
                        return TaskResult.ABORT;
                    }
                } else {
                    openThirdMarketOrder = new OpenThirdMarketOrder();
                    openThirdMarketOrder.setCorpId(corpId);
                    openThirdMarketOrder.setOrderId(orderId);
                    openThirdMarketOrder.setOpenType(OpenType.FEISHU_ISV.getType());
                    openThirdMarketOrder.setProcessStatus(OpenThirdMarketOrderProcessStatus.INIT.getStatus());
                    openThirdMarketOrderService.save(openThirdMarketOrder);
                }
                //3.判断企业是否新订单
                List<OpenThirdMarketOrder> openThirdMarketOrders = openThirdMarketOrderService.listOpenThirdMarketOrderByCorpId(corpId);
                //4.更新企业合同

                updateContractInfo(feishuIsvCompany.getCompanyId(), feiShuOrder);
                //5.更新订单
                log.info("feishu isv 开始更新订单");
                openThirdMarketOrderService.success(openThirdMarketOrder);
                log.info("feishu isv 订单处理成功");
            } catch (Exception e) {
                log.error("feishu isv 处理购买订单失败, result: {}", e);
                openThirdMarketOrderService.failed(openThirdMarketOrder);
                throw e;
            } finally {
                RedisDistributionLock.unlock(lockKey, lockTime, redisTemplate);
            }
        }
        return TaskResult.SUCCESS;
    }

    /**
     * 更新合同
     *
     * @param companyId
     * @param feiShuOrder
     */
    public void updateContractInfo(String companyId, FeiShuIsvGetOrderRespDTO.Order feiShuOrder) {
        String pricePlanType = feiShuOrder.getPricePlanType();
        Integer buyCount = feiShuOrder.getBuyCount();
        String payTimeStr = feiShuOrder.getPayTime();
        Date payTime = DateUtils.toDate(NumericUtils.obj2long(payTimeStr) * 1000);
        ContractVo contractVo = iCompanyNewInfoService.queryContract(companyId);
        String endDateStr = contractVo.getEndDate();
        //Date date = DateUtils.toDate(endDateStr);
        String endDateStrNew = DateUtils.afterYear(payTime, buyCount);
        log.info("企业购买套餐更新到期时间，companyId={},pricePlanType={},buyCount={},原到期日期={},新到期日期={}", companyId, pricePlanType, buyCount, endDateStr, endDateStrNew);
        ContractInfoDTO contractInfoDTO = new ContractInfoDTO();
        contractInfoDTO.setCompanyId(companyId);
        contractInfoDTO.setEndDate(DateUtils.toDate(endDateStrNew));
        iCompanyNewInfoService.updateCompanyContractInfo(contractInfoDTO);
    }

    /**
     * 从飞书获取订单
     *
     * @param orderId
     */
    public FeiShuIsvGetOrderRespDTO.Order getFeiShuOrder(String orderId, String corpId) {
//        String res = "{\n" +
//                "    \"code\":0,\n" +
//                "    \"msg\":\"ok\",\n" +
//                "    \"data\": {        \n" +
//                "        \"order\": {\n" +
//                "                \"order_id\":\"6704894492631105539\",\n" +
//                "                \"price_plan_id\":\"price_9daf66c96968c003\",\n" +
//                "                \"price_plan_type\":\"per_year\",\n" +
//                "                \"seats\":2,\n" +
//                "                \"buy_count\":2,\n" +
//                "                \"create_time\":\"1565003215\",\n" +
//                "                \"pay_time\":\"1598248455\",\n" +
//                "                \"status\":\"normal\",\n" +
//                "                \"buy_type\":\"buy\",\n" +
//                "                \"src_order_id\":\"6704894492631105539\",\n" +
//                "                \"dst_order_id\":\"6704894492631105539\",\n" +
//                "                \"order_pay_price\":10000,\n" +
//                "                \"tenant_key\":\"2d8799d0044f175e\"\n" +
//                "            }\n" +
//                "    }\n" +
//                "}";
        String url = feishuHost + FeiShuConstant.GET_ORDER;
        Map<String, Object> param = new HashMap<>();
        param.put("order_id", orderId);
        String res = feiShuIsvHttpUtils.getWithTenantAccessToken(url, param, corpId);
        FeiShuIsvGetOrderRespDTO feiShuIsvGetOrderRespDTO = JsonUtils.toObj(res, FeiShuIsvGetOrderRespDTO.class);
        if (feiShuIsvGetOrderRespDTO == null || 0 != feiShuIsvGetOrderRespDTO.getCode()) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.GET_ORDER_FAILED);
        }
        return feiShuIsvGetOrderRespDTO.getData().getOrder();
    }

}

package com.fenbeitong.openapi.plugin.kingdee.common.controller;

import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.core.util.HttpServletRequestUtils;
import com.fenbeitong.openapi.plugin.kingdee.common.service.KingDeeKPushService;
import com.fenbeitong.openapi.plugin.kingdee.common.service.KingDeeOrgService;
import com.fenbeitong.openapi.plugin.kingdee.common.service.impl.KingDeeCommonServiceImpl;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

/**
 * @Description
 * @Author duhui
 * @Date 2021/11/5
 **/
@Slf4j
@RestController
@RequestMapping("/kingdee/3kCloud")
public class KingdeeK3CloudController {

    @Autowired
    KingDeeOrgService jinDie3kCloudService;

    @Autowired
    KingDeeCommonServiceImpl kingDeeCommonService;

    @Autowired
    KingDeeKPushService kingDeePushService;

    /**
     * 组织架构同步
     */
    @RequestMapping("/syncOrganization/{companyId}")
    public Object syncIteam(@PathVariable("companyId") String companyId) {
        CompletableFuture.supplyAsync(() -> jinDie3kCloudService.syncOrganization(companyId)).exceptionally(e -> {
            log.warn("", e);
            return "false";
        });
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }


    /**
     * @Description 订单推送
     * @param companyId 企业ID
     * @param moduleType open_kingdee_req_data 表的 module_type
     * @param templateType open_template_config 表的 PLUGIN_TYPE
     * @param dataType open_template_config 表的 TYPE
     * @Author duhui
     * @Date 2021/8/30
     **/
    /**
     * @Description 推送订单
     * @Author duhui
     * @Date 2021/6/22
     **/
    @RequestMapping("/order/push")
    @ResponseBody
    public Object orderPush(HttpServletRequest request, @RequestParam("companyId") String companyId, @RequestParam("moduleType") String moduleType, @RequestParam("templateType") Integer templateType, @RequestParam("dataType") String dataType) {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        kingDeePushService.orderPush(requestBody, companyId, moduleType, templateType, dataType);
        return OpenapiResponseUtils.success(Maps.newHashMap());

    }

    /**
     * @Description 凭证推送
     * @Author duhui
     * @Date 2021/6/22
     **/
    @RequestMapping("/voucher/push")
    @ResponseBody
    public Object voucherPush(@RequestParam("companyId") String companyId, @RequestParam("batchId") String batchId) {
        kingDeePushService.voucherPush(companyId, batchId);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

    /**
     * @param companyId    企业ID
     * @param moduleType   open_kingdee_req_data 表的 module_type
     * @param templateType open_template_config 表的 PLUGIN_TYPE
     * @param billNo       账单编号
     * @Description 账单推送
     * @Author duhui
     * @Date 2021/11/4
     **/
    @RequestMapping("/bill/push")
    @ResponseBody
    public Object billPush(@RequestParam("companyId") String companyId, @RequestParam("billNo") String billNo, @RequestParam("moduleType") String moduleType, @RequestParam("templateType") Integer templateType) {
        kingDeePushService.billPush(companyId, billNo, moduleType, templateType);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

    /**
     * 手动推送
     */
    @RequestMapping("/made/bill/push")
    @ResponseBody
    public Object madePillPush(@RequestParam("companyId") String companyId, @RequestParam("billNo") String billNo, @RequestParam("moduleType") String moduleType, @RequestParam("templateType") Integer templateType) {
        kingDeePushService.madeBillPush(companyId, billNo, moduleType, templateType);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }

    /**
     * 手动初始化三方数据
     */
    @RequestMapping("/made/bill/initThirdData")
    @ResponseBody
    public Object initThirdData(@RequestParam("companyId") String companyId, @RequestParam("billNo") String billNo, @RequestParam("moduleType") String moduleType, @RequestParam("templateType") Integer templateType) {
        kingDeePushService.initThirdData(companyId, billNo, moduleType, templateType);
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }
}

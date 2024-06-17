package com.fenbeitong.openapi.plugin.dingtalk.isv.service.impl;

import com.dingtalk.api.request.OapiAppstoreInternalSkupageGetRequest;
import com.dingtalk.api.response.OapiAppstoreInternalSkupageGetResponse;
import com.fenbeitong.finhub.auth.entity.base.UserComInfoVO;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.DingtalkIsvCompany;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyDefinitionService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvOpenPayService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.util.DingtalkIsvClientUtils;
import com.fenbeitong.openapi.plugin.support.common.dto.UserCenterResponse;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigCode;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.support.employee.dto.UcEmployeeSelfInfoResponse;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * 钉钉应用内充值
 *
 * @author lizhen
 * @date 2020/11/6
 */
@ServiceAspect
@Service
@Slf4j
public class DingtalkIsvOpenPayServiceImpl implements IDingtalkIsvOpenPayService {


    @Value("${dingtalk.host}")
    private String dingtalkHost;

    @Autowired
    private DingtalkIsvClientUtils dingtalkIsvClientUtils;

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private IDingtalkIsvCompanyDefinitionService dingtalkIsvCompanyDefinitionService;

    @Autowired
    private OpenSysConfigService openSysConfigService;


    /**
     * 充值
     *
     * @param user
     * @param callbackPage
     * @return
     */
    @Override
    public String recharge(UserComInfoVO user, String callbackPage) {
        if (user == null) {
            throw new OpenApiDingtalkException(NumericUtils.obj2int(DingtalkResponseCode.TOKEN_INFO_IS_ERROR));
        }
        DingtalkIsvCompany dingtalkIsvCompany = dingtalkIsvCompanyDefinitionService.getDingtalkIsvCompanyByCompanyId(user.getCompany_id());
        if (dingtalkIsvCompany == null) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_COMPANY_UNDEFINED);
        }
        String corpId = dingtalkIsvCompany.getCorpId();
        String goodsCode = openSysConfigService.getOpenSysConfigByCode(OpenSysConfigCode.DINGTALK_ISV_GOODS_CODE.getCode());
        String skupage = getSkupage(goodsCode, callbackPage, corpId);
        return skupage;
    }

    /**
     * 获取内购商品SKU页面地址
     *
     * @param goodsCode
     * @param callbackPage
     * @param corpId
     * @return
     */
    private String getSkupage(String goodsCode, String callbackPage, String corpId) {
        String url = dingtalkHost + "topapi/appstore/internal/skupage/get";
        OapiAppstoreInternalSkupageGetRequest request = new OapiAppstoreInternalSkupageGetRequest();
        request.setGoodsCode(goodsCode);
        request.setCallbackPage(callbackPage);
        request.setHttpMethod("GET");
        OapiAppstoreInternalSkupageGetResponse oapiAppstoreInternalSkupageGetResponse = dingtalkIsvClientUtils.executeWithCorpAccesstoken(url, request, corpId);
        if (oapiAppstoreInternalSkupageGetResponse == null || oapiAppstoreInternalSkupageGetResponse.getErrcode() != 0) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_DINGTALK_ERROR, oapiAppstoreInternalSkupageGetResponse.getMessage());
        }
        return oapiAppstoreInternalSkupageGetResponse.getResult();
    }
}

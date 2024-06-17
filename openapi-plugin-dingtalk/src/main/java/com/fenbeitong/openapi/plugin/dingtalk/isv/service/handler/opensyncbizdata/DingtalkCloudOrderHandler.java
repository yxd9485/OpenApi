package com.fenbeitong.openapi.plugin.dingtalk.isv.service.handler.opensyncbizdata;

import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.dingtalk.isv.constant.OpenSyncBizDataType;
import com.fenbeitong.openapi.plugin.dingtalk.isv.dto.DingtalkIsvMarketOrderDTO;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.DingtalkIsvCompany;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.OpenSyncBizData;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyAuthService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyDefinitionService;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigCode;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.service.OpenSysConfigService;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author lizhen
 */
@Component
@Slf4j
public class DingtalkCloudOrderHandler implements IOpenSyncBizDataTaskHandler {

    @Autowired
    IDingtalkIsvCompanyAuthService dingtalkIsvCompanyAuthService;

    @Autowired
    private OpenSysConfigService openSysConfigService;

    @Autowired
    private IDingtalkIsvCompanyDefinitionService dingtalkIsvCompanyDefinitionService;

    @Override
    public OpenSyncBizDataType getTaskType() {
        return OpenSyncBizDataType.DINGTALK_ISV_ORDER;
    }

    @Override
    public TaskResult execute(OpenSyncBizData task) {
        String corpId = task.getCorpId();
        String bizData = task.getBizData();
        DingtalkIsvMarketOrderDTO dingtalkIsvMarketOrderDTO = JsonUtils.toObj(bizData, DingtalkIsvMarketOrderDTO.class);
        dingtalkIsvCompanyAuthService.authCompanyOrPerson(dingtalkIsvMarketOrderDTO);//个人或企业添加授权信息，判断isv_company表中是否有数据，如果没有，新增，有的话，不用处理
        String goodsCode = dingtalkIsvMarketOrderDTO.getGoodsCode();
        //套餐内购
        String openSysConfigByCode = openSysConfigService.getOpenSysConfigByCode(OpenSysConfigCode.DINGTALK_ISV_DEFAULT_PACKAGE_GOODS_CODE.getCode());
        if (openSysConfigByCode.equals(goodsCode)) {
            dingtalkIsvCompanyAuthService.companyChangeEditon(dingtalkIsvMarketOrderDTO);
            return TaskResult.SUCCESS;
        }
        return TaskResult.ABORT;
    }

}

package com.fenbeitong.openapi.plugin.kingdee.customize.yuanqisenlin.service;

import com.fenbeitong.openapi.plugin.kingdee.support.entity.OpenThirdKingdeeConfig;

public interface IKingdeeCommonService {

    String loginAndGetCookie(OpenThirdKingdeeConfig openThirdKingdeeConfig);

    OpenThirdKingdeeConfig getOpenThirdKingdeeConfig(String companyId);
}

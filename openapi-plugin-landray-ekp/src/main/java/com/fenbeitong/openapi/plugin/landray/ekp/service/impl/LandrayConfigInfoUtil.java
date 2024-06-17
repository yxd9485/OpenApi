package com.fenbeitong.openapi.plugin.landray.ekp.service.impl;

import com.fenbeitong.openapi.plugin.landray.ekp.dao.OpenLandrayEkpConfigDao;
import com.fenbeitong.openapi.plugin.landray.ekp.entity.OpenLandrayEkpConfig;
import com.fenbeitong.openapi.plugin.support.apply.dto.FenbeitongApproveDto;
import com.fenbeitong.openapi.plugin.support.revert.apply.service.AbstractConfigInfoUtil;
import com.fenbeitong.openapi.plugin.support.revert.apply.service.AbstractFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 蓝凌配置信息查询
 * @Auther zhang.peng
 * @Date 2021/8/12
 */
@Component
public class LandrayConfigInfoUtil extends AbstractConfigInfoUtil {

    @Autowired
    private OpenLandrayEkpConfigDao openLandrayEkpConfigDao;

    public String getUrl(FenbeitongApproveDto fenbeitongApproveDto){
        OpenLandrayEkpConfig ekpConfig = openLandrayEkpConfigDao.getOpenLandrayEkpConfigByCompanyId(fenbeitongApproveDto.getCompanyId());
        return null == ekpConfig ? "" : ekpConfig.getHttpUrl();
    }

    @Override
    protected String getUrl(String companyId) {
        OpenLandrayEkpConfig ekpConfig = openLandrayEkpConfigDao.getOpenLandrayEkpConfigByCompanyId(companyId);
        return null == ekpConfig ? "" : ekpConfig.getHttpUrl();
    }

    public AbstractFormService getFormService(){
        return new LandrayFormService();
    }
}

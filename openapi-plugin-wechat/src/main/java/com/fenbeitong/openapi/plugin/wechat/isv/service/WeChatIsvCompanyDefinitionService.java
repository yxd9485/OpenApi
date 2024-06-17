package com.fenbeitong.openapi.plugin.wechat.isv.service;

import com.fenbeitong.openapi.plugin.wechat.isv.dao.WeChatIsvCompanyDao;
import com.fenbeitong.openapi.plugin.wechat.isv.entity.WeChatIsvCompany;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * 企业微信三方应用企业信息
 * Created by log.chang on 2020/3/23.
 */
@ServiceAspect
@Service
@Slf4j
public class WeChatIsvCompanyDefinitionService {

    @Autowired
    private WeChatIsvCompanyDao weChatIsvCompanyDao;

    /**
     * 根据corpId查询企业微信企业对接信息
     */
    public WeChatIsvCompany getByCorpId(String corpId) {
        return weChatIsvCompanyDao.getByCorpId(corpId);
    }

    /**
     * 根据companyId查询企业微信企业对接信息
     */
    public WeChatIsvCompany getByCompanyId(String companyId) {
        return weChatIsvCompanyDao.getByCompanyId(companyId);
    }

    /**
     * 保存企业微信对接信息
     * @param weChatIsvCompany
     */
    public void saveWeChatIsvCompany(WeChatIsvCompany weChatIsvCompany) {
        weChatIsvCompanyDao.saveSelective(weChatIsvCompany);
    }

    /**
     * 更新企业微信对接信息
     * @param weChatIsvCompany
     */
    public void updateWechatIsvCompany(WeChatIsvCompany weChatIsvCompany) {
        weChatIsvCompanyDao.updateById(weChatIsvCompany);
    }
}

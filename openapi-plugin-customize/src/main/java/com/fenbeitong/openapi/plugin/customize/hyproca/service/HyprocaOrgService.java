package com.fenbeitong.openapi.plugin.customize.hyproca.service;

/**
 * <p>Title: TalkOrganizationService</p>
 * <p>Description: 海普诺凯组织架构同步</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-09-18 15:26
 */
public interface HyprocaOrgService {

    /**
     * 组织架构全量同步
     */
    String allSync(String companyId, String topId);

    /**
     * 组织架构增量同步  (作废)
     */
    String syncOrganizationProtion(String companyId);

}

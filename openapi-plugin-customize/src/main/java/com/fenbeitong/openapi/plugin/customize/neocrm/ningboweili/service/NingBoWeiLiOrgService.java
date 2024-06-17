package com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.service;

/**
 * <p>Title: TalkOrganizationService</p>
 * <p>Description: 宁波伟立组织架构同步</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-09-18 15:26
 */
public interface NingBoWeiLiOrgService {

    /**
     * 组织架构全量同步
     */
    String allSync(String companyId, String topId);


}

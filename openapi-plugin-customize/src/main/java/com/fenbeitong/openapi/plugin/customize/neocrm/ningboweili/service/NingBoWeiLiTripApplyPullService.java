package com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.service;

import com.fenbeitong.openapi.plugin.customize.neocrm.ningboweili.dto.NingBoWeiLIJobConfigDto;

/**
 * <p>Title: TalkOrganizationService</p>
 * <p>Description: 海普诺凯差旅审批同步</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-09-18 15:26
 */
public interface NingBoWeiLiTripApplyPullService {

    /**
     * 拉取审批数据
     */
    String tripApplyPull(NingBoWeiLIJobConfigDto ningBoWeiLIJobConfigDto);
}

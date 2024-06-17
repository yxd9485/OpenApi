package com.fenbeitong.openapi.plugin.customize.hyproca.service;

import com.fenbeitong.openapi.plugin.customize.hyproca.dto.HyprocaJobConfigDto;

/**
 * <p>Title: TalkOrganizationService</p>
 * <p>Description: 海普诺凯差旅审批同步</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-09-18 15:26
 */
public interface HyprocaTripApplyPullService {

    /**
     * 拉取审批数据
     */
    String tripApplyPull(HyprocaJobConfigDto HyprocaJobConfigDto);
}

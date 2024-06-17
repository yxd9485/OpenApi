package com.fenbeitong.openapi.plugin.customize.zhiou.service;

import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @ClassName LandrayAndBeisenPushApplyService
 * @Description 推送非行程差旅审批接口
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/8/30
 **/
public interface ZhiouNonTravelApplyService {
    /**
     * 非行程差旅审批单推送
     * @param request 参数
     * @param companyId 公司id
     */
    boolean nonTravelApplyPush(HttpServletRequest request, String companyId);

    /**
     * 查询审批单详情
     * @param params 参数
     * @return 审批单详情
     */
    Map<String, Object> getApplyDetail(MultiValueMap params);
}

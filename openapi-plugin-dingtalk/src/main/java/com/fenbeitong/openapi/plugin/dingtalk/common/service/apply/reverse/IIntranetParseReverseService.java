package com.fenbeitong.openapi.plugin.dingtalk.common.service.apply.reverse;

import com.dingtalk.api.request.OapiProcessinstanceCreateRequest;
import com.fenbeitong.openapi.plugin.support.apply.dto.IntranetApplyBaseDTO;

import java.util.List;

/**
 * <p>Title: IIntranetParseReverseForm<p>
 * <p>Description: 钉钉反向回调解析表单接口 <p>
 * <p>Company:www.fenbeitong.com<p>
 *
 * @author liuhong
 * @date 2022/7/5 11:04
 */
public interface IIntranetParseReverseService {
    /**
     * 获取回调类型
     *
     * @return 回调类型
     */
    Integer getCallBackType();

    /**
     * 构建钉钉审批单创建请求体
     *
     * @param baseDTO          内部项目对应的审批详情
     * @return 钉钉审批单创建请求体
     */
    List<OapiProcessinstanceCreateRequest.FormComponentValueVo> buildProcessReq(IntranetApplyBaseDTO baseDTO);
}

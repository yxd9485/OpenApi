package com.fenbeitong.openapi.plugin.wechat.eia.listener;

import com.fenbeitong.openapi.plugin.wechat.common.dto.ApprovalInfo;
import com.fenbeitong.openapi.plugin.wechat.common.dto.CarApprovalInfo;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WeChatApprovalDetail;

import java.util.List;

public interface WeChatCarListener {
    /**
     * 订单用车审批模板设置
     */
    String filterEiaWeChat(WeChatApprovalDetail.WeChatApprovalInfo approvalInfo, List<ApprovalInfo.TripListBean> tripBeans, List<WeChatApprovalDetail.Content> contens, CarApprovalInfo processInfo, int type, String companyId, String userId);

}
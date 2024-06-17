package com.fenbeitong.openapi.plugin.dingtalk.common.service;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiAttendanceApproveCancelRequest;
import com.dingtalk.api.request.OapiAttendanceApproveFinishRequest;
import com.dingtalk.api.response.OapiAttendanceApproveCancelResponse;
import com.dingtalk.api.response.OapiAttendanceApproveFinishResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingTalkConstant;
import com.fenbeitong.openapi.plugin.support.apply.service.AbstractApplyService;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>Title: DingtalkAttendanceApproveImpl</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021/10/10 2:00 下午
 */
@Service
@Slf4j
public class DingtalkAttendanceApproveImpl extends AbstractApplyService {


    @Autowired
    private UserCenterService userCenterService;

    /**
     * 通知审批通过
     */
    public void approveFinish(OapiAttendanceApproveFinishRequest req, String accessToken, String url) {
        finish(req, accessToken, url);
    }

    /**
     * 通知审批撤销
     */
    public void approveCancel(String userId, String ApproveId, String accessToken, String url) {
        cancel(userId, ApproveId, accessToken, url);
    }

    /**
     * 通知审批修改 先撤销，再重新通过
     */
    public void approveUpdate(OapiAttendanceApproveFinishRequest req, String accessToken, String url) {
        if (cancel(req.getUserid(), req.getApproveId(), accessToken, url)) {
            finish(req, accessToken, url);
        }
    }


    private boolean finish(OapiAttendanceApproveFinishRequest req, String accessToken, String url) {
        DingTalkClient client = new DefaultDingTalkClient(url + "/topapi/attendance/approve/finish");
        OapiAttendanceApproveFinishResponse response;
        try {
            response = client.execute(req, accessToken);
            log.info("调用钉钉通知审批通过接口完成，参数: accessToken: {}, req: {}，result: {}", accessToken, JsonUtils.toJson(req), response.getBody());
            if (response.isSuccess()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("调用钉钉通知审批通过接口异常：", e);
        }
        return false;
    }

    private boolean cancel(String userId, String ApproveId, String accessToken, String url) {
        DingTalkClient client = new DefaultDingTalkClient(url + "/topapi/attendance/approve/cancel");
        OapiAttendanceApproveCancelRequest req = new OapiAttendanceApproveCancelRequest();
        OapiAttendanceApproveCancelResponse response;
        req.setUserid(userId);
        req.setApproveId(ApproveId);
        try {
            response = client.execute(req, accessToken);
            log.info("调用钉钉通知审批撤销过接口完成，参数: accessToken: {}, req: {}，result: {}", accessToken, JsonUtils.toJson(req), response.getBody());
            if (response.getErrcode().equals(400002L) || response.isSuccess()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("调用钉钉通知审批撤销接口异常：", e);
        }
        return false;
    }


    public void execute(String companyId, String applyId, String userId, String msgUrl, String accessToken, String url) {
        OapiAttendanceApproveFinishRequest req = new OapiAttendanceApproveFinishRequest();
        Map<String, Object> data = getData(companyId, applyId);
        format(req, data, userId, msgUrl, applyId);
        // 如果是变更行程，先取消再创建
        if ((Boolean) MapUtils.getValueByExpress(data, "apply:is_change_apply")) {
            String parentApplyId = (String) MapUtils.getValueByExpress(data, "apply:parent_apply_order_id");
            cancel(userId, parentApplyId, accessToken, url);
        }
        ;
        finish(req, accessToken, url);

    }

    public void format(OapiAttendanceApproveFinishRequest req, Map<String, Object> data, String userId, String msgUrl, String applyId) {
        Map<String, Object> objectDataMap = (Map) MapUtils.getValueByExpress(data, "objectData");
        List<Map<String, Object>> list = JsonUtils.toObj(JsonUtils.toJson(MapUtils.getValueByExpress(data, "fields")), new TypeReference<List<Map<String, Object>>>() {
        });
        list.forEach(t ->
        {
            if (DingTalkConstant.custformField.START_DATE.equals(MapUtils.getValueByExpress(t, "mtField:fieldDisplayName"))) {
                req.setFromTime(StringUtils.obj2str(objectDataMap.get((String) MapUtils.getValueByExpress(t, "mtField:fieldCode"))));
            } else if (DingTalkConstant.custformField.END_DATE.equals(MapUtils.getValueByExpress(t, "mtField:fieldDisplayName"))) {
                req.setToTime(StringUtils.obj2str(objectDataMap.get((String) MapUtils.getValueByExpress(t, "mtField:fieldCode"))));
            } else if (DingTalkConstant.custformField.START_END_DATEE.equals(MapUtils.getValueByExpress(t, "mtField:fieldDisplayName"))) {
                String dateCode = (String) MapUtils.getValueByExpress(t, "mtField:fieldCode");
                req.setFromTime(StringUtils.obj2str(MapUtils.getValueByExpress(objectDataMap,dateCode+":dStartTime" )));
                req.setToTime(StringUtils.obj2str(MapUtils.getValueByExpress(objectDataMap,dateCode+":dEndTime" )));
            }
        });
        req.setUserid(userId);
        // 1:加班 2:出差 3:请假
        req.setBizType(2L);
        req.setDurationUnit("day");
        // 0:按自然日计算 1:按工作日计算
        req.setCalculateModel(1L);
        req.setTagName("出差");
        req.setApproveId(applyId);
        req.setJumpUrl(msgUrl);
    }

    public Map<String, Object> getData(String companyId, String applyId) {
        String token = userCenterService.getUcSuperAdminToken(companyId);
        // 获取审批详情
        return getCustformApproveDetail(token, applyId, 1);

    }


}

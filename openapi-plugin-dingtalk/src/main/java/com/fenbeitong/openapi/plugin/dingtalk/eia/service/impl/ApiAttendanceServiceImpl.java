package com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl;


import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiAttendanceListRequest;
import com.dingtalk.api.response.OapiAttendanceListResponse;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IApiAttendanceService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IApiTokenService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkRouteService;
import com.google.common.collect.Lists;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.List;

import static com.dingtalk.api.response.OapiAttendanceListResponse.Recordresult;

/**
 * <p>Title: ApiAttendanceServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/1/16 3:06 PM
 */
@Slf4j
@ServiceAspect
@Service
public class ApiAttendanceServiceImpl implements IApiAttendanceService {

    @Autowired
    private IApiTokenService dingtalkTokenService;

    @Autowired
    private IDingtalkRouteService dingtalkRouteService;

    @Override
    public List<Recordresult> getAttendanceList(String workDateFrom, String workDateTo, String corpId, List<String> userIdList) {
        log.info("调用钉钉考勤列表接口， 参数：workDateFrom: {}, workDateTo: {}, corpId: {}, userIdList: {}", workDateFrom, workDateTo, corpId, userIdList);
        List<Recordresult> resultList = Lists.newArrayList();
        OapiAttendanceListResponse response = null;
        long limit = 50L;
        long offset = 0;
        do {
            response = getOapiAttendanceListResponse(workDateFrom, workDateTo, corpId, userIdList, offset, limit);
            if (response != null && !ObjectUtils.isEmpty(response.getRecordresult())) {
                resultList.addAll(response.getRecordresult());
            }
            offset += limit;
        } while (response != null && response.getHasMore());
        return resultList;
    }

    private OapiAttendanceListResponse getOapiAttendanceListResponse(String workDateFrom, String workDateTo, String corpId, List<String> userIdList, long offSet, long limit) {
        String accessToken = dingtalkTokenService.getAccessToken(corpId);
        String proxyUrl = dingtalkRouteService.getRouteByCorpId(corpId).getProxyUrl();
        DingTalkClient client = new DefaultDingTalkClient(proxyUrl + "/attendance/list");
        OapiAttendanceListRequest request = new OapiAttendanceListRequest();
        request.setWorkDateFrom(workDateFrom);
        request.setWorkDateTo(workDateTo);
        request.setUserIdList(userIdList);
        request.setOffset(offSet);
        request.setLimit(limit);
        try {
            OapiAttendanceListResponse response = client.execute(request, accessToken);
            log.info("调用钉钉考勤列表接口完成， 返回结果：{}", response.getBody());
            if (response.isSuccess()) {
                return response;
            }
        } catch (ApiException e) {
            e.printStackTrace();
            log.error("调用钉钉考勤列表接口异常", e);
        }
        return null;
    }
}

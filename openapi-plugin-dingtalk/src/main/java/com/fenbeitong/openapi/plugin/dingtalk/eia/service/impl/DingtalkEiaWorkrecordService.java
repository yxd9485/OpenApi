package com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiWorkrecordAddRequest;
import com.dingtalk.api.request.OapiWorkrecordUpdateRequest;
import com.dingtalk.api.response.OapiWorkrecordAddResponse;
import com.dingtalk.api.response.OapiWorkrecordUpdateResponse;
import com.fenbeitong.openapi.plugin.dingtalk.common.util.DingtalkWorkRecordUtil;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IApiTokenService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkCorpService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkRouteService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.constant.DingtalkIsvConstant;
import com.fenbeitong.openapi.plugin.event.saas.dto.WebHookOrderEvent;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.webhook.dao.TbWorkrecordTaskDao;
import com.fenbeitong.openapi.plugin.support.webhook.entity.TbWorkrecordTask;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.common.CommonIdDTO;
import com.fenbeitong.usercenter.api.service.common.ICommonService;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author xiaohai
 * @date 2021/11/08
 */
@Service
@Slf4j
public class DingtalkEiaWorkrecordService {

    @Value("${dingtalk.host}")
    private String dingtalkHost;

    @Value("${host.webapp}")
    private String webappHost;

    @Autowired
    private IDingtalkCorpService dingtalkCorpService;

    @Autowired
    private IDingtalkRouteService dingtalkRouteService;

    @Autowired
    private IApiTokenService dingtalkTokenService;

    @DubboReference(check = false)
    private ICommonService iCommonService;

    @Autowired
    private TbWorkrecordTaskDao tbWorkrecordTaskDao;

    @Autowired
    private DingtalkWorkRecordUtil dingtalkWorkRecordUtil;

    /**
     * 钉钉创建待办
     *
     * @return
     */
    public void pushMessage(WebHookOrderEvent webHookOrderEvent) {
        String companyId = webHookOrderEvent.getCompanyId();
        String approverId = webHookOrderEvent.getApproverId();//审批人id
        String starterId = webHookOrderEvent.getStarterId();//提交人id
        if (StringUtils.isBlank(companyId) || StringUtils.isBlank(approverId)  || StringUtils.isBlank( starterId ) ) {
            log.info("【push信息】消息推送失败，缺少必要参数:{}", JsonUtils.toJson( webHookOrderEvent ));
            return;
        }
        //查询企业授权信息
        PluginCorpDefinition pluginCorpDefinition = dingtalkCorpService.getByCompanyId(companyId);
        if (pluginCorpDefinition == null) {
            log.info("【push信息】eia推送非dingtalk eia企业,companyId:{}", companyId);
            return;
        }
        Map<String, CommonIdDTO> thirdEmployeeIds = dingtalkWorkRecordUtil.parseThirdEmployeeId(companyId, approverId, starterId);

        String taskStatus = webHookOrderEvent.getTaskStatus();
        String thirdCorpId = pluginCorpDefinition.getThirdCorpId();
        if("0".equals(taskStatus)){//创建待办
            String uri = webappHost + String.format(DingtalkIsvConstant.DINGTALK_EIA_APP_HOME, thirdCorpId);
            OapiWorkrecordAddRequest req = dingtalkWorkRecordUtil.setWorkrecordReq( uri , webHookOrderEvent, thirdEmployeeIds , thirdCorpId,true );
            String recordId = addWorkrecord(req, thirdCorpId );
            //新增表里数据
            TbWorkrecordTask tbWorkreocrdTask = dingtalkWorkRecordUtil.setTbWorkrecordTask( webHookOrderEvent ,  recordId );
            tbWorkrecordTaskDao.save(tbWorkreocrdTask);
        }else{//修改待办状态
            String taskId = webHookOrderEvent.getTaskId();//任务id
            String thirdEmployId = Optional.ofNullable(thirdEmployeeIds.get(approverId)).orElse(new CommonIdDTO()).getThirdId();
            List<OapiWorkrecordUpdateRequest> workrecordUpdateRequestList = dingtalkWorkRecordUtil.updateWorkrecord(taskId, thirdEmployId,webHookOrderEvent,thirdCorpId,true);
            workrecordUpdateRequestList.forEach( workrecordUpdateReq ->{
                boolean b = updateWorkrecord( workrecordUpdateReq , pluginCorpDefinition.getThirdCorpId());
                if(b) tbWorkrecordTaskDao.updateWorkcordByTaskId( taskId );
            });
        }
    }

    //创建待办
    public String addWorkrecord(OapiWorkrecordAddRequest request, String corpId) {
        String proxyUrl = dingtalkRouteService.getRouteByCorpId(corpId).getProxyUrl();
        String url = proxyUrl + "/topapi/workrecord/add";
        String accessToken = dingtalkTokenService.getAccessToken(corpId);
        DingTalkClient client = new DefaultDingTalkClient(url);
        OapiWorkrecordAddResponse response;
        try {
            response = client.execute(request, accessToken);
            log.info("eia钉钉创建待办完成，参数: corpId: {}，request: {}，result: {}", corpId, JsonUtils.toJson(request), response.getBody());
            return response.getRecordId();
        } catch (ApiException e) {
            log.error("eia钉钉创建待办接口异常：{}", e);
        }
        return null;
    }
    //更新待办
    public boolean updateWorkrecord(OapiWorkrecordUpdateRequest request, String corpId) {
        String proxyUrl = dingtalkRouteService.getRouteByCorpId(corpId).getProxyUrl();
        String url = proxyUrl + "/topapi/workrecord/update";
        String accessToken = dingtalkTokenService.getAccessToken(corpId);
        DingTalkClient client = new DefaultDingTalkClient(url);
        OapiWorkrecordUpdateResponse response;
        try {
            response = client.execute(request, accessToken);
            log.info("eia钉钉更新待办完成，参数: corpId: {}，request: {}，result: {}", corpId, JsonUtils.toJson(request), response.getBody());
            return response.getResult();
        } catch (ApiException e) {
            log.error("eia钉钉更新待办接口异常：{}", e);
        }
        return false;
    }


}

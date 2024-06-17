package com.fenbeitong.openapi.plugin.dingtalk.isv.service.impl;

import com.dingtalk.api.request.OapiWorkrecordAddRequest;
import com.dingtalk.api.request.OapiWorkrecordUpdateRequest;
import com.dingtalk.api.response.OapiWorkrecordAddResponse;
import com.dingtalk.api.response.OapiWorkrecordUpdateResponse;
import com.fenbeitong.openapi.plugin.dingtalk.common.util.DingtalkWorkRecordUtil;
import com.fenbeitong.openapi.plugin.dingtalk.isv.constant.DingtalkIsvConstant;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.DingtalkIsvCompany;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyDefinitionService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.util.DingtalkIsvClientUtils;
import com.fenbeitong.openapi.plugin.event.saas.dto.WebHookOrderEvent;
import com.fenbeitong.openapi.plugin.support.webhook.dao.TbWorkrecordTaskDao;
import com.fenbeitong.openapi.plugin.support.webhook.entity.TbWorkrecordTask;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.common.CommonIdDTO;
import com.fenbeitong.usercenter.api.service.common.ICommonService;
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
public class DingtalkIsvWorkrecordService {

    @Value("${dingtalk.host}")
    private String dingtalkHost;

    @Value("${host.dd_webapp}")
    private String webappHost;

    @Autowired
    private DingtalkIsvClientUtils dingtalkIsvClientUtils;

    @Autowired
    private IDingtalkIsvCompanyDefinitionService dingtalkIsvCompanyDefinitionService;

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
        log.info("isv接收到消息内部消息的消息体为：{}", webHookOrderEvent);
        String companyId = webHookOrderEvent.getCompanyId();
        String approverId = webHookOrderEvent.getApproverId();//审批人id
        String starterId = webHookOrderEvent.getStarterId();//提交人id
        if (StringUtils.isBlank(companyId) || StringUtils.isBlank(approverId)  || StringUtils.isBlank( starterId ) ) {
            log.info("【push信息】消息推送失败，缺少必要参数:{}", JsonUtils.toJson( webHookOrderEvent ));
            return;
        }
        DingtalkIsvCompany dingtalkIsvCompany = dingtalkIsvCompanyDefinitionService.getDingtalkIsvCompanyByCompanyId(companyId);
        if (dingtalkIsvCompany == null) {
            log.info("【push信息】非dingtalk isv企业,companyId:{}", companyId);
            return;
        }

        //查询企业授权信息  查询员工id
        Map<String, CommonIdDTO> thirdEmployeeIds = dingtalkWorkRecordUtil.parseThirdEmployeeId(companyId, approverId, starterId);
        String taskStatus = webHookOrderEvent.getTaskStatus();
        if("0".equals(taskStatus)){//创建待办
            String uri = webappHost + String.format(DingtalkIsvConstant.DINGTALK_ISV_APP_HOME,dingtalkIsvCompany.getCorpId());
            OapiWorkrecordAddRequest req = dingtalkWorkRecordUtil.setWorkrecordReq( uri , webHookOrderEvent, thirdEmployeeIds , null ,false);
            String recordId = addWorkrecord(req, dingtalkIsvCompany.getCorpId());
            TbWorkrecordTask tbWorkreocrdTask = dingtalkWorkRecordUtil.setTbWorkrecordTask( webHookOrderEvent ,  recordId );
            tbWorkrecordTaskDao.save(tbWorkreocrdTask); //新增待办表里数据
        }else{//修改待办状态
            String taskId = webHookOrderEvent.getTaskId();//任务id
            String thirdEmployId = Optional.ofNullable(thirdEmployeeIds.get(approverId)).orElse(new CommonIdDTO()).getThirdId();
            List<OapiWorkrecordUpdateRequest> workrecordUpdateRequestList = dingtalkWorkRecordUtil.updateWorkrecord(taskId, thirdEmployId,webHookOrderEvent,null,false);
            workrecordUpdateRequestList.forEach( workrecordUpdateReq ->{
                boolean b = updateWorkrecord( workrecordUpdateReq , dingtalkIsvCompany.getCorpId());
                if(b) tbWorkrecordTaskDao.updateWorkcordByTaskId( taskId );
            });
        }

    }

    //新增待办
    public String addWorkrecord(OapiWorkrecordAddRequest request, String corpId) {
        String url = dingtalkHost + "topapi/workrecord/add";
        OapiWorkrecordAddResponse response = dingtalkIsvClientUtils.executeWithCorpAccesstoken(url, request, corpId);
        return response.getRecordId();
    }

    //更新待办状态
    public boolean updateWorkrecord(OapiWorkrecordUpdateRequest request, String corpId) {
        String url = dingtalkHost + "topapi/workrecord/update";
        OapiWorkrecordUpdateResponse result = dingtalkIsvClientUtils.executeWithCorpAccesstoken(url, request, corpId);
        return result.getResult();
    }






}

package com.fenbeitong.openapi.plugin.dingtalk.common.util;

import com.dingtalk.api.request.OapiWorkrecordAddRequest;
import com.dingtalk.api.request.OapiWorkrecordUpdateRequest;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IApiUserService;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvEmployeeService;
import com.fenbeitong.openapi.plugin.etl.constant.EtlScriptType;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.event.saas.dto.ApplyOrderDetailCommonDTO;
import com.fenbeitong.openapi.plugin.event.saas.dto.WebHookOrderEvent;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseCode;
import com.fenbeitong.openapi.plugin.func.exception.OpenApiFuncException;
import com.fenbeitong.openapi.plugin.support.apply.dto.CompanyApplyDetailReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.impl.OpenApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.message.MessagePushUtils;
import com.fenbeitong.openapi.plugin.support.webhook.dao.TbWorkrecordTaskDao;
import com.fenbeitong.openapi.plugin.support.webhook.entity.TbWorkrecordTask;
import com.fenbeitong.openapi.plugin.util.*;
import com.fenbeitong.usercenter.api.model.dto.common.CommonIdDTO;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.fenbeitong.usercenter.api.service.common.ICommonService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: xiaohai
 * @Date: 2021/11/08 下午3:23
 */
@Component
public class DingtalkWorkRecordUtil {

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private OpenApplyServiceImpl openApplyService;

    @Autowired
    private TbWorkrecordTaskDao tbWorkrecordTaskDao;

    @DubboReference(check = false)
    private ICommonService iCommonService;

    @Autowired
    private IApiUserService apiUserService;

    @Autowired
    private IDingtalkIsvEmployeeService dingtalkIsvEmployeeService;

    @Autowired
    private OpenThirdScriptConfigDao openThirdScriptConfigDao;

    //转换待办消息
    public TbWorkrecordTask setTbWorkrecordTask(WebHookOrderEvent webHookOrderEvent , String recordId ){
        TbWorkrecordTask tbWorkreocrdTask = new TbWorkrecordTask();
        tbWorkreocrdTask.setId( RandomUtils.bsonId() );
        tbWorkreocrdTask.setWorkrecordId( recordId );
        tbWorkreocrdTask.setApplyId( webHookOrderEvent.getApplyOrderId() );
        tbWorkreocrdTask.setWorkrecordStatus( 0 );//0:待处理 1：已处理
        tbWorkreocrdTask.setTaskId( webHookOrderEvent.getTaskId() );
        tbWorkreocrdTask.setApplyName(webHookOrderEvent.getProcessDefName() );
        tbWorkreocrdTask.setCompanyId( webHookOrderEvent.getCompanyId() );
        tbWorkreocrdTask.setStarterId( webHookOrderEvent.getStarterId() );
        tbWorkreocrdTask.setApproverId( webHookOrderEvent.getApproverId() );
        tbWorkreocrdTask.setCreateTime( new Date() );
        tbWorkreocrdTask.setUpdateTime( new Date() );
        return tbWorkreocrdTask;
    }

    //钉钉审批消息
    public OapiWorkrecordAddRequest setWorkrecordReq(String uri , WebHookOrderEvent webHookOrderEvent , Map<String, CommonIdDTO> thirdEmployeeIds , String thirdCorpId , boolean isEia){
        //查询待办详情
        ApplyOrderDetailCommonDTO applyOrderDetail = getApplyOrderDetail(webHookOrderEvent.getCompanyId(), webHookOrderEvent.getApplyOrderId());
        OapiWorkrecordAddRequest req = new OapiWorkrecordAddRequest();
        String thirdId = Optional.ofNullable(thirdEmployeeIds.get( webHookOrderEvent.getApproverId() )).orElse(new CommonIdDTO()).getThirdId();
        // 审批人id
        req.setUserid( thirdId );
        if (isEia){
            String companyId = webHookOrderEvent.getCompanyId();
            String thirdByPhone = getThirdByPhoneIfExist(companyId,webHookOrderEvent.getApproverId(),thirdCorpId);
            req.setUserid( StringUtils.isBlank(thirdByPhone) ? thirdId : thirdByPhone );
        }
        req.setCreateTime( System.currentTimeMillis() );
        req.setTitle( webHookOrderEvent.getStarterName() + "提交的" +applyOrderDetail.getApplyOrderTypeName());
        List<OapiWorkrecordAddRequest.FormItemVo> formItemVoList = new ArrayList<>();
        OapiWorkrecordAddRequest.FormItemVo formItemVo = new OapiWorkrecordAddRequest.FormItemVo();
        formItemVo.setTitle("申请事由");
        String applyReason = applyOrderDetail.getApplyReason();
        String reason = StringUtils.isBlank(applyReason) ? "未填写"  : applyReason;
        formItemVo.setContent( reason );
        formItemVoList.add( formItemVo );
        req.setFormItemList( formItemVoList );
        //发起人id
        req.setOriginatorUserId( thirdEmployeeIds.get( webHookOrderEvent.getStarterId() ).getThirdId() );
        req.setSourceName( "审批" );
        req.setPcOpenType( 2L );
        req.setBizId( webHookOrderEvent.getTaskId() );
        String jumpurl = getJumpurl(webHookOrderEvent, uri , applyOrderDetail.getIsVirtualCustomForm());
        req.setPcUrl( jumpurl );
        req.setUrl( jumpurl );
        return req;
    }

    //获取跳转路径
    public  String getJumpurl(WebHookOrderEvent webHookOrderEvent , String uri , Boolean isVirtualCustomForm){
        Map eventMsgMap = new HashMap();
        // 申请单号
        eventMsgMap.put("id" , webHookOrderEvent.getApplyOrderId() );
        // 申请单类型
        eventMsgMap.put("setting_type" , webHookOrderEvent.getApplyOrderType() );
        //二级申请单类型
        eventMsgMap.put("apply_type" , webHookOrderEvent.getApplyType() );
        // 2:审批人
        eventMsgMap.put("view_type" , "2");
        // order_type不能为空，后续跳转判断不能为空，没有用该字段
        eventMsgMap.put("order_type" , "7");
        //虚拟卡自定义页面
        eventMsgMap.put("isVirtualCustomForm" , isVirtualCustomForm);
        return MessagePushUtils.initApplicationUrl(eventMsgMap, uri);
    }

    //查询审批单详情
    public ApplyOrderDetailCommonDTO getApplyOrderDetail(String companyId, String  applyOrderId){
        //查询审批单详情
        String token = userCenterService.getUcSuperAdminToken(companyId);
        CompanyApplyDetailReqDTO companyApplyDetailReqDTO = CompanyApplyDetailReqDTO.builder().applyId(applyOrderId).build();
        Map<String, Object> companyApproveDetail = openApplyService.getCompanyApproveDetail(token, companyApplyDetailReqDTO);
        if (MapUtils.isBlank(companyApproveDetail)){
            throw new OpenApiFuncException(NumericUtils.obj2int(FuncResponseCode.OPANAPI_HTTP_DATA_ERROR));
        }
        ApplyOrderDetailCommonDTO applyOrderDetailCommonDTO = JsonUtils.toObj(JsonUtils.toJson(companyApproveDetail.get("apply")), ApplyOrderDetailCommonDTO.class);
        return applyOrderDetailCommonDTO;
    }

    //人员转换分贝通id
    public Map<String, CommonIdDTO> parseThirdEmployeeId(String companyId , String... ids){
        List<String> employeeList = CollectionUtils.newArrayList();
        for ( String id : ids ){
            employeeList.add( id );
        }
        List<CommonIdDTO> commonIdDTOS = iCommonService.queryIdDTO(companyId, employeeList, 1, 3);
        return commonIdDTOS.stream().collect(Collectors.toMap(CommonIdDTO::getId, Function.identity(), (key1, key2) -> key2));
    }

    //修改待办状态
    public List<OapiWorkrecordUpdateRequest> updateWorkrecord(String taskId , String approverThirdEmployeeId , WebHookOrderEvent webHookOrderEvent , String thirdCorpId , boolean isEia ){
        //通过任务id查询taskId
        Map<String, Object> condition = new HashMap<>();
        condition.put("taskId" , taskId );
        List<TbWorkrecordTask> tbWorkrecordTasks = tbWorkrecordTaskDao.listTbWorkrecordTask(condition);
        List<OapiWorkrecordUpdateRequest> workrecordUpdateRequestList = new ArrayList<>();
        tbWorkrecordTasks.forEach( tbWorkrecordTask -> {
            String workrecordId = tbWorkrecordTask.getWorkrecordId();
            OapiWorkrecordUpdateRequest req = new OapiWorkrecordUpdateRequest();
            req.setUserid( approverThirdEmployeeId );
            if (isEia){
                String thirdByPhone = getThirdByPhoneIfExist(webHookOrderEvent.getCompanyId(),webHookOrderEvent.getApproverId(),thirdCorpId);
                //审批人id
                req.setUserid( StringUtils.isBlank(thirdByPhone) ? approverThirdEmployeeId : thirdByPhone );
            }
            req.setRecordId( workrecordId );
            workrecordUpdateRequestList.add( req );
        });
        return workrecordUpdateRequestList;
    }

    public String getThirdByPhoneIfExist( String companyId , String employeeId , String thirdCorpId ){
        OpenThirdScriptConfig freeAccountConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.USER_FREE_LOGIN);
        // 有配置手机号免登 , 根据手机号查询 dingdingId
        if ( null != freeAccountConfig ){
            EmployeeContract employee = dingtalkIsvEmployeeService.getEmployeeByEmployeeId(companyId, employeeId);
            // 审批人id
            String thirdByPhone = apiUserService.getDingtalkUserIdByPhoneNum(thirdCorpId,employee.getPhone_num());
            return thirdByPhone;
        }
        return null;
    }

}

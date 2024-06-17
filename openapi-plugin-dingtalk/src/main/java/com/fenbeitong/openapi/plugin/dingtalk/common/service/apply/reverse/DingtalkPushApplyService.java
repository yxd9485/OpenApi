package com.fenbeitong.openapi.plugin.dingtalk.common.service.apply.reverse;

import com.aliyun.dingtalkdrive_1_0.models.AddFileResponse;
import com.aliyun.dingtalkdrive_1_0.models.AddFileResponseBody;
import com.dingtalk.api.request.OapiProcessinstanceCreateRequest;
import com.dingtalk.api.response.OapiProcessinstanceCreateResponse;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.dingtalk.common.service.DingtalkDriveClientService;
import com.fenbeitong.openapi.plugin.dingtalk.common.util.DingtalkParseFormUtil;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.DingtalkIsvCompany;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyDefinitionService;
import com.fenbeitong.openapi.plugin.support.apply.constant.OpenTripApplyConstant;
import com.fenbeitong.openapi.plugin.support.apply.constant.ProcessTypeConstant;
import com.fenbeitong.openapi.plugin.support.apply.dao.ThirdApplyDefinitionDao;
import com.fenbeitong.openapi.plugin.support.apply.dto.*;
import com.fenbeitong.openapi.plugin.support.apply.dao.OpenOrderApplyDao;
import com.fenbeitong.openapi.plugin.support.apply.entity.ThirdApplyDefinition;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.common.service.CommonService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.util.ExceptionRemind;
import com.fenbeitong.openapi.plugin.util.*;
import com.fenbeitong.usercenter.api.model.dto.common.CommonIdDTO;
import com.fenbeitong.usercenter.api.model.enums.common.IdBusinessTypeEnums;
import com.fenbeitong.usercenter.api.model.enums.common.IdTypeEnums;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: xiaohai
 * @Date: 2022/1/26 上午11:00
 */
@Slf4j
@ServiceAspect
@Service
public class DingtalkPushApplyService extends AbstractDingTalkPushApplySuper {

    @Autowired
    OpenOrderApplyDao openOrderApplyDao;
    @Autowired
    CommonApplyServiceImpl commonApplyService;
    @Autowired
    ExceptionRemind exceptionRemind;

    @Autowired
    ThirdApplyDefinitionDao thirdApplyDefinitionDao;

    @Autowired
    private IDingtalkIsvCompanyDefinitionService dingtalkIsvCompanyService;

    @Autowired
    private DingtalkDriveClientService dingtalkDriveClientService;

    @Autowired
    private CommonService commonService;


    private Map<String, String> setApplyInfo(ApplyDetailInfoDTO.Apply apply){
        return new HashMap<String, String>() {{
            put("companyId", apply.getCompanyId() );
            put("employeeId", apply.getEmployeeId());
            put("applyId", apply.getId());
            put("deptId", apply.getUserDept());
        }};
    }

    /**
     *  外卖审批
     * @param applyTakeAwayNoticeDTO
     * @param openType
     * @return
     */
    public boolean pushTakeawayApply(ApplyTakeAwayNoticeDTO applyTakeAwayNoticeDTO,String companyId, Integer openType) {
        List<OapiProcessinstanceCreateRequest.FormComponentValueVo> formComponentList = DingtalkParseFormUtil.setTakeawayApply(applyTakeAwayNoticeDTO);
        ApplyTakeAwayNoticeDTO.Apply apply = applyTakeAwayNoticeDTO.getApply();
        Map<String, String> applyInfo =  setApplyInfo( companyId , apply.getEmployeeId() , apply.getApplyId() , apply.getDepartmentId());
        return createTripApply( applyInfo , formComponentList, ProcessTypeConstant.TAKEAWAY_REVERSE , openType);
    }

    /**
     *  采购审批
     * @param applyDetail
     * @param openType
     * @return
     */
    public boolean pushMallApply(MallApplyDTO applyDetail, String companyId ,Integer openType) {
        List<OapiProcessinstanceCreateRequest.FormComponentValueVo> formComponentList = DingtalkParseFormUtil.setMallApply(applyDetail);
        Map<String, String> applyInfo = setThirdApplyInfo(companyId , applyDetail.getThirdEmployeeId() , applyDetail.getApplyId() , applyDetail.getThirdDeptId());
        return createTripApply( applyInfo , formComponentList, ProcessTypeConstant.MALL_REVERSE , openType);
    }

    /**
     *  分贝券审批单
     * @param
     * @param openType
     * @return
     */
    public boolean pushFbCouponApply(FBCouponApplyDetailDTO fbCouponApplyDetail, String companyId , Integer openType) {
        DingtalkIsvCompany dingtalkIsvCompany = dingtalkIsvCompanyService.getDingtalkIsvCompanyByCompanyId(companyId);
        if (dingtalkIsvCompany == null) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_COMPANY_UNDEFINED);
        }
        List<OapiProcessinstanceCreateRequest.FormComponentValueVo> formComponentList = DingtalkParseFormUtil.setFbCouponApply( fbCouponApplyDetail );
        List<String> applyAttachmentUrl = fbCouponApplyDetail.getCoupon().getApplyAttachmentUrl();
        if( !CollectionUtils.isBlank( applyAttachmentUrl ) ){
            //附件信息
            OapiProcessinstanceCreateRequest.FormComponentValueVo attachmentVo = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
            attachmentVo.setName(OpenTripApplyConstant.fbCouponApply.APPLY_ATTACHMENT_URL);
            List<AddFileResponse> filelist = dingtalkDriveClientService.uploadFileToDingtalk(dingtalkIsvCompany.getCorpId(), dingtalkIsvCompany.getAgentid(), fbCouponApplyDetail.getApply().getThirdEmployeeId(), applyAttachmentUrl);
            attachmentVo.setValue(JsonUtils.toJson( setFileinfo( filelist )));
            formComponentList.add(attachmentVo);
        }
        FBCouponApplyDetailDTO.Apply apply = fbCouponApplyDetail.getApply();
        Map<String, String> applyInfo =  setApplyInfo( companyId , apply.getEmployeeId() , apply.getApplyId() , apply.getDepartmentId());
        return createTripApply( applyInfo , formComponentList, ProcessTypeConstant.FB_COUNPON_REVERSE , openType);
    }

    private Map<String, String> setThirdApplyInfo(String companyId , String thirdEmployeeId , String applyId , String thirdDeptId){
        return new HashMap<String, String>() {{
            put("companyId", companyId );
            put("thirdEmployeeId", thirdEmployeeId);
            put("applyId", applyId);
            put("thirdDeptId", thirdDeptId);
        }};
    }

    private Map<String, String> setApplyInfo(String companyId , String employeeId , String applyId , String deptId){
        return new HashMap<String, String>() {{
            put("companyId", companyId );
            put("employeeId", employeeId);
            put("applyId", applyId);
            put("deptId", deptId);
        }};
    }
    /**
     * 里程补贴审批单
     * @param
     * @param openType
     * @return
     */
    public boolean pushMileageApply(MileageSubsidyNoticeDTO mileageSubsidyNotice, String companyId , Integer openType) {
        List<OapiProcessinstanceCreateRequest.FormComponentValueVo> formComponentList = DingtalkParseFormUtil.setMileageApply( mileageSubsidyNotice );
        MileageSubsidyNoticeDTO.Apply apply = mileageSubsidyNotice.getApply();
        Map<String, String> applyInfo =  setApplyInfo( companyId , apply.getEmployeeId() , apply.getApplyId() , apply.getDepartmentId());
        return createTripApply( applyInfo , formComponentList, ProcessTypeConstant.MILEAGE_REVERSE , openType);
    }

    /**
     * 虚拟额度审批单
     * @param
     * @param openType
     * @return
     */
    public boolean pushVirtualCardAmountApply(VirtualCardAmountDetailDTO virtualCardAmountDetailDTO, String companyId , Integer openType , int processType) {
        List<OapiProcessinstanceCreateRequest.FormComponentValueVo> formComponentList = DingtalkParseFormUtil.setVirtualCardAmountApply( virtualCardAmountDetailDTO );
        VirtualCardAmountDetailDTO.Apply apply = virtualCardAmountDetailDTO.getApply();
        DingtalkIsvCompany dingtalkIsvCompany = dingtalkIsvCompanyService.getDingtalkIsvCompanyByCompanyId(companyId);
        if (dingtalkIsvCompany == null) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_COMPANY_UNDEFINED);
        }
        if( virtualCardAmountDetailDTO.getVirtualCard() !=null && !CollectionUtils.isBlank( virtualCardAmountDetailDTO.getVirtualCard().getApplyAttachmentUrl() ) ){
            //附件信息
            OapiProcessinstanceCreateRequest.FormComponentValueVo attachmentVo = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
            attachmentVo.setName(OpenTripApplyConstant.virtualCardApply.ATTACHMENT_URL);
            List<AddFileResponse> list = dingtalkDriveClientService.uploadFileToDingtalk(dingtalkIsvCompany.getCorpId(), dingtalkIsvCompany.getAgentid(), virtualCardAmountDetailDTO.getApply().getThirdEmployeeId(), virtualCardAmountDetailDTO.getVirtualCard().getApplyAttachmentUrl());
            attachmentVo.setValue(JsonUtils.toJson( setFileinfo( list )));
            formComponentList.add(attachmentVo);
        }
        Map<String, String> applyInfo =  setApplyInfo( companyId , apply.getEmployeeId() , apply.getApplyId() , apply.getDepartmentId());
        return createTripApply( applyInfo , formComponentList, processType , openType);
    }

    /**
     * 对公付款审批单
     * @param
     * @param openType
     * @return
     */
    public boolean pushPaymentApply(PaymentApplyDetailDTO paymentApplyDetailDTO, String companyId , Integer openType , int processType) {
        DingtalkIsvCompany dingtalkIsvCompany = dingtalkIsvCompanyService.getDingtalkIsvCompanyByCompanyId(companyId);
        if (dingtalkIsvCompany == null) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_COMPANY_UNDEFINED);
        }
        //凭证附件
        PaymentApplyDetailDTO.ProofInfo proofInfo = paymentApplyDetailDTO.getProofInfo();
        String proofInfoFile = "";
        if(proofInfo != null){
            List<PaymentApplyDetailDTO.AdditionUrl> addition = proofInfo.getAddition();
            if(CollectionUtils.isNotBlank(addition)){
                List<String> applyAttachmentUrl = addition.stream().map(PaymentApplyDetailDTO.AdditionUrl::getUrl).collect(Collectors.toList());
                List<AddFileResponse> list = dingtalkDriveClientService.uploadFileToDingtalk(dingtalkIsvCompany.getCorpId(), dingtalkIsvCompany.getAgentid(),
                    paymentApplyDetailDTO.getApply().getThirdEmployeeId(), applyAttachmentUrl);
                proofInfoFile = JsonUtils.toJson(setFileinfo(list));
            }
        }
        String invoiceInfoFile = "";
        List<PaymentApplyDetailDTO.InvoiceInfo> invoiceInfos = paymentApplyDetailDTO.getInvoiceInfos();
        if(CollectionUtils.isNotBlank(invoiceInfos)){
            List<String> applyAttachmentUrl = invoiceInfos.stream().map(PaymentApplyDetailDTO.InvoiceInfo::getInvoiceAttatchmentUrl).collect(Collectors.toList());
            List<AddFileResponse> list = dingtalkDriveClientService.uploadFileToDingtalk(dingtalkIsvCompany.getCorpId(), dingtalkIsvCompany.getAgentid(),
                paymentApplyDetailDTO.getApply().getThirdEmployeeId(), applyAttachmentUrl);
            invoiceInfoFile = JsonUtils.toJson(setFileinfo(list));
        }
        List<OapiProcessinstanceCreateRequest.FormComponentValueVo> formComponentList = DingtalkParseFormUtil.setPaymentApply( paymentApplyDetailDTO  , proofInfoFile , invoiceInfoFile );
        PaymentApplyDetailDTO.Apply apply = paymentApplyDetailDTO.getApply();
        Map<String, String> applyInfo =  setApplyInfo( companyId , apply.getEmployeeId() , apply.getApplyId() , apply.getDepartmentId());
        return createTripApply( applyInfo , formComponentList, processType , openType);
    }

    private boolean createTripApply(Map<String, String> applyInfo, List<OapiProcessinstanceCreateRequest.FormComponentValueVo> formComponentList,
                                    int processType ,Integer openType){
        String companyId = applyInfo.get("companyId");
        DingtalkIsvCompany dingtalkIsvCompany = dingtalkIsvCompanyService.getDingtalkIsvCompanyByCompanyId(companyId);
        if (dingtalkIsvCompany == null) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_ISV_COMPANY_UNDEFINED);
        }
        //通过公司id查询三方企业id
        ThirdApplyDefinition thirdApply = thirdApplyDefinitionDao.getThirdApply(companyId, processType);
        if (ObjectUtils.isEmpty(thirdApply)) {
            log.warn("模版信息配置有误，请检查审批模版配置信息！" );
            throw new OpenApiDingtalkException(SupportRespCode.FB_ORDER_APPLY_IS_NULL);
        }
        String processCode = thirdApply.getThirdProcessCode();
        String corpId = dingtalkIsvCompany.getCorpId();
        String employeeId = applyInfo.get("employeeId");
        String thirdEmployeeId = applyInfo.get("thirdEmployeeId");
        if(StringUtils.isBlank(thirdEmployeeId)){
            thirdEmployeeId = commonService.getThirdEmployeeId(companyId, employeeId);
        }
        String applyId = applyInfo.get("applyId");
        OapiProcessinstanceCreateRequest req = new OapiProcessinstanceCreateRequest();
        req.setProcessCode( processCode );
        req.setOriginatorUserId( thirdEmployeeId );
        String thirdDeptId = applyInfo.get("thirdDeptId");
        if(StringUtils.isBlank(thirdDeptId)){
            String deptId = applyInfo.get("deptId");
            if(corpId.equals(deptId)){
                req.setDeptId(-1L);
            }else{
                req.setDeptId(NumericUtils.obj2long(getThirdDeptId( companyId ,  deptId )));
            }
        }else{
            req.setDeptId( NumericUtils.obj2long( thirdDeptId ) );
        }
        req.setFormComponentValues( formComponentList );
        OapiProcessinstanceCreateResponse result = execute(getOpenType(), req, corpId);
        Map map = JsonUtils.toObj(result.getBody(), Map.class);
        String spNo = map.get("process_instance_id").toString();
        //存储分贝通审批单ID和第三方审批单ID关系
        return commonApplyService.saveFbtOrderApply(companyId, thirdEmployeeId, applyId, spNo, openType);
    }

    private List<Map<String,String>> setFileinfo(List<AddFileResponse> filelist){
        List<Map<String,String>> listMap = new ArrayList<>();
        filelist.forEach( addFileResponse -> {
            AddFileResponseBody fileResponseBody = addFileResponse.getBody();
            Map<String,String> fileMap = new HashMap<>();
            fileMap.put("spaceId" , fileResponseBody.getSpaceId());
            fileMap.put("fileName" , fileResponseBody.getFileName());
            fileMap.put("fileSize" , StringUtils.obj2str(fileResponseBody.getFileSize()));
            fileMap.put("fileType" , fileResponseBody.getFileType());
            fileMap.put("fileId" , fileResponseBody.getFileId());
            listMap.add(fileMap);
        });
        return listMap ;
    }

    /**
     * 获取三方部门id
     * @param companyId
     * @param deptId
     * @return
     */
    private String getThirdDeptId( String companyId , String deptId ){
        List<String> ids = new ArrayList<>();
        ids.add(deptId);
        List<CommonIdDTO> commonIdDTOS = commonService.queryIdDTO(companyId, ids, IdTypeEnums.FB_ID.getKey(), IdBusinessTypeEnums.ORG.getKey());
        if(commonIdDTOS == null || commonIdDTOS.size()<=0) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_DEPARTMENT_UN_SYNC);
        }
        return commonIdDTOS.get(0).getThirdId();
    }

    public int getOpenType() {
        return OpenType.DINGTALK_ISV.getType();
    }

}

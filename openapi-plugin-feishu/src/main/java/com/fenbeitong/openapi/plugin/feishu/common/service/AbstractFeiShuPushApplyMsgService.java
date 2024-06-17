package com.fenbeitong.openapi.plugin.feishu.common.service;

import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResponseCode;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalSimpleFormDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuCreateApprovalInstanceReqDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuFileuploadResp;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeishuUploadFileReq;
import com.fenbeitong.openapi.plugin.feishu.common.exception.OpenApiFeiShuException;
import com.fenbeitong.openapi.plugin.feishu.common.util.FeiShuParseFormUtils;
import com.fenbeitong.openapi.plugin.feishu.isv.entity.FeishuIsvCompany;
import com.fenbeitong.openapi.plugin.feishu.isv.service.FeiShuIsvCompanyDefinitionService;
import com.fenbeitong.openapi.plugin.support.apply.constant.OpenTripApplyConstant;
import com.fenbeitong.openapi.plugin.support.apply.constant.ProcessTypeConstant;
import com.fenbeitong.openapi.plugin.support.apply.dao.OpenOrderApplyDao;
import com.fenbeitong.openapi.plugin.support.apply.dao.ThirdApplyDefinitionDao;
import com.fenbeitong.openapi.plugin.support.apply.dto.*;
import com.fenbeitong.openapi.plugin.support.apply.entity.ThirdApplyDefinition;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.common.service.CommonService;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author xiaohai
 * @date 2022/01/26
 */
@Slf4j
@ServiceAspect
@Service
public abstract class AbstractFeiShuPushApplyMsgService {

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    @Autowired
    private ThirdApplyDefinitionDao thirdApplyDefinitionDao;

    @Autowired
    private FeiShuIsvCompanyDefinitionService feiShuIsvCompanyService;

    @Autowired
    private CommonService commonService;

    @Autowired
    OpenOrderApplyDao openOrderApplyDao;

    /**
     * 差旅行程
     * @param applyDetail
     * @param companyId
     * @return
     */
    public boolean pushTripApply(ApplyTripDTO applyDetail , String companyId) {
        String processcode = getProcesscode(companyId , ProcessTypeConstant.TRIP_REVERSE);
        String corpId = getCorpId(  companyId );
        List<FeiShuApprovalSimpleFormDTO> approvalDefines = getFeiShuApprovalService().getApprovalDefine(processcode, corpId);
        Map<String, FeiShuApprovalSimpleFormDTO> approvalFormMap = approvalDefines.stream().collect(Collectors.toMap(FeiShuApprovalSimpleFormDTO::getName, a -> a,(k1, k2)->k1));
        List<FeiShuApprovalSimpleFormDTO> approvalFormList = FeiShuParseFormUtils.parseTripInfo( applyDetail , approvalFormMap );
        return feishuCreateApplyByThirdId( companyId ,  applyDetail.getThirdEmployeeId(), applyDetail.getApplyId() , processcode , approvalFormList);
    }

    public boolean pushMultiTripApply(IntranetApplyMultiTripDetailDTO applyDetail , String companyId) {
        String processcode = getProcesscode(companyId , ProcessTypeConstant.MULTI_TRIP_REVERSE);
        String corpId = getCorpId(  companyId );
        List<FeiShuApprovalSimpleFormDTO> approvalDefines = getFeiShuApprovalService().getApprovalDefine(processcode, corpId);
        Map<String, FeiShuApprovalSimpleFormDTO> approvalFormMap = approvalDefines.stream().collect(Collectors.toMap(FeiShuApprovalSimpleFormDTO::getName, a -> a,(k1, k2)->k1));
        List<FeiShuApprovalSimpleFormDTO> approvalFormList = FeiShuParseFormUtils.setMultiTrip( applyDetail , approvalFormMap );
        return feishuCreateApplyByThirdId( companyId ,  applyDetail.getThirdEmployeeId(), applyDetail.getApplyId() , processcode , approvalFormList);
    }

    public boolean pushDinnerApply(DinnerApplyDetailDTO applyDetail, String companyId) {
        String processcode = getProcesscode(companyId, ProcessTypeConstant.DINNER_REVERSE);
        String corpId = getCorpId(companyId);
        List<FeiShuApprovalSimpleFormDTO> approvalDefines = getFeiShuApprovalService().getApprovalDefine(processcode, corpId);
        Map<String, FeiShuApprovalSimpleFormDTO> approvalFormMap = approvalDefines.stream().collect(Collectors.toMap(FeiShuApprovalSimpleFormDTO::getName, a -> a, (k1, k2) -> k1));
        List<FeiShuApprovalSimpleFormDTO> approvalFormList = FeiShuParseFormUtils.setDinnerApply(applyDetail, approvalFormMap);
        return feishuCreateApply(companyId, applyDetail.getApply().getEmployeeId(), applyDetail.getApply().getApplyId(), processcode, approvalFormList);
    }

    private boolean feishuCreateApplyByThirdId( String companyId , String thirdEmployeeId , String  applyId , String processcode, List<FeiShuApprovalSimpleFormDTO> approvalFormList ){
        Map<String, String> applyInfo = new HashMap<String, String>() {{
            put("companyId", companyId );
            put("thirdEmployeeId", thirdEmployeeId);
            put("applyId", applyId);
        }};
        return createApply( applyInfo , processcode , approvalFormList);
    }

    public boolean pushPurchaseApply(MallApplyDTO applyDetail, String companyId){
        String processcode = getProcesscode(companyId, ProcessTypeConstant.MALL_REVERSE);
        String corpId = getCorpId(  companyId );
        List<FeiShuApprovalSimpleFormDTO> approvalDefines = getFeiShuApprovalService().getApprovalDefine(processcode, corpId);
        Map<String, FeiShuApprovalSimpleFormDTO> approvalFormMap = approvalDefines.stream().collect(Collectors.toMap(FeiShuApprovalSimpleFormDTO::getName, a -> a,(k1, k2)->k1));
        List<FeiShuApprovalSimpleFormDTO> approvalFormList = FeiShuParseFormUtils.setMallApply( applyDetail , approvalFormMap );
        return feishuCreateApplyByThirdId( companyId ,  applyDetail.getThirdEmployeeId(), applyDetail.getApplyId() , processcode , approvalFormList );
    }

    public boolean pushTakeawayApply(ApplyTakeAwayNoticeDTO applyTakeAwayNoticeDTO , String companyId){
        String processcode = getProcesscode( companyId , ProcessTypeConstant.TAKEAWAY_REVERSE);
        Map<String, FeiShuApprovalSimpleFormDTO> approvalFormMap = getApprovalForm(  companyId ,  processcode );
        List<FeiShuApprovalSimpleFormDTO> approvalFormList = FeiShuParseFormUtils.setTakeawayApply( applyTakeAwayNoticeDTO , approvalFormMap );
        ApplyTakeAwayNoticeDTO.Apply apply = applyTakeAwayNoticeDTO.getApply();
        return feishuCreateApply(  companyId ,  apply.getEmployeeId() ,  apply.getApplyId() ,  processcode, approvalFormList );
    }

    private Map<String, String> setApplyInfo( ApplyDetailInfoDTO applyDetail ){
          return new HashMap<String, String>() {{
            put("companyId", applyDetail.getApply().getCompanyId() );
            put("employeeId", applyDetail.getApply().getEmployeeId());
            put("applyId", applyDetail.getApply().getId());
        }};
    }

    public boolean pushFbCounponApply( FBCouponApplyDetailDTO fbCouponApplyDetail , String companyId ){
        String processcode = getProcesscode( companyId , ProcessTypeConstant.FB_COUNPON_REVERSE);
        Map<String, FeiShuApprovalSimpleFormDTO> approvalFormMap = getApprovalForm(  companyId ,  processcode );
        List<FeiShuApprovalSimpleFormDTO> approvalFormList = FeiShuParseFormUtils.setFbCouponApply( fbCouponApplyDetail , approvalFormMap );
        FBCouponApplyDetailDTO.Apply apply = fbCouponApplyDetail.getApply();
        List<String> applyAttachmentUrl = fbCouponApplyDetail.getCoupon().getApplyAttachmentUrl();
        List<String> listCode = setFileCode( applyAttachmentUrl , companyId);
        if(approvalFormMap.containsKey(OpenTripApplyConstant.fbCouponApply.APPLY_ATTACHMENT_URL) && CollectionUtils.isNotBlank(listCode)){
            FeiShuApprovalSimpleFormDTO attachment = approvalFormMap.get(OpenTripApplyConstant.fbCouponApply.APPLY_ATTACHMENT_URL);
            attachment.setValue(listCode);
            approvalFormList.add(attachment);
        }
        return feishuCreateApply(  companyId ,  apply.getEmployeeId() ,  apply.getApplyId() ,  processcode, approvalFormList );
    }

    private List<String> setFileCode(List<String> applyAttachmentUrl,String companyId){
        String corpId = getCorpId( companyId );
        List<String> listCode = new ArrayList<>();
        if(CollectionUtils.isNotBlank( applyAttachmentUrl )){
            applyAttachmentUrl.forEach( url ->{
                FeishuUploadFileReq feiShuCreateInstanceReqDTO = new FeishuUploadFileReq();
                if(StringUtils.isNotBlank(url)){
                    String[] split = url.split("/");
                    String fileName = split[split.length-1];
                    feiShuCreateInstanceReqDTO.setType("attachment");
                    feiShuCreateInstanceReqDTO.setName(fileName);
                    File tempFile = null;
                    try {
                        tempFile = new File(fileName);
                        org.apache.commons.io.FileUtils.copyURLToFile(new URL(url) , tempFile );
                        feiShuCreateInstanceReqDTO.setContent(tempFile);
                        FeiShuFileuploadResp feiShuFileuploadResp = getFeiShuApprovalService().feiShuFileupload(corpId, feiShuCreateInstanceReqDTO);
                        String code = feiShuFileuploadResp.getData().getCode();
                        listCode.add( code );
                    } catch (IOException e) {
                        log.warn("调用飞书上传文件接口异常：", e);
                        throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_UPLOAD_FAILED);
                    }finally {
                        if(tempFile!=null){
                            tempFile.delete();
                        }
                    }
                }
            });
        }
        return listCode;
    }

    public boolean pushMileageApply(MileageSubsidyNoticeDTO mileageSubsidyNotice , String companyId ){
        String processcode = getProcesscode( companyId , ProcessTypeConstant.MILEAGE_REVERSE);
        Map<String, FeiShuApprovalSimpleFormDTO> approvalFormMap = getApprovalForm(  companyId ,  processcode );
        List<FeiShuApprovalSimpleFormDTO> approvalFormList = FeiShuParseFormUtils.setMileageApply( mileageSubsidyNotice , approvalFormMap );
        MileageSubsidyNoticeDTO.Apply apply = mileageSubsidyNotice.getApply();
        return feishuCreateApply(  companyId ,  apply.getEmployeeId() ,  apply.getApplyId() ,  processcode, approvalFormList );
    }

    public boolean pushVirtualCardApply(VirtualCardAmountDetailDTO virtualCardAmountDetailDTO  , String companyId , int processType){
        String processcode = getProcesscode( companyId , processType);
        Map<String, FeiShuApprovalSimpleFormDTO> approvalFormMap = getApprovalForm(  companyId ,  processcode );
        List<FeiShuApprovalSimpleFormDTO> approvalFormList = FeiShuParseFormUtils.setVirtualCardAmountApply( virtualCardAmountDetailDTO , approvalFormMap );
        VirtualCardAmountDetailDTO.Apply apply = virtualCardAmountDetailDTO.getApply();
        if(virtualCardAmountDetailDTO.getVirtualCard()!=null){
            List<String> applyAttachmentUrl = virtualCardAmountDetailDTO.getVirtualCard().getApplyAttachmentUrl();
            List<String> listCode = setFileCode( applyAttachmentUrl , companyId);
            if(approvalFormMap.containsKey(OpenTripApplyConstant.virtualCardApply.ATTACHMENT_URL)  && CollectionUtils.isNotBlank(listCode)){
                FeiShuApprovalSimpleFormDTO attachment = approvalFormMap.get(OpenTripApplyConstant.virtualCardApply.ATTACHMENT_URL);
                attachment.setValue(listCode);
                approvalFormList.add(attachment);
            }
        }
        return feishuCreateApply(  companyId ,  apply.getEmployeeId() ,  apply.getApplyId() ,  processcode, approvalFormList );
    }

    public boolean pushPaymentApply(PaymentApplyDetailDTO paymentApplyDetailDTO  , String companyId ){
        String processcode = getProcesscode( companyId , ProcessTypeConstant.PAYMENT_APPLY);
        Map<String, FeiShuApprovalSimpleFormDTO> approvalFormMap = getApprovalForm(  companyId ,  processcode );
        //凭证附件
        PaymentApplyDetailDTO.ProofInfo proofInfo = paymentApplyDetailDTO.getProofInfo();
        List<String> proofInfoCode = new ArrayList<>();
        if(proofInfo != null){
            List<PaymentApplyDetailDTO.AdditionUrl> addition = proofInfo.getAddition();
            if(CollectionUtils.isNotBlank(addition)){
                List<String> applyAttachmentUrl = addition.stream().map(PaymentApplyDetailDTO.AdditionUrl::getUrl).collect(Collectors.toList());
                proofInfoCode = setFileCode( applyAttachmentUrl , companyId);
            }
        }
        List<String> invoiceInfoCode = new ArrayList<>();
        List<PaymentApplyDetailDTO.InvoiceInfo> invoiceInfos = paymentApplyDetailDTO.getInvoiceInfos();
        if(CollectionUtils.isNotBlank(invoiceInfos)){
            List<String> applyAttachmentUrl = invoiceInfos.stream().map(PaymentApplyDetailDTO.InvoiceInfo::getInvoiceAttatchmentUrl).collect(Collectors.toList());
            invoiceInfoCode = setFileCode( applyAttachmentUrl , companyId);
        }
        List<FeiShuApprovalSimpleFormDTO> approvalFormList = FeiShuParseFormUtils.setPaymentApply( paymentApplyDetailDTO , approvalFormMap  , proofInfoCode , invoiceInfoCode );
        PaymentApplyDetailDTO.Apply apply = paymentApplyDetailDTO.getApply();
        return feishuCreateApply(  companyId ,  apply.getEmployeeId() ,  apply.getApplyId() ,  processcode, approvalFormList );
    }

    private boolean feishuCreateApply( String companyId , String employeeId , String  applyId , String processCode, List<FeiShuApprovalSimpleFormDTO> approvalFormList ){
        if (CollectionUtils.isBlank(approvalFormList)){
            log.warn("未成功解析到推向飞书的表单数据，companyId:{},employeeId:{},applyId:{},processCode:{}",companyId,employeeId,applyId,processCode);
            return false;
        }
        Map<String, String> applyInfo = new HashMap<String, String>() {{
            put("companyId", companyId );
            put("employeeId", employeeId);
            put("applyId", applyId);
        }};
        return createApply( applyInfo , processCode , approvalFormList);
    }

    private Map<String, FeiShuApprovalSimpleFormDTO>  getApprovalForm( String companyId , String processcode ){
        String corpId = getCorpId(  companyId );
        List<FeiShuApprovalSimpleFormDTO> approvalDefines = getFeiShuApprovalService().getApprovalDefine(processcode, corpId);
        return approvalDefines.stream().collect(Collectors.toMap(FeiShuApprovalSimpleFormDTO::getName, a -> a,(k1, k2)->k1));
    }

    private String getCorpId( String companyId ){
        FeishuIsvCompany feishuIsvCompany = feiShuIsvCompanyService.getFeiShuIsvCompanyByCompanyId(companyId);
        if (feishuIsvCompany == null) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_ISV_COMPANY_UNDEFINED);
        }
        return feishuIsvCompany.getCorpId();
    }


    private String getProcesscode(String companyId , int processType){
        ThirdApplyDefinition thirdApply = thirdApplyDefinitionDao.getThirdApply(companyId,  processType );
        if (ObjectUtils.isEmpty(thirdApply)) {
            log.warn("模版信息配置有误，请检查审批模版配置信息！" );
            throw new OpenApiFeiShuException(SupportRespCode.FB_ORDER_APPLY_IS_NULL);
        }
        return thirdApply.getThirdProcessCode();
    }

    private boolean createApply( Map<String, String> applyInfo , String processCode,  List<FeiShuApprovalSimpleFormDTO> approvalFormList ){
        String companyId = applyInfo.get("companyId");
        String employeeId = applyInfo.get("employeeId");
        String thirdEmployeeId = applyInfo.get("thirdEmployeeId");
        FeishuIsvCompany feishuIsvCompany = feiShuIsvCompanyService.getFeiShuIsvCompanyByCompanyId(companyId);
        if (feishuIsvCompany == null) {
            throw new OpenApiFeiShuException(FeiShuResponseCode.FEISHU_ISV_COMPANY_UNDEFINED);
        }
        String corpId = feishuIsvCompany.getCorpId();
        if(StringUtils.isBlank(thirdEmployeeId)){
            thirdEmployeeId = commonService.getThirdEmployeeId(companyId, employeeId);
        }
        String applyId =  applyInfo.get("applyId") ;
        FeiShuCreateApprovalInstanceReqDTO feiShuCreateInstanceReqDTO = new FeiShuCreateApprovalInstanceReqDTO();
        feiShuCreateInstanceReqDTO.setApprovalCode(processCode);
        feiShuCreateInstanceReqDTO.setUserId(thirdEmployeeId);
        feiShuCreateInstanceReqDTO.setOpenId(thirdEmployeeId);
        feiShuCreateInstanceReqDTO.setForm(JsonUtils.toJson(approvalFormList));
        String approvalInstance = getFeiShuApprovalService().createApprovalInstance(feiShuCreateInstanceReqDTO, corpId);
        //存储分贝通审批单ID和第三方审批单ID关系
        return commonApplyService.saveFbtOrderApply(companyId, thirdEmployeeId, applyId, approvalInstance, getOpenType());
    }

    protected abstract AbstractFeiShuApprovalService getFeiShuApprovalService();

    protected abstract int getOpenType();

    /**
     *  创建飞书反向审批单
     * @param carApplyDetailDTO 用车申请详情（内部项目）
     * @param companyId 公司id
     * @return
     */
    public boolean pushCarApply(IntranetApplyCarDTO carApplyDetailDTO, String companyId) {
        String processcode = getProcesscode(companyId, ProcessTypeConstant.CAR_REVERSE);
        Map<String, FeiShuApprovalSimpleFormDTO> approvalFormMap = getApprovalForm(companyId, processcode);
        List<FeiShuApprovalSimpleFormDTO> approvalFormList = FeiShuParseFormUtils.setCarApply(carApplyDetailDTO, approvalFormMap);
        return feishuCreateApply(companyId, carApplyDetailDTO.getEmployeeId(), carApplyDetailDTO.getApplyId(), processcode, approvalFormList);
    }
}

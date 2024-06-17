package com.fenbeitong.openapi.plugin.wechat.common.service;

import com.fenbeitong.finhub.auth.entity.base.UserComInfoVO;
import com.fenbeitong.invoice.api.model.dto.ChooseInvoiceInfoRPCDTO;
import com.fenbeitong.invoice.api.model.dto.EmployeeInfoRPCDTO;
import com.fenbeitong.invoice.api.model.dto.WxChooseInvoiceReqRPCDTO;
import com.fenbeitong.invoice.api.model.third.InvoiceThirdChooseRespRpcVO;
import com.fenbeitong.invoice.api.service.IFbtInvoiceService;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode;
import com.fenbeitong.openapi.plugin.wechat.common.exception.OpenApiWechatException;
import com.fenbeitong.openapi.plugin.wechat.eia.service.wechat.WeChatWebService;
import com.fenbeitong.openapi.plugin.wechat.isv.constant.WeChatIsvConstant;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.InvoiceInfoRequest;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.WeChatIsvInvoiceRequest;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.WeChatIsvInvoiceResponse;
import com.fenbeitong.openapi.plugin.wechat.isv.entity.WeChatIsvCompany;
import com.fenbeitong.openapi.plugin.wechat.isv.service.WeChatIsvCompanyDefinitionService;
import com.fenbeitong.openapi.plugin.wechat.isv.service.WeChatIsvEmployeeService;
import com.fenbeitong.openapi.plugin.wechat.isv.service.WeChatIsvInvoiceService;
import com.fenbeitong.openapi.plugin.wechat.isv.util.WeChatIsvHttpUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
 * 企业微信
 */
@ServiceAspect
@Service
@Slf4j
public class WeChatInvoiceService {

    @Autowired
    private WeChatIsvCompanyDefinitionService weChatIsvCompanyDefinitionService;

    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    private WeChatIsvInvoiceService weChatIsvInvoiceService;

    @Autowired
    private WeChatWebService weChatWebService;

    @DubboReference(check = false)
    private IFbtInvoiceService iFbtInvoiceService;


    /**
     * 保存微信发票信息到fbt
     *
     * @param user
     * @param invoiceInfoRequest
     */
    public InvoiceThirdChooseRespRpcVO importWxChooseInvoice(UserComInfoVO user, InvoiceInfoRequest invoiceInfoRequest) {
        if (user == null) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.TOKEN_INFO_IS_ERROR));
        }
        String companyId = user.getCompany_id();
        Integer sourceType = invoiceInfoRequest.getSourceType();
        List<InvoiceInfoRequest.ChooseInvoiceInfo> choicedInvoices = invoiceInfoRequest.getChooseInvoiceInfoList();
        List<ChooseInvoiceInfoRPCDTO> chooseInvoiceInfoRPCDTOS = new ArrayList<>();
        if(sourceType !=null && OpenType.WECHAT_EIA.getType() == sourceType){
            //微信内部应用
            PluginCorpDefinition corpDefinition = pluginCorpDefinitionDao.getByCompanyId(companyId);
            if (corpDefinition == null) {
                throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_COMMPANY_NOT_EXISTS));
            }
            choicedInvoices.forEach( choicedInvoice ->{
                String invoiceInfo = weChatWebService.getInvoiceInfo(choicedInvoice.getCardId(), choicedInvoice.getEncryptCode(), corpDefinition.getThirdCorpId());
                chooseInvoiceInfoRPCDTOS.add( setInvoiceInfo( choicedInvoice , invoiceInfo ) );
            });
        }else if(sourceType !=null && OpenType.WECHAT_ISV.getType() == sourceType){
            //微信三方应用
            WeChatIsvCompany weChatIsvCompany = weChatIsvCompanyDefinitionService.getByCompanyId(companyId);
            if (weChatIsvCompany == null) {
                throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_COMMPANY_NOT_EXISTS));
            }
            choicedInvoices.forEach( choicedInvoice ->{
                String invoiceInfoRes = weChatIsvInvoiceService.getInvoiceInfo(choicedInvoice.getCardId(), choicedInvoice.getEncryptCode(), weChatIsvCompany.getCorpId());
                chooseInvoiceInfoRPCDTOS.add( setInvoiceInfo( choicedInvoice , invoiceInfoRes ) );
            });
        }else{
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ARGUMENT_INCORRECT));
        }
        EmployeeInfoRPCDTO employeeInfoDTO = new EmployeeInfoRPCDTO();
        employeeInfoDTO.setCompanyId(companyId);
        employeeInfoDTO.setEmployeeId(user.getUser_id());
        employeeInfoDTO.setCompanyName(user.getCompany_name());
        employeeInfoDTO.setEmployeeName(user.getUser_name());
        employeeInfoDTO.setEmployeePhone(user.getUser_phone());
        WxChooseInvoiceReqRPCDTO wxChooseInvoiceReqRPCDTO = new WxChooseInvoiceReqRPCDTO();
        wxChooseInvoiceReqRPCDTO.setChooseInvoiceInfoRPCDTOS(chooseInvoiceInfoRPCDTOS);
        wxChooseInvoiceReqRPCDTO.setEmployeeInfoDTO(employeeInfoDTO);
        log.info("导入微信发票至分贝通，request={}", JsonUtils.toJson(wxChooseInvoiceReqRPCDTO));
        InvoiceThirdChooseRespRpcVO invoiceThirdChooseRespRpcVO = iFbtInvoiceService.webAppWxChooseInvoice(wxChooseInvoiceReqRPCDTO);
        log.info("导入微信发票至分贝通，response={}", JsonUtils.toJson(invoiceThirdChooseRespRpcVO));
        return invoiceThirdChooseRespRpcVO;
    }

    private ChooseInvoiceInfoRPCDTO setInvoiceInfo(InvoiceInfoRequest.ChooseInvoiceInfo choicedInvoice ,String invoiceInfoRes){
        ChooseInvoiceInfoRPCDTO chooseInvoiceInfoRPCDTO = new ChooseInvoiceInfoRPCDTO();
        chooseInvoiceInfoRPCDTO.setAppId(choicedInvoice.getAppId());
        chooseInvoiceInfoRPCDTO.setCardId(choicedInvoice.getCardId());
        chooseInvoiceInfoRPCDTO.setEncryptCode(choicedInvoice.getEncryptCode());
        chooseInvoiceInfoRPCDTO.setWxSnapshot(invoiceInfoRes);
        return chooseInvoiceInfoRPCDTO;
    }




}

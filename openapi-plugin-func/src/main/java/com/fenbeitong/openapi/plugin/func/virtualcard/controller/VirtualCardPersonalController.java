package com.fenbeitong.openapi.plugin.func.virtualcard.controller;

import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.annotation.FuncAuthAnnotation;
import com.fenbeitong.openapi.plugin.func.apply.dto.FuncCompanyApplyListReqDTO;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseUtils;
import com.fenbeitong.openapi.plugin.func.order.dto.BaseOrderListRespDTO;
import com.fenbeitong.openapi.plugin.func.virtualcard.dto.VirtualCardPersonalAccountReqDTO;
import com.fenbeitong.openapi.plugin.func.virtualcard.dto.VirtualCardPersonalAccountResDTO;
import com.fenbeitong.openapi.plugin.func.virtualcard.dto.VirtualCardPersonalDetailReqDTO;
import com.fenbeitong.openapi.plugin.func.virtualcard.service.VirtualCardPersonalService;
import com.fenbeitong.openapi.plugin.support.apply.dto.TakeawayAddressDTO;
import com.fenbeitong.openapi.plugin.support.common.service.CommonAuthService;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @ClassName VirtualCardPersonalController
 * @Description 虚拟卡个人消费
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/10/21 下午9:57
 **/
@Controller
@Slf4j
@RequestMapping("/func/virtualCard")
public class VirtualCardPersonalController {

    @Autowired
    private VirtualCardPersonalService virtualCardPersonalService;

    //获取虚拟卡个人消费明细
    @RequestMapping("/personal/transDetail")
    @ResponseBody
    @FuncAuthAnnotation
    public Object getPersonalTransDetail(HttpServletRequest httpRequest, @Valid ApiRequestBase apiRequest) throws BindException, IOException {
        VirtualCardPersonalDetailReqDTO req = JsonUtils.toObj(apiRequest.getData(), VirtualCardPersonalDetailReqDTO.class);
        ValidatorUtils.validateBySpring(req);

        String companyId = StringUtils.obj2str(httpRequest.getAttribute("companyId"));
        BaseOrderListRespDTO personalTransDetail = virtualCardPersonalService.getPersonalTransDetail(apiRequest,companyId);
        return FuncResponseUtils.success(personalTransDetail);
    }
    //虚拟卡个人账户信息查询
    @RequestMapping("/personal/accountInfo")
    @ResponseBody
    @FuncAuthAnnotation
    public Object listPersonalAccount(HttpServletRequest httpRequest,@Valid ApiRequestBase apiRequest) throws BindException {
        VirtualCardPersonalAccountReqDTO req = JsonUtils.toObj(apiRequest.getData(), VirtualCardPersonalAccountReqDTO.class);
        ValidatorUtils.validateBySpring(req);
        String companyId = StringUtils.obj2str(httpRequest.getAttribute("companyId"));
        List<VirtualCardPersonalAccountResDTO> personalAccountInfoList = virtualCardPersonalService.listPersonalAccountInfo(req,companyId);
        return FuncResponseUtils.success(personalAccountInfoList);
    }
}

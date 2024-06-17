package com.fenbeitong.openapi.plugin.func.virtualcard.service;

import com.fenbeitong.openapi.plugin.func.order.dto.BaseOrderListRespDTO;
import com.fenbeitong.openapi.plugin.func.virtualcard.dto.VirtualCardPersonalAccountReqDTO;
import com.fenbeitong.openapi.plugin.func.virtualcard.dto.VirtualCardPersonalAccountResDTO;
import com.fenbeitong.openapi.plugin.func.virtualcard.dto.VirtualCardPersonalDetailReqDTO;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;
import org.springframework.validation.BindException;

import java.io.IOException;
import java.util.List;

/**
 * @ClassName VirtualCardPersonalService
 * @Description 虚拟卡个人消费
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/10/21 下午9:58
 **/
public interface VirtualCardPersonalService {
    //虚拟卡个人消费明细查询
     BaseOrderListRespDTO getPersonalTransDetail(ApiRequestBase apiRequest, String companyId) throws IOException, BindException;
    //虚拟卡个人账户信息查询
    List<VirtualCardPersonalAccountResDTO> listPersonalAccountInfo(VirtualCardPersonalAccountReqDTO accountReq, String companyId) throws BindException;
}

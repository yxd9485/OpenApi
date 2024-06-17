package com.fenbeitong.openapi.plugin.func.virtualcard.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.bank.api.model.dto.VirtualCardInfoReqDTO;
import com.fenbeitong.bank.api.model.dto.VirtualCardInfoRespDTO;
import com.fenbeitong.bank.api.service.IVirtualCardInfoService;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.etl.service.impl.EtlServiceImpl;
import com.fenbeitong.openapi.plugin.func.apply.service.FuncVirtualCardApplyService;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseCode;
import com.fenbeitong.openapi.plugin.func.exception.OpenApiFuncException;
import com.fenbeitong.openapi.plugin.func.order.dto.BaseOrderListRespDTO;
import com.fenbeitong.openapi.plugin.func.virtualcard.constant.CardStatusEnum;
import com.fenbeitong.openapi.plugin.func.virtualcard.constant.EmployeeStatusEnum;
import com.fenbeitong.openapi.plugin.func.virtualcard.dto.*;
import com.fenbeitong.openapi.plugin.func.virtualcard.service.VirtualCardPersonalService;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.common.service.CommonAuthService;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.init.dto.UcEmployeeDetailDTO;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.saasplus.api.model.base.BasePageVO;
import com.fenbeitong.saasplus.api.model.base.VirtualBasePageResDTO;
import com.fenbeitong.saasplus.api.model.req.VirtualCardBankSearchQueryReqDTO;
import com.fenbeitong.saasplus.api.model.vo.VirtualBankOrderDetaiVO;
import com.fenbeitong.saasplus.api.service.virtualCard.VirtualCardOpenRpcService;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindException;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName VirtualCardPersonalServiceImpl
 * @Description 虚拟卡个人消费
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/10/21 下午9:59
 **/
@Service
@Slf4j
public class VirtualCardPersonalServiceImpl implements VirtualCardPersonalService {

    @DubboReference(check = false)
    private VirtualCardOpenRpcService virtualCardRpcService;
    @Autowired
    private EtlServiceImpl etlService;

    // private static final long PERSONAL_TRANS_DETAIL = 2529L;
    @DubboReference(check = false)
    private IBaseEmployeeExtService employeeExtService;
    @DubboReference(check = false)
    private IVirtualCardInfoService virtualCardInfoService;
    @Autowired
    private FuncVirtualCardApplyService virtualCardApplyService;
    @Autowired
    private CommonApplyServiceImpl commonApplyService;
    @Autowired
    private CommonAuthService signService;
    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;

    //虚拟卡个人消费明细添加
    @Override
    public BaseOrderListRespDTO getPersonalTransDetail(ApiRequestBase apiRequest, String companyId) throws IOException, BindException {

        String token = signService.checkSign(apiRequest);
        VirtualCardPersonalDetailReqDTO virtualCardPersonalDetailReqDTO = JsonUtils.toObj(apiRequest.getData(), VirtualCardPersonalDetailReqDTO.class);
        ValidatorUtils.validateBySpring(virtualCardPersonalDetailReqDTO);
        VirtualCardBankSearchQueryReqDTO virtualCardBankSearchQueryReqDTO = new VirtualCardBankSearchQueryReqDTO();
        BeanUtils.copyProperties(virtualCardPersonalDetailReqDTO, virtualCardBankSearchQueryReqDTO);
        UcEmployeeDetailDTO ucEmployeeDetailDTO = virtualCardApplyService.loadUserData(companyId, virtualCardPersonalDetailReqDTO.getThirdEmployeeId(), 1, 2);
        if (ObjectUtils.isEmpty(ucEmployeeDetailDTO)) {
            log.warn("三方用户不存在,{}", virtualCardPersonalDetailReqDTO.getThirdEmployeeId());
            throw new FinhubException(-999, "三方用户不存在");
        }

        VirtualBasePageResDTO<VirtualBankOrderDetaiVO> result = null;
        virtualCardBankSearchQueryReqDTO.setUserId(ucEmployeeDetailDTO.getEmployee().getId());
        BasePageVO pageInfo = new BasePageVO();
        int pageIndex = ObjectUtils.isEmpty(virtualCardPersonalDetailReqDTO.getPageIndex()) ? 1 : virtualCardPersonalDetailReqDTO.getPageIndex();
        int pageSize = ObjectUtils.isEmpty(virtualCardPersonalDetailReqDTO.getPageSize()) ? 20 : virtualCardPersonalDetailReqDTO.getPageSize();
        pageInfo.setPage(pageIndex);
        pageInfo.setPageSize(pageSize);
        virtualCardBankSearchQueryReqDTO.setPageInfo(pageInfo);
        virtualCardBankSearchQueryReqDTO.setCompanyId(companyId);
        log.info(">>>虚拟卡个人消费明细查询接口开始：{}>>>", JsonUtils.toJson(virtualCardBankSearchQueryReqDTO));
        try {
            result = virtualCardRpcService.getVirtualCardTrandDetail(virtualCardBankSearchQueryReqDTO);
        } catch (Exception e) {
            log.warn(">>>虚拟卡个人消费明细查询接口调用异常：{}>>>", e);
        }
        log.info(">>>虚拟卡个人消费明细查询接口返回：{}>>>", JsonUtils.toJson(result));

        int totalCount = result == null || ObjectUtils.isEmpty(result.getList()) ? 0 : Optional.ofNullable(result.getPageInfo().getTotalSize()).orElse(0).intValue();
        BaseOrderListRespDTO resp = BaseOrderListRespDTO.builder().pageSize(pageSize).pageIndex(pageIndex).totalCount(0).build();
        List<OpenMsgSetup> etlConfigList = openMsgSetupDao.listByCompanyIdAndItemCodeList("open-plus", Lists.newArrayList("company_virtual_detail_etl_config"));

        if (totalCount > 0 && etlConfigList != null && etlConfigList.size() > 0) {
            //etl配置
            OpenMsgSetup openMsgSetup = etlConfigList.get(0);
            long etlConfigId = NumericUtils.obj2long(openMsgSetup.getIntVal1());
            List dataList = JsonUtils.toObj(JsonUtils.toJson(result.getList()), new TypeReference<List<Map<String, Object>>>() {
            });
            List<Map<String, Object>> transferList = etlService.transform(etlConfigId, dataList);
            //费用归属转换
            for (int i = 0; i < transferList.size(); i++) {
                VirtualCardPersonalDetailResDTO virtualCardPersonalDetailResDTO = covertCostInfo(transferList.get(i), companyId, token);
                //三方人员id转换
                if (!ObjectUtils.isEmpty(virtualCardPersonalDetailResDTO) && !ObjectUtils.isEmpty(virtualCardPersonalDetailResDTO.getOrderInfo())) {
                    EmployeeContract employeeContract = employeeExtService.queryEmployeeInfo(virtualCardPersonalDetailResDTO.getOrderInfo().getEmployeeId(), companyId);
                    if (!ObjectUtils.isEmpty(employeeContract)) {
                        virtualCardPersonalDetailResDTO.getOrderInfo().setThirdEmployeeId(employeeContract.getThird_employee_id());
                    }
                }
            }
            resp.setTotalCount(totalCount);
            resp.setResults(transferList);
        }
        return resp;
    }

    //费用归属
    private VirtualCardPersonalDetailResDTO covertCostInfo(Map<String, Object> transfer, String companyId, String token) {
        VirtualCardPersonalDetailResDTO virtualCardPersonalDetailResDTO = new VirtualCardPersonalDetailResDTO();
        Object costInfoMap =null;
        if (!ObjectUtils.isEmpty(transfer.get("cost_info"))) {
            costInfoMap =MapUtils.getValueByExpress((Map)JsonUtils.toObj(JsonUtils.toJson(transfer.get("cost_info")), List.class).get(0),"cost_attribution_id");
        }
        if (transfer.containsKey("cost_info") && !ObjectUtils.isEmpty(costInfoMap)) {
            virtualCardPersonalDetailResDTO = JsonUtils.toObj(JsonUtils.toJson(transfer), VirtualCardPersonalDetailResDTO.class);
            //费用归属三方id转换
            virtualCardPersonalDetailResDTO.getCostInfo().stream().map(cost -> {
                Map requestMap = Maps.newHashMap();
                HashMap<String, Object> objectObjectHashMap = Maps.newHashMap();
                if (1 == cost.getCostAttributionType()) {//部门ID
                    objectObjectHashMap.put("businessType", 1);
                } else if (2 == cost.getCostAttributionType()) {//项目ID
                    objectObjectHashMap.put("businessType", 2);
                }
                objectObjectHashMap.put("companyId", companyId);
                objectObjectHashMap.put("ids", Lists.newArrayList(cost.getCostAttributionId()));
                objectObjectHashMap.put("type", 1);//根据分贝id获取第三方id

                requestMap.put("data", JsonUtils.toJson(objectObjectHashMap));
                requestMap.put("token", token);
                //2.调用不同类型，获取详情数据
                //调用具体转换
                Map<String, Object> idExchange = commonApplyService.getIdExchange(requestMap);
                //解析结果
                String costResult = (String) idExchange.get("data");
                Map<String, Object> map = JsonUtils.toObj(costResult, Map.class);
                Map<String, String> dataMap = (Map) map.get("data");
                String thirdCostAttributionId = dataMap.get(cost.getCostAttributionId());
                if (StringUtils.isNotBlank(thirdCostAttributionId)) {
                    cost.setCostAttributionId(thirdCostAttributionId);
                }
                return cost;
            }).collect(Collectors.toList());
        } else {
            VirtualCardOrderInfoDTO virtualCardOrderInfo = JsonUtils.toObj(JsonUtils.toJson(transfer.get("order_info")), VirtualCardOrderInfoDTO.class);
            virtualCardPersonalDetailResDTO.setOrderInfo(virtualCardOrderInfo);
        }
        return virtualCardPersonalDetailResDTO;
    }

    //虚拟卡个人账户信息查询
    @Override
    public List<VirtualCardPersonalAccountResDTO> listPersonalAccountInfo(VirtualCardPersonalAccountReqDTO
                                                                                  accountReq, String companyId) throws BindException {
        ValidatorUtils.validateBySpring(accountReq);
        List<VirtualCardPersonalAccountResDTO> result = new ArrayList<>();
        //三方id转分贝通id
        UcEmployeeDetailDTO ucEmployeeDetailDTO = virtualCardApplyService.loadUserData(companyId, accountReq.getThirdEmployeeId(), 1, 2);
        if (ObjectUtils.isEmpty(ucEmployeeDetailDTO)) {
            log.warn("三方用户不存在,{}", accountReq.getThirdEmployeeId());
            throw new FinhubException(-999, "三方用户不存在");
        }
        VirtualCardInfoReqDTO virtualCardInfoReqDTO = new VirtualCardInfoReqDTO();
        virtualCardInfoReqDTO.setCompanyId(companyId);
        virtualCardInfoReqDTO.setEmployeeId(ucEmployeeDetailDTO.getEmployee().getId());

        BeanUtils.copyProperties(accountReq, virtualCardInfoReqDTO);
        //查询默认配置表
        setVirtualCardDefaultVal(virtualCardInfoReqDTO, companyId);
        log.info(">>>虚拟卡个人消费账户查询接口开始：{}>>>", JsonUtils.toJson(virtualCardInfoReqDTO));
        try {
            List<VirtualCardInfoRespDTO> virtualCardInfoRespDTOS = virtualCardInfoService.listVirtualCard(virtualCardInfoReqDTO);
            log.info(">>>虚拟卡个人消费账户查询接口结束：{}>>>", JsonUtils.toJson(virtualCardInfoRespDTOS));
            for (VirtualCardInfoRespDTO virtualCardInfoRespDTO : virtualCardInfoRespDTOS) {
                VirtualCardPersonalAccountResDTO virtualCardPersonalAccountResDTO = new VirtualCardPersonalAccountResDTO();
                BeanUtils.copyProperties(virtualCardInfoRespDTO, virtualCardPersonalAccountResDTO);
                virtualCardPersonalAccountResDTO.setOrgUnitName(virtualCardInfoRespDTO.getUserUnitName());
                result.add(virtualCardPersonalAccountResDTO);
            }
        } catch (Exception e) {
            log.warn(">>>虚拟卡个人消费账户查询接口>>>{}调用时异常", e);
            throw new OpenApiFuncException(NumericUtils.obj2int(FuncResponseCode.OPANAPI_HTTP_INTERAL_ERROR));
        }
        return result;
    }

    //设置虚拟卡个人账户查询默认值
    private void setVirtualCardDefaultVal(VirtualCardInfoReqDTO virtualCardInfoReqDTO, String companyId) {
        //查询默认配置表
        List<OpenMsgSetup> virtualDefaultSetting = openMsgSetupDao.listByCompanyIdAndItemCodeList(companyId, Lists.newArrayList("virtual_default_setting"));
        OpenMsgSetup openMsgSetup = null;
        if (!ObjectUtils.isEmpty(virtualDefaultSetting)) {
            openMsgSetup = virtualDefaultSetting.get(0);
        }
        //非必传设置默认值
        if (StringUtils.isEmpty(virtualCardInfoReqDTO.getBankName())) {
            if (!ObjectUtils.isEmpty(openMsgSetup)) {
                String bankName = com.fenbeitong.openapi.plugin.util.StringUtils.obj2str(MapUtils.getValueByExpress(JsonUtils.toObj(openMsgSetup.getStrVal1(), Map.class), "bank_name"));
                virtualCardInfoReqDTO.setBankName(bankName);
            }
        }
        if (ObjectUtils.isEmpty(virtualCardInfoReqDTO.getCardStatus())) {
            if (!ObjectUtils.isEmpty(openMsgSetup)) {
                int cardStatus = NumericUtils.obj2int(MapUtils.getValueByExpress(JsonUtils.toObj(openMsgSetup.getStrVal1(), Map.class), "card_status"));
                cardStatus = cardStatus != 0 ? cardStatus : CardStatusEnum.NORMAL.getStatus();
                virtualCardInfoReqDTO.setCardStatus(cardStatus);
            } else {
                virtualCardInfoReqDTO.setCardStatus(CardStatusEnum.NORMAL.getStatus());
            }
        }

        if (ObjectUtils.isEmpty(virtualCardInfoReqDTO.getEmployeeStatus())) {
            if (!ObjectUtils.isEmpty(openMsgSetup)) {
                int employeeStatus = NumericUtils.obj2int(MapUtils.getValueByExpress(JsonUtils.toObj(openMsgSetup.getStrVal1(), Map.class), "employee_status"));
                employeeStatus = employeeStatus != 0 ? employeeStatus : EmployeeStatusEnum.ACTIVE.getStatus();
                virtualCardInfoReqDTO.setEmployeeStatus(employeeStatus);
            } else {
                virtualCardInfoReqDTO.setEmployeeStatus(EmployeeStatusEnum.ACTIVE.getStatus());
            }
        }
        if (ObjectUtils.isEmpty(virtualCardInfoReqDTO.getCompanyStatus())) {
            if (!ObjectUtils.isEmpty(openMsgSetup)) {
                int companyStatus = NumericUtils.obj2int(MapUtils.getValueByExpress(JsonUtils.toObj(openMsgSetup.getStrVal1(), Map.class), "company_status"));
                if (companyStatus != 0) {
                    virtualCardInfoReqDTO.setCompanyStatus(companyStatus);
                }
            } else {
                virtualCardInfoReqDTO.setCompanyStatus(EmployeeStatusEnum.ACTIVE.getStatus());
            }
        }
    }
}

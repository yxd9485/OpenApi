package com.fenbeitong.openapi.plugin.func.apply.service;

import com.fenbeitong.bank.api.model.BankHuPoRefundCreditOpenApiDTO;
import com.fenbeitong.bank.api.model.BankHuPoRefundCreditRespDTO;
import com.fenbeitong.fenbeipay.api.constant.enums.bank.BankCardStatus;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.apply.dto.VirtualCardApplyReqDTO;
import com.fenbeitong.openapi.plugin.func.apply.dto.VirtualCardRefundReqDTO;
import com.fenbeitong.openapi.plugin.func.apply.dto.VirtualCardRefundResDTO;
import com.fenbeitong.openapi.plugin.func.common.FuncIdTypeEnums;
import com.fenbeitong.openapi.plugin.func.common.FuncResponseCode;
import com.fenbeitong.openapi.plugin.func.employee.service.FuncEmployeeService;
import com.fenbeitong.openapi.plugin.func.exception.OpenApiFuncException;
import com.fenbeitong.openapi.plugin.func.virtualcard.dto.VirtualCardPersonalAccountReqDTO;
import com.fenbeitong.openapi.plugin.func.virtualcard.dto.VirtualCardPersonalAccountResDTO;
import com.fenbeitong.openapi.plugin.func.virtualcard.service.VirtualCardPersonalService;
import com.fenbeitong.openapi.plugin.support.apply.dto.VirtualCardApplyReqDTO.VirtualCardApply;
import com.fenbeitong.openapi.plugin.support.apply.service.AbstractVirtualCardApplyService;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.init.dto.UcEmployeeDetailDTO;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeExtServiceImpl;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 虚拟卡审批
 * Created by log.chang on 2020/4/27.
 */
@ServiceAspect
@Service
@Slf4j
public class FuncVirtualCardApplyService extends AbstractVirtualCardApplyService {

    @Autowired
    private FuncEmployeeService funcEmployeeService;
    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;
    @Autowired
    private OpenEmployeeExtServiceImpl employeeExtService;
    @Autowired
    private RestHttpUtils httpUtils;
    @Value("${host.usercenter}")
    private String ucHost;
    @Autowired
    private VirtualCardPersonalService virtualCardPersonalService;

    public Object createVirtualCardApply(String companyId, String employeeId, String employeeType, VirtualCardApplyReqDTO req) throws BindException {
        String token = funcEmployeeService.getEmployeeFbToken(companyId, employeeId, employeeType);
        //参数校验
        ValidatorUtils.validateBySpring(req);
        if (StringUtils.isBlank(token)) {
            throw new OpenApiPluginSupportException(NumericUtils.obj2int(SupportRespCode.THIRD_USER_NOT_FOUNT));
        }
        List<OpenMsgSetup> virtualDefaultSetting = openMsgSetupDao.listByCompanyIdAndItemCodeList(companyId, Lists.newArrayList("virtual_default_setting"));
        OpenMsgSetup openMsgSetup = null;
        if (!ObjectUtils.isEmpty(virtualDefaultSetting)) {
            openMsgSetup = virtualDefaultSetting.get(0);
        }
        if (ObjectUtils.isEmpty(req.getType())) {
            //类型为空时从配置表中取默认值
            if (ObjectUtils.isEmpty(openMsgSetup)) {
                throw new OpenApiPluginSupportException(NumericUtils.obj2int(SupportRespCode.VIRTUAL_CARD_TYPE_NOT_NULL));
            }
            if (StringUtils.isEmpty(openMsgSetup.getStrVal1())) {
                throw new OpenApiPluginSupportException(NumericUtils.obj2int(SupportRespCode.VIRTUAL_CARD_TYPE_NOT_NULL));
            }
            Object typeObj = MapUtils.getValueByExpress(JsonUtils.toObj(openMsgSetup.getStrVal1(), Map.class), "type");
            if(ObjectUtils.isEmpty(typeObj)){
                throw new OpenApiPluginSupportException(NumericUtils.obj2int(SupportRespCode.VIRTUAL_CARD_TYPE_NOT_NULL));
            }
            req.setType(NumericUtils.obj2int(typeObj));
        }
        if (ObjectUtils.isEmpty(req.getBankName())) {
            //银行名称为空时从配置表中取默认值
            if (ObjectUtils.isEmpty(openMsgSetup)) {
                throw new OpenApiPluginSupportException(NumericUtils.obj2int(SupportRespCode.VIRTUAL_CARD_BANKNAME_NOT_NULL));
            }
            if (StringUtils.isEmpty(openMsgSetup.getStrVal1())) {
                throw new OpenApiPluginSupportException(NumericUtils.obj2int(SupportRespCode.VIRTUAL_CARD_BANKNAME_NOT_NULL));
            }
            String bankName = com.fenbeitong.openapi.plugin.util.StringUtils.obj2str(MapUtils.getValueByExpress(JsonUtils.toObj(openMsgSetup.getStrVal1(), Map.class), "bank_name"));
            req.setBankName(bankName);
        }

        // 协合运维费用归属信息为空时获取人员的部门信息
        boolean missParameter = StringUtils.isEmpty(req.getCostAttributionId()) || StringUtils.isEmpty(req.getCostAttributionName()) || StringUtils.isEmpty(String.valueOf(req.getCostAttributionCategory()));
        if (missParameter && !ObjectUtils.isEmpty(virtualDefaultSetting)) {
            Integer type = null;
            Integer userType = null;
            if (String.valueOf(FuncIdTypeEnums.FB_ID.getKey()).equals(employeeType)) {
                //分贝通用户
                type = 0;
                userType = 1;
            } else if (String.valueOf(FuncIdTypeEnums.THIRD_ID.getKey()).equals(employeeType)) {
                //三方用户
                type = 1;
                userType = 2;
            }
            UcEmployeeDetailDTO ucEmployeeDetailInfo = loadUserData(companyId, employeeId, type, userType);
            if (!ObjectUtils.isEmpty(ucEmployeeDetailInfo) && !ObjectUtils.isEmpty(ucEmployeeDetailInfo.getEmployee())) {
                String orgUnitId = ucEmployeeDetailInfo.getEmployee().getOrg_unit_id();
                String orgUnitName = ucEmployeeDetailInfo.getEmployee().getOrg_unit_name();
                // 部门
                req.setCostAttributionCategory(1);
                req.setCostAttributionId(orgUnitId);
                req.setCostAttributionName(orgUnitName);
            } else {
                log.info("人员的部门查询失败，费用归属设置失败");
                throw new FinhubException(500, "人员的部门查询查询失败，获取费用归属信息失败");
            }
        } else if (StringUtils.isEmpty(req.getCostAttributionId()) && ObjectUtils.isEmpty(virtualDefaultSetting)) {
            log.info("申请额度费用归属id为空");
            throw new OpenApiPluginSupportException(NumericUtils.obj2int(SupportRespCode.COST_ATTRIBUTION_ID_NOT_NULL));
        } else if (StringUtils.isEmpty(req.getCostAttributionName()) && ObjectUtils.isEmpty(virtualDefaultSetting)) {
            log.info("申请额度费用归属name为空");
            throw new OpenApiPluginSupportException(NumericUtils.obj2int(SupportRespCode.COST_ATTRIBUTION_NAME_NOT_NULL));
        } else if (req.getCostAttributionCategory() == null && ObjectUtils.isEmpty(virtualDefaultSetting)) {
            log.info("申请额度费用归属category为空");
            throw new OpenApiPluginSupportException(NumericUtils.obj2int(SupportRespCode.COST_ATTRIBUTION_CATEGORY_NOT_NULL));
        }

        com.fenbeitong.openapi.plugin.support.apply.dto.VirtualCardApplyReqDTO fbtApplyReq = com.fenbeitong.openapi.plugin.support.apply.dto.VirtualCardApplyReqDTO.builder()
            .apply(buildFbtApplyReqApplyInfo(req))
            .tripList(buildFbtApplyReqTripInfo(req)).build();
        return createFbtVirtualCardApply(token, fbtApplyReq);
    }

    /**
     * 构建申请单费用归属信息
     */
    private List<com.fenbeitong.openapi.plugin.support.apply.dto.VirtualCardApplyReqDTO.VirtualCardCostAttributionEntity> buildFbtApplyReqTripInfo(VirtualCardApplyReqDTO req) {
        com.fenbeitong.openapi.plugin.support.apply.dto.VirtualCardApplyReqDTO.VirtualCardCostAttribution virtualCardCostAttribution = com.fenbeitong.openapi.plugin.support.apply.dto.VirtualCardApplyReqDTO.VirtualCardCostAttribution.builder()
            .category(req.getCostAttributionCategory())
            .id(req.getCostAttributionId())
            .name(req.getCostAttributionName()).build();
        com.fenbeitong.openapi.plugin.support.apply.dto.VirtualCardApplyReqDTO.VirtualCardCostAttributionEntity entity = com.fenbeitong.openapi.plugin.support.apply.dto.VirtualCardApplyReqDTO.VirtualCardCostAttributionEntity.builder()
            .costAttributionName(virtualCardCostAttribution).build();
        List<com.fenbeitong.openapi.plugin.support.apply.dto.VirtualCardApplyReqDTO.VirtualCardCostAttributionEntity> list = new ArrayList<>();
        list.add(entity);
        return list;
    }

    /**
     * 构建申请单基础信息
     */
    private VirtualCardApply buildFbtApplyReqApplyInfo(VirtualCardApplyReqDTO req) {
        return VirtualCardApply.builder()
            .applyReasonDesc(req.getReasonDesc())
            .budget(req.getBudget())
            .thirdId(req.getApplyId())
            .bankName(req.getBankName())
            .costAttributionCategory(req.getCostAttributionCategory())
            .costAttributionId(req.getCostAttributionId())
            .costAttributionNname(req.getCostAttributionName())
            .type(req.getType())
            .subType(req.getSubType()).build();

    }

    public UcEmployeeDetailDTO loadUserData(String companyId, String thirdEmpId, Integer type, Integer userType) {
        Map<String, Object> jsonMap = Maps.newHashMap();
        jsonMap.put("companyId", companyId);
        jsonMap.put("type", type);
        jsonMap.put("employeeId", thirdEmpId);
        jsonMap.put("userType", userType);
        String result = httpUtils.postJson(ucHost + "/uc/inner/employee/third/operate/info", JsonUtils.toJson(jsonMap));
        Map<String, Object> resultMap = JsonUtils.toObj(result, Map.class);
        return resultMap == null ? null : JsonUtils.toObj(JsonUtils.toJson(MapUtils.getValueByExpress(resultMap, "data")), UcEmployeeDetailDTO.class);
    }

    public Object refundVirtualCardCredit(String companyId, VirtualCardRefundReqDTO virtualCardRefundReqDTO) throws BindException, InvocationTargetException, IllegalAccessException {
        //参数校验
        ValidatorUtils.validateBySpring(virtualCardRefundReqDTO);
        virtualCardRefundReqDTO.setCompanyId(companyId);

        //三方人员id转换
        UcEmployeeDetailDTO ucEmployeeDetailInfo = loadUserData(companyId, virtualCardRefundReqDTO.getThirdEmployeeId(),1,2);
        if(ObjectUtils.isEmpty(ucEmployeeDetailInfo) || ObjectUtils.isEmpty(ucEmployeeDetailInfo.getEmployee())){
            log.info("员工不存在，企业id:{},三方人员id:{}",companyId,virtualCardRefundReqDTO.getThirdEmployeeId());
            throw new OpenApiFuncException(NumericUtils.obj2int(FuncResponseCode.QUERY_THIRD_USER_ERROR));
        }

        //查询个人虚拟卡账户信息
        VirtualCardPersonalAccountReqDTO personalAccountReqDTO  = new VirtualCardPersonalAccountReqDTO();
        personalAccountReqDTO.setThirdEmployeeId(virtualCardRefundReqDTO.getThirdEmployeeId());
        List<VirtualCardPersonalAccountResDTO> personalAccountList = virtualCardPersonalService.listPersonalAccountInfo(personalAccountReqDTO, companyId);
        if(ObjectUtils.isEmpty(personalAccountList)){
            log.info("获取个人账户信息失败，三方用户id：{},公司id:{}",virtualCardRefundReqDTO.getThirdEmployeeId(),companyId);
            throw new OpenApiFuncException(-9999,"获取个人账户信息失败");
        }
        //筛选开卡状态
        VirtualCardPersonalAccountResDTO virtualCardPersonalRes = personalAccountList.stream().filter(p -> BankCardStatus.NORMAL.getKey()==p.getCardStatus()).findFirst().orElse(null);
        if(ObjectUtils.isEmpty(virtualCardPersonalRes)){
            log.info("虚拟卡状态异常,三方用户id：{},公司id:{}",virtualCardRefundReqDTO.getThirdEmployeeId(),companyId);
            throw new OpenApiFuncException(-9999,"虚拟卡状态异常，请检查");
        }

        BankHuPoRefundCreditOpenApiDTO bankHuPoRefundCredit = new BankHuPoRefundCreditOpenApiDTO();
        BeanUtils.copyProperties(virtualCardRefundReqDTO,bankHuPoRefundCredit);
        bankHuPoRefundCredit.setEmployeeId(ucEmployeeDetailInfo.getEmployee().getId());
        bankHuPoRefundCredit.setFbCardNo(virtualCardPersonalRes.getBankAccountNo());
        BankHuPoRefundCreditRespDTO bankHuPoRefundCreditRespDTO = refundVirtualCardAmount(bankHuPoRefundCredit);

        VirtualCardRefundResDTO virtualCardRefundResDTO = new VirtualCardRefundResDTO();
        BeanUtils.copyProperties(bankHuPoRefundCreditRespDTO,virtualCardRefundResDTO);
        virtualCardRefundResDTO.setThirdEmployeeId(virtualCardRefundReqDTO.getThirdEmployeeId());
        return virtualCardRefundResDTO;
    }

}

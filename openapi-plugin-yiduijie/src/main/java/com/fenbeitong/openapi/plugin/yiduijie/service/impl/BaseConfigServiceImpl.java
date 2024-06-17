package com.fenbeitong.openapi.plugin.yiduijie.service.impl;

import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.yiduijie.common.YiDuiJieApiResponseCode;
import com.fenbeitong.openapi.plugin.yiduijie.constant.*;
import com.fenbeitong.openapi.plugin.yiduijie.dao.YiDuiJieConfDao;
import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJieBaseResp;
import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJieListConfigResp;
import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJieListExtConfigResp;
import com.fenbeitong.openapi.plugin.yiduijie.entity.YiDuiJieConf;
import com.fenbeitong.openapi.plugin.yiduijie.exception.OpenApiYiDuiJieException;
import com.fenbeitong.openapi.plugin.yiduijie.model.config.BillConfigProjectDeptDTO;
import com.fenbeitong.openapi.plugin.yiduijie.model.config.BillConfigReqDTO;
import com.fenbeitong.openapi.plugin.yiduijie.model.config.BillConfigTaxCalcDTO;
import com.fenbeitong.openapi.plugin.yiduijie.model.config.ConfigDTO;
import com.fenbeitong.openapi.plugin.yiduijie.sdk.YiDuiJieConfigApi;
import com.fenbeitong.openapi.plugin.yiduijie.service.IYiDuiJieTokenService;
import com.fenbeitong.openapi.plugin.yiduijie.service.config.IConfigService;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * <p>Title: BaseConfigServiceImpl</p>
 * <p>Description: 配置服务实现</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/16 10:41 AM
 */
@ServiceAspect
@Service
public class BaseConfigServiceImpl extends BaseYiDuiJieService implements IConfigService {

    @Autowired
    private YiDuiJieConfDao yiDuiJieConfDao;

    @Autowired
    private IYiDuiJieTokenService yiDuiJieTokenService;

    @Autowired
    private YiDuiJieConfigApi yiDuiJieConfigApi;

    @Override
    public void setConfig(String companyId, Map<String, Object> configMap) {
        YiDuiJieConf yiDuiJieConf = yiDuiJieConfDao.getByCompanyId(companyId);
        if (yiDuiJieConf == null) {
            return;
        }
        String token = yiDuiJieTokenService.getYiDuiJieToken();
        YiDuiJieBaseResp setConfigResp = yiDuiJieConfigApi.setConfig(token, yiDuiJieConf.getAppId(), configMap);
        if (setConfigResp == null || !setConfigResp.success()) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.MODIFY_CONFIG_ERROR)));
        }
    }

    @Override
    public void setCreateVoucherConfig(String companyId, int createType, int taxType) {
        YiDuiJieConf yiDuiJieConf = yiDuiJieConfDao.getByCompanyId(companyId);
        if (yiDuiJieConf == null) {
            return;
        }
        String token = yiDuiJieTokenService.getYiDuiJieToken();
        Map<String, Object> configMap = Maps.newHashMap();
        configMap.put(VoucherCreateType.getConfigName(), VoucherCreateType.getVoucherCreateType(createType).getValue());
        configMap.put(VoucherTaxType.getConfigName(), VoucherTaxType.getVoucherTaxType(taxType).getValue());
        YiDuiJieBaseResp setConfigResp = yiDuiJieConfigApi.setConfig(token, yiDuiJieConf.getAppId(), configMap);
        if (setConfigResp == null || !setConfigResp.success()) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.MODIFY_VOUCHER_CONFIG_ERROR)));
        }
    }

    @Override
    public List<ConfigDTO> listConfig(String companyId) {
        YiDuiJieConf yiDuiJieConf = yiDuiJieConfDao.getByCompanyId(companyId);
        if (yiDuiJieConf == null) {
            return Lists.newArrayList();
        }
        String token = yiDuiJieTokenService.getYiDuiJieToken();
        YiDuiJieListConfigResp listConfigResp = yiDuiJieConfigApi.listConfig(token, yiDuiJieConf.getAppId());
        if (listConfigResp == null || !listConfigResp.success()) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.LIST_CONFIG_ERROR)));
        }
        return listConfigResp == null || listConfigResp.getConfigList() == null ? Lists.newArrayList() : listConfigResp.getConfigList();
    }

    @Override
    public void setExtConfig(String companyId, String config) {
        YiDuiJieConf yiDuiJieConf = yiDuiJieConfDao.getByCompanyId(companyId);
        if (yiDuiJieConf == null) {
            return;
        }
        String token = yiDuiJieTokenService.getYiDuiJieToken();
        YiDuiJieBaseResp extConfigResp = yiDuiJieConfigApi.setExtConfig(token, yiDuiJieConf.getAppId(), config);
        if (extConfigResp == null || !extConfigResp.success()) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.MODIFY_EXT_CONFIG_ERROR)));
        }
    }

    @Override
    public String listExtConfig(String companyId) {
        YiDuiJieConf yiDuiJieConf = yiDuiJieConfDao.getByCompanyId(companyId);
        if (yiDuiJieConf == null) {
            return "";
        }
        String token = yiDuiJieTokenService.getYiDuiJieToken();
        YiDuiJieListExtConfigResp listExtConfigResp = yiDuiJieConfigApi.listExtConfig(token, yiDuiJieConf.getAppId());
        if (listExtConfigResp == null || !listExtConfigResp.success()) {
            handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.LIST_EXT_CONFIG_ERROR)));
        }
        return listExtConfigResp.getBody();
    }

    @Override
    public void setAccountConfig(String companyId, int accountType, String accountName) {
        YiDuiJieConf yiDuiJieConf = yiDuiJieConfDao.getByCompanyId(companyId);
        if (yiDuiJieConf == null) {
            return;
        }
        String token = yiDuiJieTokenService.getYiDuiJieToken();
        if (accountType == 2) {
            Map<String, Object> taxAccountNameMap = Maps.newHashMap();
            taxAccountNameMap.put("taxAccountName", accountName);
            YiDuiJieBaseResp yiDuiJieBaseResp = yiDuiJieConfigApi.setConfig(token, yiDuiJieConf.getAppId(), taxAccountNameMap);
            if (yiDuiJieBaseResp == null || !yiDuiJieBaseResp.success()) {
                handlerException(new OpenApiYiDuiJieException(NumericUtils.obj2int(YiDuiJieApiResponseCode.LIST_EXT_CONFIG_ERROR)));
            }
        } else if (accountType == 3) {
            String config = String.format("rule:090;(vitem.debit=='0'):vitem.accountName='%s'\r\nrule:180;(vitem.summary==null):vitem.summary=item.summary", accountName);
            setExtConfig(companyId, config);
        }
    }

    @Override
    public void setBillConfig(BillConfigReqDTO req) {
        //检查参数
        checkReqParam(Lists.newArrayList(req));
        Map<String, Object> configMap = Maps.newHashMap();
        //公司id
        String companyId = req.getCompanyId();
        //人员选择 1:预订人 2:实际使用人
        Integer employeeSource = req.getEmployeeSource();
        if (employeeSource != null) {
            EmployeeSource employeeSourceEnum = EmployeeSource.getEmployeeSource(employeeSource);
            if (employeeSourceEnum != null) {
                configMap.put(EmployeeSource.getConfigName(), employeeSourceEnum.getValue());
            }
        }
        //部门选择 1:费用归属 部门
        Integer departmentSource = req.getDepartmentSource();
        if (departmentSource != null) {
            DepartmentSource departmentSourceEnum = DepartmentSource.getDepartmentSource(departmentSource);
            if (departmentSourceEnum != null) {
                configMap.put(DepartmentSource.getConfigName(), departmentSourceEnum.getValue());
            }
        }
        //项目费用固定核算部门列表
        List<BillConfigProjectDeptDTO> projectDeptList = req.getProjectDeptList();
        if (!ObjectUtils.isEmpty(projectDeptList)) {
            projectDeptList.forEach(projectDept -> {
                configMap.put("project-department;" + projectDept.getProject(), projectDept.getDepartment());
            });
        }
        //服务单是否独核算 0:服务不单独核算 1:服务单独核算
        Integer treatmentOfFee = req.getTreatmentOfFee();
        if (treatmentOfFee != null) {
            TreatmentOfFee treatmentOfFeeEnum = TreatmentOfFee.getTreatmentOfFee(treatmentOfFee);
            if (treatmentOfFeeEnum != null) {
                configMap.put(TreatmentOfFee.getConfigName(), treatmentOfFeeEnum.getValue());
            }
        }
        //服务费借方科目名称
        String feeCredit = req.getFeeCredit();
        if (!ObjectUtils.isEmpty(feeCredit)) {
            configMap.put("feeCredit", feeCredit);
        }
        //服务费进项税科目映射
        String treatmentOfTaxFee = req.getTreatmentOfTaxFee();
        if (!ObjectUtils.isEmpty(treatmentOfTaxFee)) {
            configMap.put("treatmentOfTax;fee", treatmentOfTaxFee);
        }
        //业务线进行税科目映射
        String treatmentOfTaxDefault = req.getTreatmentOfTaxDefault();
        if (!ObjectUtils.isEmpty(treatmentOfTaxDefault)) {
            configMap.put("treatmentOfTax;default", treatmentOfTaxDefault);
        }
        //贷方科目映射
        String defaultCredit = req.getDefaultCredit();
        if (!ObjectUtils.isEmpty(defaultCredit)) {
            configMap.put("defaultCredit", defaultCredit);
        }
        //凭证制单人
        String defaultmaker = req.getDefaultmaker();
        if (!ObjectUtils.isEmpty(defaultmaker)) {
            configMap.put("defaultmaker", defaultmaker);
        }
        //企业账单进项税规则配置列表
        List<BillConfigTaxCalcDTO> taxCalcDtoList = req.getTaxCalcDtoList();
        if (!ObjectUtils.isEmpty(taxCalcDtoList)) {
            taxCalcDtoList.forEach(taxCalcDto -> {
                String key = "taxRule;" + taxCalcDto.getBusinessType();
                String value = (taxCalcDto.getCalcTax() ? "是" : "否") + "," + NumericUtils.obj2int(taxCalcDto.getTaxRate());
                if (!ObjectUtils.isEmpty(taxCalcDto.getFbtSupplierCode())) {
                    value = value + "," + taxCalcDto.getFbtSupplierCode();
                }
                configMap.put(key, value);
            });
        }
        //分贝通在客户系统的供应商代码，当应付科目启用供应商辅助核算时会自动填上
        String supplierCode = req.getSupplierCode();
        if (!ObjectUtils.isEmpty(supplierCode)) {
            configMap.put("supplierCode", supplierCode);
        }
        //人员选择 1：外部人员不抵扣 2：参与抵扣
        Integer externalPersonTaxReduce = req.getExternalPersonTaxReduce();
        if (externalPersonTaxReduce != null) {
            ExternalPersonTaxReduce externalPersonTaxReduceEnum = ExternalPersonTaxReduce.getExternalPersonTaxReduce(externalPersonTaxReduce);
            if (externalPersonTaxReduceEnum != null) {
                configMap.put(ExternalPersonTaxReduce.getConfigName(), externalPersonTaxReduceEnum.getValue());
            }
        }
        //不参与费用抵扣的部门，可以多个，用英文逗号分隔
        String departmentTaxReduce = req.getDepartmentTaxReduce();
        configMap.put("taxReduceExclude;department", departmentTaxReduce);
        //不参与费用抵扣的项目，可以多个，用英文逗号分隔
        String projectTaxReduce = req.getProjectTaxReduce();
        configMap.put("taxReduceExclude;project", projectTaxReduce);
        //合并 1:根据辅助核算自动合并 2:生成明细
        Integer mergingOfExpense = req.getMergingOfExpense();
        if (mergingOfExpense != null) {
            MergingOfExpense mergingOfExpenseEnum = MergingOfExpense.getMergingOfExpense(mergingOfExpense);
            if (mergingOfExpenseEnum != null) {
                configMap.put(MergingOfExpense.getConfigName(), mergingOfExpenseEnum.getValue());
            }
        }
        //1:按照业务线计算进项税 2:所有进项税合并到一行
        Integer mergingOfTax = req.getMergingOfTax();
        if (mergingOfTax != null) {
            MergingOfTax mergingOfTaxEnum = MergingOfTax.getMergingOfTax(mergingOfTax);
            if (mergingOfTaxEnum != null) {
                configMap.put(MergingOfTax.getConfigName(), mergingOfTaxEnum.getValue());
            }
        }
        if (!ObjectUtils.isEmpty(configMap)) {
            setConfig(companyId, configMap);
        }
    }

    @Override
    public void setMappingOrder(String companyId, String mappingOrder) {
        Map<String, Object> configMap = Maps.newHashMap();
        configMap.put("mappingOrder", mappingOrder);
        setConfig(companyId, configMap);
    }
}

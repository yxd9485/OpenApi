package com.fenbeitong.openapi.plugin.voucher.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.finhub.common.constant.CompanyLoginChannelEnum;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.voucher.dto.*;
import com.fenbeitong.openapi.plugin.voucher.service.IFinanceConfigService;
import com.fenbeitong.openapi.plugin.voucher.service.IFinanceInitConfigService;
import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.fenbeitong.usercenter.api.model.enums.common.IdTypeEnums;
import com.fenbeitong.usercenter.api.model.enums.common.SourceEnums;
import com.fenbeitong.usercenter.api.service.auth.IAuthService;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>Title: FinanceInitConfigServiceImpl</p>
 * <p>Description: 初始化财务配置类</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/29 11:37 AM
 */
@Slf4j
@ServiceAspect
@Service
public class FinanceInitConfigServiceImpl implements IFinanceInitConfigService {

    @Autowired
    private IFinanceConfigService financeConfigService;

    @DubboReference(check = false)
    private IBaseEmployeeExtService employeeExtService;

    @DubboReference(check = false)
    private IAuthService authService;

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;

    @Override
    public FinanceGlobalConfigDto initConfig(String companyId, String operatorId) {
        FinanceGlobalConfigDto globalConfigDto = new FinanceGlobalConfigDto();
        EmployeeContract employee = employeeExtService.queryEmployeeInfo(operatorId, companyId);
        LoginResVO loginRes = authService.loginAuthInitV5(companyId, employee.getId(), employee.getPhone_num(), IdTypeEnums.FB_ID.getKey(), SourceEnums.OPENAPI.getKey(), CompanyLoginChannelEnum.OPENAPI.getPlatform(), CompanyLoginChannelEnum.OPENAPI.getEntrance());
        String token = loginRes.getLogin_info().getToken();
        AtomicInteger threadId = new AtomicInteger(1);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(12, 12, 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(), (r) -> new Thread(r, "financeConfigThreadPool-" + threadId.getAndIncrement()));
        try {
            //财务配置
            CompletableFuture<FinanceConfigDto> financeConfigFuture = CompletableFuture.supplyAsync(() -> financeConfigService.financeConfig(companyId, token), executor);
            //项目映射
            CompletableFuture<List<FinanceProjectMappingDto>> projectMappingFuture = CompletableFuture.supplyAsync(() -> financeConfigService.listProjectMapping(companyId, token), executor);
            //部门映射
            CompletableFuture<List<FinanceDeptMappingDto>> deptMappingFuture = CompletableFuture.supplyAsync(() -> financeConfigService.listDeptMapping(companyId, token), executor);
            //科目清单
            CompletableFuture<List<FinanceCourseDto>> courseFuture = CompletableFuture.supplyAsync(() -> financeConfigService.listCourse(companyId, token), executor);
            //账单业务线借方科目映射
            CompletableFuture<List<FinanceBillBizDebtorCourseMappingDto>> bizDebtorCourseMappingFuture = CompletableFuture.supplyAsync(() -> financeConfigService.listBillBizDebtorCourseMapping(companyId, token), executor);
            //虚拟卡核销单借方科目映射
            CompletableFuture<List<VirtualCardDebtorCourseMappingDto>> vcDebtorCourseMappingFuture = CompletableFuture.supplyAsync(() -> financeConfigService.listVirtualCardDebtorCourseMapping(companyId, token), executor);
            //单项科目映射
            CompletableFuture<Map<String, FinanceCourseDto>> singleCourseMappingFuture = CompletableFuture.supplyAsync(() -> financeConfigService.getSingleCourseMapping(companyId, token), executor);
            //账单进项税规则
            CompletableFuture<List<FinanceBillTaxRuleDto>> financeBillTaxRuleFuture = CompletableFuture.supplyAsync(() -> financeConfigService.listBillTaxRule(companyId, token), executor);
            //不计税部门及项目
            CompletableFuture<FinanceBillExcludeTaxDto> financeBillExcludeTaxFuture = CompletableFuture.supplyAsync(() -> financeConfigService.getFinanceBillExcludeTaxDto(companyId, token), executor);
            //虚拟卡发票税率配置
            CompletableFuture<List<VirtualCardTaxRateDto>> vcTaxRateFuture = CompletableFuture.supplyAsync(() -> financeConfigService.listVirtualCardTaxRate(companyId, token), executor);
            //虚拟卡抵扣类型配置
            CompletableFuture<List<VirtualCardDeductionTypeDto>> vcDeductionTypeFuture = CompletableFuture.supplyAsync(() -> financeConfigService.listVirtualCardDeductionType(companyId, token), executor);
            //不计税部门及项目
            CompletableFuture<FinanceVoucherManageDto> financeVoucherManageFuture = CompletableFuture.supplyAsync(() -> financeConfigService.financeVoucherManage(companyId, token), executor);
            CompletableFuture.allOf(financeConfigFuture, projectMappingFuture, deptMappingFuture, courseFuture,
                    bizDebtorCourseMappingFuture, vcDebtorCourseMappingFuture, singleCourseMappingFuture,
                    financeBillTaxRuleFuture, financeBillExcludeTaxFuture, vcTaxRateFuture, vcDeductionTypeFuture, financeVoucherManageFuture).join();
            globalConfigDto.setOperatorId(operatorId);
            globalConfigDto.setOperatorName(employee.getName());
            globalConfigDto.setConfig_1(financeConfigFuture.get());
            globalConfigDto.setConfig_2(projectMappingFuture.get());
            globalConfigDto.setConfig_3(deptMappingFuture.get());
            globalConfigDto.setConfig_4(courseFuture.get());
            globalConfigDto.setConfig_5(bizDebtorCourseMappingFuture.get());
            globalConfigDto.setConfig_6(vcDebtorCourseMappingFuture.get());
            globalConfigDto.setConfig_7(singleCourseMappingFuture.get());
            globalConfigDto.setConfig_8(financeBillTaxRuleFuture.get());
            globalConfigDto.setConfig_9(financeBillExcludeTaxFuture.get());
            globalConfigDto.setConfig_10(vcTaxRateFuture.get());
            globalConfigDto.setConfig_11(vcDeductionTypeFuture.get());
            globalConfigDto.setConfig_12(financeVoucherManageFuture.get());
            List<OpenMsgSetup> queryEtlConfigList = openMsgSetupDao.listByCompanyIdAndItemCodeList(companyId, Lists.newArrayList("company_voucher_strategy_params"));
            OpenMsgSetup voucherParams = ObjectUtils.isEmpty(queryEtlConfigList) ? null : queryEtlConfigList.get(0);
            String voucherParamsJson = voucherParams == null ? null : voucherParams.getStrVal1();
            globalConfigDto.setExtConfig(JsonUtils.toObj(voucherParamsJson, Map.class));
        } catch (Exception e) {
            log.info("公司[" + companyId + "]加载凭证配置失败", e);
        } finally {
            executor.shutdown();
        }
        return globalConfigDto;
    }
}

package com.fenbeitong.openapi.plugin.voucher.service.impl;

import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.voucher.dto.*;
import com.fenbeitong.openapi.plugin.voucher.service.IFinanceConfigService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * <p>Title: FinanceConfigServiceImpl</p>
 * <p>Description: 财务配置实现类</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/28 8:34 PM
 */
@ServiceAspect
@Service
public class FinanceConfigServiceImpl implements IFinanceConfigService {

    @Value("${host.appgate}")
    private String appgateHost;

    @Override
    public List<FinanceProjectMappingDto> listProjectMapping(String companyId, String token) {
        String url = appgateHost + "/saas_plus/finance/costcenter/relation/list?pageSize=100&pageIndex=%d&costCenterName=&costCenterCode=&financeCostCenterCode=&status=1";
        int pageIndex = 1;
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", token);
        String result = RestHttpUtils.get(String.format(url, pageIndex), headers, null);
        FinanceProjectMappingRespDto resp = JsonUtils.toObj(result, FinanceProjectMappingRespDto.class);
        int total = resp == null || resp.getData() == null ? 0 : resp.getData().getTotal();
        List<FinanceProjectMappingDto> mappingList = Lists.newArrayList();
        List<FinanceProjectMappingDto> dataList = null;
        while (resp != null && total > 0 && resp.success() && total != mappingList.size() && !ObjectUtils.isEmpty((dataList = resp.getData().getDataList()))) {
            mappingList.addAll(dataList);
            result = RestHttpUtils.get(String.format(url, ++pageIndex), headers, null);
            resp = JsonUtils.toObj(result, FinanceProjectMappingRespDto.class);
        }
        return mappingList;
    }

    @Override
    public List<FinanceDeptMappingDto> listDeptMapping(String companyId, String token) {
        String url = appgateHost + "/saas_plus/finance/org_unit/list?page_index=%d&page_size=100&finance_dept=&fbt_dept=&dept_state=1&finance_dept_code=";
        int pageIndex = 1;
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", token);
        String result = RestHttpUtils.get(String.format(url, pageIndex), headers, null);
        FinanceDeptMappingRespDto resp = JsonUtils.toObj(result, FinanceDeptMappingRespDto.class);
        int total = resp == null || resp.getData() == null ? 0 : resp.getData().getTotalCount();
        List<FinanceDeptMappingDto> mappingList = Lists.newArrayList();
        List<FinanceDeptMappingDto> dataList = null;
        while (resp != null && total > 0 && resp.success() && total != mappingList.size() && !ObjectUtils.isEmpty((dataList = resp.getData().getOrgUnitList()))) {
            mappingList.addAll(dataList);
            result = RestHttpUtils.get(String.format(url, ++pageIndex), headers, null);
            resp = JsonUtils.toObj(result, FinanceDeptMappingRespDto.class);
        }
        return mappingList;
    }

    @Override
    public List<FinanceCourseDto> listCourse(String companyId, String token) {
        String url = appgateHost + "/saas_plus/invoice/finance_course/list?courseName=&courseCode=&page=%d&size=100";
        int pageIndex = 1;
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", token);
        String result = RestHttpUtils.get(String.format(url, pageIndex), headers, null);
        FinanceCourseRespDto resp = JsonUtils.toObj(result, FinanceCourseRespDto.class);
        int total = resp == null || resp.getData() == null ? 0 : resp.getData().getTotal();
        List<FinanceCourseDto> courseList = Lists.newArrayList();
        List<FinanceCourseDto> dataList = null;
        while (resp != null && total > 0 && resp.success() && total != courseList.size() && !ObjectUtils.isEmpty((dataList = resp.getData().getData()))) {
            courseList.addAll(dataList);
            result = RestHttpUtils.get(String.format(url, ++pageIndex), headers, null);
            resp = JsonUtils.toObj(result, FinanceCourseRespDto.class);
        }
        return courseList;
    }

    @Override
    public List<FinanceBillBizDebtorCourseMappingDto> listBillBizDebtorCourseMapping(String companyId, String token) {
        String url = appgateHost + "/saas_plus/finance/biz_debtor_course/query";
        int pageIndex = 1;
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", token);
        Map<String, Object> params = Maps.newHashMap();
        params.put("pageIndex", pageIndex);
        params.put("pageSize", 100);
        String result = RestHttpUtils.postJson(url, headers, JsonUtils.toJson(params));
        FinanceBillBizDebtorCourseMappingRespDto resp = JsonUtils.toObj(result, FinanceBillBizDebtorCourseMappingRespDto.class);
        int total = resp == null || resp.getData() == null ? 0 : resp.getData().getTotal();
        List<FinanceBillBizDebtorCourseMappingDto> mappingList = Lists.newArrayList();
        List<FinanceBillBizDebtorCourseMappingDto> dataList = null;
        while (resp != null && total > 0 && resp.success() && total != mappingList.size() && !ObjectUtils.isEmpty((dataList = resp.getData().getDataList()))) {
            mappingList.addAll(dataList);
            params.put("pageIndex", ++pageIndex);
            params.put("pageSize", 100);
            result = RestHttpUtils.postJson(url, headers, JsonUtils.toJson(params));
            resp = JsonUtils.toObj(result, FinanceBillBizDebtorCourseMappingRespDto.class);
        }
        return mappingList;
    }

    @Override
    public FinanceConfigDto financeConfig(String companyId, String token) {
        String url = appgateHost + "/saas_plus/finance/field/config/list";
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", token);
        String result = RestHttpUtils.get(url, headers, null);
        FinanceConfigRespDto resp = JsonUtils.toObj(result, FinanceConfigRespDto.class);
        FinanceConfigDto financeConfigDto = resp == null ? null : resp.getData();
        if (financeConfigDto != null) {
            FinanceConfigDto superMappingConfig = querySuperMappingConfig(companyId, token);
            if (superMappingConfig != null) {
                financeConfigDto.setPriorityMatch(superMappingConfig.getPriorityMatch());
            }
        }
        return financeConfigDto;
    }

    @Override
    public FinanceConfigDto querySuperMappingConfig(String companyId, String token) {
        String url = appgateHost + "/saas_plus/finance/biz_debtor_course/query_super_mapping_config";
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", token);
        String result = RestHttpUtils.get(url, headers, null);
        FinanceConfigRespDto resp = JsonUtils.toObj(result, FinanceConfigRespDto.class);
        return resp == null ? null : resp.getData();
    }

    @Override
    public List<VirtualCardDebtorCourseMappingDto> listVirtualCardDebtorCourseMapping(String companyId, String token) {
        String url = appgateHost + "/saas_plus/finance/debtor_course/query";
        int pageIndex = 1;
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", token);
        Map<String, Object> params = Maps.newHashMap();
        params.put("pageIndex", pageIndex);
        params.put("pageSize", 100);
        String result = RestHttpUtils.postJson(url, headers, JsonUtils.toJson(params));
        VirtualCardDebtorCourseMappingRespDto resp = JsonUtils.toObj(result, VirtualCardDebtorCourseMappingRespDto.class);
        int total = resp == null || resp.getData() == null ? 0 : resp.getData().getTotal();
        List<VirtualCardDebtorCourseMappingDto> mappingList = Lists.newArrayList();
        List<VirtualCardDebtorCourseMappingDto> dataList = null;
        while (resp != null && total > 0 && resp.success() && total != mappingList.size() && !ObjectUtils.isEmpty((dataList = resp.getData().getDataList()))) {
            mappingList.addAll(dataList);
            params.put("pageIndex", ++pageIndex);
            params.put("pageSize", 100);
            result = RestHttpUtils.postJson(url, headers, JsonUtils.toJson(params));
            resp = JsonUtils.toObj(result, VirtualCardDebtorCourseMappingRespDto.class);
        }
        return mappingList;
    }

    @Override
    public Map<String, FinanceCourseDto> getSingleCourseMapping(String companyId, String token) {
        Map<String, FinanceCourseDto> singleCourseMappingMap = Maps.newHashMap();
        String url = appgateHost + "/saas_plus/finance/debtor_course/info";
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", token);
        Map<String, Object> params = Maps.newHashMap();
        params.put("type", Lists.newArrayList(2, 3, 4, 5, 6, 7, 8));
        String result = RestHttpUtils.postJson(url, headers, JsonUtils.toJson(params));
        Map respMap = JsonUtils.toObj(result, Map.class);
        if (!ObjectUtils.isEmpty(respMap) && ((int) respMap.get("code")) == 0) {
            Map<String, Object> dataMap = (Map) respMap.get("data");
            for (String key : dataMap.keySet()) {
                singleCourseMappingMap.put(key, JsonUtils.toObj(JsonUtils.toJson(dataMap.get(key)), FinanceCourseDto.class));
            }
        }
        return singleCourseMappingMap;
    }

    @Override
    public List<FinanceBillTaxRuleDto> listBillTaxRule(String companyId, String token) {
        String url = appgateHost + "/saas_plus/invoice/biz_taxes/listBiz";
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", token);
        String result = RestHttpUtils.get(url, headers, null);
        FinanceBillTaxRuleRespDto resp = JsonUtils.toObj(result, FinanceBillTaxRuleRespDto.class);
        return resp == null ? null : resp.getData();
    }

    @Override
    public FinanceBillExcludeTaxDto getFinanceBillExcludeTaxDto(String companyId, String token) {
        String url = appgateHost + "/saas_plus/finance/org_cost/get";
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", token);
        String result = RestHttpUtils.get(url, headers, null);
        FinanceBillExcludeTaxRespDto resp = JsonUtils.toObj(result, FinanceBillExcludeTaxRespDto.class);
        return resp == null ? null : resp.getData();
    }

    @Override
    public List<VirtualCardTaxRateDto> listVirtualCardTaxRate(String companyId, String token) {
        String url = appgateHost + "/saas_plus/invoice/taxes/list";
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", token);
        String result = RestHttpUtils.get(url, headers, null);
        VirtualCardTaxRateRespDto resp = JsonUtils.toObj(result, VirtualCardTaxRateRespDto.class);
        return resp == null ? null : resp.getData();
    }

    @Override
    public List<VirtualCardDeductionTypeDto> listVirtualCardDeductionType(String companyId, String token) {
        String url = appgateHost + "/saas_plus/deduction/config/list?expense_category=&invoice_type=&page_index=%d&page_size=100";
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", token);
        int pageIndex = 1;
        String result = RestHttpUtils.get(String.format(url, pageIndex), headers, null);
        VirtualCardDeductionTypeRespDto resp = JsonUtils.toObj(result, VirtualCardDeductionTypeRespDto.class);
        int total = resp == null || resp.getData() == null ? 0 : resp.getData().getTotalCount();
        List<VirtualCardDeductionTypeDto> deductionTypeList = Lists.newArrayList();
        List<VirtualCardDeductionTypeDto> dataList = null;
        while (resp != null && total > 0 && resp.success() && total != deductionTypeList.size() && !ObjectUtils.isEmpty((dataList = resp.getData().getDeductionList()))) {
            deductionTypeList.addAll(dataList);
            result = RestHttpUtils.get(String.format(url, ++pageIndex), headers, null);
            resp = JsonUtils.toObj(result, VirtualCardDeductionTypeRespDto.class);
        }
        return deductionTypeList;
    }

    @Override
    public FinanceVoucherManageDto financeVoucherManage(String companyId, String token) {
        String url = appgateHost + "/saas_plus/invoice/finance_voucher_manage/query";
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", token);
        String result = RestHttpUtils.get(url, headers, null);
        FinanceVoucherManageRespDto resp = JsonUtils.toObj(result, FinanceVoucherManageRespDto.class);
        return resp == null ? null : resp.getData();
    }
}

package com.fenbeitong.openapi.plugin.voucher.utils;

import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.voucher.dto.FinanceBillBizDebtorCourseMappingDto;
import com.fenbeitong.openapi.plugin.voucher.dto.FinanceConfigDto;
import com.fenbeitong.openapi.plugin.voucher.dto.VirtualCardDebtorCourseMappingDto;
import com.google.common.collect.Lists;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Title: FinanceToolUtils</p>
 * <p>Description: 财务凭证工具</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/30 3:53 PM
 */
public class FinanceVoucherUtils {

    public static FinanceBillBizDebtorCourseMappingDto getBizDebtorCourseMappingProject(List<FinanceBillBizDebtorCourseMappingDto> bizDebtorCourseMappingDtoList, String deptName, String projectCode, String businessName) {
        FinanceBillBizDebtorCourseMappingDto result = null;
        if (!ObjectUtils.isEmpty(projectCode)) {
            bizDebtorCourseMappingDtoList = CollectionUtils.filterList(bizDebtorCourseMappingDtoList, "fieldInfoType", 2);
        }
        for (FinanceBillBizDebtorCourseMappingDto bizDebtorCourseMappingDto : bizDebtorCourseMappingDtoList) {
            if (!businessName.equals(bizDebtorCourseMappingDto.getBizName())) {
                continue;
            }
            List<String> orgUnitNameList = Lists.newArrayList(bizDebtorCourseMappingDto.getOrgUnitName().split(","));
            List<String> projectCodeList = ObjectUtils.isEmpty(bizDebtorCourseMappingDto.getFieldInfoName()) ? Lists.newArrayList() : Lists.newArrayList(bizDebtorCourseMappingDto.getFieldInfoName().split(","));
            if (orgUnitNameList.contains(deptName) && (projectCodeList.contains(projectCode) || ObjectUtils.isEmpty(projectCode))) {
                result = bizDebtorCourseMappingDto;
                break;
            }
        }
        return result;
    }

    public FinanceBillBizDebtorCourseMappingDto getBillBizCourseMapping(FinanceConfigDto financeConfigDto, List<FinanceBillBizDebtorCourseMappingDto> courseMappingList, String businessName, String deptName, String projectCode, String reasons) {
        FinanceBillBizDebtorCourseMappingDto result = null;
        Integer advancedMappingConfig = financeConfigDto.getAdvancedMappingConfig();
        //借方科目高级映射配置开关：0关，1开
        if (advancedMappingConfig == null || advancedMappingConfig == 0) {
            List<FinanceBillBizDebtorCourseMappingDto> courseMappingDtos = courseMappingList.stream().filter(courseMappingDto -> {
                if (!businessName.equals(courseMappingDto.getBizName())) {
                    return false;
                }
                //财务部门名称
                List<String> orgUnitNameList = Lists.newArrayList(courseMappingDto.getOrgUnitName().split(","));
                return orgUnitNameList.contains(deptName);
            }).collect(Collectors.toList());
            result = ObjectUtils.isEmpty(courseMappingDtos) ? null : courseMappingDtos.get(0);
        } else {
            Map<Integer, List<FinanceBillBizDebtorCourseMappingDto>> courseMappingMap = courseMappingList.stream().filter(courseMappingDto -> {
                if (!businessName.equals(courseMappingDto.getBizName())) {
                    return false;
                }
                //财务部门名称
                List<String> orgUnitNameList = Lists.newArrayList(courseMappingDto.getOrgUnitName().split(","));
                //财务项目名称/事由列表
                List<String> valueList = ObjectUtils.isEmpty(courseMappingDto.getFieldInfoName()) ? Lists.newArrayList() : Lists.newArrayList(courseMappingDto.getFieldInfoName().split(","));
                //先进行项目匹配
                boolean matchProject = courseMappingDto.getFieldInfoType() != null && courseMappingDto.getFieldInfoType() == 2 && (valueList.contains(projectCode) || ObjectUtils.isEmpty(projectCode));
                //再匹配事由
                boolean matchReason = courseMappingDto.getFieldInfoType() != null && courseMappingDto.getFieldInfoType() == 1 && (valueList.contains(reasons) || ObjectUtils.isEmpty(reasons));
                return orgUnitNameList.contains(deptName) && (matchProject || matchReason || courseMappingDto.getFieldInfoType() == null);
            }).collect(Collectors.groupingBy(f -> f.getFieldInfoType() == null ? 0 : f.getFieldInfoType()));
            //优先匹配字段：1事由，2项目
            int priorityMatch = financeConfigDto.getPriorityMatch() == null ? 2 : financeConfigDto.getPriorityMatch();
            //优先匹配事由
            boolean reasonCourseFirst = priorityMatch == 1;
            result = getCourseByReasonProjectMatch(reasonCourseFirst ? reasons : projectCode, courseMappingMap, reasonCourseFirst ? 1 : 2);
            if (result == null) {
                result = getCourseByReasonProjectMatch(reasonCourseFirst ? projectCode : reasons, courseMappingMap, reasonCourseFirst ? 2 : 1);
            }
            if (result == null) {
                result = getCourseByReasonProjectMatch(null, courseMappingMap, 0);
            }
        }
        return result;
    }

    private FinanceBillBizDebtorCourseMappingDto getCourseByReasonProjectMatch(String refValue, Map<Integer, List<FinanceBillBizDebtorCourseMappingDto>> courseMappingMap, Integer priorityMatch) {
        FinanceBillBizDebtorCourseMappingDto result;
        List<FinanceBillBizDebtorCourseMappingDto> matchCourseList = courseMappingMap.get(priorityMatch);
        if (matchCourseList == null) {
            return null;
        }
        List<FinanceBillBizDebtorCourseMappingDto> realMatchMappingList = matchCourseList.stream().filter(courseMappingDto -> {
            boolean refValueNull = ObjectUtils.isEmpty(refValue);
            boolean mappingRefValueNull = ObjectUtils.isEmpty(courseMappingDto.getFieldInfoName());
            return (refValueNull && mappingRefValueNull) || (!refValueNull && !mappingRefValueNull);
        }).collect(Collectors.toList());
        result = ObjectUtils.isEmpty(realMatchMappingList) ? null : realMatchMappingList.get(0);
        return result;
    }

    public static VirtualCardDebtorCourseMappingDto getBillDebtorCourseMapping(List<VirtualCardDebtorCourseMappingDto> mappingList, String deptName, String businessName, Integer billType) {
        VirtualCardDebtorCourseMappingDto result = null;
        if (!ObjectUtils.isEmpty(mappingList)) {
            for (VirtualCardDebtorCourseMappingDto mapping : mappingList) {
                if (mapping.getBillType().equals(billType)) {
                    List<String> orgUnitNameList = Lists.newArrayList(mapping.getOrgUnitName().split(","));
                    if (orgUnitNameList.contains(deptName) && mapping.getCostCategory().equals(businessName)) {
                        result = mapping;
                        break;
                    }
                }
            }
        }
        return result;
    }
}

package com.fenbeitong.openapi.plugin.beisen.standard.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenEmpOrgDTO;
import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenParamConfig;
import com.fenbeitong.openapi.plugin.beisen.standard.service.third.BeisenApiService;
import com.fenbeitong.openapi.plugin.support.employee.service.IEmployeeRankTemplateService;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenCustomizeConfigDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.usercenter.api.model.dto.rule.EmployeeAuthRankDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName BeisenRankListener
 * @Description 获取北森员工机构，并同步到分贝通员工职级上
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/8/17
 **/
@Service
@Slf4j
public class BeisenRankListener extends DefaultOrgListener {
    @Autowired
    OpenCustomizeConfigDao openCustomizeConfigDao;
    @Autowired
    IEmployeeRankTemplateService employeeRankTemplateService;
    @Autowired
    BeisenApiService beisenApiService;

    @Override
    public List<OpenThirdEmployeeDTO> filterOpenThirdOrgUnitDtoBefore(List<OpenThirdEmployeeDTO> openThirdOrgUnitDTOList, String companyId, BeisenParamConfig beisenParamConfig, Object... objects) {
        //1、查询所有员工的任职机构
        Map<String, BeisenEmpOrgDTO> employeeOrganizationDTOMap = getBeisenEmployeeOrganizationList(beisenParamConfig);
        //2、员工职级列表查询
        Map rankObject = (Map)employeeRankTemplateService.listRank(companyId);
        if (!ObjectUtils.isEmpty(rankObject)) {
            List<EmployeeAuthRankDto> rankList = JsonUtils.toObj(JsonUtils.toJson(rankObject.get("results")), new TypeReference<List<EmployeeAuthRankDto>>() {
            });
            Map<String, EmployeeAuthRankDto> rankMap = rankList.stream().collect(Collectors.toMap(EmployeeAuthRankDto::getThird_rank_id, d -> d));
            //3、遍历人员加上分贝通职级id
            if (!ObjectUtils.isEmpty(rankMap)) {
                for (OpenThirdEmployeeDTO openThirdEmployeeDTO : openThirdOrgUnitDTOList) {
                    if (!ObjectUtils.isEmpty(employeeOrganizationDTOMap)) {
                        BeisenEmpOrgDTO empOrgDTO = employeeOrganizationDTOMap.get(openThirdEmployeeDTO.getThirdEmployeeId());
                        EmployeeAuthRankDto employeeAuthRankDto = rankMap.get(empOrgDTO.getOIdOrganization());
                        openThirdEmployeeDTO.setThirdEmployeeRankId(employeeAuthRankDto.getRank_id());
                    }
                }
            }
        }
        return openThirdOrgUnitDTOList;
    }

    /**
     * 查询所有员工的任职机构
     * @param beisenParamConfig 单位相关信息
     * @return Map<String, BeisenEmpOrgDTO>
     */
    private Map<String, BeisenEmpOrgDTO> getBeisenEmployeeOrganizationList(BeisenParamConfig beisenParamConfig) {
        List<BeisenEmpOrgDTO> empOrgDTOList = beisenApiService.getEmployeeOrganization(beisenParamConfig);
        return empOrgDTOList.stream().collect(Collectors.toMap(BeisenEmpOrgDTO::getUserID, d -> d));
    }
}

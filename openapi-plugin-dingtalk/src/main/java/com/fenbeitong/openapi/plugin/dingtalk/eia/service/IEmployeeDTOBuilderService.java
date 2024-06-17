package com.fenbeitong.openapi.plugin.dingtalk.eia.service;

import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;

import java.util.List;
import java.util.Map;

/**
 * 员工中间表信息转换
 * @author zhangpeng
 * @date 2022/3/25 5:05 下午
 */
public interface IEmployeeDTOBuilderService {

    /**
     * 员工三方信息
     * @param companyId     公司id
     * @param thirdCorpId   三方企业id
     * @param userIds       用户ids
     * @return 员工花名册信息
     */
    Map<String, Map<String,String>> getRouterInfo(String companyId , String thirdCorpId , List<String> userIds );
}

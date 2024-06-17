package com.fenbeitong.openapi.plugin.beisen.standard.service;

import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenParamConfig;

import java.util.List;

/**
 * @Description
 * @Author duhui
 * @Date  2021/9/27
**/
public interface BeisenAttendanceService {


    /**
     * 拉取北森通过差旅审批并生成考勤
     */
    public void createAttendanceByAooly(BeisenParamConfig beisenParamConfig);

    /**
     * 考勤发券，每月发放一次
     */
    public void grantVoucher(String companyId, List<Long> ruleIdList);
}

package com.fenbeitong.openapi.plugin.feishu.common.service;

import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResultEntity;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeishuAttendanceRespDTO;

import java.util.List;

/**
 * 飞书考勤服务
 * @Auther zhang.peng
 * @Date 2021/9/25
 */
public interface FeishuAttendanceInterface {

    FeiShuResultEntity createAttendanceGroupInfo(String companyId, List<String> userNames);

    void pullAttendanceRecords( String companyId, boolean useCustomDay , int dayTime );
}

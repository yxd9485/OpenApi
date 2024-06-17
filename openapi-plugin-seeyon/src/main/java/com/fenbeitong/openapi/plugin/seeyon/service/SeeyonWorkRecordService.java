package com.fenbeitong.openapi.plugin.seeyon.service;

import com.fenbeitong.openapi.plugin.seeyon.dto.WorkRecordData;
/**
 * 致远待办
 * @Auther xiaohai
 * @Date 2022/09/27
 */
public interface SeeyonWorkRecordService {

    void syncWorkRecord(WorkRecordData workRecordData);
}

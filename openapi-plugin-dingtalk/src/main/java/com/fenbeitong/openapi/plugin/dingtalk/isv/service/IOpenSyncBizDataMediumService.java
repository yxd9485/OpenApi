package com.fenbeitong.openapi.plugin.dingtalk.isv.service;

import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.OpenSyncBizDataMedium;

import java.util.List;
import java.util.Map;

/**
 * @author lizhen
 */
public interface IOpenSyncBizDataMediumService {

    List<OpenSyncBizDataMedium> listOpenSyncBizMediumData(Map<String, Object> condition);

    void saveTask(Long taskId, String result);

    void updateTask(Long taskId, String result);
}

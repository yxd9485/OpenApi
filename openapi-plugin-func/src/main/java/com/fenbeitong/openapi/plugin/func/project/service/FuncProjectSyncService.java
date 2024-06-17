package com.fenbeitong.openapi.plugin.func.project.service;

/**
 * <p>Title: FuncProjectSyncService</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-12-11 14:05
 */
public interface FuncProjectSyncService {

    /**
     * 全量同步
     */
    String allProjectSync(String data);

    /**
     * 增量新增和更新
     */
    String partProjectSync(String data);

    /**
     *修改项目状态
     */
    String updateStatus(String data);

}

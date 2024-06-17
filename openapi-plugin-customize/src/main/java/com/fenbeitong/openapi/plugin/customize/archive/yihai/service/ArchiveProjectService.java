package com.fenbeitong.openapi.plugin.customize.archive.yihai.service;

import com.fenbeitong.openapi.plugin.customize.archive.yihai.dto.YiHaiConfigDTO;

/**
 * <p>Title: ArchiveProjectSyncService</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021-05-17 10:34
 */
public interface ArchiveProjectService {
    /**
     * 全量档案项目同步
     */
    public String ArchiveProjectSyncAll(YiHaiConfigDTO yiHaiConfigDTO);

    /**
     * 增量档案项目同步
     */
    public String ArchiveProjectSyncPart(YiHaiConfigDTO yiHaiConfigDTO);
}

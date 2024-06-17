package com.fenbeitong.openapi.plugin.moka.service;

import com.fenbeitong.openapi.plugin.moka.dto.JobConfigDto;

/**
 * <p>Title: IFxkSync</p>
 * <p>Description: 数据同步</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-08-31 15:47
 */
public interface MokaSyncService {

    /**
     * 组织架构同步
     */
    String syncOrganization(JobConfigDto jobConfigDto);
}

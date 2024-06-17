package com.fenbeitong.openapi.plugin.customize.yuntianlifei.service;

import com.fenbeitong.openapi.plugin.customize.yuntianlifei.dto.YunTianJobConfigDto;

/**
 * <p>Title: YunTianLiFeiOrgService</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2022/4/25 4:24 下午
 */
public interface YunTianOrgService {
    /**
     * @Description 组织架构同步
     * @Author duhui
     * @Date 2022/4/28
     **/
    void orgSync(YunTianJobConfigDto configDto);

    /**
     * @Description 项目同步
     * @Author duhui
     * @Date 2022/4/28
     **/
    void projectSync(YunTianJobConfigDto configDto);
}

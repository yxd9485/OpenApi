package com.fenbeitong.openapi.plugin.func.sso.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.func.sso.entity.OpenYufuSsoConfig;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

/**
 * <p>Title: OpenYufuSsoConfigDao</p>
 * <p>Description: 玉符单点登录配置dao</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/5/27 5:22 PM
 */
@Component
public class OpenYufuSsoConfigDao extends OpenApiBaseDao<OpenYufuSsoConfig> {

    public OpenYufuSsoConfig getByCompanyIdPlatformType(String companyId, int platformType) {
        Example example = new Example(OpenYufuSsoConfig.class);
        example.createCriteria().andEqualTo("companyId", companyId)
                .andEqualTo("platformType", platformType);
        return getByExample(example);
    }
}

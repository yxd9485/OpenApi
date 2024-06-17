package com.fenbeitong.openapi.plugin.yiduijie.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.yiduijie.entity.YiDuiJieConf;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

/**
 * <p>Title: YiDuiJieConfDao</p>
 * <p>Description: 易对接配置信息</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/12 3:41 PM
 */
@Component
public class YiDuiJieConfDao extends OpenApiBaseDao<YiDuiJieConf> {

    /**
     * 根据公司查询易对接配置
     *
     * @param companyId 公司id
     * @return 公司配置
     */
    public YiDuiJieConf getByCompanyId(String companyId) {
        Example example = new Example(YiDuiJieConf.class);
        example.createCriteria().andEqualTo("companyId", companyId);
        return getByExample(example);
    }
}

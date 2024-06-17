package com.fenbeitong.openapi.plugin.wechat.eia.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.wechat.eia.entity.WechatTokenConf;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

/**
 * Created by huangsiyuan on 2020/02/26.
 */
@Component
public class WechatTokenConfDao extends OpenApiBaseDao<WechatTokenConf> {

    public WechatTokenConf getByCompany(String compnayId) {
        Example example = new Example(WechatTokenConf.class);
        example.createCriteria().andEqualTo("companyId", compnayId);
        return getByExample(example);
    }

}

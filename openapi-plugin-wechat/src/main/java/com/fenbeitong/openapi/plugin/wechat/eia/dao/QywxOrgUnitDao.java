package com.fenbeitong.openapi.plugin.wechat.eia.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.wechat.eia.entity.QywxOrgUnit;
import com.fenbeitong.openapi.plugin.wechat.eia.mapper.QywxOrgUnitMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 企业微信部门表
 * Created by Z.H.W on 2020/02/18.
 */
@Component
public class QywxOrgUnitDao extends OpenApiBaseDao<QywxOrgUnit> {

    @Autowired
    private QywxOrgUnitMapper qywxOrgUnitMapper;

    /**
     * 根据corpId查询企业
     */
    public List<QywxOrgUnit> getOrgUnitByCorpId(QywxOrgUnit qywxOrgUnit) {
        Example example = new Example(QywxOrgUnit.class);
        example.createCriteria().andEqualTo("corpId", qywxOrgUnit.getCorpId()).andEqualTo("state", 0);
        return listByExample(example);
    }

    public void updateQywxOrgUnit(QywxOrgUnit orgUnit) {
        qywxOrgUnitMapper.updateByPrimaryKeySelective(orgUnit);
    }

    public void insertQywxOrgUnit(QywxOrgUnit orgUnit) {
        qywxOrgUnitMapper.insertSelective(orgUnit);
    }
}

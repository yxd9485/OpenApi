package com.fenbeitong.openapi.plugin.wechat.eia.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.wechat.eia.entity.QywxEmployee;
import com.fenbeitong.openapi.plugin.wechat.eia.mapper.QywxEmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 企业微信员工表
 * Created by Z.H.W on 2020/02/18.
 */
@Component
public class QywxEmployeeDao extends OpenApiBaseDao<QywxEmployee> {

    @Autowired
    private QywxEmployeeMapper qywxEmployeeMapper;

    /**
     * 根据corpId查询企业
     */
    public List<QywxEmployee> getEmployeeCorpId(QywxEmployee qywxEmployee) {
        Example example = new Example(QywxEmployee.class);
        example.createCriteria().andEqualTo("corpId", qywxEmployee.getCorpId()).andEqualTo("enable", 1);
        return listByExample(example);
    }

    public QywxEmployee getQywxEmployeeByMobile(String mobile) {
        Example example = new Example(QywxEmployee.class);
        example.createCriteria().andEqualTo("mobile", mobile);
        return getByExample(example);
    }
}

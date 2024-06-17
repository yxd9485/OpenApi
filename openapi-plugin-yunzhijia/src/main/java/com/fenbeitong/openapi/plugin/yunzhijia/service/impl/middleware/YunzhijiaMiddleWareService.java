package com.fenbeitong.openapi.plugin.yunzhijia.service.impl.middleware;

import com.fenbeitong.openapi.plugin.yunzhijia.dao.YunzhijiaOrgUnitDao;
import com.fenbeitong.openapi.plugin.yunzhijia.entity.YunzhijiaOrgUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@ServiceAspect
@Service
@Slf4j
public class YunzhijiaMiddleWareService {

    @Autowired
    YunzhijiaOrgUnitDao yunzhijiaOrgUnitDao;


    /**
     * 查询中间件存储的部门数据
     * @param yunzhijiaOrgUnit
     * @return
     */
    public List<YunzhijiaOrgUnit> selectYunzhijiaOrgUnitByParam(YunzhijiaOrgUnit yunzhijiaOrgUnit) {
        Example example = new Example(YunzhijiaOrgUnit.class);
        example.createCriteria().andEqualTo("corpId", yunzhijiaOrgUnit.getCorpId()).andEqualTo("state", 0);
        return yunzhijiaOrgUnitDao.listByExample(example);
    }

    /**
     * 更新中间件部门状态
     * @param yunzhijiaOrgUnit
     */
    public void updateYunzhijiaOrgUnit(YunzhijiaOrgUnit yunzhijiaOrgUnit) {
        Example example = new Example(YunzhijiaOrgUnit.class);
        example.createCriteria().andEqualTo("yunzhijiaOrgId",yunzhijiaOrgUnit.getYunzhijiaOrgId());
        yunzhijiaOrgUnitDao.updateByExample(yunzhijiaOrgUnit,example);
    }

    /**
     * 增加中间件部门
     * @param yunzhijiaOrgUnit
     */
    public void addYunzhijiaOrgUnit(YunzhijiaOrgUnit yunzhijiaOrgUnit) {
        try{
            yunzhijiaOrgUnitDao.save(yunzhijiaOrgUnit);
        }catch (Exception e){

            log.info("添加部门失败 {}",e.getCause());
            e.printStackTrace();
        }

    }




}

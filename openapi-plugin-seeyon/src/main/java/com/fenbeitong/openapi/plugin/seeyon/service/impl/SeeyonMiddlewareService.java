package com.fenbeitong.openapi.plugin.seeyon.service.impl;

import com.fenbeitong.openapi.plugin.seeyon.dao.SeeyonOrgDepartmentDao;
import com.fenbeitong.openapi.plugin.seeyon.dao.SeeyonOrgEmpDao;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonOrgDepartment;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonOrgEmployee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.util.List;

@ServiceAspect
@Service
@Slf4j
public class SeeyonMiddlewareService {

    @Autowired
    SeeyonOrgDepartmentDao seeyonOrgDepartmentDao;
    @Autowired
    SeeyonOrgEmpDao seeyonOrgEmpDao;

    /**
     * 查询中间件存储的部门数据
     *
     * @param seeyonOrgUnit
     * @return
     */
    public List<SeeyonOrgDepartment> selectSeeyonOrgUnitByParam(SeeyonOrgDepartment seeyonOrgUnit) {
        Example example = new Example(SeeyonOrgDepartment.class);
        example.createCriteria().andEqualTo("corpId", seeyonOrgUnit.getOrgAccountId()).andEqualTo("state", 1);
        return seeyonOrgDepartmentDao.listByExample(example);
    }

    /**
     * 更新中间件部门状态
     *
     * @param seeyonOrgUnit
     */
    public void updateSeeyonOrgUnit(SeeyonOrgDepartment seeyonOrgUnit) {
        Example example = new Example(SeeyonOrgDepartment.class);
        example.createCriteria().andEqualTo("id", seeyonOrgUnit.getId());
        seeyonOrgDepartmentDao.updateByExample(seeyonOrgUnit, example);
    }

    /**
     * 增加中间件部门
     *
     * @param seeyonOrgUnit
     */
    public void addSeeyonOrgUnit(SeeyonOrgDepartment seeyonOrgUnit) {
        try {
            seeyonOrgDepartmentDao.save(seeyonOrgUnit);
        } catch (Exception e) {
            log.info("添加致远部门失败 {}", e.getCause());
            e.printStackTrace();
        }
    }

    /**
     * 比对前一天组织机构数据，生成删除推送
     *
     * @param newDays
     * @param oldDays
     * @return
     */
    public List<SeeyonOrgDepartment> getDiffOrgAll(int newDays, int oldDays) {
        return seeyonOrgDepartmentDao.getDiffOrgAll(newDays, oldDays);
    }

    /**
     * 比对前一天人员数据，生成删除推送
     *
     * @param newDays
     * @param oldDays
     * @return
     */
    public List<SeeyonOrgEmployee> getDiffEmpAll(int newDays, int oldDays) {
        return seeyonOrgEmpDao.getDiffEmpAll(newDays, oldDays);
    }

    public boolean saveSeeyonDepartment(SeeyonOrgDepartment seeyonOrgDepartment) {
        int save = seeyonOrgDepartmentDao.save(seeyonOrgDepartment);
        if (save > 0) {
            return true;
        }
        return false;
    }

    /**
     * 定时清理流水表数据
     *
     * @param corpId
     * @param beginTime
     * @param endTime
     */
    public boolean deleteSeeyonDepartment(String corpId, String beginTime, String endTime) {
        Example example = new Example(SeeyonOrgDepartment.class);
        Example.Criteria orgAccountId = example.createCriteria()
                .andEqualTo("orgAccountId", corpId);
        orgAccountId.andBetween("seeyonFetchTime", beginTime, endTime);
        return seeyonOrgDepartmentDao.deleteByExample(example) == 0 ? false : true;
    }

    /**
     * 定时清理流水表数据
     * @param corpId
     * @param beginTime
     * @param endTime
     */
    public boolean deleteSeeyonEmp(String corpId, String beginTime, String endTime) {
        Example example = new Example(SeeyonOrgEmployee.class);
        Example.Criteria orgAccountId = example.createCriteria()
                .andEqualTo("orgAccountId", corpId);
        orgAccountId.andBetween("seeyonFetchTime", beginTime, endTime);
        return seeyonOrgDepartmentDao.deleteByExample(example) == 0 ? false : true;
    }

}

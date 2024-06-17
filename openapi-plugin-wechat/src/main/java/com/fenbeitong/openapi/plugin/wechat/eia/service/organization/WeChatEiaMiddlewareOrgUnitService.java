package com.fenbeitong.openapi.plugin.wechat.eia.service.organization;

import com.fenbeitong.openapi.plugin.wechat.eia.dao.QywxOrgUnitDao;
import com.fenbeitong.openapi.plugin.wechat.eia.entity.QywxOrgUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.List;

/**
 * Created by dave.hansins on 19/11/25.
 */
@ServiceAspect
@Service
public class WeChatEiaMiddlewareOrgUnitService {

    @Autowired
    private QywxOrgUnitDao qywxOrgUnitDao;

    /**
     * 根据公司ID查询部门数据
     *
     * @param qywxOrgUnit
     * @return
     */
    public List<QywxOrgUnit> selectQywxOrgUnitByParam(QywxOrgUnit qywxOrgUnit) {
        return qywxOrgUnitDao.getOrgUnitByCorpId(qywxOrgUnit);
    }

    /**
     * 更新中间件部门状态
     *
     * @param orgUnit
     */
    public void updateQywxOrgUnit(QywxOrgUnit orgUnit) {
        qywxOrgUnitDao.updateQywxOrgUnit(orgUnit);
    }

    /**
     * 增加中间件部门
     *
     * @param qywxOrgUnit
     */
    public void addQywxOrgUnit(QywxOrgUnit qywxOrgUnit) {
        qywxOrgUnitDao.insertQywxOrgUnit(qywxOrgUnit);
    }
}

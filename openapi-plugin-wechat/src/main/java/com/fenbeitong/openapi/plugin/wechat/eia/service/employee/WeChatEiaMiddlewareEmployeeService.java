package com.fenbeitong.openapi.plugin.wechat.eia.service.employee;

import com.fenbeitong.openapi.plugin.wechat.eia.dao.QywxEmployeeDao;
import com.fenbeitong.openapi.plugin.wechat.eia.entity.QywxEmployee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.List;

/**
 * Created by Z.H.W on 2020/02/18.
 */
@ServiceAspect
@Service
public class WeChatEiaMiddlewareEmployeeService {

    @Autowired
    private QywxEmployeeDao qywxEmployeeDao;

    public List<QywxEmployee> getEmployeeListByCorpId(String corpId) {
        return qywxEmployeeDao.getEmployeeCorpId(QywxEmployee.builder().corpId(corpId).build());
    }

    public void insertQywxEmployeeList(List<QywxEmployee> employeeList) {
        qywxEmployeeDao.saveList(employeeList);
    }

    public void updateQywxEmployeeList(List<QywxEmployee> employeeList) {
        employeeList.forEach(employee -> qywxEmployeeDao.updateById(employee));
    }

    public QywxEmployee getQywxEmployeeByMobile(String mobile) {
        return qywxEmployeeDao.getQywxEmployeeByMobile(mobile);
    }
}

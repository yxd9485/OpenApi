package com.fenbeitong.openapi.plugin.seeyon.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.openapi.plugin.seeyon.constant.FbOrgEmpConstants;
import com.fenbeitong.openapi.plugin.seeyon.dao.SeeyonFbOrgEmpDao;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonClient;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonFbOrgEmp;
import com.fenbeitong.openapi.plugin.support.employee.service.AbstractEmployeeService;
import com.fenbeitong.usercenter.api.service.employee.IThirdEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import tk.mybatis.mapper.entity.Example;
@ServiceAspect
@Service
public  class FbOrgEmpService extends AbstractEmployeeService {
    @Autowired
    SeeyonFbOrgEmpDao seeyonFbOrgEmpDao;

    protected boolean updateFbOrgEmp(SeeyonClient seeyonClient, SeeyonFbOrgEmp seeyonFbOrgEmp) {
        seeyonFbOrgEmp.setExecuteMark(FbOrgEmpConstants.CALL_EXECUTE_DONE);
        Example example = new Example(SeeyonFbOrgEmp.class);
        example.createCriteria().andEqualTo("id", seeyonFbOrgEmp.getId());
        int i = seeyonFbOrgEmpDao.updateByExample(seeyonFbOrgEmp, example);
        return i==1?true:false;
    }

}

package com.fenbeitong.openapi.plugin.zhongxin.isv.service;

import com.fenbeitong.openapi.plugin.support.employee.service.AbstractEmployeeService;
import com.fenbeitong.openapi.plugin.zhongxin.isv.dao.ZhongxinIsvUserDao;
import com.fenbeitong.openapi.plugin.zhongxin.isv.entity.ZhongxinIsvUser;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

import java.util.Date;

@ServiceAspect
@Service
public class ZhongxinEmployeeService extends AbstractEmployeeService {

    @Autowired
    private ZhongxinIsvUserDao zhongxinIsvUserDao;

    /**
     * 保存中信用户信息
     * @param employeeContract
     * @param thirdEmployeeId
     */
    public void saveEmployeeInfo(EmployeeContract employeeContract, String thirdEmployeeId){
        if(null == zhongxinIsvUserDao.getZhongxinIsvUserByHash(thirdEmployeeId)){
            ZhongxinIsvUser zhongxinIsvUser = new ZhongxinIsvUser();
            zhongxinIsvUser.setCompanyId(employeeContract.getCompany_id());
            zhongxinIsvUser.setHash(thirdEmployeeId);
            zhongxinIsvUser.setUserName(employeeContract.getName());
            zhongxinIsvUser.setIdType(employeeContract.getId_type()+"");
            zhongxinIsvUser.setIdNum(employeeContract.getId_number());
            zhongxinIsvUser.setPhoneNum(employeeContract.getPhone_num());
            zhongxinIsvUser.setEmployeeId(employeeContract.getEmployee_id());
            zhongxinIsvUser.setCreateTime(new Date());
            zhongxinIsvUser.setUpdateTime(new Date());
            zhongxinIsvUserDao.save(zhongxinIsvUser);
        }
    }

    /**
     * 保存中信用户信息
     * @param companyId
     * @param userId
     * @param userName
     * @param hash
     */
    public void saveEmployeeInfo(String companyId, String employeeId, String userId, String userName, String hash){
        ZhongxinIsvUser zhongxinIsvUser = new ZhongxinIsvUser();
        zhongxinIsvUser.setCompanyId(companyId);
        zhongxinIsvUser.setEmployeeId(employeeId);
        zhongxinIsvUser.setHash(hash);
        zhongxinIsvUser.setUserName(userName);
        zhongxinIsvUser.setPhoneNum(userId);
        zhongxinIsvUser.setCreateTime(new Date());
        zhongxinIsvUser.setUpdateTime(new Date());
        zhongxinIsvUserDao.save(zhongxinIsvUser);
    }

    /**
     * 通过hash获取用户信息
     * @param hash
     * @return
     */
    public ZhongxinIsvUser getEmployeeInfoByHash(String hash){
        return zhongxinIsvUserDao.getZhongxinIsvUserByHash(hash);
    }

    /**
     * 通过手机号查询用户信息
     * @param phoneNum
     * @param companyId
     * @return
     */
    public ZhongxinIsvUser getEmployeeInfoByPhoneNum(String phoneNum, String companyId){
        return zhongxinIsvUserDao.getZhongxinIsvUserByPhoneNum(phoneNum, companyId);
    }
}

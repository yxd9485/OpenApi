package com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl;

import com.dingtalk.api.response.OapiDepartmentListResponse;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkUser;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkUserExt;
import com.fenbeitong.openapi.plugin.support.employee.dto.SupportEmployeeInsertDTO;
import com.fenbeitong.openapi.plugin.support.employee.dto.SupportUpdateEmployeeReqDTO;
import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Title: DingTalkEmployeeServiceExt1Impl</p>
 * <p>Description: 钉钉用户服务扩展工号作为三方id</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/2/12 6:24 PM
 */
@ServiceAspect
@Service
public class DingTalkEmployeeServiceExt1Impl extends DingTalkSyncThirdEmployeeServiceImpl {

    @Value("${dingtalk.jobnumberasthirdid.companylist}")
    private String jobNumberCompanyIdStr;

    @Value("${dingtalk.fixroletype.companylist}")
    private String fixRoleCompanyStr;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    protected List<DingtalkUser> listDingtalkUser(String companyId, String corpId) {
        List<DingtalkUser> dingtalkUserList = super.listDingtalkUser(companyId, corpId);
        List<String> jobNumberCompanyIdList = ObjectUtils.isEmpty(jobNumberCompanyIdStr) ? Lists.newArrayList() : Lists.newArrayList(jobNumberCompanyIdStr.split(","));
        //如果是配置的公司ID，则人员ID进行设置为员工工号字段
        if (jobNumberCompanyIdList.contains(companyId)) {
            return dingtalkUserList.stream().map(u -> {
                redisTemplate.opsForValue().set(corpId + "-" + u.getUserid(), u.getJobnumber());
                DingtalkUserExt userExt = new DingtalkUserExt(1);
                BeanUtils.copyProperties(u, userExt);
                return userExt;
            }).collect(Collectors.toList());
        }
        return dingtalkUserList;
    }

    @Override
    protected List<DingtalkUser> listDingtalkUser(List<OapiDepartmentListResponse.Department> departments, String corpId) {
        List<DingtalkUser> dingtalkUserList = super.listDingtalkUser(departments, corpId);
        return dingtalkUserList.stream().map(u -> {
            redisTemplate.opsForValue().set(corpId + "-" + u.getUserid(), u.getJobnumber());
            DingtalkUserExt userExt = new DingtalkUserExt(1);
            BeanUtils.copyProperties(u, userExt);
            return userExt;
        }).collect(Collectors.toList());
    }

    @Override
    protected void beforeEmployeeInsert(String companyId, SupportEmployeeInsertDTO employeeInsert) {
        //权限固定公司列表
        List<String> fixRoleCompanyList = ObjectUtils.isEmpty(fixRoleCompanyStr) ? Lists.newArrayList() : Lists.newArrayList(fixRoleCompanyStr.split(","));
        //权限固定为1
        if (fixRoleCompanyList.contains(companyId)) {
            employeeInsert.setRoleType("1");
        }
    }

    @Override
    protected void beforeBindUpdate(String companyId, SupportUpdateEmployeeReqDTO updateEmployeeReq) {
        //权限固定公司列表
        List<String> fixRoleCompanyList = ObjectUtils.isEmpty(fixRoleCompanyStr) ? Lists.newArrayList() : Lists.newArrayList(fixRoleCompanyStr.split(","));
        //权限固定为1
        if (fixRoleCompanyList.contains(companyId)) {
            updateEmployeeReq.getEmployeeList().forEach(e -> e.setRoleType("1"));
        }
    }

    @Override
    protected void beforeUpdate(String companyId, SupportUpdateEmployeeReqDTO updateEmployeeReq) {
        //权限固定公司列表
        List<String> fixRoleCompanyList = ObjectUtils.isEmpty(fixRoleCompanyStr) ? Lists.newArrayList() : Lists.newArrayList(fixRoleCompanyStr.split(","));
        //权限固定为1
        if (fixRoleCompanyList.contains(companyId)) {
            updateEmployeeReq.getEmployeeList().forEach(e -> e.setRoleType(null));
        }
    }

}

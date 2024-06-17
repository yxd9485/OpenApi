package com.fenbeitong.openapi.plugin.seeyon.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.openapi.plugin.seeyon.constant.FbOrgEmpConstants;
import com.fenbeitong.openapi.plugin.seeyon.constant.SeeyonConstant;
import com.fenbeitong.openapi.plugin.seeyon.dao.SeeyonExtInfoDao;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountEmpResp;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountOrgResp;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountParam;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonClient;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonExtInfo;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonFbOrgEmp;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonOrgEmployee;
import com.fenbeitong.openapi.plugin.seeyon.helper.Jsr310DateHelper;
import com.fenbeitong.openapi.plugin.seeyon.service.SeeyonDepartmentService;
import com.fenbeitong.openapi.plugin.seeyon.service.SeeyonEmpService;
import com.fenbeitong.openapi.plugin.seeyon.service.SeeyonFbOrgEmpService;
import com.fenbeitong.openapi.plugin.support.employee.service.AbstractEmployeeService;
import com.fenbeitong.openapi.plugin.support.util.PhoneCheckUtil;
import com.fenbeitong.openapi.sdk.webservice.employee.FbtEmployeeService;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeBaseInfo;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeRes;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import com.fenbeitong.usercenter.api.service.employee.IThirdEmployeeService;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ServiceAspect
@Service
public class SeeyonPullEmpService extends AbstractEmployeeService {
    public static Map<String, SeeyonExtInfo> seeyonExtInfoMap = Maps.newHashMap();
    @Autowired
    private SeeyonDepartmentService seeyonDepartmentService;
    @Autowired
    private SeeyonClientService seeyonClientService;
    @Autowired
    private SeeyonFbOrgEmpService seeyonFbOrgEmpService;
    @Autowired
    private SeeyonMiddlewareService seeyonMiddlewareService;
    @Autowired
    SeeyonExtInfoService seeyonExtInfoService;
    @DubboReference(check = false)
    private IThirdEmployeeService iThirdEmployeeService;
    @Autowired
    SeeyonEmpService seeyonEmpService;
    @Autowired
    FbtEmployeeService fbtEmployeeService;

    @Value("${seeyon.rest-apis.get-org-employee}")
    private String seeyonOrgEmpInfoUrl;

    /**
     * <p>//判断组织人员数据，生成FB推送数据
     *
     * @param seeyonClient       : 公司信息
     * @param accountEmpResponse : 人员信息
     * @param compareDate        : 指定时间间隔
     * @return boolean
     */
    public boolean filterEmpData(
            SeeyonClient seeyonClient, SeeyonAccountEmpResp accountEmpResponse, Long compareDate) {
        LocalDate createDate =
                Jsr310DateHelper.getDateTimeOfTimestamp(accountEmpResponse.getCreateTime()).toLocalDate();
        LocalDate updateDate =
                Jsr310DateHelper.getDateTimeOfTimestamp(accountEmpResponse.getUpdateTime()).toLocalDate();
        if (PhoneCheckUtil.validMomile(accountEmpResponse.getTelNumber())) {//手机号校验，非正确手机号不做同步
            /*
             * 人员创建
             * 1. 创建时间 = 指定时间
             * 2. 状态为有效
             */
            if (Jsr310DateHelper.equalComparedDayGaps(createDate, LocalDate.now(), compareDate)
                    && SeeyonConstant.EMP_ENABLED_TRUE.equals(accountEmpResponse.isEnabled())) {
                SeeyonFbOrgEmp d = SeeyonFbOrgEmp.builder()
                        .companyId(seeyonClient.getOpenapiAppId())
                        .orgPath(String.valueOf(accountEmpResponse.getId()))
                        .dataType(1)
                        .sort("d")
                        .build();
                List<SeeyonFbOrgEmp> seeyonFbOrgEmpsByfbOrgEmp = seeyonFbOrgEmpService.getSeeyonFbOrgEmpsByfbOrgEmp(d);
                if (ObjectUtils.isEmpty(seeyonFbOrgEmpsByfbOrgEmp)) {//查询结构返回没有数据时，进行添加操作
                    SeeyonExtInfo seeyonExtInfo = null;
                    if (ObjectUtils.isEmpty(seeyonExtInfoMap)) {
                        seeyonExtInfo = seeyonExtInfoService.getSeeyonExtInfo(seeyonClient.getOpenapiAppId(), 1, 0);
                        if (ObjectUtils.isEmpty(seeyonExtInfoMap.get(seeyonClient.getOpenapiAppId()))) {
                            seeyonExtInfoMap.put(seeyonClient.getOpenapiAppId(), seeyonExtInfo);
                        }
                    } else {
                        seeyonExtInfo = seeyonExtInfoMap.get(seeyonClient.getOpenapiAppId());
                    }
                    return seeyonFbOrgEmpService.createEmp(seeyonClient, accountEmpResponse, seeyonExtInfo);
                }
                return false;
            } else if (Jsr310DateHelper.equalComparedDayGaps(updateDate, LocalDate.now(), compareDate)) {
                /*
                 * 人员更新
                 * 1. 更新时间 = 指定时间
                 * 2. 状态为有效
                 */
                if (SeeyonConstant.EMP_ENABLED_TRUE.equals(accountEmpResponse.isEnabled())) {
                    Long employeeId = accountEmpResponse.getId();
                    //根据人员ID查询分贝用户信息,进行新增和更新人员判断标识
                    ThirdEmployeeRes employeeByThirdId = getEmployeeByThirdId(seeyonClient.getOpenapiAppId(), String.valueOf(employeeId));
                    if (!ObjectUtils.isEmpty(employeeByThirdId)) {
                        SeeyonFbOrgEmp e = SeeyonFbOrgEmp.builder()
                                .companyId(seeyonClient.getOpenapiAppId())
                                .orgPath(String.valueOf(employeeId))
                                .dataType(1)
                                .executeMark(FbOrgEmpConstants.CALL_EXECUTE_WAIT)
                                .sort("e")
                                .build();
                        List<SeeyonFbOrgEmp> seeyonFbOrgEmpsByfbOrgEmp = seeyonFbOrgEmpService.getSeeyonFbOrgEmpsByfbOrgEmp(e);
                        if (ObjectUtils.isEmpty(seeyonFbOrgEmpsByfbOrgEmp)) {//查询结构返回没有数据时，进行添加操作
                            SeeyonExtInfo seeyonExtInfo = null;
                            if (ObjectUtils.isEmpty(seeyonExtInfoMap)) {
                                seeyonExtInfo = seeyonExtInfoService.getSeeyonExtInfo(seeyonClient.getOpenapiAppId(), 1, 0);
                                if (ObjectUtils.isEmpty(seeyonExtInfoMap.get(seeyonClient.getOpenapiAppId()))) {
                                    seeyonExtInfoMap.put(seeyonClient.getOpenapiAppId(), seeyonExtInfo);
                                }
                            } else {
                                seeyonExtInfo = seeyonExtInfoMap.get(seeyonClient.getOpenapiAppId());
                            }
                            return seeyonFbOrgEmpService.updateEmp(seeyonClient, accountEmpResponse, seeyonExtInfo);
                        }
                        return false;
                    } else {//查询人员不在分贝通后进行人员的新增操作
                        SeeyonFbOrgEmp d = SeeyonFbOrgEmp.builder()
                                .companyId(seeyonClient.getOpenapiAppId())
                                .orgPath(String.valueOf(employeeId))
                                .dataType(1)
                                .sort("d")
                                .build();
                        List<SeeyonFbOrgEmp> seeyonFbOrgEmpsByfbOrgEmp = seeyonFbOrgEmpService.getSeeyonFbOrgEmpsByfbOrgEmp(d);
                        if (ObjectUtils.isEmpty(seeyonFbOrgEmpsByfbOrgEmp)) {//查询结构返回没有数据时，进行添加操作
                            SeeyonExtInfo seeyonExtInfo = null;
                            if (ObjectUtils.isEmpty(seeyonExtInfoMap)) {
                                seeyonExtInfo = seeyonExtInfoService.getSeeyonExtInfo(seeyonClient.getOpenapiAppId(), 1, 0);
                                if (ObjectUtils.isEmpty(seeyonExtInfoMap.get(seeyonClient.getOpenapiAppId()))) {
                                    seeyonExtInfoMap.put(seeyonClient.getOpenapiAppId(), seeyonExtInfo);
                                }
                            } else {
                                seeyonExtInfo = seeyonExtInfoMap.get(seeyonClient.getOpenapiAppId());
                            }
                            return seeyonFbOrgEmpService.createEmp(seeyonClient, accountEmpResponse, seeyonExtInfo);
                        }
                        return false;
                    }
                }
                /*
                 * 人员删除
                 * 1. 更新时间 = 指定时间
                 * 2. 状态为有效
                 */
                else if (SeeyonConstant.EMP_ENABLED_FALSE.equals(accountEmpResponse.isEnabled()) && accountEmpResponse.getState() == 2) {

                    SeeyonFbOrgEmp a = SeeyonFbOrgEmp.builder()
                            .companyId(seeyonClient.getOpenapiAppId())
                            .orgPath(String.valueOf(accountEmpResponse.getId()))
                            .dataType(1)
                            .executeMark(FbOrgEmpConstants.CALL_EXECUTE_WAIT)
                            .sort("a")
                            .build();
                    List<SeeyonFbOrgEmp> seeyonFbOrgEmpsByfbOrgEmp = seeyonFbOrgEmpService.getSeeyonFbOrgEmpsByfbOrgEmp(a);
                    if (ObjectUtils.isEmpty(seeyonFbOrgEmpsByfbOrgEmp)) {//查询结构返回没有数据时，进行添加操作
                        return seeyonFbOrgEmpService.delEmp(seeyonClient, accountEmpResponse);
                    }
                    return false;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 根据返回的人员数据结果，查找出需要删除的人员数据
     *
     * @return
     */
    public Boolean filterDiffEmp(SeeyonClient seeyonClient, List<SeeyonAccountEmpResp> orgEmpInfo) {
        //2.根据部门ID循环查询人员数据
        List<String> seeyonEmpIds = orgEmpInfo.stream().map(orgResp -> String.valueOf(orgResp.getId())).collect(Collectors.toList());
        //3.根据公司ID查询分贝全量人员数据
        List<EmployeeBaseInfo> employeeBaseInfos = listFbEmployee(seeyonClient.getOpenapiAppId());
        List<String> fbEmpIds = employeeBaseInfos.stream().map(emp -> emp.getThirdEmployeeId()).collect(Collectors.toList());
        //4.比对需要删除的人员数据
        List<String> diffEmpIdList = fbEmpIds.stream().filter(num -> (!seeyonEmpIds.contains(num)))
                .collect(Collectors.toList());
        for (String empId : diffEmpIdList) {
            SeeyonOrgEmployee seeyonOrgEmployee = new SeeyonOrgEmployee();
            seeyonOrgEmployee.setId(Long.valueOf(empId));
            seeyonFbOrgEmpService.delEmp(seeyonClient, seeyonOrgEmployee);
        }
        return true;
    }

    /**
     * <p>///人员数据初始化,新增
     *
     * @param seeyonClient       : 公司信息
     * @param accountEmpResponse : 人员数据
     * @return java.lang.Boolean
     */
    public Boolean initEmpData(SeeyonClient seeyonClient, SeeyonAccountEmpResp accountEmpResponse) {
        if (SeeyonConstant.EMP_ENABLED_FALSE == accountEmpResponse.isEnabled()) {
            return true;
        }
        SeeyonExtInfo seeyonExtInfo = seeyonExtInfoService.getSeeyonExtInfo(seeyonClient.getOpenapiAppId(), 1, 0);
        return seeyonFbOrgEmpService.createEmp(seeyonClient, accountEmpResponse, seeyonExtInfo);
    }

    @Override
    protected IThirdEmployeeService getThirdEmployeeService() {
        return iThirdEmployeeService;
    }

}

package com.fenbeitong.openapi.plugin.seeyon.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.openapi.plugin.support.employee.dto.SupportDeleteEmployeeReqDTO;
import com.fenbeitong.openapi.plugin.support.employee.service.IEmployeeRankTemplateService;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdEmployeeDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdEmployee;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeBean;
import com.fenbeitong.usercenter.api.model.dto.employee.ThirdEmployeeRes;
import com.fenbeitong.usercenter.api.service.employee.IThirdEmployeeService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

@ServiceAspect
@Service
@Slf4j
public class SeeyonSyncEmployeeService extends OpenEmployeeServiceImpl {

    @Autowired
    SeeyonExtInfoService seeyonExtInfoService;
    @DubboReference(check = false)
    private IThirdEmployeeService iThirdEmployeeService;
    @Autowired
    OpenMsgSetupDao openMsgSetupDao;
    @Autowired
    private OpenThirdEmployeeDao openThirdEmployeeDao;
    @Autowired
    SeeyonEmailService seeyonEmailService;

    @Autowired
    private IEmployeeRankTemplateService employeeRankTemplateService;


    @Override
    public boolean isNeedUpdate(OpenThirdEmployee employee, Map<String, OpenThirdEmployeeDTO> destEmployeeMap) {
        OpenThirdEmployeeDTO destEmployee = destEmployeeMap.get(employee.getThirdEmployeeId());
        boolean update = false;
        if (destEmployee != null && !StringUtils.isBlank(destEmployee.getThirdEmployeeName()) && !ObjectUtils.nullSafeEquals(destEmployee.getThirdEmployeeName(), employee.getThirdEmployeeName())) {
            update = true;
        }

        //需要修改手机号，根据企业配置是否以分贝通手机号为准
        //根据公司配置获取手机号使用方，默认使用接口获取的手机号，如果公司配置，则可以以分贝通手机号为准
        Map itemCodeMap = Maps.newHashMap();
        itemCodeMap.put("companyId", employee.getCompanyId());
        itemCodeMap.put("itemCode", "company_use_fb_phone");
        List<OpenMsgSetup> list = openMsgSetupDao.openMsgSetupList(itemCodeMap);
        if (!ObjectUtils.isEmpty(list)) {
            OpenMsgSetup openMsgSetup = list.get(0);
            Integer intVal1 = openMsgSetup.getIntVal1();
            if (intVal1 == 1) {//配置了特殊公司的值，才进行相应的属性设置，否则默认走正常逻辑
                //查询分贝通手机号
                String thirdEmployeeId = employee.getThirdEmployeeId();
                ThirdEmployeeRes employeeByThirdId = getEmployeeByThirdId(employee.getCompanyId(), thirdEmployeeId);
                log.info(" 人员Id: {}, 根据人员ID查询分贝通人员信息结果: {}", thirdEmployeeId, JsonUtils.toJson(employeeByThirdId));
                if (!ObjectUtils.isEmpty(employeeByThirdId)) {//不为空则进行手机号的更新操作
                    //手机号不相同才进行更新判断，手机号相同就不更新
                    if (destEmployee != null && !ObjectUtils.nullSafeEquals(destEmployee.getThirdEmployeePhone(), employee.getThirdEmployeePhone())) {
                        ThirdEmployeeBean fbtEmployee = employeeByThirdId.getEmployee();
                        String phoneNum = fbtEmployee.getPhone_num();
                        //手机号覆盖操作
                        OpenThirdEmployeeDTO openThirdEmployeeDTO = destEmployeeMap.get(thirdEmployeeId);
                        //目的设置新的值
                        openThirdEmployeeDTO.setThirdEmployeePhone(phoneNum);
                        destEmployeeMap.put(thirdEmployeeId, openThirdEmployeeDTO);
                        //源设置新的值
                        employee.setThirdEmployeePhone(phoneNum);
                        update = true;
                    }
                }
            } else {//配置了值，按照原有逻辑更新手机号
                if (destEmployee != null && !StringUtils.isBlank(destEmployee.getThirdEmployeePhone()) && !ObjectUtils.nullSafeEquals(destEmployee.getThirdEmployeePhone(), employee.getThirdEmployeePhone())) {
                    update = true;
                }
            }
        }
        if (destEmployee != null && !StringUtils.isBlank(destEmployee.getThirdDepartmentId()) && !ObjectUtils.nullSafeEquals(destEmployee.getThirdDepartmentId(), employee.getThirdDepartmentId())) {
            update = true;
        }

        boolean useRank = employeeRankTemplateService.useRank(employee.getCompanyId());
        if(useRank && destEmployee != null && destEmployee.getThirdEmployeeRoleTye() != null && !ObjectUtils.nullSafeEquals(destEmployee.getThirdEmployeeRoleTye(), employee.getTemplateName())){
            update = true;
        }else if(!useRank && destEmployee != null && destEmployee.getThirdEmployeeRoleTye() != null && !ObjectUtils.nullSafeEquals(destEmployee.getThirdEmployeeRoleTye(), com.fenbeitong.openapi.plugin.util.StringUtils.obj2str(employee.getThirdEmployeeRoleTye()))) {
            update = true;
        }
//        String thirdEmployeeIdCard = destEmployee.getThirdEmployeeIdCard();
//        String thirdEmployeeIdCard1 = employee.getThirdEmployeeIdCard();
        if (destEmployee != null && !StringUtils.isBlank(destEmployee.getThirdEmployeeIdCard()) && !ObjectUtils.nullSafeEquals(destEmployee.getThirdEmployeeIdCard(), employee.getThirdEmployeeIdCard())) {
            update = true;
        }
        return update;
    }

    @Override
    public void deleteEmployee(String companyId, List<SupportDeleteEmployeeReqDTO> supportDeleteEmployeeReqList) {
        if (!ObjectUtils.isEmpty(supportDeleteEmployeeReqList)) {
            Map itemCodeMap = Maps.newHashMap();
            itemCodeMap.put("companyId", companyId);
            itemCodeMap.put("itemCode", "company_early_warning");
            List<OpenMsgSetup> list = openMsgSetupDao.openMsgSetupList(itemCodeMap);
            if (!ObjectUtils.isEmpty(list)) {
                OpenMsgSetup openMsgSetup = list.get(0);
                if (!ObjectUtils.isEmpty(openMsgSetup)) {//不为空，说明存在配置值
                    Integer intVal1 = openMsgSetup.getIntVal1();
                    if (1 == intVal1) {//说明公司配置了预警的信息，需要根据预警信息进行过滤
                        //获取需要删除的人员和部门数据，如果人员和部门数据大于客户之前约定的部门数据，则不进行数据的更新，把需要删除的部门和人员数据剔除掉
//                        SupportDeleteEmployeeReqDTO supportDeleteEmployeeReqDTO = supportDeleteEmployeeReqList.get(0);
                        //所有集合的大小
                        int deleteCount = 0;
                        for (SupportDeleteEmployeeReqDTO supportDeleteEmployeeReqDTO1 : supportDeleteEmployeeReqList) {
                            int size = supportDeleteEmployeeReqDTO1.getIdList().size();
                            deleteCount = deleteCount + size;
                        }
//                        int deleteCount = supportDeleteEmployeeReqDTO.getIdList().size();
                        if (deleteCount > openMsgSetup.getIntVal3()) {//人员数量预警,并进行相应邮件的发送消息通知
                            //邮件预警通知
                            Map sendEmailNoticeMap = Maps.newHashMap();
                            sendEmailNoticeMap.put("companyId", companyId);
                            sendEmailNoticeMap.put("itemCode", "company_send_self_email_notice");
                            String contents = "同步人员数据超过与客户约定的删除数量，请检查确认后再次同步";
                            StringBuilder stringBuilder = new StringBuilder(contents);
                            //用于存储需要指定的人员邮箱，如果有特定邮箱，则取该邮箱，如果没有特定邮箱，则取company_send_self_email_notice中配置的邮箱
                            String strVal1 = openMsgSetup.getStrVal1();
                            //即使邮件没有发送通知成功，部门已经不同步，下次同步删除部门时，会再次发送邮件信息
                            seeyonEmailService.sendEmail(companyId, sendEmailNoticeMap, stringBuilder, strVal1);
                            return;
                        }
                        if (!ObjectUtils.isEmpty(supportDeleteEmployeeReqList)) {
                            supportDeleteEmployeeReqList.forEach(req -> {
                                deleteUser(req);
                                req.getIdList().forEach(id -> {
                                    openThirdEmployeeDao.deleteById(id);
                                });
                            });
                        }
                    } else {
                        if (!ObjectUtils.isEmpty(supportDeleteEmployeeReqList)) {
                            supportDeleteEmployeeReqList.forEach(req -> {
                                deleteUser(req);
                                req.getIdList().forEach(id -> {
                                    openThirdEmployeeDao.deleteById(id);
                                });
                            });
                        }
                    }
                } else {
                    if (!ObjectUtils.isEmpty(supportDeleteEmployeeReqList)) {
                        supportDeleteEmployeeReqList.forEach(req -> {
                            deleteUser(req);
                            req.getIdList().forEach(id -> {
                                openThirdEmployeeDao.deleteById(id);
                            });
                        });
                    }
                }
            } else {
                if (!ObjectUtils.isEmpty(supportDeleteEmployeeReqList)) {
                    supportDeleteEmployeeReqList.forEach(req -> {
                        deleteUser(req);
                        req.getIdList().forEach(id -> {
                            openThirdEmployeeDao.deleteById(id);
                        });
                    });
                }
            }
        }
    }

    @Override
    protected IThirdEmployeeService getThirdEmployeeService() {
        return iThirdEmployeeService;
    }

}

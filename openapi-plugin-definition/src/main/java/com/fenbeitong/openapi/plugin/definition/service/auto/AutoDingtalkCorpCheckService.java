package com.fenbeitong.openapi.plugin.definition.service.auto;

import com.dingtalk.api.response.OapiDepartmentListResponse;
import com.fenbeitong.finhub.common.validation.validators.DeptValidator;
import com.fenbeitong.finhub.common.validation.validators.UserValidator;
import com.fenbeitong.finhub.common.validation.validators.Validation;
import com.fenbeitong.openapi.plugin.definition.dto.auto.CorpAutoCheckDTO;
import com.fenbeitong.openapi.plugin.definition.dto.auto.CorpAutoOrgEmpDTO;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkUser;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IApiDepartmentService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IApiUserService;
import com.fenbeitong.openapi.sdk.dto.common.OpenApiRespDTO;
import com.fenbeitong.openapi.sdk.dto.employee.GetUserByPhoneNumReqDTO;
import com.fenbeitong.openapi.sdk.dto.employee.GetUserByPhoneNumRespDTO;
import com.fenbeitong.openapi.sdk.webservice.employee.FbtEmployeeService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import retrofit2.Call;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@ServiceAspect
@Service
@Slf4j
public class AutoDingtalkCorpCheckService {

    @Autowired
    private IApiDepartmentService apiDepartmentService;
    @Autowired
    private IApiUserService dingtalkUserService;
    @Autowired
    private FbtEmployeeService fbtUserCenterService;

    public List<CorpAutoOrgEmpDTO> check(CorpAutoCheckDTO corpAutoCheckDTO) {
        List<CorpAutoOrgEmpDTO> corpAutoOrgEmpDTOS = Lists.newArrayList();
        if (!StringUtils.isBlank(corpAutoCheckDTO.getThirdEmpId())) {//检查人员

        } else if ("0".equals(corpAutoCheckDTO.getCheckDetail())) {//检查部门
            corpAutoOrgEmpDTOS = checkIllegalNameDepartments(corpAutoCheckDTO.getThirdCompanyId());
        } else if ("2".equals(corpAutoCheckDTO.getCheckDetail()) || "3".equals(corpAutoCheckDTO.getCheckDetail()) || "5".equals(corpAutoCheckDTO.getCheckDetail())) {//分贝通手机号
            //全部人员列表
            List<DingtalkUser> userList = dingtalkUserService.getAllUserByDepartment(1L, corpAutoCheckDTO.getThirdCompanyId());
            List<DingtalkUser> dingtalkUsers = Lists.newArrayList();
            if ("2".equals(corpAutoCheckDTO.getCheckDetail())) {
                //获取没有设置分贝通手机号的人员数据
                dingtalkUsers = userList.stream().filter(user -> {
                    String fbtMobile = user.getFbtMobile();
                    Validation validation = UserValidator.validateMobile(fbtMobile);
                    return !validation.isSuccess();
                }).collect(Collectors.toList());

            } else if ("5".equals(corpAutoCheckDTO.getCheckDetail())) {
                //获取有相同的邮箱的人
                Map<String, String> map = new HashMap<>();
                Map<String, DingtalkUser> userMap = new HashMap<>();
                Set<String> set = new HashSet<>();
                for(DingtalkUser user : userList) {
                    //1:map.containsKey()   检测key是否重复
                    userMap.put(user.getUserid(), user);
                    if (StringUtils.isNotEmpty(user.getEmail()) && map.containsKey(user.getEmail())) {
                        set.add(user.getUserid());
                        set.add(map.get(user.getEmail()));
                    } else if (StringUtils.isNotEmpty(user.getEmail())) {
                        map.put(user.getEmail(), user.getUserid());
                    }
                }
                if (!set.isEmpty()) {
                   for (String id : set) {
                       dingtalkUsers.add(userMap.get(id));
                   }
                }
            } else {//获取没有设置分贝通手机号的人员数据
                dingtalkUsers = userList.stream().filter(user -> {
                    String fbtRoleType = user.getFbtNullRoleType();
                    Validation validation = UserValidator.validateRoleType(fbtRoleType);
                    return !validation.isSuccess();
                }).collect(Collectors.toList());
            }
            //符合页面规则的数据
            List<CorpAutoOrgEmpDTO> finalCorpAutoOrgEmpDTOS = corpAutoOrgEmpDTOS;
            dingtalkUsers.stream().forEach(user -> finalCorpAutoOrgEmpDTOS.add(CorpAutoOrgEmpDTO.builder()
                    .thirdEmpId(user.getUserid())
                    .thirdEmpName(user.getName())
                    .email(user.getEmail())
                    .build()
            ));
            corpAutoOrgEmpDTOS = finalCorpAutoOrgEmpDTOS;
            log.info("获取没有设置分贝通手机号的人员列表 {}", corpAutoOrgEmpDTOS);
        } else if ("4".equals(corpAutoCheckDTO.getCheckDetail())) {//分贝通账户
            List<DingtalkUser> userList = dingtalkUserService.getAllUserByDepartment(1L, corpAutoCheckDTO.getThirdCompanyId());
            List<CorpAutoOrgEmpDTO> fbtAccoutList = Lists.newArrayList();
            //遍历集合单独查询数据，不进行批量查询
            List<List<DingtalkUser>> partition = com.google.common.collect.Lists.partition(userList, 50);
            for (List<DingtalkUser> dingtalkUserList : partition) {
                for (DingtalkUser dingtalkUser : dingtalkUserList) {
                    String thirdMobile = dingtalkUser.getFbtMobile();
                    String thirdName = dingtalkUser.getName();
//                String thirdUserid = dingtalkUser.getUserid();
                    List<CorpAutoOrgEmpDTO> fbtAccountByMobile = getFbtAccountByMobile(thirdName, Lists.newArrayList(thirdMobile));
                    if (0 != fbtAccountByMobile.size() && !corpAutoCheckDTO.getFbtCompName().equals(fbtAccountByMobile.get(0).getCompanyName())) {
                        corpAutoOrgEmpDTOS.add(fbtAccountByMobile.get(0));
                    }
                }
            }
        }
        return corpAutoOrgEmpDTOS;
    }

    /**
     * 根据手机号查询人员数据
     *
     * @return
     */
    public List<CorpAutoOrgEmpDTO> getFbtAccountByMobile(String userName, List<String> mobileList) {
        Call<OpenApiRespDTO<List<GetUserByPhoneNumRespDTO>>> userInfoOnlyByPhoneNum = fbtUserCenterService.getUserInfoOnlyByPhoneNum(GetUserByPhoneNumReqDTO.builder().phoneNums(mobileList).build());
        OpenApiRespDTO<List<GetUserByPhoneNumRespDTO>> body = null;
        try {
            body = userInfoOnlyByPhoneNum.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<CorpAutoOrgEmpDTO> fbtAccoutList = Lists.newArrayList();
        List<GetUserByPhoneNumRespDTO> fbtUserList = body == null ? com.google.common.collect.Lists.newArrayList() : body.getData();
        fbtUserList.stream().forEach(account -> fbtAccoutList.add(CorpAutoOrgEmpDTO.builder()
                .companyName(account.getCompanyName())
                .fbtEmpName(account.getUserName())
                .thirdEmpName(userName)
                .mobilePhone(account.getUserPhone())
                .build()));
        return fbtAccoutList;
    }

    /**
     * 获取钉钉不合规部门名称
     *
     * @param corpId
     * @return
     */
    public List<CorpAutoOrgEmpDTO> checkIllegalNameDepartments(String corpId) {
//        CheckUtils.checkEmpty("corpId", "corpId 不能为空");
        List<CorpAutoOrgEmpDTO> illegalDepartments = new ArrayList<>();
        List<OapiDepartmentListResponse.Department> departments = apiDepartmentService.listDepartment(corpId);
        if (CollectionUtils.isNotEmpty(departments)) {
            for (OapiDepartmentListResponse.Department department : departments) {
                Validation validation = DeptValidator.validateDeptName(department.getName());
                if (!validation.isSuccess()) {
                    illegalDepartments.add(CorpAutoOrgEmpDTO.builder()
                            .thirdOrgId(String.valueOf(department.getId()))
                            .thirdOrgName(department.getName())
                            .build());
                }
            }
        }
        return illegalDepartments;
    }

}

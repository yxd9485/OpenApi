package com.fenbeitong.openapi.plugin.wechat.isv.service;

import com.fenbeitong.openapi.plugin.support.common.dto.OpenApiRespDTO;
import com.fenbeitong.openapi.plugin.support.organization.dto.OrgUnitDTO;
import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.openapi.plugin.support.company.dao.OpenCompanySourceTypeDao;
import com.fenbeitong.openapi.plugin.support.company.entity.OpenCompanySourceType;
import com.fenbeitong.openapi.plugin.support.employee.dto.*;
import com.fenbeitong.openapi.plugin.support.employee.service.AbstractEmployeeService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.util.VirtualPhoneUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WechatUserListRespDTO;
import com.fenbeitong.openapi.plugin.wechat.common.exception.OpenApiWechatException;
import com.fenbeitong.openapi.plugin.wechat.eia.constant.WechatEiaPullOrgConstant;
import com.fenbeitong.openapi.plugin.wechat.eia.entity.QywxEmployee;
import com.fenbeitong.openapi.plugin.wechat.eia.service.wechat.WeChatEmployeeService;
import com.fenbeitong.openapi.plugin.wechat.eia.service.wechat.WeChatOrganizationService;
import com.fenbeitong.openapi.plugin.wechat.isv.constant.WeChatIsvConstant;
import com.fenbeitong.openapi.plugin.wechat.isv.dto.AuthInfoResponse;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WeChatGetUserResponse;
import com.fenbeitong.openapi.plugin.wechat.isv.util.WeChatIsvHttpUtils;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeBaseInfo;
import com.fenbeitong.usercenter.api.service.employee.IThirdEmployeeService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * Created by lizhen on 2020/3/24.
 */
@ServiceAspect
@Service
@Slf4j
public class WeChatIsvEmployeeService extends AbstractEmployeeService {

    //读取成员
    private static final String GET_USER_URL = "/cgi-bin/user/get?access_token={access_token}&userid={userid}";
    //获取部门成员详情列表
    private static final String USER_LIST_URL = "/cgi-bin/user/list?access_token={access_token}&department_id={department_id}&fetch_child={fetch_child}";

    @Value("${wechat.api-host}")
    private String wechatHost;

    @Autowired
    private WeChatIsvHttpUtils wechatIsvHttpUtil;

    @DubboReference(check = false)
    private IThirdEmployeeService iThirdEmployeeService;
    @Autowired
    private WeChatIsvCompanyAuthService weChatIsvCompanyAuthService;

    @Autowired
    private WeChatOrganizationService wechatOrganizationService;

    @Autowired
    private WeChatEmployeeService wechatEmployeeService;

    @Autowired
    private WeChatIsvOrganizationService weChatIsvOrganizationService;

    @Autowired
    private VirtualPhoneUtils virtualPhoneUtils;

    @Autowired
    private OpenCompanySourceTypeDao openCompanySourceTypeDao;

    public Map<String, Object> syncWechatUser(String corpId, String companyId) {
        AuthInfoResponse authInfo = weChatIsvCompanyAuthService.getAuthInfo(corpId);
        List<Integer> allowParty = authInfo.getAuthInfo().getAgent().get(0).getPrivilege().getAllowParty();
        List<String> allowUser = authInfo.getAuthInfo().getAgent().get(0).getPrivilege().getAllowUser();
        //1.按部门拉取微信全量人员
        List<WechatUserListRespDTO.WechatUser> allUsers = new ArrayList<>();
        for (Integer partyId : allowParty) {
            List<WechatUserListRespDTO.WechatUser> wechatUsers = listUser(String.valueOf(partyId), corpId);
            allUsers.addAll(wechatUsers);
        }
        //2.拉取单独添加进来的人员
        for (String userId : allowUser) {
            WeChatGetUserResponse wechatUser = getWechatUser(userId, corpId);
            //使用主部门。主部门为0的置到根部门1
            if ("0".equals(wechatUser.getDepartmentStr())) {
                wechatUser.setMainDepartment(1L);
            }
            ArrayList<Long> departmentList = new ArrayList<>();
            departmentList.add(wechatUser.getMainDepartment());
            wechatUser.setDepartmentList(departmentList);
            WechatUserListRespDTO.WechatUser user = new WechatUserListRespDTO.WechatUser();
            BeanUtils.copyProperties(wechatUser, user);
            allUsers.add(user);
        }
        //3.去重
        List<WechatUserListRespDTO.WechatUser> distinctList = allUsers
                .stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getUserId()))), ArrayList::new))
                .stream().sorted(Comparator.comparing(WechatUserListRespDTO.WechatUser::getUserId)).collect(Collectors.toList());
        //4.拉取分贝通全量人员
        List<EmployeeBaseInfo> employeeBaseInfos = super.listFbEmployee(companyId);
        //5.分贝部门id转三方部门id
        List<OrgUnitDTO> orgUnitDTOS = weChatIsvOrganizationService.listFbOrgUnit(companyId);
        Map<String, List<OrgUnitDTO>> orgUnitMap = orgUnitDTOS.stream().collect(groupingBy(OrgUnitDTO::getOrgUnitId));
        //根部门的三方id转在0,用于比对
        orgUnitMap.get(companyId).get(0).setOrgThirdUnitId("1");
        List<QywxEmployee> fbEmployeeList = employeeBaseInfos.stream().map(employeeBaseInfo -> QywxEmployee.builder()
                .userId(employeeBaseInfo.getThirdEmployeeId())
                .name(employeeBaseInfo.getName())
                .department(orgUnitMap.get(employeeBaseInfo.getDeptId()).get(0).getOrgThirdUnitId())
                .mobile("")
                .extattr("{}")
                .build()).collect(Collectors.toList());
        //6.获取不到人员姓名，人员姓名转空
        for (WechatUserListRespDTO.WechatUser wechatUser : distinctList) {
            wechatUser.setName("");
        }
        Map<String, Object> userMap = wechatEmployeeService.groupUser(distinctList, fbEmployeeList, corpId, companyId);
        //7.将新增的人员手机号生成
        WeChatEmployeeService.WeChatUserAdd userAdd = (WeChatEmployeeService.WeChatUserAdd) userMap.get(WechatEiaPullOrgConstant.INSERT);
        if (userAdd != null) {
            List<SupportCreateEmployeeReqDTO> createUserReqList = userAdd.getCreateUserReqList();
            for (SupportCreateEmployeeReqDTO supportCreateEmployeeReqDTO : createUserReqList) {
                List<SupportEmployeeInsertDTO> employeeList = supportCreateEmployeeReqDTO.getEmployeeList();
                for (SupportEmployeeInsertDTO supportEmployeeInsertDTO : employeeList) {
                    if (StringUtils.isBlank(supportEmployeeInsertDTO.getPhone())) {
                        Long oriVirtualPhone = virtualPhoneUtils.getVirtualPhone(companyId, supportEmployeeInsertDTO.getThirdEmployeeId());
                        supportEmployeeInsertDTO.setPhone(String.valueOf(oriVirtualPhone));
                        supportEmployeeInsertDTO.setName(WeChatIsvConstant.WECHAT_ISV_USER_NAME);
                    }
                }
            }
        }
        //8.微信微信isv取不到人员姓名和手机号，更新的人员手机号和姓名清空，防止覆盖掉用户的信息
        WeChatEmployeeService.WeChatUserUpdate userUpdate = (WeChatEmployeeService.WeChatUserUpdate) userMap.get(WechatEiaPullOrgConstant.UPDATE);
        if (userUpdate != null) {
            List<SupportUpdateEmployeeReqDTO> updateUserReqList = userUpdate.getUpdateUserReqList();
            for (SupportUpdateEmployeeReqDTO supportCreateEmployeeReqDTO : updateUserReqList) {
                List<SupportEmployeeUpdateDTO> employeeList = supportCreateEmployeeReqDTO.getEmployeeList();
                for (SupportEmployeeUpdateDTO supportEmployeeInsertDTO : employeeList) {
                    if (StringUtils.isBlank(supportEmployeeInsertDTO.getPhone())) {
                        supportEmployeeInsertDTO.setPhone(null);
                        supportEmployeeInsertDTO.setName(null);
                    }
                }
            }
        }
        return userMap;
    }

    /**
     * 获取部门人员
     *
     * @param corpId
     * @param departmentId
     */
    public List<WechatUserListRespDTO.WechatUser> listUser(String departmentId, String corpId) {
        Map<String, String> param = new HashMap<>();
        param.put("department_id", departmentId);
        param.put("fetch_child", "1");
        String res = wechatIsvHttpUtil.getJsonWithAccessToken(wechatHost + USER_LIST_URL, param, corpId);
        log.info("根据企业微信部门ID获取用户集合返回数据 {}", res);
        WechatUserListRespDTO wechatUserListResp = JsonUtils.toObj(res, WechatUserListRespDTO.class);
        if (wechatUserListResp == null || Optional.ofNullable(wechatUserListResp.getErrCode()).orElse(-1) != 0) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_CORP_EMPLOYEE_IS_NULL));
        }
        return wechatUserListResp.getUserList();
    }


    /**
     * 获取微信人员
     *
     * @param userId
     * @param corpId
     */
    public WeChatGetUserResponse getWechatUser(String userId, String corpId) {
        Map<String, String> param = new HashMap<>();
        param.put("userid", userId);
        String res = wechatIsvHttpUtil.getJsonWithAccessToken(wechatHost + GET_USER_URL, param, corpId);
        WeChatGetUserResponse weChatGetUserResponse = JsonUtils.toObj(res, WeChatGetUserResponse.class);
        if (weChatGetUserResponse == null || (Optional.ofNullable(weChatGetUserResponse.getErrCode()).orElse(-1) != 0)) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_CORP_EMPLOYEE_IS_NULL));
        }
        return weChatGetUserResponse;
    }

    /**
     * 清除企业下所有人员
     *
     * @param companyId
     */
    public void clearAllEmployee(String companyId, String operatorId) {
        operatorId = superAdmin(companyId);
        List<EmployeeBaseInfo> employeeList = super.listFbEmployee(companyId);
        if (employeeList != null && employeeList.size() > 0) {
            List<String> thirdEmployeeIds = employeeList.stream().map(EmployeeBaseInfo::getThirdEmployeeId).collect(Collectors.toList());
            SupportDeleteEmployeeReqDTO req = new SupportDeleteEmployeeReqDTO();
            req.setOperatorId(operatorId);
            req.setCompanyId(companyId);
            req.setThirdEmployeeIds(thirdEmployeeIds);
            super.deleteUser(req);
        }
    }


    public void createEmployee(WeChatEmployeeService.WeChatUserAdd userAdd) {
        if (userAdd != null) {
            List<SupportCreateEmployeeReqDTO> createUserReqList = userAdd.getCreateUserReqList();
            List<List<SupportCreateEmployeeReqDTO>> batchSupportCreateEmployeeReqList = batch(createUserReqList);
            for (int i = 0; i < batchSupportCreateEmployeeReqList.size(); i++) {
                List<SupportCreateEmployeeReqDTO> currentBatchCreateEmployeeReqList = batchSupportCreateEmployeeReqList.get(i);
                List<SupportEmployeeInsertDTO> employeeInsertList = Lists.newArrayList();
                currentBatchCreateEmployeeReqList.forEach(req -> employeeInsertList.addAll(req.getEmployeeList()));
                SupportCreateEmployeeReqDTO supportCreateEmployeeReq = currentBatchCreateEmployeeReqList.get(0);
                supportCreateEmployeeReq.setEmployeeList(employeeInsertList);
                createUser(supportCreateEmployeeReq);
            }
        }
    }

    public void updateEmployee(WeChatEmployeeService.WeChatUserUpdate userUpdate) {
        if (userUpdate != null) {
            List<SupportUpdateEmployeeReqDTO> updateUserReqList = userUpdate.getUpdateUserReqList();
            List<List<SupportUpdateEmployeeReqDTO>> batchSupportUpdateEmployeeReqList = batch(updateUserReqList);
            for (int i = 0; i < batchSupportUpdateEmployeeReqList.size(); i++) {
                List<SupportUpdateEmployeeReqDTO> currentBatchUpdateEmployeeReqList = batchSupportUpdateEmployeeReqList.get(i);
                List<SupportEmployeeUpdateDTO> employeeUpdateList = Lists.newArrayList();
                currentBatchUpdateEmployeeReqList.forEach(req -> employeeUpdateList.addAll(req.getEmployeeList()));
                SupportUpdateEmployeeReqDTO supportUpdateEmployeeReq = currentBatchUpdateEmployeeReqList.get(0);
                supportUpdateEmployeeReq.setEmployeeList(employeeUpdateList);
                updateUser(supportUpdateEmployeeReq);
            }
        }
    }

    public void deleteEmployee(WeChatEmployeeService.WeChatUserDelete userDelete) {
        if (userDelete != null) {
            List<SupportDeleteEmployeeReqDTO> deleteUserReqList = userDelete.getDeleteUserReqList();
            List<List<SupportDeleteEmployeeReqDTO>> batchSupportDeleteEmployeeReqList = batch(deleteUserReqList);
            for (int i = 0; i < batchSupportDeleteEmployeeReqList.size(); i++) {
                List<SupportDeleteEmployeeReqDTO> currentBatchDeleteEmployeeReqList = batchSupportDeleteEmployeeReqList.get(i);
                List<String> thirdEmployeeIdList = Lists.newArrayList();
                currentBatchDeleteEmployeeReqList.forEach(req -> thirdEmployeeIdList.addAll(req.getThirdEmployeeIds()));
                SupportDeleteEmployeeReqDTO supportDeleteEmployeeReq = currentBatchDeleteEmployeeReqList.get(0);
                supportDeleteEmployeeReq.setThirdEmployeeIds(thirdEmployeeIdList);
                deleteUser(supportDeleteEmployeeReq);
            }
        }
    }

    /**
     * 分组
     *
     * @param list 原始参数
     * @param <T>  类型
     * @return 分组数据
     */
    private <T> List<List<T>> batch(List<T> list) {
        int count = 50;
        List<T> batchList = Lists.newArrayList();
        List<List<T>> batchReqList = Lists.newArrayList();
        list.forEach(item -> {
            batchList.add(item);
            if (batchList.size() > 0 && batchList.size() % count == 0) {
                batchReqList.add(Lists.newArrayList(batchList));
                batchList.clear();
            }
        });
        if (!batchList.isEmpty()) {
            batchReqList.add(Lists.newArrayList(batchList));
        }
        return batchReqList;
    }

    @Override
    public OpenApiRespDTO bindUser(SupportBindEmployeeReqDTO req) {
        return super.bindUser(req);
    }


    @Override
    protected IThirdEmployeeService getThirdEmployeeService() {
        return iThirdEmployeeService;
    }


    /**
     * 获取微信ISV全量部门
     *
     * @param corpId
     * @return
     */
    public List<WechatUserListRespDTO.WechatUser> listAllUser(String corpId) {
        AuthInfoResponse authInfo = weChatIsvCompanyAuthService.getAuthInfo(corpId);
        List<Integer> allowParty = authInfo.getAuthInfo().getAgent().get(0).getPrivilege().getAllowParty();
        List<String> allowUser = authInfo.getAuthInfo().getAgent().get(0).getPrivilege().getAllowUser();
        //1.按部门拉取微信全量人员
        List<WechatUserListRespDTO.WechatUser> allUsers = new ArrayList<>();
        for (Integer partyId : allowParty) {
            List<WechatUserListRespDTO.WechatUser> wechatUsers = listUser(String.valueOf(partyId), corpId);
            allUsers.addAll(wechatUsers);
        }
        //2.拉取单独添加进来的人员
        for (String userId : allowUser) {
            WeChatGetUserResponse wechatUser = getWechatUser(userId, corpId);
            //使用主部门。主部门为0的，为部门不可见，则置到根部门1
            if ("0".equals(wechatUser.getDepartmentStr()) || StringUtils.isBlank(wechatUser.getDepartmentStr())) {
                wechatUser.setMainDepartment(1L);
            }
            ArrayList<Long> departmentList = new ArrayList<>();
            departmentList.add(wechatUser.getMainDepartment());
            wechatUser.setDepartmentList(departmentList);
            WechatUserListRespDTO.WechatUser user = new WechatUserListRespDTO.WechatUser();
            BeanUtils.copyProperties(wechatUser, user);
            allUsers.add(user);
        }
        //3.去重
        List<WechatUserListRespDTO.WechatUser> distinctList = allUsers
                .stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getUserId()))), ArrayList::new))
                .stream().sorted(Comparator.comparing(WechatUserListRespDTO.WechatUser::getUserId)).collect(Collectors.toList());
        //6.获取不到人员姓名，人员姓名固定填充
        for (WechatUserListRespDTO.WechatUser wechatUser : distinctList) {
            wechatUser.setName(WeChatIsvConstant.WECHAT_ISV_USER_NAME);
        }
        return distinctList;
    }


    public void savePhoneNumToRedis(String companyId) {
        if (StringUtils.isBlank(companyId)) {
            List<OpenCompanySourceType> openCompanySourceTypes = openCompanySourceTypeDao.listAll();
            for (OpenCompanySourceType openCompanySourceType : openCompanySourceTypes) {
                Integer openType = openCompanySourceType.getOpenType();
                if (OpenType.WECHAT_ISV.getType() == openType || OpenType.FEISHU_ISV.getType() == openType
                        || OpenType.WELINK_ISV.getType() == openType || OpenType.DINGTALK_ISV.getType() == openType) {
                    companyId = openCompanySourceType.getCompanyId();
                    List<EmployeeBaseInfo> employeeBaseInfos = super.listFbEmployee(companyId);
                    saveEmployeePhone(employeeBaseInfos, companyId);
                }
            }
        } else {
            List<EmployeeBaseInfo> employeeBaseInfos = super.listFbEmployee(companyId);
            saveEmployeePhone(employeeBaseInfos, companyId);
        }
    }

    private void saveEmployeePhone(List<EmployeeBaseInfo> employeeBaseInfos, String companyId) {
        if (!ObjectUtils.isEmpty(employeeBaseInfos)) {
            for (EmployeeBaseInfo employeeBaseInfo : employeeBaseInfos) {
                String thirdEmployeeId = employeeBaseInfo.getThirdEmployeeId();
                Long phoneNum = NumericUtils.obj2long(employeeBaseInfo.getPhone());
                virtualPhoneUtils.updatePhoneNum(companyId, thirdEmployeeId, phoneNum);
            }
        }
    }

}

package com.fenbeitong.openapi.plugin.feishu.common.service.attendance;

import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResponseUtils;
import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResultEntity;
import com.fenbeitong.openapi.plugin.feishu.common.dao.OpenFeishuGroupInfoDao;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuUserInfoDTO;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeishuAttendanceGroupDetail;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeishuAttendanceRespDTO;
import com.fenbeitong.openapi.plugin.feishu.common.entity.OpenFeishuGroupInfo;
import com.fenbeitong.openapi.plugin.feishu.common.service.FeishuAttendanceInterface;
import com.fenbeitong.openapi.plugin.feishu.eia.service.FeiShuEiaEmployeeService;
import com.fenbeitong.openapi.plugin.support.company.dao.AuthDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.AuthDefinition;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.employee.service.BaseEmployeeRefServiceImpl;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdEmployeeDao;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdEmployee;
import com.fenbeitong.openapi.plugin.support.util.SubListUtils;
import com.fenbeitong.openapi.plugin.support.voucher.dao.AttendanceDao;
import com.fenbeitong.openapi.plugin.support.voucher.dto.CreateAttendanceReq;
import com.fenbeitong.openapi.plugin.support.voucher.entity.Attendance;
import com.fenbeitong.openapi.plugin.util.*;
import com.fenbeitong.openapi.sdk.dto.common.OpenApiRespDTO;
import com.fenbeitong.openapi.sdk.dto.employee.GetUserByPhoneNumReqDTO;
import com.fenbeitong.openapi.sdk.dto.employee.GetUserByPhoneNumRespDTO;
import com.fenbeitong.openapi.sdk.webservice.employee.FbtEmployeeService;
import com.fenbeitong.usercenter.api.model.dto.employee.*;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import com.fenbeitong.usercenter.api.service.employee.IThirdEmployeeService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import retrofit2.Call;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Auther zhang.peng
 * @Date 2021/9/25
 */
@Service
@Slf4j
public class FeishuAttendanceServiceImpl implements FeishuAttendanceInterface {

    @DubboReference(check = false)
    private IBaseEmployeeExtService iBaseEmployeeExtService;

    @Autowired
    private OpenThirdEmployeeDao openThirdEmployeeDao;

    @Autowired
    private AttendanceDao attendanceDao;

    @Autowired
    private FbtEmployeeService fbtUserCenterService;

    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    private BaseEmployeeRefServiceImpl baseEmployeeRefService;

    @Autowired
    private AuthDefinitionDao authDefinitionDao;

    @Autowired
    private FeiShuEiaEmployeeService feiShuEiaEmployeeService;

    @Autowired
    private OpenFeishuGroupInfoDao openFeishuGroupInfoDao;

    @Override
    public FeiShuResultEntity createAttendanceGroupInfo(String companyId, List<String> userNames) {
        // 根据用户名查询打卡记录
        List<OpenThirdEmployee> openThirdEmployeeList = openThirdEmployeeDao.listEmployeeByThirdEmployeeName(OpenType.FEISHU_EIA.getType(),companyId,userNames);
        if (CollectionUtils.isBlank(openThirdEmployeeList)){
            log.info("未查到用户信息 , companyId : {} ",companyId);
            return FeiShuResponseUtils.error(-1, "查询考勤组id失败,用户不存在");
        }
        PluginCorpDefinition pluginCorpDefinition = pluginCorpDefinitionDao.getByCompanyId(companyId);
        if ( null == pluginCorpDefinition ){
            log.info("公司信息未配置 , 请检查 dingtalk_corp ");
            return FeiShuResponseUtils.error(-1, "查询考勤组id失败,公司信息未配置");
        }
        List<String> thirdEmployeeIds = openThirdEmployeeList.stream().map(OpenThirdEmployee::getThirdEmployeeId).collect(Collectors.toList());
        int yesterday = Integer.parseInt(DateUtils.toStr(DateUtils.yesterday(),DateUtils.FORMAT_DATE_PATTERN_NO_BREAK));
        // 将打卡记录的 group_id 落库
        List<FeishuAttendanceRespDTO.TaskResult> taskResultList = feiShuEiaEmployeeService.getCheckRecordInfoList(thirdEmployeeIds, yesterday, yesterday,pluginCorpDefinition.getThirdCorpId());
        if (CollectionUtils.isBlank(taskResultList)){
            log.info("考勤组ID为空");
            return FeiShuResponseUtils.error(-1, "查询考勤组id为空");
        }
        // 查询原有考勤组信息
        List<OpenFeishuGroupInfo> oldGroupInfoList = openFeishuGroupInfoDao.getByCompanyId(companyId);
        Set<String> oldGroupSet = CollectionUtils.isBlank(oldGroupInfoList) ? new HashSet<>() : oldGroupInfoList.stream().map(OpenFeishuGroupInfo::getGroupId).collect(Collectors.toSet());
        // 找到原有记录里最大的那个值
        int maxOldGroupId = CollectionUtils.isBlank(oldGroupInfoList) ? 0 : Integer.parseInt(oldGroupInfoList.stream().max((o1, o2)->o1.getVoucherGroupId().compareTo(o2.getVoucherGroupId())).get().getVoucherGroupId());
        List<String> groupIds = taskResultList.stream().map(FeishuAttendanceRespDTO.TaskResult::getGroup_id).distinct().collect(Collectors.toList());
        List<OpenFeishuGroupInfo> openFeishuGroupInfoList = new ArrayList<>();
        for (int i = 0; i < groupIds.size(); i++) {
            if ( !oldGroupSet.isEmpty() && oldGroupSet.contains(groupIds.get(i))){
                continue;
            }
            OpenFeishuGroupInfo openFeishuGroupInfo = new OpenFeishuGroupInfo();
            openFeishuGroupInfo.setId(RandomUtils.bsonId());
            openFeishuGroupInfo.setCompanyId(companyId);
            openFeishuGroupInfo.setGroupId(groupIds.get(i));
            maxOldGroupId++;
            openFeishuGroupInfo.setVoucherGroupId(maxOldGroupId+"");
            openFeishuGroupInfo.setStatus("1");
            openFeishuGroupInfoList.add(openFeishuGroupInfo);
        }
        int result = 0;
        if (CollectionUtils.isNotBlank(openFeishuGroupInfoList)){
            result = openFeishuGroupInfoDao.saveList(openFeishuGroupInfoList);
        }
        return result > 0 ? FeiShuResponseUtils.success("查询考勤组ID成功") : FeiShuResponseUtils.error(-1,"保存考勤组ID失败,没有新增的考勤组信息");
    }

    public void saveAttendance(List<FeishuAttendanceRespDTO.TaskResult> taskResultList , String companyId ,List<String> allUserIds ) {
        List<OpenFeishuGroupInfo> openFeishuGroupInfoList = openFeishuGroupInfoDao.getByCompanyId(companyId);
        Map<String,String> map = new HashMap<>();
        if (CollectionUtils.isNotBlank(openFeishuGroupInfoList)){
            map = openFeishuGroupInfoList.stream().collect(Collectors.toMap(OpenFeishuGroupInfo::getGroupId,OpenFeishuGroupInfo::getVoucherGroupId));
        }
        Map<String,String> feishuGroupId2VoucherGroupIdMap = map;
        AuthDefinition authDefinition = authDefinitionDao.getAuthInfoByAppId(companyId);
        String companyName = authDefinition.getAppName();
        List<Attendance> attendanceList = new ArrayList<>();
        if (CollectionUtils.isNotBlank(taskResultList)) {
            // 判断是否有当天时间的数据 , 如果有 , 说明不用记录此次考勤数据
            Date workDate = DateUtils.toDate(taskResultList.get(0).getDay(),DateUtils.FORMAT_DATE_PATTERN_NO_BREAK);
            List<Attendance> oldAttendanceList = attendanceDao.listAttendanceByWorkDate(companyId,workDate);
            if (CollectionUtils.isNotBlank(oldAttendanceList)){
                log.info("当前时间 {} 已经有考勤数据,无需重复记录",workDate);
                return;
            }
            taskResultList.forEach(taskResult -> {
                if ( null == taskResult || StringUtils.isBlank(taskResult.getGroup_id())){
                    return;
                }
                Attendance attendance = new Attendance();
                attendance.setCompanyId(companyId);
                ThirdEmployeeRes thirdEmployeeRes = getEmployeeInfo(companyId,taskResult.getUser_id());
                ThirdEmployeeBean openThirdEmployee = null == thirdEmployeeRes ? new ThirdEmployeeBean() : thirdEmployeeRes.getEmployee();
                if ( null != openThirdEmployee ){
                    attendance.setEmployeePhone(openThirdEmployee.getPhone_num());
                    attendance.setEmployeeId(openThirdEmployee.getId());
                    attendance.setEmployeeName(openThirdEmployee.getName());
                }
                attendance.setCompanyName(companyName);
                // 员工id
                attendance.setThirdUserId(taskResult.getUser_id());
                // 员工名称
                attendance.setEmployeeName(taskResult.getEmployee_name());
                List<FeishuAttendanceRespDTO.CheckRecord> checkRecordList = taskResult.getRecords();
                if (CollectionUtils.isNotBlank(checkRecordList)){
                    checkRecordList.forEach(checkRecord -> {
                        if ( null == checkRecord ){
                            return;
                        }
                        FeishuAttendanceRespDTO.CheckInResult checkInResult = checkRecord.getCheck_in_record();
                        FeishuAttendanceRespDTO.CheckOutResult checkOutResult = checkRecord.getCheck_out_record();
                        if ( null != checkInResult && !StringUtils.isBlank(checkInResult.getCheck_time()) ){
                            attendance.setUserCheckInTime(DateUtils.toDate(Long.valueOf(checkInResult.getCheck_time())*1000));
                        }
                        if ( null != checkOutResult && !StringUtils.isBlank(checkOutResult.getCheck_time()) ){
                            attendance.setUserCheckOutTime(DateUtils.toDate(Long.valueOf(checkOutResult.getCheck_time())*1000));
                        }
                        if ( !StringUtils.isBlank(checkRecord.getCheck_in_shift_time())){
                            attendance.setBaseCheckInTime(DateUtils.toDate(Long.valueOf(checkRecord.getCheck_in_shift_time())*1000));
                        }
                        if ( !StringUtils.isBlank(checkRecord.getCheck_out_shift_time())){
                            attendance.setBaseCheckOutTime(DateUtils.toDate(Long.valueOf(checkRecord.getCheck_out_shift_time())*1000));
                        }
                        // 是否外勤
                        attendance.setAttr1( null == checkOutResult ? "" : checkOutResult.isField()+"");
                        // 下班打卡是否正常
                        attendance.setAttr2( checkRecord.getCheck_out_result() );
                        // 下班打卡经度
                        attendance.setAttr3( null == checkOutResult ? "" : checkOutResult.getLongitude()+"");
                        // 下班打卡纬度
                        attendance.setAttr4( null == checkOutResult ? "" : checkOutResult.getLatitude()+"");
                        // 下班打卡 recordId
                        attendance.setAttr5( checkRecord.getCheck_out_record_id());
                    });
                }
                // 工作日时间
                attendance.setWorkDate(DateUtils.toDate(taskResult.getDay(),DateUtils.FORMAT_DATE_PATTERN_NO_BREAK));
                attendance.setStatus(0);
                // 审批类型为 0
                attendance.setApproveType(0);
                // 发放标识为 1 , 需要发放
                attendance.setGrantVoucherFlag(1);
                // 飞书考勤组id ： 映射到分贝通的考勤组id
                String voucherGroupId = feishuGroupId2VoucherGroupIdMap.get(taskResult.getGroup_id());
                if (StringUtils.isBlank(voucherGroupId)){
                    log.info("该条考勤没有考勤组数据,过滤");
                    return;
                }
                attendance.setGroupId( Long.valueOf(voucherGroupId) );
                attendanceList.add(attendance);
            });
            log.info("记录飞书打卡数据 : json : {}", JsonUtils.toJson(attendanceList));
            attendanceDao.saveList(attendanceList);
        }
    }


    private void createAttendanceRecordList(CreateAttendanceReq req, List<GetUserByPhoneNumRespDTO> fbUserList, List<OpenThirdEmployeeDTO> weChatUserList, Date workDate) {
        Map<String, OpenThirdEmployeeDTO> weChatUserMap = weChatUserList.stream().collect(Collectors.toMap(OpenThirdEmployeeDTO::getThirdEmployeePhone, dingTalkUser -> dingTalkUser, (o, n) -> n));
        List<Attendance> attendanceList = fbUserList.stream()
                .map(user ->
                        Attendance.builder()
                                .employeeId(user.getId())
                                .employeeName(user.getUserName())
                                .employeePhone(user.getUserPhone())
                                .companyId(user.getCompanyId())
                                .companyName(user.getCompanyName())
                                .wechatUserId(weChatUserMap.get(user.getUserPhone()).getThirdEmployeeId())
                                .grantVoucherFlag(1)
                                .workDate(workDate)
                                .build()
                )
                .collect(Collectors.toList());
        //指定发券的部门
        List<String> limitOrgIdList = req.getLimitOrgIdList();
        if (!ObjectUtils.isEmpty(limitOrgIdList)) {
            List<String> grantVoucherUserIdList = getUserIdListByOrgIdList(req.getCompanyId(), limitOrgIdList);
            if (!ObjectUtils.isEmpty(grantVoucherUserIdList)) {
                attendanceList.forEach(attendance -> {
                    //限制部门时只有对应部门的员工才发券 否则不发 grantVoucherFlag=0
                    if (!grantVoucherUserIdList.contains(attendance.getEmployeeId())) {
                        attendance.setGrantVoucherFlag(0);
                    }
                });
            }
        }
        //指定不发券的部门
        List<String> excludeOrgIdList = req.getExcludeOrgIdList();
        if (!ObjectUtils.isEmpty(excludeOrgIdList)) {
            List<String> excludeUserIdList = getUserIdListByOrgIdList(req.getCompanyId(), excludeOrgIdList);
            if (!ObjectUtils.isEmpty(excludeUserIdList)) {
                attendanceList.forEach(attendance -> {
                    //排除发券对应部门的员工 不发 grantVoucherFlag=0
                    if (excludeUserIdList.contains(attendance.getEmployeeId())) {
                        attendance.setGrantVoucherFlag(0);
                    }
                });
            }
        }
        attendanceList = filterData(attendanceList, workDate, req.getCompanyId());
        if (!ObjectUtils.isEmpty(attendanceList)) {
            attendanceDao.saveList(attendanceList);
        }
    }

    private List<Attendance> filterData(List<Attendance> attendanceList, Date workDate, String companyId) {
        Example example = new Example(Attendance.class);
        example.createCriteria().andEqualTo("workDate", workDate).andEqualTo("companyId", companyId);
        List<Attendance> oldData = attendanceDao.listByExample(example);
        Map<String, Attendance> oldMap = oldData.stream().collect(Collectors.toMap(t -> t.getCompanyId() + t.getEmployeeId() + DateUtils.toSimpleStr(t.getWorkDate(), true), Function.identity(), (o, n) -> n));
        return attendanceList.stream().filter(t -> !oldMap.containsKey(t.getCompanyId() + t.getEmployeeId() + DateUtils.toSimpleStr(t.getWorkDate(), true))).collect(Collectors.toList());
    }

    private List<String> getUserIdListByOrgIdList(String companyId, List<String> orgIdList) {
        List<String> userIds = Lists.newArrayList();
        orgIdList.forEach(orgId -> {
            int page = 1;
            EmployeePageListResult employeePageListResult = iBaseEmployeeExtService.queryEmployeeByDeptId(2, companyId, orgId, 100, page);
            int count = employeePageListResult == null ? 0 : employeePageListResult.getCount();
            List<String> userIdList = employeePageListResult == null || employeePageListResult.getData() == null ? Lists.newArrayList() : employeePageListResult.getData().stream().map(EmployeeBaseInfo::getId).collect(Collectors.toList());
            while (userIdList.size() != count) {
                EmployeePageListResult pageResult = iBaseEmployeeExtService.queryEmployeeByDeptId(2, companyId, orgId, 100, ++page);
                List<String> currentPageUserIdList = pageResult == null || pageResult.getData() == null ? Lists.newArrayList() : pageResult.getData().stream().map(EmployeeBaseInfo::getId).collect(Collectors.toList());
                userIdList.addAll(currentPageUserIdList);
            }
            userIds.addAll(userIdList);
        });
        return userIds;
    }

    private List<GetUserByPhoneNumRespDTO> listVoucherGrantFbUsersByPhoneNum(String corpId, List<String> phoneNumList) throws Exception {
        PluginCorpDefinition corpDefinition = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId);
        Call<OpenApiRespDTO<List<GetUserByPhoneNumRespDTO>>> userInfoByPhoneNum = fbtUserCenterService.getUserInfoByPhoneNum(GetUserByPhoneNumReqDTO.builder()
                .phoneNums(phoneNumList)
                .companyId(corpDefinition.getAppId())
                .build());
        OpenApiRespDTO<List<GetUserByPhoneNumRespDTO>> respDTO = userInfoByPhoneNum.execute().body();
        List<GetUserByPhoneNumRespDTO> fbUserList = respDTO == null ? Lists.newArrayList() : respDTO.getData();
        //  sendMsgIfPhoneNumNotInFinhub(corpId, phoneNumList, fbUserList);
        return fbUserList;
    }

    private ThirdEmployeeRes getEmployeeInfo(String companyId, String thirdEmployeeId ){
        ThirdEmployeeContract thirdEmployeeContract = new ThirdEmployeeContract();
        thirdEmployeeContract.setCompanyId(companyId);
        thirdEmployeeContract.setEmployeeId(thirdEmployeeId);
        thirdEmployeeContract.setType(1);
        thirdEmployeeContract.setUserType(2);
        //调用uc接口根据公司ID和人员ID获取手机号
        try {
            IThirdEmployeeService thirdEmployeeService = baseEmployeeRefService.getThirdEmployeeService();
            ThirdEmployeeRes thirdEmployeeRes = thirdEmployeeService.queryEmployeeInfo(thirdEmployeeContract);
            return thirdEmployeeRes;
        }catch (Exception e){
            log.warn("获取失败:{}",e.getMessage());
            return null;
        }
    }

    @Async
    @Override
    public void pullAttendanceRecords( String companyId, boolean useCustomDay , int dayTime ){
        // 根据公司id，获取考勤组id
        List<OpenFeishuGroupInfo> groupInfoList = openFeishuGroupInfoDao.getByCompanyId(companyId);
        // 根据考勤组id,获取考勤组下面的员工信息
        if (CollectionUtils.isBlank(groupInfoList)){
            log.info("考勤组信息为空 , 生成考勤失败");
            return;
        }
        PluginCorpDefinition pluginCorpDefinition = pluginCorpDefinitionDao.getByCompanyId(companyId);
        if ( null == pluginCorpDefinition ){
            log.info("公司信息未配置 , 请检查 dingtalk_corp ");
            return;
        }
        List<String> allUserIds = new ArrayList<>();
        groupInfoList.stream().forEach(groupInfo -> {
            FeishuAttendanceGroupDetail.GroupDetail groupDetail = feiShuEiaEmployeeService.getAttendanceDetail(groupInfo.getGroupId(),pluginCorpDefinition.getThirdCorpId());
            log.info("飞书考勤组详情数据 : json : {}", JsonUtils.toJson(groupDetail));
            List<String> userIds = new ArrayList<>();
            userIds.addAll(groupDetail.getBind_user_ids());
            // 把部门人员也添加进来
            if (CollectionUtils.isNotBlank(groupDetail.getBind_dept_ids())){
                for (String departmentId : groupDetail.getBind_dept_ids()) {
                    List<FeiShuUserInfoDTO> feiShuIsvUserInfos = feiShuEiaEmployeeService.departmentUserIdsList(departmentId, pluginCorpDefinition.getThirdCorpId(), null);
                    List<String> userIdInDepartment = CollectionUtils.isBlank(feiShuIsvUserInfos) ? new ArrayList<>() : feiShuIsvUserInfos.stream().map(FeiShuUserInfoDTO::getEmployeeId).collect(Collectors.toList());
                    userIds.addAll(userIdInDepartment);
                }
            }
            allUserIds.addAll(userIds);
        });
        // 根据员工信息获取打卡记录，并落库
        // 开始时间默认昨天
        int yesterday = Integer.parseInt(DateUtils.toStr(DateUtils.yesterday(),DateUtils.FORMAT_DATE_PATTERN_NO_BREAK));
        // 结束时间默认昨天
        int startTime = yesterday;
        int endTime = yesterday;
        // 如果指定了时间,采用指定时间
        if ( useCustomDay ){
            startTime = dayTime;
            endTime = dayTime;
        }
        List<FeishuAttendanceRespDTO.TaskResult> taskResultList = new ArrayList<>();
        List<String> newAllUserIds = new ArrayList<>();
        newAllUserIds = allUserIds.stream().distinct().collect(Collectors.toList());
        List<List<String>> subUserIds = SubListUtils.groupListByQuantity(newAllUserIds,50);
        if (CollectionUtils.isNotBlank(subUserIds)){
            for (List<String> eachSubUserIds : subUserIds) {
                List<FeishuAttendanceRespDTO.TaskResult> eachTaskResultList = feiShuEiaEmployeeService.getCheckRecordInfoList(eachSubUserIds, startTime, endTime,pluginCorpDefinition.getThirdCorpId());
                taskResultList.addAll(eachTaskResultList);
            }
        }
        if (CollectionUtils.isBlank(taskResultList)){
            return;
        }
        saveAttendance(taskResultList,companyId,allUserIds);
    }
}

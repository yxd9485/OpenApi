package com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import com.alibaba.fastjson.JSON;
import com.dingtalk.api.response.OapiAttendanceListResponse.Recordresult;
import com.dingtalk.api.response.OapiDepartmentListResponse;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingTakCheckType;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingTalkSourceType;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingtalkLocationResult;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingtalkTimeResult;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dao.AttendanceDingtalkDao;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkUser;
import com.fenbeitong.openapi.plugin.dingtalk.eia.entity.AttendanceDingtalk;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.*;
import com.fenbeitong.openapi.plugin.func.apply.dto.ApplyOrderDetailDTO;
import com.fenbeitong.openapi.plugin.func.apply.service.FuncTripApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.employee.service.AbstractEmployeeService;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.support.voucher.dao.AttendanceDao;
import com.fenbeitong.openapi.plugin.support.voucher.dto.CreateAttendanceReq;
import com.fenbeitong.openapi.plugin.support.voucher.entity.Attendance;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.sdk.dto.common.OpenApiRespDTO;
import com.fenbeitong.openapi.sdk.dto.employee.GetUserByPhoneNumReqDTO;
import com.fenbeitong.openapi.sdk.dto.employee.GetUserByPhoneNumRespDTO;
import com.fenbeitong.openapi.sdk.webservice.employee.FbtEmployeeService;
import com.fenbeitong.saasplus.api.service.apply.IApplyOrderService;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeBaseInfo;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeePageListResult;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;
import retrofit2.Call;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>Title: DingtalkAttendanceServiceImpl</p>
 * <p>Description: 钉钉考勤服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/1/17 11:52 AM
 */
@Slf4j
@ServiceAspect
@Service
public class DingtalkAttendanceServiceImpl extends AbstractEmployeeService implements IDingtalkAttendanceService {

    @Autowired
    private IDingtalkCorpService dingtalkCorpService;

    @Autowired
    private IApiDepartmentService apiDepartmentService;

    @Autowired
    private IApiUserService dingtalkUserService;

    @Autowired
    private FbtEmployeeService fbtUserCenterService;

    @Autowired
    private IDingTalkNoticeService dingTalkNoticeService;

    @Autowired
    private IApiAttendanceService apiAttendanceService;

    @Autowired
    private AttendanceDao attendanceDao;

    @Autowired
    private AttendanceDingtalkDao attendanceDingtalkDao;

    @DubboReference(check = false)
    private IBaseEmployeeExtService iBaseEmployeeExtService;

    @DubboReference(check = false)
    private IApplyOrderService applyOrderService;

    @Value("${host.saas_plus}")
    private String saasplusUrl;

    @Autowired
    FuncTripApplyServiceImpl funcTripApplyService;

    @Override
    public void createAttendance(CreateAttendanceReq req, Date workDate) throws Exception {
        String companyId = req.getCompanyId();
        PluginCorpDefinition corpDefinition = dingtalkCorpService.getByCompanyId(companyId);
        //钉钉平台企业ID
        String thirdCorpId = corpDefinition.getThirdCorpId();
        //有考勤权限的钉钉用户
        List<DingtalkUser> dingtalkUserList = listVoucherGrantUsers(thirdCorpId);
        log.info("companyId:{},配置了分贝通手机号的员工总数:{}。", companyId, dingtalkUserList.size());
        if (!ObjectUtils.isEmpty(dingtalkUserList)) {
            //分贝通手机号
            List<String> phoneNumList = Lists.newArrayList(dingtalkUserList.stream().map(u -> ObjectUtils.isEmpty(u.getFbtMobile()) ? u.getMobile() : u.getFbtMobile()).collect(Collectors.toSet()));
            //有考勤权限的分贝通用户
            List<GetUserByPhoneNumRespDTO> fbUserList = listVoucherGrantFbUsersByPhoneNum(thirdCorpId, phoneNumList);
            fbUserList = ObjectUtils.isEmpty(fbUserList) ? Lists.newArrayList() : fbUserList.stream().filter(u -> !ObjectUtils.isEmpty(u.getId())).collect(Collectors.toList());
            sendMsgIfPhoneNumNotInFinhub(thirdCorpId, phoneNumList, fbUserList);
            //生成考勤表
            createAttendanceRecordList(req, fbUserList, dingtalkUserList, workDate);
        }
    }

    private void createAttendanceRecordList(CreateAttendanceReq req, List<GetUserByPhoneNumRespDTO> fbUserList, List<DingtalkUser> dingtalkUserList, Date workDate) {
        Map<String, DingtalkUser> dingtalkUserMap = dingtalkUserList.stream().collect(Collectors.toMap(u -> ObjectUtils.isEmpty(u.getFbtMobile()) ? u.getMobile() : u.getFbtMobile(), dingTalkUser -> dingTalkUser, (o, n) -> n));
        List<Attendance> attendanceList = fbUserList.stream()
                .map(user ->
                        Attendance.builder()
                                .employeeId(user.getId())
                                .employeeName(user.getUserName())
                                .employeePhone(user.getUserPhone())
                                .companyId(user.getCompanyId())
                                .companyName(user.getCompanyName())
                                .dingtalkUserId(dingtalkUserMap.get(user.getUserPhone()).getUserid())
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
        // 过滤重复数据
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

    @Override
    public List<DingtalkUser> listVoucherGrantUsers(String corpId) {
        Set<DingtalkUser> dingtalkUserSet = Sets.newHashSet();
        List<OapiDepartmentListResponse.Department> departments = apiDepartmentService.listDepartment(corpId);
        if (!ObjectUtils.isEmpty(departments)) {
            departments.forEach(department -> {
                try {
                    //请求钉钉不要太快 防止超过频率限制
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                List<DingtalkUser> userList = dingtalkUserService.getAllUserByDepartment(department.getId(), corpId);
                if (!userList.isEmpty()) {
                    dingtalkUserSet.addAll(userList);
                }
            });
        }
        List<DingtalkUser> userList = dingtalkUserService.getAllUserByDepartment(1L, corpId);
        if (!userList.isEmpty()) {
            dingtalkUserSet.addAll(userList);
        }
        log.info("corpId:{},员工总数:{}。", corpId, dingtalkUserSet.size());
        return ObjectUtils.isEmpty(dingtalkUserSet)
                ? Lists.newArrayList()
                : dingtalkUserSet.stream().filter(u -> !ObjectUtils.isEmpty(u.getFbtMobile()) || !ObjectUtils.isEmpty(u.getMobile())).collect(Collectors.toList());
    }

    @Override
    public List<GetUserByPhoneNumRespDTO> listVoucherGrantFbUsers(String corpId) throws Exception {
        List<DingtalkUser> dingtalkUserList = listVoucherGrantUsers(corpId);
        if (ObjectUtils.isEmpty(dingtalkUserList)) {
            return Lists.newArrayList();
        }
        return listVoucherGrantFbUsersByPhoneNum(corpId, Lists.newArrayList(dingtalkUserList.stream().map(DingtalkUser::getFbtMobile).collect(Collectors.toSet())));
    }

    private List<GetUserByPhoneNumRespDTO> listVoucherGrantFbUsersByPhoneNum(String corpId, List<String> phoneNumList) throws Exception {
        PluginCorpDefinition corpDefinition = dingtalkCorpService.getByCorpId(corpId);
        Call<OpenApiRespDTO<List<GetUserByPhoneNumRespDTO>>> userInfoByPhoneNum = fbtUserCenterService.getUserInfoByPhoneNum(GetUserByPhoneNumReqDTO.builder()
                .phoneNums(phoneNumList)
                .companyId(corpDefinition.getAppId())
                .build());
        OpenApiRespDTO<List<GetUserByPhoneNumRespDTO>> respDTO = userInfoByPhoneNum.execute().body();
        List<GetUserByPhoneNumRespDTO> fbUserList = respDTO == null ? Lists.newArrayList() : respDTO.getData();
        // sendMsgIfPhoneNumNotInFinhub(corpId, phoneNumList, fbUserList);
        return fbUserList;
    }

    private void sendMsgIfPhoneNumNotInFinhub(String corpId, List<String> phoneNums, List<GetUserByPhoneNumRespDTO> userList) {
        if (phoneNums.size() != userList.size()) {
            List<String> userPhones = userList.stream().map(GetUserByPhoneNumRespDTO::getUserPhone).collect(Collectors.toList());
            List<String> phonesNotInFinhub = phoneNums.stream().filter(phoneNum -> !userPhones.contains(phoneNum)).collect(Collectors.toList());
//            dingTalkNoticeService.sendMsg(corpId, "通知:分贝通App中未找到以下手机号对应的用户，将无法统计考勤及发放加班补贴，请注意查看。手机号:" + String.join(",", phonesNotInFinhub) + "。");
        }
    }

    @Override
    @Async
    public void pullDingTalkAttendance(String companyId, String onlyNormalLocation, List<Date> dateList, List<String> dingtalkUserIdList, Boolean isCheckApprove) {
        //加载考勤表
        List<Attendance> attendanceList = attendanceDao.listUserAttendanceByWorkdate(companyId, dateList, dingtalkUserIdList);
        if (ObjectUtils.isEmpty(attendanceList)) {
            return;
        }
        //按照钉钉人员id分组
        Map<String, List<Attendance>> attendanceMap = attendanceList.stream().collect(Collectors.groupingBy(Attendance::getDingtalkUserId));
        //钉钉人员列表
        List<String> dingtalkUserList = Lists.newArrayList(attendanceMap.keySet());
        //加载钉钉考勤记录 每25人加载一次
        List<String> batchDingtalkUserList = Lists.newArrayList();
        for (int i = 0; i < dingtalkUserList.size(); i++) {
            batchDingtalkUserList.add(dingtalkUserList.get(i));
            if (!ObjectUtils.isEmpty(batchDingtalkUserList) && batchDingtalkUserList.size() % 25 == 0) {
                batchPullDingTalkAttendance(companyId, batchDingtalkUserList, attendanceMap, onlyNormalLocation, dateList, isCheckApprove);
                batchDingtalkUserList.clear();
            }
        }
        if (!ObjectUtils.isEmpty(batchDingtalkUserList)) {
            batchPullDingTalkAttendance(companyId, batchDingtalkUserList, attendanceMap, onlyNormalLocation, dateList, isCheckApprove);
        }
    }

    private void batchPullDingTalkAttendance(String companyId, List<String> batchDingtalkUserList, Map<String, List<Attendance>> attendanceMap, String onlyNormalLocation, List<Date> dateList, Boolean isCheckApprove) {
        PluginCorpDefinition corpDefinition = dingtalkCorpService.getByCompanyId(companyId);
        //钉钉平台企业ID
        String thirdCorpId = corpDefinition.getThirdCorpId();
        //昨天
        String yesterday = DateUtils.toSimpleStr(dateList.get(0));
        //今天
        String today = DateUtils.toSimpleStr(dateList.size() == 2 ? dateList.get(1) : dateList.get(0));
        //加载钉钉考勤记录
        List<Recordresult> dingtalkAttendanceList = apiAttendanceService.getAttendanceList(yesterday, today, thirdCorpId, batchDingtalkUserList);
        if (!ObjectUtils.isEmpty(dingtalkAttendanceList)) {
            dingtalkAttendanceList.forEach(dingtalkAttendance -> {
                //钉钉用户id
                String userId = dingtalkAttendance.getUserId();
                //工作日
                Date workDate = dingtalkAttendance.getWorkDate();
                //昨天和今天的考勤表
                List<Attendance> attendanceList = attendanceMap.get(userId);
                if (!ObjectUtils.isEmpty(attendanceList)) {
                    //考勤表
                    Attendance attendance = attendanceList.stream().filter(a -> workDate.getTime() == a.getWorkDate().getTime()).findFirst().orElse(null);
                    if (attendance != null) {
                        //更新考勤表
                        updateAttendance(attendance, dingtalkAttendance, onlyNormalLocation, isCheckApprove);
                        //插入考勤记录
                        saveAttendanceDingtalk(attendance, thirdCorpId, dingtalkAttendance);
                    }
                }
            });
        }
    }

    private void saveAttendanceDingtalk(Attendance attendance, String thirdCorpId, Recordresult dingtalkAttendance) {
        AttendanceDingtalk attendanceDingtalk = attendanceDingtalkDao.getAttendanceDingtalk(dingtalkAttendance.getId(), dingtalkAttendance.getRecordId() == null ? -1L : dingtalkAttendance.getRecordId());
        if (attendanceDingtalk == null) {
            attendanceDingtalk = new AttendanceDingtalk();
            setAttendanceDingtalk(attendance, thirdCorpId, dingtalkAttendance, attendanceDingtalk);
            attendanceDingtalkDao.saveSelective(attendanceDingtalk);
        }
    }

    private void setAttendanceDingtalk(Attendance attendance, String thirdCorpId, Recordresult dingtalkAttendance, AttendanceDingtalk attendanceDingtalk) {
        attendanceDingtalk.setMainId(attendance.getId());
        attendanceDingtalk.setDingtalkCheckId(dingtalkAttendance.getId());
        attendanceDingtalk.setRecordId(dingtalkAttendance.getRecordId() == null ? -1 : dingtalkAttendance.getRecordId());
        attendanceDingtalk.setGroupId(dingtalkAttendance.getGroupId());
        attendanceDingtalk.setPlanId(dingtalkAttendance.getPlanId());
        attendanceDingtalk.setCheckType(dingtalkAttendance.getCheckType());
        attendanceDingtalk.setCorpId(thirdCorpId);
        attendanceDingtalk.setUserId(dingtalkAttendance.getUserId());
        attendanceDingtalk.setWorkDate(dingtalkAttendance.getWorkDate());
        attendanceDingtalk.setBaseCheckTime(dingtalkAttendance.getBaseCheckTime());
        attendanceDingtalk.setUserCheckTime(dingtalkAttendance.getUserCheckTime());
        DingtalkTimeResult dingtalkTimeResult = DingtalkTimeResult.getDingtalkTimeResult(dingtalkAttendance.getTimeResult());
        attendanceDingtalk.setTimeResult(dingtalkTimeResult.getType());
        attendanceDingtalk.setTimeResultDesc(dingtalkTimeResult.getDescription());
        DingtalkLocationResult dingtalkLocationResult = DingtalkLocationResult.getDingtalkLocationResult(dingtalkAttendance.getLocationResult());
        attendanceDingtalk.setLocationResult(dingtalkLocationResult.getType());
        attendanceDingtalk.setLocationResultDesc(dingtalkLocationResult.getDescription());
        DingTalkSourceType dingTalkSourceType = DingTalkSourceType.getDingTalkSourceType(dingtalkAttendance.getSourceType());
        attendanceDingtalk.setSourceType(dingTalkSourceType.getDescription());
        attendanceDingtalk.setApproveId(StringUtils.obj2str(dingtalkAttendance.getApproveId()));
        attendanceDingtalk.setProcInstId(dingtalkAttendance.getProcInstId());
    }

    private void updateAttendance(Attendance attendance, Recordresult dingtalkAttendance, String onlyNormalLocation, Boolean isCheckApprove) {
        Attendance updateAttendance = new Attendance();
        updateAttendance.setId(attendance.getId());
        //签到签退
        String checkType = dingtalkAttendance.getCheckType();
        DingTakCheckType dingTakCheckType = DingTakCheckType.getDingTakCheckType(checkType);
        Date baseCheckTime = dingtalkAttendance.getBaseCheckTime();
        Date userCheckTime = dingtalkAttendance.getUserCheckTime();
        //打卡位置结果
        DingtalkLocationResult dingtalkLocationResult = DingtalkLocationResult.getDingtalkLocationResult(dingtalkAttendance.getLocationResult());
        //有效打卡 不限制位置 或者 限制位置且位置正常
        boolean validCheck = "0".equals(onlyNormalLocation) || ("1".equals(onlyNormalLocation) && dingtalkLocationResult == DingtalkLocationResult.Normal);
        //有效打卡才更新考勤记录
        if (validCheck) {
            //上班
            if (DingTakCheckType.OnDuty == dingTakCheckType) {
                updateAttendance.setBaseCheckInTime(baseCheckTime);
                updateAttendance.setUserCheckInTime(userCheckTime);
                updateAttendance.setLocationResultIn(dingtalkLocationResult.getType());
                updateAttendance.setGroupId(dingtalkAttendance.getGroupId());
                updateAttendance.setStatus(0);
                selectApprove(attendance, dingtalkAttendance, updateAttendance, isCheckApprove);
            }
            //下班
            else if (DingTakCheckType.OffDuty == dingTakCheckType) {
                updateAttendance.setBaseCheckOutTime(baseCheckTime);
                updateAttendance.setUserCheckOutTime(userCheckTime);
                updateAttendance.setLocationResultOut(dingtalkLocationResult.getType());
                updateAttendance.setStatus(0);
                selectApprove(attendance, dingtalkAttendance, updateAttendance, isCheckApprove);
            }
            attendanceDao.updateById(updateAttendance);
        }
    }

    /**
     * 查询审批流程
     */
    private void selectApprove(Attendance attendance, Recordresult dingtalkAttendance, Attendance updateAttendance, Boolean isCheckApprove) {
        if (!ObjectUtils.isEmpty(isCheckApprove) && isCheckApprove) {
            if (!ObjectUtils.isEmpty(dingtalkAttendance) && !ObjectUtils.isEmpty(dingtalkAttendance.getProcInstId()) && !ObjectUtils.isEmpty(attendance.getEmployeeId())) {
                try {
                    ApplyOrderDetailDTO applyOrderDetailDTO = getApplyDetail(dingtalkAttendance.getProcInstId(), attendance.getCompanyId(), attendance.getEmployeeId());
                    if (!ObjectUtils.isEmpty(applyOrderDetailDTO)) {
                        updateAttendance.setApproveType(applyOrderDetailDTO.getApply().getType());
                        if (!ObjectUtils.isEmpty(applyOrderDetailDTO.getApply().getTime_range())) {
                            String[] rangeTime = applyOrderDetailDTO.getApply().getTime_range().split(",");
                            if (rangeTime.length >= 2) {
                                int day = DateUtils.differentDaysByMillisecond(DateUtils.toDate(rangeTime[0]), DateUtils.toDate(rangeTime[1])) + 1;
                                updateAttendance.setApproveDay(day);
                            } else {
                                updateAttendance.setApproveDay(1);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.info("{}", e);
                }

            }
        }
    }
    /**
     * 查询分贝通申请单
     */
    private ApplyOrderDetailDTO getApplyDetail(String applyId, String companyId, String employeeId) {
        log.info("查询审批流程applyId:{},companyId:{},employeeId:{}", applyId, companyId, employeeId);
        String token = getEmployeeFbToken(companyId, employeeId, "0");
        applyId = applyOrderService.queryApplyId(companyId, applyId);
        if (ObjectUtils.isEmpty(applyId)) {
            log.info("考勤发券未获取到流程审批companyId:{},applyId{}", companyId, applyId);
            return null;
        }
        StringBuilder url = new StringBuilder(saasplusUrl);
        url.append("/saas_plus/apply/web/detail?apply_id=").append(applyId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Auth-Token", token);
        String responseBody = RestHttpUtils.get(url.toString(), httpHeaders, new HashMap<>());
        log.info("审批单详情查询结束，返回数据为{}", responseBody);
        Map<String, Object> map = funcTripApplyService.analyzeResponse(responseBody);
        if (NumericUtils.obj2int(map.get("code")) == 0) {
            ApplyOrderDetailDTO applyOrderDetailDTO = JSON.parseObject(JSON.toJSONString(map.get("data")), ApplyOrderDetailDTO.class);
            return applyOrderDetailDTO;
        }
        return null;
    }
}

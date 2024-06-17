package com.fenbeitong.openapi.plugin.wechat.eia.service.attendance.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.init.dto.OpenThirdEmployeeDTO;
import com.fenbeitong.openapi.plugin.support.voucher.dao.AttendanceDao;
import com.fenbeitong.openapi.plugin.support.voucher.dto.CreateAttendanceReq;
import com.fenbeitong.openapi.plugin.support.voucher.entity.Attendance;
import com.fenbeitong.openapi.plugin.util.CollectionUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WechatDepartmentListRespDTO;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WechatUserListRespDTO;
import com.fenbeitong.openapi.plugin.wechat.eia.dao.WeChatAttendanceDao;
import com.fenbeitong.openapi.plugin.wechat.eia.dto.attendance.WeChatAttendanceDTO;
import com.fenbeitong.openapi.plugin.wechat.eia.entity.WechatAttendanceEntity;
import com.fenbeitong.openapi.plugin.wechat.eia.service.attendance.WaChatApiAttendanceService;
import com.fenbeitong.openapi.plugin.wechat.eia.service.attendance.WeChatAttendanceService;
import com.fenbeitong.openapi.plugin.wechat.eia.service.organization.WeChatEiaOrgUnitService;
import com.fenbeitong.openapi.plugin.wechat.eia.service.wechat.WeChatEmployeeService;
import com.fenbeitong.openapi.plugin.wechat.eia.service.wechat.WeChatOrganizationService;
import com.fenbeitong.openapi.plugin.wechat.eia.service.wechat.WeChatPullThirdOrgService;
import com.fenbeitong.openapi.plugin.wechat.eia.service.wechat.WechatTokenService;
import com.fenbeitong.openapi.sdk.dto.common.OpenApiRespDTO;
import com.fenbeitong.openapi.sdk.dto.employee.GetUserByPhoneNumReqDTO;
import com.fenbeitong.openapi.sdk.dto.employee.GetUserByPhoneNumRespDTO;
import com.fenbeitong.openapi.sdk.webservice.employee.FbtEmployeeService;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeBaseInfo;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeePageListResult;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;
import retrofit2.Call;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@ServiceAspect
@Service
public class WeChatAttendanceServiceImpl implements WeChatAttendanceService {


    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    private WeChatEmployeeService weChatEmployeeService;


    @Autowired
    private WeChatPullThirdOrgService weChatPullThirdOrgService;

    @Autowired
    private FbtEmployeeService fbtUserCenterService;

    @DubboReference(check = false)
    private IBaseEmployeeExtService iBaseEmployeeExtService;

    @Autowired
    private AttendanceDao attendanceDao;

    @Autowired
    private WaChatApiAttendanceService apiAttendanceService;

    @Autowired
    private WeChatAttendanceDao weChatAttendanceDao;

    @Autowired
    private WechatTokenService wechatTokenService;

    @Autowired
    private WeChatOrganizationService weChatOrganizationService;

    @Autowired
    private WeChatEiaOrgUnitService weChatEiaOrgUnitService;


    @Override
    @Async
    public void createAttendance(CreateAttendanceReq req, Date workDate) throws Exception {
        String companyId = req.getCompanyId();
        PluginCorpDefinition corpDefinition = pluginCorpDefinitionDao.getByCompanyId(companyId);
        //企业wqxc token
        String wechatToken = wechatTokenService.getWeChatContactToken(companyId);
        List<OpenThirdEmployeeDTO> weCharUserList = new ArrayList<>();
        //微信平台企业ID
        String thirdCorpId = corpDefinition.getThirdCorpId();
        if ("2".equals(req.getPullUserType().toString())) {
            //查询可见范围的部门
            WechatDepartmentListRespDTO departmentListResp = weChatEiaOrgUnitService.getAllDepByDepId(wechatToken, "0");
            //有考勤权限的微信用户
            weCharUserList = getWeChatUser(companyId, departmentListResp, thirdCorpId, wechatToken, req.getPullUserType());
        } else {
            weCharUserList = getWeChatUser(companyId, null, thirdCorpId, wechatToken, req.getPullUserType());
        }

        log.info("companyId:{},配置了分贝通手机号的员工总数:{}。", companyId, weCharUserList.size());
        if (!ObjectUtils.isEmpty(weCharUserList)) {
            //分贝通手机号
            List<String> phoneNumList = Lists.newArrayList(weCharUserList.stream().map(t -> t.getThirdEmployeePhone()).collect(Collectors.toSet()));
            //有考勤权限的分贝通用户
            List<GetUserByPhoneNumRespDTO> fbUserList = listVoucherGrantFbUsersByPhoneNum(thirdCorpId, phoneNumList);
            fbUserList = ObjectUtils.isEmpty(fbUserList) ? Lists.newArrayList() : fbUserList.stream().filter(u -> !ObjectUtils.isEmpty(u.getId())).collect(Collectors.toList());
            //sendMsgIfPhoneNumNotInFinhub(thirdCorpId, phoneNumList, fbUserList);
            //生成考勤表
            createAttendanceRecordList(req, fbUserList, weCharUserList, workDate);
        }
    }


    @Override
    @Async
    public void pullWeChatAttendance(String companyId, int onlyNormalLocation, List<Date> dateList, List<String> weChatUserIdList) {

        //加载考勤表
        List<Attendance> attendanceList = attendanceDao.listUserAttendanceByWorkdate(companyId, dateList, weChatUserIdList);
        if (ObjectUtils.isEmpty(attendanceList)) {
            return;
        }
        //按照微信人员id分组
        Map<String, List<Attendance>> attendanceMap = attendanceList.stream().collect(Collectors.groupingBy(Attendance::getWechatUserId));
        //微信人员列表
        List<String> weChatUserList = Lists.newArrayList(attendanceMap.keySet());
        //加载微信考勤记录 每100人加载一次
        List<List<String>> batchList = CollectionUtils.batch(weChatUserList, 100);
        batchList.forEach(t -> {
            batchPullDingTalkAttendance(companyId, t, attendanceMap, onlyNormalLocation, dateList);
        });

    }

    /**
     * opencheckindatatype 打卡类型。1：上下班打卡；2：外出打卡；3：全部打卡
     */
    private void batchPullDingTalkAttendance(String companyId, List<String> batchDingtalkUserList, Map<String, List<Attendance>> attendanceMap, int opencheckindatatype, List<Date> dateList) {
        PluginCorpDefinition corpDefinition = pluginCorpDefinitionDao.getByCompanyId(companyId);
        //微信平台企业ID
        String thirdCorpId = corpDefinition.getThirdCorpId();
        //昨天
        Long yesterday = dateList.get(0).getTime();
        //今天
        Long today = dateList.size() == 2 ? dateList.get(1).getTime() + (24 * 60 * 60 * 1000 - 1000) : dateList.get(0).getTime() + (48 * 60 * 60 * 1000 - 1000);
        //加载微信考勤记录
        WeChatAttendanceDTO weChatAttendanceDTO = apiAttendanceService.getAttendance(opencheckindatatype, yesterday / 1000, today / 1000, companyId, batchDingtalkUserList.stream().toArray(String[]::new));
        if (!ObjectUtils.isEmpty(weChatAttendanceDTO)) {
            weChatAttendanceDTO.getCheckindata().forEach(checkindata -> {
                //微信用户id
                String userId = checkindata.getUserId();
                //工作日
                if (!ObjectUtils.isEmpty(checkindata.getSchCheckinTime())) {
                    Date workDate = DateUtils.toDate(DateUtils.toSimpleStr(DateUtils.toDate(checkindata.getSchCheckinTime() * 1000), true));
                    //昨天和今天的考勤表
                    List<Attendance> attendanceList = attendanceMap.get(userId);
                    if (!ObjectUtils.isEmpty(attendanceList)) {
                        //考勤表
                        Attendance attendance = attendanceList.stream().filter(a -> workDate.getTime() == a.getWorkDate().getTime()).findFirst().orElse(null);
                        if (attendance != null) {
                            //更新考勤表
                            updateAttendance(attendance, checkindata, opencheckindatatype);
                            //插入考勤记录
                            saveAttendanceDingtalk(attendance, thirdCorpId, checkindata);
                        }
                    }
                } else {
                    log.info("数据异常 checkindata:{}", JsonUtils.toJson(checkindata));
                }
            });
        }
    }


    private void updateAttendance(Attendance attendance, WeChatAttendanceDTO.DataBean weChatAttendance, int onlyNormalLocation) {
        Attendance updateAttendance = new Attendance();
        updateAttendance.setId(attendance.getId());
        updateAttendance.setGroupId(weChatAttendance.getGroupId());
        // 正常打卡才更新
        if ("".equals(weChatAttendance.getExceptionType())) {
            //上班
            if ("上班打卡".equals(weChatAttendance.getCheckinType())) {
                updateAttendance.setBaseCheckInTime(DateUtils.toDate(weChatAttendance.getSchCheckinTime() * 1000));
                updateAttendance.setUserCheckInTime(DateUtils.toDate(weChatAttendance.getCheckinTime() * 1000));
                updateAttendance.setLocationResultIn(getLocationResultIn(weChatAttendance.getCheckinType()));

            }
            //下班
            else if ("下班打卡".equals(weChatAttendance.getCheckinType())) {
                updateAttendance.setBaseCheckOutTime(DateUtils.toDate(weChatAttendance.getSchCheckinTime() * 1000));
                updateAttendance.setUserCheckOutTime(DateUtils.toDate(weChatAttendance.getCheckinTime() * 1000));
                updateAttendance.setLocationResultOut(getLocationResultIn(weChatAttendance.getCheckinType()));
            }
            attendanceDao.updateById(updateAttendance);
        }
    }


    private int getLocationResultIn(String checkinType) {
        int locationResuliIn = -1;
        switch (checkinType) {
            case "上班打卡":
            case "下班打卡":
                locationResuliIn = 1;
                break;
            case "外出打卡":
                locationResuliIn = 2;
                break;
            default:
                locationResuliIn = 3;
        }
        return locationResuliIn;
    }


    private void saveAttendanceDingtalk(Attendance attendance, String thirdCorpId, WeChatAttendanceDTO.DataBean dingtalkAttendance) {
        WechatAttendanceEntity wechatAttendanceEntity = weChatAttendanceDao.getByUserIdAndcheckinTime(dingtalkAttendance.getUserId(), DateUtils.toSimpleStr(dingtalkAttendance.getCheckinTime() * 1000));
        if (wechatAttendanceEntity == null) {
            WechatAttendanceEntity wechatAttendanceEntity1 = new WechatAttendanceEntity();
            setAttendanceDingtalk(attendance, thirdCorpId, dingtalkAttendance, wechatAttendanceEntity1);
            weChatAttendanceDao.saveSelective(wechatAttendanceEntity1);
        }
    }

    private void setAttendanceDingtalk(Attendance attendance, String thirdCorpId, WeChatAttendanceDTO.DataBean dingtalkAttendance, WechatAttendanceEntity wechatAttendanceEntity) {
        wechatAttendanceEntity.setCorp_id(thirdCorpId);
        wechatAttendanceEntity.setMainId(attendance.getId());
        wechatAttendanceEntity.setUserId(dingtalkAttendance.getUserId());
        wechatAttendanceEntity.setGroupName(dingtalkAttendance.getGroupName());
        wechatAttendanceEntity.setCheckinType(dingtalkAttendance.getCheckinType());
        wechatAttendanceEntity.setExceptionType(dingtalkAttendance.getExceptionType());
        wechatAttendanceEntity.setCheckinTime(DateUtils.toDate(dingtalkAttendance.getCheckinTime() * 1000));
        wechatAttendanceEntity.setLocationTitle(dingtalkAttendance.getLocationTitle());
        wechatAttendanceEntity.setLocationDetail(dingtalkAttendance.getLocationDetail());
        wechatAttendanceEntity.setNotes(dingtalkAttendance.getNotes());
        wechatAttendanceEntity.setSchCheckinTime(DateUtils.toDate(dingtalkAttendance.getSchCheckinTime() * 1000));
        wechatAttendanceEntity.setGroupId(dingtalkAttendance.getGroupId());
        wechatAttendanceEntity.setTimelineId(dingtalkAttendance.getTimelineId());
        wechatAttendanceEntity.setCreateTime(new Date());
        wechatAttendanceEntity.setUpdateTime(new Date());

    }

    /**
     * 获取全量人员
     */
    public List<OpenThirdEmployeeDTO> getWeChatUser(String companyId, WechatDepartmentListRespDTO departmentListResp, String corpId, String wechatToken, Integer pullUserType) {
        //获取企业微信全量人员
        List<WechatUserListRespDTO.WechatUser> wechatUserList = new ArrayList<>();
        if ("2".equals(pullUserType.toString())) {
            departmentListResp.getDepartmentList().forEach(t -> {
                wechatUserList.addAll(weChatEmployeeService.getWechatUserList(wechatToken, t.getId().toString()));
            });
        } else {
            wechatUserList.addAll(weChatEmployeeService.getWechatUserList(wechatToken, "1"));
        }

        List<OpenThirdEmployeeDTO> employeeList = new ArrayList<>();
        weChatPullThirdOrgService.packageEmployee(employeeList, wechatUserList, null, companyId, corpId, null, false);
        return employeeList.stream().filter(t -> {
            if (1 == t.getStatus()) {
                return true;
            }
            if (!StringUtils.isBlank(t.getThirdEmployeePhone())) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
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

    private void sendMsgIfPhoneNumNotInFinhub(String corpId, List<String> phoneNums, List<GetUserByPhoneNumRespDTO> userList) {
        if (phoneNums.size() != userList.size()) {
            List<String> userPhones = userList.stream().map(GetUserByPhoneNumRespDTO::getUserPhone).collect(Collectors.toList());
            List<String> phonesNotInFinhub = phoneNums.stream().filter(phoneNum -> !userPhones.contains(phoneNum)).collect(Collectors.toList());
//            dingTalkNoticeService.sendMsg(corpId, "通知:分贝通App中未找到以下手机号对应的用户，将无法统计考勤及发放加班补贴，请注意查看。手机号:" + String.join(",", phonesNotInFinhub) + "。");
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


}

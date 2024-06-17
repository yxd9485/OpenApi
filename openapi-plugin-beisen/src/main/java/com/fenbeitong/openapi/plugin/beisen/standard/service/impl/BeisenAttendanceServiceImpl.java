package com.fenbeitong.openapi.plugin.beisen.standard.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.openapi.plugin.beisen.common.dto.ApproveDto;
import com.fenbeitong.openapi.plugin.beisen.common.dto.BeiSenBusinessListDTO;
import com.fenbeitong.openapi.plugin.beisen.common.dto.BeisenParamConfig;
import com.fenbeitong.openapi.plugin.beisen.standard.service.BeisenAttendanceService;
import com.fenbeitong.openapi.plugin.beisen.standard.service.third.BeisenApiService;
import com.fenbeitong.openapi.plugin.support.apply.dto.CompanyApplyListReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.service.impl.OpenApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.voucher.dao.AttendanceDao;
import com.fenbeitong.openapi.plugin.support.voucher.dao.AttendanceGrantVoucherRulesDao;
import com.fenbeitong.openapi.plugin.support.voucher.entity.Attendance;
import com.fenbeitong.openapi.plugin.support.voucher.entity.AttendanceGrantVoucherRule;
import com.fenbeitong.openapi.plugin.support.voucher.service.IGrantVoucherService;
import com.fenbeitong.openapi.plugin.support.voucher.service.impl.GrantVoucherServiceImpl;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description 考勤发券
 * @Author duhui
 * @Date 2021/9/27
 **/
@Slf4j
@Service
public class BeisenAttendanceServiceImpl implements BeisenAttendanceService {

    @Autowired
    BeisenApiService beisenApiService;
    @Autowired
    private OpenApplyServiceImpl openApplyService;
    @Autowired
    private UserCenterService userCenterService;
    @DubboReference(check = false)
    private IBaseEmployeeExtService employeeExtService;
    @Autowired
    private AttendanceDao attendanceDao;
    @Autowired
    private GrantVoucherServiceImpl grantVoucherService;
    @Autowired
    private IGrantVoucherService iGrantVoucherService;
    @Autowired
    private AttendanceGrantVoucherRulesDao attendanceGrantVoucherRulesDao;


    @Override
    public void createAttendanceByAooly(BeisenParamConfig beisenParamConfig) {
        String companyId = beisenParamConfig.getCompanyId();
        // 获取审批记录
        List<BeiSenBusinessListDTO.Businesslist> businesslistList = beisenApiService.getApprovalCompletedBusinessList(beisenParamConfig);
        businesslistList = businesslistList.stream().filter(business -> filter(business)).collect(Collectors.toList());
        log.info("北森获取考勤审批：{}", JsonUtils.toJson(businesslistList));
        List<Attendance> attendanceList = new ArrayList<>();
        // 转换ID
        businesslistList.forEach(businesslist -> {
            businesslist.getBusinessDetailsSync().forEach(businessdetailssync -> {
                if (!StringUtils.isBlank(businessdetailssync.getStaffEmail())) {
                    List<EmployeeContract> employeeContractList = employeeExtService.queryByEmailAndCompanyId(companyId, businessdetailssync.getStaffEmail());
                    if (!ObjectUtils.isEmpty(employeeContractList) && employeeContractList.size() > 0) {
                        Date startDate = DateUtils.toDate(businessdetailssync.getStartDateTime(), "yyyy-MM-dd");
                        for (int i = 0; i < businessdetailssync.getDayValOfDuration(); i++) {
                            attendanceList.add(Attendance.builder()
                                    .employeeId(employeeContractList.get(0).getEmployee_id())
                                    .employeeName(employeeContractList.get(0).getName())
                                    .employeePhone(employeeContractList.get(0).getPhone_num())
                                    .companyId(companyId)
                                    .companyName(employeeContractList.get(0).getCompany_name())
                                    .thirdUserId(employeeContractList.get(0).getThird_employee_id())
                                    .grantVoucherFlag(1)
                                    .status(0)
                                    .workDate(DateUtils.addDay(startDate, i))
                                    .build());
                        }
                    }
                } else {
                    log.info("北森获取考勤审批邮箱为空:{}", JsonUtils.toJson(businessdetailssync));
                }
            });
        });
        createAttendanceRecordList(attendanceList, DateUtils.addDay(DateUtils.now(), -90), companyId);
    }

    @Override
    public void grantVoucher(String companyId, List<Long> ruleIdList) {
        List<AttendanceGrantVoucherRule> ruleList = attendanceGrantVoucherRulesDao.listRules(companyId, ruleIdList);
        if (ObjectUtils.isEmpty(ruleList)) {
            log.info("企业[{}]未找到加班发券规则信息。", companyId);
            return;
        }
        List<Attendance> attendanceList = attendanceDao.listNeedVoucherAttendance(companyId);
        if (ObjectUtils.isEmpty(attendanceList)) {
            log.info("企业[{}]未找到签退打卡信息。", companyId);
            return;
        }
        ruleList.forEach(rule -> {
            iGrantVoucherService.grantVoucherByAttendance(attendanceList, rule);
        });

        attendanceList.forEach(t -> {
            attendanceDao.updateById(Attendance.builder().id(t.getId()).grantVoucherFlag(0).status(1).build());
        });
    }


    private void createAttendanceRecordList(List<Attendance> attendanceList, Date workDate, String companyId) {
        attendanceList = attendanceList.stream().distinct().collect(Collectors.toList());
        // 过滤重复数据
        attendanceList = filterData(attendanceList, workDate, companyId);
        if (!ObjectUtils.isEmpty(attendanceList)) {
            attendanceDao.saveList(attendanceList);
        }
    }

    private List<Attendance> filterData(List<Attendance> attendanceList, Date workDate, String companyId) {
        Example example = new Example(Attendance.class);
        example.createCriteria().andGreaterThanOrEqualTo("workDate", workDate).andEqualTo("companyId", companyId);
        List<Attendance> oldData = attendanceDao.listByExample(example);
        Map<String, Attendance> oldMap = oldData.stream().collect(Collectors.toMap(t -> t.getCompanyId() + t.getEmployeeId() + DateUtils.toSimpleStr(t.getWorkDate(), true), Function.identity(), (o, n) -> n));
        return attendanceList.stream().filter(t -> !oldMap.containsKey(t.getCompanyId() + t.getEmployeeId() + DateUtils.toSimpleStr(t.getWorkDate(), true))).collect(Collectors.toList());
    }

    private boolean filter(BeiSenBusinessListDTO.Businesslist business) {
        if (ObjectUtils.isEmpty(business.getBusinessDetailsSync())) {
            return false;
        } else if (business.getStopDateTime().compareTo(DateUtils.toSimpleStr(DateUtils.getLastMonth(DateUtils.now(true), 0))) >= 0) {
            return false;
        } else if (!"通过".equals(business.getApproveStatus())) {
            return false;
        }
        return true;
    }

    /**
     * 根据企业获取公司num天以前至今的审批单列表
     */
    private List<ApproveDto.Results> getCompanyApprovelist(String companyId, int num) {
        Integer pageSize = 100;
        Integer pageIndex = 1;
        String token = userCenterService.getUcSuperAdminToken(companyId);
        List<ApproveDto.Results> results = new ArrayList<>();
        Integer remainDateCount = 1;
        while (remainDateCount > 0) {
            CompanyApplyListReqDTO companyApplyListReqDTO = new CompanyApplyListReqDTO();
            companyApplyListReqDTO.setStartTime(DateUtils.afterDay(num));
            companyApplyListReqDTO.setPageSize(pageSize);
            companyApplyListReqDTO.setPageIndex(pageIndex);
            companyApplyListReqDTO.setState(-1);
            companyApplyListReqDTO.setType(1);
            Map<String, Object> companyApproveList = openApplyService.getCompanyApproveList(token, companyApplyListReqDTO);
            ApproveDto approveDto = JsonUtils.toObj(JsonUtils.toJson(companyApproveList), ApproveDto.class);
            if (!ObjectUtils.isEmpty(approveDto)) {
                results.addAll(approveDto.getResults());
            }
            remainDateCount = approveDto.totalCount - pageSize * pageIndex;
            pageIndex++;
        }
        return results;
    }
}

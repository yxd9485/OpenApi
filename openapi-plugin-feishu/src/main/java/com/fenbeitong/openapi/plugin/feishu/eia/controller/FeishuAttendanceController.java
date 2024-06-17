package com.fenbeitong.openapi.plugin.feishu.eia.controller;

import com.fenbeitong.openapi.plugin.feishu.common.FeiShuResponseUtils;
import com.fenbeitong.openapi.plugin.feishu.common.service.FeishuAttendanceInterface;
import com.fenbeitong.openapi.plugin.support.voucher.dto.GrantOverTimeVoucherReq;
import com.fenbeitong.openapi.plugin.support.voucher.service.IGrantVoucherService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 飞书考勤发券
 * @Auther zhang.peng
 * @Date 2021/9/25
 */
@Controller
@Slf4j
@RequestMapping("/feishu/attendance")
public class FeishuAttendanceController {

    @Autowired
    private FeishuAttendanceInterface feishuAttendanceInterface;

    @Autowired
    private IGrantVoucherService IGrantVoucherService;

    /**
     * 获取飞书打卡记录
     * @param companyId 公司id
     * @param useCustomDay 是否使用指定日期,默认为 false
     * @param dayTime 指定时间
     * @return
     * @throws Exception
     */
    @RequestMapping("/pullFeishuAttendanceRecord/{companyId}/{useCustomDay}/{dayTime}")
    @ResponseBody
    public Object pullFeishuAttendanceRecord(@PathVariable("companyId") String companyId , @PathVariable("useCustomDay") boolean useCustomDay , @PathVariable("dayTime") int dayTime) throws Exception{
        feishuAttendanceInterface.pullAttendanceRecords(companyId,useCustomDay,dayTime);
        return FeiShuResponseUtils.success("生成考勤记录成功");
    }

    /**
     * 执行发券逻辑
     * @param companyId
     * @param companyName
     * @param corpId
     * @return
     */
    @RequestMapping("/getFeishuAttendanceDetail/{companyId}/{corpId}/{companyName}")
    @ResponseBody
    public Object getFeishuAttendanceDetail(@PathVariable("companyId") String companyId, @PathVariable("companyName") String companyName, @PathVariable("corpId") String corpId){
        // 读取打卡记录
        // 对比规则，然后发券
//        List<FeiShuDepartmentSimpleListRespDTO.DepartmentInfo> unSynDeptList = feiShuEiaOrganizationService.checkFeiShuDepartment(companyId, corpId,companyName, OpenType.FEISHU_EIA.getType());
//        return FeiShuResponseUtils.success(unSynDeptList);
        return null;
    }

    /**
     * 根据用户获取考勤组id
     * @param companyId 公司ID
     * @param userNames 飞书考勤组管理员姓名
     * @return
     */
    @RequestMapping("/getFeiShuAttendanceGroupIdInfo/{companyId}")
    @ResponseBody
    public Object getFeiShuAttendanceGroupIdInfo(@PathVariable("companyId") String companyId, @RequestBody List<String> userNames){
        return feishuAttendanceInterface.createAttendanceGroupInfo(companyId,userNames);
    }

    /**
     * 分发指定日期的考勤发券
     */
    @RequestMapping("/grantVoucherDate")
    @ResponseBody
    public Object grantVoucherDate( @RequestParam("jobConfig") String jobConfig) {
        GrantOverTimeVoucherReq req = JsonUtils.toObj(jobConfig, GrantOverTimeVoucherReq.class);
        if ( null == req ){
            return FeiShuResponseUtils.error(-1,"配置信息为空");
        }
        IGrantVoucherService.grantYesterdayVoucher(req.getCompanyId(), req.getRuleIdList());
        return FeiShuResponseUtils.success("昨天加班的分贝券发放成功");
    }
}

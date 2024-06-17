package com.fenbeitong.openapi.plugin.customize.hyproca.handle;

import com.fenbeitong.openapi.plugin.customize.hyproca.dto.HyprocaTripApplyDto;
import com.fenbeitong.openapi.plugin.support.apply.constant.SaasApplyType;
import com.fenbeitong.openapi.plugin.support.apply.dto.*;
import com.fenbeitong.openapi.plugin.support.apply.service.AbstractTripApplyService;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.common.constant.OrderCategoryEnum;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.init.dto.UcEmployeeDetailDTO;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeExtServiceImpl;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
import com.fenbeitong.openapi.plugin.util.ExceptionUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.usercenter.api.service.common.ICommonService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description 创建差旅审批
 * @Author duhui
 * @Date 2020-12-03
 **/
@Component
@Slf4j
public class HyprocaTripApplyCreateHandler extends AbstractTripApplyService implements ITaskHandler {

    @Autowired
    UserCenterService userCenterService;

    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    @DubboReference(check = false)
    private ICommonService iCommonService;

    @Autowired
    private OpenEmployeeExtServiceImpl employeeExtService;

    @Override
    public TaskType getTaskType() {
        return TaskType.HYPROCA_TRIP_APPLY_CREATE;
    }

    @Override
    public TaskResult execute(Task task) {
        // 1.解析task
        String corpId = task.getCorpId();
        // 2.检查企业是否注册
        PluginCorpDefinition pluginCorpDefinition = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId);
        if (pluginCorpDefinition == null) {
            log.info("企业不存在，任务丢弃，taskId={}", task.getId());
            return TaskResult.EXPIRED;
        }
        HyprocaTripApplyDto thipApplyDto = JsonUtils.toObj(task.getDataContent(), HyprocaTripApplyDto.class);
        String ucToken = userCenterService.getUcEmployeeToken(pluginCorpDefinition.getAppId(), thipApplyDto.getEmpId());
        // 封装数据
        CommonApplyReqDTO commonApplyReqDTO = setData(thipApplyDto, pluginCorpDefinition.getAppId());
        try {
            if (commonApplyReqDTO.getApply() != null && commonApplyReqDTO.getApply().getType() == SaasApplyType.ChaiLv.getValue()) {
                TripApproveCreateReqDTO tripApproveCreateReqDTO = (TripApproveCreateReqDTO) commonApplyService.convertApply(ucToken, commonApplyReqDTO);
                CreateApplyRespDTO tripApproveRespDTO = createTripApprove(ucToken, tripApproveCreateReqDTO);
                if (ObjectUtils.isEmpty(tripApproveRespDTO) || StringUtils.isBlank(tripApproveRespDTO.getId())) {
                    throw new OpenApiPluginSupportException(SupportRespCode.APPLY_CREATE_ERROR, "海普诺凯创建审行程批单失败");
                }
                log.info("create trip apply companyId: {}  applyId : {} , result: {} ", pluginCorpDefinition.getAppId(), tripApproveCreateReqDTO.getApply().getThirdId(), tripApproveRespDTO != null ? tripApproveRespDTO.getId() : null);
            }
        } catch (Exception ex) {
            throw new OpenApiPluginSupportException(SupportRespCode.APPLY_CREATE_ERROR, ExceptionUtils.getStackTraceAsString(ex));
        }
        return TaskResult.SUCCESS;
    }


    /**
     * 数据封装
     */
    public CommonApplyReqDTO setData(HyprocaTripApplyDto thipApplyDto, String companyId) {
        CommonApplyReqDTO commonApplyReqDTO = new CommonApplyReqDTO();
        String thirdId = StringUtils.isBlank(thipApplyDto.getTripId())?thipApplyDto.getWfinstanceId():thipApplyDto.getTripId();
        // 申请单
        CommonApply commonApply = new CommonApply();
        commonApply.setApplyReason(thipApplyDto.getRemark());
        commonApply.setApplyReasonDesc(thipApplyDto.getRemark());
        commonApply.setThirdRemark(StringUtils.isBlank(thipApplyDto.getRemark()) ? "海普诺凯三方行程审批" : thipApplyDto.getRemark());
        commonApply.setThirdId(thirdId);
        commonApply.setFlowType(4);
        commonApply.setBudget(0);
        commonApply.setType(1);
        commonApplyReqDTO.setApply(commonApply);

        // 行程单
        List<CommonApplyTrip> tripList = new ArrayList();
        String[] type = new String[0];
        // 2593国内  2594国外
        if ("2593".equals(thipApplyDto.getRegType())) {
            // 7:机票 11:酒店 15:火车
            type = new String[]{"7", "11", "15"};
        } else if ("2594".equals(thipApplyDto.getRegType())) {
            // 40国际机票
            type = new String[]{"40"};
        }
        Arrays.asList(type).forEach(t -> {
            CommonApplyTrip commonApplyFromTrip = new CommonApplyTrip();
            // 单程往返 1:单程;2:往返
            commonApplyFromTrip.setTripType(1);
            // 预估金额
            commonApplyFromTrip.setEstimatedAmount(0);
            // 出发城市
            if(StringUtils.isEmpty(thipApplyDto.getOutCity())){
                commonApplyFromTrip.setStartCityName("北京市");
            }else{
                commonApplyFromTrip.setStartCityName(thipApplyDto.getOutCity());
            }
            // 到达城市
            if(StringUtils.isEmpty(thipApplyDto.getArriveCity())){
                commonApplyFromTrip.setArrivalCityName("北京市");
            }else{
                commonApplyFromTrip.setArrivalCityName(thipApplyDto.getArriveCity());
            }
            String beginTime = StringUtils.isEmpty(thipApplyDto.getTripBeginTime())?thipApplyDto.getBeginTime():thipApplyDto.getTripBeginTime();
            // 开始时间
            commonApplyFromTrip.setStartTime(beginTime);
            // 结束时间
            String endTime = StringUtils.isEmpty(thipApplyDto.getTripEndTime())?thipApplyDto.getEndTime():thipApplyDto.getTripEndTime();
            commonApplyFromTrip.setEndTime(endTime);
            // 出差类型
            commonApplyFromTrip.setType(Integer.parseInt(t));
            tripList.add(commonApplyFromTrip);

            if(OrderCategoryEnum.Hotel.getKey()==Integer.parseInt(t) && StringUtils.isNotBlank(thipApplyDto.getArriveCity()) && !thipApplyDto.getArriveCity().equals(thipApplyDto.getOutCity())){
                CommonApplyTrip commonApplyFromTrip2 = new CommonApplyTrip();
                BeanUtils.copyProperties(commonApplyFromTrip,commonApplyFromTrip2);

                if(StringUtils.isEmpty(thipApplyDto.getArriveCity())){
                    commonApplyFromTrip2.setStartCityName("北京市");
                }else{
                    commonApplyFromTrip2.setStartCityName(thipApplyDto.getArriveCity());
                }
                tripList.add(commonApplyFromTrip2);
            }
        });
        commonApplyReqDTO.setTripList(tripList);
        //同行人三方用户id
        List<UcEmployeeDetailDTO> employeeDetailList  = new ArrayList<>();
        UcEmployeeDetailDTO employeeDetailInfo = employeeExtService.loadUserData(companyId, thipApplyDto.getEmpId());
        if(!StringUtils.isEmpty(thipApplyDto.getTzr())){
            UcEmployeeDetailDTO tzrEmployeeDetailInfo = employeeExtService.loadUserData(companyId, thipApplyDto.getTzr());
            employeeDetailList.add(tzrEmployeeDetailInfo);
        }
        employeeDetailList.add(employeeDetailInfo);
        List<CommonApplyGuest> guestList = addGuest(employeeDetailList);
        commonApplyReqDTO.setGuestList(guestList);
        return commonApplyReqDTO;
    }

    private List<CommonApplyGuest> addGuest(List<UcEmployeeDetailDTO> employeeDetailList) {
        //添加同行人
        List<CommonApplyGuest> guestList = new ArrayList<>();
        if(employeeDetailList.size()>0){
            employeeDetailList.stream().forEach(t -> {
                if (!ObjectUtils.isEmpty(t) && !ObjectUtils.isEmpty(t.getEmployee())) {
                    CommonApplyGuest guest = new CommonApplyGuest();
                    guest.setId(t.getEmployee().getId());
                    guest.setIsEmployee(true);
                    guest.setName(t.getEmployee().getName());
                    guest.setEmployeeType(0);
                    guest.setPhoneNum(t.getEmployee().getPhone_num());
                    guestList.add(guest);
                }
            });
        }
        return guestList;
    }
}

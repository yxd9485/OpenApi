package com.fenbeitong.openapi.plugin.customize.yuanqishenlin.service.impl;

import com.alibaba.druid.support.json.JSONUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.common.utils.basis.StringUtil;
import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.customize.common.vo.ResultVo;
import com.fenbeitong.openapi.plugin.customize.yuanqishenlin.dto.YqslTripApplyConvertDTO;
import com.fenbeitong.openapi.plugin.customize.yuanqishenlin.dto.YqslTripApplyDTO;
import com.fenbeitong.openapi.plugin.customize.yuanqishenlin.service.NonTravelApplyService;
import com.fenbeitong.openapi.plugin.func.apply.service.FuncCarApplyServiceImpl;
import com.fenbeitong.openapi.plugin.func.apply.service.FuncTripApplyServiceImpl;
import com.fenbeitong.openapi.plugin.func.common.FuncResultEntity;
import com.fenbeitong.openapi.plugin.support.apply.dao.OpenApplyRecordDao;
import com.fenbeitong.openapi.plugin.support.apply.dto.*;
import com.fenbeitong.openapi.plugin.support.apply.entity.OpenApplyRecord;
import com.fenbeitong.openapi.plugin.support.callback.constant.ApplyType;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.common.service.CommonAuthService;
import com.fenbeitong.openapi.plugin.support.init.dto.UcEmployeeDetailDTO;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeExtServiceImpl;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.po.company.CompanyEmployee;
import com.fenbeitong.usercenter.api.service.employee.IREmployeeService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * @ClassName NonTravelApplyService
 * @Description 差旅审批
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/7/1 上午11:25
 **/
@ServiceAspect
@Service
@Slf4j
public class NonTravelApplyServiceImpl  implements NonTravelApplyService {

    @Autowired
    private CommonAuthService signService;
    @Autowired
    private FuncTripApplyServiceImpl tripApplyService;
    @Autowired
    private OpenApplyRecordDao openApplyRecordDao;
    @Autowired
    private FuncCarApplyServiceImpl carApplyService;
    @Autowired
    private OpenEmployeeExtServiceImpl employeeExtService;
    @DubboReference(check = false)
    private IREmployeeService employeeService;
    @Autowired
    private YqslCommonApplyService commonApplyService;


    @Override
    public FuncResultEntity createTripApply(ApiRequestBase request) throws Exception {
        Map applyIdResult = new HashMap<String,String>();
        StringBuilder resRes=new StringBuilder();
        //获取token
        String token = signService.checkSign(request);
        YqslTripApplyDTO yqslTripApplyDTO = JsonUtils.toObj(request.getData(), YqslTripApplyDTO.class);
        log.info("元气森林创建非行程差旅审批数据:{}", JSONUtils.toJSONString(request.getData()));
        String companyId = signService.getAppId(request);
        String thirdApplyId = String.valueOf(yqslTripApplyDTO.getProcessBasicInfo().getFlowNumber());
        //判断该申请单是否已申请
        commonApplyService.checkInputParam(request);
        String data = JsonUtils.toJson(yqslTripApplyDTO);
        //转为ApiRequest
        ApiRequest apiRequest = new ApiRequest();
        BeanUtils.copyProperties(request, apiRequest);
        //对用户id，用户类型赋值
        apiRequest.setEmployeeType(0);
        List<CompanyEmployee> companyEmployees = employeeService.queryCompanyEmployeeListByEmployeeNum(companyId, Lists.newArrayList(yqslTripApplyDTO.getProcessBasicInfo().getEmployeeNo()));
        if(companyEmployees==null || companyEmployees.size()==0){
            log.warn("员工信息不存在，请核对信息");
            throw new OpenApiPluginSupportException(Integer.parseInt(SupportRespCode.THIRD_CAR_APPLY_CREATE_FAILED), "元气森林创建非行程差旅审批单失败，三方员工信息不存在");
        }
        apiRequest.setEmployeeId(companyEmployees.get(0).getEmployee_id());
        CompanyEmployee thirdCompanyEmployee = companyEmployees.get(0);
        //构造差旅申请单数据
        YqslTripApplyConvertDTO yqslTripApplyConvertDTO;
        yqslTripApplyConvertDTO = commonApplyService.buildTripDto(yqslTripApplyDTO, thirdCompanyEmployee);
        MultiTripApproveCreateReqDTO yqslTripApplyConvertDto = buildTripApplyDto(yqslTripApplyConvertDTO, thirdCompanyEmployee);
        String yqslTripApplyConvertStr = JsonUtils.toJson(yqslTripApplyConvertDto);
        //构造用车申请单数据
        CarApproveCreateReqDTO carApproveCreateReqDTO = commonApplyService.buildCarApplyDto(yqslTripApplyConvertDTO, companyId);
        String carApproveCreateReqStr = JsonUtils.toJson(carApproveCreateReqDTO);
        //判断差旅申请单是否已申请
        OpenApplyRecord openApplyRecord = openApplyRecordDao.getOpenApplyRecord(companyId, yqslTripApplyConvertDTO.getThirdId(),  ApplyType.APPLY_MULTI_TRIP.getType());
        //已经存在该申请单，查看状态，返回不同提示
        if (ObjectUtils.isEmpty(openApplyRecord) || !openApplyRecord.getStatus().equals("S")) {
            //入中间表
            OpenApplyRecord openTripApplyRecordIn = openApplyRecordDao.save(companyId, yqslTripApplyConvertDTO.getEmployeeId(), thirdApplyId, ApplyType.APPLY_MULTI_TRIP.getType(), yqslTripApplyConvertStr);
            //校验参数
            ValidatorUtils.validateBySpring(data);
            //调用差旅创建申请单
            request.setData(yqslTripApplyConvertStr);
            String tripApplyId = null;
            ResultVo tripResult = null;
            int errcode = -999;
            String msg=null;
            try {
                tripApplyId = tripApplyService.createMultiTripApply(request);
                if(!StringUtils.isBlank(tripApplyId)){
                    //创建审批单成功更新数据
                    tripResult = commonApplyService.buildBaseRes(tripApplyId, 0, "success！");
                }else{
                    tripResult = commonApplyService.buildBaseRes(tripApplyId, -999, "非行程创建差旅批单失败！");
                }
                openApplyRecordDao.updateSaasResult(openTripApplyRecordIn, JsonUtils.toJson(tripResult));
                applyIdResult.put("multi_trip_apply_id",tripApplyId);
            } catch (Exception e) {
                log.error("元气森林非行程创建差旅批单失败，返回：" + e.getMessage());
                if (e instanceof OpenApiPluginSupportException) {
                    errcode = ((OpenApiPluginSupportException) e).getCode();
                    msg = ((OpenApiPluginSupportException) e).getArgs()[0].toString();
                }
                tripResult = commonApplyService.buildBaseRes(null, errcode, msg);
                openApplyRecordDao.updateSaasResult(openTripApplyRecordIn, JsonUtils.toJson(tripResult));
            }
        }else{
            applyIdResult.put("multi_trip_apply_id",openApplyRecord.getApplyId());
        }

        //调用用车申请单
        apiRequest.setData(carApproveCreateReqStr);
        CreateApplyRespDTO carApply=null;
        try {
            String thirdEmpToken = commonApplyService.getToken(apiRequest, companyId);
            log.info("调用用车申请单参数,人员token:{},用车数据：{}",thirdEmpToken,JsonUtils.toJson(carApproveCreateReqDTO));
            carApply = carApplyService.createCarApprove(thirdEmpToken, carApproveCreateReqDTO);
        }catch(Exception e){
            log.error("元气森林非行程创建用车批单失败，返回：" + e.getMessage());
        }
        if (!ObjectUtils.isEmpty(carApply) && !StringUtils.isBlank((String) MapUtils.getValueByExpress(JsonUtils.toObj(JsonUtils.toJson(carApply), Map.class), "id"))) {
            applyIdResult.put("car_apply_id",carApply.getId());
        }
        Boolean resFalg = true;
        //拼接错误信息
        if(StringUtils.isBlank(StringUtils.obj2str(applyIdResult.get("multi_trip_apply_id")))){
            resFalg=false;
            resRes.append("创建非行程审批差旅申请单失败;");
        }else{
            resRes.append("创建非行程审批差旅申请单成功，applyId:"+StringUtils.obj2str(applyIdResult.get("multi_trip_apply_id"))+";");
        }
        if(StringUtils.isBlank(StringUtils.obj2str(applyIdResult.get("car_apply_id")))){
            resFalg=false;
            resRes.append(",");
            resRes.append("创建非行程审批用车申请单失败;");
        }else{
            resRes.append(",");
            resRes.append("创建非行程审批用车申请单成功，applyId:"+StringUtils.obj2str(applyIdResult.get("car_apply_id"))+";");
        }
        FuncResultEntity result = new FuncResultEntity();
        result.setData(applyIdResult);
        result.setMsg(StringUtils.obj2str(resRes));
        if(resFalg){
            result.setCode(0);
        }else{
            result.setCode(SupportRespCode.MULTI_APPLY_CREATE_ERROR);
        }
        result.setRequestId(MDC.get("requestId"));

        return result;
    }


    /*
    * 差旅申请单参数封装
     **/
    public MultiTripApproveCreateReqDTO buildTripApplyDto(YqslTripApplyConvertDTO commApplyDto,CompanyEmployee ucEmployeeDetailDTO){
        //调用uc查询电话
        UcEmployeeDetailDTO ucEmployeeDetailInfo = employeeExtService.loadUserData(ucEmployeeDetailDTO.getCompany_id(), commApplyDto.getEmployeeId());
        //2、非行程差旅审批数据封装
        MultiTripApproveCreateReqDTO multiTripApproveCreateReqDTO = new MultiTripApproveCreateReqDTO();
        MultiTripApplyDTO multiTripApplyDTO = new MultiTripApplyDTO();
        BeanUtils.copyProperties(commApplyDto,multiTripApplyDTO);
        MultiTripDTO multiTripDTO = new MultiTripDTO();
        BeanUtils.copyProperties(commApplyDto,multiTripDTO);
        multiTripDTO.setMultiTripScene(Arrays.asList(7,11,15,40));//多场景,机、酒、火,国际机票
        List multiGuestList = new ArrayList<MultiGuestListDTO>();
        MultiGuestListDTO multiGuestListDTO = new MultiGuestListDTO();
        multiGuestListDTO.setName(commApplyDto.getName());//乘客姓名
        multiGuestListDTO.setPhoneNum(ucEmployeeDetailInfo.getEmployee().getPhone_num());//电话
        multiGuestList.add(multiGuestListDTO);
        multiGuestList.addAll(commApplyDto.getGuestList());//添加同行人
        //申请单信息
        multiTripApproveCreateReqDTO.setApply(multiTripApplyDTO);
        //行程信息
        multiTripApproveCreateReqDTO.setTrip(multiTripDTO);
        //乘客信息
        multiTripApproveCreateReqDTO.setGuestList(multiGuestList);
        return multiTripApproveCreateReqDTO;
    }
}

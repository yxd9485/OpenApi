package com.fenbeitong.openapi.plugin.customize.yuanqishenlin.service.impl;

import com.alibaba.druid.support.json.JSONUtils;
import com.fenbeitong.openapi.plugin.support.apply.dto.CreateApplyRespDTO;
import org.apache.dubbo.config.annotation.DubboReference;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fenbeitong.openapi.plugin.customize.yuanqishenlin.dto.YqslTripApplyConvertDTO;
import com.fenbeitong.openapi.plugin.customize.yuanqishenlin.dto.YqslTripApplyDTO;
import com.fenbeitong.openapi.plugin.customize.yuanqishenlin.service.YqslCarApplyService;
import com.fenbeitong.openapi.plugin.func.apply.service.FuncCarApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.apply.dao.OpenApplyRecordDao;
import com.fenbeitong.openapi.plugin.support.apply.dto.CarApproveCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CarApproveDetail;
import com.fenbeitong.openapi.plugin.support.apply.entity.OpenApplyRecord;
import com.fenbeitong.openapi.plugin.support.city.constants.CityRelationType;
import com.fenbeitong.openapi.plugin.support.city.service.CityRelationService;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.common.service.CommonAuthService;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeExtServiceImpl;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.usercenter.api.model.po.company.CompanyEmployee;
import com.fenbeitong.usercenter.api.service.employee.IREmployeeService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName YqslCarApplyServiceImpl
 * @Description 元气森林用车申请审批
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/7/3 上午10:23
 **/
@ServiceAspect
@Service
@Slf4j
public class YqslCarApplyServiceImpl  implements YqslCarApplyService {

    @Autowired
    private CommonAuthService signService;
    @Autowired
    private OpenEmployeeExtServiceImpl employeeExtServiceImpl;
    @Autowired
    private OpenApplyRecordDao openApplyRecordDao;
    @Autowired
    private FuncCarApplyServiceImpl carApplyService;
    @Autowired
    private CityRelationService cityRelationService;
    @Autowired
    private YqslCommonApplyService commonApplyService;
    @DubboReference(check = false)
    private IREmployeeService employeeService;

    @Override
    public Object createCarApply(ApiRequestBase request) throws Exception {
        //获取token
        String token = signService.checkSign(request);
        //1、接受参数，构造用车审批参数
        YqslTripApplyDTO yqslTripApplyDTO = JsonUtils.toObj(request.getData(), YqslTripApplyDTO.class);
        log.info("元气森林创建外出审批数据:{}", JSONUtils.toJSONString(request.getData()));
        OpenApplyRecord openApplyRecord = commonApplyService.checkInputParam(request);
        String companyId = signService.getAppId(request);
        String thirdApplyId = String.valueOf(yqslTripApplyDTO.getProcessBasicInfo().getFlowNumber());
        //已经存在该申请单且成功则直接返回
        if (openApplyRecord != null && "S".equals(openApplyRecord.getStatus())) {
            Map<String, Map> map = JsonUtils.toObj(openApplyRecord.getResponse(), Map.class);
            return new CreateApplyRespDTO(StringUtils.obj2str(map.get("data").get("id")));
        }
        //获取员工信息
        //转为ApiRequest
        ApiRequest apiRequest = new ApiRequest();
        BeanUtils.copyProperties(request, apiRequest);
        //对用户id，用户类型赋值
        apiRequest.setEmployeeType(0);
        List<CompanyEmployee> companyEmployees = employeeService.queryCompanyEmployeeListByEmployeeNum(companyId, Lists.newArrayList(yqslTripApplyDTO.getProcessBasicInfo().getEmployeeNo()));
        if (companyEmployees == null || companyEmployees.size() == 0) {
            log.warn("员工信息不存在，请核对信息");
            throw new OpenApiPluginSupportException(Integer.parseInt(SupportRespCode.THIRD_CAR_APPLY_CREATE_FAILED), "元气森林创建用车批单失败，三方员工信息不存在");
        }
        CompanyEmployee thirdCompanyEmployee = companyEmployees.get(0);
        apiRequest.setEmployeeId(thirdCompanyEmployee.getEmployee_id());
        //构造用车申请单数据
        YqslTripApplyConvertDTO yqslTripApplyConvertDTO;
        yqslTripApplyConvertDTO = commonApplyService.buildTripDto(yqslTripApplyDTO, thirdCompanyEmployee);
        CarApproveCreateReqDTO carApproveCreateReqDTO = commonApplyService.buildCarApplyDto(yqslTripApplyConvertDTO, companyId);

        //转换城市编码
        String cityCode = null;
        List<Map<String, String>> cityCodeByEmpInfo = getCityCodeByEmpInfo(companyEmployees);
        for (Map cityMap : cityCodeByEmpInfo) {
            List<CarApproveDetail> carApproveDetailList = carApproveCreateReqDTO.getTripList().stream().map(t -> {
                CarApproveDetail carApproveDetail = new CarApproveDetail();
                BeanUtils.copyProperties(t, carApproveDetail);
                carApproveDetail.setStartTime(DateUtils.toSimpleStr(DateUtils.addDay(DateUtils.toDate(t.getStartTime()), 1),true));
                carApproveDetail.setEndTime(DateUtils.toSimpleStr(DateUtils.addDay(DateUtils.toDate(t.getEndTime()), -1),true));
                return carApproveDetail;
            }).collect(Collectors.toList());
            carApproveCreateReqDTO.setTripList(carApproveDetailList);
            cityCode = (String) cityMap.get(yqslTripApplyDTO.getProcessBasicInfo().getEmployeeNo());
        }
        carApproveCreateReqDTO.getTripList().get(0).setStartCityId(cityCode);
        String carApproveCreateReqStr = JsonUtils.toJson(carApproveCreateReqDTO);
        //2、调用用车申请单
        //调用用车申请单
        request.setData(carApproveCreateReqStr);
        String thirdEmpToken = commonApplyService.getToken(apiRequest, companyId);
        CreateApplyRespDTO carApply = carApplyService.createCarApprove(thirdEmpToken, carApproveCreateReqDTO);
        if (ObjectUtils.isEmpty(carApply) || StringUtils.isBlank((String) MapUtils.getValueByExpress(JsonUtils.toObj(JsonUtils.toJson(carApply), Map.class), "id"))) {
            log.warn("元气森林非行程创建用车审批单失败");
            throw new OpenApiPluginSupportException(SupportRespCode.APPLY_CREATE_ERROR, "元气森林创建非行程用车批单失败");
        }
        return (String) MapUtils.getValueByExpress(JsonUtils.toObj(JsonUtils.toJson(carApply), Map.class), "id");
    }

    //获取城市名称并查询转换编码
    public List<Map<String, String>> getCityCodeByEmpInfo(List<CompanyEmployee> companyEmployees) {
        List<Map<String, String>> empCityCodeList = new ArrayList<>();
        for (CompanyEmployee companyEmployee : companyEmployees) {
            JSONArray jsonArray = JSONArray.parseArray(companyEmployee.getExpand());
            if (!ObjectUtils.isEmpty(jsonArray) && jsonArray.size() > 0) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    Map empCityMap = new HashMap<String, Object>();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    log.info("遍历jsonArray,获取数组中的name属性值:{},员工id：{},三方id:{}：", jsonObject.get("employee_base1"), companyEmployee.getEmployee_id(), companyEmployee.getThird_employee_id());
                    //将名称转换为编码
                    String fbtCityCode = cityRelationService.getFbtCode((String) jsonObject.get("employee_base1"), CityRelationType.XRXS.getCode());
                    empCityMap.put(companyEmployee.getEmployee_number(), fbtCityCode);
                    empCityCodeList.add(empCityMap);
                }
            }
        }
        return empCityCodeList;
    }
}

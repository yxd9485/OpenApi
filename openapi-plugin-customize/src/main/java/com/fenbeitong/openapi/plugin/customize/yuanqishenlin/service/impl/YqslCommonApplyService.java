package com.fenbeitong.openapi.plugin.customize.yuanqishenlin.service.impl;

import com.fenbeitong.openapi.plugin.core.entity.KvEntity;
import com.fenbeitong.openapi.plugin.customize.common.vo.ResultVo;
import com.fenbeitong.openapi.plugin.customize.yuanqishenlin.dto.YqslTripApplyConvertDTO;
import com.fenbeitong.openapi.plugin.customize.yuanqishenlin.dto.YqslTripApplyDTO;
import com.fenbeitong.openapi.plugin.support.apply.dao.OpenApplyRecordDao;
import com.fenbeitong.openapi.plugin.support.apply.dto.CarApproveApply;
import com.fenbeitong.openapi.plugin.support.apply.dto.CarApproveCreateReqDTO;
import com.fenbeitong.openapi.plugin.support.apply.dto.CarApproveDetail;
import com.fenbeitong.openapi.plugin.support.apply.dto.MultiGuestListDTO;
import com.fenbeitong.openapi.plugin.support.apply.entity.OpenApplyRecord;
import com.fenbeitong.openapi.plugin.support.callback.constant.ApplyType;
import com.fenbeitong.openapi.plugin.support.city.constants.CityRelationType;
import com.fenbeitong.openapi.plugin.support.city.service.CityRelationService;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.support.common.service.CommonAuthService;
import com.fenbeitong.openapi.plugin.support.init.dto.UcEmployeeDetailDTO;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeExtServiceImpl;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequest;
import com.fenbeitong.openapi.plugin.support.sign.dto.ApiRequestBase;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.sdk.dto.common.OpenApiRespDTO;
import com.fenbeitong.openapi.sdk.dto.employee.QueryThirdEmployeeRespDTO;
import com.fenbeitong.openapi.sdk.webservice.employee.FbtEmployeeService;
import com.fenbeitong.usercenter.api.model.po.company.CompanyEmployee;
import com.fenbeitong.usercenter.api.service.employee.IREmployeeService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import retrofit2.Call;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName YqseCommonApplyService
 * @Description
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/7/4 下午11:22
 **/
@Component
public class YqslCommonApplyService {

    @Autowired
    private CityRelationService cityRelationService;
    @Autowired
    private OpenApplyRecordDao openApplyRecordDao;
    @Autowired
    private CommonAuthService signService;
    @Autowired
    private FbtEmployeeService userCenterService;
    @Autowired
     OpenEmployeeExtServiceImpl employeeExtService;

    @DubboReference(check = false)
    IREmployeeService employeeService;

    /*
     * 构造公共参数
     */
    protected YqslTripApplyConvertDTO buildTripDto(YqslTripApplyDTO yqslTripApplyDTO, CompanyEmployee ucEmployeeDetailDTO) {
        YqslTripApplyDTO.ProcessBasicInfo basicInfo = yqslTripApplyDTO.getProcessBasicInfo();
        YqslTripApplyConvertDTO yqslTripApplyConvertDto = new YqslTripApplyConvertDTO();
        //员工姓名
        String empName = basicInfo.getOwnerName();
        String fbtEmployeeId = ucEmployeeDetailDTO.getEmployee_id();
        String applyReasonDesc = null;
        String startValue = null;
        String endValue = null;
        List<String> cityNameList = new ArrayList<String>();
        List<KvEntity> cityInfoList = new ArrayList<KvEntity>();
        List<String> peerEmployeeNoList = new ArrayList<>();
        for (YqslTripApplyDTO.ProcessFormGroupInfoDTO formInfo : yqslTripApplyDTO.getProcessMetaInfoList()) {
            if ("申请详情".equals(formInfo.getGroupName())) {
                for (YqslTripApplyDTO.ProcessFormDetailInfoDTO detailInfo : formInfo.getDetailInfos()) {
                    if ("申请事由".equals(detailInfo.getLabName())) {
                        applyReasonDesc = detailInfo.getValue();
                    }
                    if ("开始时间".equals(detailInfo.getStartLabName()) && "结束时间".equals(detailInfo.getEndLabName())) {
                        startValue = detailInfo.getStartValue();
                        endValue = detailInfo.getEndValue();
                    }
                }
            }
            //出发地城市获取，兼容老版本
            if ("详情".equals(formInfo.getGroupName())) {
                for (YqslTripApplyDTO.ProcessFormDetailInfoDTO startCityInfo : formInfo.getDetailInfos()) {
                    if ("出发地".equals(startCityInfo.getLabName())) {
                        cityNameList.add(startCityInfo.getValue());
                    }
                    if ("目的地".equals(startCityInfo.getLabName())) {
                        cityNameList.add(startCityInfo.getValue());
                    }
                }
            }
            //出发地城市获取
            if("出发地".equals(formInfo.getGroupName())){
                for(YqslTripApplyDTO.ProcessFormDetailInfoDTO startCityInfo : formInfo.getDetailInfos()){
                    if ("请选择出发地".equals(startCityInfo.getLabName())) {
                        cityNameList.add(startCityInfo.getValue());
                    }
                }
            }
            //目的地城市获取
            if("目的地".equals(formInfo.getGroupName())){
                for(YqslTripApplyDTO.ProcessFormDetailInfoDTO arriveCityInfo : formInfo.getDetailInfos()){
                    if ("请选择目的地".equals(arriveCityInfo.getLabName())) {
                        cityNameList.add(arriveCityInfo.getValue());
                    }
                }
            }

            //同行人信息
            if("行程选择".equals(formInfo.getGroupName())){
                for (YqslTripApplyDTO.ProcessFormDetailInfoDTO peerInfo : formInfo.getDetailInfos()) {
                    if ("同行人".equals(peerInfo.getLabName())) {
                        if(!StringUtils.isBlank(peerInfo.getPeerEmployeeNo())){
                            String[] peerNo = peerInfo.getPeerEmployeeNo().split(",");
                            peerEmployeeNoList.addAll(Arrays.asList(peerNo));
                        }
                    }
                }
            }
        }
        List<MultiGuestListDTO> multiGuestList= new ArrayList<>();
        if(!ObjectUtils.isEmpty(peerEmployeeNoList)){
                List<CompanyEmployee> companyEmployees = employeeService.queryCompanyEmployeeListByEmployeeNum(ucEmployeeDetailDTO.getCompany_id(), Lists.newArrayList(peerEmployeeNoList));
                if(!ObjectUtils.isEmpty(companyEmployees)){
                    for(CompanyEmployee companyEmployee:companyEmployees){
                        UcEmployeeDetailDTO ucEmployeeDetailInfo = employeeExtService.loadUserData(ucEmployeeDetailDTO.getCompany_id(), companyEmployee.getThird_employee_id());
                        if(!ObjectUtils.isEmpty(ucEmployeeDetailInfo)){
                            MultiGuestListDTO multiGuest = MultiGuestListDTO.builder().name(ucEmployeeDetailInfo.getEmployee().getName()).phoneNum(ucEmployeeDetailInfo.getEmployee().getPhone_num()).build();
                            multiGuestList.add(multiGuest);
                        }
                    }

            }
        }
        //同行人添加
        yqslTripApplyConvertDto.setGuestList(multiGuestList);
        //根据城市名字获取城市编码
        for (String cityName : cityNameList) {
            KvEntity cityEntity = new KvEntity();
            String fbtCityCode = cityRelationService.getFbtCode(cityName, CityRelationType.XRXS.getCode());
            if (StringUtils.isBlank(fbtCityCode)) {
                throw new OpenApiPluginSupportException(SupportRespCode.APPLY_CREATE_ERROR, "城市名称错误");
            }
            cityEntity.setKey(fbtCityCode);
            cityEntity.setValue(cityName);
            cityInfoList.add(cityEntity);
        }
        Date startDate = DateUtils.addDay(DateUtils.toDate(startValue.substring(0, 10)), -1);
        Date endDate = DateUtils.addDay(DateUtils.toDate(endValue.substring(0, 10)), 1);
//        yqslTripApplyConvertDto.setApplyReason(applyReasonDesc);//申请事由
//        yqslTripApplyConvertDto.setApplyReasonDesc(basicInfo.getRemark());//申请事由描述
        yqslTripApplyConvertDto.setEmployeeId(ucEmployeeDetailDTO.getThird_employee_id());
        yqslTripApplyConvertDto.setThirdId(String.valueOf(basicInfo.getFlowNumber()));//三方审批单id
        yqslTripApplyConvertDto.setStartTime(startDate);//开始日期
        yqslTripApplyConvertDto.setEndTime(endDate);//结束日期
        yqslTripApplyConvertDto.setMultiTripCity(cityInfoList);//多城市
        yqslTripApplyConvertDto.setName(empName);//乘客姓名
        //yqslTripApplyConvertDto.setEmployeeId(fbtEmployeeId);//分贝id
        return yqslTripApplyConvertDto;
    }

    /*
     * 用车申请单参数封装
     **/
    protected CarApproveCreateReqDTO buildCarApplyDto(YqslTripApplyConvertDTO commApplyDto, String companyId) {
        //用车审批数据封装
        CarApproveCreateReqDTO carApproveCreateReqDTO = new CarApproveCreateReqDTO();
        CarApproveApply carApproveApply = new CarApproveApply();
        BeanUtils.copyProperties(commApplyDto, carApproveApply);
        carApproveApply.setCompanyId(companyId);//公司id
        carApproveApply.setThirdId(commApplyDto.getThirdId() + "_car");
        //用车审批申请单
        carApproveCreateReqDTO.setApply(carApproveApply);

        CarApproveDetail carApproveDetail = new CarApproveDetail();

        List<String> fbtCityCodeList = commApplyDto.getMultiTripCity().stream().map(KvEntity::getKey).collect(Collectors.toList());
        String fbtCitys = fbtCityCodeList.stream().collect(Collectors.joining(","));
        carApproveDetail.setStartCityId(fbtCitys);//城市ID ,多城市时将城市id用逗号分割
        String startDateStr = DateUtils.toStr(commApplyDto.getStartTime(), "yyyy-MM-dd");//用车行程开始日期
        String endDateStr = DateUtils.toStr(commApplyDto.getEndTime(), "yyyy-MM-dd");//用车行程结束日期
        carApproveDetail.setStartTime(startDateStr);//出发时间
        carApproveDetail.setEndTime(endDateStr);//结束时间
        carApproveCreateReqDTO.setTripList(Arrays.asList(carApproveDetail));//行程信息
        return carApproveCreateReqDTO;
    }

    protected ResultVo buildBaseRes(String applyId, Integer errCode, String errMsg) {
        ResultVo resultVo = new ResultVo();
        if (errCode != 0 || StringUtils.isBlank(errMsg)) {
            resultVo.setMsg(errMsg);
            resultVo.setCode(errCode);
        } else {
            resultVo.setMsg("success");
            HashMap<String, Object> data = Maps.newHashMap();
            data.put("id", applyId);
            resultVo.setData(data);
            resultVo.setCode(0);
        }
        return resultVo;
    }

    /*
     * 参数校验
     **/
    protected OpenApplyRecord checkInputParam(ApiRequestBase request) {

        YqslTripApplyDTO yqslTripApplyDTO = JsonUtils.toObj(request.getData(), YqslTripApplyDTO.class);
        YqslTripApplyDTO.ProcessBasicInfo basicInfo = yqslTripApplyDTO.getProcessBasicInfo();
        List<YqslTripApplyDTO.ProcessFormGroupInfoDTO> processMetaInfoList = yqslTripApplyDTO.getProcessMetaInfoList();
        if (ObjectUtils.isEmpty(basicInfo) || ObjectUtils.isEmpty(processMetaInfoList) || processMetaInfoList.size() == 0) {
            throw new OpenApiPluginSupportException(SupportRespCode.APPLY_CREATE_ERROR, "创建审批单参数错误");
        }
        int applyType = "外出".equals(basicInfo.getFlowTypeName()) ? ApplyType.APPLY_CAR.getType() : ApplyType.APPLY_MULTI_TRIP.getType();
        //判断该申请单是否已申请
        OpenApplyRecord openApplyRecord = openApplyRecordDao.getOpenApplyRecord(signService.getAppId(request), String.valueOf(basicInfo.getFlowNumber()), applyType);
        return openApplyRecord;
    }

    /*
     *   获取三方用户token
     **/
    protected String getToken(ApiRequest apiRequest, String appId) throws IOException {
        Map<String, Object> param = new HashMap<>();
        param.put("user_id", apiRequest.getEmployeeId());
        param.put("company_id", appId);
        param.put("appType", apiRequest.getEmployeeType());
        Call<OpenApiRespDTO<QueryThirdEmployeeRespDTO>> result = userCenterService.queryThirdEmployee(param);
        OpenApiRespDTO<QueryThirdEmployeeRespDTO> resp = result.execute().body();
        if (resp == null || !resp.success()) {
            throw new OpenApiPluginSupportException(NumericUtils.obj2int(SupportRespCode.QUERY_THIRD_USER_ERROR));
        }
        QueryThirdEmployeeRespDTO data = resp.getData();
        return data == null ? null : data.getToken();
    }
}

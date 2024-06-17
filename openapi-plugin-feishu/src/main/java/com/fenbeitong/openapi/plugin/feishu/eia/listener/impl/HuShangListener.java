package com.fenbeitong.openapi.plugin.feishu.eia.listener.impl;


import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.entity.KvEntity;
import com.fenbeitong.openapi.plugin.core.exception.RespCode;
import com.fenbeitong.openapi.plugin.feishu.common.dto.FeiShuApprovalFormDTO;
import com.fenbeitong.openapi.plugin.feishu.common.listener.FeiShuApprovalListener;
import com.fenbeitong.openapi.plugin.feishu.common.util.FeiShuParseTimeUtils;
import com.fenbeitong.openapi.plugin.feishu.eia.listener.AbstractFeiShuEiaCommon;
import com.fenbeitong.openapi.plugin.feishu.eia.service.FeiShuEiaEmployeeService;
import com.fenbeitong.openapi.plugin.support.apply.dto.*;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.apply.service.IOpenApplyService;
import com.fenbeitong.openapi.plugin.support.citycode.service.CityCodeService;
import com.fenbeitong.openapi.plugin.support.common.service.CommonService;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;

import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@ServiceAspect
@Service
@Slf4j
public  class HuShangListener implements FeiShuApprovalListener {



    @Autowired
    private   CommonApplyServiceImpl commonApplyService;

    @Autowired
    private  CityCodeService cityCodeService;


    /**
     * 用车审批
     */
    @Override
    public   CommonApplyReqDTO parseFeiShuCarForm(String companyId,String corpId, String approvalId, String form, String thirdEmployeeId){
        return  null;
    };


    @Override
    public  CommonApplyReqDTO parseFeiShuBusinessForm(String companyId, String corpId, String approvalId, String form) {
        List<LinkedHashMap> list = JsonUtils.toObj(form, List.class);
        CommonApplyReqDTO commonApplyReqDTO = new CommonApplyReqDTO();
        if (ObjectUtils.isEmpty(list)) {
            return null;
        }
        MultiTripDTO multiTripDTO = new MultiTripDTO();
        for (LinkedHashMap map : list) {
            FeiShuApprovalFormDTO feiShuApprovalFormDTO = new FeiShuApprovalFormDTO(map);


            FeiShuApprovalFormDTO.Value value = feiShuApprovalFormDTO.getValue();
            String stringValue = feiShuApprovalFormDTO.getStringValue();
            if (ObjectUtils.isEmpty(value) && ObjectUtils.isEmpty(stringValue)) {
                continue;
            }

            switch (feiShuApprovalFormDTO.getName()) {
                case "DateInterval":
                    String end = feiShuApprovalFormDTO.getValue().getEnd();
                    String start = feiShuApprovalFormDTO.getValue().getStart();
                    start = start.substring(0, start.indexOf("T"));
                    end = FeiShuParseTimeUtils.getEndTime(end);
                    Date startTime = DateUtils.toDate(start, DateUtils.FORMAT_DATE_PATTERN);
                    Date endTime = DateUtils.toDate(end, DateUtils.FORMAT_DATE_PATTERN);
                    multiTripDTO.setStartTime(startTime);
                    multiTripDTO.setEndTime(endTime);
                    break;
                case "目的地":
                    Set<String> citys = new HashSet<>();
                    citys.add(feiShuApprovalFormDTO.getStringValue());

                    String city = String.join(",", citys);
                    List<String> cityIdList = commonApplyService.getCarStartIdList(null,city, 0);
                    List<KvEntity> kvEntities = buildMultiCity(cityIdList);
                    multiTripDTO.setMultiTripCity(kvEntities);



                    break;
                case "出差事由":

                    CommonApply commonApply = new CommonApply();
                    commonApply.setApplyReason(feiShuApprovalFormDTO.getStringValue());
                    commonApply.setThirdId(approvalId);
                    commonApply.setType(1);
                    commonApply.setFlowType(4);
                    commonApply.setBudget(0);
                    commonApplyReqDTO.setApply(commonApply);
                }


        }
        multiTripDTO.setMultiTripScene(Arrays.asList(11,15));
        commonApplyReqDTO.setMultiTrip(multiTripDTO);
        return commonApplyReqDTO;

    }





    /**
     * 构建非行程city
     *
     * @param cityIds
     * @return
     */
    private  List<com.fenbeitong.openapi.plugin.core.entity.KvEntity> buildMultiCity(List<String> cityIds) {
        List<com.fenbeitong.openapi.plugin.core.entity.KvEntity> targetList = Lists.newArrayList();
        for (String cityId : cityIds) {
            CityInfoDTO cityInfoByCode = cityCodeService.getCityInfoByCode(cityId);
            if (cityInfoByCode == null) {
                throw new FinhubException(RespCode.ARGUMENT_ERROR, "城市id不合规");
            }
            com.fenbeitong.openapi.plugin.core.entity.KvEntity kvEntity = new com.fenbeitong.openapi.plugin.core.entity.KvEntity();
            kvEntity.setKey(cityId);
            kvEntity.setValue(cityInfoByCode.getName());
            targetList.add(kvEntity);
        }
        return targetList;
    }





}


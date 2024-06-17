package com.fenbeitong.openapi.plugin.customize.zhiou.service.impl;

import com.fenbeitong.openapi.plugin.core.util.HttpServletRequestUtils;
import com.fenbeitong.openapi.plugin.customize.common.exception.OpenApiCustomizeException;
import com.fenbeitong.openapi.plugin.customize.zhiou.constant.BeisenResponseCodeEnum;
import com.fenbeitong.openapi.plugin.customize.zhiou.constant.ZhiouConstant;
import com.fenbeitong.openapi.plugin.customize.zhiou.dto.ApplyDetailDTO;
import com.fenbeitong.openapi.plugin.customize.zhiou.dto.ApplyRequestDTO;
import com.fenbeitong.openapi.plugin.customize.zhiou.dto.ApplyTripDTO;
import com.fenbeitong.openapi.plugin.customize.zhiou.entity.CustomizeBeisenCorp;
import com.fenbeitong.openapi.plugin.customize.zhiou.entity.OpenLandrayEkpConfig;
import com.fenbeitong.openapi.plugin.customize.zhiou.service.ZhiouNonTravelApplyService;
import com.fenbeitong.openapi.plugin.customize.zhiou.service.beisen.BeiSenAttendanceServiceImpl;
import com.fenbeitong.openapi.plugin.customize.zhiou.service.landray.LandrayApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackRecordDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackRecord;
import com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @ClassName NonTravelApplyServiceImpl
 * @Description 推送非行程差旅审批实现类
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/8/30
 **/
@Service
@Slf4j
public class ZhiouNonTravelApplyServiceImpl implements ZhiouNonTravelApplyService {

    @Value("${host.openplus}")
    private String openPlusHost;

    @Autowired
    private ZhiouOpenApiAuthServiceImpl openApiAuthService;

    @Autowired
    private LandrayApplyServiceImpl landrayApplyService;

    @Autowired
    private BeiSenAttendanceServiceImpl beiSenAttendanceService;

    @Autowired
    private ThirdCallbackRecordDao thirdCallbackRecordDao;

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;

    @Override
    public boolean nonTravelApplyPush(HttpServletRequest request, String companyId) {
        String requestBody = HttpServletRequestUtils.ReadAsChars(request);
        ApplyRequestDTO applyRequestDTO = JsonUtils.toObj(requestBody, ApplyRequestDTO.class);
        log.info("非行程审批单推送开始,公司:{},applyRequestDTO:{}", companyId, applyRequestDTO);
        long start = System.currentTimeMillis();
        //查询蓝凌配置信息
        OpenLandrayEkpConfig landrayEkpConfig = openApiAuthService.getLandrayEkpConfig(companyId);
        //查询北森配置信息
        CustomizeBeisenCorp beisenCorp = openApiAuthService.getBeisenCorp(companyId);
        //生成openapi鉴权参数
        MultiValueMap params = openApiAuthService.genApiAuthParams(applyRequestDTO, companyId);
        //查询审批单详情
        Map<String, Object> applyDetail = getApplyDetail(params);
        if (ObjectUtils.isEmpty(applyDetail)) {
            log.info("查询非行程审批详情失败,公司:{},审批单号:{}", companyId, applyRequestDTO.getApplyId());
            throw new OpenApiCustomizeException(-9999, "查询非行程审批详情失败,公司:{},审批单号:{}", companyId, applyRequestDTO.getApplyId());
        }
        //参数校验
        checkParam(applyDetail, companyId);
        //查询是否推送蓝凌配置
        OpenMsgSetup openMsgSetupToLandray = openMsgSetupDao.selectByCompanyIdAndItemCode(companyId,ZhiouConstant.IS_PUSH_LANDRAY);
        //推送审批单详情到蓝凌
        boolean isLandraySuccess = true;
        if (!ObjectUtils.isEmpty(openMsgSetupToLandray)) {
            Map<String, Object> landrayResultMap = landrayApplyService.pushApplyDetails(applyRequestDTO, applyDetail, landrayEkpConfig);
            isLandraySuccess = !ObjectUtils.isEmpty(landrayResultMap.get("success")) && (boolean) landrayResultMap.get("success");
            log.info("公司:{},审批单推送蓝凌返回,landrayResultMap:{}", companyId, landrayResultMap);
            applyRequestDTO.setPushLandraySuccess(isLandraySuccess);
        }
        //查询是否推送北森配置
        OpenMsgSetup openMsgSetupToBeisen = openMsgSetupDao.selectByCompanyIdAndItemCode(companyId,ZhiouConstant.IS_PUSH_BEISEN);
        //推送审批单详情到北森
        boolean isBeisenSuccess = true;
        if (!ObjectUtils.isEmpty(openMsgSetupToBeisen)) {
            Map<String, Object> beisenResultMap = beiSenAttendanceService.pushAttendance(applyRequestDTO, applyDetail, beisenCorp);
            isBeisenSuccess = (!ObjectUtils.isEmpty(beisenResultMap.get("code")) && BeisenResponseCodeEnum.BEISEN_STATE_SUCCESS.getCode().equals(beisenResultMap.get("code")));
            log.info("公司:{},审批单推送北森返回,beisenResultMap:{}", companyId, beisenResultMap);
            applyRequestDTO.setPushBeisenSuccess(isBeisenSuccess);
        }
        //保存蓝凌北森是否成功标识
        updateRecordByApplyId(applyRequestDTO);
        long end = System.currentTimeMillis();
        log.info("公司:{},非行程审批单推送结束，用时{}分钟{}秒...", companyId,(end - start) / 60000L, (end - start) % 60000L / 1000L);
        return isLandraySuccess && isBeisenSuccess;
    }

    /**
     * 查询审批单详情
     *
     * @param params 查询参数
     * @return map 审批单详情map
     */
    @Override
    public Map<String, Object> getApplyDetail(MultiValueMap params) {
        HttpHeaders httpHeaders = new HttpHeaders();
        String response = RestHttpUtils.postFormUrlEncode((openPlusHost.concat(ZhiouConstant.APPLY_DETAIL_URL_SUFFIX)), httpHeaders, params);
        Map<String, Object> map = JsonUtils.toObj(response, Map.class);
        return map == null ? Maps.newHashMap() : (Map<String, Object>) map.get("data");
    }

    /**
     * 参数校验
     *
     * @param applyDetail 审批单详情
     */
    public void checkParam(Map<String, Object> applyDetail, String companyId) {
        ApplyDetailDTO apply = JsonUtils.toObj(JsonUtils.toJson(applyDetail.get("apply")), ApplyDetailDTO.class);
        List<Map<String, Object>> tripList = (List<Map<String, Object>>) applyDetail.get("trip_list");
        ApplyTripDTO tripDTO = JsonUtils.toObj(JsonUtils.toJson(tripList.get(0)), ApplyTripDTO.class);
        if (StringUtils.isBlank(apply.getThirdEmployeeId())) {
            log.info("员工三方id不能为空，公司id：{}", companyId);
            throw new OpenApiCustomizeException(SupportRespCode.DATA_NOT_EXISTS, "员工三方id不能为空,公司id：{}", companyId);
        }
        if (StringUtils.isBlank(tripDTO.getStartTime())) {
            log.info("出发时间不能为空，公司id：{}", companyId);
            throw new OpenApiCustomizeException(SupportRespCode.DATA_NOT_EXISTS, "出发时间不能为空,公司id：{}", companyId);
        }
        if (StringUtils.isBlank(tripDTO.getEndTime())) {
            log.info("结束时间不能为空，公司id：{}", companyId);
            throw new OpenApiCustomizeException(SupportRespCode.DATA_NOT_EXISTS, "结束时间不能为空,公司id：{}", companyId);
        }
    }

    /**
     * 根据审批单id更新第三方回调记录表
     *
     * @param applyRequestDTO 审批单参数
     */
    public void updateRecordByApplyId(ApplyRequestDTO applyRequestDTO) {
        ThirdCallbackRecord record = ThirdCallbackRecord.builder().callbackData(JsonUtils.toJson(applyRequestDTO)).build();
        Example example = new Example(ThirdCallbackRecord.class);
        example.createCriteria()
            .andEqualTo("orderId", applyRequestDTO.getApplyId());
        thirdCallbackRecordDao.updateByExample(record, example);
    }

}

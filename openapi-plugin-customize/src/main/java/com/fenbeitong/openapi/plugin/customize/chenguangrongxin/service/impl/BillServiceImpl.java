package com.fenbeitong.openapi.plugin.customize.chenguangrongxin.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.finhub.common.utils.StringUtils;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.customize.chenguangrongxin.dto.BillDataDTO;
import com.fenbeitong.openapi.plugin.customize.chenguangrongxin.dto.JdyBillListPushDTO;
import com.fenbeitong.openapi.plugin.customize.chenguangrongxin.dto.OpenBillDetailQueryResDTO;
import com.fenbeitong.openapi.plugin.customize.chenguangrongxin.dto.OpenBillPageInfoResDTO;
import com.fenbeitong.openapi.plugin.customize.chenguangrongxin.service.IBillService;
import com.fenbeitong.openapi.plugin.customize.common.vo.ResultVo;
import com.fenbeitong.openapi.plugin.etl.service.IEtlService;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackStatus;
import com.fenbeitong.openapi.plugin.support.callback.constant.CallbackType;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackConfDao;
import com.fenbeitong.openapi.plugin.support.callback.dao.ThirdCallbackRecordDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackRecord;
import com.fenbeitong.openapi.plugin.support.callback.service.IBusinessDataPushService;
import com.fenbeitong.openapi.plugin.support.common.dto.OpenApiResponseDTO;
import com.fenbeitong.openapi.plugin.support.company.service.impl.UcCompanyServiceImpl;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.RandomUtils;
import com.fenbeitong.usercenter.api.model.dto.company.CompanyNewDto;
import com.finhub.framework.core.json.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName BillServiceImpl
 * @Description 账单接口同步简道云
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/9/19 上午11:03
 **/
@Service
@Slf4j
public class BillServiceImpl implements IBillService {

    @Value("${host.tiger}")
    private String tigerHost;

    @Autowired
    private IEtlService etlService;

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;

    @Autowired
    private ThirdCallbackConfDao callbackConfDao;

    @Autowired
    private ThirdCallbackRecordDao recordDao;

    @Autowired
    private UcCompanyServiceImpl ucCompanyService;

    @Autowired
    private IBusinessDataPushService businessDataPushService;

    private static final String CGRX_BILL_CODE = "cgrx_bill_code";

    /**
     * 查询辰光融信汇总账单并进行推送
     *
     * @param companyId
     * @param billNo
     * @return Object
     * @author helu
     * @date 2022/9/19 下午1:49
     */
    @Override
    public void pushBillData(String companyId, String billNo) {
        if (ObjectUtils.isEmpty(companyId) || ObjectUtils.isEmpty(billNo)) {
            throw new OpenApiArgumentException("账单查询参数信息缺失");
        }
        int pageIndex = 1;
        int pageSize = 90;
        Map billReqData = new HashMap();
        billReqData.put("bill_code", billNo);
        billReqData.put("page_index", pageIndex);
        billReqData.put("page_size", pageSize);

        OpenMsgSetup openMsgSetup = openMsgSetupDao.selectByCompanyIdAndItemCode(companyId, CGRX_BILL_CODE);
        if (ObjectUtils.isEmpty(openMsgSetup) || StringUtils.isBlank(openMsgSetup.getStrVal1())) {
            log.info("辰光融信账单推送简道云配置为空");
            throw new OpenApiArgumentException("辰光融信账单推送简道云配置为空");
        }
        Long mainId =Long.valueOf(MapUtils.getValueByExpress(JsonUtils.toObj(openMsgSetup.getStrVal1(), Map.class), "etlConfig").toString());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("appId", companyId);

        String reqUrl = tigerHost + "/openapi/bill/business/v1/detail";
        log.info("辰光融信账单入参：url:{},head:{},params:{}", reqUrl, headers, JsonUtils.toJson(billReqData));
        String result = RestHttpUtils.postJson(reqUrl, headers, JsonUtils.toJson(billReqData));
        log.info("辰光融信账单返回数据:{}", JsonUtils.toJson(result));

        ResultVo respDTO = JsonUtils.toObj(result, ResultVo.class);
        OpenBillPageInfoResDTO billDataList = JsonUtils.toObj(JsonUtils.toJson(respDTO.getData()), OpenBillPageInfoResDTO.class);

        while (!ObjectUtils.isEmpty(billDataList) && billDataList.getTotalPages() >= billDataList.getPageIndex()) {
            List<OpenBillDetailQueryResDTO> openBillDetailQueryRes = JsonUtils.toObj(JsonUtils.toJson(billDataList.getDetails()), new TypeReference<List<OpenBillDetailQueryResDTO>>() {
            });
            log.info("openBillDetailQueryRes.size:{}",openBillDetailQueryRes.size());
            //构造简道云入参
            JdyBillListPushDTO jdyBillListData = buildBillData(openBillDetailQueryRes,mainId);
            log.info("jdyBillListData.size:{}",jdyBillListData.getDataList().size());
            //给简道云推送账单数据
            pushJdyBillData(jdyBillListData,companyId);
            billReqData.put("page_index", ++pageIndex);
            String loopRes = RestHttpUtils.postJson(reqUrl, headers, JsonUtils.toJson(billReqData));
            OpenApiResponseDTO resp = JsonUtils.toObj(loopRes, OpenApiResponseDTO.class);
            billDataList = JsonUtils.toObj(JsonUtils.toJson(resp.getData()), OpenBillPageInfoResDTO.class);
        }
    }

    private JdyBillListPushDTO buildBillData(List<OpenBillDetailQueryResDTO> billDetailQueryResList,Long mainId) {
        JdyBillListPushDTO jdyBillListPushDTO = new JdyBillListPushDTO();
        jdyBillListPushDTO.setTransactionId(DateUtils.toStr(DateUtils.now(), "yyyyMMddHHmmss") + RandomUtils.randomNum(4));
        List<BillDataDTO> billDataList = new ArrayList<>();
        for (OpenBillDetailQueryResDTO billData : billDetailQueryResList) {
            Map data = JsonUtils.toObj(JsonUtils.toJson(billData), Map.class);
            Map result = etlService.transform(mainId, data);
            BillDataDTO billDataDTO = JsonUtils.toObj(JsonUtils.toJson(result), BillDataDTO.class);
            //费用归属单独做处理
            String costAttributeCategory = StringUtils.isBlank(billData.getCostAttributionCategory1())?billData.getCostAttributionCategory2():billData.getCostAttributionCategory1();
            BillDataDTO.Entry<String> costAttributionCategory = new BillDataDTO.Entry();
            costAttributionCategory.setValue(costAttributeCategory);

            String costAttributionName = StringUtils.isBlank(billData.getCostAttributionName1())?billData.getCostAttributionName2():billData.getCostAttributionName1();
            BillDataDTO.Entry<String> costAttributeName = new BillDataDTO.Entry();
            costAttributeName.setValue(costAttributionName);
            billDataDTO.setCostAttributionCategory(costAttributionCategory);
            billDataDTO.setCostAttributionName(costAttributeName);
            billDataList.add(billDataDTO);
        }
        jdyBillListPushDTO.setDataList(billDataList);
        return jdyBillListPushDTO;
    }

    private void pushJdyBillData(JdyBillListPushDTO jdyBillListPushDTO,String companyId) {

        CompanyNewDto companyNewDto = ucCompanyService.getCompanyService().queryCompanyNewByCompanyId(companyId);
        ThirdCallbackRecord record = ThirdCallbackRecord.builder()
            .callbackData(com.fenbeitong.openapi.plugin.util.JsonUtils.toJson(jdyBillListPushDTO))
            .callbackStatus(CallbackStatus.NEED_CALLBACK.getStatus())
            .callbackType(CallbackType.BILL.getType())
            .companyId(companyId)
            .companyName(companyNewDto.getCompanyName())
            .build();
        recordDao.saveSelective(record);

        businessDataPushService.pushData(companyId, record, 0, 2);
    }
}

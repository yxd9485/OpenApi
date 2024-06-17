package com.fenbeitong.openapi.plugin.kingdee.common.service.impl;

import com.fenbeitong.openapi.plugin.etl.constant.EtlScriptType;
import com.fenbeitong.openapi.plugin.etl.dao.OpenThirdScriptConfigDao;
import com.fenbeitong.openapi.plugin.etl.entity.OpenThirdScriptConfig;
import com.fenbeitong.openapi.plugin.etl.service.impl.EtlUtils;
import com.fenbeitong.openapi.plugin.kingdee.common.constant.KingdeeConstant;
import com.fenbeitong.openapi.plugin.kingdee.common.dto.KingDeeK3CloudConfigDTO;
import com.fenbeitong.openapi.plugin.kingdee.common.dto.KingDeeReqConfigDTO;
import com.fenbeitong.openapi.plugin.kingdee.common.listener.KingDeekBillBeforeListener;
import com.fenbeitong.openapi.plugin.kingdee.common.service.KingDeeKPushService;
import com.fenbeitong.openapi.plugin.kingdee.support.dao.OpenKingdeeUrlConfigDao;
import com.fenbeitong.openapi.plugin.kingdee.support.entity.OpenKingdeeUrlConfig;
import com.fenbeitong.openapi.plugin.support.bill.service.IOpenBillService;
import com.fenbeitong.openapi.plugin.support.callback.dao.OpenThirdBillRecordDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.OpenBillDetailRecord;
import com.fenbeitong.openapi.plugin.support.callback.entity.OpenThirdBillRecord;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenTemplateConfigConstant;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenTemplateConfigDao;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenTemplateConfig;
import com.fenbeitong.openapi.plugin.support.voucher.dao.FinanceVoucherDataDao;
import com.fenbeitong.openapi.plugin.support.voucher.entity.FinanceVoucherData;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.RandomUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Description 订单、审批、凭证、账单推送
 * @Author duhui
 * @Date 2021/7/30
 **/
@Service
@ServiceAspect
@Slf4j
public class KingDeePushServiceImpl implements KingDeeKPushService {

    @Autowired
    KingDeeCommonServiceImpl kingDeeCommonService;
    @Autowired
    FinanceVoucherDataDao financeVoucherDataDao;
    @Autowired
    IOpenBillService iOpenBillService;
    @Autowired
    OpenThirdBillRecordDao openThirdBillRecordDao;
    @Autowired
    KingDeeBaseService kingDeeBaseService;
    @Autowired
    OpenKingdeeUrlConfigDao openKingdeeUrlConfigDao;
    @Autowired
    OpenTemplateConfigDao openTemplateConfigDao;
    @Autowired
    KingDeeOrgServiceImpl kingDeeOrgService;
    @Autowired
    private OpenThirdScriptConfigDao openThirdScriptConfigDao;

    @Override
    public void orderPush(String requestBody, String companyId, String moduleType, Integer templateType, String dataType) {
        KingDeeReqConfigDTO kingDeeReqConfigDTO = kingDeeBaseService.getAllReqConfig(companyId, moduleType, templateType);
        Map<String, Object> map = JsonUtils.toObj(requestBody, Map.class);
        log.info("订单数据推送金蝶: {}",JsonUtils.toJson(map));
        List<String> strList = new ArrayList<>();
        if (!StringUtils.isBlank(dataType)) {
            strList = Arrays.asList(dataType.split(","));
        }
        OpenThirdScriptConfig kingDeeOrderConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.KINGDEE_ORDER_PUSH);
        Boolean bool = true;
        if (!ObjectUtils.isEmpty(kingDeeOrderConfig)) {
            List<String> finalStrList = strList;
            bool = EtlUtils.etlFilter(kingDeeOrderConfig, new HashMap<String, Object>(4) {{
                put("map", map);
                put("strList", finalStrList);
            }});
        }
        if (bool) {
            log.info("etl 转换后的数据->>>{}",JsonUtils.toJson(map));
            kingDeeCommonService.sendData(companyId, kingDeeReqConfigDTO.getKingDeekListener(), kingDeeReqConfigDTO.getOpenKingdeeUrlConfig(), kingDeeReqConfigDTO.getToken(), kingDeeReqConfigDTO.getOpenKingdeeReqDataList(), map);
        }
    }

    @Override
    public void voucherPush(String companyId, String batchId) {
        KingDeeReqConfigDTO kingDeeReqConfigDTO = kingDeeBaseService.getAllReqConfig(companyId, "cwkj_zz_pz", OpenTemplateConfigConstant.TYPE.voucher);
        List<FinanceVoucherData> voucherDataList = financeVoucherDataDao.selectByBatchId(companyId, batchId);
        Map<String, List<FinanceVoucherData>> mapList = voucherDataList.stream().collect(Collectors.groupingBy(FinanceVoucherData::getProjectCode));
        mapList.forEach((k, v) -> {
            kingDeeCommonService.sendData(companyId, kingDeeReqConfigDTO.getKingDeekListener(), kingDeeReqConfigDTO.getOpenKingdeeUrlConfig(), kingDeeReqConfigDTO.getToken(), kingDeeReqConfigDTO.getOpenKingdeeReqDataList(), v);
        });

    }

    @Override
    public void billPush(String companyId, String billNo, String moduleType, Integer templateType) {
        KingDeeReqConfigDTO kingDeeReqConfigDTO = kingDeeBaseService.getAllReqConfig(companyId, moduleType, templateType);
        // 保存数据
        List<OpenThirdBillRecord> openThirdBillRecordList = packageThirdBiillData(iOpenBillService.saveBill(companyId, billNo, true), companyId, billNo, kingDeeReqConfigDTO.getToken());
        send(openThirdBillRecordList, companyId, billNo, moduleType, templateType);
    }

    @Override
    public void madeBillPush(String companyId, String billNo, String moduleType, Integer templateType) {
        // 保存数据
        List<OpenThirdBillRecord> openThirdBillRecordList = openThirdBillRecordDao.getListNotSuccess(companyId, billNo);
        send(openThirdBillRecordList, companyId, billNo, moduleType, templateType);
    }

    @Override
    public void initThirdData(String companyId, String billNo, String moduleType, Integer templateType) {
        KingDeeK3CloudConfigDTO kingDee3KCloudConfigDTO = kingDeeOrgService.getConfig(companyId);
        List<OpenThirdBillRecord> openThirdBillRecordList = openThirdBillRecordDao.getListNotSuccess(companyId, billNo);
        OpenKingdeeUrlConfig kingDeeUrlConfig = openKingdeeUrlConfigDao.getByCompanyId(companyId);
        KingDeeReqConfigDTO kingDeeReqConfigDTO = kingDeeBaseService.getAllReqConfig(companyId, moduleType, templateType);
        OpenTemplateConfig openTemplateConfig = openTemplateConfigDao.selectByCompanyId(companyId, OpenTemplateConfigConstant.TYPE.common, OpenType.KIngdee.getType());
        KingDeekBillBeforeListener kingDeekGetDataListener = kingDeeBaseService.getDataKingDeekLister(openTemplateConfig);
        if (!ObjectUtils.isEmpty(openThirdBillRecordList) && !ObjectUtils.isEmpty(kingDee3KCloudConfigDTO) && !ObjectUtils.isEmpty(kingDeeUrlConfig) && !ObjectUtils.isEmpty(kingDeeReqConfigDTO)) {
            // 业务数据
            openThirdBillRecordList.forEach(openThirdBillRecord -> {
                OpenBillDetailRecord openBillDetailRecord = JsonUtils.toObj(openThirdBillRecord.getBillDetailData(), OpenBillDetailRecord.class);
                kingDeekGetDataListener.setThirdData(companyId, kingDeeReqConfigDTO.getToken(), openBillDetailRecord, openThirdBillRecord, kingDeeUrlConfig, kingDee3KCloudConfigDTO);
            });
            billBusinessBeforeFilter(openThirdBillRecordList, companyId);
            openThirdBillRecordList.forEach(openThirdBillRecord -> {
                Example example = new Example(OpenThirdBillRecord.class);
                example.createCriteria().andEqualTo("id", openThirdBillRecord.getId());
                openThirdBillRecordDao.updateByExample(openThirdBillRecord, example);
            });
        } else {
            log.warn("金蝶推送账单数配置初始化有误,请检查配置");
        }
    }


    private void updateState(Map<String, List<OpenThirdBillRecord>> openThirdBillRecordList, Integer status) {
        openThirdBillRecordList.forEach((k, v) -> {
            v.forEach(t -> {
                Example example = new Example(OpenThirdBillRecord.class);
                example.createCriteria().andEqualTo("id", t.getId());
                openThirdBillRecordDao.updateByExample(OpenThirdBillRecord.builder().status(status).build(), example);
            });
        });
    }

    private List<OpenThirdBillRecord> packageThirdBiillData(List<OpenBillDetailRecord> openBillDetailRecords, String companyId, String billNo, String token) {
        KingDeeK3CloudConfigDTO kingDee3KCloudConfigDTO = kingDeeOrgService.getConfig(companyId);
        OpenKingdeeUrlConfig kingDeeUrlConfig = openKingdeeUrlConfigDao.getByCompanyId(companyId);
        OpenTemplateConfig openTemplateConfig = openTemplateConfigDao.selectByCompanyId(companyId, OpenTemplateConfigConstant.TYPE.common, OpenType.KIngdee.getType());
        KingDeekBillBeforeListener kingDeekGetDataListener = kingDeeBaseService.getDataKingDeekLister(openTemplateConfig);
        if (!ObjectUtils.isEmpty(openBillDetailRecords)) {
            List<OpenThirdBillRecord> srcOpenThirdBillRecordList = openThirdBillRecordDao.listAll(companyId, billNo);
            Set<String> strSet = null;
            if (!ObjectUtils.isEmpty(srcOpenThirdBillRecordList)) {
                strSet = srcOpenThirdBillRecordList.stream().map(t -> t.getCompanyId() + t.getOrderId() + t.getOrderCategory()).collect(Collectors.toSet());
            }
            List<OpenThirdBillRecord> openThirdBillRecordList = new ArrayList<>();
            Set<String> finalStrSet = strSet;
            openBillDetailRecords.forEach(openBillDetailRecord -> {
                if (ObjectUtils.isEmpty(finalStrSet) || !finalStrSet.contains(openBillDetailRecord.getCompanyId() + openBillDetailRecord.getOrderId() + openBillDetailRecord.getOrderCategory())) {
                    OpenThirdBillRecord openThirdBillRecord = OpenThirdBillRecord.builder()
                            .id(RandomUtils.bsonId())
                            .billNo(billNo)
                            .companyId(companyId)
                            .orderId(openBillDetailRecord.getOrderId())
                            .orderCategory(openBillDetailRecord.getOrderCategory())
                            .billDetailData(JsonUtils.toJson(openBillDetailRecord))
                            .status(0)
                            .createDate(new Date())
                            .updateDate(new Date())
                            .build();
                    kingDeekGetDataListener.setThirdData(companyId, token, openBillDetailRecord, openThirdBillRecord, kingDeeUrlConfig, kingDee3KCloudConfigDTO);
                    openThirdBillRecordList.add(openThirdBillRecord);
                }
            });
            // 业务数据
            billBusinessBeforeFilter(openThirdBillRecordList, companyId);
            if (!ObjectUtils.isEmpty(openThirdBillRecordList)) {
                openThirdBillRecordDao.saveList(openThirdBillRecordList);
            }
            return openThirdBillRecordList;
        }
        return null;
    }

    /**
     * 推送数据
     */
    private void send(List<OpenThirdBillRecord> openThirdBillRecordList, String companyId, String billNo, String moduleType, Integer templateType) {
        if (!ObjectUtils.isEmpty(openThirdBillRecordList)) {
            OpenTemplateConfig openTemplateConfig = openTemplateConfigDao.selectByCompanyId(companyId, OpenTemplateConfigConstant.TYPE.common, OpenType.KIngdee.getType());
            KingDeekBillBeforeListener kingDeekBillBeforeListener = kingDeeBaseService.getDataKingDeekLister(openTemplateConfig);
            Map<String, Map<String, List<OpenThirdBillRecord>>> map = kingDeekBillBeforeListener.dataGroup(openThirdBillRecordList);
            if (!ObjectUtils.isEmpty(map)) {
                KingDeeReqConfigDTO kingDeeReqConfigDTO = kingDeeBaseService.getAllReqConfig(companyId, moduleType, templateType);
                map.forEach((k, v) -> {
                    if (kingDeeCommonService.sendData(companyId, kingDeeReqConfigDTO.getKingDeekListener(), kingDeeReqConfigDTO.getOpenKingdeeUrlConfig(), kingDeeReqConfigDTO.getToken(), kingDeeReqConfigDTO.getOpenKingdeeReqDataList(), v)) {
                        updateState(v, KingdeeConstant.Status.SUCCESS);
                    } else {
                        updateState(v, KingdeeConstant.Status.FAIL);
                    }
                });
            }
        }
    }

    private void billBusinessBeforeFilter(List<OpenThirdBillRecord> openThirdBillRecordList, String companyId) {
        OpenThirdScriptConfig openThirdScriptConfig = openThirdScriptConfigDao.getCommonScriptConfig(companyId, EtlScriptType.BILL_PUSH, KingdeeConstant.TtlRelationType.BUSINESS);
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("openThirdBillRecordList", openThirdBillRecordList);
        }};
        EtlUtils.execute(openThirdScriptConfig.getScript(), params);
    }

}

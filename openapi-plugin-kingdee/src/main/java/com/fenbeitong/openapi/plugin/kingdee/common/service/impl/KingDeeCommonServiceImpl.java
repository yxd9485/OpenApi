package com.fenbeitong.openapi.plugin.kingdee.common.service.impl;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.kingdee.common.constant.KingdeeConstant;
import com.fenbeitong.openapi.plugin.kingdee.common.dto.KingDeeK3CloudConfigDTO;
import com.fenbeitong.openapi.plugin.kingdee.common.listener.KingDeekListener;
import com.fenbeitong.openapi.plugin.kingdee.common.service.KingDeeLoginService;
import com.fenbeitong.openapi.plugin.kingdee.support.dto.KingdeeConfig;
import com.fenbeitong.openapi.plugin.kingdee.support.entity.OpenKingdeeReqData;
import com.fenbeitong.openapi.plugin.kingdee.support.entity.OpenKingdeeUrlConfig;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenTemplateConfigDao;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 获取金蝶接口信息
 *
 * @Auther duhui
 * @Date 2021/6/3
 */

@Slf4j
@Service
public class KingDeeCommonServiceImpl extends KingDeeSuperService {
    @Autowired
    OpenSysConfigDao openSysConfigDao;
    @Autowired
    KingdeeConfig kingdeeConfig;


    @Autowired
    OpenTemplateConfigDao openTemplateConfigDao;
    @Autowired
    KingDeeLoginService kingDeeLoginService;


    public boolean sendData(String companyId, KingDeekListener kingDeekListener, OpenKingdeeUrlConfig openKingdeeUrlConfig, String token, List<OpenKingdeeReqData> openKingdeeReqDataList, Object... reqData) {
        KingDeeK3CloudConfigDTO kingDeeK3CloudConfigDTO = new KingDeeK3CloudConfigDTO();
        KingDeeK3CloudConfigDTO.Bills bills = new KingDeeK3CloudConfigDTO.Bills();
        if (!ObjectUtils.isEmpty(openKingdeeReqDataList)) {
            Map<String, List<OpenKingdeeReqData>> OpenKingdeeReqDataGroup = openKingdeeReqDataList.stream().collect(Collectors.groupingBy(OpenKingdeeReqData::getOperationType));
            if (!ObjectUtils.isEmpty(OpenKingdeeReqDataGroup.get(KingdeeConstant.Bills.SAVE))) {
                openKingdeeReqDataList = OpenKingdeeReqDataGroup.get(KingdeeConstant.Bills.SAVE).stream().sorted(Comparator.comparing(OpenKingdeeReqData::getDataLevel).reversed()).collect(Collectors.toList());
                AtomicInteger flag = new AtomicInteger(0);
                StringBuffer strData = new StringBuffer();
                openKingdeeReqDataList.forEach(openKingdeeReqData -> {
                    if (flag.get() == 0 || (openKingdeeReqData.getDataLevel() + 1) == flag.get()) {
                        dataSwatch(openKingdeeReqData.getDataType().intValue(), openKingdeeReqData.getDataKey(), openKingdeeReqData.getDataValue(), companyId, strData, kingDeekListener, reqData);
                        flag.set(openKingdeeReqData.getDataLevel());
                    } else {
                        throw new FinhubException(500, "OpenKingdeeReqData->dataLevel不连续");
                    }
                });
                log.info("保存金蝶信息->>>{}",strData);
                bills.setSaveStr(strData.toString());
            }
            if (!ObjectUtils.isEmpty(OpenKingdeeReqDataGroup.get(KingdeeConstant.Bills.COMMIT))) {
                bills.setCommitStr(OpenKingdeeReqDataGroup.get(KingdeeConstant.Bills.COMMIT).get(0).getDataValue());
            }
            if (!ObjectUtils.isEmpty(OpenKingdeeReqDataGroup.get(KingdeeConstant.Bills.AUDIT))) {
                bills.setAuditStr(OpenKingdeeReqDataGroup.get(KingdeeConstant.Bills.AUDIT).get(0).getDataValue());
            }
            kingDeeK3CloudConfigDTO.setBills(bills);
            return init(companyId, kingDeeK3CloudConfigDTO, kingDeekListener, openKingdeeUrlConfig, token, reqData);
        } else {
            log.warn("获取 openKingdeeReqDataList:{} 失败", JsonUtils.toJson(openKingdeeReqDataList));
            return false;
        }
    }

    public void dataSwatch(int data_type, String dataKey, String dataValue, String companyId, StringBuffer strData, KingDeekListener kingDeekListener, Object... objects) {
        switch (data_type) {
            case KingdeeConstant.DataType.MAP:
                kingDeekListener.setMap(dataKey, dataValue, companyId, strData,objects);
                break;
            case KingdeeConstant.DataType.LIST:
                kingDeekListener.setList(dataKey, dataValue, companyId, strData, objects);
                break;
            default:
                throw new FinhubException(500, "KingDeeCommonServiceImpl->dataSwatch 类型不存在");
        }
    }


}

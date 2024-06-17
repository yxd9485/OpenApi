package com.fenbeitong.openapi.plugin.customize.dashenlin.service.impl;

import com.fenbeitong.openapi.plugin.customize.dashenlin.dao.OpenDashenlinEmployeeCostAttributionDao;
import com.fenbeitong.openapi.plugin.customize.dashenlin.entity.OpenDashenlinEmployeeCostAttribution;
import com.fenbeitong.openapi.plugin.func.company.dto.FuncBillExtInfoTransformDTO;
import com.fenbeitong.openapi.plugin.func.company.service.ICompanyBillExtListener;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * 大参林账单扩展字段
 *
 * @author lizhen
 */
@ServiceAspect
@Service
public class DashenlinBillExtListener implements ICompanyBillExtListener {


    @Autowired
    private OpenDashenlinEmployeeCostAttributionDao openDashenlinEmployeeCostAttributionDao;

    @Override
    public void setBillExt(String companyId, Map<String, Object> srcData, Map<String, Object> resultData, FuncBillExtInfoTransformDTO transformDto) {
        if (ObjectUtils.isEmpty(resultData)) {
            return;
        }
        //如果审批单扩展属性为空，取订单自定义字段
        String travelApprovalFields = (String) resultData.get("travelApprovalFields");
        if (ObjectUtils.isEmpty(travelApprovalFields)) {
            List customRemarkList = (List) MapUtils.getValueByExpress(srcData, "data:customRemark");
            if (!ObjectUtils.isEmpty(customRemarkList)) {
                for (int i = 0; i < customRemarkList.size(); i++) {
                    Map remark = (Map) customRemarkList.get(i);
                    String customFieldTitle = (String) remark.get("custom_field_title");
                    if ("申请公司".equals(customFieldTitle)) {
                        travelApprovalFields = (String) remark.get("custom_field_content");
                        if (!ObjectUtils.isEmpty(travelApprovalFields)) {
                            String[] split = travelApprovalFields.split("：");
                            if (split.length >= 2) {
                                travelApprovalFields = "cost_attribution_id_oa:" + split[0] + ",cost_attribution_name_oa:" + split[1];
                                resultData.put("travelApprovalFields", travelApprovalFields);
                            }
                        }
                        break;
                    }
                }
            }
            String passengerUserIds = StringUtils.obj2str(resultData.get("passengerUserId"));
            String bookerUserId = StringUtils.obj2str(resultData.get("bookerUserId"));
            //如果审批单扩展属性和订单自定义字段都没有取到，取配置表数据
            if (ObjectUtils.isEmpty(travelApprovalFields) && !ObjectUtils.isEmpty(passengerUserIds)) {
                //优先取使用人，取不到（为非企业员工订票）取下单人
                String[] passengerUserIdArr = passengerUserIds.split(",");
                for (int i = 0; i < passengerUserIdArr.length; i++) {
                    OpenDashenlinEmployeeCostAttribution openDashenlinEmployeeCostAttribution = openDashenlinEmployeeCostAttributionDao.getOpenDashenlinEmployeeCostAttribution(passengerUserIdArr[i]);
                    if (!ObjectUtils.isEmpty(openDashenlinEmployeeCostAttribution)) {
                        String costAttributionIdOa = openDashenlinEmployeeCostAttribution.getCostAttributionIdOa();
                        String costAttributionNameOa = openDashenlinEmployeeCostAttribution.getCostAttributionNameOa();
                        travelApprovalFields = "cost_attribution_id_oa:" + costAttributionIdOa + ",cost_attribution_name_oa:" + costAttributionNameOa;
                        resultData.put("travelApprovalFields", travelApprovalFields);
                        break;
                    }
                }
            }
            //使用人未取到，取下单人
            if (ObjectUtils.isEmpty(travelApprovalFields) && !StringUtils.isBlank(bookerUserId)) {
                OpenDashenlinEmployeeCostAttribution openDashenlinEmployeeCostAttribution = openDashenlinEmployeeCostAttributionDao.getOpenDashenlinEmployeeCostAttribution(bookerUserId);
                if (!ObjectUtils.isEmpty(openDashenlinEmployeeCostAttribution)) {
                    String costAttributionIdOa = openDashenlinEmployeeCostAttribution.getCostAttributionIdOa();
                    String costAttributionNameOa = openDashenlinEmployeeCostAttribution.getCostAttributionNameOa();
                    travelApprovalFields = "cost_attribution_id_oa:" + costAttributionIdOa + ",cost_attribution_name_oa:" + costAttributionNameOa;
                    resultData.put("travelApprovalFields", travelApprovalFields);
                }
            }
        }
        //处理导出账单只保留中文的
        if (!ObjectUtils.isEmpty(travelApprovalFields)) {
            resultData.put("travelApprovalFieldsOriginal", travelApprovalFields);
            String[] split = travelApprovalFields.split(":");
            if (split.length == 3) {
                resultData.put("travelApprovalFields", split[2]);
            }
        }
    }
}

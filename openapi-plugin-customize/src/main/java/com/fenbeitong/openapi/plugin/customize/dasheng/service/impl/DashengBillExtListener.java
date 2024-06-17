package com.fenbeitong.openapi.plugin.customize.dasheng.service.impl;

import com.fenbeitong.openapi.plugin.func.company.dao.OpenEbsOrgCostRelationsDao;
import com.fenbeitong.openapi.plugin.func.company.dto.FuncBillExtInfoTransformDTO;
import com.fenbeitong.openapi.plugin.func.company.entity.OpenEbsOrgCostRelations;
import com.fenbeitong.openapi.plugin.func.company.service.ICompanyBillExtListener;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * <p>Title: DashengBillExtListener</p>
 * <p>Description: 大生账单扩展字段</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/10/26 2:35 PM
 */
@ServiceAspect
@Service
public class DashengBillExtListener implements ICompanyBillExtListener {

    @Autowired
    private OpenEbsOrgCostRelationsDao openEbsOrgCostRelationsDao;

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;

    @Override
    public void setBillExt(String companyId, Map<String, Object> srcData, Map<String, Object> resultData, FuncBillExtInfoTransformDTO transformDto) {
        if (ObjectUtils.isEmpty(resultData)) {
            return;
        }
        List<OpenMsgSetup> openMsgSetups = openMsgSetupDao.listByCompanyIdAndItemCodeList(companyId, Lists.newArrayList("51talk_customize_bill_info_cc_src_field"));
        String field = ObjectUtils.isEmpty(openMsgSetups) ? null : openMsgSetups.get(0).getStrVal1();
        if (ObjectUtils.isEmpty(field)) {
            field = "bookerDeptId";
        }
        String thirdDeptId = (String) resultData.get(field);
        OpenEbsOrgCostRelations orgCostRel = openEbsOrgCostRelationsDao.getByPsDeptId(thirdDeptId);
        if (orgCostRel != null) {
            resultData.put("ebsCcCode", orgCostRel.getEbsCcCode());
            resultData.put("ebsCcDesc", orgCostRel.getEbsCcDesc());
            resultData.put("ebsCcAttrCode", orgCostRel.getCostAttrCode());
        }
    }
}

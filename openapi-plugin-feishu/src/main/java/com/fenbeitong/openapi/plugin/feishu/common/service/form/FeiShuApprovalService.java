package com.fenbeitong.openapi.plugin.feishu.common.service.form;

import com.fenbeitong.openapi.plugin.feishu.common.listener.FeiShuApprovalListener;
import com.fenbeitong.openapi.plugin.feishu.common.listener.FeiShuApprovalDefaultListener;
import com.fenbeitong.openapi.plugin.feishu.eia.listener.impl.HuShangListener;
import com.fenbeitong.openapi.plugin.support.apply.dto.CommonApplyReqDTO;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenTemplateConfigDao;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenTemplateConfig;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.finhub.framework.core.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;


/**
 * 表单解析
 * @author xiaohai
 * @Date 2022/07/04
 */
@ServiceAspect
@Service
public class FeiShuApprovalService{

    @Autowired
    OpenTemplateConfigDao openTemplateConfigDao;

    /**
     * 飞书审批使用自带出差套件
     *
     * @param corpId
     * @param approvalId
     * @param form
     * @param type
     * @return
     */
    public CommonApplyReqDTO parseFeiShuTripApprovalForm(String companyId, String corpId, String approvalId, String form, int type , String thirdEmployeeId) {
        CommonApplyReqDTO commonApplyReqDTO;
        FeiShuApprovalListener feiShuApprovalListener = getFeiShuApprovalLister(companyId);
        if (1 == type) {
            //差旅
            commonApplyReqDTO = feiShuApprovalListener.parseFeiShuBusinessForm(companyId, corpId, approvalId, form);
        } else {
            //用车
            commonApplyReqDTO = feiShuApprovalListener.parseFeiShuCarForm(companyId, corpId, approvalId, form , thirdEmployeeId);
        }
        commonApplyReqDTO.getApply().setCompanyId(companyId);
        return commonApplyReqDTO;
    }

    /**
     * 反射获取监听类
     */
    private FeiShuApprovalListener getFeiShuApprovalLister(String companyId) {
        OpenTemplateConfig openTemplateConfig = openTemplateConfigDao.selectByCompanyId(companyId, 0, 3);
        if (openTemplateConfig != null) {
            String className = openTemplateConfig.getListenerClass();
            if (!ObjectUtils.isEmpty(className)) {
                Class clazz = null;
                try {
                    clazz = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if (clazz != null) {
                    Object bean = SpringUtils.getBean(clazz);
                    if (bean != null && bean instanceof FeiShuApprovalListener) {
                        return ((FeiShuApprovalListener) bean);
                    }
                }
            }
        }
        return SpringUtils.getBean(FeiShuApprovalDefaultListener.class);
    }

}

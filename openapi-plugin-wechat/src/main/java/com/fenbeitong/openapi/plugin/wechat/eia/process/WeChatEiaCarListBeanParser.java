package com.fenbeitong.openapi.plugin.wechat.eia.process;

import com.finhub.framework.core.SpringUtils;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenTemplateConfigDao;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenTemplateConfig;
import com.fenbeitong.openapi.plugin.wechat.common.dto.ApprovalInfo;
import com.fenbeitong.openapi.plugin.wechat.common.dto.CarApprovalInfo;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WeChatApprovalDetail;
import com.fenbeitong.openapi.plugin.wechat.eia.listener.Impl.WeChatCarDefaultListener;
import com.fenbeitong.openapi.plugin.wechat.eia.listener.WeChatCarListener;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * Created by dave.hansins on 19/12/23.
 */
@ServiceAspect
@Service("weChatEiaCarListBeanParser")
@Slf4j
public class WeChatEiaCarListBeanParser implements IWeChatEiaProcessFormParser {


    @Autowired
    OpenTemplateConfigDao openTemplateConfigDao;

    @Override
    public ApprovalInfo parse(String companyId, int applyType, String instanceId, WeChatApprovalDetail.WeChatApprovalInfo approvalInfo) {
        CarApprovalInfo processInfo = new CarApprovalInfo();
        //不为空时进行具体数据解析
        //审批行程具体信息
        // 行程表单信息
        List<ApprovalInfo.TripListBean> tripListBeans = getTripListBean(companyId, applyType, approvalInfo, processInfo);
        if (ObjectUtils.isEmpty(tripListBeans)) {
            return null;
        }
        processInfo.setTripList(tripListBeans);
        return processInfo;
    }

    public List<CarApprovalInfo.TripListBean> getTripListBean(String companyId, int type, WeChatApprovalDetail.WeChatApprovalInfo approvalInfo, CarApprovalInfo processInfo) {

        List<ApprovalInfo.TripListBean> tripBeans = Lists.newArrayList();
        if (!ObjectUtils.isEmpty(approvalInfo)) {
            String spNo = approvalInfo.getSpNo();
            String spName = approvalInfo.getSpName();
            WeChatApprovalDetail.Applyer applyer = approvalInfo.getApplyer();
            if (!ObjectUtils.isEmpty(applyer)) {
                String userId = applyer.getUserId();
                log.info("审批单创建单号：{},人员ID: {},部门ID: {}", spNo, userId, applyer.getPartyId());
                //审批行程具体信息
                WeChatApprovalDetail.ApplyData applyData = approvalInfo.getApplyData();
                if (!ObjectUtils.isEmpty(applyData)) {
                    List<WeChatApprovalDetail.Content> contens = applyData.getContens();
                    if (!ObjectUtils.isEmpty(contens)) {
                        // 获取监听
                        WeChatCarListener weChatCarListener = getWeChatCarLister(companyId);
                        // 钉钉用车模板监听
                        weChatCarListener.filterEiaWeChat(approvalInfo, tripBeans, contens, processInfo, type, companyId, userId);

                    }
                }
            }
        }
        return tripBeans;
    }


    /**
     * 反射获取监听类
     */
    private WeChatCarListener getWeChatCarLister(String companyId) {
        OpenTemplateConfig dingTalkTemplateConfig = openTemplateConfigDao.selectByCompanyId(companyId, 1, 2);
        if (dingTalkTemplateConfig != null) {
            String className = dingTalkTemplateConfig.getListenerClass();
            if (!ObjectUtils.isEmpty(className)) {
                Class clazz = null;
                try {
                    clazz = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if (clazz != null) {
                    Object bean = SpringUtils.getBean(clazz);
                    if (bean != null && bean instanceof WeChatCarListener) {
                        return ((WeChatCarListener) bean);
                    }
                }
            }
        }
        return SpringUtils.getBean(WeChatCarDefaultListener.class);
    }

}

package com.fenbeitong.openapi.plugin.welink.isv.service;

import com.fenbeitong.openapi.plugin.welink.isv.dao.WeLinkIsvOrderDao;
import com.fenbeitong.openapi.plugin.welink.isv.entity.WeLinkIsvOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;

/**
 * @author lizhen
 * @date 2020/7/1
 */
@ServiceAspect
@Service
public class WeLinkIsvOrderService {

    @Autowired
    private WeLinkIsvOrderDao weLinkIsvOrderDao;

    public void saveWeLinkIsvOrder(WeLinkIsvOrder weLinkIsvOrder) {
        weLinkIsvOrderDao.saveSelective(weLinkIsvOrder);
    }
}

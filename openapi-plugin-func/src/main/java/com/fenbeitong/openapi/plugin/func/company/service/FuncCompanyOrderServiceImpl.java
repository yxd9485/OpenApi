package com.fenbeitong.openapi.plugin.func.company.service;

import com.finhub.framework.core.SpringUtils;
import com.fenbeitong.openapi.plugin.func.company.dao.OpenCompanyBillExtConfigDao;
import com.fenbeitong.openapi.plugin.func.company.dto.FuncBillExtInfoParamDTO;
import com.fenbeitong.openapi.plugin.func.company.entity.OpenCompanyBillExtConfig;
import com.fenbeitong.openapi.plugin.support.company.service.ICompanyOrderService;
import com.fenbeitong.openapi.plugin.support.company.service.impl.HttpCompanyOrderServiceImpl;
import com.fenbeitong.openapi.plugin.support.util.FinhubAdminTokenUtil;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * <p>Title: FuncCompanyOrderServiceImpl</p>
 * <p>Description: 公司订单服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/7/31 7:50 PM
 */
@SuppressWarnings("unchecked")
@ServiceAspect
@Service
public class FuncCompanyOrderServiceImpl {

    @Autowired
    private OpenCompanyBillExtConfigDao companyBillExtConfigDao;

    public Map<String, Object> getOrder(String orderId, String ticketId, int type) {
        List<OpenCompanyBillExtConfig> billExtConfigs = companyBillExtConfigDao.getByType(type);
        Map<String, Object> result = Maps.newHashMap();
        if (!ObjectUtils.isEmpty(billExtConfigs)) {
            for (OpenCompanyBillExtConfig billExtConfig : billExtConfigs) {
                Map<String, Object> data = getOrderDetail(billExtConfig, orderId, ticketId, type);
                if (!ObjectUtils.isEmpty(data)) {
                    data.put("companyBillEtlConfigId", billExtConfig.getEtlConfigId());
                    result.putAll(data);
                    break;
                }
            }
        }
        return result;
    }

    private Map<String, Object> getOrderDetail(OpenCompanyBillExtConfig config, String orderId, String ticketId, int type) {
        //请求方法 1:http;2:dubbo
        int requestMethod = config.getRequestMethod();
        String requestParam = config.getRequestParam();
        FuncBillExtInfoParamDTO extInfoParamDto = JsonUtils.toObj(requestParam, FuncBillExtInfoParamDTO.class);
        ICompanyOrderService companyOrderService = getCompanyOrderService(extInfoParamDto.getClazzName());
        return requestMethod == 1 ? getExtInfoByHttp(companyOrderService, extInfoParamDto.getUrl(), orderId, ticketId) : requestMethod == 2 ? getExtInfoByDubbo(companyOrderService, orderId, ticketId, type) : Maps.newHashMap();
    }

    private Map<String, Object> getExtInfoByDubbo(ICompanyOrderService companyOrderService, String orderId, String ticketId, int type) {
        return (Map<String, Object>) companyOrderService.dubboLoadOrderDetail(null, null, orderId, ticketId, type);
    }

    private Map<String, Object> getExtInfoByHttp(ICompanyOrderService companyOrderService, String url, String orderId, String ticketId) {
        String token = FinhubAdminTokenUtil.getStereoAdminToken();
        Map<String, Object> data = (Map<String, Object>) companyOrderService.httpLoadOrderDetail(url, token, null, null, orderId, ticketId);
        Map<String, Object> result = ObjectUtils.isEmpty(data) ? Maps.newHashMap() : NumericUtils.obj2int(data.get("code")) != 0 ? Maps.newHashMap() : data;
        Map<String, Object> dataMap = (Map<String, Object>) result.get("data");
        return ObjectUtils.isEmpty(dataMap) ? Maps.newHashMap() : result;
    }

    private ICompanyOrderService getCompanyOrderService(String clazzName) {
        ICompanyOrderService companyOrderService = null;
        if (!ObjectUtils.isEmpty(clazzName)) {
            try {
                Class clazz = Class.forName(clazzName);
                companyOrderService = (ICompanyOrderService) SpringUtils.getBean(clazz);
            } catch (Exception e) {
            }
        }
        return companyOrderService == null ? SpringUtils.getBean(HttpCompanyOrderServiceImpl.class) : companyOrderService;
    }

}

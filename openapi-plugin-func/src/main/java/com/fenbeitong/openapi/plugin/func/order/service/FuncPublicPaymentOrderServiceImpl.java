package com.fenbeitong.openapi.plugin.func.order.service;

import org.apache.dubbo.config.annotation.DubboReference;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.noc.api.service.bank.model.dto.req.BankPublicSearchQueryReqDTO;
import com.fenbeitong.noc.api.service.bank.model.vo.BankOrderPublicStereoVO;
import com.fenbeitong.noc.api.service.bank.service.IBankOrderSearchService;
import com.fenbeitong.noc.api.service.base.BasePageResDTO;
import com.fenbeitong.noc.api.service.base.BasePageVO;
import com.fenbeitong.openapi.plugin.etl.service.IEtlService;
import com.fenbeitong.openapi.plugin.func.order.dto.PublicPaymentOrderListReqDTO;
import com.fenbeitong.openapi.plugin.support.order.AbstractOrderService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.luastar.swift.base.utils.ObjUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: FuncPublicPaymentOrderServiceImpl</p>
 * <p>Description: 对公支付订单开放</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaowei
 * @date 2020/8/29 4:58 PM
 */
@Slf4j
@ServiceAspect
@Service
public class FuncPublicPaymentOrderServiceImpl extends AbstractOrderService {

    private static final Long PUBLIC_PAYMENT_LIST = 2360l;


    @DubboReference(check = false)
    private IBankOrderSearchService bankOrderSearchService;

    @Autowired
    private IEtlService etlService;


    public Object list(PublicPaymentOrderListReqDTO req) {
        Map<String, Object> results = new HashMap<>();
        try {
            BankPublicSearchQueryReqDTO queryReq = new BankPublicSearchQueryReqDTO();
            queryReq.setOrderId(req.getOrderId());
            queryReq.setCompanyId(req.getCompanyId());
            queryReq.setUserName(req.getUserName());
            queryReq.setUserPhone(req.getUserPhone());
            queryReq.setBankAccountNo(req.getBankAccountNo());
            if (!StringUtils.isBlank(req.getBankHupoTransType())) {
                queryReq.setBankHupoTransType(Integer.parseInt(req.getBankHupoTransType()));
            } else {
                queryReq.setBankHupoTransType(1);
            }
            if (!StringUtils.isBlank(req.getOrderStatus())) {
                queryReq.setOrderStatus(Integer.valueOf(req.getOrderStatus()));
            }
            BasePageVO basePageVO = new BasePageVO();
            basePageVO.setPage(ObjUtils.toInteger(req.getPageIndex(), 1));
            basePageVO.setPageSize(ObjUtils.toInteger(req.getPageSize(), 10));
            queryReq.setPageInfo(basePageVO);
            if (!ObjectUtils.isEmpty(req.getCreateTimeBegin()) || !ObjectUtils.isEmpty(req.getCreateTimeEnd())) {
                queryReq.setCreateBegin(req.getCreateTimeBegin());
                queryReq.setCreateEnd(req.getCreateTimeEnd());
            }
            BasePageResDTO<BankOrderPublicStereoVO> publiyPayPageSearchDTO = bankOrderSearchService.stereoPubliyPayPageSearch(queryReq);
            if (publiyPayPageSearchDTO != null && publiyPayPageSearchDTO.getList() != null && publiyPayPageSearchDTO.getList().size() > 0) {
                List<Map> transform = etlService.transform(PUBLIC_PAYMENT_LIST, JsonUtils.toObj(JsonUtils.toJson(publiyPayPageSearchDTO.getList()), new TypeReference<List<Map<String, Object>>>() {
                }));
                results.put("results", transform);
                results.put("page_index", publiyPayPageSearchDTO.getPageInfo().getCurrentPage());
                results.put("page_size", publiyPayPageSearchDTO.getPageInfo().getPageSize());
                results.put("total_pages", (publiyPayPageSearchDTO.getPageInfo().getTotalPages()));
                results.put("total_count", publiyPayPageSearchDTO.getPageInfo().getTotalSize());
            }
        } catch (Exception e) {
            log.warn(">>>对公支付订单列表查询接口>>>{}调用时异常", e);
        }
        return results;
    }


}

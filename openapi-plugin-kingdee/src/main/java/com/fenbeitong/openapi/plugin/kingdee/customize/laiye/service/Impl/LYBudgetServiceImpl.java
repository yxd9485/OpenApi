package com.fenbeitong.openapi.plugin.kingdee.customize.laiye.service.Impl;

import com.fenbeitong.openapi.plugin.kingdee.common.dto.KingDeekBudgetDTO;
import com.fenbeitong.openapi.plugin.kingdee.customize.laiye.service.LYBudgetService;
import com.fenbeitong.openapi.plugin.kingdee.customize.laiye.service.LYKingDeeBudgetService;
import com.fenbeitong.openapi.plugin.support.callback.dao.TbThirdCallbackRecordShadowDao;
import com.fenbeitong.openapi.plugin.support.callback.dto.TbThirdCallbackConfShadowDao;
import com.fenbeitong.openapi.plugin.support.callback.entity.TbThirdCallbackConfShadow;
import com.fenbeitong.openapi.plugin.support.callback.entity.TbThirdCallbackRecordShadow;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackConf;
import com.fenbeitong.openapi.plugin.support.callback.entity.ThirdCallbackRecord;
import com.fenbeitong.openapi.plugin.support.callback.mapper.TbThirdCallbackRecordShadowMapper;
import com.fenbeitong.openapi.plugin.support.callback.mapper.ThirdCallbackRecordMapper;
import com.fenbeitong.openapi.plugin.support.callback.service.CallbackThirdSupportService;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>Title: LYBudgetService</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021/6/27 1:55 下午
 */
@ServiceAspect
@Service
@Slf4j
public class LYBudgetServiceImpl implements LYBudgetService {
    @Autowired
    LYKingDeeBudgetService budgetService;

    @Autowired
    ThirdCallbackRecordMapper thirdCallbackRecordMapper;

    @Autowired
    TbThirdCallbackRecordShadowMapper tbThirdCallbackRecordShadowMapper;

    @Autowired
    TbThirdCallbackRecordShadowDao tbThirdCallbackRecordShadowDao;

    @Autowired
    CallbackThirdSupportService callbackThirdSupportService;

    @Autowired
    TbThirdCallbackConfShadowDao tbThirdCallbackConfShadowDao;


    @Override
    public boolean subtractBudget(String companyId, String callbackType) {
        Map<String, Object> map = new HashMap();
        map.put("companyId", companyId);
        map.put("callbackStatus", -1);
        map.put("callbackType", callbackType);
        List<TbThirdCallbackRecordShadow> tbThirdCallbackRecordShadow = tbThirdCallbackRecordShadowDao.listTbThirdCallbackRecordShadow(map);
        tbThirdCallbackRecordShadow.forEach(t -> {
            doSubtract(companyId, t);
        });
        return true;
    }

    public void doSubtract(String companyId, TbThirdCallbackRecordShadow thirdCallbackRecordShadow) {
        Map<String, String> map = JsonUtils.toObj(thirdCallbackRecordShadow.getCallbackData(), Map.class);
        Map priceInfo = (Map) MapUtils.getValueByExpress(map, "price_info");
        Map orderThirdInfo = (Map) MapUtils.getValueByExpress(map, "third_info");
        Map userInfo = (Map) MapUtils.getValueByExpress(map, "user_info");
        KingDeekBudgetDTO kingDeekBudgetDTO = new KingDeekBudgetDTO();
        kingDeekBudgetDTO.setFPeriod(DateUtils.getQuarter(new Date()).toString());
        kingDeekBudgetDTO.setMoney(new BigDecimal(priceInfo.get("total_price").toString()));
        kingDeekBudgetDTO.setOrgCode(orderThirdInfo.get("unit_id").toString());
        kingDeekBudgetDTO.setOrgName(userInfo.get("unit_name").toString());
        kingDeekBudgetDTO.setFAdjustDate(DateUtils.toSimpleStr(new Date(), false));
        TbThirdCallbackRecordShadow updateTbThirdCallbackRecordShadow = new TbThirdCallbackRecordShadow();
        updateTbThirdCallbackRecordShadow.setId(thirdCallbackRecordShadow.getId());
        boolean result = false;
        try {
            result = budgetService.subtractBudget(companyId, kingDeekBudgetDTO);
        } catch (Exception e) {
            log.info("{}", e);
        }
        if (result) {
            updateTbThirdCallbackRecordShadow.setCallbackStatus(0);
        } else {
            updateTbThirdCallbackRecordShadow.setCallbackStatus(1);
        }
        tbThirdCallbackRecordShadowMapper.updateByPrimaryKeySelective(updateTbThirdCallbackRecordShadow);
    }

    @Override
    public void separateOrder(String data, String callbackType, String companyId) {
        Map<String, String> map = JsonUtils.toObj(data, Map.class);
        Map orderInfo = (Map) MapUtils.getValueByExpress(map, "order_info");
        String orderId = orderInfo.get("order_id").toString();
        Example thirdCallbackRecordExample = new Example(ThirdCallbackRecord.class);
        thirdCallbackRecordExample.createCriteria().andEqualTo("companyId", companyId).andEqualTo("callbackStatus", "-1").andEqualTo("orderId", orderId);
        List<ThirdCallbackRecord> thirdCallbackRecordList = thirdCallbackRecordMapper.selectByExample(thirdCallbackRecordExample);
        Example thirdCallbackRecordShadowExample = new Example(TbThirdCallbackRecordShadow.class);
        thirdCallbackRecordShadowExample.createCriteria().andEqualTo("companyId", companyId).andEqualTo("orderId", orderId);
        List<TbThirdCallbackRecordShadow> TbThirdCallbackRecordShadowList = tbThirdCallbackRecordShadowMapper.selectByExample(thirdCallbackRecordShadowExample);
        // 备份入库
        if (TbThirdCallbackRecordShadowList.size() <= 0) {
            thirdCallbackRecordList.forEach(t -> {
                insertShadow(t);
            });
        } else {
            Map<String, TbThirdCallbackRecordShadow> TbThirdCallbackRecordShadowMap = TbThirdCallbackRecordShadowList.stream().collect(Collectors.toMap(t -> {
                return StringUtils.md5(t.getCallbackData());
            }, Function.identity(), (o, n) -> n));
            Map<String, ThirdCallbackRecord> thirdCallbackRecordMap = thirdCallbackRecordList.stream().collect(Collectors.toMap(t -> {
                return StringUtils.md5(t.getCallbackData());
            }, Function.identity(), (o, n) -> n));
            thirdCallbackRecordMap.forEach((k, v) -> {
                if (!TbThirdCallbackRecordShadowMap.containsKey(k)) {
                    insertShadow(v);
                }
            });
        }
        // 数据分发
        pushData(companyId, thirdCallbackRecordList, callbackType);
    }

    @Override
    public boolean quarterSubtractBudget(String companyId, String object) {
        return false;
    }

    /**
     * 数据入库
     */
    public void insertShadow(ThirdCallbackRecord thirdCallbackRecord) {
        TbThirdCallbackRecordShadow tbThirdCallbackRecordShadow = new TbThirdCallbackRecordShadow();
        BeanUtils.copyProperties(thirdCallbackRecord, tbThirdCallbackRecordShadow);
        tbThirdCallbackRecordShadow.setId(null);
        tbThirdCallbackRecordShadow.setUpdateTime(null);
        tbThirdCallbackRecordShadow.setCreateTime(new Date());
        tbThirdCallbackRecordShadow.setCallbackStatus(-1);
        tbThirdCallbackRecordShadowMapper.insert(tbThirdCallbackRecordShadow);
    }

    /**
     * 数据推送
     */
    public void pushData(String companyId, List<ThirdCallbackRecord> companyCallBackRecordList, String callbackType) {
        Example example = new Example(TbThirdCallbackConfShadow.class);
        example.createCriteria().andEqualTo("companyId", companyId).andEqualTo("callbackType", callbackType);
        TbThirdCallbackConfShadow tbThirdCallbackConfShadow = tbThirdCallbackConfShadowDao.getByExample(example);
        ThirdCallbackConf thirdCallbackConf = new ThirdCallbackConf();
        BeanUtils.copyProperties(tbThirdCallbackConfShadow, thirdCallbackConf);
        callbackThirdSupportService.pushCompanyData(companyId, companyCallBackRecordList, 1, true, thirdCallbackConf);
    }
}

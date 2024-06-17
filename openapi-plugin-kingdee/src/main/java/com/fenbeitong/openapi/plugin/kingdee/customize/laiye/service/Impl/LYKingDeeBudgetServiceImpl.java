package com.fenbeitong.openapi.plugin.kingdee.customize.laiye.service.Impl;

import com.fenbeitong.openapi.plugin.kingdee.common.constant.KingdeeConstant;
import com.fenbeitong.openapi.plugin.kingdee.common.dto.KingDeeK3CloudConfigDTO;
import com.fenbeitong.openapi.plugin.kingdee.common.dto.KingDeekBudgetDTO;
import com.fenbeitong.openapi.plugin.kingdee.common.listener.KingDeekListener;
import com.fenbeitong.openapi.plugin.kingdee.common.service.KingDeeLoginService;
import com.fenbeitong.openapi.plugin.kingdee.common.service.impl.KingDeeSuperService;
import com.fenbeitong.openapi.plugin.kingdee.customize.laiye.service.LYKingDeeBudgetService;
import com.fenbeitong.openapi.plugin.kingdee.support.dao.OpenKingdeeReqDataDao;
import com.fenbeitong.openapi.plugin.kingdee.support.dto.KingdeeConfig;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.dao.OpenSysConfigDao;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.entity.OpenSysConfig;
import com.fenbeitong.openapi.plugin.support.common.sysconfig.enums.OpenSysConfigType;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenTemplateConfigConstant;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenTemplateConfigDao;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenTemplateConfig;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: BudgetServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021/6/22 5:00 下午
 */
@Service
@Slf4j
public class LYKingDeeBudgetServiceImpl extends KingDeeSuperService implements LYKingDeeBudgetService {
    @Autowired
    KingDeeLoginService loginService;
    @Autowired
    KingdeeConfig kingdeeConfig;
    @Autowired
    OpenTemplateConfigDao openTemplateConfigDao;
    @Autowired
    OpenSysConfigDao openSysConfigDao;
    @Autowired
    OpenKingdeeReqDataDao openKingdeeReqDataDao;


    @Override
    public boolean subtractBudget(String companyId, KingDeekBudgetDTO kingDeekBudgetDTO) {
        //查询预算数据
        KingDeeK3CloudConfigDTO kingDeeK3CloudConfigDTO = getConfig(companyId);
        Map map = JsonUtils.toObj(JsonUtils.toJson(kingDeeK3CloudConfigDTO.getLogin()), Map.class);
        MultiValueMap multiValueMap = new LinkedMultiValueMap<>();
        map.forEach((k, v) -> {
            multiValueMap.add(k, v);
        });
        String token = loginService.gettoken(kingDeeK3CloudConfigDTO.getUrl() + kingdeeConfig.getLoginByAppSecret(), multiValueMap);
        log.info("金蝶预算-> companyId:{},token:{},kingDeeK3CloudConfigDTO:{}", companyId, token, kingDeeK3CloudConfigDTO);
        // 执行预算扣减
        Map<String, String> doMap = new HashMap<>();
        OpenTemplateConfig openTemplateConfig = openTemplateConfigDao.selectByCompanyId(companyId, OpenTemplateConfigConstant.TYPE.trip, OpenType.KIngdee.getType());
        KingDeekListener kingDeekListener = getKingDeekLister(openTemplateConfig);
        if (!ObjectUtils.isEmpty(kingDeeK3CloudConfigDTO.getBills().getSaveStr())) {
            doMap.put(KingdeeConstant.Bills.SAVE, kingDeekListener.saveParse(kingDeeK3CloudConfigDTO.getBills().getSaveStr(), companyId,kingDeekBudgetDTO));
        }
        if (!ObjectUtils.isEmpty(kingDeeK3CloudConfigDTO.getBills().getCommitStr())) {
            doMap.put(KingdeeConstant.Bills.COMMIT, kingDeeK3CloudConfigDTO.getBills().getCommitStr());
        }
        if (!ObjectUtils.isEmpty(kingDeeK3CloudConfigDTO.getBills().getAuditStr())) {
            doMap.put(KingdeeConstant.Bills.AUDIT, kingDeeK3CloudConfigDTO.getBills().getAuditStr());
        }
        return invoking(doMap, kingDeeK3CloudConfigDTO.getUrl(), token, kingDeeK3CloudConfigDTO, kingDeekListener,companyId);
    }

    @Override
    public boolean quarterSubtractBudget(String companyId, KingDeekBudgetDTO kingDeekBudgetDTO) {
        return false;
    }

    public KingDeeK3CloudConfigDTO getConfig(String companyId) {
        // 获取配置信息
        HashMap<String, Object> configMap = Maps.newHashMap();
        configMap.put("code", companyId);
        configMap.put("type", OpenSysConfigType.JINDIE_3kCLOUD_SYS_CONFIG.getType());
        OpenSysConfig openSysConfig = openSysConfigDao.getOpenSysConfig(configMap);
        KingDeeK3CloudConfigDTO kingDee3KCloudConfigDTO = JsonUtils.toObj(openSysConfig.getValue(), KingDeeK3CloudConfigDTO.class);
        return kingDee3KCloudConfigDTO;
    }


}

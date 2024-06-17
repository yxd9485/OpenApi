package com.fenbeitong.openapi.plugin.kingdee.common.service.impl;

import com.fenbeitong.openapi.plugin.kingdee.common.constant.KingdeeConstant;
import com.fenbeitong.openapi.plugin.kingdee.common.dto.KingDeeK3CloudConfigDTO;
import com.fenbeitong.openapi.plugin.kingdee.common.listener.KingDeekListener;
import com.fenbeitong.openapi.plugin.kingdee.support.dao.OpenKingdeeUrlConfigDao;
import com.fenbeitong.openapi.plugin.kingdee.support.entity.OpenKingdeeUrlConfig;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenTemplateConfigDao;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: KingDeeSuperServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021/7/27 7:39 下午
 */
@Service
@Slf4j
public class KingDeeSuperService extends KingDeeBaseService {

    @Autowired
    OpenTemplateConfigDao openTemplateConfigDao;

    @Autowired
    OpenKingdeeUrlConfigDao openKingdeeUrlConfigDao;

    public boolean init(String companyId, KingDeeK3CloudConfigDTO kingDeeK3CloudConfigDTO, KingDeekListener kingDeekListener, OpenKingdeeUrlConfig openKingdeeUrlConfig,String token, Object... objects) {
        log.info("金蝶-> companyId:{},token:{},kingDeeK3CloudConfigDTO:{}", companyId, token, kingDeeK3CloudConfigDTO);
        Map<String, String> doMap = new HashMap<>();
        if (!ObjectUtils.isEmpty(kingDeeK3CloudConfigDTO.getBills().getSaveStr())) {
            doMap.put(KingdeeConstant.Bills.SAVE, kingDeekListener.saveParse(kingDeeK3CloudConfigDTO.getBills().getSaveStr(),companyId, objects));
        }
        if (!ObjectUtils.isEmpty(kingDeeK3CloudConfigDTO.getBills().getCommitStr())) {
            doMap.put(KingdeeConstant.Bills.COMMIT, kingDeeK3CloudConfigDTO.getBills().getCommitStr());
        }
        if (!ObjectUtils.isEmpty(kingDeeK3CloudConfigDTO.getBills().getAuditStr())) {
            doMap.put(KingdeeConstant.Bills.AUDIT, kingDeeK3CloudConfigDTO.getBills().getAuditStr());
        }
        return invoking(doMap, openKingdeeUrlConfig.getUrl(), token, kingDeeK3CloudConfigDTO, kingDeekListener,companyId);
    }

}

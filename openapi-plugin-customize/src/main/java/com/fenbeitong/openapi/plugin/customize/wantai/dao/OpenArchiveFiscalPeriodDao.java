package com.fenbeitong.openapi.plugin.customize.wantai.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.customize.wantai.entity.OpenArchiveFiscalPeriod;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.RandomUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;

import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lizhen on 2022/07/26.
 */
@Component
public class OpenArchiveFiscalPeriodDao extends OpenApiBaseDao<OpenArchiveFiscalPeriod> {

    /**
     * 使用map条件查询
     *
     * @param condition
     * @return
     */
    public List<OpenArchiveFiscalPeriod> listOpenArchiveFiscalPeriod(Map<String, Object> condition) {
        Example example = new Example(OpenArchiveFiscalPeriod.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return listByExample(example);
    }

    /**
     * 使用map条件查询
     *
     * @param condition
     * @return
     */
    public OpenArchiveFiscalPeriod getOpenArchiveFiscalPeriod(Map<String, Object> condition) {
        Example example = new Example(OpenArchiveFiscalPeriod.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

    public OpenArchiveFiscalPeriod getByFiscalPeriod(String companyId, String fiscalPeriod, String sysCode,
        String thirdArchiveType) {
        if (StringUtils.isBlank(companyId) || StringUtils.isBlank(fiscalPeriod) || StringUtils.isBlank(sysCode)
            || StringUtils.isBlank(thirdArchiveType)) {
            return null;
        }
        Map<String, Object>condition = new HashMap<>();
        condition.put("companyId", companyId);
        condition.put("fiscalPeriod", fiscalPeriod);
        condition.put("sysCode", sysCode);
        condition.put("thirdArchiveType", thirdArchiveType);
        return getOpenArchiveFiscalPeriod(condition);
    }


    public void createOrUpdateArchiveFiscal(String companyId, String orgCode, String fiscalPeriod,
        String lastSyncTime, String sysCode,String thirdArchiveType) {
        OpenArchiveFiscalPeriod archiveFiscalPeriod = getByFiscalPeriod(companyId, fiscalPeriod, sysCode, thirdArchiveType);
        if (archiveFiscalPeriod == null) {
            archiveFiscalPeriod =
                OpenArchiveFiscalPeriod.builder().companyId(companyId).orgCode(orgCode).fiscalPeriod(fiscalPeriod).id(
                    RandomUtils.bsonId()).sysCode(sysCode).lastSyncTime(DateUtils.toDate(lastSyncTime)).thirdArchiveType(thirdArchiveType).build();
            save(archiveFiscalPeriod);
        } else {
            archiveFiscalPeriod.setLastSyncTime(DateUtils.toDate(lastSyncTime));
            updateById(archiveFiscalPeriod);
        }
    }
}

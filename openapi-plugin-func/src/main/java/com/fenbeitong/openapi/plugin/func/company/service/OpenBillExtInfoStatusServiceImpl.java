package com.fenbeitong.openapi.plugin.func.company.service;

import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.func.company.dao.OpenBillExtInfoDao;
import com.fenbeitong.openapi.plugin.func.company.entity.OpenBillExtInfo;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.flexible.entity.OpenMsgSetup;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Title: OpenBillExtInfoStatusServiceImpl</p>
 * <p>Description: 扩展字段状态</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/11/15 8:16 PM
 */
@Slf4j
@Service
public class OpenBillExtInfoStatusServiceImpl {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;

    @Autowired
    private OpenBillExtInfoDao openBillExtInfoDao;

    public void setStatus(OpenBillExtInfo openBillExtInfo) {
        List<String> checkExtFields = getCheckExtFields(openBillExtInfo.getCompanyId(), openBillExtInfo.getAccountType());
        if (ObjectUtils.isEmpty(checkExtFields)) {
            return;
        }
        String extData = openBillExtInfo.getExtData();
        Map extDataMap = JsonUtils.toObj(extData, Map.class);
        if (extDataMap == null) {
            extDataMap = Maps.newHashMap();
        }
        for (String checkField : checkExtFields) {
            Object checkValue = extDataMap.get(checkField);
            if (ObjectUtils.isEmpty(checkValue)) {
                openBillExtInfo.setStatus(2);
                break;
            }
        }
    }

    public boolean isCheckFields(String companyId, Integer accountType) {
        List<String> checkExtFields = getCheckExtFields(companyId, accountType);
        return !ObjectUtils.isEmpty(checkExtFields);
    }

    private List<String> getCheckExtFields(String companyId, Integer accountType) {
        String checkFieldsKey = MessageFormat.format(RedisKeyConstant.OPEN_PLUGIN_REDIS_KEY, "openBillExtInfo_checkFields_" + companyId + "_" + accountType);
        String checkFields = (String) redisTemplate.opsForHash().get(checkFieldsKey, "fields");
        Long timestamp = (Long) redisTemplate.opsForHash().get(checkFieldsKey, "timestamp");
        if (ObjectUtils.isEmpty(checkFields)) {
            long now = System.currentTimeMillis();
            if (timestamp == null) {
                OpenMsgSetup openMsgSetup = getOpenMsgSetup(companyId, accountType);
                checkFields = openMsgSetup == null ? "" : StringUtils.obj2str(openMsgSetup.getStrVal1(), "");
                redisTemplate.opsForHash().put(checkFieldsKey, "fields", checkFields);
                redisTemplate.opsForHash().put(checkFieldsKey, "timestamp", now);
            } else if (now - timestamp > 60 * 60 * 1000L) {
                OpenMsgSetup openMsgSetup = getOpenMsgSetup(companyId, accountType);
                checkFields = openMsgSetup == null ? "" : StringUtils.obj2str(openMsgSetup.getStrVal1(), "");
                redisTemplate.opsForHash().put(checkFieldsKey, "fields", checkFields);
                redisTemplate.opsForHash().put(checkFieldsKey, "timestamp", now);
            }
        }
        return ObjectUtils.isEmpty(checkFields) ? Lists.newArrayList() : Lists.newArrayList(checkFields.split(","));
    }

    private OpenMsgSetup getOpenMsgSetup(String companyId, Integer accountType) {
        List<OpenMsgSetup> openMsgSetups = openMsgSetupDao.listByCompanyIdAndItemCodeList(companyId, Lists.newArrayList("openBillExtInfo_checkFields_" + accountType));
        return ObjectUtils.isEmpty(openMsgSetups) ? null : openMsgSetups.get(0);
    }

    public Object noticeCheckMsg() {
        Example setupExample = new Example(OpenMsgSetup.class);
        setupExample.createCriteria().andIn("itemCode", Lists.newArrayList("openBillExtInfo_checkFields_1", "openBillExtInfo_checkFields_2"));
        List<OpenMsgSetup> openMsgSetups = openMsgSetupDao.listByExample(setupExample);
        if (ObjectUtils.isEmpty(openMsgSetups)) {
            return Maps.newHashMap();
        }
        List<String> companyIdList = openMsgSetups.stream().map(OpenMsgSetup::getCompanyId).collect(Collectors.toList());
        Example extInfoExample = new Example(OpenMsgSetup.class);
        extInfoExample.createCriteria().andIn("companyId", companyIdList).andEqualTo("status", 2);
        List<OpenBillExtInfo> validList = openBillExtInfoDao.listByExample(extInfoExample);
        if (ObjectUtils.isEmpty(validList)) {
            return Maps.newHashMap();
        }
        Map<String, List<OpenBillExtInfo>> validMap = validList.stream().collect(Collectors.groupingBy(i -> i.getCompanyId() + "@" + i.getAccountType()));
        List<String> keyList = Lists.newArrayList(validMap.keySet());
        Collections.sort(keyList);
        Map<String, Object> res = Maps.newHashMap();
        for (String key : keyList) {
            List<OpenBillExtInfo> openBillExtInfos = validMap.get(key);
            String ids = openBillExtInfos.stream().map(OpenBillExtInfo::getId).collect(Collectors.joining("\n"));
            res.put(key, ids);
            log.warn("账单字段缺失:", new RuntimeException(key + ":\n" + ids));
        }
        return res;
    }
}

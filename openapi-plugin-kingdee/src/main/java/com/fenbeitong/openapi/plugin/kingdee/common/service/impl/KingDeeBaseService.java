package com.fenbeitong.openapi.plugin.kingdee.common.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fenbeitong.openapi.plugin.kingdee.common.constant.KingdeeConstant;
import com.fenbeitong.openapi.plugin.kingdee.common.dto.KingDeeK3CloudConfigDTO;
import com.fenbeitong.openapi.plugin.kingdee.common.dto.KingDeeReqConfigDTO;
import com.fenbeitong.openapi.plugin.kingdee.common.listener.KingDeekBillBeforeListener;
import com.fenbeitong.openapi.plugin.kingdee.common.listener.KingDeekListener;
import com.fenbeitong.openapi.plugin.kingdee.common.listener.impl.KingDeeBillBeforeDefaultListener;
import com.fenbeitong.openapi.plugin.kingdee.common.listener.impl.KingDeeDefaultListener;
import com.fenbeitong.openapi.plugin.kingdee.common.service.KingDeeLoginService;
import com.fenbeitong.openapi.plugin.kingdee.support.dao.OpenKingdeeDataRecordDao;
import com.fenbeitong.openapi.plugin.kingdee.support.dao.OpenKingdeeReqDataDao;
import com.fenbeitong.openapi.plugin.kingdee.support.dao.OpenKingdeeUrlConfigDao;
import com.fenbeitong.openapi.plugin.kingdee.support.dto.KingdeeConfig;
import com.fenbeitong.openapi.plugin.kingdee.support.entity.OpenKingdeeDataRecord;
import com.fenbeitong.openapi.plugin.kingdee.support.entity.OpenKingdeeReqData;
import com.fenbeitong.openapi.plugin.kingdee.support.entity.OpenKingdeeUrlConfig;
import com.fenbeitong.openapi.plugin.kingdee.support.service.KingdeeService;
import com.fenbeitong.openapi.plugin.support.apply.constant.ItemCodeEnum;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenTemplateConfigDao;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenTemplateConfig;
import com.fenbeitong.openapi.plugin.support.project.service.OpenProjectService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.RandomUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.sdk.dto.project.ListThirdProjectRespDTO;
import com.fenbeitong.usercenter.api.model.dto.employee.EmployeeContract;
import com.fenbeitong.usercenter.api.service.employee.IBaseEmployeeExtService;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.finhub.framework.core.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>Title: KingDeeSuperServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021/7/27 7:39 下午
 */
@ServiceAspect
@Service
@Slf4j
public class KingDeeBaseService {
    @Autowired
    KingDeeLoginService kingDeeLoginService;
    @Autowired
    OpenTemplateConfigDao openTemplateConfigDao;
    @Autowired
    private KingdeeService kingdeeService;
    @Autowired
    private KingdeeConfig kingdeeConfig;
    @Autowired
    OpenKingdeeReqDataDao openKingdeeReqDataDao;
    @Autowired
    OpenKingdeeUrlConfigDao openKingdeeUrlConfigDao;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Reference(check = false)
    private IBaseEmployeeExtService employeeExtService;
    @Autowired
    OpenProjectService openProjectService;
    @Autowired
    OpenKingdeeDataRecordDao openKingdeeDataRecordDao;
    @Autowired
    OpenMsgSetupDao openMsgSetupDao;

    private static final String JINDIE_EMPLOYEE = "jindie_employee:{0}";

    private static final String KINGDEE_PROJECT = "kingDee_bill_project:{0}";

    private static final String KINGDEE_SETUP = "kingDee_msgup:{0}";

    /**
     * 免登获取token
     */
    public String getToken(OpenKingdeeUrlConfig openKingdeeUrlConfig) {
        if (!StringUtils.isBlank(openKingdeeUrlConfig.getAppId()) && !StringUtils.isBlank(openKingdeeUrlConfig.getAppSecret())) {
            return kingDeeLoginService.gettoken(openKingdeeUrlConfig, kingdeeConfig.getLoginByAppSecret());
        } else {
            return kingDeeLoginService.gettoken(openKingdeeUrlConfig, kingdeeConfig.getLogin());
        }
    }

    /**
     * 获取配置
     */
    public KingDeekListener getKingDeekListener(String companyId, Integer type) {
        OpenTemplateConfig openTemplateConfig = openTemplateConfigDao.selectByCompanyId(companyId, type, OpenType.KIngdee.getType());
        return getKingDeekLister(openTemplateConfig);
    }

    public List<OpenKingdeeReqData> openKingdeeReqDataList(String companyId, String moduleType) {
        return openKingdeeReqDataDao.getReqDataConfig(companyId, moduleType);
    }

    /**
     * 提交、保存、审核
     */
    public boolean invoking(Map<String, String> doMap, String url, String token, KingDeeK3CloudConfigDTO kingDeeK3CloudConfigDTO, KingDeekListener kingDeekListener, String companyId) {
        if (!ObjectUtils.isEmpty(doMap)) {
            String id = saveOpenKingdeeDataRecord(companyId, doMap.get(KingdeeConstant.Bills.SAVE));
            // 保存
            String number = kingdeeService.getNumberBySave(url + kingdeeConfig.getSave(), token, doMap.get(KingdeeConstant.Bills.SAVE));
            if (!ObjectUtils.isEmpty(number)) {
                // 提交
                if (kingdeeService.submitAndAudit(url + kingdeeConfig.getSubmit(), token, kingDeekListener.commitParse(kingDeeK3CloudConfigDTO.getBills().getCommitStr(), number))) {
                    // 查询配置是否审核
                    if (getKIngdeeAuditConfig(companyId) <= 0) {
                        // 审核
                        if (kingdeeService.submitAndAudit(url + kingdeeConfig.getAudit(), token, kingDeekListener.auditParse(kingDeeK3CloudConfigDTO.getBills().getAuditStr(), number))) {
                            updateOpenKingdeeDataRecord(id, KingdeeConstant.OpenKingdeeDataRecordStatus.AUDIT, number);
                            return true;
                        } else {
                            updateOpenKingdeeDataRecord(id, KingdeeConstant.OpenKingdeeDataRecordStatus.COMMIT, number);
                            log.warn("Data:{},number:{},审核失败!", JsonUtils.toJson(doMap), number);
                        }
                    } else {
                        updateOpenKingdeeDataRecord(id, KingdeeConstant.OpenKingdeeDataRecordStatus.COMMIT, number);
                        return true;
                    }
                } else {
                    updateOpenKingdeeDataRecord(id, KingdeeConstant.OpenKingdeeDataRecordStatus.SAVE, number);
                    log.warn("Data:{},number:{},提交失败!", JsonUtils.toJson(doMap), number);
                }
            }
        }

        return false;
    }

    private String saveOpenKingdeeDataRecord(String companyId, String data) {
        String id = RandomUtils.bsonId();
        openKingdeeDataRecordDao.save(OpenKingdeeDataRecord.builder()
                .id(id)
                .companyId(companyId)
                .dataValue(data)
                .status(KingdeeConstant.OpenKingdeeDataRecordStatus.INIT)
                .createDate(new Date())
                .build());
        return id;
    }

    private void updateOpenKingdeeDataRecord(String id, Integer Status, String number) {
        Example example = new Example(OpenKingdeeDataRecord.class);
        example.createCriteria().andEqualTo("id", id);
        openKingdeeDataRecordDao.updateByExample(OpenKingdeeDataRecord.builder()
                .id(id)
                .number(number)
                .status(Status)
                .updateDate(new Date())
                .build(), example);
    }

    /**
     * 反射获取监听类
     */
    public KingDeekListener getKingDeekLister(OpenTemplateConfig openTemplateConfig) {
        String className = null;
        if (!ObjectUtils.isEmpty(openTemplateConfig) && !ObjectUtils.isEmpty(openTemplateConfig.getListenerClass())) {
            className = openTemplateConfig.getListenerClass();
        }
        if (!ObjectUtils.isEmpty(className)) {
            Class clazz = null;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (clazz != null) {
                Object bean = SpringUtils.getBean(clazz);
                if (bean != null && bean instanceof KingDeekListener) {
                    return ((KingDeekListener) bean);
                }
            }
        }
        return SpringUtils.getBean(KingDeeDefaultListener.class);
    }

    /**
     * 反射获取监听类
     */
    public KingDeekBillBeforeListener getDataKingDeekLister(OpenTemplateConfig openTemplateConfig) {
        String className = null;
        if (!ObjectUtils.isEmpty(openTemplateConfig) && !ObjectUtils.isEmpty(openTemplateConfig.getListenerClass())) {
            className = openTemplateConfig.getListenerClass();
        }
        if (!ObjectUtils.isEmpty(className)) {
            Class clazz = null;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (clazz != null) {
                Object bean = SpringUtils.getBean(clazz);
                if (bean != null && bean instanceof KingDeekBillBeforeListener) {
                    return ((KingDeekBillBeforeListener) bean);
                }
            }
        }
        return SpringUtils.getBean(KingDeeBillBeforeDefaultListener.class);
    }

    public KingDeeReqConfigDTO getAllReqConfig(String companyId, String moduleType, Integer templateType) {
        OpenKingdeeUrlConfig openKingdeeUrlConfig = openKingdeeUrlConfigDao.getByCompanyId(companyId);
        return KingDeeReqConfigDTO.builder()
                .kingDeekListener(getKingDeekListener(companyId, templateType))
                .openKingdeeReqDataList(openKingdeeReqDataList(companyId, moduleType))
                .openKingdeeUrlConfig(openKingdeeUrlConfig)
                .token(getToken(openKingdeeUrlConfig))
                .build();
    }

    public EmployeeContract getEmployee(String companyId, String employeeId) {
        final String redisKey = MessageFormat.format(JINDIE_EMPLOYEE, companyId + employeeId);
        String employeeStr = (String) redisTemplate.opsForValue().get(redisKey);
        if (!StringUtils.isBlank(employeeStr)) {
            return JsonUtils.toObj(employeeStr, EmployeeContract.class);
        } else {
            EmployeeContract employeeContract = employeeExtService.queryEmployeeInfo(employeeId, companyId);
            if (!ObjectUtils.isEmpty(employeeContract)) {
                redisTemplate.opsForValue().set(redisKey, JsonUtils.toJson(employeeContract), 1, TimeUnit.HOURS);
                return employeeContract;
            }
        }
        return null;
    }

    public ListThirdProjectRespDTO getAllProject(String companyId) {
        final String redisKey = MessageFormat.format(KINGDEE_PROJECT, companyId);
        String redisData = (String) redisTemplate.opsForValue().get(redisKey);
        if (!StringUtils.isBlank(redisData)) {
            return JsonUtils.toObj(redisData, ListThirdProjectRespDTO.class);
        } else {
            // 获取部门配置
            ListThirdProjectRespDTO listThirdProjectRespDTO = openProjectService.getProjectByCompanyId(companyId);
            if (!ObjectUtils.isEmpty(listThirdProjectRespDTO)) {
                redisTemplate.opsForValue().set(redisKey, JsonUtils.toJson(listThirdProjectRespDTO), 1, TimeUnit.HOURS);
                return listThirdProjectRespDTO;
            }
            return null;
        }
    }

    private Integer getKIngdeeAuditConfig(String companyId) {
        final String redisKey = MessageFormat.format(KINGDEE_SETUP, ItemCodeEnum.KINGDEE_AUDIT_CONFIG + "_" + "companyId");
        Integer count = (Integer) redisTemplate.opsForValue().get(redisKey);
        if (!ObjectUtils.isEmpty(count)) {
            return count;
        } else {
            // 获取部门配置
            count = openMsgSetupDao.countByCompanyIdAndItemCode(companyId, ItemCodeEnum.KINGDEE_AUDIT_CONFIG.getCode());
            if (!ObjectUtils.isEmpty(count)) {
                redisTemplate.opsForValue().set(redisKey, count, 1, TimeUnit.HOURS);
            } else {
                redisTemplate.opsForValue().set(redisKey, 0, 1, TimeUnit.HOURS);
                count = 0;
            }
            return count;
        }
    }
}

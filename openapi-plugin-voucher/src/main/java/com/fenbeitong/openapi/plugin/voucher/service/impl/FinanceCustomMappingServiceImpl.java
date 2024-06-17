package com.fenbeitong.openapi.plugin.voucher.service.impl;

import com.fenbeitong.openapi.plugin.voucher.dao.CustomizeVoucherMappingDao;
import com.fenbeitong.openapi.plugin.voucher.entity.CustomizeVoucherMapping;
import com.fenbeitong.openapi.plugin.voucher.service.IFinanceCustomMappingService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

/**
 * <p>Title: FinanceCustomMappingServiceImpl</p>
 * <p>Description: 自定义映射</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/9/30 11:18 AM
 */
@Service
public class FinanceCustomMappingServiceImpl implements IFinanceCustomMappingService {

    @Autowired
    private CustomizeVoucherMappingDao customizeVoucherMappingDao;

    @Override
    public Map<String, Object> loadCustomMapping(String companyId) {
        Map<String, Object> configMap = Maps.newHashMap();
        Map<String, Object> employeeCodeMapping = Maps.newHashMap();
        Map<String, Object> employeeNameMapping = Maps.newHashMap();
        Map<String, Object> deptCodeMapping = Maps.newHashMap();
        Map<String, Object> deptNameMapping = Maps.newHashMap();
        Map<String, Object> projectCodeMapping = Maps.newHashMap();
        Map<String, Object> projectNameMapping = Maps.newHashMap();
        //类型 1:人员;2:部门;3:项目
        int[] types = new int[]{1, 2, 3};
        for (int type : types) {
            List<CustomizeVoucherMapping> mappingList = getCustomizeVoucherMappingList(companyId, type);
            if (ObjectUtils.isEmpty(mappingList)) {
                continue;
            }
            for (CustomizeVoucherMapping mapping : mappingList) {
                String srcCode = mapping.getSrcCode();
                String srcName = mapping.getSrcName();
                String tgtCode = mapping.getTgtCode();
                String tgtName = mapping.getTgtName();
                Map<String, Object> tgtMap = Maps.newHashMap();
                tgtMap.put("code", tgtCode);
                tgtMap.put("name", tgtName);
                if (!ObjectUtils.isEmpty(srcCode)) {
                    if (type == 1) {
                        employeeCodeMapping.put(srcCode, tgtMap);
                    }
                    if (type == 2) {
                        deptCodeMapping.put(srcCode, tgtMap);
                    }
                    if (type == 3) {
                        projectCodeMapping.put(srcCode, tgtMap);
                    }
                }
                if (!ObjectUtils.isEmpty(srcName)) {
                    if (type == 1) {
                        employeeNameMapping.put(srcName, tgtMap);
                    }
                    if (type == 2) {
                        deptNameMapping.put(srcName, tgtMap);
                    }
                    if (type == 3) {
                        projectNameMapping.put(srcName, tgtMap);
                    }
                }
            }
        }
        configMap.put("employeeCodeMapping", employeeCodeMapping);
        configMap.put("employeeNameMapping", employeeNameMapping);
        configMap.put("deptCodeMapping", deptCodeMapping);
        configMap.put("deptNameMapping", deptNameMapping);
        configMap.put("projectCodeMapping", projectCodeMapping);
        configMap.put("projectNameMapping", projectNameMapping);
        return configMap;
    }

    private List<CustomizeVoucherMapping> getCustomizeVoucherMappingList(String companyId, int type) {
        Example example = new Example(CustomizeVoucherMapping.class);
        example.createCriteria().andEqualTo("companyId", companyId).andEqualTo("type", type);
        return customizeVoucherMappingDao.listByExample(example);
    }
}

package com.fenbeitong.openapi.plugin.customize.common.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.fenbeimeta.sdk.enums.common.MetaCategoryTypeEnum;
import com.fenbeitong.fenbeimeta.sdk.enums.common.ObjectNameEnum;
import com.fenbeitong.fenbeimeta.sdk.enums.common.SystemEnum;
import com.fenbeitong.fenbeimeta.sdk.model.dto.BaseOperationDTO;
import com.fenbeitong.fenbeimeta.sdk.model.vo.data.CustomizeVO;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.constant.RedisKeyConstant;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.customize.common.dto.CustomExcelDTO;
import com.fenbeitong.openapi.plugin.customize.common.dto.CustomFieldTranDTO;
import com.fenbeitong.openapi.plugin.customize.common.service.CustomFieldTranService;
import com.fenbeitong.openapi.plugin.support.employee.dao.OpenCustomFieldConfigDao;
import com.fenbeitong.openapi.plugin.support.employee.dto.CustomFieldDTO;
import com.fenbeitong.openapi.plugin.support.employee.entity.OpenCustomFieldConfig;
import com.fenbeitong.openapi.plugin.support.employee.service.EmployeeCustomFieldsService;
import com.fenbeitong.openapi.plugin.support.meta.service.CommonMetaService;
import com.fenbeitong.openapi.plugin.support.util.ExceptionRemind;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.google.common.collect.Lists;
import com.luastar.swift.base.excel.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 自定义字段清洗
 * 将老的expandJson洗到web后台可展示的自定义字段中
 *
 * @author ctl
 * @date 2021/10/29
 */
@Service
@Slf4j
public class CustomFieldTranServiceImpl implements CustomFieldTranService {

    @Autowired
    private OpenCustomFieldConfigDao openCustomFieldConfigDao;

    @Autowired
    private EmployeeCustomFieldsService employeeCustomFieldsService;

    @Autowired
    private ExceptionRemind exceptionRemind;

    @Autowired
    private CommonMetaService commonMetaService;

    @Override
    public long tran(CustomFieldTranDTO dto) {
        long startTime = System.currentTimeMillis();
        String companyId = dto.getCompanyId();
        String url = dto.getUrl();
        if (StringUtils.isBlank(companyId)) {
            throw new OpenApiArgumentException("[companyId]不能为空");
        }
        if (StringUtils.isBlank(url)) {
            throw new OpenApiArgumentException("[url]不能为空");
        }

        // 获取自定义字段配置
        List<OpenCustomFieldConfig> openCustomFieldConfigs = getCustomFieldConfigs(companyId);
        List<String> srcCodeList = openCustomFieldConfigs.stream().map(OpenCustomFieldConfig::getFieldCode).collect(Collectors.toList());
        Map<String, OpenCustomFieldConfig> srcCodeMap = openCustomFieldConfigs.stream().collect(Collectors.toMap(OpenCustomFieldConfig::getFieldCode, v -> v));

        // 读取excel数据
        List<CustomExcelDTO> excelDTOList = getDataFromExcel(url);
        // 按员工id去重
        List<CustomExcelDTO> targetExcelList = excelDTOList.stream().collect(Collectors.collectingAndThen(
            Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(CustomExcelDTO::getEmployeeId))), ArrayList::new
        ));

        // 判断是否存在自定义字段 存在则更新 不存在则创建
        CustomFieldDTO obj = employeeCustomFieldsService.query(companyId);
        if (obj == null || ObjectUtils.isEmpty(obj.getFields())) {
            log.info("企业[{}]没有创建过自定义字段", companyId);

            // 没有创建过自定义字段 直接创建
            createCustomField(companyId, openCustomFieldConfigs);

            // 创建后 再查一下最新的 根据code匹配 excel中的值
            CustomFieldDTO newObj = employeeCustomFieldsService.query(companyId);
            log.info("自定义字段定义创建成功,obj:{}", JsonUtils.toJson(newObj));

            // 匹配赋值 存入元数据
            matchCustomField(targetExcelList, newObj, srcCodeMap, companyId);
        } else {
            // 创建过自定义字段 先比较已有的与需要的是否一致 一致直接赋值 不一致更新定义 并且赋值
            List<String> ucCodeList = obj.getFields().stream().map(CustomFieldDTO.FieldDTO::getFieldCode).collect(Collectors.toList());
            if (obj.getFields().size() == openCustomFieldConfigs.size() && isSameList(srcCodeList, ucCodeList)) {
                log.info("已有的自定义字段与需要的自定义字段完全一致,可以直接赋值,obj:{}", JsonUtils.toJson(obj));
                matchCustomField(targetExcelList, obj, srcCodeMap, companyId);
            } else {
                // 不完全一致 以配置表为准 配置表存在 uc不存在的 创建 配置表不存在的不处理
                updateCustomField(companyId, srcCodeList, srcCodeMap, obj, ucCodeList);
                CustomFieldDTO newObj = employeeCustomFieldsService.query(companyId);
                log.info("自定义字段定义更新成功,obj:{}", JsonUtils.toJson(newObj));
                matchCustomField(targetExcelList, newObj, srcCodeMap, companyId);
            }
        }

        long endTime = System.currentTimeMillis();
        long cost = endTime - startTime;
        log.info("企业[{}]扩展字段======>自定义字段清洗成功,耗时{}s", companyId, (cost) / 1000);
        return cost;
    }

    @Override
    @Async
    public void tranAndNotify(CustomFieldTranDTO dto, String companyName) {
        long cost;
        try {
            cost = this.tran(dto);
        } catch (Exception e) {
            // 失败 发送通知
            String msg = String.format("公司id【%s】\n公司名称【%s】\n自定义字段清洗失败【%s】", dto.getCompanyId(), companyName, e.toString());
            exceptionRemind.remindDingTalk(msg);
            return;
        }
        // 成功 发送通知
        String msg = String.format("公司id【%s】\n公司名称【%s】\n自定义字段清洗成功 耗时【%d】ms", dto.getCompanyId(), companyName, cost);
        exceptionRemind.remindDingTalk(msg);
    }

    /**
     * 更新自定义字段定义
     *
     * @param companyId
     * @param srcCodeList
     * @param srcCodeMap
     * @param obj
     * @param ucCodeList
     */
    private void updateCustomField(String companyId, List<String> srcCodeList, Map<String, OpenCustomFieldConfig> srcCodeMap, CustomFieldDTO obj, List<String> ucCodeList) {
        List<String> newCodeList = srcCodeList.stream().filter(e -> !ucCodeList.contains(e)).collect(Collectors.toList());
        // 更新自定义字段
        for (String code : newCodeList) {
            CustomFieldDTO.FieldDTO fieldDTO = new CustomFieldDTO.FieldDTO();
            fieldDTO.setStatus(srcCodeMap.get(code).getStatus());
            fieldDTO.setFieldName(srcCodeMap.get(code).getFieldName());
            fieldDTO.setFieldCode(code);
            obj.getFields().add(fieldDTO);
        }
        employeeCustomFieldsService.createOrUpdate(obj, companyId);
    }

    /**
     * 创建自定义字段定义
     *
     * @param companyId
     * @param openCustomFieldConfigs
     */
    private void createCustomField(String companyId, List<OpenCustomFieldConfig> openCustomFieldConfigs) {
        CustomFieldDTO customFieldDTO = new CustomFieldDTO();
        List<CustomFieldDTO.FieldDTO> fieldDTOList = new ArrayList<>();
        for (OpenCustomFieldConfig openCustomFieldConfig : openCustomFieldConfigs) {
            CustomFieldDTO.FieldDTO fieldDTO = new CustomFieldDTO.FieldDTO();
            fieldDTO.setFieldCode(openCustomFieldConfig.getFieldCode());
            fieldDTO.setFieldName(openCustomFieldConfig.getFieldName());
            fieldDTO.setStatus(openCustomFieldConfig.getStatus());
            fieldDTOList.add(fieldDTO);
        }
        customFieldDTO.setFields(fieldDTOList);
        employeeCustomFieldsService.createOrUpdate(customFieldDTO, companyId);
    }

    /**
     * 匹配赋值 存入元数据
     *
     * @param excelDTOList
     * @param newObj
     */
    private void matchCustomField(List<CustomExcelDTO> excelDTOList, CustomFieldDTO newObj,
                                  Map<String, OpenCustomFieldConfig> srcCodeMap,
                                  String companyId) {
        // 查出的code和id的映射关系
        Map<String, String> ucCodeIdMap = newObj.getFields().stream().collect(
            Collectors.toMap(CustomFieldDTO.FieldDTO::getFieldCode, CustomFieldDTO.FieldDTO::getFieldId));

        for (int i = 0; i < excelDTOList.size(); i++) {
            List<CustomizeVO> targetList = new ArrayList<>();
            CustomExcelDTO customExcelDTO = excelDTOList.get(i);
            log.info("当前处理第{}条,共{}条", i, excelDTOList.size());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                log.info("线程睡眠被打断");
            }
            String employeeId = customExcelDTO.getEmployeeId();
            String expand = customExcelDTO.getExpand();
            if (StringUtils.isBlank(expand)) {
                throw new FinhubException(0, "读取后的expand为空");
            }
            if (StringUtils.isBlank(employeeId)) {
                throw new FinhubException(0, "读取后的expand为空");
            }
            List<Map<String, String>> expandList = JsonUtils.toObj(expand, new TypeReference<List<Map<String, String>>>() {
            });
            if (ObjectUtils.isEmpty(expandList)) {
                throw new FinhubException(0, "expandList为空");
            }

            // 检查数据
            checkData(companyId, expand);

            Map<String, String> expandMap = expandList.get(0);
            Map<String, String> targetMap = new HashMap<>();
            for (String code : expandMap.keySet()) {
                targetMap.put(code.substring(0, code.length() - 1), expandMap.get(code));
            }
            targetMap.forEach((code, value) -> {
                // 如果数据在配置表中存在 再赋值
                if (srcCodeMap.get(code) != null) {
                    // dataId 对应 fieldId
                    // dataContent 对应 value
                    // employeeId 对应 employeeId
                    // companyId 对应 companyId
                    CustomizeVO customizeVO = new CustomizeVO();
                    customizeVO.setCompanyId(customExcelDTO.getCompanyId())
                        .setEmployeeId(customExcelDTO.getEmployeeId())
                        .setDataId(ucCodeIdMap.get(code))
                        .setDataContent(value);
                    targetList.add(customizeVO);
                }
            });
            commonMetaService.batchUpdate(targetList, ObjectNameEnum.EMPLOYEE_INNER.getKey());
        }
    }

    /**
     * 检查数据
     *
     * @param companyId
     * @param expand
     */
    private void checkData(String companyId, String expand) {
        BaseOperationDTO baseOperationDTO = commonMetaService.getBaseOperation(companyId);
        String apiName = commonMetaService.getApiName(RedisKeyConstant.EXT_CUSTOM_FIELD_TRAN_KEY, companyId, SystemEnum.USERCENTER, MetaCategoryTypeEnum.EMPLOYEE_EXPAND, baseOperationDTO);
        commonMetaService.checkDataInfo(baseOperationDTO, apiName, expand);
    }

    /**
     * 获取自定义字段配置
     *
     * @param companyId
     * @return
     */
    private List<OpenCustomFieldConfig> getCustomFieldConfigs(String companyId) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("companyId", companyId);
        condition.put("status", 1);
        List<OpenCustomFieldConfig> openCustomFieldConfigs =
            openCustomFieldConfigDao.listOpenCustomFieldConfig(condition);

        if (ObjectUtils.isEmpty(openCustomFieldConfigs)) {
            throw new FinhubException(0, "[companyId=" + companyId + "]在自定义字段表配置不存在");
        }
        return openCustomFieldConfigs;
    }

    /**
     * 从excel中获取数据
     *
     * @param url
     * @return
     */
    private List<CustomExcelDTO> getDataFromExcel(String url) {
        // 读取excel中的数据
        List<ImportColumn> columnList = Lists.newArrayList(
            new ImportColumn("company_id", "companyId", ExcelDataType.StringValue),
            new ImportColumn("employee_id", "employeeId", ExcelDataType.StringValue),
            new ImportColumn("expand", "expand", ExcelDataType.StringValue)
        );
        ImportSheet importSheet = new ImportSheet(columnList, CustomExcelDTO.class);
        try {
            URL remoteUrl = new URL(url);
            InputStream resourceAsStream = remoteUrl.openStream();
            ExcelUtils.readBigXlsxExcel(resourceAsStream, importSheet);
        } catch (Exception e) {
            log.error("文件读取异常:{},", e.getLocalizedMessage());
            throw new FinhubException(0, "文件读取失败");
        }
        List<ExcelData> dataList = importSheet.getDataList();
        List<CustomExcelDTO> excelDTOList = new ArrayList<>(dataList.size());
        if (!ObjectUtils.isEmpty(dataList)) {
            for (ExcelData excelData : dataList) {
                CustomExcelDTO customExcelDTO = (CustomExcelDTO) excelData.getData();
                excelDTOList.add(customExcelDTO);
            }
        }

        if (ObjectUtils.isEmpty(excelDTOList)) {
            throw new FinhubException(0, "excel读取到的数据为空");
        }
        return excelDTOList;
    }

    /**
     * 判断list内容是否一致
     *
     * @param srcCodeList
     * @param ucCodeList
     * @return
     */
    private boolean isSameList(List<String> srcCodeList, List<String> ucCodeList) {
        return Arrays.toString(srcCodeList.toArray()).equals(Arrays.toString(ucCodeList.toArray()));
    }

}

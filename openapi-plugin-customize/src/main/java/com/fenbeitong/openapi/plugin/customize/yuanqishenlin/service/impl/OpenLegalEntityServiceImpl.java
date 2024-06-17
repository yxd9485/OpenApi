package com.fenbeitong.openapi.plugin.customize.yuanqishenlin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.common.OpenapiResultEntity;
import com.fenbeitong.openapi.plugin.customize.yuanqishenlin.service.IOpenLegalEntityService;
import com.fenbeitong.openapi.plugin.func.organization.dto.OpenCreateLegalEntityReqDTO;
import com.fenbeitong.openapi.plugin.func.organization.dto.OpenQueryLegalEntityReqDTO;
import com.fenbeitong.openapi.plugin.func.organization.dto.OpenQueryLegalEntityResDTO;
import com.fenbeitong.openapi.plugin.func.organization.dto.OpenUpdateLegalEntityReqDTO;
import com.fenbeitong.openapi.plugin.func.organization.service.FuncLegalEntityService;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenSyncConstant;
import com.fenbeitong.openapi.plugin.support.init.constant.OpenType;
import com.fenbeitong.openapi.plugin.support.organization.dao.OpenLegalEntityDao;
import com.fenbeitong.openapi.plugin.support.organization.dto.OpenLegalEntityResDTO;
import com.fenbeitong.openapi.plugin.support.organization.entity.OpenLegalEntity;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName OpenLegalEntityServiceImpl
 * @Description 法人主体元气林森定制化接口实现类
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/6/13
 **/
@Service
@Slf4j
public class OpenLegalEntityServiceImpl implements IOpenLegalEntityService {

    @Autowired
    private OpenLegalEntityDao openLegalEntityDao;

    @Autowired
    private FuncLegalEntityService funcLegalEntityService;

    @Override
    @Async
    public void syncAllLegal(List<OpenLegalEntity> openLegalEntityList) {

        String companyId = openLegalEntityList.get(0).getCompanyId();
        Integer openType = OpenType.FEISHU_EIA.getType();

        if (ObjectUtils.isEmpty(openLegalEntityList)) {
            return;
        }

        // 判断是否需要初始化
        if (isNeedInit(companyId, openType)) {
            init(companyId, openType);
        }

        log.info("公司:{},openType:{},法人实体开始同步", companyId, openType);
        long start = System.currentTimeMillis();

        // 预处理 过滤掉三方id为空的 并且根据三方id去重
        List<OpenLegalEntity> parsedList = preParse(openLegalEntityList);

        // 对比出 需要绑定 更新 创建 删除 的
        Map<String, List<OpenLegalEntity>> projectMap = parseFunc(companyId, openType, parsedList);

        // 执行操作
        doSync(companyId, projectMap, openType);

        long end = System.currentTimeMillis();
        log.info("公司:{},openType:{},法人实体同步完成，用时{}分钟{}秒...", companyId, openType, (end - start) / 60000L, (end - start) % 60000L / 1000L);
    }

    /**
     * 是否需要初始化中间表
     *
     * @param companyId 公司id
     * @param openType  来源类型
     */
    private boolean isNeedInit(String companyId, Integer openType) {
        return ObjectUtils.isEmpty(openLegalEntityDao.listOpenLegalEntity(companyId, openType));
    }

    /**
     * 初始化中间表
     *
     * @param companyId 公司id
     * @param openType  来源类型
     */
    private void init(String companyId, Integer openType) {
        log.info("公司:{},openType:{}开始执行初始法人主体中间表初始化", companyId, openType);
        long start = System.currentTimeMillis();
        List<OpenCreateLegalEntityReqDTO> legalList = getAllLegalList(companyId);
        List<OpenLegalEntity> insertList = buildInsertList(openType, companyId, legalList);
        if (!ObjectUtils.isEmpty(insertList)) {
            for (OpenLegalEntity legalEntityDTO : insertList) {
                openLegalEntityDao.saveSelective(legalEntityDTO);
            }
        }
        long end = System.currentTimeMillis();
        log.info("公司:{},openType:{},法人主体中间表初始化完成，用时{}分钟{}秒...", companyId, openType, (end - start) / 60000L, (end - start) % 60000L / 1000L);
    }


    /**
     * 预处理 过滤掉三方id为空的 并且根据三方id去重
     *
     * @param openThirdProjectList 法人主体集合
     * @return List<OpenLegalEntity> 法人主体集合
     */
    private List<OpenLegalEntity> preParse(List<OpenLegalEntity> openThirdProjectList) {
        List<OpenLegalEntity> filterList = openThirdProjectList.stream().filter(e -> StringUtils.isNotBlank(e.getThirdId())).collect(Collectors.toList());
        if (ObjectUtils.isEmpty(filterList)) {
            throw new FinhubException(-9999, "三方id不可全部为空");
        }

        // 根据三方ID去重
        return filterList.stream().collect(
            Collectors.collectingAndThen(Collectors.toCollection(
                () -> new TreeSet<>(Comparator.comparing(OpenLegalEntity::getThirdId))
            ), ArrayList::new));

    }

    /**
     * 对比出 需要更新 新增 的
     * <p>
     * 传入的三方name在中间表不存在 需要新增<br/>
     * <p>
     * 传入的三方name在中间表存在且code不存在 需要根据UC的三方id更新其code<br/>
     *
     * <p>
     *
     * @param companyId            公司id
     * @param openType             来源类型
     * @param openThirdProjectList 法人主体集合
     * @return Map<String, List < OpenLegalEntity>> 法人主体map
     */
    private Map<String, List<OpenLegalEntity>> parseFunc(String companyId, Integer openType, List<OpenLegalEntity> openThirdProjectList) {
        Map<String, List<OpenLegalEntity>> legalMap = Maps.newHashMap();
        List<OpenLegalEntity> needAddList = Lists.newArrayList();
        List<OpenLegalEntity> needUpdateList = Lists.newArrayList();
        // 获取中间表中的全量数据
        List<OpenLegalEntity> srcOpenThirdProjects = openLegalEntityDao.listOpenLegalEntity(companyId, openType);
        if (ObjectUtils.isEmpty(srcOpenThirdProjects)) {
            // 初始化后还是空 则全部新增
            needAddList.addAll(openThirdProjectList);
        } else {
            //中间表数据code集合
            List<String> srcThirdCodes = srcOpenThirdProjects.stream().map(OpenLegalEntity::getCode).collect(Collectors.toList());
            //中间表数据name集合
            List<String> srcNames = srcOpenThirdProjects.stream().map(OpenLegalEntity::getName).collect(Collectors.toList());

            List<OpenLegalEntity> addList = getAddList(srcNames, openThirdProjectList);
            if (!ObjectUtils.isEmpty(addList)) {
                needAddList.addAll(addList);
            }

            List<OpenLegalEntity> updateList = getUpdateList(srcThirdCodes, srcNames, openThirdProjectList, srcOpenThirdProjects);
            if (!ObjectUtils.isEmpty(updateList)) {
                needUpdateList.addAll(updateList);
            }

        }
        legalMap.put("insert", needAddList);
        legalMap.put("update", needUpdateList);
        return legalMap;
    }

    /**
     * 执行同步操作
     *
     * @param companyId  公司id
     * @param projectMap 需要操作的map
     * @param openType   来源类型
     */
    private void doSync(String companyId, Map<String, List<OpenLegalEntity>> projectMap, Integer openType) {
        if (ObjectUtils.isEmpty(projectMap)) {
            return;
        }
        // 绑定
        doBind(companyId, projectMap.get(OpenSyncConstant.BIND));
        // 新增
        doAdd(companyId, projectMap.get(OpenSyncConstant.INSERT), openType);
        // 更新
        doUpdate(companyId, projectMap.get(OpenSyncConstant.UPDATE), openType);
        // 删除
        doDel(companyId, projectMap.get(OpenSyncConstant.DELETE));
    }

    /**
     * 获取需要新增的
     * <p>
     * 中间表name不存在的 需要新增
     *
     * @param srcNames            中间表法人主体名称集合
     * @param openLegalEntityList 法人主体集合
     * @return List<OpenLegalEntity> 过滤好的法人主体集合
     */
    private List<OpenLegalEntity> getAddList(List<String> srcNames, List<OpenLegalEntity> openLegalEntityList) {
        return openLegalEntityList.stream().filter(e -> !srcNames.contains(e.getName())).collect(Collectors.toList());
    }

    /**
     * 获取需要绑定的
     * <p>
     *
     * @param codes               中间表法人主体编码集合
     * @param srcThirdIds         中间表法人主体id集合
     * @param openLegalEntityList 法人主体集合
     * @return List<OpenLegalEntity> 需要绑定的法人主体集合
     */
    private List<OpenLegalEntity> getBindList(List<String> codes, List<String> srcThirdIds, List<OpenLegalEntity> openLegalEntityList) {
        return null;
    }

    /**
     * 获取需要删除的
     *
     * @param srcOpenThirdProjects 法人主体集合
     * @param thirdIds             三方法人主体id集合
     * @return List<OpenLegalEntity> 需要删除的法人主体集合
     */
    private List<OpenLegalEntity> getDeleteList(List<OpenLegalEntity> srcOpenThirdProjects, List<String> thirdIds) {
        return null;
    }

    /**
     * 获取需要更新的
     * name存在且code不存在的
     *
     * @param srcCodes            中间表法人主体编码集合
     * @param srcNames            中间表法人主体名称集合
     * @param openLegalEntityList 法人主体集合
     * @return List<OpenLegalEntity> 需要更新的法人主体集合
     */
    private List<OpenLegalEntity> getUpdateList(List<String> srcCodes, List<String> srcNames, List<OpenLegalEntity> openLegalEntityList, List<OpenLegalEntity> srcLegalEntityList) {
        List<OpenLegalEntity> updateListBefore = openLegalEntityList.stream().filter(e -> srcNames.contains(e.getName()) && !srcCodes.contains(e.getCode())).collect(Collectors.toList());
        List<OpenLegalEntity> updateList = Lists.newArrayList();
        Map<String, OpenLegalEntity> srcLegalEntityMap = srcLegalEntityList.stream().collect(Collectors.toMap(OpenLegalEntity::getName, v -> v));
        //将中间表的third_id放到传参中，UC根据third_id更新法人实体
        for (OpenLegalEntity legalEntityDTO : updateListBefore) {
            OpenLegalEntity updateEntity = new OpenLegalEntity();
            BeanUtils.copyProperties(legalEntityDTO, updateEntity);
            OpenLegalEntity srcLegal = srcLegalEntityMap.get(legalEntityDTO.getName());
            updateEntity.setThirdId(srcLegal.getThirdId());
            updateList.add(updateEntity);
        }
        return updateList;
    }


    /**
     * 获取全量法人主体
     *
     * @param companyId 公司id
     * @return List<OpenCreateLegalEntityReqDTO> 法人主体集合
     */
    private List<OpenCreateLegalEntityReqDTO> getAllLegalList(String companyId) {
        List<OpenCreateLegalEntityReqDTO> legalList = Lists.newArrayList();
        int pageIndex = 1;
        OpenQueryLegalEntityReqDTO legalEntityReqDTO = new OpenQueryLegalEntityReqDTO();
        legalEntityReqDTO.setPageIndex(pageIndex);
        legalEntityReqDTO.setPageSize(200);
        OpenapiResultEntity<OpenQueryLegalEntityResDTO> dataList = (OpenapiResultEntity<OpenQueryLegalEntityResDTO>) funcLegalEntityService.listLegalEntities(companyId, legalEntityReqDTO);
        while (!ObjectUtils.isEmpty(dataList.getData())) {
            legalList.addAll((dataList.getData()).getEntities());
            legalEntityReqDTO.setPageIndex(++pageIndex);
            dataList = (OpenapiResultEntity<OpenQueryLegalEntityResDTO>) funcLegalEntityService.listLegalEntities(companyId, legalEntityReqDTO);
        }

        return legalList;
    }

    /**
     * 构建中间表实体
     *
     * @param openType  来源类型
     * @param companyId 公司id
     * @param legalList 法人主体集合
     * @return List<OpenLegalEntity> 构造好的法人主体集合
     */
    private List<OpenLegalEntity> buildInsertList(Integer openType, String companyId, List<OpenCreateLegalEntityReqDTO> legalList) {
        List<OpenLegalEntity> insertList = Lists.newArrayList();

        if (!CollectionUtils.isEmpty(legalList)) {
            //初始化数据时默认三方法人实体id为空
            for (OpenCreateLegalEntityReqDTO source : legalList) {
                OpenLegalEntity target = OpenLegalEntity.builder().id(UUID.randomUUID().getMostSignificantBits())
                    .thirdId(source.getThirdId())
                    .companyId(companyId)
                    .openType(openType).name(source.getName()).identificationNumber(source.getIdentificationNumber()).type(source.getType())
                    .thirdParentId(source.getThirdParentId()).state(source.getState()).phone(source.getPhone()).code(source.getCode())
                    .bankName(source.getBankName()).bankCode(source.getBankCode()).address(source.getAddress())
                    .createTime(new Date()).updateTime(new Date()).build();

                insertList.add(target);
            }
        }
        return insertList;
    }

    /**
     * 执行绑定操作
     *
     * @param companyId         公司id
     * @param openThirdProjects 法人实体集合
     */
    private void doBind(String companyId, List<OpenLegalEntity> openThirdProjects) {

    }

    /**
     * 执行新增操作
     *
     * @param companyId           公司id
     * @param openLegalEntityList 法人实体集合
     */
    private void doAdd(String companyId, List<OpenLegalEntity> openLegalEntityList, Integer openType) {
        if (ObjectUtils.isEmpty(openLegalEntityList)) {
            return;
        }
        OpenapiResultEntity res = null;

        try {
            List<OpenCreateLegalEntityReqDTO> legalEntityList = buildAddList(openLegalEntityList);
            res = (OpenapiResultEntity) funcLegalEntityService.batchCreateLegalEntity(companyId, legalEntityList);
            Thread.sleep(500);
        } catch (Exception e) {
            log.warn("companyId:{},项目法人主体失败,法人主体列表:{},e:{}", companyId, JsonUtils.toJson(openLegalEntityList), e);
        }

        //需要新增的法人主体
        List<OpenLegalEntity> needInsertList = Lists.newArrayList();

        if (res.getCode() == 0) {
            needInsertList.addAll(openLegalEntityList);
        } else {
            //UC新增失败的法人主体信息
            List<OpenLegalEntityResDTO.LegalEntityResDTO> resDTOList = (List<OpenLegalEntityResDTO.LegalEntityResDTO>) res.getData();
            //UC新增成功的法人主体集合
            List<OpenLegalEntity> openLegalEntitys = Lists.newArrayList();
            //UC新增失败且表里名称存在的法人主体集合
            List<OpenLegalEntity> legalEntitys = Lists.newArrayList();
            //如果有新增失败的，需要将其过滤掉
            if (res.getCode() != 0 && !CollectionUtils.isEmpty(resDTOList)) {
                List<String> errorThirds = resDTOList.stream().map(OpenLegalEntityResDTO.LegalEntityResDTO::getThirdId).collect(Collectors.toList());
                openLegalEntitys = openLegalEntityList.stream().filter(d -> !errorThirds.contains(d.getThirdId())).collect(Collectors.toList());
                //将新增失败且UC存在的法人实体入中间表并绑定
                legalEntitys = bindEntity(companyId, errorThirds, openLegalEntityList, openType);
            }
            if (!CollectionUtils.isEmpty(openLegalEntitys)) {
                needInsertList.addAll(openLegalEntitys);
            }
            if (!CollectionUtils.isEmpty(legalEntitys)) {
                needInsertList.addAll(legalEntitys);
            }
        }

        //保存法人主体到中间表
        if (!CollectionUtils.isEmpty(needInsertList)) {
            for (OpenLegalEntity legalEntityDTO : needInsertList) {
                try {
                    legalEntityDTO.setId(UUID.randomUUID().getMostSignificantBits());
                    legalEntityDTO.setCreateTime(new Date());
                    legalEntityDTO.setUpdateTime(new Date());
                    legalEntityDTO.setOpenType(openType);
                    legalEntityDTO.setCompanyId(companyId);
                    openLegalEntityDao.saveSelective(legalEntityDTO);
                } catch (Exception e) {
                    log.warn("法人主体新增集合入库失败:{},e:{}", JsonUtils.toJson(openLegalEntityList), e);
                }
            }
        }

    }

    /**
     * 执行更新操作
     *
     * @param companyId           公司id
     * @param openLegalEntityList 法人主体集合
     * @param openType            来源类型
     */
    private void doUpdate(String companyId, List<OpenLegalEntity> openLegalEntityList, Integer openType) {
        if (ObjectUtils.isEmpty(openLegalEntityList)) {
            return;
        }

        OpenapiResultEntity res = null;
        try {
            List<OpenUpdateLegalEntityReqDTO> legalEntityList = buildUpdateList(openLegalEntityList);
            res = (OpenapiResultEntity) funcLegalEntityService.batchUpdateLegalEntity(companyId, legalEntityList);
            Thread.sleep(500);
        } catch (Exception e) {
            log.warn("companyId:{},法人主体更新失败,法人主体列表:{},e:{}", companyId, JsonUtils.toJson(openLegalEntityList), e);
        }

        //中间表待修改的法人主体集合
        List<OpenLegalEntity> needUpdateList = Lists.newArrayList();
        if (res.getCode() == 0) {
            needUpdateList.addAll(openLegalEntityList);
        } else {
            //UC修改失败的法人主体信息
            List<OpenLegalEntityResDTO.LegalEntityResDTO> resDTOList = (List<OpenLegalEntityResDTO.LegalEntityResDTO>) res.getData();
            //修改成功的法人主体集合
            List<OpenLegalEntity> OpenLegalEntityS = Lists.newArrayList();
            //如果有修改失败的，需要将其过滤掉
            if (res.getCode() != 0 && !CollectionUtils.isEmpty(resDTOList)) {
                List<String> errorThirds = resDTOList.stream().map(OpenLegalEntityResDTO.LegalEntityResDTO::getThirdId).collect(Collectors.toList());
                OpenLegalEntityS = openLegalEntityList.stream().filter(d -> !errorThirds.contains(d.getThirdId())).collect(Collectors.toList());
            }
            if (!CollectionUtils.isEmpty(OpenLegalEntityS)) {
                needUpdateList.addAll(OpenLegalEntityS);
            }
        }

        //更新法人主体到中间表
        if (!CollectionUtils.isEmpty(needUpdateList)) {
            for (OpenLegalEntity legalEntityDTO : needUpdateList) {
                try {
                    openLegalEntityDao.updateByCompanyIdAndThirdIdAndOpenType(legalEntityDTO, companyId, legalEntityDTO.getCode(), openType);
                } catch (Exception e) {
                    log.warn("法人主体删除集合入库失败:{},e:{}", JsonUtils.toJson(needUpdateList), e);
                }
            }
        }
    }

    /**
     * 执行删除操作
     *
     * @param companyId           公司id
     * @param OpenLegalEntityList 法人主体集合
     */
    private void doDel(String companyId, List<OpenLegalEntity> OpenLegalEntityList) {

    }

    /**
     * 判断是否需要更新 null不操作
     *
     * @param srcLegalMap     中间表数据map集合
     * @param openLegalEntity 法人主体集合
     */
    private boolean isNeedUpdate(Map<String, OpenLegalEntity> srcLegalMap, OpenLegalEntity openLegalEntity) {
        boolean needUpdate = false;

        OpenLegalEntity srcLegal = srcLegalMap.get(openLegalEntity.getCode());

        if (!ObjectUtils.isEmpty(srcLegal)) {
            if (!ObjectUtils.isEmpty(openLegalEntity.getName()) && !openLegalEntity.getName().equals(srcLegal.getName())) {
                needUpdate = true;
            }
            if (!ObjectUtils.isEmpty(openLegalEntity.getThirdId()) && !openLegalEntity.getThirdId().equals(srcLegal.getThirdId())) {
                needUpdate = true;
            }
        }
        return needUpdate;
    }

    /**
     * 构建新增法人主体请求体
     *
     * @param openLegalEntityList 法人主体集合
     * @return List<OpenCreateLegalEntityReqDTO> 构建好的法人主体集合
     */
    private List<OpenCreateLegalEntityReqDTO> buildAddList(List<OpenLegalEntity> openLegalEntityList) {
        List<OpenCreateLegalEntityReqDTO> legalEntityReqDTOList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(openLegalEntityList)) {
            for (OpenLegalEntity entityDTO : openLegalEntityList) {
                legalEntityReqDTOList.add(OpenCreateLegalEntityReqDTO.builder().thirdId(entityDTO.getThirdId()).name(entityDTO.getName()).code(entityDTO.getCode()).identificationNumber(entityDTO.getIdentificationNumber()).type(1).state(1).build());
            }
        }
        return legalEntityReqDTOList;
    }

    /**
     * 构建更新法人主体请求体
     *
     * @param openLegalEntityList 法人主体集合
     * @return List<OpenUpdateLegalEntityReqDTO> 构建好的法人主体集合
     */
    private List<OpenUpdateLegalEntityReqDTO> buildUpdateList(List<OpenLegalEntity> openLegalEntityList) {
        List<OpenUpdateLegalEntityReqDTO> legalEntityReqDTOList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(openLegalEntityList)) {
            for (OpenLegalEntity entityDTO : openLegalEntityList) {
                legalEntityReqDTOList.add(OpenUpdateLegalEntityReqDTO.builder().thirdId(entityDTO.getThirdId()).name(entityDTO.getName()).code(entityDTO.getCode()).identificationNumber(entityDTO.getIdentificationNumber()).type(1).build());
            }
        }
        return legalEntityReqDTOList;
    }

    /**
     * 将新增失败且UC存在的法人实体入中间表
     */
    private List<OpenLegalEntity> bindEntity(String companyId, List<String> errorThirds, List<OpenLegalEntity> OpenLegalEntityList, Integer openType) {
        List<OpenLegalEntity> insertList = Lists.newArrayList();
        Map<String, OpenLegalEntity> legalEntityDTOMap = OpenLegalEntityList.stream().collect(Collectors.toMap(OpenLegalEntity::getThirdId, v -> v));
        //获取UC全量法人主体
        List<OpenCreateLegalEntityReqDTO> legalList = getAllLegalList(companyId);
        List<String> nameList = Lists.newArrayList();
        for (String errorId : errorThirds) {
            OpenLegalEntity legalEntityDTO = legalEntityDTOMap.get(errorId);
            nameList.add(legalEntityDTO.getName());
        }
        //过滤出名称在新增失败集合的法人主体
        List<OpenCreateLegalEntityReqDTO> maybeInsertList = legalList.stream().filter(l -> nameList.contains(l.getName())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(maybeInsertList)) {
            insertList.addAll(buildInsertList(openType, companyId, maybeInsertList));
        }
        return insertList;
    }

}

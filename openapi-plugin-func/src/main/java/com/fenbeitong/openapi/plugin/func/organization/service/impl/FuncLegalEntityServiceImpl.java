package com.fenbeitong.openapi.plugin.func.organization.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fenbeitong.openapi.plugin.core.common.OpenapiResponseUtils;
import com.fenbeitong.openapi.plugin.core.common.OpenapiResultEntity;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.organization.dto.OpenCreateLegalEntityReqDTO;
import com.fenbeitong.openapi.plugin.func.organization.dto.OpenQueryLegalEntityReqDTO;
import com.fenbeitong.openapi.plugin.func.organization.dto.OpenQueryLegalEntityResDTO;
import com.fenbeitong.openapi.plugin.func.organization.dto.OpenUpdateLegalEntityReqDTO;
import com.fenbeitong.openapi.plugin.func.organization.service.FuncLegalEntityService;
import com.fenbeitong.openapi.plugin.support.organization.dto.OpenLegalEntityResDTO;
import com.fenbeitong.openapi.plugin.support.organization.service.ILegalEntityService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.usercenter.api.model.dto.common.CommonBatchOpResultDTO;
import com.fenbeitong.usercenter.api.model.dto.common.CommonPageResDTO;
import com.fenbeitong.usercenter.api.model.dto.company.BatchModifyRpcDTO;
import com.fenbeitong.usercenter.api.model.dto.company.CompanyLegalEntityRpcDTO;
import com.fenbeitong.usercenter.api.model.dto.company.CompanyLegalEntityWriteRpcDTO;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName FuncLegalEntityServiceImpl
 * @Description 公司法人主体相关接口
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/4/13 下午7:18
 **/
@ServiceAspect
@Service
@Slf4j
public class FuncLegalEntityServiceImpl implements FuncLegalEntityService {

    @Autowired
    private ILegalEntityService legalEntityService;
    /**
     * @param companyId       公司id
     * @param legalEntityList 创建法人主体信息入参
     * @return OpenapiResultEntity 创建法人主体失败返回结果集
     * @author helu
     * @date 2022/4/18 下午5:06
     */
    @Override
    public OpenapiResultEntity batchCreateLegalEntity(String companyId, List<OpenCreateLegalEntityReqDTO> legalEntityList) throws BindException {
        //校验参数
        if (ObjectUtils.isEmpty(legalEntityList)) {
            log.info("法人主体信息为空");
            throw new OpenApiArgumentException("法人主体信息为空,请检查参数信息");
        }
        for(OpenCreateLegalEntityReqDTO legalEntity:legalEntityList){
            if(StringUtils.isEmpty(legalEntity.getName())){
                log.info("法人主体名称为空");
                throw new OpenApiArgumentException("法人主体名称为空,请检查参数信息");
            }
            if(StringUtils.isEmpty(legalEntity.getIdentificationNumber())){
                log.info("纳税人识别号为空");
                throw new OpenApiArgumentException("纳税人识别号为空,请检查参数信息");
            }
            if(StringUtils.isEmpty(legalEntity.getType())){
                log.info("类型为空");
                throw new OpenApiArgumentException("类型为空,请检查参数信息");
            }
        }
        BatchModifyRpcDTO<CompanyLegalEntityWriteRpcDTO> batchLegalEntityDTO = new BatchModifyRpcDTO<CompanyLegalEntityWriteRpcDTO>();
        batchLegalEntityDTO.setCompanyId(companyId);
        batchLegalEntityDTO.setList(checkAndBuildLegalEntity(legalEntityList));
        CommonBatchOpResultDTO openCreateLegalEntityUcRes = legalEntityService.batchCreateLegalEntity(batchLegalEntityDTO);
        return buildLegalEntityResult(openCreateLegalEntityUcRes);
    }

    /**
     * @param companyId       公司id
     * @param legalEntityList 更新法人主体信息集
     * @return OpenapiResultEntity  更新法人主体失败返回结果集
     * @author helu
     * @date 2022/4/18 下午5:07
     */
    @Override
    public OpenapiResultEntity batchUpdateLegalEntity(String companyId, List<OpenUpdateLegalEntityReqDTO> legalEntityList) throws BindException {
        //校验参数
        ValidatorUtils.validateBySpring(legalEntityList);

        BatchModifyRpcDTO<CompanyLegalEntityWriteRpcDTO> batchLegalEntityDTO = new BatchModifyRpcDTO<CompanyLegalEntityWriteRpcDTO>();
        batchLegalEntityDTO.setCompanyId(companyId);
        //转换参数，公用参数构建
        List<OpenCreateLegalEntityReqDTO> updateReqList = JsonUtils.toObj(JsonUtils.toJson(legalEntityList), new TypeReference<List<OpenCreateLegalEntityReqDTO>>() {
        });
        batchLegalEntityDTO.setList(checkAndBuildLegalEntity(updateReqList));
        CommonBatchOpResultDTO openUpdateLegalEntityUcRes = legalEntityService.batchUpdateLegalEntity(batchLegalEntityDTO);
        return buildLegalEntityResult(openUpdateLegalEntityUcRes);
    }

    /**
     * @param companyId      公司id
     * @param legalEntityIds 删除法人主体的三方id
     * @return OpenapiResultEntity 删除法人主体失败返回结果集
     * @author helu
     * @date 2022/4/18 下午5:08
     */
    @Override
    public OpenapiResultEntity deleteLegalEntity(String companyId, List<String> legalEntityIds) {
        if (ObjectUtils.isEmpty(legalEntityIds)) {
            log.info("删除法人实体参数为空");
            throw new OpenApiArgumentException("删除法人实体的三方系统id【third_id】必填");
        }
        CommonBatchOpResultDTO deleteUcRes = legalEntityService.batchDeleteLegalEntity(companyId, legalEntityIds);
        return buildLegalEntityResult(deleteUcRes);
    }

    /**
     * @param companyId         公司id
     * @param legalEntityReqDTO 分页查询参数
     * @return List<OpenCreateLegalEntityReqDTO> 查询法人主体列表集
     * @author helu
     * @date 2022/4/18 下午5:09
     */
    @Override
    public OpenapiResultEntity listLegalEntities(String companyId, OpenQueryLegalEntityReqDTO legalEntityReqDTO) {
        CommonPageResDTO<CompanyLegalEntityRpcDTO> companyLegalEntityRpcVos = legalEntityService.queryLegalEntityList(companyId, legalEntityReqDTO.getPageIndex(), legalEntityReqDTO.getPageSize());
        //返回结果集数据转换
        List<OpenCreateLegalEntityReqDTO> openCreateLegalEntityList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(companyLegalEntityRpcVos.getEntities())) {
            companyLegalEntityRpcVos.getEntities().stream().forEach(l -> {
                OpenCreateLegalEntityReqDTO legalEntityReqDTOS = new OpenCreateLegalEntityReqDTO();
                legalEntityReqDTOS.setThirdId(l.getThirdId());
                legalEntityReqDTOS.setName(l.getEntityName());
                legalEntityReqDTOS.setCode(l.getBusinessCode());
                legalEntityReqDTOS.setThirdParentId(l.getThirdParentLegalEntityId());
                legalEntityReqDTOS.setIdentificationNumber(l.getTaxpayerNumber());
                legalEntityReqDTOS.setType(l.getTaxpayerType());
                legalEntityReqDTOS.setBankName(l.getOpenBank());
                legalEntityReqDTOS.setBankCode(l.getBankAccount());
                legalEntityReqDTOS.setAddress(l.getRegisteredAddress());
                legalEntityReqDTOS.setPhone(l.getFixedTelephone());
                legalEntityReqDTOS.setState(l.getState());
                openCreateLegalEntityList.add(legalEntityReqDTOS);
            });
            OpenQueryLegalEntityResDTO result = new OpenQueryLegalEntityResDTO();
            BeanUtils.copyProperties(companyLegalEntityRpcVos,result);
            result.setEntities(openCreateLegalEntityList);
            return OpenapiResponseUtils.success(result);
        }
        return OpenapiResponseUtils.success(Maps.newHashMap());
    }


    /**
     * 参数信息转换
     *
     * @param legalEntityList 用户传入的法人主体参数信息
     * @return List<CompanyLegalEntityWriteRpcDTO> uc需要的法人主体参数信息
     * @author helu
     * @date 2022/4/18 下午5:09
     */
    List<CompanyLegalEntityWriteRpcDTO> checkAndBuildLegalEntity(List<OpenCreateLegalEntityReqDTO> legalEntityList) throws BindException {

        List<CompanyLegalEntityWriteRpcDTO> legalEntityRpcList = new ArrayList<CompanyLegalEntityWriteRpcDTO>();
        if (ObjectUtils.isEmpty(legalEntityList)) {
            log.info("法人主体信息为空");
            throw new OpenApiArgumentException("法人主体信息为空,请检查参数信息");
        }
        List<String> legalEntityReqDTOList = new ArrayList<>();
        for(OpenCreateLegalEntityReqDTO legalEntity:legalEntityList){
            if(ObjectUtils.isEmpty(legalEntity.getThirdId())){
                log.info("法人主体id为空");
                throw new OpenApiArgumentException("法人主体id为空");
            }
            if(legalEntityReqDTOList.contains(legalEntity.getThirdId())){
                //存在重复元素
                log.info("法人主体id存在重复元素");
                throw new OpenApiArgumentException("法人主体id存在重复元素");
            }
            legalEntityReqDTOList.add(legalEntity.getThirdId());
        }

        //构建uc法人主体入参
        legalEntityList.forEach(l -> {
            CompanyLegalEntityWriteRpcDTO legalEntityWriteRpcDTO = new CompanyLegalEntityWriteRpcDTO();
            BeanUtils.copyProperties(l, legalEntityWriteRpcDTO);
            legalEntityWriteRpcDTO.setThirdLegalEntityId(l.getThirdId());
            legalEntityWriteRpcDTO.setLegalEntityName(l.getName());
            legalEntityWriteRpcDTO.setBusinessCode(l.getCode());
            legalEntityWriteRpcDTO.setTaxpayerNumber(l.getIdentificationNumber());
            legalEntityWriteRpcDTO.setThirdParentLegalEntityId(l.getThirdParentId());
            legalEntityWriteRpcDTO.setTaxpayerType(l.getType());
            legalEntityWriteRpcDTO.setOpenBank(l.getBankName());
            legalEntityWriteRpcDTO.setBankAccount(l.getBankCode());
            legalEntityWriteRpcDTO.setRegisteredAddress(l.getAddress());
            legalEntityWriteRpcDTO.setFixedTelephone(l.getPhone());
            legalEntityWriteRpcDTO.setState(l.getState());
            legalEntityRpcList.add(legalEntityWriteRpcDTO);
        });
        return legalEntityRpcList;
    }


    /**
     * 对全部失败、部分失败、全部成功情况做错误码和提示信息划分
     *
     * @param result uc法人主体相关接口返回结果集
     * @return OpenapiResultEntity
     * @author helu
     * @date 2022/4/18 下午5:11
     */
    OpenapiResultEntity buildLegalEntityResult(CommonBatchOpResultDTO result) {

        List<OpenLegalEntityResDTO.LegalEntityResDTO> legalEntityRes = new ArrayList<>();
        OpenLegalEntityResDTO resDTO = new OpenLegalEntityResDTO();
        resDTO.setSuccessCount(result.getSucceededCount());
        resDTO.setFailCount(result.getFailedCount());
        if (result.getFailedCount() > 0) {
            result.getDetails().stream().forEach(l -> {
                OpenLegalEntityResDTO.LegalEntityResDTO detail = OpenLegalEntityResDTO.LegalEntityResDTO.builder().errorMsg(l.getDescription()).thirdId(l.getId()).build();
                legalEntityRes.add(detail);
            });
            resDTO.setErrorDetail(legalEntityRes);
        }
        if (resDTO.getFailCount() == 0) {
            //全部成功
            return OpenapiResponseUtils.success(Maps.newHashMap());
        }
        if (resDTO.getSuccessCount() == 0) {
            //全部失败
            return OpenapiResponseUtils.error(-9999, "操作法人实体信息全部失败", resDTO.getErrorDetail());
        }
        //部分失败，部分成功
        return OpenapiResponseUtils.error(1, "操作新增法人实体信息部分成功", resDTO.getErrorDetail());
    }
}

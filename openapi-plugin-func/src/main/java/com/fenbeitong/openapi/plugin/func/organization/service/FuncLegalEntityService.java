package com.fenbeitong.openapi.plugin.func.organization.service;

import com.fenbeitong.openapi.plugin.func.organization.dto.OpenCreateLegalEntityReqDTO;
import com.fenbeitong.openapi.plugin.func.organization.dto.OpenQueryLegalEntityReqDTO;
import com.fenbeitong.openapi.plugin.func.organization.dto.OpenUpdateLegalEntityReqDTO;
import org.springframework.validation.BindException;

import java.util.List;

/**
 * @ClassName FuncLegalEntityService
 * @Description 公司法人主体相关接口
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/4/13 下午7:17
 **/

public interface FuncLegalEntityService {

    /**
     * @author helu
     * @date 2022/4/15 下午5:49
     * @param companyId
     * @param legalEntityList
     * @return Object
     */
   Object batchCreateLegalEntity(String companyId,List<OpenCreateLegalEntityReqDTO> legalEntityList) throws BindException;

    /**
     * 批量更新法人主体信息
     */
    Object batchUpdateLegalEntity(String companyId,List<OpenUpdateLegalEntityReqDTO> legalEntityList) throws BindException;

    /**
     * 删除法人主体信息
     */
   Object deleteLegalEntity(String companyId,List<String> legalEntityIds);

    /**
     * 批量查询法人详情信息
     */
    Object listLegalEntities(String companyId, OpenQueryLegalEntityReqDTO legalEntityReqDTO);

}

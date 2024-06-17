package com.fenbeitong.openapi.plugin.func.archive.service;

import com.fenbeitong.openapi.plugin.func.archive.dto.ArchiveItemResDTO;
import com.fenbeitong.openapi.plugin.func.archive.dto.DeleteArchiveItemReqDTO;
import com.fenbeitong.openapi.plugin.func.archive.dto.UpdateArchiveItemReqDTO;
import org.springframework.validation.BindException;

import java.util.List;
import java.util.Map;

/**
 * @ClassName FuncArchiveService
 * @Description 自定义档案
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/10/31 下午2:11
 **/

public interface FuncArchiveService {
    //新增或更新档案项目
     List<ArchiveItemResDTO> createOrUpdateArchiveItem(UpdateArchiveItemReqDTO updateArchiveItemReqDTO, String companyId) throws BindException;
    //批量删除项目
     void deleteArchiveItem(DeleteArchiveItemReqDTO deleteArchiveItemReqDTO, String companyId);
    //查询档案项目列表


}

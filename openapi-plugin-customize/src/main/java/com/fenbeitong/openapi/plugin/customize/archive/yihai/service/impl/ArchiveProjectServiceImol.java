package com.fenbeitong.openapi.plugin.customize.archive.yihai.service.impl;

import com.fenbeitong.openapi.plugin.core.exception.OpenApiBindException;
import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.customize.archive.yihai.dto.YiHaiArchiveProjectDTO;
import com.fenbeitong.openapi.plugin.customize.archive.yihai.dto.YiHaiConfigDTO;
import com.fenbeitong.openapi.plugin.customize.archive.yihai.dto.YiHaiReqDTO;
import com.fenbeitong.openapi.plugin.customize.archive.yihai.service.ArchiveProjectService;
import com.fenbeitong.openapi.plugin.support.archive.dto.OpenThirdArchiveProjectDTO;
import com.fenbeitong.openapi.plugin.support.archive.service.OpenThirdArchiveProjectService;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Title: ArchiveProjectSyncService</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021-05-17 10:34
 */
@Slf4j
@ServiceAspect
@Service
public class ArchiveProjectServiceImol extends AbstractCommon implements ArchiveProjectService {

    @Autowired
    OpenThirdArchiveProjectService openThirdArchiveProjectService;

    @Override
    public String ArchiveProjectSyncAll(YiHaiConfigDTO yiHaiConfigDTO) {
        checkParams(yiHaiConfigDTO);
        YiHaiReqDTO yiHaiReqDTO = new YiHaiReqDTO();
        yiHaiReqDTO.setLOGIC_SYS("FBT");
        YiHaiArchiveProjectDTO yiHaiArchiveProjectDTO = JsonUtils.toObj(getData(yiHaiConfigDTO, JsonUtils.toObj(JsonUtils.toJson(yiHaiReqDTO), Map.class)), YiHaiArchiveProjectDTO.class);
        if (!ObjectUtils.isEmpty(yiHaiArchiveProjectDTO) && "S".equals(yiHaiArchiveProjectDTO.getSTATUS()) && !ObjectUtils.isEmpty(yiHaiArchiveProjectDTO.getITEMS())) {
            yiHaiArchiveProjectDTO.setITEMS(yiHaiArchiveProjectDTO.getITEMS().stream().filter(t -> !"X".equals(t.getBKZKP())).collect(Collectors.toList()));
            List<OpenThirdArchiveProjectDTO> openThirdArchiveProjectDTOS = process(yiHaiArchiveProjectDTO, yiHaiConfigDTO);
            openThirdArchiveProjectService.archiveProjectSync(yiHaiConfigDTO.getCompanyId(), false, null, yiHaiConfigDTO.getThirdArchiveId(), openThirdArchiveProjectDTOS);
        }
        return "success";
    }

    @Override
    public String ArchiveProjectSyncPart(YiHaiConfigDTO yiHaiConfigDTO) {
        checkParams(yiHaiConfigDTO);
        YiHaiReqDTO yiHaiReqDTO = new YiHaiReqDTO();
        yiHaiReqDTO.setLOGIC_SYS("FBT");
        yiHaiReqDTO.setUDATE(DateUtils.toStr(DateUtils.yesterday(), "yyyyMMdd"));
        YiHaiArchiveProjectDTO yiHaiArchiveProjectDTO = JsonUtils.toObj(getData(yiHaiConfigDTO, JsonUtils.toObj(JsonUtils.toJson(yiHaiReqDTO), Map.class)), YiHaiArchiveProjectDTO.class);
        if (!ObjectUtils.isEmpty(yiHaiArchiveProjectDTO) && "S".equals(yiHaiArchiveProjectDTO.getSTATUS()) && !ObjectUtils.isEmpty(yiHaiArchiveProjectDTO.getITEMS())) {
            List<OpenThirdArchiveProjectDTO> openThirdArchiveProjectDTOS = process(yiHaiArchiveProjectDTO, yiHaiConfigDTO);
            openThirdArchiveProjectService.archiveProjectSync(yiHaiConfigDTO.getCompanyId(), false, null, yiHaiConfigDTO.getThirdArchiveId(), openThirdArchiveProjectDTOS);
        }
        return "success";
    }


    private void checkParams(YiHaiConfigDTO yiHaiConfigDTO) {
        OpenApiBindException openApiBindException = ValidatorUtils.checkValid(yiHaiConfigDTO);
        if (openApiBindException != null) {
            throw openApiBindException;
        }
    }

    private List<OpenThirdArchiveProjectDTO> process(YiHaiArchiveProjectDTO yiHaiArchiveProjectDTO, YiHaiConfigDTO yiHaiConfigDTO) {
        List<OpenThirdArchiveProjectDTO> openThirdArchiveProjectDTOS = new ArrayList<>();
        yiHaiArchiveProjectDTO.ITEMS.forEach(t ->
        {
            OpenThirdArchiveProjectDTO openThirdArchiveProjectDTO = new OpenThirdArchiveProjectDTO();
            openThirdArchiveProjectDTO.setCompanyId(yiHaiConfigDTO.getCompanyId());
            openThirdArchiveProjectDTO.setCode(t.getKOSTL());
            openThirdArchiveProjectDTO.setName(t.getKTEXT().replaceAll("/", "-") + "[" + t.getKOSTL() + "]");
            openThirdArchiveProjectDTO.setThirdProjectId(t.getKOSTL());
            openThirdArchiveProjectDTO.setThirdArchiveId(yiHaiConfigDTO.getThirdArchiveId());
            if ("X".equals(t.getBKZKP())) {
                openThirdArchiveProjectDTO.setState(0);
            } else {
                openThirdArchiveProjectDTO.setState(1);
            }
            openThirdArchiveProjectDTOS.add(openThirdArchiveProjectDTO);
        });
        return openThirdArchiveProjectDTOS;
    }


}

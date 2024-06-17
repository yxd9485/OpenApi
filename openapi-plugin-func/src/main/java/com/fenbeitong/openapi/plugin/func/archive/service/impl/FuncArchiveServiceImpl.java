package com.fenbeitong.openapi.plugin.func.archive.service.impl;

import com.alibaba.fastjson.JSON;
import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiArgumentException;
import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.func.archive.dto.*;
import com.fenbeitong.openapi.plugin.func.archive.service.FuncArchiveService;
import com.fenbeitong.openapi.plugin.support.apply.dto.BaseDTO;
import com.fenbeitong.openapi.plugin.support.employee.service.UserCenterService;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName FuncArchiveServiceImpl
 * @Description 自定义档案项目
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2021/10/31 下午4:37
 **/
@Service
@ServiceAspect
@Slf4j
public class FuncArchiveServiceImpl implements FuncArchiveService {
    @Autowired
    private UserCenterService userCenterService;

    @Value("${host.usercenter}")
    private String ucHost;

    //创建或更新自定义档案项目
    @Override
    public List<ArchiveItemResDTO> createOrUpdateArchiveItem(UpdateArchiveItemReqDTO updateArchiveItemReqDTO, String companyId) throws BindException {
        ValidatorUtils.validateBySpring(updateArchiveItemReqDTO);
        List<ArchiveItemReqDTO> archiveItemList = updateArchiveItemReqDTO.getArchiveItemList();
        Map<String, Long> collect = archiveItemList.stream().collect(Collectors.groupingBy(e -> e.getThirdProjectId(), Collectors.counting()));
        List<String> chongfu = collect.entrySet().stream()
                .filter(e -> e.getValue() > 1).map(Map.Entry::getKey)
                .collect(Collectors.toList());
        if (!ObjectUtils.isEmpty(chongfu)) {
            throw new OpenApiArgumentException(StringUtils.formatString("自定义档案三方id重复:{0}", String.join(",", chongfu)));
        }
        String url = null;
        //档案不存在须先创建。存在但不匹配时异常。
        String thirdArchivedId = queryThirdArchivedIdByCode(companyId);
        if (StringUtils.isBlank(thirdArchivedId)) {
            log.info("自定义档案不存在，先添加自定义档案");
            createArchive(companyId, updateArchiveItemReqDTO.getArchiveName(), updateArchiveItemReqDTO.getThirdArchiveId());
        } else if (!updateArchiveItemReqDTO.getThirdArchiveId().equals(thirdArchivedId)) {
            log.info("仅可对自定义档案下的项目进行操作，自定义档案三方id:{}，传入更新档案id:{}", thirdArchivedId, updateArchiveItemReqDTO.getThirdArchiveId());
            throw new OpenApiArgumentException(StringUtils.formatString("仅可对自定义档案下的项目进行操作，自定义档案三方id:{0}，传入更新档案id:{1}", thirdArchivedId, updateArchiveItemReqDTO.getThirdArchiveId()));
        }
        //新增档案项目
        if (updateArchiveItemReqDTO.getType().equals(Integer.parseInt("1"))) {
            url = "/uc/archive/project/third/create";
        } else if (updateArchiveItemReqDTO.getType().equals(Integer.parseInt("2"))){
            //更新档案项目
            url = "/uc/archive/project/third/update";
        } else {
            throw new OpenApiArgumentException("操作类型错误");
        }

        //1、查询企业授权负责人
        String ucSuperAdminToken = userCenterService.getUcSuperAdminToken(companyId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Auth-Token", ucSuperAdminToken);

        //返回结果集
        List<ArchiveItemResDTO> errorResList = new ArrayList<>();

        //2、赋值，调用新增或更新自定义档案
        for (ArchiveItemReqDTO archiveItemReqDTO : updateArchiveItemReqDTO.getArchiveItemList()) {
            archiveItemReqDTO.setThirdArchiveId(updateArchiveItemReqDTO.getThirdArchiveId());
            archiveItemReqDTO.setCompanyId(companyId);
            log.info(">>>自定义档案新增或更新开始,入参:{}", StringUtils.obj2str(JSON.toJSON(archiveItemReqDTO)));
            String result = RestHttpUtils.postJson(ucHost + url, httpHeaders, StringUtils.obj2str(JSON.toJSON(archiveItemReqDTO)));
            log.info(">>>>自定义档案新增或更新结束,出参:{}", result);
            BaseDTO baseResult = JsonUtils.toObj(result, BaseDTO.class);

            if (baseResult == null || !baseResult.success()) {
                String msg = baseResult == null ? "" : Optional.ofNullable(baseResult.getMsg()).orElse("");
                ArchiveItemResDTO errorRes = ArchiveItemResDTO.builder().errorCode(-999).errorMsg(msg).code(archiveItemReqDTO.getCode()).name(archiveItemReqDTO.getName()).thirdProjectId(archiveItemReqDTO.getThirdProjectId()).build();
                errorResList.add(errorRes);
            }
        }
        return errorResList;
    }

    //批量删除自定义档案项目
    @Override
    public void deleteArchiveItem(DeleteArchiveItemReqDTO deleteArchiveItemReqDTO, String companyId) {

        //1、根据三方档案id在档案列表中查询档案信息
        String thirdArchiveIdRes = queryThirdArchivedIdByCode(companyId);
        //校验三方档案id是否正确
        if (!deleteArchiveItemReqDTO.getThirdArchiveId().equals(thirdArchiveIdRes)) {
            log.warn("仅可删除自定义档案下的档案项目，三方档案id错误，自定义三方档案id:{},传入的三方档案ID:{}", thirdArchiveIdRes, deleteArchiveItemReqDTO.getThirdArchiveId());
            throw new OpenApiArgumentException("仅可删除自定义档案下的档案项目，三方档案id错误");
        }
        //去重
        List<String> idList = deleteArchiveItemReqDTO.getIdList();
        idList = idList.stream().distinct().collect(Collectors.toList());
        deleteArchiveItemReqDTO.setIdList(idList);
        //1、查询企业授权负责人
        String ucSuperAdminToken = userCenterService.getUcSuperAdminToken(companyId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Auth-Token", ucSuperAdminToken);
        log.info(">>>批量删除自定义档案项目,入参:{}", StringUtils.obj2str(JSON.toJSON(deleteArchiveItemReqDTO)));
        deleteArchiveItemReqDTO.setArchiveId(deleteArchiveItemReqDTO.getThirdArchiveId());
        String result = RestHttpUtils.postJson(ucHost + "/uc/archive/project/third/batch_delete", httpHeaders, StringUtils.obj2str(JSON.toJSON(deleteArchiveItemReqDTO)));
        log.info(">>>批量删除自定义档案项目返回:{}", result);
        BaseDTO baseResult = JsonUtils.toObj(result, BaseDTO.class);
        if (baseResult == null || !baseResult.success()) {
            String msg = baseResult == null ? "" : Optional.ofNullable(baseResult.getMsg()).orElse("");
            throw new FinhubException(-999, msg);
        }
    }

    //新增档案
    public void createArchive(String companyId, String name, String thirdArchiveId) {
        ArchiveAddReqDTO archiveAddReqDTO = ArchiveAddReqDTO.builder().companyId(companyId).code("custom_archive").name(name).thirdArchiveId(thirdArchiveId).archiveFile(1).build();
        //1、查询企业授权负责人
        String ucSuperAdminToken = userCenterService.getUcSuperAdminToken(companyId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Auth-Token", ucSuperAdminToken);
        log.info(">>>新增自定义档案,入参:{}", StringUtils.obj2str(JSON.toJSON(archiveAddReqDTO)));
        String result = RestHttpUtils.postJson(ucHost + "/uc/archive/third/create", httpHeaders, StringUtils.obj2str(JSON.toJSON(archiveAddReqDTO)));
        log.info(">>>新增自定义档案返回:{}", result);
        BaseDTO baseResult = JsonUtils.toObj(result, BaseDTO.class);
        if (baseResult == null || !baseResult.success()) {
            String msg = baseResult == null ? "" : Optional.ofNullable(baseResult.getMsg()).orElse("");
            throw new FinhubException(-999, msg);
        }
    }

    //根据三方项目id查找档案编号
    public String queryThirdArchivedIdByCode(String companyId) {
        String ucSuperAdminToken = userCenterService.getUcSuperAdminToken(companyId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Auth-Token", ucSuperAdminToken);

        HashMap<String, Object> param = Maps.newHashMap();
        param.put("companyId", companyId);
        log.info(">>>查看档案列表,入参:{}", companyId);
        String result = RestHttpUtils.get(ucHost + "/uc/archive/third/list", httpHeaders, param);
        log.info(">>>查看档案列表:{}", result);
        String thirdArchiveId = null;
        BaseDTO baseResult = JsonUtils.toObj(result, BaseDTO.class);
        if (baseResult == null || !baseResult.success()) {
            String msg = baseResult == null ? "" : Optional.ofNullable(baseResult.getMsg()).orElse("");
            throw new FinhubException(-999, msg);
        }
        Object data = MapUtils.getValueByExpress(JsonUtils.toObj(result, Map.class), "data");
        List<Map<String, Object>> dataList = ObjectUtils.isEmpty(data) ? null : JsonUtils.toObj(JsonUtils.toJson(data), List.class);
        if (!ObjectUtils.isEmpty(dataList)) {
            for (Map<String, Object> jo : dataList) {
                if ("custom_archive".equals(jo.get("code"))) {
                    thirdArchiveId = jo.get("thirdArchiveId") == null ? null : StringUtils.obj2str(jo.get("thirdArchiveId")).trim();
                }
            }
        }
        return thirdArchiveId;
    }
}

package com.fenbeitong.openapi.plugin.task.yunzhijia;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.constant.TaskConfig;
import com.fenbeitong.finhub.task.itf.ITaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.core.exception.OpenApiPluginException;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.task.utils.FinhubTaskUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.yunzhijia.common.YunzhijiaResponseCode;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaEmployeeDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaOrgInChargeDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaOrgReqDTO;
import com.fenbeitong.openapi.plugin.yunzhijia.dto.YunzhijiaResponse;
import com.fenbeitong.openapi.plugin.yunzhijia.handler.YunzhijiaOrgHandler;
import com.fenbeitong.openapi.plugin.yunzhijia.service.IYunzhijiaOrgService;
import com.fenbeitong.openapi.sdk.dto.common.OpenApiRespDTO;
import com.fenbeitong.openapi.sdk.dto.organization.UpdateOrgUnitLeaderReqDTO;
import com.fenbeitong.openapi.sdk.webservice.organization.FbtOrganizationService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import retrofit2.Call;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 云之家部门负责人修变更理器
 */
@Component
@Slf4j
public class YunzhijiaOrgLeaderModifyProcessor extends YunzhijiaOrgHandler implements ITaskProcessor {

    @Autowired
    private IYunzhijiaOrgService yunzhijiaOrgService;

    @Autowired
    private FbtOrganizationService fbtOrganizationService;

    @Autowired
    private TaskConfig taskConfig;

    @Override
    public Integer getTaskType() {
        return TaskType.YUNZHIJIA_ORG_DEPT_LEADER_MODIFY.getCode();
    }

    @Override
    public TaskProcessResult process(FinhubTask task) {
        //1.解析task
        String corpId = task.getCompanyId();
        String dataId = task.getDataId();
        //2.查询企业注册信息
        PluginCorpDefinition byCorpId = getPluginCorpDefinitionByCorpId(corpId);
        //3.部门参数构造
        ArrayList<String> idList = Lists.newArrayList();
        idList.add(dataId);
        YunzhijiaOrgReqDTO build = YunzhijiaOrgReqDTO.builder()
                .eid(corpId)
                .array(idList)
                .build();
        //4.查询云之家部门基础信息和负责人信息
        YunzhijiaResponse<YunzhijiaOrgInChargeDTO> yunzhijiaRemoteOrgBaseOrLeaderDetail = yunzhijiaOrgService.getYunzhijiaRemoteOrgBaseOrLeaderDetail(build);
        if (ObjectUtils.isEmpty(yunzhijiaRemoteOrgBaseOrLeaderDetail) || yunzhijiaRemoteOrgBaseOrLeaderDetail.getErrorCode() != NumericUtils.obj2int(YunzhijiaResponseCode.YUNZHIJIA_SUCCESS)) {
            throw new OpenApiPluginException((NumericUtils.obj2int(YunzhijiaResponseCode.YUNZHIJIA_ORG_NULL)));
        }
        //5.分贝通参数构造
        YunzhijiaOrgInChargeDTO data = yunzhijiaRemoteOrgBaseOrLeaderDetail.getData();
        //获取部门负责人信息
        List<YunzhijiaEmployeeDTO> inChargers = data.getInChargers();
        if (ObjectUtils.isEmpty(inChargers)) {//为空则不进行更新操作
            throw new OpenApiPluginException((NumericUtils.obj2int(YunzhijiaResponseCode.YUNZHIJIA_ORG_LEADER_NULL)));
        }
        List<String> userIdList = Lists.newArrayList();
        inChargers.stream().forEach(e ->
                userIdList.add(e.getOpenId())
        );

        //6.分贝通更新部门负责人，调用分贝通
        UpdateOrgUnitLeaderReqDTO updateOrgUnitLeaderReqDTO = new UpdateOrgUnitLeaderReqDTO();
        UpdateOrgUnitLeaderReqDTO.OrgUnitRoleDTO orgUnitRoleDTO = new UpdateOrgUnitLeaderReqDTO.OrgUnitRoleDTO();
        orgUnitRoleDTO.setOrgUnitId(dataId);
        orgUnitRoleDTO.setUserIds(userIdList);
        List<UpdateOrgUnitLeaderReqDTO.OrgUnitRoleDTO> orgUnitRoleDTOList = Lists.newArrayList();
        orgUnitRoleDTOList.add(orgUnitRoleDTO);
        //构建更细部门主管请求参数
        updateOrgUnitLeaderReqDTO.setCompanyId(byCorpId.getAppId());
        updateOrgUnitLeaderReqDTO.setDeleteHistory(true);
        updateOrgUnitLeaderReqDTO.setOperatorId(byCorpId.getAdminId());
        updateOrgUnitLeaderReqDTO.setOperatorRole("6");
        //部门主管为1
        updateOrgUnitLeaderReqDTO.setEmpowerType(1);
        updateOrgUnitLeaderReqDTO.setSource("openapi");
        //第三方ID为2
        updateOrgUnitLeaderReqDTO.setType("2");
        updateOrgUnitLeaderReqDTO.setRoleIds(orgUnitRoleDTOList);
        //6.调用分贝通添加部门
        Call<OpenApiRespDTO> leaderModify = fbtOrganizationService.leaderModify(updateOrgUnitLeaderReqDTO);
        //如果返回错误code
        OpenApiRespDTO body = null;
        try {
            body = leaderModify.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (body.getCode() != 0) {
            throw new FinhubException(body.getCode(), 0, body.getMsg());
        }
        if(!ObjectUtils.isEmpty(body.getData())){
            throw new FinhubException(body.getCode(), 0, JsonUtils.toJson(body.getData()));
        }
        return TaskProcessResult.success("success");
    }

    @Override
    public String getTaskSrc() {
        return this.taskConfig.getTaskNamespace();
    }

    @Override
    public Long getSleepSeconds(FinhubTask task) {
        return FinhubTaskUtils.getSleepSeconds(task);
    }
}

package com.fenbeitong.openapi.plugin.dingtalk.yida.handler;

import com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl.DingtalkCorpServiceImpl;
import com.fenbeitong.openapi.plugin.dingtalk.yida.dao.DingtalkYidaCorpDao;
import com.fenbeitong.openapi.plugin.dingtalk.yida.dto.YiDaApplyDetailRespDTO;
import com.fenbeitong.openapi.plugin.dingtalk.yida.entity.DingtalkYidaCorp;
import com.fenbeitong.openapi.plugin.dingtalk.yida.service.IYiDaApplyService;
import com.fenbeitong.openapi.plugin.dingtalk.yida.service.IYiDaProcessApplyService;
import com.fenbeitong.openapi.plugin.dingtalk.yida.service.impl.YiDaProcessApplyFactory;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.task.entity.Task;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.support.task.handler.ITaskHandler;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.luastar.swift.base.json.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 钉钉审批流程结束处理类
 *
 * @author zhaokechun
 * @date 2018/11/27 14:34
 */
@Slf4j
@Service
public class YiDaApplyHandler implements ITaskHandler {

    @Autowired
    private DingtalkYidaCorpDao dingtalkYidaCorpDao;

    @Autowired
    private IYiDaApplyService yiDaApplyService;

    @Autowired
    private YiDaProcessApplyFactory yiDaProcessApplyFactory;

    @Override
    public TaskResult execute(Task task) throws Exception {
        String corpId = task.getCorpId();
        DingtalkYidaCorp DingtalkYidaCorp = dingtalkYidaCorpDao.getDingtalkYidaCorpByCorpId(corpId);
        if (DingtalkYidaCorp == null) {
            log.info("企业不存在, 跳过, corpId: {}", corpId);
            return TaskResult.ABORT;
        }
        Map<String, Object> dataMap = JsonUtils.toObj(task.getDataContent(), Map.class);
        String formInstId = StringUtils.obj2str(dataMap.get("formInstId"));
        int applyType = NumericUtils.obj2int(dataMap.get("applyType"));
        String companyId = DingtalkYidaCorp.getCompanyId();
        YiDaApplyDetailRespDTO yidaApplyDetailResp = yiDaApplyService.getInstanceById(formInstId, corpId);
        IYiDaProcessApplyService processApply = yiDaProcessApplyFactory.getProcessApply(applyType);
        return processApply.processApply(companyId, yidaApplyDetailResp);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.YIDA_BPMS_INSTANCE_CHANGE;
    }

}

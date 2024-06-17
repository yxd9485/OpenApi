package com.fenbeitong.openapi.plugin.task.dingtalk.yida;

import com.fenbeitong.finhub.task.bo.TaskProcessResult;
import com.fenbeitong.finhub.task.impl.AbstractTaskProcessor;
import com.fenbeitong.finhub.task.po.FinhubTask;
import com.fenbeitong.openapi.plugin.dingtalk.yida.dao.DingtalkYidaCorpDao;
import com.fenbeitong.openapi.plugin.dingtalk.yida.dto.YiDaApplyDetailRespDTO;
import com.fenbeitong.openapi.plugin.dingtalk.yida.entity.DingtalkYidaCorp;
import com.fenbeitong.openapi.plugin.dingtalk.yida.service.IYiDaApplyService;
import com.fenbeitong.openapi.plugin.dingtalk.yida.service.IYiDaProcessApplyService;
import com.fenbeitong.openapi.plugin.dingtalk.yida.service.impl.YiDaProcessApplyFactory;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskType;
import com.fenbeitong.openapi.plugin.task.utils.FinhubTaskUtils;
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
public class YiDaApplyProcessor extends AbstractTaskProcessor {

    @Autowired
    private DingtalkYidaCorpDao dingtalkYidaCorpDao;

    @Autowired
    private IYiDaApplyService yiDaApplyService;

    @Autowired
    private YiDaProcessApplyFactory yiDaProcessApplyFactory;

    @Override
    public TaskProcessResult process(FinhubTask task) throws Exception {
        String corpId = task.getCompanyId();
        DingtalkYidaCorp DingtalkYidaCorp = dingtalkYidaCorpDao.getDingtalkYidaCorpByCorpId(corpId);
        if (DingtalkYidaCorp == null) {
            log.info("企业不存在, 跳过, corpId: {}", corpId);
            return TaskProcessResult.success("企业不存在, 跳过 success");
        }
        Map<String, Object> dataMap = JsonUtils.toObj(task.getDataContent(), Map.class);
        String formInstId = StringUtils.obj2str(dataMap.get("formInstId"));
        int applyType = NumericUtils.obj2int(dataMap.get("applyType"));
        String companyId = DingtalkYidaCorp.getCompanyId();
        YiDaApplyDetailRespDTO yidaApplyDetailResp = yiDaApplyService.getInstanceById(formInstId, corpId);
        IYiDaProcessApplyService processApply = yiDaProcessApplyFactory.getProcessApply(applyType);
        TaskResult taskResult = processApply.processApply(companyId, yidaApplyDetailResp);
        return FinhubTaskUtils.convert2FinhubTaskResult(taskResult);
    }

    @Override
    public Integer getTaskType() {
        return TaskType.YIDA_BPMS_INSTANCE_CHANGE.getCode();
    }

}

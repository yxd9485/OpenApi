package com.fenbeitong.openapi.plugin.dingtalk.common.message.apply.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.dingtalk.common.message.apply.entity.DingtalkApproveTask;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by xiaohai on 2021/08/04.
 */
@Component
public class DingtalkApproveTaskDao extends OpenApiBaseDao<DingtalkApproveTask> {

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<DingtalkApproveTask> listDingtalkApproveTask(Map<String, Object> condition) {
        Example example = new Example(DingtalkApproveTask.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return listByExample(example);
    }

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public DingtalkApproveTask getDingtalkApprovexTask(Map<String, Object> condition) {
        Example example = new Example(DingtalkApproveTask.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

    /**
     * 使用审批单ID查询（APPROVE_ID）
     * @param approveId
     * @return
     */
    public List<DingtalkApproveTask> getDingtalkApproveTaskByApproveId(String approveId) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("approveId", approveId);
        return listDingtalkApproveTask(condition);
    }

    /**
     * 使用审批单ID和用户ID查询（APPROVE_ID、USRE_ID）
     * @param approveId
     * @param userId
     * @return
     */
    public DingtalkApproveTask getDingtalkApproveTaskByApproveIdAndUserId(String approveId,String userId) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("approveId", approveId);
        condition.put("userId", userId);
        return getDingtalkApprovexTask(condition);
    }

    /**
     * 通过状态和审批单id查询（APPROVE_ID ，APPROVE_STATU）
     * @param approveId
     * @return
     */
    public List<DingtalkApproveTask> getDingtalkApproveTaskByApproveIdAndStatus(String approveId,Integer approveStauts) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("approveId", approveId);
        condition.put("approveStauts", approveStauts);
        return listDingtalkApproveTask(condition);
    }

}

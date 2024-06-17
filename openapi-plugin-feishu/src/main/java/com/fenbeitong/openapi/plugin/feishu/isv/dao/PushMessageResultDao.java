package com.fenbeitong.openapi.plugin.feishu.isv.dao;

import com.fenbeitong.openapi.plugin.feishu.isv.entity.PushMessageResult;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.feishu.isv.mapper.PushMessageResultMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by zhang on 2021/01/14.
 */
@Component
public class PushMessageResultDao extends OpenApiBaseDao<PushMessageResult> {

    @Autowired
    private PushMessageResultMapper pushMessageResultMapper;

    /**
     * 使用map条件查询
     * @param condition
     * @return
     */
    public List<PushMessageResult> listPushMessageResult(Map<String, Object> condition) {
        Example example = new Example(PushMessageResult.class);
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
    public PushMessageResult getPushMessageResult(Map<String, Object> condition) {
        Example example = new Example(PushMessageResult.class);
        Example.Criteria criteria = example.createCriteria();
        for (String key : condition.keySet()) {
            criteria.andEqualTo(key, condition.get(key));
        }
        return getByExample(example);
    }

    /**
     * 查询发送失败的用户
     * @param
     * @return
     */
    public List<PushMessageResult> listFindFailUser(String thirdEmployeeId,String companyId,
                                                String createTimeBegin,String createTimeEnd,Integer sendSuccess) {
        Example example = new Example(PushMessageResult.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtil.isNotEmpty(thirdEmployeeId)) {
            criteria.andEqualTo("thirdEmployeeId", thirdEmployeeId);
        }
        if(StringUtil.isNotEmpty(companyId)) {
            criteria.andEqualTo("companyId", companyId);
        }
        if(StringUtil.isNotEmpty(createTimeBegin)||StringUtil.isNotEmpty(createTimeEnd)){
            criteria.andBetween("createTime", createTimeBegin, createTimeEnd);
        }
        criteria.andEqualTo("sendSuccess", sendSuccess);
        return listByExample(example);
    }


    /**
     * 批量修改通知结果
     * @param pushMessageResultList
     */
    public void batchUpdateList(List<PushMessageResult> pushMessageResultList){
        pushMessageResultMapper.batchUpdateList(pushMessageResultList);
    }

}

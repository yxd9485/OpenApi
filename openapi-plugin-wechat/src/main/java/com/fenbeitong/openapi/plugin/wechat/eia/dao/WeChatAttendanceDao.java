package com.fenbeitong.openapi.plugin.wechat.eia.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.wechat.eia.entity.WeChatApply;
import com.fenbeitong.openapi.plugin.wechat.eia.entity.WechatAttendanceEntity;
import com.fenbeitong.openapi.plugin.wechat.eia.entity.WechatTokenConf;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

/**
 * @Description
 * @Author duhui
 * @Date 2021-02-19
 **/
@Component
public class WeChatAttendanceDao extends OpenApiBaseDao<WechatAttendanceEntity> {

    public WechatAttendanceEntity getByUserIdAndcheckinTime(String userId, String checkInTime) {
        Example example = new Example(WechatAttendanceEntity.class);
        example.createCriteria().andEqualTo("userId", userId).andEqualTo("checkinTime", checkInTime);
        return getByExample(example);
    }

}

package com.fenbeitong.openapi.plugin.feishu.isv.mapper;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseMapper;
import com.fenbeitong.openapi.plugin.feishu.isv.entity.PushMessageResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by zhang on 2021/01/14.
 */
@Component
@Mapper
public interface PushMessageResultMapper extends OpenApiBaseMapper<PushMessageResult> {

    @Update({"<script>" +
            "<foreach collection='list' item='item' index='index' separator=';' close=';'> " +
            "update " +
            "push_message_result set " +
            "send_success=#{item.sendSuccess}, " +
            "fail_num=#{item.failNum} " +
            "where id=#{item.id} " +
            "</foreach> " +
            "</script>"})
    void batchUpdateList(@Param("list") List<PushMessageResult> pushMessageResultLis);
}

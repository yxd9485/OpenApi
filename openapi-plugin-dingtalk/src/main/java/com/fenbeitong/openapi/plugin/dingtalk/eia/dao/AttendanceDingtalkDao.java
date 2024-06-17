package com.fenbeitong.openapi.plugin.dingtalk.eia.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.dingtalk.eia.entity.AttendanceDingtalk;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

/**
 * <p>Title: AttendanceDingtalkDao</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/1/17 11:28 AM
 */
@Component
public class AttendanceDingtalkDao extends OpenApiBaseDao<AttendanceDingtalk> {

    /**
     * 获取钉钉打卡记录
     *
     * @param dingtalkCheckId 唯一标识ID
     * @param recordId        打卡记录ID
     * @return 钉打卡记录
     */
    public AttendanceDingtalk getAttendanceDingtalk(Long dingtalkCheckId, Long recordId) {
        Example example = new Example(AttendanceDingtalk.class);
        example.createCriteria()
                .andEqualTo("dingtalkCheckId", dingtalkCheckId)
                .andEqualTo("recordId", recordId);
        return getByExample(example);
    }
}

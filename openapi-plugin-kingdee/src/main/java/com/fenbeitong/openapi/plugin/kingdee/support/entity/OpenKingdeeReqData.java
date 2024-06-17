package com.fenbeitong.openapi.plugin.kingdee.support.entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by duhui on 2021/07/27.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "open_kingdee_req_data")
public class OpenKingdeeReqData {

    /**
     * 
     */
    @Id
    @Column(name = "id")
    private String id;

    /**
     * 公司ID
     */
    @Column(name = "company_id")
    private String companyId;

    /**
     * JSON数据
     */
    @Column(name = "data_key")
    private String dataKey;

    /**
     * JSON数据
     */
    @Column(name = "data_value")
    private String dataValue;

    /**
     * JSON所在层级，1，2，3，4
     */
    @Column(name = "data_level")
    private Integer dataLevel;

    /**
     * 数据类型 1:map 2:list 3数组
     */
    @Column(name = "data_type")
    private Integer dataType;

    /**
     * 操作类型  add:保存 commit:提交  audit:审核
     */
    @Column(name = "operation_type")
    private String operationType;
    /**
     * 金蝶的业务模块类型
     */
    @Column(name = "module_type")
    private String moduleType;

    /**
     * 0 无效 1有效
     */
    @Column(name = "status")
    private Integer status;

    /**
     * 创建时间
     */
    @Column(name = "create_date")
    private Date createDate;

    /**
     * 更新时间
     */
    @Column(name = "update_date")
    private Date updateDate;

    /**
     * 
     */
    @Column(name = "is_ding")
    private Integer isDing;


}

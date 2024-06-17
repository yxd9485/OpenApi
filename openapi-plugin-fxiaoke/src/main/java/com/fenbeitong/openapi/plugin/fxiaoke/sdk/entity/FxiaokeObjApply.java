package com.fenbeitong.openapi.plugin.fxiaoke.sdk.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by hanshuqi on 2020/07/05.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "fxiaoke_obj_apply")
public class FxiaokeObjApply {

    /**
     * 
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 第三方公司ID
     */
    @Column(name = "CORP_ID")
    private String corpId;

    /**
     * 自定义对象API_NAME
     */
    @Column(name = "OBJ_API_NAME")
    private String objApiName;

    /**
     * api_name状态，0:可用，1:不可用
     */
    @Column(name = "OBJ_STATE")
    private Integer objState;

    /**
     * 对象关联的审批单对象
     */
    @Column(name = "OBJ_APPLY_API_NAME")
    private String objApplyApiName;

    /**
     * 审批单类型，1:差旅，12:用车
     */
    @Column(name = "OBJ_APPLY_TYPE")
    private String objApplyType;

    /**
     * 创建时间
     */
    @Column(name = "CREATE_TIME")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "UPDATE_TIME")
    private Date updateTime;


}

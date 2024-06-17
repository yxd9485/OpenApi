package com.fenbeitong.openapi.plugin.seeyon.entity;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@Table(name="fb_org_emp")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeeyonFbOrgEmp {

    private static final long serialVersionUID = 1L;

    /** 执行ID */
    @Column(name = "ID")
    private String id;

    /** 公司ID */
    @Column(name = "COMPANY_ID")
    private String companyId;

    /** 需要同步到分贝通的数据 */
    @Column(name = "JSON_DATA")
    private String jsonData;

    /** 标识是部门还是人员 */
    @Column(name = "DATA_TYPE")
    private Integer dataType;

    /** 数据执行方式，包含新增，修改，删除 */
    @Column(name = "DATA_EXECUTE_MANNER")
    private String dataExecuteManner;

    /** 执行顺序 */
    @Column(name = "SORT")
    private String sort;

    /** 执行标识，标识数据是否执行过，用于执行数据的标识，从而判定数据是否被执行过 */
    @Column(name = "EXECUTE_MARK")
    private String executeMark;

    /** 部门添加操作，部门比对是需要根据父部门的长度进行添加顺序的判断 */
    @Column(name = "ORG_PATH")
    private String orgPath;

    /** 创建时间 */
    @Column(name = "CREATE_TIME")
    private LocalDateTime createTime;

    /** 更新时间，当execute_mark由未执行状态变为执行过状态时，会进行更新该字段 */
    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;

    public static final String ID = "ID";

    public static final String COMPANY_ID = "COMPANY_ID";

    public static final String JSON_DATA = "JSON_DATA";

    public static final String DATA_TYPE = "DATA_TYPE";

    public static final String DATA_EXECUTE_MANNER = "DATA_EXECUTE_MANNER";

    public static final String SORT = "SORT";

    public static final String EXECUTE_MARK = "EXECUTE_MARK";

    public static final String ORG_PATH = "ORG_PATH";

    public static final String CREATE_TIME = "CREATE_TIME";

    public static final String UPDATE_TIME = "UPDATE_TIME";
}

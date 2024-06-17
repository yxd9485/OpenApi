// @formatter:off
package com.fenbeitong.openapi.plugin.seeyon.entity;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * @author Ivan
 * @since 2019-03-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Table(name="fb_error_org_emp")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeeyonFbErrorOrgEmp extends SuperModel {

  private static final long serialVersionUID = 1L;

  /** 主键ID */
  @Column(name = "ID")
  private String id;

  /** 公司ID */
  @Column(name = "COMPANY_ID")
  private String companyId;

  @Column(name = "JSON_DATA")
  private String jsonData;

  @Column(name = "RESPONSE_JSON_DATA")
  private String responseJsonData;

  @Column(name = "DATA_TYPE")
  private Integer dataType;

  /** 数据执行方式，包含新增，修改，删除 */
  @Column(name = "DATA_EXECUTE_MANNER")
  private String dataExecuteManner;

  /** 执行次数 */
  @Column(name = "EXECUTE_TIMES")
  private Integer executeTimes;

  /** 执行顺序 */
  @Column(name = "SORT")
  private String sort;

  /** 部门添加操作，部门比对是需要根据父部门的长度进行添加顺序的判断 */
  @Column(name = "ORG_PATH")
  private String orgPath;

  /** 处理结果，包括已完成:0，待处理:1，无需处理:2, */
  @Column(name = "EXECUTE_RESULT")
  private Integer executeResult;

  /** 创建时间 */
  @Column(name = "CREATE_TIME")
  private LocalDateTime createTime;

  /** 当execute_times由未执行状态变为执行过状态时，次数会一次增加，次数增加后会更新该字段值 */
  @Column(name = "UPDATE_TIME")
  private LocalDateTime updateTime;

  public static final String ID = "ID";

  public static final String COMPANY_ID = "COMPANY_ID";

  public static final String JSON_DATA = "JSON_DATA";

  public static final String RESPONSE_JSON_DATA = "RESPONSE_JSON_DATA";

  public static final String DATA_TYPE = "DATA_TYPE";

  public static final String DATA_EXECUTE_MANNER = "DATA_EXECUTE_MANNER";

  public static final String EXECUTE_TIMES = "EXECUTE_TIMES";

  public static final String SORT = "SORT";

  public static final String ORG_PATH = "ORG_PATH";

  public static final String EXECUTE_RESULT = "EXECUTE_RESULT";

  public static final String CREATE_TIME = "CREATE_TIME";

  public static final String UPDATE_TIME = "UPDATE_TIME";
}

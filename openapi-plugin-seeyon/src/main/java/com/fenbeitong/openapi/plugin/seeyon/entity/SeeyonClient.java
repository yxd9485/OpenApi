// @formatter:off
package com.fenbeitong.openapi.plugin.seeyon.entity;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * @author Ivan
 * @since 2019-03-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Table(name ="seeyon_client")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeeyonClient extends SuperModel {

  private static final long serialVersionUID = 1L;

  /** UUID */
  @Column(name = "UUID")
  private String uuid;

  /** 致远OA系统中企业组织中文名称，用于查询企业信息 */
  @Column(name = "SEEYON_ORG_NAME")
  private String seeyonOrgName;

  /** 致远OA系统中企业orgAccountId，用于调用业务接口 */
  @Column(name = "SEEYON_ACCOUNT_ID")
  private String seeyonAccountId;

  /** 致远OA系统提供的第三方调用用户名，用于获取token */
  @Column(name = "SEEYON_USERNAME")
  private String seeyonUsername;

  /** 致远OA系统提供的第三方调用密码，用于获取token */
  @Column(name = "SEEYON_PASSWORD")
  private String seeyonPassword;

  /** 致远OA系统访问URI */
  @Column(name = "SEEYON_SYS_URI")
  private String seeyonSysUri;
  /** 致远OA系统code */
  @Column(name = "SEEYON_CODE")
  private String seeyonCode;
  /** 致远OA单位集团类型 */
  @Column(name = "GROUP_TYPE")
  private String groupType;

  /** 开放平台，企业唯一ID */
  @Column(name = "OPENAPI_APP_ID")
  private String openapiAppId;

  /** 开放平台，企业调用接口密钥 */
  @Column(name = "OPENAPI_APP_KEY")
  private String openapiAppKey;

  /** 开放平台，企业请求数据校验密钥 */
  @Column(name = "OPENAPI_SIGN_KEY")
  private String openapiSignKey;

  /** 创建时间 */
  @Column(name = "CREATE_TIME")
  private Timestamp createTime;

  /** 更新时间 */
  @Column(name = "UPDATE_TIME")
  private Timestamp updateTime;

  /** 员工ID */
  @Column(name = "EMPLOYEE_ID")
  private String employeeId;

  /** 员工ID */
  @Column(name = "EMPLOYEE_ID_THIRD")
  private String employeeIdThird;

  /** 数据有效状态，0，有效，1，失效 */
  @Column(name = "STATUS")
  private Integer status;

    /** 致远系统编码 */
    @Column(name = "REGISTER_CODE")
    private String registerCode;

    public static final String UUID = "UUID";

  public static final String SEEYON_ORG_NAME = "SEEYON_ORG_NAME";

  public static final String SEEYON_ACCOUNT_ID = "SEEYON_ACCOUNT_ID";

  public static final String SEEYON_USERNAME = "SEEYON_USERNAME";

  public static final String SEEYON_PASSWORD = "SEEYON_PASSWORD";

  public static final String SEEYON_SYS_URI = "SEEYON_SYS_URI";

  public static final String OPENAPI_APP_ID = "OPENAPI_APP_ID";

  public static final String OPENAPI_APP_KEY = "OPENAPI_APP_KEY";

  public static final String OPENAPI_SIGN_KEY = "OPENAPI_SIGN_KEY";

  public static final String CREATE_TIME = "CREATE_TIME";

  public static final String UPDATE_TIME = "UPDATE_TIME";

  public static final String EMPLOYEE_ID = "EMPLOYEE_ID";

  public static final String EMPLOYEE_ID_THIRD = "EMPLOYEE_ID_THIRD";

  public static final String STATUS = "STATUS";

  public static final String REGISTER_CODE = "REGISTER_CODE";
}

// @formatter:off
package com.fenbeitong.openapi.plugin.seeyon.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * @author Ivan
 * @since 2019-03-05
 */
@Data
@Table(name = "seeyon_org_dept")
public class SeeyonOrgDepartment extends SuperModel {

  private static final long serialVersionUID = 1L;

  /** 致远系统部门ID */
  @Id
  @Column(name = "ID")
  private Long id;

  /** 致远系统公司ID */
  @Column(name = "ORG_ACCOUNT_ID")
  private Long orgAccountId;

  /** 部门名称 */
  @Column(name = "NAME")
  private String name;

  /** 部门编码 */
  @Column(name = "CODE")
  private String code;

  /** 创建时间 */
  @Column(name = "CREATE_TIME")
  private LocalDateTime createTime;

  /** 更新时间 */
  @Column(name = "UPDATE_TIME")
  private LocalDateTime updateTime;

  /** 排序ID */
  @Column(name = "SORT_ID")
  private Integer sortId;

  /** 是否删除 */
  @Column(name = "IS_DELETED")
  private Boolean isDeleted;

  /** 启用 */
  @Column(name = "ENABLED")
  private Boolean enabled;

  /** 外部机构类型 */
  @Column(name = "EXTERNAL_TYPE")
  private Integer externalType;

  /** 状态 */
  @Column(name = "STATUS")
  private Integer status;

  /** 描述 */
  @Column(name = "DESCRIPTION")
  private String description;

  /** 路径 */
  @Column(name = "PATH")
  private String path;

  /** 简称 */
  @Column(name = "SHORT_NAME")
  private String shortName;

  /** 外文名称 */
  @Column(name = "SECOND_NAME")
  private String secondName;

  /** 是否是内部机构 */
  @Column(name = "IS_INTERNAL")
  private Boolean isInternal;

  /** 是否是集团 */
  @Column(name = "IS_GROUP")
  private Boolean isGroup;

  /** 只对type=account有效 */
  @Column(name = "LEVEL_SCOPE")
  private Integer levelScope;

  /** 类型 */
  @Column(name = "TYPE")
  private String type;

  /** 上级单位Id，如果当前单位为集团则值为-1 */
  @Column(name = "SUPERIOR")
  private Long superior;

  /** 上级单位名称 */
  @Column(name = "SUPERIOR_NAME")
  private String superiorName;

  /** 排序id类型 */
  @Column(name = "SORT_ID_TYPE")
  private String sortIdType;

  /** 层级 */
  @Column(name = "LEVEL")
  private Integer level;

  /** 自定义属性 */
  @Column(name = "CUSTOMER_PROPERTIES")
  private String customerProperties;

  /** 部门主管 */
  @Column(name = "DEP_MANAGER")
  private String depManager;

  /** 部门管理员 */
  @Column(name = "DEP_ADMIN")
  private String depAdmin;

  /** 全名 */
  @Column(name = "WHOLE_NAME")
  private String wholeName;

  /** 实体类型 */
  @Column(name = "ENTITY_TYPE")
  private String entityType;

  /** 是否合法 */
  @Column(name = "VALID")
  private Boolean valid;

  /** 是否是集团 */
  @Column(name = "I_GROUP")
  private Boolean iGroup;

  /** 上级单位id */
  @Column(name = "SUPERIOR0")
  private Long superior0;

  /** 自定义入口 */
  @Column(name = "CUSTOM_LOGIN")
  private Boolean customLogin;

  /** 自定义入口地址 */
  @Column(name = "CUSTOM_LOGIN_URL")
  private String customLoginUrl;

  /** 上级路径 */
  @Column(name = "PARENT_PATH")
  private String parentPath;

  /** 所属公司名称 */
  @Column(name = "ORG_ACCOUNT_NAME")
  private String orgAccountName;

  /** 所属公司seeyon client id */
  @Column(name = "SEEYON_CLIENT_ID")
  private String seeyonClientId;

  /** 流水ID */
  @Column(name = "UUID")
  private String uuid;

  /** 拉取时间 */
  @Column(name = "SEEYON_FETCH_TIME")
  private LocalDateTime seeyonFetchTime;

  /** 期待数据时间 */
  @Column(name = "SEEYON_DATA_TIME")
  private LocalDateTime seeyonDataTime;

  public static final String ID = "ID";

  public static final String ORG_ACCOUNT_ID = "ORG_ACCOUNT_ID";

  public static final String NAME = "NAME";

  public static final String CODE = "CODE";

  public static final String CREATE_TIME = "CREATE_TIME";

  public static final String UPDATE_TIME = "UPDATE_TIME";

  public static final String SORT_ID = "SORT_ID";

  public static final String IS_DELETED = "IS_DELETED";

  public static final String ENABLED = "ENABLED";

  public static final String EXTERNAL_TYPE = "EXTERNAL_TYPE";

  public static final String STATUS = "STATUS";

  public static final String DESCRIPTION = "DESCRIPTION";

  public static final String PATH = "PATH";

  public static final String SHORT_NAME = "SHORT_NAME";

  public static final String SECOND_NAME = "SECOND_NAME";

  public static final String IS_INTERNAL = "IS_INTERNAL";

  public static final String IS_GROUP = "IS_GROUP";

  public static final String LEVEL_SCOPE = "LEVEL_SCOPE";

  public static final String TYPE = "TYPE";

  public static final String SUPERIOR = "SUPERIOR";

  public static final String SUPERIOR_NAME = "SUPERIOR_NAME";

  public static final String SORT_ID_TYPE = "SORT_ID_TYPE";

  public static final String LEVEL = "LEVEL";

  public static final String CUSTOMER_PROPERTIES = "CUSTOMER_PROPERTIES";

  public static final String DEP_MANAGER = "DEP_MANAGER";

  public static final String DEP_ADMIN = "DEP_ADMIN";

  public static final String WHOLE_NAME = "WHOLE_NAME";

  public static final String ENTITY_TYPE = "ENTITY_TYPE";

  public static final String VALID = "VALID";

  public static final String GROUP = "GROUP";

  public static final String SUPERIOR0 = "SUPERIOR0";

  public static final String CUSTOM_LOGIN = "CUSTOM_LOGIN";

  public static final String CUSTOM_LOGIN_URL = "CUSTOM_LOGIN_URL";

  public static final String PARENT_PATH = "PARENT_PATH";

  public static final String ORG_ACCOUNT_NAME = "ORG_ACCOUNT_NAME";

  public static final String SEEYON_CLIENT_ID = "SEEYON_CLIENT_ID";

  public static final String UUID = "UUID";

  public static final String SEEYON_FETCH_TIME = "SEEYON_FETCH_TIME";

  public static final String SEEYON_DATA_TIME = "SEEYON_DATA_TIME";

  public Boolean getDeleted() {
    return isDeleted;
  }

  public void setDeleted(Boolean deleted) {
    isDeleted = deleted;
  }

  public Boolean getInternal() {
    return isInternal;
  }

  public void setInternal(Boolean internal) {
    isInternal = internal;
  }

  public Boolean getGroup() {
    return isGroup;
  }

  public void setGroup(Boolean group) {
    isGroup = group;
  }
}

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
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Table(name="seeyon_org_employee")
public class SeeyonOrgEmployee extends SuperModel {

  private static final long serialVersionUID = 1L;

  /** 流水号 */
  @Column(name = "UUID")
  private String uuid;

  /** 数据拉取时间 */
  @Column(name = "SEEYON_FETCH_TIME")
  private LocalDateTime seeyonFetchTime;

  /** 致远系统client UUID */
  @Column(name = "SEEYON_CLIENT_ID")
  private String seeyonClientId;

  /** 主键 id */
  @Id
  @Column(name = "ID")
  private Long id;

  /** 主岗单位 */
  @Column(name = "ORG_ACCOUNT_ID")
  private Long orgAccountId;

  /** 姓名 */
  @Column(name = "NAME")
  private String name;

  /** 编号 */
  @Column(name = "CODE")
  private String code;

  /** 创建时间 */
  @Column(name = "CREATE_TIME")
  private LocalDateTime createTime;

  /** 更新时间 */
  @Column(name = "UPDATE_TIME")
  private LocalDateTime updateTime;

  /** 排序号 */
  @Column(name = "SORT_ID")
  private Integer sortId;

  /** 是否被删除 1:删除 0:未删除 */
  @Column(name = "IS_DELETED")
  private Boolean isDeleted;

  /** 是否启用 1:启用 0 停用 */
  @Column(name = "ENABLED")
  private Boolean enabled;

  /** 外部类型 */
  @Column(name = "EXTERNAL_TYPE")
  private Integer externalType;

  /** 枚举：在职/离职/..(此字段作 废) */
  @Column(name = "STATUS")
  private Integer status;

  /** 描述 */
  @Column(name = "DESCRIPTION")
  private String description;

  /** 主岗职务级别 */
  @Column(name = "ORG_LEVEL_ID")
  private Long orgLevelId;

  /** 主岗岗位 */
  @Column(name = "ORG_POST_ID")
  private Long orgPostId;

  /** 主岗单位 */
  @Column(name = "ORG_DEPARTMENT_ID")
  private Long orgDepartmentId;

  /** 枚举：正式/非正式/.. 1：正式 2：非正式 */
  @Column(name = "TYPE")
  private Integer type;

  /** 是否是内部 1:是 0:否 */
  @Column(name = "IS_INTERNAL")
  private Boolean isInternal;

  /** 是否可以登录 1:是 0:否 */
  @Column(name = "IS_LOGINABLE")
  private Boolean isLoginable;

  /** 是否是虚拟账号 1:是 0:否 */
  @Column(name = "IS_VIRTUAL")
  private Boolean isVirtual;

  /** 是否被分配 1:是 0:否 */
  @Column(name = "IS_ASSIGNED")
  private Boolean isAssigned;

  /** 是否是管理员 1:是 0:否 */
  @Column(name = "IS_ADMIN")
  private Boolean isAdmin;

  @Column(name = "IS_VALID")
  private Boolean isValid;

  /** 在职/离职 1：在职 2：离职 */
  @Column(name = "STATE")
  private Integer state;

  /** 第二岗位 */
  @Column(name = "SECOND_POST")
  private String secondPost;

  /** 兼任岗位 */
  @Column(name = "CONCURRENT_POST")
  private String concurrentPost;

  /** 自定义地址列表 */
  @Column(name = "CUSTOMER_ADDRESS_BOOKLIST")
  private String customerAddressBooklist;

  /** 主要语言 */
  @Column(name = "PRIMARY_LANGUANGE")
  private String primaryLanguange;

  /** 拼音 */
  @Column(name = "PINYIN")
  private String pinyin;

  /** 拼音首字母 */
  @Column(name = "PINYINHEAD")
  private String pinyinhead;

  /** 地址 */
  @Column(name = "ADDRESS")
  private String address;

  /** 位置 */
  @Column(name = "LOCATION")
  private String location;

  /** 是否可用 */
  @Column(name = "VALID")
  private Boolean valid;

  /** 汇报 */
  @Column(name = "REPORTER")
  private Integer reporter;

  /** 登录名 */
  @Column(name = "LOGIN_NAME")
  private String loginName;

  /** 邮编 */
  @Column(name = "POSTALCODE")
  private String postalcode;

  /** 微信 */
  @Column(name = "WEIXIN")
  private String weixin;

  /** 微博 */
  @Column(name = "WEIBO")
  private String weibo;

  /** 邮寄地址 */
  @Column(name = "POST_ADDRESS")
  private String postAddress;

  /** 博客 */
  @Column(name = "BLOG")
  private String blog;

  /** 网站 */
  @Column(name = "WEBSITE")
  private String website;

  /** 生日 */
  @Column(name = "BIRTHDAY")
  private String birthday;

  /** 入职日期 */
  @Column(name = "HIREDATE")
  private String hiredate;

  /** ID号 */
  @Column(name = "ID_NUM")
  private String idNum;

  /** 学历 */
  @Column(name = "DEGREE")
  private String degree;

  /** 电话 */
  @Column(name = "TEL_NUMBER")
  private String telNumber;

  /** 工作电话 */
  @Column(name = "OFFICE_NUM")
  private String officeNum;

  /** 邮箱 */
  @Column(name = "EMAIL_ADDRESS")
  private String emailAddress;

  /** 性别 */
  @Column(name = "GENDER")
  private Integer gender;

  /** v5扩展 */
  @Column(name = "V5_EXTERNAL")
  private Boolean v5External;

  /** 复合扩展 */
  @Column(name = "VJOIN_EXTERNAL")
  private Boolean vjoinExternal;

  /** 实体类型 */
  @Column(name = "ENTITY_TYPE")
  private String entityType;

  /** 任职单位名称 */
  @Column(name = "ORG_ACCOUNT_NAME")
  private String orgAccountName;

  /** 任职岗位名称 */
  @Column(name = "ORG_POST_NAME")
  private String orgPostName;

  /** 任职部门名称 */
  @Column(name = "ORG_DEPARTMENT_NAME")
  private String orgDepartmentName;

  /** 机构层级 */
  @Column(name = "ORG_LEVEL_NAME")
  private String orgLevelName;

  /** 期待数据时间 */
  @Column(name = "SEEYON_DATA_TIME")
  private LocalDateTime seeyonDataTime;

  public static final String UUID = "UUID";

  public static final String SEEYON_FETCH_TIME = "SEEYON_FETCH_TIME";

  public static final String SEEYON_CLIENT_ID = "SEEYON_CLIENT_ID";

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

  public static final String ORG_LEVEL_ID = "ORG_LEVEL_ID";

  public static final String ORG_POST_ID = "ORG_POST_ID";

  public static final String ORG_DEPARTMENT_ID = "ORG_DEPARTMENT_ID";

  public static final String TYPE = "TYPE";

  public static final String IS_INTERNAL = "IS_INTERNAL";

  public static final String IS_LOGINABLE = "IS_LOGINABLE";

  public static final String IS_VIRTUAL = "IS_VIRTUAL";

  public static final String IS_ASSIGNED = "IS_ASSIGNED";

  public static final String IS_ADMIN = "IS_ADMIN";

  public static final String IS_VALID = "IS_VALID";

  public static final String STATE = "STATE";

  public static final String SECOND_POST = "SECOND_POST";

  public static final String CONCURRENT_POST = "CONCURRENT_POST";

  public static final String CUSTOMER_ADDRESS_BOOKLIST = "CUSTOMER_ADDRESS_BOOKLIST";

  public static final String PRIMARY_LANGUANGE = "PRIMARY_LANGUANGE";

  public static final String PINYIN = "PINYIN";

  public static final String PINYINHEAD = "PINYINHEAD";

  public static final String ADDRESS = "ADDRESS";

  public static final String LOCATION = "LOCATION";

  public static final String VALID = "VALID";

  public static final String REPORTER = "REPORTER";

  public static final String LOGIN_NAME = "LOGIN_NAME";

  public static final String POSTALCODE = "POSTALCODE";

  public static final String WEIXIN = "WEIXIN";

  public static final String WEIBO = "WEIBO";

  public static final String POST_ADDRESS = "POST_ADDRESS";

  public static final String BLOG = "BLOG";

  public static final String WEBSITE = "WEBSITE";

  public static final String BIRTHDAY = "BIRTHDAY";

  public static final String HIREDATE = "HIREDATE";

  public static final String ID_NUM = "ID_NUM";

  public static final String DEGREE = "DEGREE";

  public static final String TEL_NUMBER = "TEL_NUMBER";

  public static final String OFFICE_NUM = "OFFICE_NUM";

  public static final String EMAIL_ADDRESS = "EMAIL_ADDRESS";

  public static final String GENDER = "GENDER";

  public static final String V5_EXTERNAL = "V5_EXTERNAL";

  public static final String VJOIN_EXTERNAL = "VJOIN_EXTERNAL";

  public static final String ENTITY_TYPE = "ENTITY_TYPE";

  public static final String ORG_ACCOUNT_NAME = "ORG_ACCOUNT_NAME";

  public static final String ORG_POST_NAME = "ORG_POST_NAME";

  public static final String ORG_DEPARTMENT_NAME = "ORG_DEPARTMENT_NAME";

  public static final String ORG_LEVEL_NAME = "ORG_LEVEL_NAME";

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

  public Boolean getLoginable() {
    return isLoginable;
  }

  public void setLoginable(Boolean loginable) {
    isLoginable = loginable;
  }

  public Boolean getVirtual() {
    return isVirtual;
  }

  public void setVirtual(Boolean virtual) {
    isVirtual = virtual;
  }

  public Boolean getAssigned() {
    return isAssigned;
  }

  public void setAssigned(Boolean assigned) {
    isAssigned = assigned;
  }

  public Boolean getAdmin() {
    return isAdmin;
  }

  public void setAdmin(Boolean admin) {
    isAdmin = admin;
  }

  public Boolean getValid() {
    return isValid;
  }

  public void setValid(Boolean valid) {
    isValid = valid;
  }
}

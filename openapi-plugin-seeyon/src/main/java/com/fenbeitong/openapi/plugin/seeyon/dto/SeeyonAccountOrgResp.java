package com.fenbeitong.openapi.plugin.seeyon.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/** Created by dave.hansins on 19/3/5. */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
public class SeeyonAccountOrgResp implements Serializable {

  /**
   * code : 111 type : Department orgAccountId : 7365657164498092000 id : -6404207155090273000 name
   * : 平台领导 createTime : 1283001309000 updateTime : 1508569165000 sortId : 2 isDeleted : false
   * enabled : true externalType : 0 status : 1 description : 5433 path : 000000010020 shortName :
   * 64ttrer secondName : oierjfd isInternal : true isGroup : false levelScope : -1 properties :
   * {"zipCode":"97843","permissionType":1,"acceFAIL_ON_EMPTY_BEANSssLevels":"1","address":"sss","isCustomLoginUrl":"wwdsds","ipAddress":"12.12.234.45","telephone":"29489823","customLoginUrl":"sfsfsa","unitMail":"sfs","chiefLeader":"wrsd","unitCategory":"34","ldapOu":"09lref","fax":"oiwejrfks"}
   * superior : 7365657164498092000 superiorName : sortIdType : 1 level : 0 customerProperties : []
   * depManager : manager_a depAdmin : manager_super wholeName : 平台领导 entityType : Department valid
   * : true group : false superior0 : 7365657164498092000 customLogin : false customLoginUrl :
   * url_test parentPath : 00000001 orgAccountName : 中粮粮谷专业化公司
   */
  @ApiModelProperty(value = "", example = "", required = false)
  private String code;

  @ApiModelProperty(value = "", example = "", required = false)
  private String type;

  @ApiModelProperty(value = "", example = "", required = false)
  private long orgAccountId;

  @ApiModelProperty(value = "", example = "", required = false)
  private long id;

  @ApiModelProperty(value = "", example = "", required = false)
  private String name;

  @ApiModelProperty(value = "", example = "", required = false)
  private long createTime;

  @ApiModelProperty(value = "", example = "", required = false)
  private long updateTime;

  @ApiModelProperty(value = "", example = "", required = false)
  private int sortId;

  @ApiModelProperty(value = "", example = "", required = false)
  private boolean isDeleted;

  @ApiModelProperty(value = "", example = "", required = false)
  private boolean enabled;

  @ApiModelProperty(value = "", example = "", required = false)
  private int externalType;

  @ApiModelProperty(value = "", example = "", required = false)
  private int status;

  @ApiModelProperty(value = "", example = "", required = false)
  private String description;

  @ApiModelProperty(value = "", example = "", required = false)
  private String path;

  @ApiModelProperty(value = "", example = "", required = false)
  private String shortName;

  @ApiModelProperty(value = "", example = "", required = false)
  private String secondName;

  @ApiModelProperty(value = "", example = "", required = false)
  private boolean isInternal;

  @ApiModelProperty(value = "", example = "", required = false)
  private boolean isGroup;

  @ApiModelProperty(value = "", example = "", required = false)
  private int levelScope;

  @ApiModelProperty(value = "", example = "", required = false)
  private PropertiesBean properties;

  @ApiModelProperty(value = "", example = "", required = false)
  private long superior;

  @ApiModelProperty(value = "", example = "", required = false)
  private String superiorName;

  @ApiModelProperty(value = "", example = "", required = false)
  private String sortIdType;

  @ApiModelProperty(value = "", example = "", required = false)
  private int level;

  @ApiModelProperty(value = "", example = "", required = false)
  private String depManager;

  @ApiModelProperty(value = "", example = "", required = false)
  private String depAdmin;

  @ApiModelProperty(value = "", example = "", required = false)
  private String wholeName;

  @ApiModelProperty(value = "", example = "", required = false)
  private String entityType;

  @ApiModelProperty(value = "", example = "", required = false)
  private boolean valid;

  @ApiModelProperty(value = "", example = "", required = false)
  private boolean group;

  @ApiModelProperty(value = "", example = "", required = false)
  private long superior0;

  @ApiModelProperty(value = "", example = "", required = false)
  private boolean customLogin;

  @ApiModelProperty(value = "", example = "", required = false)
  private String customLoginUrl;

  @ApiModelProperty(value = "", example = "", required = false)
  private String parentPath;

  @ApiModelProperty(value = "", example = "", required = false)
  private String orgAccountName;

  @ApiModelProperty(value = "", example = "", required = false)
  private List<?> customerProperties;

  public boolean isDeleted() {
    return isDeleted;
  }

  public void setDeleted(boolean deleted) {
    isDeleted = deleted;
  }

  public boolean isInternal() {
    return isInternal;
  }

  public void setInternal(boolean internal) {
    isInternal = internal;
  }

  public boolean isGroup() {
    return isGroup;
  }

  public void setGroup(boolean group) {
    isGroup = group;
  }

  @JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
  public static class PropertiesBean {
    /**
     * zipCode : 97843 permissionType : 1 accessLevels : 1 address : sss isCustomLoginUrl : wwdsds
     * ipAddress : 12.12.234.45 telephone : 29489823 customLoginUrl : sfsfsa unitMail : sfs
     * chiefLeader : wrsd unitCategory : 34 ldapOu : 09lref fax : oiwejrfks
     */
    @ApiModelProperty(value = "", example = "", required = false)
    private String zipCode;

    @ApiModelProperty(value = "", example = "", required = false)
    private int permissionType;

    @ApiModelProperty(value = "", example = "", required = false)
    private String accessLevels;

    @ApiModelProperty(value = "", example = "", required = false)
    private String address;

    @ApiModelProperty(value = "", example = "", required = false)
    private String isCustomLoginUrl;

    @ApiModelProperty(value = "", example = "", required = false)
    private String ipAddress;

    @ApiModelProperty(value = "", example = "", required = false)
    private String telephone;

    @ApiModelProperty(value = "", example = "", required = false)
    private String customLoginUrl;

    @ApiModelProperty(value = "", example = "", required = false)
    private String unitMail;

    @ApiModelProperty(value = "", example = "", required = false)
    private String chiefLeader;

    @ApiModelProperty(value = "", example = "", required = false)
    private String unitCategory;

    @ApiModelProperty(value = "", example = "", required = false)
    private String ldapOu;

    @ApiModelProperty(value = "", example = "", required = false)
    private String fax;
  }
}

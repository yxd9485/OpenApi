package com.fenbeitong.openapi.plugin.seeyon.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AccountResponse
 *
 * <p>Seeyon Account信息响应
 *
 * @author ivan
 * @version 1.0 Created by ivan on 3/2/19 - 5:52 PM.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeeyonAccountResp {
  //  private String orgAccountId;

  /**
   * orgAccountId : 7365657164498092432 id : 7365657164498092432 name : 中粮粮谷专业化公司 code : 1251
   * createTime : 1524195641000 updateTime : 1545214092000 sortId : 1 isDeleted : false enabled :
   * true externalType : 0 status : 1 description : null path : 00000001 shortName : 粮谷总部 secondName
   * : null isInternal : true isGroup : false levelScope : 1 type : Account properties :
   * {"zipCode":null,"permissionType":0,"accessLevels":null,"address":null,"isCustomLoginUrl":0,"ipAddress":null,"telephone":null,"customLoginUrl":null,"unitMail":null,"chiefLeader":null,"unitCategory":null,"ldapOu":null,"fax":null}
   * superior : -1730833917365171641 superiorName : sortIdType : 1 isCanAccess : true accessIds : []
   * accessScopeLevels : [] entityType : Account valid : true group : false superior0 :
   * -1730833917365171641 customLogin : false customLoginUrl : null parentPath : 0000
   */
  @ApiModelProperty(value = "", example = "", required = false)
  private long orgAccountId;

  private long id;
  private String name;
  private String code;
  private long createTime;
  private long updateTime;
  private int sortId;
  private boolean isDeleted;
  private boolean enabled;
  private int externalType;
  private int status;
  private Object description;
  private String path;
  private String shortName;
  private Object secondName;
  private boolean isInternal;
  private boolean isGroup;
  private int levelScope;
  private String type;
  private PropertiesBean properties;
  private long superior;
  private String superiorName;
  private String sortIdType;
  private boolean isCanAccess;
  private String entityType;
  private boolean valid;
  private boolean group;
  private long superior0;
  private boolean customLogin;
  private Object customLoginUrl;
  private String parentPath;
  private List<?> accessIds;
  private List<?> accessScopeLevels;

  public long getOrgAccountId() {
    return orgAccountId;
  }

  public void setOrgAccountId(long orgAccountId) {
    this.orgAccountId = orgAccountId;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(long createTime) {
    this.createTime = createTime;
  }

  public long getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(long updateTime) {
    this.updateTime = updateTime;
  }

  public int getSortId() {
    return sortId;
  }

  public void setSortId(int sortId) {
    this.sortId = sortId;
  }

  public boolean isIsDeleted() {
    return isDeleted;
  }

  public void setIsDeleted(boolean isDeleted) {
    this.isDeleted = isDeleted;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public int getExternalType() {
    return externalType;
  }

  public void setExternalType(int externalType) {
    this.externalType = externalType;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public Object getDescription() {
    return description;
  }

  public void setDescription(Object description) {
    this.description = description;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getShortName() {
    return shortName;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  public Object getSecondName() {
    return secondName;
  }

  public void setSecondName(Object secondName) {
    this.secondName = secondName;
  }

  public boolean isIsInternal() {
    return isInternal;
  }

  public void setIsInternal(boolean isInternal) {
    this.isInternal = isInternal;
  }

  public boolean isIsGroup() {
    return isGroup;
  }

  public void setIsGroup(boolean isGroup) {
    this.isGroup = isGroup;
  }

  public int getLevelScope() {
    return levelScope;
  }

  public void setLevelScope(int levelScope) {
    this.levelScope = levelScope;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public PropertiesBean getProperties() {
    return properties;
  }

  public void setProperties(PropertiesBean properties) {
    this.properties = properties;
  }

  public long getSuperior() {
    return superior;
  }

  public void setSuperior(long superior) {
    this.superior = superior;
  }

  public String getSuperiorName() {
    return superiorName;
  }

  public void setSuperiorName(String superiorName) {
    this.superiorName = superiorName;
  }

  public String getSortIdType() {
    return sortIdType;
  }

  public void setSortIdType(String sortIdType) {
    this.sortIdType = sortIdType;
  }

  public boolean isIsCanAccess() {
    return isCanAccess;
  }

  public void setIsCanAccess(boolean isCanAccess) {
    this.isCanAccess = isCanAccess;
  }

  public String getEntityType() {
    return entityType;
  }

  public void setEntityType(String entityType) {
    this.entityType = entityType;
  }

  public boolean isValid() {
    return valid;
  }

  public void setValid(boolean valid) {
    this.valid = valid;
  }

  public boolean isGroup() {
    return group;
  }

  public void setGroup(boolean group) {
    this.group = group;
  }

  public long getSuperior0() {
    return superior0;
  }

  public void setSuperior0(long superior0) {
    this.superior0 = superior0;
  }

  public boolean isCustomLogin() {
    return customLogin;
  }

  public void setCustomLogin(boolean customLogin) {
    this.customLogin = customLogin;
  }

  public Object getCustomLoginUrl() {
    return customLoginUrl;
  }

  public void setCustomLoginUrl(Object customLoginUrl) {
    this.customLoginUrl = customLoginUrl;
  }

  public String getParentPath() {
    return parentPath;
  }

  public void setParentPath(String parentPath) {
    this.parentPath = parentPath;
  }

  public List<?> getAccessIds() {
    return accessIds;
  }

  public void setAccessIds(List<?> accessIds) {
    this.accessIds = accessIds;
  }

  public List<?> getAccessScopeLevels() {
    return accessScopeLevels;
  }

  public void setAccessScopeLevels(List<?> accessScopeLevels) {
    this.accessScopeLevels = accessScopeLevels;
  }

  public static class PropertiesBean {
    /**
     * zipCode : null permissionType : 0 accessLevels : null address : null isCustomLoginUrl : 0
     * ipAddress : null telephone : null customLoginUrl : null unitMail : null chiefLeader : null
     * unitCategory : null ldapOu : null fax : null
     */
    private Object zipCode;

    private int permissionType;
    private Object accessLevels;
    private Object address;
    private int isCustomLoginUrl;
    private Object ipAddress;
    private Object telephone;
    private Object customLoginUrl;
    private Object unitMail;
    private Object chiefLeader;
    private Object unitCategory;
    private Object ldapOu;
    private Object fax;

    public Object getZipCode() {
      return zipCode;
    }

    public void setZipCode(Object zipCode) {
      this.zipCode = zipCode;
    }

    public int getPermissionType() {
      return permissionType;
    }

    public void setPermissionType(int permissionType) {
      this.permissionType = permissionType;
    }

    public Object getAccessLevels() {
      return accessLevels;
    }

    public void setAccessLevels(Object accessLevels) {
      this.accessLevels = accessLevels;
    }

    public Object getAddress() {
      return address;
    }

    public void setAddress(Object address) {
      this.address = address;
    }

    public int getIsCustomLoginUrl() {
      return isCustomLoginUrl;
    }

    public void setIsCustomLoginUrl(int isCustomLoginUrl) {
      this.isCustomLoginUrl = isCustomLoginUrl;
    }

    public Object getIpAddress() {
      return ipAddress;
    }

    public void setIpAddress(Object ipAddress) {
      this.ipAddress = ipAddress;
    }

    public Object getTelephone() {
      return telephone;
    }

    public void setTelephone(Object telephone) {
      this.telephone = telephone;
    }

    public Object getCustomLoginUrl() {
      return customLoginUrl;
    }

    public void setCustomLoginUrl(Object customLoginUrl) {
      this.customLoginUrl = customLoginUrl;
    }

    public Object getUnitMail() {
      return unitMail;
    }

    public void setUnitMail(Object unitMail) {
      this.unitMail = unitMail;
    }

    public Object getChiefLeader() {
      return chiefLeader;
    }

    public void setChiefLeader(Object chiefLeader) {
      this.chiefLeader = chiefLeader;
    }

    public Object getUnitCategory() {
      return unitCategory;
    }

    public void setUnitCategory(Object unitCategory) {
      this.unitCategory = unitCategory;
    }

    public Object getLdapOu() {
      return ldapOu;
    }

    public void setLdapOu(Object ldapOu) {
      this.ldapOu = ldapOu;
    }

    public Object getFax() {
      return fax;
    }

    public void setFax(Object fax) {
      this.fax = fax;
    }
  }
}

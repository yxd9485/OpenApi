package com.fenbeitong.openapi.plugin.seeyon.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * 致远单位DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
public class SeeyonAccountOrgListResp implements Serializable {

  @ApiModelProperty(value = "", example = "", required = false)
  private String code;

  @ApiModelProperty(value = "", example = "", required = false)
  private String fullName;

  @ApiModelProperty(value = "", example = "", required = false)
  private String orgAccountId;

  @ApiModelProperty(value = "", example = "", required = false)
  private String shortName;

  @ApiModelProperty(value = "", example = "", required = false)
  private boolean isGroup;

  @ApiModelProperty(value = "", example = "", required = false)
  private String superior;

  @ApiModelProperty(value = "", example = "", required = false)
  private String id;

  @ApiModelProperty(value = "", example = "", required = false)
  private String name;

  @Override
  public String toString() {
    return "SeeyonAccountOrgListResp{" +
            "code='" + code + '\'' +
            ", fullName='" + fullName + '\'' +
            ", orgAccountId='" + orgAccountId + '\'' +
            ", shortName='" + shortName + '\'' +
            ", isGroup=" + isGroup +
            ", superior='" + superior + '\'' +
            ", id='" + id + '\'' +
            ", name='" + name + '\'' +
            '}';
  }

  public static class Builder{
    public static SeeyonAccountOrgListResp build(SeeyonAccountResp seeyonAccountResp){
      SeeyonAccountOrgListResp seeyonAccountOrgListResp = new SeeyonAccountOrgListResp();
      BeanUtils.copyProperties(seeyonAccountResp,seeyonAccountOrgListResp);
      seeyonAccountOrgListResp.setId(seeyonAccountResp.getId()+"");
      seeyonAccountOrgListResp.setSuperior(seeyonAccountResp.getSuperior()+"");
      seeyonAccountOrgListResp.setOrgAccountId(seeyonAccountResp.getOrgAccountId()+"");
      return seeyonAccountOrgListResp;
    }
  }
}

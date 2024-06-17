package com.fenbeitong.openapi.plugin.seeyon.transformer;

import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountOrgResp;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonApiOrgRequest;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonClient;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonOrgDepartment;

/**
 * OrgTransformer
 *
 * <p>Seeyon组织机构数据包装为OpenApi组织机构请求
 *
 * @author ivan
 * @version 1.0 Created by ivan on 3/6/19 - 3:46 PM.
 */
public class SeeyonOrgApiWrapper {
  /**
   * @author Create by Ivan on 18:58 2019/3/21
   *     <p>组织机构创建
   * @param seeyonClient : 公司信息
   * @param accountOrgResponse : 组织机构信息
   * @return com.fenbeitong.openapi.seeyon.invoker.api.seeyon.openapi.model.dto.ApiOrgRequest
   */
  public static SeeyonApiOrgRequest createOrg(
          SeeyonClient seeyonClient, SeeyonAccountOrgResp accountOrgResponse) {
    return SeeyonApiOrgRequest.builder()
        .companyId(seeyonClient.getOpenapiAppId())
        .operatorId("OPEN-API")
        .orgUnitName(accountOrgResponse.getName())
        .thirdOrgId(accountOrgResponse.getId() + "")
        .thirdParentId(accountOrgResponse.getSuperior() + "")
        .build();
  }
  /**
   * @author Create by Ivan on 18:58 2019/3/21
   *     <p>组织机构删除
   * @param seeyonClient : 公司信息
   * @param accountOrgResponse : 组织机构信息
   * @return com.fenbeitong.openapi.seeyon.invoker.api.seeyon.openapi.model.dto.ApiOrgRequest
   */
  public static SeeyonApiOrgRequest delOrg(
      SeeyonClient seeyonClient, SeeyonAccountOrgResp accountOrgResponse) {
    return SeeyonApiOrgRequest.builder()
        .companyId(seeyonClient.getOpenapiAppId())
        .thirdOrgId(accountOrgResponse.getId() + "")
        .build();
  }

  /**
   * @author Create by Ivan on 18:58 2019/3/21
   *     <p>组织机构删除
   * @param seeyonClient : 公司信息
   * @param seeyonOrgDepartment : 储存组织机构信息
   * @return com.fenbeitong.openapi.seeyon.invoker.api.seeyon.openapi.model.dto.ApiOrgRequest
   */
  public static SeeyonApiOrgRequest delOrg(
      SeeyonClient seeyonClient, SeeyonOrgDepartment seeyonOrgDepartment) {
    return SeeyonApiOrgRequest.builder()
        .companyId(seeyonClient.getOpenapiAppId())
        .thirdOrgId(seeyonOrgDepartment.getId() + "")
        .build();
  }

  /**
   * @author Create by Ivan on 18:58 2019/3/21
   *     <p>组织机构更新
   * @param seeyonClient : 公司信息
   * @param accountOrgResponse : 组织机构信息
   * @return com.fenbeitong.openapi.seeyon.invoker.api.seeyon.openapi.model.dto.ApiOrgRequest
   */
  public static SeeyonApiOrgRequest updateOrg(
      SeeyonClient seeyonClient, SeeyonAccountOrgResp accountOrgResponse) {
    return SeeyonApiOrgRequest.builder()
        .companyId(seeyonClient.getOpenapiAppId())
        .operatorId("OPEN-API")
        .orgUnitName(accountOrgResponse.getName())
        .thirdOrgId(accountOrgResponse.getId() + "")
        .thirdParentId(accountOrgResponse.getSuperior() + "")
        .build();
  }
}

package com.fenbeitong.openapi.plugin.seeyon.transformer;

import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountOrgResp;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonOrgDepartment;
import com.fenbeitong.openapi.plugin.seeyon.helper.BeanHelper;
import com.fenbeitong.openapi.plugin.seeyon.helper.Jsr310DateHelper;
import com.fenbeitong.openapi.plugin.seeyon.utils.IdGenerator;
import com.fenbeitong.openapi.plugin.util.JsonUtils;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * SeeyonOrgDepartmentTransformer
 *
 * <p>组织机构储存数据包装
 *
 * @author ivan
 * @version 1.0 Created by ivan on 3/5/19 - 6:12 PM.
 */
public class SeeyonOrgDepartmentTransformer {
  /**
   * @author Create by Ivan on 19:01 2019/3/21
   *     <p>根据拉取数据构造存储数据
   * @param accountOrgResponse :
   * @param seeyonClientId :
   * @param seeyonFetchTime :
   * @return
   *     com.fenbeitong.openapi.seeyon.invoker.api.seeyon.department.model.domain.SeeyonOrgDepartment
   */
  public static SeeyonOrgDepartment createFromIncome(
          SeeyonAccountOrgResp accountOrgResponse,
          @NotNull String seeyonClientId,
          @NotNull LocalDateTime seeyonFetchTime,
          Long compareDaysGap) {

    SeeyonOrgDepartment seeyonOrgDepartment =
        BeanHelper.beanToBean(accountOrgResponse, SeeyonOrgDepartment.class);
    seeyonOrgDepartment.setSeeyonClientId(seeyonClientId);
    seeyonOrgDepartment.setSeeyonFetchTime(seeyonFetchTime);
    seeyonOrgDepartment.setSeeyonDataTime(seeyonFetchTime.minusDays(compareDaysGap));
    seeyonOrgDepartment.setUuid(IdGenerator.getId32bit());
    seeyonOrgDepartment.setCreateTime(
        Jsr310DateHelper.getDateTimeOfTimestamp(accountOrgResponse.getCreateTime()));
    seeyonOrgDepartment.setUpdateTime(
        Jsr310DateHelper.getDateTimeOfTimestamp(accountOrgResponse.getUpdateTime()));
    seeyonOrgDepartment.setCustomerProperties(
        JsonUtils.toJson(accountOrgResponse.getCustomerProperties()));
    return seeyonOrgDepartment;
  }
}

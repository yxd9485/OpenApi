package com.fenbeitong.openapi.plugin.seeyon.transformer;

import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountEmpResp;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonOrgEmployee;
import com.fenbeitong.openapi.plugin.seeyon.helper.BeanHelper;
import com.fenbeitong.openapi.plugin.seeyon.helper.JacksonHelper;
import com.fenbeitong.openapi.plugin.seeyon.helper.Jsr310DateHelper;
import com.fenbeitong.openapi.plugin.seeyon.utils.IdGenerator;
import com.fenbeitong.openapi.plugin.util.JsonUtils;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * SeeyonOrgEmployeeTransformer
 *
 * <p>人员储存数据包装
 *
 * @author ivan
 * @version 1.0 Created by ivan on 3/5/19 - 6:56 PM.
 */
public class SeeyonOrgEmployeeTransformer {
  /**
   * @author Create by Ivan on 19:03 2019/3/21
   *     <p>根据拉取数据构造存储数据
   * @param accountEmpResponse :
   * @param seeyonClientId :
   * @param seeyonFetchTime :
   * @return
   *     com.fenbeitong.openapi.seeyon.invoker.api.seeyon.employee.model.domain.SeeyonOrgEmployee
   */
  public static SeeyonOrgEmployee createFromIncome(
      SeeyonAccountEmpResp accountEmpResponse,
      @NotNull String seeyonClientId,
      @NotNull LocalDateTime seeyonFetchTime,
      Long compareDaysGap) {

    SeeyonOrgEmployee seeyonOrgEmployee =
        BeanHelper.beanToBean(accountEmpResponse, SeeyonOrgEmployee.class);
    seeyonOrgEmployee.setSeeyonClientId(seeyonClientId);
    seeyonOrgEmployee.setSeeyonFetchTime(seeyonFetchTime);
    seeyonOrgEmployee.setSeeyonDataTime(seeyonFetchTime.minusDays(compareDaysGap));
    seeyonOrgEmployee.setUuid(IdGenerator.getId32bit());
    seeyonOrgEmployee.setCreateTime(
        Jsr310DateHelper.getDateTimeOfTimestamp(accountEmpResponse.getCreateTime()));
    seeyonOrgEmployee.setUpdateTime(
        Jsr310DateHelper.getDateTimeOfTimestamp(accountEmpResponse.getUpdateTime()));
    seeyonOrgEmployee.setSecondPost(JsonUtils.toJson(accountEmpResponse.getSecondPost()));
    seeyonOrgEmployee.setConcurrentPost(
        JacksonHelper.toJson(accountEmpResponse.getConcurrentPost()));
    seeyonOrgEmployee.setCustomerAddressBooklist(
        JacksonHelper.toJson(accountEmpResponse.getCustomerAddressBooklist()));
    return seeyonOrgEmployee;
  }
}

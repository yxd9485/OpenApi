package com.fenbeitong.openapi.plugin.ecology.v8.common;

import com.fenbeitong.openapi.plugin.util.DateUtils;
import lombok.Data;

import java.util.Map;

/**
 * <p>Title: SipaiSyncWorkflowReq</p>
 * <p>Description: 泛微工作流请求</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/4/22 7:43 PM
 */
@Data
public class EcologySyncWorkflowReq {

    private Long configId;

    private String companyId;

    private String createDate;

    private Map<String, Integer> applyNameMapping;

    public String getCreateDate() {
        return createDate == null ? DateUtils.toSimpleStr(DateUtils.now(), true) : createDate;
    }
}

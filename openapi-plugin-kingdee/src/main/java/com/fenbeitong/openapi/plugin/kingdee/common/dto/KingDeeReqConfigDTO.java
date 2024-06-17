package com.fenbeitong.openapi.plugin.kingdee.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.kingdee.common.listener.KingDeekListener;
import com.fenbeitong.openapi.plugin.kingdee.support.entity.OpenKingdeeReqData;
import com.fenbeitong.openapi.plugin.kingdee.support.entity.OpenKingdeeUrlConfig;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * <p>Title: ProjectListDTo</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2020-08-26 14:56
 */
@Data
@Builder
public class KingDeeReqConfigDTO {
    List<OpenKingdeeReqData> openKingdeeReqDataList;
    KingDeekListener kingDeekListener;
    OpenKingdeeUrlConfig openKingdeeUrlConfig;
    String token;

}

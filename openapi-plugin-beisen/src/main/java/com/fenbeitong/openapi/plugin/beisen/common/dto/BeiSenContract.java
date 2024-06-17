package com.fenbeitong.openapi.plugin.beisen.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: BeisenResultEntity</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaowei
 * @date 2020/08/03 4:53 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BeiSenContract {
    public Integer userID;
    public String code;
    public String firstPartyCode;
}

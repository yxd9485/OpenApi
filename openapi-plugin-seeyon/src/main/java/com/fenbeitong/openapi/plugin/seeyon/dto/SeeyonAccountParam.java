package com.fenbeitong.openapi.plugin.seeyon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeeyonAccountParam {
    private String orgName;
    private String accountCode;
    private String orgAccountId;
}

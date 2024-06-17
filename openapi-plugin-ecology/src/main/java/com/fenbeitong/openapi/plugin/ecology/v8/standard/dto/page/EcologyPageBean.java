package com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.page;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author ctl
 * @date 2021/11/12
 */
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class EcologyPageBean<T> implements Serializable {

    @JsonProperty("totalSize")
    private Integer totalSize;
    @JsonProperty("dataList")
    private List<T> dataList;
    @JsonProperty("pageSize")
    private Integer pageSize;
    @JsonProperty("page")
    private Integer page;

}

package com.fenbeitong.openapi.plugin.customize.wawj.dto;

import lombok.Data;

import java.util.List;

/**
 * <p>Title: WawjBaoXiaoReq</p>
 * <p>Description: 我爱我家报销请求参数</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/12/5 10:43 AM
 */
@Data
public class WawjBaoXiaoReq<T> {

    private List<T> result;
}

package com.fenbeitong.openapi.plugin.welink.isv.dto;

import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
import com.fenbeitong.usercenter.api.model.dto.menu.MenuInfoVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 企业微信服务商后台登录信息
 * Created by log.chang on 2020/3/25.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeLinkIsvWebLoginInfoRespDTO {

    LoginResVO loginResVO;
    List<MenuInfoVo> menuInfoVoList;

}

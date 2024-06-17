package com.fenbeitong.openapi.plugin.feishu.isv.dto;

import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
import com.fenbeitong.usercenter.api.model.dto.menu.MenuInfoVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 服务商后台登录信息
 *
 * @author log.chang
 * @date 2020/3/25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeiShuIsvWebLoginInfoRespDTO {

    LoginResVO loginResVO;
    List<MenuInfoVo> menuInfoVoList;

}

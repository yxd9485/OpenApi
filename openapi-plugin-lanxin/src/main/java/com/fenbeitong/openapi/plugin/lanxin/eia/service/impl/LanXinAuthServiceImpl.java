package com.fenbeitong.openapi.plugin.lanxin.eia.service.impl;

import com.fenbeitong.finhub.common.constant.CompanyLoginChannelEnum;
import com.fenbeitong.openapi.plugin.lanxin.common.constant.LanXInResponseCode;
import com.fenbeitong.openapi.plugin.lanxin.common.dao.LanxinCorpDao;
import com.fenbeitong.openapi.plugin.lanxin.common.dto.response.LanXinBaseDTO;
import com.fenbeitong.openapi.plugin.lanxin.common.dto.response.LanXinUserInfoDTO;
import com.fenbeitong.openapi.plugin.lanxin.common.entity.LanxinCorp;
import com.fenbeitong.openapi.plugin.lanxin.common.exception.OpenApiLanXinException;
import com.fenbeitong.openapi.plugin.lanxin.common.service.LanXinService;
import com.fenbeitong.openapi.plugin.lanxin.eia.service.LanXinAuthService;
import com.fenbeitong.openapi.plugin.support.apply.constant.ItemCodeEnum;
import com.fenbeitong.openapi.plugin.support.employee.dto.UcFetchEmployInfoReqDto;
import com.fenbeitong.openapi.plugin.support.flexible.dao.OpenMsgSetupDao;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>Title: LanXinAuthServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021/12/6 4:27 下午
 */
@Slf4j
@Service
@ServiceAspect
public class LanXinAuthServiceImpl implements LanXinAuthService {
    @Autowired
    LanxinCorpDao lanxinCorpDao;
    @Autowired
    private OpenEmployeeServiceImpl openEmployeeService;
    @Autowired
    private OpenMsgSetupDao openMsgSetupDao;
    @Autowired
    LanXinService lanXinService;

    @Override
    public LoginResVO getLoginUser(String corpId, String authCode) {
        log.info("蓝信用户登录请求：corpId: {}, authCode: {}", corpId, authCode);
        LanxinCorp lanxinCorp = lanxinCorpDao.selectByAppId(corpId);
        // 1.检查企业状态,只有已完成数据初始化的企业才可以登录
        if (lanxinCorp == null || lanxinCorp.getState() != 1) {
            throw new OpenApiLanXinException(LanXInResponseCode.CORP_INALID);
        }
        UcFetchEmployInfoReqDto ucFetchEmployInfoReqDto = new UcFetchEmployInfoReqDto();
        ucFetchEmployInfoReqDto.setCompanyId(lanxinCorp.getCompanyId());
        // 2.根据corpId及授权码获取登录用户ID
        LanXinBaseDTO<LanXinUserInfoDTO> lanXinUserInfoDTO = lanXinService.getUserInfo(lanxinCorp, authCode);
        // 有数据用手机号免登，无数据用三方ID免登，默认三方ID免登
        int count = openMsgSetupDao.countByCompanyIdAndItemCode(lanxinCorp.getCompanyId(), ItemCodeEnum.LAN_XIN_FREE.getCode());
        if (count > 0) {
            ucFetchEmployInfoReqDto.setPhone(lanXinUserInfoDTO.getData().getMobilePhone().getNumber());
        } else {
            ucFetchEmployInfoReqDto.setEmployeeId(lanXinUserInfoDTO.getData().getStaffId());
        }
        return openEmployeeService.fetchLoginAuthInfoByPhoneNum(ucFetchEmployInfoReqDto, CompanyLoginChannelEnum.LANXIN_EMBED);
    }


}

package com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl;

import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingTalkNoticeService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkCorpService;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.voucher.service.impl.GrantVoucherServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * <p>Title: DingtalkGrantVoucherServiceImpl</p>
 * <p>Description: 钉钉考勤发券</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/4/17 6:13 PM
 */
@ServiceAspect
@Service
public class DingtalkGrantVoucherServiceImpl extends GrantVoucherServiceImpl {

    @Autowired
    private IDingTalkNoticeService dingTalkNoticeService;

    @Autowired
    private IDingtalkCorpService dingtalkCorpService;

    @Override
    protected void sendMsgIfNonExistentEmployees(String companyId, List<String> nonExistentEmployees) {
        if (!ObjectUtils.isEmpty(nonExistentEmployees)) {
            PluginCorpDefinition corpDefinition = dingtalkCorpService.getByCompanyId(companyId);
            dingTalkNoticeService.sendMsg(corpDefinition.getThirdCorpId(), "通知:分贝通App中未找到以下用户，将无法统计考勤及发放加班补贴，请注意查看。分贝通员工id:" + String.join(",", nonExistentEmployees) + "。");
        }
    }
}

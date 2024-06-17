package com.fenbeitong.openapi.plugin.dingtalk.yida.service.impl;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingTalkNoticeService;
import com.fenbeitong.openapi.plugin.dingtalk.yida.constant.YiDaFormTypeEnum;
import com.fenbeitong.openapi.plugin.dingtalk.yida.service.IYiDaFormDetailDispatchService;
import com.fenbeitong.openapi.plugin.dingtalk.yida.service.IYiDaFormSyncService;
import com.fenbeitong.openapi.plugin.support.company.dao.PluginCorpDefinitionDao;
import com.fenbeitong.openapi.plugin.support.company.entity.PluginCorpDefinition;
import com.fenbeitong.openapi.plugin.support.company.service.impl.UcCompanyServiceImpl;
import com.fenbeitong.openapi.plugin.support.util.ExceptionRemind;
import com.fenbeitong.openapi.plugin.support.util.SignTool;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * 宜搭表单详情业务调度类
 *
 * @author ctl
 * @date 2022/3/4
 */
@Service
@ServiceAspect
@Slf4j
public class YiDaFormDetailDispatchServiceImpl implements IYiDaFormDetailDispatchService {

    @Value("${dingtalk.yida.callback.md5SignKey}")
    private String signKey;

    @Autowired
    private YiDaPaymentFormSyncServiceImpl yiDaPaymentFormSyncService;

    @Autowired
    private ExceptionRemind exceptionRemind;

    @Autowired
    private PluginCorpDefinitionDao pluginCorpDefinitionDao;

    @Autowired
    private UcCompanyServiceImpl ucCompanyService;

    @Autowired
    private IDingTalkNoticeService iDingTalkNoticeService;

    @Override
    public void dispatch(Map<String, Object> params) {
        String companyId = "";
        String companyName = "";
        String corpId = "";
        YiDaFormTypeEnum yiDaFormTypeEnum = YiDaFormTypeEnum.DEFAULT;
        try {
            if (ObjectUtils.isEmpty(params)) {
                throw new FinhubException(9999, "宜搭表单参数为空");
            }
            corpId = StringUtils.obj2str(params.get("corpId"));
            if (StringUtils.isBlank(corpId)) {
                throw new FinhubException(9999, "[corpId]不能为空");
            }
            PluginCorpDefinition dingtalkCorp = pluginCorpDefinitionDao.getCorpByThirdCorpId(corpId);
            if (dingtalkCorp == null) {
                throw new FinhubException(9999, "[dingtalk_corp]表中不存在对应的配置，corpId:" + corpId);
            }
            companyId = dingtalkCorp.getAppId();
            companyName = ucCompanyService.getCompanyName(companyId);
            int formType = NumericUtils.obj2int(params.get("applyType"));
            yiDaFormTypeEnum = YiDaFormTypeEnum.getEnumByType(formType);
            // 根据类型选择具体的业务执行
            IYiDaFormSyncService targetService = getTargetService(yiDaFormTypeEnum);
            if (targetService == null) {
                throw new FinhubException(9999, "不正确的业务类型:[" + formType + "]");
            }
            // 执行业务
            targetService.execute(params, companyId);
        } catch (Exception e) {
            String errMsg = StringUtils.isBlank(e.getMessage()) ? e.toString() : e.getMessage();
            try {
                // 失败通知
                noticeError(params, companyId, companyName, corpId, yiDaFormTypeEnum, errMsg);
            } catch (Exception ex) {
                log.warn("宜搭表单同步失败时发送通知失败", e);
            }
            throw new FinhubException(9999, "公司id:" + companyId + ",宜搭表单同步失败,业务类型:" + yiDaFormTypeEnum.getDesc() + ",原因:" + errMsg);
        }
    }

    /**
     * 业务失败通知
     *
     * @param params
     * @param companyId
     * @param companyName
     * @param corpId
     * @param yiDaFormTypeEnum
     * @param errMsg
     */
    private void noticeError(Map<String, Object> params, String companyId, String companyName, String corpId, YiDaFormTypeEnum yiDaFormTypeEnum, String errMsg) {
        if (StringUtils.isBlank(errMsg)) {
            // 如果错误信息为空 未知
            errMsg = "未知原因";
        } else {
            // 如果错误信息大于100字符 截取前100字符
            if (errMsg.length() > 100) {
                errMsg = errMsg.substring(0, 100);
            }
        }
        // 发送到业务异常报警群
        String msg = String.format("公司id【%s】\n公司名称【%s】\n宜搭表单同步失败 \n业务类型【%s】 \n原因【%s】", companyId, companyName, yiDaFormTypeEnum.getDesc(), errMsg);
        exceptionRemind.remindDingTalk(msg);
        // 发送到发起人的钉钉
        String noticeMsg = String.format("您提交的对公付款申请同步到分贝通失败 \n原因【%s】\n请联系管理员处理", errMsg);
        String thirdEmployeeId = StringUtils.obj2str(params.get("thirdEmployeeId"));
        if (!StringUtils.isBlank(thirdEmployeeId)) {
            iDingTalkNoticeService.sendMsg(corpId, thirdEmployeeId, noticeMsg);
        }
    }

    /**
     * 密钥校验 暂时不用
     *
     * @param params
     */
    private void checkSign(Map<String, Object> params) {
        /*
         * 宜搭密钥逻辑：
         * MD5 签名验证：MD5(32位大写)签名验证，当选择加密时需要额外输入 加密密钥(自定义)，宜搭在发起请求时会使用该密钥和所有入参一起进行加密，
         * 加密的字符串会以默认参数：__signature(流程里的自动节点使用的参数名是: sign ) 传递给目标服务。
         * 目标服务验证签名时需要将所有参数值进行去重（宜搭内部使用TreeSet处理，调用时需要保证服务端参数名不重复）升序排序，
         * 每个参数值之间使用$分隔，并在末尾拼接密钥后进行 MD5 加密，例如：当参数值为123，密钥为test时的加密串为：123$test。
         */
        String signature = StringUtils.obj2str(params.get("__signature"));
        if (StringUtils.isBlank(signature)) {
            throw new FinhubException(9999, "密钥为空");
        }
        params.remove("__signature");
        // 按参数名使用treeSet排序
        TreeSet<String> treeSet = new TreeSet<>();
        for (String key : params.keySet()) {
            if (!ObjectUtils.isEmpty(key)) {
                treeSet.add(key);
            }
        }
        List<String> strList = new ArrayList<>();
        for (String key : treeSet) {
            Object val = params.get(key);
            if (!ObjectUtils.isEmpty(val)) {
                strList.add(val.toString());
            }
        }
        // 转字符串 拼接$ 拼接密钥
        String str = StringUtils.joinStr("$", strList) + "$" + signKey;
        // md5 比较密钥是否一致
        String md5Str = SignTool.md5(str);
        if (!signature.equalsIgnoreCase(md5Str)) {
            throw new FinhubException(9999, "密钥校验失败,外部加密串:[" + signature + "],内部加密串:[" + md5Str + "]");
        }
    }

    /**
     * 根据表单类型获取要执行的业务类
     *
     * @param yiDaFormTypeEnum
     * @return
     * @see com.fenbeitong.openapi.plugin.dingtalk.yida.constant.YiDaFormTypeEnum
     */
    public IYiDaFormSyncService getTargetService(YiDaFormTypeEnum yiDaFormTypeEnum) {
        if (yiDaFormTypeEnum == null) {
            return null;
        }
        // 增加新的业务时 增加枚举 增加实现类即可
        switch (yiDaFormTypeEnum) {
            case PAYMENT:
                return yiDaPaymentFormSyncService;
            default:
                return null;
        }
    }

}

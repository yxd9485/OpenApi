package com.fenbeitong.openapi.plugin.voucher.service.impl;

import com.fenbeitong.openapi.plugin.etl.service.impl.DefaultEtlStrategyListener;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.voucher.dto.FinanceConfigDto;
import com.fenbeitong.openapi.plugin.voucher.dto.FinanceGlobalConfigDto;
import com.fenbeitong.openapi.plugin.voucher.utils.FinanceVoucherUtils;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * <p>Title: StandardVoucherStrategyListener</p>
 * <p>Description: 凭证策略类</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/1/4 3:14 PM
 */
@Slf4j
@Component
public class StandardVoucherStrategyListener extends DefaultEtlStrategyListener {

    @Override
    public ExpressRunner getRunner(Object... params) {
        ExpressRunner runner = super.getRunner();
        try {
            runner.addFunctionOfClassMethod("getBizDebtorCourseMappingProject", FinanceVoucherUtils.class.getName(), "getBizDebtorCourseMappingProject",
                    new Class[]{List.class, String.class, String.class, String.class}, null);
            runner.addFunctionOfClassMethod("getBillBizCourseMapping", FinanceVoucherUtils.class.getName(), "getBillBizCourseMapping",
                    new Class[]{FinanceConfigDto.class, List.class, String.class, String.class, String.class, String.class}, null);
            runner.addFunctionOfClassMethod("getBillDebtorCourseMapping", FinanceVoucherUtils.class.getName(), "getBillDebtorCourseMapping",
                    new Class[]{List.class, String.class, String.class, Integer.class}, null);
            runner.addFunctionOfClassMethod("info", log.getClass().getName(), "info",
                    new Class[]{String.class, Object[].class}, null);
        } catch (Exception e) {
        }
        return runner;
    }

    @Override
    public DefaultContext<String, Object> getContext(Object... params) {
        DefaultContext<String, Object> context = super.getContext();
        FinanceGlobalConfigDto globalConfigDto = (FinanceGlobalConfigDto) params[0];
        Map extConfig = globalConfigDto.getExtConfig();
        context.putAll(MapUtils.obj2map(globalConfigDto, false));
        if (!ObjectUtils.isEmpty(extConfig)) {
            context.putAll(extConfig);
        }
        log.info("凭证引擎全局参数:{}", JsonUtils.toJson(context));
        return context;
    }

}

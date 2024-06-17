package com.fenbeitong.openapi.plugin.func.common.impl;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.func.common.FuncValidService;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import jodd.util.StringUtil;
import org.springframework.stereotype.Service;

/**
 * module: 应用模块名称<br/>
 * <p>
 * description: 描述<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/7/8 20:40
 * @since 2.0
 */
@Service
@ServiceAspect
public class FuncValidServiceImpl implements FuncValidService {
    @Override
    public void lengthValid(String str,String name) {
        int length = 200;
        if (StringUtil.isNotBlank(str) && str.length()>length){
            throw new FinhubException(1000001, "[ "+name+" ] 字段长度超出200个字符");
        }
    }
}

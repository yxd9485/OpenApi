package com.fenbeitong.openapi.plugin.ecology.v8.standard.service;

import com.fenbeitong.finhub.common.exception.FinhubException;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dao.OpenEcologyResturlConfigDao;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.todo.request.EcologyCreateToDoReqDTO;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.todo.request.EcologyDeleteToDoReqDTO;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.todo.result.EcologyResultDTO;
import com.fenbeitong.openapi.plugin.ecology.v8.standard.entity.OpenEcologyResturlConfig;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * 泛微 rest 接口
 * @Auther zhang.peng
 * @Date 2021/12/7
 */
@Service
@Slf4j
@ServiceAspect
public class EcologyPostUtils {

    private static final String ECOLOGY_CREATE_OR_UPDATE_TODO_URL = "/rest/ofs/ReceiveRequestInfoByJson";

    private static final String ECOLOGY_DELETE_TODO_URL = "/rest/ofs/ReceiveRequestInfoByJson";

    private static final String SUCCESS_CODE = "1";

    @Autowired
    private OpenEcologyResturlConfigDao openEcologyResturlConfigDao;

    public boolean createToDo( EcologyCreateToDoReqDTO createToDoReqDTO , String companyId ) {
        OpenEcologyResturlConfig resturlConfig = openEcologyResturlConfigDao.findListOpenEcologyResturlConfig(companyId);
        if ( null == resturlConfig ){
            log.info(" 未配置 url , companyId {}  ",companyId);
            return false;
        }
        String address = resturlConfig.getDomainName() + ECOLOGY_CREATE_OR_UPDATE_TODO_URL;
        log.info("创建泛微待办参数 {}",JsonUtils.toJson(createToDoReqDTO));
        String result = RestHttpUtils.postJson( address, JsonUtils.toJson(createToDoReqDTO));
        EcologyResultDTO ecologyResultDTO = JsonUtils.toObj(result,EcologyResultDTO.class);
        log.info("创建泛微待办结果 {}",JsonUtils.toJson(result));
        if ( ObjectUtils.isEmpty(ecologyResultDTO) || !SUCCESS_CODE.equals(ecologyResultDTO.getOperResult()) ) {
            log.info("创建泛微待办异常 {}",JsonUtils.toJson(ecologyResultDTO));
            throw new FinhubException(1, "推送泛微待办异常");
        }
        return true;
    }

    public boolean deleteToDo( EcologyDeleteToDoReqDTO deleteToDoReqDTO , String companyId ) {
        OpenEcologyResturlConfig resturlConfig = openEcologyResturlConfigDao.findListOpenEcologyResturlConfig(companyId);
        if ( null == resturlConfig ){
            log.info(" 未配置 url , companyId {}  ",companyId);
            return false;
        }
        String address = resturlConfig.getDomainName() + ECOLOGY_DELETE_TODO_URL ;
        log.info("删除泛微待办参数 {}",JsonUtils.toJson(deleteToDoReqDTO));
        String result = RestHttpUtils.postJson(address, JsonUtils.toJson(deleteToDoReqDTO));
        log.info("删除泛微待办结果 {}",JsonUtils.toJson(result));
        EcologyResultDTO ecologyResultDTO = JsonUtils.toObj(result,EcologyResultDTO.class);
        if ( ObjectUtils.isEmpty(ecologyResultDTO) || !SUCCESS_CODE.equals(ecologyResultDTO.getOperResult()) ) {
            log.info("删除泛微待办异常 {}",JsonUtils.toJson(ecologyResultDTO));
            throw new FinhubException(1, "删除泛微待办异常");
        }
        return true;
    }

}

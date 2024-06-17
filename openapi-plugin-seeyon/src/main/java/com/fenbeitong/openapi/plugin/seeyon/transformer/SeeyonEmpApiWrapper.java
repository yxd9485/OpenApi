package com.fenbeitong.openapi.plugin.seeyon.transformer;

import com.fenbeitong.openapi.plugin.seeyon.constant.FbOrgEmpConstants;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountEmpResp;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonApiEmpRequest;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonClient;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonExtInfo;
import com.fenbeitong.openapi.plugin.seeyon.entity.SeeyonOrgEmployee;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.ObjUtils;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * EmpApiWrapper
 *
 * <p>Seeyon人员数据包装为OpenApi人员请求
 *
 * @author ivan
 * @version 1.0 Created by ivan on 3/6/19 - 3:50 PM.
 */
public class SeeyonEmpApiWrapper {
    /**
     * @param seeyonClient       : 公司信息
     * @param accountEmpResponse : 人员信息
     * @return com.fenbeitong.openapi.seeyon.invoker.api.seeyon.openapi.model.dto.ApiEmpRequest
     * @author Create by Ivan on 18:56 2019/3/21
     * <p>人员创建
     */
    public static SeeyonApiEmpRequest createEmp(
            SeeyonClient seeyonClient, SeeyonAccountEmpResp accountEmpResponse, SeeyonExtInfo seeyonExtInfo) {
        SeeyonApiEmpRequest build = SeeyonApiEmpRequest.builder()
                .thirdEmployeeId(accountEmpResponse.getId() + "")
                .thirdOrgUnitId(accountEmpResponse.getOrgDepartmentId() + "")
                .name(accountEmpResponse.getName())
                .phone(accountEmpResponse.getTelNumber())
                .role(FbOrgEmpConstants.EMP_ROLE_DEFAULT)
                .build();
        if (!ObjectUtils.isEmpty(seeyonExtInfo)) {//非空情况查询指定权限类型,设置人员分贝通权限字段
            build.setRoleType(seeyonExtInfo.getRoleType());
        }
        if(accountEmpResponse.getGender() == 1 || accountEmpResponse.getGender() == 2){
            build.setGender(accountEmpResponse.getGender());
        }
        return build;
    }

    /**
     * @param seeyonClient       : 公司信息
     * @param accountEmpResponse : 人员信息
     * @return com.fenbeitong.openapi.seeyon.invoker.api.seeyon.openapi.model.dto.ApiEmpRequest
     * @author Create by Ivan on 18:56 2019/3/21
     * <p>人员删除
     */
    public static SeeyonApiEmpRequest delEmp(
            SeeyonClient seeyonClient, SeeyonAccountEmpResp accountEmpResponse) {
        return SeeyonApiEmpRequest.builder().thirdEmployeeId(accountEmpResponse.getId() + "").build();
    }

    /**
     * @param seeyonClient      : 公司信息
     * @param seeyonOrgEmployee : 储存人员信息
     * @return com.fenbeitong.openapi.seeyon.invoker.api.seeyon.openapi.model.dto.ApiEmpRequest
     * @author Create by Ivan on 18:56 2019/3/21
     * <p>人员删除
     */
    public static SeeyonApiEmpRequest delEmp(
            SeeyonClient seeyonClient, SeeyonOrgEmployee seeyonOrgEmployee) {
        return SeeyonApiEmpRequest.builder().thirdEmployeeId(seeyonOrgEmployee.getId() + "").build();
    }

    /**
     * @param seeyonClient       : 公司信息
     * @param accountEmpResponse : 人员信息
     * @return com.fenbeitong.openapi.seeyon.invoker.api.seeyon.openapi.model.dto.ApiEmpRequest
     * @author Create by Ivan on 18:56 2019/3/21
     * <p>人员更新
     */
    public static SeeyonApiEmpRequest updateEmp(
            SeeyonClient seeyonClient, SeeyonAccountEmpResp accountEmpResponse, SeeyonExtInfo seeyonExtInfo) {

        SeeyonApiEmpRequest build = SeeyonApiEmpRequest.builder()
                .thirdEmployeeId(accountEmpResponse.getId() + "")
                .thirdOrgUnitId(accountEmpResponse.getOrgDepartmentId() + "")
                .name(accountEmpResponse.getName())
                .phone(accountEmpResponse.getTelNumber())
                .build();
        if (!ObjectUtils.isEmpty(seeyonExtInfo)) {//非空情况查询指定权限类型,设置人员分贝通权限字段
            build.setRoleType(seeyonExtInfo.getRoleType());
        }
        //设置人员性别属性
        if(accountEmpResponse.getGender() == 1 || accountEmpResponse.getGender() == 2){
            build.setGender(accountEmpResponse.getGender());
        }
        return build;
    }

}

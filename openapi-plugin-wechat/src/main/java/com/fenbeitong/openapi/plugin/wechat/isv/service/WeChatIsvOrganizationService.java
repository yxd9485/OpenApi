package com.fenbeitong.openapi.plugin.wechat.isv.service;

import com.fenbeitong.openapi.plugin.support.organization.AbstractOrganizationService;
import com.fenbeitong.openapi.plugin.support.organization.dto.*;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.wechat.common.WeChatApiResponseCode;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WechatDepartmentListRespDTO;
import com.fenbeitong.openapi.plugin.wechat.common.dto.WechatDepartmentListRespDTO.WechatDepartment;
import com.fenbeitong.openapi.plugin.wechat.common.exception.OpenApiWechatException;
import com.fenbeitong.openapi.plugin.wechat.eia.entity.QywxOrgUnit;
import com.fenbeitong.openapi.plugin.wechat.eia.service.wechat.WeChatOrganizationService;
import com.fenbeitong.openapi.plugin.wechat.isv.entity.WeChatIsvCompany;
import com.fenbeitong.openapi.plugin.wechat.isv.util.WeChatIsvHttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * Created by lizhen on 2020/3/23.
 */
@ServiceAspect
@Service
@Slf4j
public class WeChatIsvOrganizationService extends AbstractOrganizationService {


    //部门列表
    private static final String LIST_DEPARTMENT_URL = "/cgi-bin/department/list?access_token={access_token}&id={id}";

    @Value("${wechat.api-host}")
    private String wechatHost;

    @Autowired
    private WeChatIsvHttpUtils wechatIsvHttpUtil;

    @Autowired
    private WeChatIsvCompanyDefinitionService weChatIsvCompanyDefinitionService;

    @Autowired
    private WeChatOrganizationService wechatOrganizationService;

    @Autowired
    private WeChatIsvEmployeeService weChatIsvEmployeeService;


    /**
     * 全量部门人员同步，用于企业授权和变更授权
     *
     * @param corpId
     */
    public Map<String, Object> syncWechatOrgUnit(String corpId, String companyId) {
        //1.微信拉取部门
        List<WechatDepartment> wechatDepartmentList = getWechatDepartmentList(corpId, "", true);
        //2.分贝通部门
        List<OrgUnitDTO> orgUnitDTOS = super.listFbOrgUnit(companyId);
        //将分贝通部门构建成符合比对特别的。corpId=OrgThirdUnitId的，是根部门。name改成数字。父部门id为空的父部门是根，父部门id为corpId的为二级部门。
        List<QywxOrgUnit> fbOrgUnitList = new ArrayList<>();
        for(OrgUnitDTO orgUnit : orgUnitDTOS) {
            QywxOrgUnit qywxOrgUnit = new QywxOrgUnit();
            qywxOrgUnit.setId((corpId.equals(orgUnit.getOrgThirdUnitId()) || StringUtils.isBlank(orgUnit.getOrgThirdUnitId())) ? 1 : Long.parseLong(orgUnit.getOrgThirdUnitId()));
            qywxOrgUnit.setQywxOrgId((corpId.equals(orgUnit.getOrgThirdUnitId()) || StringUtils.isBlank(orgUnit.getOrgThirdUnitId())) ? 1 : Long.parseLong(orgUnit.getOrgThirdUnitId()));
            qywxOrgUnit.setQywxOrgName((corpId.equals(orgUnit.getOrgThirdUnitId()) || StringUtils.isBlank(orgUnit.getOrgThirdUnitId())) ? "1" : orgUnit.getOrgThirdUnitId());
            if (StringUtils.isBlank(orgUnit.getOrgThirdUnitParentId())) {
                //当前为根部门
                qywxOrgUnit.setQywxParentOrgId(0L);
            } else if(corpId.equals(orgUnit.getOrgThirdUnitParentId())) {
                //父部门是根
                qywxOrgUnit.setQywxParentOrgId(1L);
            } else {
                qywxOrgUnit.setQywxParentOrgId(Long.parseLong(orgUnit.getOrgThirdUnitParentId()));
            }
            fbOrgUnitList.add(qywxOrgUnit);
        }
        //3.同步部门结果
        Map<String, Object> departmentMap = wechatOrganizationService.groupDepartment(wechatDepartmentList, fbOrgUnitList, corpId, companyId);
        return departmentMap;

    }


    /**
     * 获取微信部门列表
     * @param corpId
     * @param id
     * @param initRoot true检查是否有根节点，并自动生成
     * @return
     */
    public List<WechatDepartment> getWechatDepartmentList(String corpId, String id, boolean initRoot) {
        Map<String, String> param = new HashMap<>();
        param.put("id", id);
        String res = wechatIsvHttpUtil.getJsonWithAccessToken(wechatHost + LIST_DEPARTMENT_URL, param, corpId);
        WechatDepartmentListRespDTO departmentListResp = JsonUtils.toObj(res, WechatDepartmentListRespDTO.class);
        if (departmentListResp == null || Optional.ofNullable(departmentListResp.getErrCode()).orElse(-1) != 0) {
            throw new OpenApiWechatException(NumericUtils.obj2int(WeChatApiResponseCode.WECHAT_ISV_CORP_DEPT_IS_NULL));
        }
        if (initRoot) {
            WeChatIsvCompany weChatIsvCompany = weChatIsvCompanyDefinitionService.getByCorpId(corpId);
            return departmentListResp.getIsvDepartmentList(weChatIsvCompany.getCompanyName());
        }
        return departmentListResp.getDepartmentListOriginal();
    }


    /**
     * 清除企业所有组织
     *
     * @param companyId
     */
    public void clearAllOrganization(String companyId, String operatorId) {
        List<OrgUnitDTO> orgUnitDTOS = super.listFbOrgUnit(companyId);
        if (orgUnitDTOS != null && orgUnitDTOS.size() > 0) {
            for (OrgUnitDTO orgUnit : orgUnitDTOS) {
                SupportDeleteOrgUnitReqDTO req = new SupportDeleteOrgUnitReqDTO();
                req.setCompanyId(companyId);
                req.setOperatorId(operatorId);
                req.setThirdOrgId(orgUnit.getOrgThirdUnitId());
                super.deleteDepartment(req);
            }
        }
    }


    public void deleteOrgUnit(List<WeChatOrganizationService.WechatOrgUnitDelete> deleteOrgList) {
        if (!ObjectUtils.isEmpty(deleteOrgList)) {
            log.info("删除部门数量");
            deleteOrgList.forEach(org -> {
                List<SupportDeleteOrgUnitReqDTO> deleteOrgUnitReqList = org.getDeleteOrgUnitReqList();
                deleteOrgUnitReqList.forEach(this::deleteDepartment);
            });
        }
    }

    public void addOrgUnit(String companyId, List<WeChatOrganizationService.WechatOrgUnitAdd> addOrgList) {
        if (!ObjectUtils.isEmpty(addOrgList)) {
            addOrgList.forEach(org -> {
                List<SupportCreateOrgUnitReqDTO> createOrgUnitReqList = org.getCreateOrgUnitReqList();
                for (int i = 0; i < createOrgUnitReqList.size(); i++) {
                    createDepartment(companyId, createOrgUnitReqList.get(i));
                }
            });
        }
    }

    public void updateOrgUnit(String companyId, List<WeChatOrganizationService.WechatOrgUnitUpdate> updateOrgList) {
        if (!ObjectUtils.isEmpty(updateOrgList)) {
            updateOrgList.forEach(org -> {
                List<SupportUpdateOrgUnitReqDTO> updateOrgUnitReqList = org.getUpdateOrgUnitReqList();
                updateOrgUnitReqList.forEach(req -> updateDepartment(companyId, req));
            });
        }
    }

    public void bind(SupportBindOrgUnitReqDTO req) {
        bindDepartment(req);
    }

    public static void main(String[] args) {
        String res = "{\"errcode\":0,\"errmsg\":\"ok\",\"department\":[{\"id\":8652291,\"name\":\"8652291\",\"parentid\":1,\"order\":2138831357},{\"id\":1527011096,\"name\":\"1527011096\",\"parentid\":8652291,\"order\":620472552},{\"id\":1527011093,\"name\":\"1527011093\",\"parentid\":6,\"order\":620472555}]}";
        WechatDepartmentListRespDTO wechatDepartmentListRespDTO = JsonUtils.toObj(res, WechatDepartmentListRespDTO.class);
        //wechatDepartmentListRespDTO.getDepartmentList();
        wechatDepartmentListRespDTO.getIsvDepartmentList("AAA公司");

        System.out.println(JsonUtils.toJson(wechatDepartmentListRespDTO));
    }

}

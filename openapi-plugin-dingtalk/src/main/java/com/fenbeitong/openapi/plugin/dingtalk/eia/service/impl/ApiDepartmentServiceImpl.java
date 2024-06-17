package com.fenbeitong.openapi.plugin.dingtalk.eia.service.impl;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiDepartmentGetRequest;
import com.dingtalk.api.request.OapiDepartmentListParentDeptsByDeptRequest;
import com.dingtalk.api.request.OapiDepartmentListRequest;
import com.dingtalk.api.response.OapiDepartmentGetResponse;
import com.dingtalk.api.response.OapiDepartmentListParentDeptsByDeptResponse;
import com.dingtalk.api.response.OapiDepartmentListResponse;
import com.fenbeitong.finhub.common.utils.CheckUtils;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.dingtalk.eia.dto.DingtalkEiaDepartmentRespDTO;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IApiDepartmentService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IApiTokenService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkEiaOrgService;
import com.fenbeitong.openapi.plugin.dingtalk.eia.service.IDingtalkRouteService;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.google.common.collect.Lists;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.dingtalk.api.response.OapiDepartmentListResponse.Department;

/**
 * <p>Title: ApiDepartmentServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/1/16 12:15 PM
 */
@Slf4j
@ServiceAspect
@Service
public class ApiDepartmentServiceImpl implements IApiDepartmentService {

    @Autowired
    private IApiTokenService dingtalkTokenService;

    @Autowired
    private IDingtalkRouteService dingtalkRouteService;

    @Autowired
    private IDingtalkEiaOrgService dingtalkEiaOrgService;

    @Override
    public List<Department> listDepartment(String corpId) {
        String accessToken = dingtalkTokenService.getAccessToken(corpId);
        String proxyUrl = dingtalkRouteService.getRouteByCorpId(corpId).getProxyUrl();
        DingTalkClient client = new DefaultDingTalkClient(proxyUrl + "/department/list");
        OapiDepartmentListRequest req = new OapiDepartmentListRequest();
        req.setFetchChild(true);
        // 从根目录开始获取
        req.setId("1");
        req.setHttpMethod("GET");
        OapiDepartmentListResponse response = null;
        try {
            log.info("调用钉钉获取部门列表接口开始，参数：{}", JsonUtils.toJson(req));
            response = client.execute(req, accessToken);
            log.info("调用钉钉获取部门列表接口完成，返回结果：{}", response.getBody());
        } catch (ApiException e) {
            log.error("调用钉钉获取部门列表接口异常：", e);
        }
        List<Department> departments = response.getDepartment();
        if (ObjectUtils.isEmpty(departments)) {
            return new ArrayList<>();
        }
        List<Department> sortedDepartments = new ArrayList(departments.size());
        Long[] rootIds = {1L};
        sortDepartment(Arrays.asList(rootIds), departments, sortedDepartments);
        //过滤掉家校通讯录
        return filterJiaXiaoTongXunLu(sortedDepartments);
    }

    @Override
    public OapiDepartmentGetResponse getDepartmentInfo(String accessToken, String proxyUrl, String depId) {
        DingTalkClient client = new DefaultDingTalkClient(proxyUrl + "/department/get");
        OapiDepartmentGetRequest request = new OapiDepartmentGetRequest();
        request.setId(depId);
        try {

            //请求钉钉不要太快 防止超过频率限制
            TimeUnit.MILLISECONDS.sleep(80);
            log.info("调用钉钉获取部门详情接口开始，参数：{}", JsonUtils.toJson(request));
            OapiDepartmentGetResponse response = client.execute(request, accessToken);
            log.info("调用钉钉获取部门详情接口完成，返回结果：{}", response.getBody());
            return response;
        } catch (ApiException e) {
            log.error("调用钉钉获取部门详情接口异常：", e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Department> filterJiaXiaoTongXunLu(List<Department> sortedDepartments) {
        List<Long> needFilterDeptIdList = Lists.newArrayList();
        List<Department> jiaXiaoTongXunLu = sortedDepartments.stream().filter(d -> d.getId() < 0).collect(Collectors.toList());
        if (!ObjectUtils.isEmpty(jiaXiaoTongXunLu)) {
            jiaXiaoTongXunLu.forEach(d -> needFilterDeptIdList.addAll(getAllJiaXiaoSubDeptIdList(d.getId(), sortedDepartments)));
        }
        return ObjectUtils.isEmpty(needFilterDeptIdList)
                ? sortedDepartments
                : sortedDepartments.stream().filter(d -> !needFilterDeptIdList.contains(d.getId())).collect(Collectors.toList());
    }

    private List<Long> getAllJiaXiaoSubDeptIdList(Long id, List<Department> sortedDepartments) {
        List<Long> idList = Lists.newArrayList();
        idList.add(id);
        List<Department> subDeptList = sortedDepartments.stream().filter(d -> NumericUtils.obj2long(d.getParentid(), 0) == id).collect(Collectors.toList());
        if (!ObjectUtils.isEmpty(subDeptList)) {
            subDeptList.forEach(d -> idList.addAll(getAllJiaXiaoSubDeptIdList(d.getId(), sortedDepartments)));
        }
        return idList;
    }

    /**
     * 按照部门级别排序
     * 从一级部门开始，递归查找下级部门
     *
     * @param parentIds         父ID
     * @param departments       部门集合
     * @param sortedDepartments 排序部门集合
     */
    private void sortDepartment(List<Long> parentIds, List<Department> departments, List<Department> sortedDepartments) {
        List<Long> childrenIds = new ArrayList<>();
        for (long id : parentIds) {
            Iterator<Department> iterator = departments.iterator();
            while (iterator.hasNext()) {
                OapiDepartmentListResponse.Department department = iterator.next();
                if (department.getParentid() == id) {
                    sortedDepartments.add(department);
                    childrenIds.add(department.getId());
                    iterator.remove();
                }
            }
        }
        if (!childrenIds.isEmpty()) {
            sortDepartment(childrenIds, departments, sortedDepartments);
        }
    }

    /**
     * 查询钉钉部门的所有上级部门ID，按照级别次序依次排序
     * 假设部门的组织结构如下：
     * 1
     * |->123
     * |->456
     * |->789
     * 当传入部门id为789时，返回的结果按顺序依次为当前部门id及其所有父部门的ID，直到根部门，如[789,456,123,1]
     *
     * @param departmentId 钉钉部门ID
     * @param corpId       corpId
     * @return 父部门ID列表
     */
    @Override
    public List<Long> listParentDeptIds(Long departmentId, String corpId) {
        String accessToken = dingtalkTokenService.getAccessToken(corpId);
        String proxyUrl = dingtalkRouteService.getRouteByCorpId(corpId).getProxyUrl();
        DingTalkClient client = new DefaultDingTalkClient(proxyUrl + "/department/list_parent_depts_by_dept");
        OapiDepartmentListParentDeptsByDeptRequest request = new OapiDepartmentListParentDeptsByDeptRequest();
        request.setId(String.valueOf(departmentId));
        request.setHttpMethod("GET");
        try {
            //请求钉钉不要太快 防止超过频率限制
            TimeUnit.MILLISECONDS.sleep(80);
            log.info("调用钉钉查询父部门ID列表接口开始，参数: {}", JsonUtils.toJson(request));
            OapiDepartmentListParentDeptsByDeptResponse response = client.execute(request, accessToken);
            log.info("调用钉钉查询父部门ID列表接口完成，返回结果: {}", response.getBody());
            return response.getParentIds();
        } catch (ApiException e) {
            log.error("调用钉钉查询父部门ID列表接口异常：", e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Lists.newArrayList();
    }


    @Override
    public List<OapiDepartmentListResponse.Department> listDepartmentRemoveNegative(String corpId) {
        List<OapiDepartmentListResponse.Department> dingtalkDepartments = listDepartment(corpId);
        List<OapiDepartmentListResponse.Department> collect = dingtalkDepartments.stream().filter(e -> e.getId() > 0 && e.getParentid() > 0).collect(Collectors.toList());
        return collect;
    }


    private OapiDepartmentGetResponse getDepartment(String departmentId, String corpId, boolean origin) {

        CheckUtils.create().addCheckEmpty(departmentId, "部门ID不能为空")
                .addCheckEmpty(corpId, "corpId不能为空")
                .check();

        log.info("调用钉钉部门详情接口，参数：departmentId: {}, corpId: {}", departmentId, corpId);

        String accessToken = dingtalkTokenService.getAccessToken(corpId);
        String proxyUrl = dingtalkRouteService.getRouteByCorpId(corpId).getProxyUrl();
        DingTalkClient client = new DefaultDingTalkClient(proxyUrl + "/department/get");
        OapiDepartmentGetRequest req = new OapiDepartmentGetRequest();
        req.setId(departmentId);
        req.setHttpMethod("GET");
        try {
            OapiDepartmentGetResponse response = client.execute(req, accessToken);
            log.info("调用钉钉部门详情接口完成，返回结果：{}", response.getBody());
            if (!origin) {
                if (!response.isSuccess()) {
                    throw new OpenApiDingtalkException(NumericUtils.obj2int(DingtalkResponseCode.DINGTALK_ERROR), response.getErrmsg());
                }
            }
            return response;
        } catch (ApiException e) {
            log.error("调用钉钉获取部门详情接口异常：", e);
            throw new OpenApiDingtalkException(NumericUtils.obj2int(DingtalkResponseCode.DINGTALK_ERROR));
        }
    }

    /**
     * 调用钉钉接口，返回钉钉原始信息，不做异常处理
     *
     * @param departmentId departmentId
     * @param corpId       corpId
     * @return
     */
    @Override
    public OapiDepartmentGetResponse getWithOriginal(String departmentId, String corpId) {
        return getDepartment(departmentId, corpId, true);
    }

    @Override
    public List<Department> getAllDepartmentsByAuth(List<Long> authedDept, String corpId, String companyName) {
        //1.获取授权范围
        List<OapiDepartmentListResponse.Department> departmentAll = new ArrayList<>();
        if (!ObjectUtils.isEmpty(authedDept)) {
            for (Long deptId : authedDept) {
                //2.授权部门子部门
                List<OapiDepartmentListResponse.Department> departmentList = dingtalkEiaOrgService.getDepartmentList(StringUtils.obj2str(deptId), corpId);
                if (!ObjectUtils.isEmpty(departmentList)) {
                    departmentAll.addAll(departmentList);
                }
                //当前授权部门
                OapiDepartmentGetResponse departmentDetail = dingtalkEiaOrgService.getDepartmentDetail(StringUtils.obj2str(deptId), corpId);
                OapiDepartmentListResponse.Department department = new OapiDepartmentListResponse.Department();
                department.setId(departmentDetail.getId());
                department.setName(departmentDetail.getName());
                department.setParentid(departmentDetail.getParentid());
                departmentAll.add(department);
            }
        }
        //3.去重
        List<OapiDepartmentListResponse.Department> distinctList = departmentAll
                .stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getId()))), ArrayList::new))
                .stream().sorted(Comparator.comparing(OapiDepartmentListResponse.Department::getId)).collect(Collectors.toList());
        //4.转换部门（排序，填被父部门、根部门信息）
        DingtalkEiaDepartmentRespDTO dingtalkEiaDepartmentRespDTO = new DingtalkEiaDepartmentRespDTO();
        dingtalkEiaDepartmentRespDTO.setDepartmentInfos(distinctList);
        List<OapiDepartmentListResponse.Department> eiaDepartmentList = dingtalkEiaDepartmentRespDTO.getEiaDepartmentList(companyName);
        return eiaDepartmentList;
    }


}

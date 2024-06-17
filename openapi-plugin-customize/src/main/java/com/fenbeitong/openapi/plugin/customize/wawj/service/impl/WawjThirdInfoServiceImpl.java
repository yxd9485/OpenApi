package com.fenbeitong.openapi.plugin.customize.wawj.service.impl;

import com.fenbeitong.openapi.plugin.customize.wawj.dto.WawjDeptInfoResDTO;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: WawjThirdInfoServiceImpl</p>
 * <p>Description: 我爱我家二级部门设置</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/11/29 2:39 PM
 */
@Service
public class WawjThirdInfoServiceImpl {

    @Value("${host.usercenter}")
    private String userCenter;

    public void setThirdInfo(String companyId, Map<String, Object> thirdInfo) {
        if (ObjectUtils.isEmpty(thirdInfo)) {
            return;
        }
        String bookerDeptId = (String) thirdInfo.get("bookerDeptId");
        if (ObjectUtils.isEmpty(bookerDeptId)) {
            return;
        }
        Map<String, Object> params = Maps.newHashMap();
        params.put("companyId", companyId);
        params.put("orgId", bookerDeptId);
        params.put("type", 2);
        String result = RestHttpUtils.get(userCenter + "/internal/uc/inner/org/info", null, params);
        WawjDeptInfoResDTO resDTO = JsonUtils.toObj(result, WawjDeptInfoResDTO.class);
        if (resDTO != null && resDTO.getData() != null) {
            List<WawjDeptInfoResDTO.ParentDept> parentDeptList = resDTO.getData().getParent_dept_list();
            if (!ObjectUtils.isEmpty(parentDeptList)) {
                Collections.reverse(parentDeptList);
                WawjDeptInfoResDTO.ParentDept secondLevelDept = parentDeptList.size() >= 3 ? parentDeptList.get(2) : null;
                if (secondLevelDept != null) {
                    thirdInfo.put("bookerSummaryDeptId", secondLevelDept.getThird_org_id());
                    thirdInfo.put("bookerSummaryDeptName", secondLevelDept.getName());
                }
                if(parentDeptList.size() == 2){
                    thirdInfo.put("bookerSummaryDeptId", resDTO.getData().getThird_org_id());
                    thirdInfo.put("bookerSummaryDeptName", resDTO.getData().getName());
                }
            }
        }
    }
}

package com.fenbeitong.openapi.plugin.func.company.dto;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <p>Title: FuncBillExtInfoTransformDTO</p>
 * <p>Description: 账单扩展字段转换dto</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/6/8 2:09 PM
 */
@SuppressWarnings("all")
@Data
public class FuncBillExtInfoTransformDTO {

    /**
     * 场景类型
     */
    private String type;

    /**
     * 下单人
     */
    private String employeeId;

    /**
     * 下单部门
     */
    private String deptId;

    /**
     * 使用人
     */
    private String userId;

    /**
     * 使用人部门
     */
    private String userDeptId;

    /**
     * 使用人手机号
     */
    private String userPhone;

    /**
     * 同住人
     */
    private String liveWithUserId;

    /**
     * 同住人部门
     */
    private String liveWithDeptId;

    /**
     * 同住人手机号
     */
    private String liveWithUserPhone;

    /**
     * 费用归属类型
     */
    private String costAttributionCategory;

    /**
     * 费用归属id
     */
    private String costAttributionId;

    /**
     * 多费用归属
     */
    private List<CostAttributionDTO> costAttributionList;

    /**
     * 审批单id
     */
    private String applyId;

    /**
     * 订单审批单id
     */
    private String orderApplyId;

    public List<String> getFbUserIdList() {
        Set<String> userIdList = Sets.newHashSet();
        if (!ObjectUtils.isEmpty(employeeId)) {
            userIdList.add(employeeId);
        }
        //使用人 可能是多个
        if (!ObjectUtils.isEmpty(userId)) {
            userIdList.addAll(Lists.newArrayList(userId.split(",")));
        }
        //同住人 可能是多个
        if (!ObjectUtils.isEmpty(liveWithUserId)) {
            userIdList.addAll(Lists.newArrayList(liveWithUserId.split(",")));
        }
        return new ArrayList<>(userIdList);
    }

    public List<String> getFbDeptIdList() {
        Set<String> deptIdList = Sets.newHashSet();
        if (!ObjectUtils.isEmpty(deptId)) {
            deptIdList.add(deptId);
        }
        //使用人部门 可能是多个
        if (!ObjectUtils.isEmpty(userDeptId)) {
            deptIdList.addAll(Lists.newArrayList(userDeptId.split(",")));
        }
        //同住人部门 可能是多个
        if (!ObjectUtils.isEmpty(liveWithDeptId)) {
            deptIdList.addAll(Lists.newArrayList(liveWithDeptId.split(",")));
        }
        return new ArrayList<>(deptIdList);
    }
}

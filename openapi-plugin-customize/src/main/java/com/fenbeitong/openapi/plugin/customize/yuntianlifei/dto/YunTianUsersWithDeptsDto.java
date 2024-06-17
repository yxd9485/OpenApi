package com.fenbeitong.openapi.plugin.customize.yuntianlifei.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>Title: UsersWithDeptsDto</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2022/4/25 4:17 下午
 */
@NoArgsConstructor
@Data
public class YunTianUsersWithDeptsDto {

    private Integer code;
    private DataBean data;

    @NoArgsConstructor
    @Data
    public static class DataBean {
        private List<UserListBean> userList;
        private List<DeptListBean> deptList;

        @NoArgsConstructor
        @Data
        public static class UserListBean {
            private String bankAccount;
            private String bankName;
            private String deptId;
            private String email;
            private String idCard;
            private String idCode;
            private Integer isDelete;
            private String name;
            private String phone;
            private String sex;
            private String userId;
        }

        @NoArgsConstructor
        @Data
        public static class DeptListBean {
            private String id;
            private String name;
            private String parentId;
            private String personInCharge;
            private String shortName;
            private String status;
            private String treePath;
        }
    }
}

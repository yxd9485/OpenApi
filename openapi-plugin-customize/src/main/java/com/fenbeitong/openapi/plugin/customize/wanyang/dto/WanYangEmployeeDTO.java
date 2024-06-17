package com.fenbeitong.openapi.plugin.customize.wanyang.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName WanYangEmployeeDTO
 * @Description 万洋员工实体对象
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/8/2 下午9:31
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WanYangEmployeeDTO extends PageInfoDTO{

    private List<WyEmployeeDTO> list;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WyEmployeeDTO{
        /**
         * EHR-1.删除0.未删除
         */
        @JsonProperty("IS_DELETED")
        private String IS_DELETED;
        /**
         * EHR-部门主管姓名
         */
        private String depManagerName;
        /**
         * EHR-民族
         */
        private String nation;
        /**
         * EHR-部门分管领导姓名
         */
        private String depLeaderName;
        /**
         * EHR-工龄
         */
        private String workingYears;
        /**
         * EHR-员工岗位
         */
        private String personPostName;
        /**
         * EHR-最高学历
         */
        private String highestEducation;
        /**
         * OA-员工部门ID
         */
        private String personDepID;
        /**
         * EHR-性别
         */
        private String personSex;
        /**
         * EHR-员工编码
         */
        private String personCode;
        /**
         * EHR-专业
         */
        private String major;
        /**
         * EHR-弃用字段
         */
        private String personOrgID;
        /**
         * EHR-登录名
         */
        private String loginName;
        /**
         * EHR-政治面貌
         */
        private String politicalOutlook;
        /**
         * EHR-在职/离职
         */
        private String state;
        /**
         * EHR-部门主管身份证号码
         */
        private String depManagerCardId;
        /**
         * EHR-部门分管领导编号
         */
        private String depLeaderCode;
        /**
         * EHR-1.启用0.停用
         */
        @JsonProperty("IS_ENABLE")
        private String IS_ENABLE;
        /**
         * EHR-部门分管领导身份证号码
         */
        private String depLeaderCardId;
        /**
         * EHR-入职时间
         */
        private String entryTime;
        /**
         * EHR-排序号
         */
        private String sortID;
        /**
         * EHR-籍贯
         */
        private String nativePlace;
        /**
         * EHR-曾用名
         */
        private String usedName;
        /**
         * EHR-业余爱好
         */
        private String hobby;
        /**
         * EHR-时间戳
         */
        private String lastUpdateTime;
        /**
         * EHR-出生日期
         */
        private String birthday;
        /**
         * EHR-内部人员：inPerson 外部人员：outPerson
         */
        private String inOrOutPerson;
        /**
         * EHR-邮件地址
         */
        private String postAddress;
        /**
         * OA-人员ID
         */
        private String OAID;
        /**
         * EHR-人员职级ID
         */
        private String personPositionID;
        /**
         * EHR-手机号
         */
        private String phoneNo;
        /**
         * EHR-弃用字段
         */
        private String personOrgCode;
        /**
         * EHR-身份证号
         */
        private String cardID;
        /**
         * EHR-人员部门名称
         */
        private String personDepName;
        /**
         * OA-人员ID
         */
        private String personID;
        /**
         * EHR-人员类型 01正式员工 02 实习人员 03 退休返聘人员 04 其他人员 05 离职人员 06 待入职人员 07 外协人员
         */
        private String personType;
        /**
         * EHR-家庭电话
         */
        private String homeTelephone;
        /**
         * EHR-邮箱
         */
        private String email;
        /**
         * EHR-页码
         */
        private String PAGE_ROW_NUMBER;
        /**
         * EHR-人员岗位编码
         */
        private String personPostCode;
        /**
         * EHR-家庭住址
         */
        private String address;
        /**
         * EHR-婚姻状态
         */
        private String marriageStatus;
        /**
         * EHR-汇报人
         */
        private String reporter;
        /**
         * EHR-弃用字段
         */
        private String personOrgName;
        /**
         * EHR-档案工资
         */
        private String fileWage;
        /**
         * EHR-部门主管编码
         */
        private String depManagerCode;
        /**
         * EHR-员工名称
         */
        private String personName;
        /**
         * EHR-员工部门编码
         */
        private String personDepCode;
        /**
         * EHR-员工主岗
         */
        private String personMainPostID;
        /**
         * EHR-年龄
         */
        private String age;

        public String getIS_DELETED() {
            return IS_DELETED;
        }

        public void setIS_DELETED(String IS_DELETED) {
            this.IS_DELETED = IS_DELETED;
        }

        public String getIS_ENABLE() {
            return IS_ENABLE;
        }

        public void setIS_ENABLE(String IS_ENABLE) {
            this.IS_ENABLE = IS_ENABLE;
        }
    }
}

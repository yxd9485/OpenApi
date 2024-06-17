package com.fenbeitong.openapi.plugin.customize.wanyang.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName WanYangOrgUnitDTO
 * @Description 万洋查询组织架构返回对象
 * @Company www.fenbeitong.com
 * @Author helu
 * @Date 2022/8/2 下午9:21
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WanYangOrgUnitDTO extends PageInfoDTO {

   private  List<WyOrgUnitDTO> list;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
   public static class WyOrgUnitDTO{
        /**
         * 页码
         */
        private String PAGE_ROW_NUMBER;
        /**
         * EHR-1.删除0.未删除
         */
        @JsonProperty("IS_DELETED")
        private String IS_DELETED;
        /**
         * EHR-部门主管身份证号
         */
        private String depManagerCardId;
        /**
         * EHR-部门主管name
         */
        private String depManagerName;
        /**
         * EHR-上级部门code
         */
        private String superDepCode;
        /**
         * EHR-部门分管领导code
         */
        private String depLeaderCode;
        /**
         * EHR-1.启用0.停用
         */
        @JsonProperty("IS_ENABLE")
        private String IS_ENABLE;
        /**
         * EHR-部门分管领导name
         */
        private String depLeaderName;
        /**
         * EHR-部门ID全路径，用于：广联达
         */
        private String deptFullPath;
        /**
         * OA-部门ID
         */
        private String OAID;
        /**
         * EHR-部门名称
         */
        private String depName;
        /**
         * EHR-组织层级
         */
        private String ZZCJ;
        /**
         * EHR-部门分管领导身份证号
         */
        private String depLeaderCardId;
        /**
         * EHR-部门主管code
         */
        private String depManagerCode;
        /**
         * EHR-部门code
         */
        private String depCode;
        /**
         * EHR-部门路径
         */
        private String path;
        /**
         * EHR-部门类型：1集团 2 中心 3 部门 4 区域 5 公司 6 项目部 7 组
         */
        private String depType;
        /**
         * EHR-上级部门名 称
         */
        private String superDepName;
        /**
         * EHR-排序号
         */
        private String sortID;
        /**
         * OA-部门ID
         */
        private String depID;
        /**
         * EHR-部门ID
         */
        private String ehrDepID;
        /**
         * OA-上级部门ID
         */
        private String superDepID;
        /**
         * OA-部门编码
         */
        private String OADepCode;
        /**
         * 时间戳
         */
        private String lastUpdateTime;

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

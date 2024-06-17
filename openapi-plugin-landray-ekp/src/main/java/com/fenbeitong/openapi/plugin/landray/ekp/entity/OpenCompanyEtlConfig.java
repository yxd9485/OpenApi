//package com.fenbeitong.openapi.plugin.landray.ekp.entity;
//
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import javax.persistence.Column;
//import javax.persistence.Id;
//import javax.persistence.Table;
//import java.util.Date;
//
///**
// * Created by zhangpeng on 2021/08/06.
// */
//@Data
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//@Table(name = "open_company_etl_config")
//public class OpenCompanyEtlConfig {
//
//    /**
//     * 主键
//     */
//    @Id
//    @Column(name = "ID")
//    private Long id;
//
//    /**
//     * 公司ID
//     */
//    @Column(name = "COMPANY_ID")
//    private String companyId;
//
//    /**
//     * 业务类型：如用车、采购等
//     */
//    @Column(name = "BUSSINESS_TYPE")
//    private String bussinessType;
//
//    /**
//     * 对应etl信息
//     */
//    @Column(name = "MAIN_ID")
//    private String mainId;
//
//    /**
//     *
//     */
//    @Column(name = "CREATE_TIME")
//    private Date createTime;
//
//    /**
//     *
//     */
//    @Column(name = "UPDATE_TIME")
//    private Date updateTime;
//
//
//}

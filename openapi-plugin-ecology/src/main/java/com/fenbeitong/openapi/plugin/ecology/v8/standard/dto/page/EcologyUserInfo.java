package com.fenbeitong.openapi.plugin.ecology.v8.standard.dto.page;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 泛微人员详情
 * 字段描述见 < a https://e-cloudstore.com/doc.html?appId=c373a4b01fb74d098b62e2b969081d2d />
 *
 * @author ctl
 * @date 2021/11/12
 */
@Data
public class EcologyUserInfo implements Serializable {
    @JsonProperty("tempresidentnumber")
    private String tempresidentnumber;
    @JsonProperty("createdate")
    private String createdate;
    @JsonProperty("language")
    private String language;
    @JsonProperty("subcompanyid1")
    private String subcompanyid1;
    @JsonProperty("subcompanyname")
    private String subcompanyname;
    @JsonProperty("joblevel")
    private String joblevel;
    @JsonProperty("startdate")
    private String startdate;
    @JsonProperty("password")
    private String password;
    @JsonProperty("subcompanycode")
    private String subcompanycode;
    @JsonProperty("jobactivitydesc")
    private String jobactivitydesc;
    @JsonProperty("bememberdate")
    private String bememberdate;
    @JsonProperty("modified")
    private String modified;
    @JsonProperty("id")
    private String id;
    @JsonProperty("mobilecall")
    private String mobilecall;
    @JsonProperty("nativeplace")
    private String nativeplace;
    @JsonProperty("certificatenum")
    private String certificatenum;
    @JsonProperty("height")
    private String height;
    @JsonProperty("loginid")
    private String loginid;
    @JsonProperty("created")
    private String created;
    @JsonProperty("degree")
    private String degree;
    @JsonProperty("bepartydate")
    private String bepartydate;
    @JsonProperty("weight")
    private String weight;
    @JsonProperty("telephone")
    private String telephone;
    @JsonProperty("residentplace")
    private String residentplace;
    @JsonProperty("lastname")
    private String lastname;
    @JsonProperty("healthinfo")
    private String healthinfo;
    @JsonProperty("enddate")
    private String enddate;
    @JsonProperty("maritalstatus")
    private String maritalstatus;
    @JsonProperty("departmentname")
    private String departmentname;
    @JsonProperty("folk")
    private String folk;
    @JsonProperty("status")
    private String status;
    @JsonProperty("birthday")
    private String birthday;
    @JsonProperty("accounttype")
    private String accounttype;
    @JsonProperty("jobcall")
    private String jobcall;
    @JsonProperty("managerid")
    private String managerid;
    @JsonProperty("assistantid")
    private String assistantid;
    @JsonProperty("departmentcode")
    private String departmentcode;
    @JsonProperty("belongto")
    private String belongto;
    @JsonProperty("email")
    private String email;
    @JsonProperty("seclevel")
    private String seclevel;
    @JsonProperty("policy")
    private String policy;
    @JsonProperty("jobtitle")
    private String jobtitle;
    @JsonProperty("workcode")
    private String workcode;
    @JsonProperty("sex")
    private String sex;
    @JsonProperty("departmentid")
    private String departmentid;
    @JsonProperty("homeaddress")
    private String homeaddress;
    @JsonProperty("mobile")
    private String mobile;
    @JsonProperty("lastmoddate")
    private String lastmoddate;
    @JsonProperty("educationlevel")
    private String educationlevel;
    @JsonProperty("islabouunion")
    private String islabouunion;
    @JsonProperty("locationid")
    private String locationid;
    @JsonProperty("regresidentplace")
    private String regresidentplace;
    @JsonProperty("dsporder")
    private String dsporder;
    @JsonProperty("base_custom_data")
    private Map<String, Object> baseCustomData;
    @JsonProperty("work_custom_data")
    private Map<String, Object> workCustomData;
    @JsonProperty("person_custom_data")
    private Map<String, Object> personCustomData;
}

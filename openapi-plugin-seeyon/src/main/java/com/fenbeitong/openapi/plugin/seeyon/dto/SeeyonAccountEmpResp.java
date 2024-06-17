package com.fenbeitong.openapi.plugin.seeyon.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeeyonAccountEmpResp implements Serializable {
    /**
     * code : 00190 orgAccountId : 7365657164498092000 id : -8171400428699367000 name : wbbd
     * createTime : 1543653563000 updateTime : 1543653760000 sortId : 1 isDeleted : false enabled :
     * true externalType : 0 status : 1 description : lksdfoiwef orgLevelId : -1 orgPostId : -1
     * orgDepartmentId : 662089437316465700 type : 1 isInternal : false isLoginable : true isVirtual :
     * false isAssigned : true isAdmin : false isValid : true state : 1 properties :
     * {"birthday":"1990-01-13","politics":0,"website":"www.baidu.com","address":"就是这么近","imageid":"90283","gender":-1,"degree":"reldis","postAddress":"ljsfs","emailaddress":"iojlwfksd","reporter":-1,"blog":"sfdlwef","hiredate":"2019-01-09","extPostLevel":"p:主岗,l:职务级别,","weixin":"slfsiew","weibo":"oi34jer","telnumber":"1298495834","postalcode":"ilweiujkew","eduBack":0,"officenumber":"1209387382","location":"localhost","idnum":"23897492923"}
     * second_post : [] concurrent_post : [] customerAddressBooklist : [] primaryLanguange : chinese
     * pinyin : wbbd pinyinhead : wbbd address : dizhi location : location valid : true reporter : -1
     * loginName : wbbd postalcode : 09090 weixin : 98iokj3 weibo : iolwlke postAddress : blog : blog
     * website : www.baidu.com birthday : 1998-09-08 hiredate : bsaafd idNum : wertg degree : hgsdg
     * telNumber : ertfd officeNum : sdgersdf emailAddress : sgsfs gender : -1 v5External : true
     * vjoinExternal : false entityType : Member orgAccountName : 中粮粮谷专业化公司 orgPostName : post_name
     * orgDepartmentName : 外部单位 orgLevelName : level_name
     */
    @ApiModelProperty(value = "", example = "", required = false)
    private String code;

    @ApiModelProperty(value = "", example = "", required = false)
    private Long orgAccountId;

    @ApiModelProperty(value = "", example = "", required = false)
    private Long id;

    @ApiModelProperty(value = "", example = "", required = false)
    private String name;

    @ApiModelProperty(value = "", example = "", required = false)
    private long createTime;

    @ApiModelProperty(value = "", example = "", required = false)
    private long updateTime;

    @ApiModelProperty(value = "", example = "", required = false)
    private Long sortId;

    @ApiModelProperty(value = "", example = "", required = false)
    private boolean isDeleted;

    @ApiModelProperty(value = "", example = "", required = false)
    private boolean enabled;

    @ApiModelProperty(value = "", example = "", required = false)
    private Integer externalType;

    @ApiModelProperty(value = "", example = "", required = false)
    private Integer status;

    @ApiModelProperty(value = "", example = "", required = false)
    private String description;

    @ApiModelProperty(value = "", example = "", required = false)
    private Long orgLevelId;

    @ApiModelProperty(value = "", example = "", required = false)
    private Long orgPostId;

    @ApiModelProperty(value = "", example = "", required = false)
    private Long orgDepartmentId;

    @ApiModelProperty(value = "", example = "", required = false)
    private Integer type;

    @ApiModelProperty(value = "", example = "", required = false)
    private boolean isInternal;

    @ApiModelProperty(value = "", example = "", required = false)
    private boolean isLoginable;

    @ApiModelProperty(value = "", example = "", required = false)
    private boolean isVirtual;

    @ApiModelProperty(value = "", example = "", required = false)
    private boolean isAssigned;

    @ApiModelProperty(value = "", example = "", required = false)
    private boolean isAdmin;

    @ApiModelProperty(value = "", example = "", required = false)
    private boolean isValid;

    @ApiModelProperty(value = "", example = "", required = false)
    private Integer state;

    @ApiModelProperty(value = "", example = "", required = false)
    private PropertiesBean properties;

    @ApiModelProperty(value = "", example = "", required = false)
    private String primaryLanguange;

    @ApiModelProperty(value = "", example = "", required = false)
    private String pinyin;

    @ApiModelProperty(value = "", example = "", required = false)
    private String pinyinhead;

    @ApiModelProperty(value = "", example = "", required = false)
    private String address;

    @ApiModelProperty(value = "", example = "", required = false)
    private String location;

    @ApiModelProperty(value = "", example = "", required = false)
    private boolean valid;

    @ApiModelProperty(value = "", example = "", required = false)
    private Long reporter;

    @ApiModelProperty(value = "", example = "", required = false)
    private String loginName;

    @ApiModelProperty(value = "", example = "", required = false)
    private String postalcode;

    @ApiModelProperty(value = "", example = "", required = false)
    private String weixin;

    @ApiModelProperty(value = "", example = "", required = false)
    private String weibo;

    @ApiModelProperty(value = "", example = "", required = false)
    private String postAddress;

    @ApiModelProperty(value = "", example = "", required = false)
    private String blog;

    @ApiModelProperty(value = "", example = "", required = false)
    private String website;

    @ApiModelProperty(value = "", example = "", required = false)
    private String birthday;

    @ApiModelProperty(value = "", example = "", required = false)
    private String hiredate;

    @ApiModelProperty(value = "", example = "", required = false)
    private String idNum;

    @ApiModelProperty(value = "", example = "", required = false)
    private String degree;

    @ApiModelProperty(value = "", example = "", required = false)
    private String telNumber;

    @ApiModelProperty(value = "", example = "", required = false)
    private String officeNum;

    @ApiModelProperty(value = "", example = "", required = false)
    private String emailAddress;

    @ApiModelProperty(value = "", example = "", required = false)
    private Integer gender;

    @ApiModelProperty(value = "", example = "", required = false)
    private boolean v5External;

    @ApiModelProperty(value = "", example = "", required = false)
    private boolean vjoinExternal;

    @ApiModelProperty(value = "", example = "", required = false)
    private String entityType;

    @ApiModelProperty(value = "", example = "", required = false)
    private String orgAccountName;

    @ApiModelProperty(value = "", example = "", required = false)
    private String orgPostName;

    @ApiModelProperty(value = "", example = "", required = false)
    private String orgDepartmentName;

    @ApiModelProperty(value = "", example = "", required = false)
    private String orgLevelName;

    @ApiModelProperty(value = "", example = "", required = false)
    @JsonProperty("second_post")
    private List<?> secondPost;

    @ApiModelProperty(value = "", example = "", required = false)
    @JsonProperty("concurrent_post")
    private List<?> concurrentPost;

    @ApiModelProperty(value = "", example = "", required = false)
    private List<?> customerAddressBooklist;

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isInternal() {
        return isInternal;
    }

    public void setInternal(boolean internal) {
        isInternal = internal;
    }

    public boolean isLoginable() {
        return isLoginable;
    }

    public void setLoginable(boolean loginable) {
        isLoginable = loginable;
    }

    public boolean isVirtual() {
        return isVirtual;
    }

    public void setVirtual(boolean virtual) {
        isVirtual = virtual;
    }

    public boolean isAssigned() {
        return isAssigned;
    }

    public void setAssigned(boolean assigned) {
        isAssigned = assigned;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    @JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
    public static class PropertiesBean {
        /**
         * birthday : 1990-01-13 politics : 0 website : www.baidu.com address : 就是这么近 imageid : 90283
         * gender : -1 degree : reldis postAddress : ljsfs emailaddress : iojlwfksd reporter : -1 blog :
         * sfdlwef hiredate : 2019-01-09 extPostLevel : p:主岗,l:职务级别, weixin : slfsiew weibo : oi34jer
         * telnumber : 1298495834 postalcode : ilweiujkew eduBack : 0 officenumber : 1209387382 location
         * : localhost idnum : 23897492923
         */
        @ApiModelProperty(value = "", example = "", required = false)
        private String birthday;

        @ApiModelProperty(value = "", example = "", required = false)
        private Integer politics;

        @ApiModelProperty(value = "", example = "", required = false)
        private String website;

        @ApiModelProperty(value = "", example = "", required = false)
        private String address;

        @ApiModelProperty(value = "", example = "", required = false)
        private String imageid;

        @ApiModelProperty(value = "", example = "", required = false)
        private Integer gender;

        @ApiModelProperty(value = "", example = "", required = false)
        private String degree;

        @ApiModelProperty(value = "", example = "", required = false)
        private String postAddress;

        @ApiModelProperty(value = "", example = "", required = false)
        private String emailaddress;

        @ApiModelProperty(value = "", example = "", required = false)
        private Long reporter;

        @ApiModelProperty(value = "", example = "", required = false)
        private String blog;

        @ApiModelProperty(value = "", example = "", required = false)
        private String hiredate;

        @ApiModelProperty(value = "", example = "", required = false)
        private String extPostLevel;

        @ApiModelProperty(value = "", example = "", required = false)
        private String weixin;

        @ApiModelProperty(value = "", example = "", required = false)
        private String weibo;

        @ApiModelProperty(value = "", example = "", required = false)
        private String telnumber;

        @ApiModelProperty(value = "", example = "", required = false)
        private String postalcode;

        @ApiModelProperty(value = "", example = "", required = false)
        private Integer eduBack;

        @ApiModelProperty(value = "", example = "", required = false)
        private String officenumber;

        @ApiModelProperty(value = "", example = "", required = false)
        private String location;

        @ApiModelProperty(value = "", example = "", required = false)
        private String idnum;
    }
}

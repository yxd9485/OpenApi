package com.fenbeitong.openapi.plugin.dingtalk.eia.dto;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author zhaokechun
 * @date 2018/11/21 10:41
 */
@Data
public class DingtalkUser implements Serializable {

    /**
     * example:
     * <p>
     * orderInDepts : {}
     * position :
     * remark :
     * department : [54962]
     * unionid : ***
     * tel :
     * userid : ***
     * isSenior : false
     * workPlace :
     * dingId : ***
     * isBoss : false
     * name : ***
     * errmsg : ok
     * stateCode : 86
     * avatar : https://static.dingtalk.com/media/***.jpg
     * errcode : 0
     * jobnumber : 0001
     * isLeaderInDepts : {54962231:false}
     * email : ***
     * roles : [{"id":"***","name":"主管理员","groupName":"默认","type":101}]
     * active : true
     * isAdmin : true
     * openId : ***
     * mobile : ***
     * isHide : false
     */

    private String orderInDepts;
    private String position;
    private String remark;
    private String unionid;
    private String tel;
    private String userid;
    private boolean isSenior;
    private String workPlace;
    private String dingId;
    private boolean isBoss;
    private String name;
    private String errmsg;
    private String stateCode;
    private String avatar;
    private int errcode;
    private String jobnumber;
    private String isLeaderInDepts;
    private String email;
    private boolean active;
    private boolean isAdmin;
    private String openId;
    private String mobile;
    private boolean isHide;
    private List<Integer> department;
    private List<RolesBean> roles;
    private Map<String, Object> extattr;
    private static final String[] FBT_MOBILE_LABEL = {"分贝通手机号", "fbtMobile"};
    private Map<String,String> routerInfo;
    private Map<String,Long> departmentNameTwoIdMap;

    /**
     * 新增分贝权限类型字段，通过钉钉或企业微信加入员工分别权限字段，同步人员时进行权限设置
     */
    private static final String[] FBT_ROLE_TYPE_LABEL = {"分贝权限", "fbPriv", "分贝通权限", "fbtPriv"};

    private static final String[] FBT_ID_CARD_LABEL = {"身份证号", "fbtIdCard"};

    private static final String[] MAIN_DEPARTMENT = {"主部门", "mainDepartment"};

    private Integer roleType;

    public String getFbtMobile() {
        String fbtMobile = null;
        if (extattr == null || extattr.isEmpty()) {
            return null;
        }
        for (String mobileLabel : FBT_MOBILE_LABEL) {
            fbtMobile = (String) extattr.get(mobileLabel);
            if (StringUtils.isNotBlank(fbtMobile)) {
                fbtMobile = StringUtils.normalizeSpace(fbtMobile);
                fbtMobile = fbtMobile.substring(fbtMobile.length()>11 ? fbtMobile.length()-11 : 0);
                break;
            }
        }
        return fbtMobile;
    }


    /**
     * @return 身份证号
     */
    public String getIdCard() {
        String idCard = null;
        if (extattr == null || extattr.isEmpty()) {
            return null;
        }
        for (String idCardLabel : FBT_ID_CARD_LABEL) {
            idCard = (String) extattr.get(idCardLabel);
            if (StringUtils.isNotBlank(idCard)) {
                idCard = idCard.trim();
                break;
            }
        }
        return idCard;
    }


    //获取权限类型字段信息
    public String getFbtRoleType() {
        String fbtRoleType = null;
        if (extattr == null || extattr.isEmpty()) {
            return null;
        }
        for (String roleTypeLabel : FBT_ROLE_TYPE_LABEL) {
            fbtRoleType = (String) extattr.get(roleTypeLabel);
            if (StringUtils.isNotBlank(fbtRoleType)) {
                fbtRoleType = fbtRoleType.trim();
//                roleType = Integer.valueOf(fbtRoleType.trim());
                break;
            }
        }
        return fbtRoleType;
    }


    /**
     * 如果不填写，则为空
     *
     * @return
     */
    public String getFbtNullRoleType() {
        String fbtRoleType = null;
        if (extattr == null || extattr.isEmpty()) {
            return null;
        }
        for (String roleTypeLabel : FBT_ROLE_TYPE_LABEL) {
            fbtRoleType = (String) extattr.get(roleTypeLabel);
            if (StringUtils.isNotBlank(fbtRoleType)) {
                fbtRoleType = fbtRoleType.trim();
//                roleType = Integer.valueOf(fbtRoleType.trim());
                break;
            }
        }
        return fbtRoleType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DingtalkUser that = (DingtalkUser) o;
        return Objects.equals(userid, that.userid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userid);
    }

    @Data
    public static class RolesBean {
        /**
         * id : ***
         * name : 主管理员
         * groupName : 默认
         * type : 101
         */

        private String id;
        private String name;
        private String groupName;
        private int type;
    }

    /**
     * @return 主部门
     */
    public String getMainDepartment() {
        String mainDepartment = null;
        if (extattr == null || extattr.isEmpty()) {
            return null;
        }
        for (String mainDepartmentLabel : MAIN_DEPARTMENT) {
            mainDepartment = (String) extattr.get(mainDepartmentLabel);
            if (StringUtils.isNotBlank(mainDepartment)) {
                mainDepartment = mainDepartment.trim();
                break;
            }
        }
        return mainDepartment;
    }


    public String getMobile() {
        if (!ObjectUtils.isEmpty(mobile) && StringUtils.normalizeSpace(mobile).length() >= 11) {
            // return mobile.trim().substring(mobile.trim().length() - 11);
            return StringUtils.normalizeSpace(mobile).substring(StringUtils.normalizeSpace(mobile).length() - 11);
        } else {
            return mobile;
        }

    }
}

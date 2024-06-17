package com.fenbeitong.openapi.plugin.seeyon.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by hanshuqi on 17/11/27.
 */
public class SeeyonPersonInfoParam implements Serializable{


    private String otypeName;

    private String birthday;

    private String perSort;

    private String sex;

    private String ocupationName;

    private String accountId;

    private String secondOcupationName;

    private String trueName;

    private String discursion;

    private String familyPhone;


    private String officePhone;

    private String departmentName;


    private String password;


    private String staffNumber;


    private String familyAddress;


    private String id;
    //此属性为预留字段，暂时不支持

    private String identity;


    private String mobilePhone;


    private String email;
    //此属性为预留字段，暂时不支持

    private String loginName;
    //人员状态，0可用，1不可用



    private  String state;

    private Date createTime;
    private Date updateTime;





    public String getOtypeName() {
        return otypeName;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getPerSort() {
        return perSort;
    }

    public String getSex() {
        return sex;
    }

    public String getOcupationName() {
        return ocupationName;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getSecondOcupationName() {
        return secondOcupationName;
    }

    public String getTrueName() {
        return trueName;
    }

    public String getDiscursion() {
        return discursion;
    }

    public String getFamilyPhone() {
        return familyPhone;
    }

    public String getOfficePhone() {
        return officePhone;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public String getPassword() {
        return password;
    }

    public String getStaffNumber() {
        return staffNumber;
    }

    public String getFamilyAddress() {
        return familyAddress;
    }

    public String getId() {
        return id;
    }

    public String getIdentity() {
        return identity;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public String getEmail() {
        return email;
    }

    public String getLoginName() {
        return loginName;
    }

    public String getState() {
        return state;
    }
//    public Date getCreateTime() {
//        return createTime;
//    }
//
//    public Date getUpdateTime() {
//        return updateTime;
//    }







    public void setOtypeName(String otypeName) {
        this.otypeName = otypeName;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setPerSort(String perSort) {
        this.perSort = perSort;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setOcupationName(String ocupationName) {
        this.ocupationName = ocupationName;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setSecondOcupationName(String secondOcupationName) {
        this.secondOcupationName = secondOcupationName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    public void setDiscursion(String discursion) {
        this.discursion = discursion;
    }

    public void setFamilyPhone(String familyPhone) {
        this.familyPhone = familyPhone;
    }

    public void setOfficePhone(String officePhone) {
        this.officePhone = officePhone;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setStaffNumber(String staffNumber) {
        this.staffNumber = staffNumber;
    }

    public void setFamilyAddress(String familyAddress) {
        this.familyAddress = familyAddress;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }


    public void setState(String state) {
        this.state = state;
    }

//    public void setCreateTime(Date createTime) {
//        this.createTime = createTime;
//    }
//
//    public void setUpdateTime(Date updateTime) {
//        this.updateTime = updateTime;
//    }
//



    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column OPEN_DN_PERSON_INFO.create_time
     *
     * @param createTime the value for OPEN_DN_PERSON_INFO.create_time
     *
     * @mbg.generated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column OPEN_DN_PERSON_INFO.update_time
     *
     * @return the value of OPEN_DN_PERSON_INFO.update_time
     *
     * @mbg.generated
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column OPEN_DN_PERSON_INFO.update_time
     *
     * @param updateTime the value for OPEN_DN_PERSON_INFO.update_time
     *
     * @mbg.generated
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "PersonInfoParam{" +
                "otypeName='" + otypeName + '\'' +
                ", birthday='" + birthday + '\'' +
                ", perSort='" + perSort + '\'' +
                ", sex='" + sex + '\'' +
                ", ocupationName='" + ocupationName + '\'' +
                ", accountId='" + accountId + '\'' +
                ", secondOcupationName='" + secondOcupationName + '\'' +
                ", trueName='" + trueName + '\'' +
                ", discursion='" + discursion + '\'' +
                ", familyPhone='" + familyPhone + '\'' +
                ", officePhone='" + officePhone + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", password='" + password + '\'' +
                ", staffNumber='" + staffNumber + '\'' +
                ", familyAddress='" + familyAddress + '\'' +
                ", id='" + id + '\'' +
                ", identity='" + identity + '\'' +
                ", mobilePhone='" + mobilePhone + '\'' +
                ", email='" + email + '\'' +
                ", loginName='" + loginName + '\'' +
                ", state='" + state + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }

        SeeyonPersonInfoParam that = (SeeyonPersonInfoParam) o;

        if (otypeName != null ? !otypeName.equals(that.otypeName) : that.otypeName != null){
            return false;
        }
        if (birthday != null ? !birthday.equals(that.birthday) : that.birthday != null){
            return false;
        }
        if (perSort != null ? !perSort.equals(that.perSort) : that.perSort != null){
            return false;
        }
        if (sex != null ? !sex.equals(that.sex) : that.sex != null){
            return false;
        }
        if (ocupationName != null ? !ocupationName.equals(that.ocupationName) : that.ocupationName != null){
            return false;
        }

        if (accountId != null ? !accountId.equals(that.accountId) : that.accountId != null){
            return false;
        }
        if (secondOcupationName != null ? !secondOcupationName.equals(that.secondOcupationName) : that.secondOcupationName != null) {
            return false;
        }
        if (trueName != null ? !trueName.equals(that.trueName) : that.trueName != null){
            return false;
        }
        if (discursion != null ? !discursion.equals(that.discursion) : that.discursion != null){
            return false;
        }
        if (familyPhone != null ? !familyPhone.equals(that.familyPhone) : that.familyPhone != null){
            return false;
        }
        if (officePhone != null ? !officePhone.equals(that.officePhone) : that.officePhone != null) return false;
        if (departmentName != null ? !departmentName.equals(that.departmentName) : that.departmentName != null)
            return false;
        if (password != null ? !password.equals(that.password) : that.password != null){
            return false;
        }
        if (staffNumber != null ? !staffNumber.equals(that.staffNumber) : that.staffNumber != null){
            return false;
        }
        if (familyAddress != null ? !familyAddress.equals(that.familyAddress) : that.familyAddress != null) {
            return false;
        }
        if (id != null ? !id.equals(that.id) : that.id != null){
            return false;
        }
        if (identity != null ? !identity.equals(that.identity) : that.identity != null) {
            return false;
        }
        if (mobilePhone != null ? !mobilePhone.equals(that.mobilePhone) : that.mobilePhone != null){
            return false;
        }
        if (email != null ? !email.equals(that.email) : that.email != null){
            return false;
        }
        return loginName != null ? loginName.equals(that.loginName) : that.loginName == null;
    }

    @Override
    public int hashCode() {
        int result = otypeName != null ? otypeName.hashCode() : 0;
        result = 31 * result + (birthday != null ? birthday.hashCode() : 0);
        result = 31 * result + (perSort != null ? perSort.hashCode() : 0);
        result = 31 * result + (sex != null ? sex.hashCode() : 0);
        result = 31 * result + (ocupationName != null ? ocupationName.hashCode() : 0);
        result = 31 * result + (accountId != null ? accountId.hashCode() : 0);
        result = 31 * result + (secondOcupationName != null ? secondOcupationName.hashCode() : 0);
        result = 31 * result + (trueName != null ? trueName.hashCode() : 0);
        result = 31 * result + (discursion != null ? discursion.hashCode() : 0);
        result = 31 * result + (familyPhone != null ? familyPhone.hashCode() : 0);
        result = 31 * result + (officePhone != null ? officePhone.hashCode() : 0);
        result = 31 * result + (departmentName != null ? departmentName.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (staffNumber != null ? staffNumber.hashCode() : 0);
        result = 31 * result + (familyAddress != null ? familyAddress.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (identity != null ? identity.hashCode() : 0);
        result = 31 * result + (mobilePhone != null ? mobilePhone.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (loginName != null ? loginName.hashCode() : 0);
        return result;
    }
}

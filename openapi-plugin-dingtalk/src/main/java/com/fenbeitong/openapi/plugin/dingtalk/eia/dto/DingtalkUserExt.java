package com.fenbeitong.openapi.plugin.dingtalk.eia.dto;

/**
 * <p>Title: DingtalkUserExt</p>
 * <p>Description: 钉钉用户扩展</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/4/15 3:31 PM
 */
public class DingtalkUserExt extends DingtalkUser {

    private int userIdType;

    public DingtalkUserExt(int userIdType) {
        this.userIdType = userIdType;
    }

    @Override
    public String getUserid() {
        if (userIdType == 1) {
            return getJobnumber();
        }
        return super.getUserid();
    }


}

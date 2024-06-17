package com.fenbeitong.openapi.plugin.func.deprecated.dto.auth;


/**
 * module: 迁移open-java<br/>
 * <p>
 * description: 描述<br/>
 *
 * @author XiaoDong.Yang
 * @date 2022/9/6 19:02
 */
public class OpenAuthThirdUser {
    private Long id;
    private String tpUserId;
    private String tpMoblie;
    private String appId;
    private String appKey;
    private String fbUserId;
    private int appType;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTpUserId() {
        return tpUserId;
    }
    public void setTpUserId(String tpUserId) {
        this.tpUserId = tpUserId == null ? null : tpUserId.trim();
    }
    public String getTpMoblie() {
        return tpMoblie;
    }
    public void setTpMoblie(String tpMoblie) {
        this.tpMoblie = tpMoblie == null ? null : tpMoblie.trim();
    }
    public String getAppId() {
        return appId;
    }
    public void setAppId(String appId) {
        this.appId = appId == null ? null : appId.trim();
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppKey() {

        return appKey;
    }

    public void setFbUserId(String fbUserId) {
        this.fbUserId = fbUserId;
    }

    public String getFbUserId() {

        return fbUserId;
    }

    public void setAppType(int appType) {
        this.appType = appType;
    }

    public int getAppType() {

        return appType;
    }
}

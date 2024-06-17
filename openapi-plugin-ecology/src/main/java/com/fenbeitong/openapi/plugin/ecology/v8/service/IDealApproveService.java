package com.fenbeitong.openapi.plugin.ecology.v8.service;

/**
 * 撤销泛微审批
 * @Auther zhang.peng
 * @Date 2021/11/15
 */
public interface IDealApproveService {

    boolean revokeAndDeleteEcologyApprove(String companyId , String oldApproveId , String userId );

}

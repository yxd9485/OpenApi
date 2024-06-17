package com.fenbeitong.openapi.plugin.customize.wawj.service;

import com.fenbeitong.openapi.plugin.customize.wawj.dto.WawjFuLiShenQingSyncReqDTO;
import com.fenbeitong.openapi.plugin.customize.wawj.dto.WawjGrantVoucherByUserReq;
import com.fenbeitong.openapi.plugin.customize.wawj.dto.WawjKaoQinSyncReqDTO;
import com.fenbeitong.openapi.plugin.customize.wawj.dto.WawjKqDTO;

import java.util.List;

/**
 * <p>Title: IWawjKaoQinShenQingService</p>
 * <p>Description: 考勤及申请单同步</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/10/26 5:42 PM
 */
public interface IWawjKaoQinShenQingService {

    /**
     * 同步考勤
     *
     * @param req
     */
    void synKaoQin(WawjKaoQinSyncReqDTO req);

    /**
     * 同步福利申请
     *
     * @param req
     */
    void synFuLiShenQing(WawjFuLiShenQingSyncReqDTO req);

    /**
     * 发券
     * @param companyId 公司id
     */
    void grantVoucher(String companyId);

    /**
     * 发券
     * @param req 指定人员发券
     */
    void grantVoucherByUser(WawjGrantVoucherByUserReq req);

    /**
     * 更新审批单及考勤状态
     * @param companyId 公司id
     */
    void updateAttendanceApply(String companyId);

    /**
     * 关闭审批单
     * @param companyId 公司id
     */
    void closeApply(String companyId);

    /**
     * 考勤同步-测试用
     * @param companyId
     * @param kqList
     */
    void kaoqinSync(String companyId, List<WawjKqDTO> kqList);

    /**
     * 关闭考勤记录
     * @param companyId
     */
    void closeAttendance(String companyId);
}

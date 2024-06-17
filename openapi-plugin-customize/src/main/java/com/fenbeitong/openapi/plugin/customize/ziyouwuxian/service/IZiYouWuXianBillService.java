package com.fenbeitong.openapi.plugin.customize.ziyouwuxian.service;

/**
 * <p>Title: IZiYouWuXianBillService</p>
 * <p>Description: 自由无限账单数据推送服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaowei
 * @date 2020/10/26 5:00 PM
 */
public interface IZiYouWuXianBillService {

    /**
     * 推送账单数据
     *
     * @param companyId
     * @param billNo
     */
    void pushBill(String companyId, String billNo);

    /**
     * 创建定制账单
     *
     * @param companyId 公司id
     * @param billNo    账单编号
     * @param type      定制账单类型  1 表示 总的汇总账单  2 表示 场景汇总账单
     */
    void createBill(String companyId, String billNo, String type);

    /**
     * 发送定制账单
     *
     * @param companyId 公司id
     * @param billNo    账单编号
     */
    void sendBill(String companyId, String billNo);

}

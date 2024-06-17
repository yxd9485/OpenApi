package com.fenbeitong.openapi.plugin.func.apply.dto;

import com.fenbeitong.saasplus.api.model.dto.finance.FinanceCostInfoVO;
import com.fenbeitong.usercenter.api.model.dto.payment.CompanyPaymentContractVO;
import com.fenbeitong.usercenter.api.model.dto.payment.CompanyPaymentProofVO;
import com.fenbeitong.usercenter.api.model.dto.payment.CompanyPaymentSupplierVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplyOrderContractDto {

    private String id;

    private Integer type;
    /**
     * type文字说明 申请单列表、审批单列表Response时用
     */
    private String type_name;


    private String employee_id;

    /**
     * 员工三方id
     */
    private String third_employee_id;


    private String approver_id;

    private BigDecimal budget;

    /**
     * 预估费用下单需校验描述
     */
    private String budget_check_desc;

    private Integer state;
    /**
     * 状态文字说明 申请单列表、审批单列表Response时用
     */
    private String state_name;

    private String check_reason;

    private String company_id;

    private String mail_order_id;

    private String travel_price_detail;

    private Integer apply_reason_id;

    private String apply_reason;

    private String apply_reason_desc;

    private String create_time;

    private String time_range;

    private String city_range;

    private Long log_id;

    private String log;
    /**
     * 员工姓名 审批列表、详情接口返回
     */
    private String user_name;
    /**
     * 员工所属部门 审批列表、详情接口返回,多个部门以,隔开
     */
    private String user_dept;
    /**
     * 审核人姓名 申请单详情需要用到这个字段
     */
    private String approver_name;
    /**
     * 审核人所在部门 申请单详情需要用到这个字段
     */
    private String approver_dept;
    /**
     * 申请单详情字段: 操作权限,位&运算
     */
    private Integer operate_auth;

    /**
     * 提交申请和申请单详情使用的审批流类型，对应枚举：CompanyApplyType
     */
    private Integer flow_type;

    /**
     * 提交申请和申请单详情使用的抄送通知类型
     */
    private Integer flow_cc_type;
    /**
     * 第三方id
     */
    private String third_id;
    /**
     * 第三方备注
     */
    private String third_remark;

    //1.行程审批单 2.事中审批单
    private Integer apply_order_type;

    //超规说明
    private String exceed_buy_desc;

    //驳回理由
    private String repulse_desc;

    //当前时间
    private String current_time;

    //过期时间
    private String expiration_time;

    //超规说明描述
    private String exceed_buy_desc_content;

    private String cost_attribution_id;

    private String cost_attribution_name;

    private Integer cost_attribution_category;

    //是否超规 1.超规 2.不超规
    private Integer exceed_buy_type;

    //订单审批超时时间
    private String overtime;

    private List<String> exceed_buy_desc_list;

    private Integer node_status;

    private BigDecimal real_price;

    //出行人
    private List<String> travel_partner;

    //申请人名称
    private String applicant_name;

    //是否本人出差
    private Boolean is_self_travel;

    //出差天数
    private BigDecimal travel_day;

    //出差时间
    private List<ApplyTravelTimeDTO> travel_time_list;

    private Boolean is_valid_order;

    // 是否为变更单
    private Boolean is_change_apply;

    //变更事由id
    private Integer change_reason_id;
    //变更事由
    private String change_reason;
    //变更事由补充内容
    private String change_reason_desc;
    //作废事由id
    private Integer cancel_reason_id;
    //作废事由
    private String cancel_reason;
    //作废事由补充内容
    private String cancel_reason_desc;

    private String root_apply_order_id;
    private String parent_apply_order_id;

    //是否本人出差
    private Boolean self_travel;

    //分贝券id
    private String coupon_id;

    //分贝券使用范围
    private String couponUseRange;

    private Integer valid_days;

    //子类型
    private Integer sub_type;

    //超时描述
    private String overtime_desc;

    private String applicant_phone;

    private Integer total_trades;

    private Boolean force_sumbit = false;

    //下载地址
    private String download_link;

    //直属部门id
    private String org_unit_id;

    private List<FinanceCostInfoVO> costList;

    //分贝劵是否可以转让 1可以,0不可以
    private Integer canTransfer;

    private Integer returnTicket;//回票状态 0:未回票，1:已全部回票 ，2:部分回票  3:无需回票

    private Integer returnDownload; //电子单回票状态 0未回 1已回
    // 1未催办 2已催办
    private Integer hasten_status;
    //催办时间
    private String hasten_create_time;

    //付款名称
    private String payment_name;

    //付款主体
    private String payer_name;

    //付款id
    private String payer_id;

    //付款时间
    private String payer_time;

    //供应商id
    private Integer receiver_id;

    //合同id
    private Integer contract_id;

    // 凭证id
    private Integer proof_id;

    //发票信息
    private List<Map<String, Object>> invoice_list;

    //供应商信息
    private CompanyPaymentSupplierVO company_supplier;

    //合同信息
    private CompanyPaymentContractVO contract;

    // 凭证信息
    private CompanyPaymentProofVO proof;

    //银行名称
    private String bank_name;

    //订单费用归属
    List<ApplyCostAttributionDto> cost_attribution_list;

    //订单费用归属
    private String order_cost_attribution;

    //是否带入订单 0.不带入 1.带入
    private Integer bring_in;

    /**
     * 用途
     */
    private String payment_use;

    //标题
    private String title;

    //付款主体id(431新增字段)
    private String company_account_id;

    //付款主体名称(431新增字段)
    private String bank_account_acct_name;

    //外卖可用金额文案
    private String takeaway_price_desc;

    //2审批中 16审批拒绝 4审批完成 64撤销
    private Integer payApplyStatus;
    //付款状态 0:待付款  1:已付款  2:付款中  3:无需付款
    private Integer paymentStatus;

    /**
     * 待核销交易记录总额及可用额度显示开关 兼容老版本
     */
    private Boolean show_credit_area;

    /**
     * 待核销交易记录总额 unsolved_total
     */
    private BigDecimal unsolved_total;

    /**
     * 可用额度 available_credit
     */
    private BigDecimal available_credit;

    /**
     * 快照信息存储
     */
    private String snap_content;

    //里程信息
    private List<Map<String, Object>> mileage_list;

    //无法补贴金额
    private BigDecimal unable_allowance_price;

    //无法补贴金额描述
    private String unable_allowance_price_desc;

    //发放时间
    private String grant_time;

    //操作人
    private String operator;

    //模板id
    private String voucher_templetId;

    //分贝券名称
    private String vouchers_name;

    //有效期
    private String voucher_term_validity;

    //使用范围
    private List<String> voucher_type_names;

    //失败详情
    private String fail_detail;

    //发放状态
    private Integer send_status;

    //总里程
    private BigDecimal total_mileage;

    //1:禁止选择里程记录 2:填写理由后允许提交申请单
    private Integer mileage_exceed_buy_type;

    private String processInstanceId;

    /**
     * 通用配置-申请单详情展示配置 0-不展示 1-展示
     */
    private String cost_attribution_code;

    /**
     * 发票选项 1-已开发票 0-待开发票 2-无发票
     */
    private Integer invoice_option;

    //出差时间
    private List<Map<String,Object>> travel_time_details;

    private Map cost_info;

    private String third_department_id;

}

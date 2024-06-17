package com.fenbeitong.openapi.plugin.customize.wantai.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by lizhen on 2022/08/08.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "open_archive_task")
public class OpenArchiveTask {

    /**
     *
     */
    @Id
    @Column(name = "id")
    private String id;

    /**
     * 公司ID
     */
    @Column(name = "company_id")
    private String companyId;

    /**
     * 公司编码
     */
    @Column(name = "org_code")
    private String orgCode;

    /**
     * 账簿
     */
    @Column(name = "book_code")
    private String bookCode;

    /**
     * 系统标识（NCC、ERP）
     */
    @Column(name = "sys_code")
    private String sysCode;

    /**
     * 会计期间
     */
    @Column(name = "fiscal_period")
    private String fiscalPeriod;

    /**
     * 三方档案类型
     */
    @Column(name = "third_archive_type")
    private String thirdArchiveType;

    /**
     * 档案类型
     */
    @Column(name = "archive_type")
    private String archiveType;

    /**
     * 开始时间
     */
    @Column(name = "start_time")
    private Date startTime;

    /**
     * 结束时间
     */
    @Column(name = "end_time")
    private Date endTime;

    /**
     * 任务ID
     */
    @Column(name = "task_id")
    private String taskId;

    /**
     * 执行状态0初始化，10归档中，20归档完成，30处理中，40已处理，99异常
     */
    @Column(name = "status")
    private Integer status;

    /**
     * 执行结果内容
     */
    @Column(name = "execute_result_content")
    private String executeResultContent;

}

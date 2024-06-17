package com.fenbeitong.openapi.plugin.wechat.isv.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

/**
 * 批处理回调
 * @author lizhen
 */
@Data
@XStreamAlias("xml")
public class WeChatIsvBatchJobResultBody {

    @XStreamAlias("ServiceCorpId")
    private String serviceCorpId;

    @XStreamAlias("InfoType")
    private String infoType;

    @XStreamAlias("TimeStamp")
    private Long timeStamp;

    @XStreamAlias("AuthCorpId")
    private String authCorpId;

    @XStreamAlias("BatchJob")
    private BatchJob batchJob;

    @Data
    public static class BatchJob {
        @XStreamAlias("JobId")
        private String jobId;

        @XStreamAlias("JobType")
        private String jobType;
    }
}


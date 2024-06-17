package com.fenbeitong.openapi.plugin.yiduijie.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Title: YiDuiJiePreviewVoucherResp</p>
 * <p>Description: 易对接预览凭证响应</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/19 11:33 AM
 */
@Data
public class YiDuiJiePreviewVoucherResp {

    private YiDuiJiePreviewVoucherRespBody body;

    private String message;

    private Integer status;

    public boolean success() {
        return status != null && status == 0;
    }

    @JsonIgnore
    public Object getPreviewResult() {
        return body == null ? null : body.getPreviewResult();
    }

    @JsonIgnore
    public String getError() {
        return body == null ? null : body.getError();
    }

    @Data
    public static class YiDuiJiePreviewVoucherRespBody {

        private PassedData passedData;

        private List result;

        public String getError() {
            return passedData == null ? null : passedData.getError();
        }

        public Object getPreviewResult() {
            return result;
        }
    }

    @Data
    public static class PassedData {

        private PreviewVoucherRespForm form;

        public String getError() {
            return form == null ? null : form.getError();
        }
    }

    @Data
    public static class PreviewVoucherRespForm {

        private String date;

        private String department;

        private String id;

        private List<PreviewVoucherRespFormItem> items;

        private String maker;

        private String note;

        private List<PreviewVoucherRespFormResult> results;

        private String type;

        private String userName;

        public String getError() {
            return ObjectUtils.isEmpty(results) ? null
                    : String.join(",", results.stream()
                    .filter(r -> "ERROR".equals(r.getLevel()))
                    .map(PreviewVoucherRespFormResult::getMessage).collect(Collectors.toList()));
        }
    }

    @Data
    public static class PreviewVoucherRespFormItem {

        private String amount;

        private String date;

        private String department;

        private String employeeName;

        private String id;

        private String summary;

        private String tax;

        private String typeName;
    }

    @Data
    public static class PreviewVoucherRespFormResult {

        private String level;

        private String message;
    }
}

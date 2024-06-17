package com.fenbeitong.openapi.plugin.yiduijie.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import com.fenbeitong.openapi.plugin.core.util.OpenapiOssUtils;
import com.fenbeitong.openapi.plugin.support.common.dao.OpenCompanyVoucherConfigDao;
import com.fenbeitong.openapi.plugin.support.common.entity.OpenCompanyVoucherConfig;
import com.fenbeitong.openapi.plugin.support.util.RestHttpUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.plugin.voucher.dao.OpenVoucherDraftDao;
import com.fenbeitong.openapi.plugin.voucher.dto.OpenVoucherDto;
import com.fenbeitong.openapi.plugin.voucher.entity.OpenVoucherDraft;
import com.fenbeitong.openapi.plugin.voucher.service.IVoucherCreateService;
import com.fenbeitong.openapi.plugin.yiduijie.constant.BusinessType;
import com.fenbeitong.openapi.plugin.yiduijie.dao.YiduijieCreateVoucherRecordDao;
import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJieFinanceBillVoucherDTO;
import com.fenbeitong.openapi.plugin.yiduijie.dto.YiDuiJieNotifyMsgResultReq;
import com.fenbeitong.openapi.plugin.yiduijie.entity.YiduijieCreateVoucherRecord;
import com.fenbeitong.openapi.plugin.yiduijie.service.IYiDuiJieInteractService;
import com.fenbeitong.saasplus.api.model.dto.finance.FinanceBillVoucherDTO;
import com.fenbeitong.saasplus.api.model.dto.finance.FinanceVoucherContract;
import com.fenbeitong.saasplus.api.service.finance.IFinanceVoucherService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Title: YiDuiJieInteractServiceImpl</p>
 * <p>Description: 易对接交互服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/3/16 4:29 PM
 */
@Slf4j
@ServiceAspect
@Service
public class YiDuiJieInteractServiceImpl implements IYiDuiJieInteractService {

    @Value("${host.harmony}")
    private String harmonyHost;

    @DubboReference(check = false)
    private IFinanceVoucherService financeVoucherService;

    @Autowired
    private IVoucherCreateService voucherCreateService;

    @Autowired
    private YiduijieCreateVoucherRecordDao yiduijieCreateVoucherRecordDao;

    @Autowired
    private OpenapiOssUtils ossUtils;

    @Autowired
    private OpenCompanyVoucherConfigDao openCompanyVoucherConfigDao;

    @Autowired
    private OpenVoucherDraftDao openVoucherDraftDao;

    @Override
    public Object queryBusinessData(String id) {
        String[] batchInfo = id.split(":");
        String businessType = batchInfo[0];
        String realBatchId = batchInfo[1];
        YiduijieCreateVoucherRecord record = yiduijieCreateVoucherRecordDao.getByBatchId(realBatchId).get(0);
        String companyId = record.getCompanyId();
        String batchId = record.getBatchId();
        if (BusinessType.APPLY.getValue().equals(businessType)) {
            return getApplyData(companyId, batchId);
        } else if (BusinessType.BILL.getValue().equals(businessType)) {
            return getBillData(companyId, batchId);
        } else if (BusinessType.VOUCHER.getValue().equals(businessType)) {
            return getVoucherData(batchId);
        }
        return null;
    }

    private Object getVoucherData(String batchId) {
        Map<String, Object> result = Maps.newHashMap();
        List<OpenVoucherDto> voucherDtos = null;
        List<OpenVoucherDraft> voucherDraftList = openVoucherDraftDao.listByBatchId(batchId);
        if (ObjectUtils.isEmpty(voucherDraftList)) {
            return Lists.newArrayList();
        }
        voucherDtos = voucherDraftList.stream().map(v -> {
            OpenVoucherDto voucherDto = new OpenVoucherDto();
            BeanUtils.copyProperties(v, voucherDto);
            return voucherDto;
        }).collect(Collectors.toList());
        result.put("voucher_items", voucherDtos);
        return result;
    }

    private Object getBillData(String companyId, String batchId) {
        YiDuiJieFinanceBillVoucherDTO voucherData = new YiDuiJieFinanceBillVoucherDTO();
        int index = 1;
        FinanceBillVoucherDTO pageData = null;
        while ((pageData = financeVoucherService.queryFinaneBillVoucherInfo(companyId, batchId, 1000, index++)) != null && !ObjectUtils.isEmpty(pageData.getBillList())) {
            String summary = voucherData.getSummary();
            if (ObjectUtils.isEmpty(summary)) {
                voucherData.setSummary(pageData.getSummary());
            }
            List<YiDuiJieFinanceBillVoucherDTO.YiDuiJieBillDTO> billList = voucherData.getBillDtoList();
            if (billList == null) {
                billList = Lists.newArrayList();
                voucherData.setBillList(billList);
            }
            List<FinanceBillVoucherDTO.BillDTO> pageDataBillList = pageData.getBillList();
            if (!ObjectUtils.isEmpty(pageDataBillList)) {
                List<YiDuiJieFinanceBillVoucherDTO.YiDuiJieBillDTO> yiDuiJieBillDtos = pageDataBillList.stream().map(bill -> {
                    YiDuiJieFinanceBillVoucherDTO.YiDuiJieBillDTO billDto = new YiDuiJieFinanceBillVoucherDTO.YiDuiJieBillDTO();
                    BeanUtils.copyProperties(bill, billDto);
                    return billDto;
                }).collect(Collectors.toList());
                billList.addAll(yiDuiJieBillDtos);
            }
        }
        return voucherData;
    }

    private Object getApplyData(String companyId, String batchId) {
        FinanceVoucherContract financeVoucherContract = financeVoucherService.queryFinaneVoucherInfo(companyId, batchId);
        setApplyCode(financeVoucherContract);
        return financeVoucherContract;
    }

    private void setApplyCode(FinanceVoucherContract financeVoucherContract) {
        if (!ObjectUtils.isEmpty(financeVoucherContract)) {
            List<FinanceVoucherContract.FinanceVoucherInfo> applyList = financeVoucherContract.getApply_list();
            if (!ObjectUtils.isEmpty(applyList)) {
                int applyCode = 1000;
                for (FinanceVoucherContract.FinanceVoucherInfo voucherInfo : applyList) {
                    voucherInfo.setApply_code(StringUtils.obj2str(applyCode++));
                }
            }
        }
    }

    @Async
    @Override
    public void notifyMsgResult(YiDuiJieNotifyMsgResultReq req) {
        String url = req.getStatus() == 0 ? req.getMessage() : "";
        String id = req.getId();
        String[] batchInfo = id.split(":");
        String businessType = batchInfo[0];
        String realBatchId = batchInfo[1];
        YiduijieCreateVoucherRecord record = yiduijieCreateVoucherRecordDao.getByBatchId(realBatchId).get(0);
        record.setId(record.getId());
        record.setStatus(req.getStatus());
        record.setType(req.getDocumentType());
        record.setLocalId(req.getLocalId());
        record.setMessage(req.getMessage());
        record.setExcelUrl(url);
        // 1:报销单;2:账单;3:凭证底表
        int voucherType = businessType.equals("APPLY") ? 1 : businessType.equals("BILL") ? 2 : 3;
        OpenCompanyVoucherConfig voucherConfig = openCompanyVoucherConfigDao.getByCompanyIdAndType(record.getCompanyId(), voucherType);
        if (voucherConfig == null || voucherConfig.getDockingType() == 2) {
            record.setFbtExcelUrl(uploadYiDuiJieExcel(record.getCompanyId(), req.getStatus() == 0 ? url : null));
        } else {
            record.setFbtExcelUrl(req.getStatus() == 0 ? url : null);
        }
        if (!ObjectUtils.isEmpty(req.getLocalId()) && voucherType == 3 && voucherConfig != null && voucherConfig.getExcelConfigId() != null) {
            String excelUrl = voucherCreateService.exportExcel(realBatchId, voucherConfig.getExcelConfigId());
            if (!ObjectUtils.isEmpty(excelUrl)) {
                record.setFbtExcelUrl(req.getStatus() == 0 ? excelUrl : null);
            }
        }
        yiduijieCreateVoucherRecordDao.updateById(record);
        String callbackUrl = record.getCallbackUrl();
        CallSaasPlusReq callSaasPlusReq = new CallSaasPlusReq();
        callSaasPlusReq.setCompanyId(record.getCompanyId());
        callSaasPlusReq.setVoucherId(record.getBatchId());
        callSaasPlusReq.setVoucherCode(req.getLocalId());
        callSaasPlusReq.setVoucherState(req.getStatus() == 0 ? 1 : 2);
        callSaasPlusReq.setRemoteUrl(record.getFbtExcelUrl());
        callSaasPlusReq.setErrorMsg(req.getStatus() == 0 ? null : ObjectUtils.isEmpty(req.getMessage()) ? "生成凭证请求失败，请稍后再试" : req.getMessage());
        RestHttpUtils.postJson(callbackUrl, JsonUtils.toJson(callSaasPlusReq));
    }

    private String uploadYiDuiJieExcel(String companyId, String url) {
        if (url == null) {
            return "";
        }
        String fbtUrl = null;
        File temp = null;
        try {
            URL remoteUrl = new URL(url);
            String fileName = remoteUrl.getFile();
            if (fileName.startsWith("/")) {
                fileName = fileName.substring(1);
            }
            fileName = fileName.contains("?") ? fileName.substring(0, fileName.indexOf("?")) : fileName;
            temp = new File(fileName);
            try (InputStream in = remoteUrl.openStream(); FileOutputStream fos = new FileOutputStream(temp)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.flush();
                //上传分贝通oss服务器
                OpenapiOssUtils.UploadFileResponse uploadFileResponse = ossUtils.uploadFiles("yiduijie", companyId, new File[]{temp});
                fbtUrl = uploadFileResponse.getData().get(0).getUrl();
            }
        } catch (Exception e) {
            log.warn("上传易对接文件失败", e);
        } finally {
            if (temp != null) {
                temp.delete();
            }
        }
        return ObjectUtils.isEmpty(fbtUrl) ? url : fbtUrl;
    }


    @Data
    private static class CallSaasPlusReq {

        /**
         * 公司id
         */
        private String companyId;

        /**
         * 凭证
         */
        private String voucherId;

        /**
         * 凭证编号
         */
        private String voucherCode;

        /**
         * 1:成功 2:失败
         */
        private Integer voucherState;

        /**
         * excel下载地址
         */
        private String remoteUrl;

        /**
         * errorMsg
         */
        private String errorMsg;

    }
}

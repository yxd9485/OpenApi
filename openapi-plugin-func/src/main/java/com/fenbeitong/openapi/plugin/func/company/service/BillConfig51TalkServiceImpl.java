package com.fenbeitong.openapi.plugin.func.company.service;

import com.fenbeitong.openapi.plugin.func.company.dao.OpenEbsCoeCostItemsDao;
import com.fenbeitong.openapi.plugin.func.company.dao.OpenEbsCompRelationsDao;
import com.fenbeitong.openapi.plugin.func.company.dao.OpenEbsOrgCostRelationsDao;
import com.fenbeitong.openapi.plugin.func.company.dto.BillConfig51TalkSyncReqDTO;
import com.fenbeitong.openapi.plugin.func.company.dto.CoeCostItemsDTO;
import com.fenbeitong.openapi.plugin.func.company.dto.CoeGlCostCenterRelDTO;
import com.fenbeitong.openapi.plugin.func.company.dto.CoePsEbsCompRelationsDTO;
import com.fenbeitong.openapi.plugin.func.company.entity.OpenEbsCoeCostItems;
import com.fenbeitong.openapi.plugin.func.company.entity.OpenEbsCompRelations;
import com.fenbeitong.openapi.plugin.func.company.entity.OpenEbsOrgCostRelations;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * <p>Title: BillConfig51TalkServiceImpl</p>
 * <p>Description: 51talk账单配置服务</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2020/9/22 2:27 PM
 */
@ServiceAspect
@Service
public class BillConfig51TalkServiceImpl {

    @Autowired
    private OpenEbsCompRelationsDao compRelationsDao;

    @Autowired
    private OpenEbsOrgCostRelationsDao orgCostRelationsDao;

    @Autowired
    private OpenEbsCoeCostItemsDao coeCostItemsDao;

    @Async
    public void doSync(BillConfig51TalkSyncReqDTO req) {
        //同步公司与法人映射关系
        synCompRelations(req);
        //同步部门与成本中心映射关系
        syncOrgCostRelations(req);
        //同步费用用途与会计科目
        syncCoeCostItems(req);
    }

    private void synCompRelations(BillConfig51TalkSyncReqDTO req) {
        List<CoePsEbsCompRelationsDTO> configList = req.getCoePsEbsCompRelations();
        List<OpenEbsCompRelations> srcList = compRelationsDao.list();
        if (!ObjectUtils.isEmpty(srcList)) {
            srcList.forEach(src -> compRelationsDao.deleteById(src.getId()));
        }
        if (!ObjectUtils.isEmpty(configList)) {
            configList.forEach(config -> {
                OpenEbsCompRelations ebsCompRelations = new OpenEbsCompRelations();
                BeanUtils.copyProperties(config, ebsCompRelations);
                compRelationsDao.save(ebsCompRelations);
            });
        }
    }

    private void syncOrgCostRelations(BillConfig51TalkSyncReqDTO req) {
        List<CoeGlCostCenterRelDTO> configList = req.getCoeGlCostCenterRel();
        List<OpenEbsOrgCostRelations> srcList = orgCostRelationsDao.list();
        if (!ObjectUtils.isEmpty(srcList)) {
            srcList.forEach(src -> orgCostRelationsDao.deleteById(src.getId()));
        }
        if (!ObjectUtils.isEmpty(configList)) {
            configList.forEach(config -> {
                OpenEbsOrgCostRelations openEbsCompRelations = new OpenEbsOrgCostRelations();
                BeanUtils.copyProperties(config, openEbsCompRelations);
                orgCostRelationsDao.save(openEbsCompRelations);
            });
        }
    }

    private void syncCoeCostItems(BillConfig51TalkSyncReqDTO req) {
        List<CoeCostItemsDTO> configList = req.getCoeCostItems();
        List<OpenEbsCoeCostItems> srcList = coeCostItemsDao.list();
        if (!ObjectUtils.isEmpty(srcList)) {
            srcList.forEach(src -> coeCostItemsDao.deleteById(src.getId()));
        }
        if (!ObjectUtils.isEmpty(configList)) {
            configList.forEach(config -> {
                OpenEbsCoeCostItems openEbsCoeCostItems = new OpenEbsCoeCostItems();
                BeanUtils.copyProperties(config, openEbsCoeCostItems);
                coeCostItemsDao.save(openEbsCoeCostItems);
            });
        }
    }

}

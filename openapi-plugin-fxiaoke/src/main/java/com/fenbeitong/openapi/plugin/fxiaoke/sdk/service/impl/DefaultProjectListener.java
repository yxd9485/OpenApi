package com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.impl;

import com.fenbeitong.openapi.plugin.etl.service.IEtlService;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.dto.FxiaokeJobConfigDTO;
import com.fenbeitong.openapi.plugin.fxiaoke.sdk.service.IFxkProjectListener;
import com.fenbeitong.openapi.plugin.support.init.dao.OpenThirdEmployeeDao;
import com.fenbeitong.openapi.plugin.support.init.entity.OpenThirdEmployee;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.MapUtils;
import com.fenbeitong.openapi.plugin.util.NumericUtils;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import com.fenbeitong.openapi.sdk.dto.project.AddThirdProjectReqDTO;
import com.fenbeitong.openapi.sdk.dto.project.MemberEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.finhub.framework.common.service.aspect.ServiceAspect;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description 可配置化的组织架构监听
 * @Author duhui
 * @Date 2020-11-26
 **/

@ServiceAspect
@Service
public class DefaultProjectListener implements IFxkProjectListener {

    @Autowired
    IEtlService iEtlService;
    @Autowired
    OpenEmployeeServiceImpl openEmployeeService;
    @Autowired
    OpenThirdEmployeeDao openThirdEmployeeDao;

    @Override
    public List<AddThirdProjectReqDTO> filterProjectBefore(FxiaokeJobConfigDTO fxiaokeJobConfigDTO, List<Map<String, Object>> dataList) {
        List<AddThirdProjectReqDTO> addThirdProjectReqDTOList = new ArrayList<>();
        List<Map> mapList = iEtlService.transform(fxiaokeJobConfigDTO.getEtlConfigId(), dataList);
        List<OpenThirdEmployee> openThirdEmployees = openThirdEmployeeDao.listEmployeeByCompanyId(fxiaokeJobConfigDTO.getCompanyId());
        Set<String> thirdEmployeeIdSet = openThirdEmployees.stream().map(t -> t.getThirdEmployeeId()).collect(Collectors.toSet());

        mapList.forEach(t -> {
            AddThirdProjectReqDTO addThirdProjectReqDTO = new AddThirdProjectReqDTO();
            if (!ObjectUtils.isEmpty(t.get("code"))) {
                addThirdProjectReqDTO.setCode(StringUtils.obj2str(t.get("code")));
                addThirdProjectReqDTO.setThirdCostId(StringUtils.obj2str(t.get("code")));
            }
            if (!ObjectUtils.isEmpty(t.get("name"))) {
                addThirdProjectReqDTO.setName(StringUtils.obj2str(t.get("name")));
            }
            if (!ObjectUtils.isEmpty(t.get("beginDate")) && !ObjectUtils.isEmpty(t.get("endDate"))) {
                addThirdProjectReqDTO.setBeginDate(DateUtils.toSimpleStr(NumericUtils.obj2long(t.get("beginDate"))));
                addThirdProjectReqDTO.setEndDate(DateUtils.toSimpleStr(NumericUtils.obj2long(t.get("endDate"))));
            }
            if (!ObjectUtils.isEmpty(t.get("expiredState"))) {
                addThirdProjectReqDTO.setExpiredState(NumericUtils.obj2int(t.get("expiredState")));
            }
            if (!ObjectUtils.isEmpty(t.get("usableRange"))) {
                addThirdProjectReqDTO.setUsableRange(NumericUtils.obj2int(t.get("usableRange")));
            }
            if (!ObjectUtils.isEmpty(t.get("state"))) {
                addThirdProjectReqDTO.setState(NumericUtils.obj2int(t.get("state")));
            }
            addThirdProjectReqDTO.setCompanyId(fxiaokeJobConfigDTO.getCompanyId());
            addThirdProjectReqDTO.setUserId(fxiaokeJobConfigDTO.getCurrentOpenUserId());
            addThirdProjectReqDTO.setType(2);
            Map manager = (Map) MapUtils.getValueByExpress(t, "manager");
            if (!ObjectUtils.isEmpty(manager)) {
                List<String> list = (List<String>) MapUtils.getValueByExpress(manager, "memberId");
                if (!ObjectUtils.isEmpty(list)) {
                    List<MemberEntity> memberEntityList = new ArrayList<>();
                    list.forEach(l -> {
                        if (thirdEmployeeIdSet.contains(l)) {
                            MemberEntity memberEntity = new MemberEntity();
                            memberEntity.setMemberId(l);
                            memberEntityList.add(memberEntity);
                        }
                    });
                    if (memberEntityList.size() > 0) {
                        addThirdProjectReqDTO.setManager(memberEntityList);
                    }
                }
            }
            addThirdProjectReqDTOList.add(addThirdProjectReqDTO);
        });
        return addThirdProjectReqDTOList;
    }
}

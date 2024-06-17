package com.fenbeitong.openapi.plugin.dingtalk.isv.service.impl;

import com.dingtalk.api.request.OapiAttendanceApproveFinishRequest;
import com.dingtalk.api.response.OapiProcessinstanceGetResponse;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkApprovalFormDTO;
import com.fenbeitong.openapi.plugin.dingtalk.common.DingtalkResponseCode;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingTalkKitConstant;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.DingtalkKitValueConstant;
import com.fenbeitong.openapi.plugin.dingtalk.common.constant.IFormFieldAliasConstant;
import com.fenbeitong.openapi.plugin.dingtalk.common.exception.OpenApiDingtalkException;
import com.fenbeitong.openapi.plugin.dingtalk.common.service.DingtalkAttendanceApproveImpl;
import com.fenbeitong.openapi.plugin.dingtalk.common.service.apply.kit.DingtalkTripKitApplyFormParserServiceImpl;
import com.fenbeitong.openapi.plugin.dingtalk.dto.DingtalkTripCommonApplyDTO;
import com.fenbeitong.openapi.plugin.dingtalk.dto.DingtalkTripKitTravelTimeDTO;
import com.fenbeitong.openapi.plugin.dingtalk.eia.constant.DingtalkProcessBizActionType;
import com.fenbeitong.openapi.plugin.dingtalk.eia.entity.DingtalkApply;
import com.fenbeitong.openapi.plugin.dingtalk.isv.constant.DingtalkIsvConstant;
import com.fenbeitong.openapi.plugin.dingtalk.isv.dto.DingtalkAttendanceDto;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.DingtalkIsvCompany;
import com.fenbeitong.openapi.plugin.dingtalk.isv.entity.OpenSyncBizDataMedium;
import com.fenbeitong.openapi.plugin.dingtalk.isv.service.IDingtalkIsvCompanyAuthService;
import com.fenbeitong.openapi.plugin.support.apply.constant.ApplyTypeConstant;
import com.fenbeitong.openapi.plugin.support.apply.dao.OpenApplyRecordDao;
import com.fenbeitong.openapi.plugin.support.apply.dto.*;
import com.fenbeitong.openapi.plugin.support.apply.entity.OpenApplyRecord;
import com.fenbeitong.openapi.plugin.support.apply.service.CommonApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.apply.service.IOpenTripApplyService;
import com.fenbeitong.openapi.plugin.support.apply.service.impl.OpenApplyServiceImpl;
import com.fenbeitong.openapi.plugin.support.common.dto.FbCostAttributionDTO;
import com.fenbeitong.openapi.plugin.support.common.service.impl.OpenIdTranServiceImpl;
import com.fenbeitong.openapi.plugin.support.init.service.impl.OpenEmployeeServiceImpl;
import com.fenbeitong.openapi.plugin.support.task.enums.TaskResult;
import com.fenbeitong.openapi.plugin.support.util.DateUtil;
import com.fenbeitong.openapi.plugin.util.*;
import com.fenbeitong.usercenter.api.model.dto.auth.LoginResVO;
import com.fenbeitong.usercenter.api.model.dto.auth.UserInfoVO;
import com.fenbeitong.usercenter.api.model.enums.common.IdBusinessTypeEnums;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>Title: DingtalkCarApplyServiceImpl</p>
 * <p>Description: 差旅套件审批单</p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author xiaohai
 * @date 2021/09/10 10:57 PM
 */
@Slf4j
@Service
public class DingtalkIsvTripkitApplyServiceImpl extends AbstractDingtalkIsvApplyService {


    @Autowired
    private DingtalkTripKitApplyFormParserServiceImpl formParser;

    @Autowired
    private CommonApplyServiceImpl commonApplyService;

    @Autowired
    private OpenEmployeeServiceImpl openEmployeeService;

    @Autowired
    private IOpenTripApplyService openTripApplyService;

    @Autowired
    private DingtalkAttendanceApproveImpl dingtalkAttendanceApprove;

    @Autowired
    private IDingtalkIsvCompanyAuthService dingtalkIsvCompanyAuthService;

    @Autowired
    private OpenIdTranServiceImpl openIdTranService;

    @Autowired
    private OpenApplyServiceImpl openApplyService;

    @Autowired
    private OpenApplyRecordDao openApplyRecordDao;

    @Value("${dingtalk.host}")
    private String dingtalkHost;

    @Value("${host.dd_webapp}")
    private String webappHost;

    @Override
    public TaskResult processApply(OpenSyncBizDataMedium task, DingtalkIsvCompany dingtalkIsvCompany, DingtalkApply apply, OapiProcessinstanceGetResponse.ProcessInstanceTopVo processInstanceTopVo) {
        LoginResVO loginResVO = openEmployeeService.loginAuthInit(dingtalkIsvCompany.getCompanyId() , processInstanceTopVo.getOriginatorUserid(), "1");
        String ucToken = loginResVO.getLogin_info().getToken();
        String originatorUserid = processInstanceTopVo.getOriginatorUserid();
        String bizData = task.getBizData();
        DingtalkApprovalFormDTO dingtalkIsvTripApprovalFormDTO = JsonUtils.toObj(bizData, DingtalkApprovalFormDTO.class);
        //审批单三方id
        String processInstanceId = Optional.ofNullable(dingtalkIsvTripApprovalFormDTO).map(DingtalkApprovalFormDTO::getProcessInstanceId).orElse(null);
        String bizAction = dingtalkIsvTripApprovalFormDTO.getBizAction();
        if(DingtalkProcessBizActionType.REVOKE.getValue().equalsIgnoreCase(bizAction)){
            //撤销审批单
            revokeTripApprove( dingtalkIsvCompany ,  ucToken , dingtalkIsvTripApprovalFormDTO , originatorUserid );
        }else{
            CommonApplyReqDTO commonApplyReqDTO = initApplyReqDTO(task.getBizId(), loginResVO, dingtalkIsvCompany.getCompanyId());
            formParser.parser( bizData, commonApplyReqDTO);
            try{
                DingtalkAttendanceDto attendanceDto = DingtalkAttendanceDto.builder().
                    thirdApplyId(processInstanceId).
                    guestList(commonApplyReqDTO.getGuestList()).
                    dingtalkIsvTripApprovalFormDTO(dingtalkIsvTripApprovalFormDTO).
                    corpId(dingtalkIsvCompany.getCorpId()).
                    companyId(dingtalkIsvCompany.getCompanyId()).
                    applicantId(originatorUserid).build();
                if(DingtalkProcessBizActionType.NONE.getValue().equalsIgnoreCase(bizAction)){
                    //正常发起,创建审批单
                    createTripApprove(  ucToken  ,  attendanceDto , commonApplyReqDTO);
                }else if(DingtalkProcessBizActionType.MODIFY.getValue().equalsIgnoreCase(bizAction)){
                    //变更
                    DingtalkAttendanceDto cancelAttendanceDto = DingtalkAttendanceDto.builder().
                        corpId(dingtalkIsvCompany.getCorpId()).
                        companyId(dingtalkIsvCompany.getCompanyId()).
                        applicantId(originatorUserid).
                        thirdApplyId(dingtalkIsvTripApprovalFormDTO.getMainProcessInstanceId()).build();
                    changeTripApprove(  ucToken  , attendanceDto, cancelAttendanceDto , commonApplyReqDTO);
                }
            }catch (Exception e){
                throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_SYNC_APPLY_FAILED, e.getMessage());
            }
        }
        //记录审批实例信息
        saveDingtalkProcessInstance(task, apply, processInstanceTopVo);
        return TaskResult.SUCCESS;
    }

    /**
     * 判断审批单是行程审批单还是非行程审批单
     * @param dingtalkIsvTripApprovalFormDTO
     * @return  true : 行程  false：非行程
     */
    private boolean checkIsTripApprove(DingtalkApprovalFormDTO dingtalkIsvTripApprovalFormDTO) {
        List<DingtalkApprovalFormDTO.FormValueVOS> formValueVOSList = dingtalkIsvTripApprovalFormDTO.getFormValueVOS();
        String tripInfoMap = formValueVOSList.stream()
            .filter(formValueVO -> IFormFieldAliasConstant.CORP_ID_TEXT.equals(formValueVO.getBizAlias()))
            .findFirst().map(DingtalkApprovalFormDTO.FormValueVOS::getValue).map(Object::toString).orElse(null);
        Boolean isTrip = true;
        Map map = JsonUtils.toObj(tripInfoMap, Map.class);
        //只有isTrip是false时是非行程，否则是行程
        if (map.containsKey("isTrip") && !isTrip.equals(map.get("isTrip"))) {
            return false;
        }
        return true;
    }


    /**
     *  行程审批单创建
     * @param commonApplyReqDTO  ：通用审批单信息
     * @param ucToken
     * @param attendanceDto ：考勤数据
     */
    private void createTripApprove( String ucToken , DingtalkAttendanceDto attendanceDto, CommonApplyReqDTO commonApplyReqDTO) throws Exception{
        DingtalkApprovalFormDTO dingtalkIsvTripApprovalFormDTO = attendanceDto.getDingtalkIsvTripApprovalFormDTO();
        if(checkIsTripApprove(dingtalkIsvTripApprovalFormDTO)){
            //正常发起,创建行程审批单
            TripApproveCreateReqDTO req = commonApplyService.buildTripApproveCreateReq(commonApplyReqDTO, ucToken, false);
            CreateApplyRespDTO tripApprove = openTripApplyService.createTripApprove(ucToken, req);
            attendanceDto.setApplyId( tripApprove.getId() );
        }else{
            //正常发起,创建非行程审批单 查询出差时间是否展示
            MultiTripApproveCreateReqDTO multiTripReq = buildMultiTrip( commonApplyReqDTO , dingtalkIsvTripApprovalFormDTO);
            boolean close = checkTravelStatisticsClose(attendanceDto.getCompanyId());
            if(close){
                multiTripReq.getApply().setTravelDay(null);
                multiTripReq.getApply().setTravelTimeList(CollectionUtils.newArrayList());
            }
            String applyId = openTripApplyService.createMultiTripApply(ucToken, multiTripReq, attendanceDto.getCompanyId());
            attendanceDto.setApplyId( applyId );
        }
        //同步考勤
        pushAttendanceNew( attendanceDto );
    }

    /**
     * 判断差旅统计按钮是否是关闭状态
     * @param companyId
     * @return false:打开  true：关闭状态
     */
    private boolean checkTravelStatisticsClose(String companyId){
        //出差时间
        Map<String,Object> param = new HashMap<>();
        param.put("companyId" , companyId);
        Object travelStatistics = openApplyService.getTravelStatistics( param );
        Map<String,Object> travelStatisticsMap = JsonUtils.toObj(JsonUtils.toJson(travelStatistics), Map.class);
        if(travelStatisticsMap == null){
             return false;
        }
        //0:关闭 1:开启
        String whetherTravelStaticsRequired = StringUtils.obj2str( travelStatisticsMap.get("whether_travel_statistics") );
        return DingTalkKitConstant.TravelStatics.TRAVEL_STATICS_CLOSE.equals(whetherTravelStaticsRequired) ? true : false;
    }



    private CommonApply.CostInfo convertCostInfo(List<CostAttributionDTO> costAttributionList){
        if(CollectionUtils.isBlank(costAttributionList)){
            return null;
        }
        CommonApply.CostInfo costInfo = new CommonApply.CostInfo();
        List<FbCostAttributionDTO> costAttributionNewList = CollectionUtils.newArrayList();
        costAttributionList.forEach(
            costAttributionDTO -> {
                List<FbCostAttributionDTO.CostAttributionListDTO> details = CollectionUtils.newArrayList();
                FbCostAttributionDTO.CostAttributionListDTO detail = new FbCostAttributionDTO.CostAttributionListDTO();
                detail.setId(costAttributionDTO.getCostAttributionId());
                detail.setName(costAttributionDTO.getCostAttributionName());
                details.add(detail);
                FbCostAttributionDTO costAttributionNewDTO = new FbCostAttributionDTO();
                costAttributionNewDTO.setCategory(costAttributionDTO.getCostAttributionCategory());
                costAttributionNewDTO.setCostAttributionList(details);
                costAttributionNewList.add(costAttributionNewDTO);
            }
        );
        costInfo.setFbCostAttributionDTOList(costAttributionNewList);
        return costInfo;
    }

    private MultiTripApproveCreateReqDTO buildMultiTrip(CommonApplyReqDTO commonApplyReqDTO , DingtalkApprovalFormDTO dingtalkIsvTripApprovalFormDTO){
        MultiTripApproveCreateReqDTO multiTripInfo = new MultiTripApproveCreateReqDTO();
        multiTripInfo.setApply(buildMultiTripApply( commonApplyReqDTO));
        multiTripInfo.setTrip( commonApplyReqDTO.getMultiTrip());
        multiTripInfo.setGuestList(buildMultiGuestList(dingtalkIsvTripApprovalFormDTO));
        DingtalkTripKitTravelTimeDTO timeDTO = getTimeDTO( dingtalkIsvTripApprovalFormDTO );
        MultiTripDTO trip = multiTripInfo.getTrip();
        trip.setStartTime(DateUtil.parseDate(timeDTO.getStartTime() , "yyyy-MM-dd"));
        trip.setEndTime(DateUtil.parseDate(timeDTO.getEndTime() , "yyyy-MM-dd"));
        CommonApply.CostInfo costInfo = convertCostInfo(commonApplyReqDTO.getApply().getCostAttributionList());
        //金额转成分
        BigDecimal estimatedAmount = trip.getEstimatedAmount();
        if(estimatedAmount!=null){
            BigDecimal bigDecimal = BigDecimalUtils.yuan2fen(  BigDecimalUtils.obj2big( estimatedAmount ) );
            trip.setEstimatedAmount( bigDecimal );
        }else{
            trip.setEstimatedAmount( new BigDecimal(0) );
        }
        multiTripInfo.getApply().setBudget(trip.getEstimatedAmount());
        multiTripInfo.getApply().setCostAttributionList(null);
        multiTripInfo.getApply().setCostInfo(costInfo);
        DingtalkTripCommonApplyDTO commonApplyDTO = (DingtalkTripCommonApplyDTO) commonApplyReqDTO.getApply();
        multiTripInfo.setUseCarFlag(commonApplyDTO.getUseCarFlag());
        return multiTripInfo;
    }

    private MultiTripApplyDTO buildMultiTripApply(CommonApplyReqDTO commonApplyReqDTO){
        MultiTripApplyDTO apply = new MultiTripApplyDTO();
        CommonApply commonApply = commonApplyReqDTO.getApply();
        apply.setThirdId( commonApply.getThirdId() );
        apply.setEmployeeId( commonApply.getEmployeeId() );
        apply.setApplyReason(commonApply.getApplyReason());
        apply.setApplyReasonDesc(commonApply.getApplyReasonDesc());
        apply.setCostAttributionList(commonApply.getCostAttributionList());
        apply.setTravelTimeList(commonApplyReqDTO.getApply().getTravelTimeList());
        apply.setTravelDay(commonApplyReqDTO.getApply().getTravelDay());
        return apply;
    }

    private List<MultiGuestListDTO> buildMultiGuestList(DingtalkApprovalFormDTO dingtalkIsvTripApprovalFormDTO){
        List<DingtalkApprovalFormDTO.FormValueVOS> formValueVOSList = dingtalkIsvTripApprovalFormDTO.getFormValueVOS();
        List<MultiGuestListDTO> multiGuestList = CollectionUtils.newArrayList();
        if (CollectionUtils.isNotBlank(formValueVOSList)) {
            String guestListExtVal = formValueVOSList.stream()
                .filter(formValueVO -> IFormFieldAliasConstant.TRAVEL_TRAVELER.equals(formValueVO.getBizAlias()))
                .findFirst().map(DingtalkApprovalFormDTO.FormValueVOS::getExtValue).map(Object::toString).orElse(null);
            // 行程表单信息
            if(StringUtils.isBlank(guestListExtVal)){
                return multiGuestList;
            }
            String guestExt = StringEscapeUtils.unescapeJava(guestListExtVal);
            List<Map> guestList = JsonUtils.toObj(guestExt, List.class, Map.class);
            if(CollectionUtils.isNotBlank(guestList)){
                guestList.forEach(guestMap-> multiGuestList.add(MultiGuestListDTO.builder().name(StringUtils.obj2str(guestMap.get("name"))).
                    thirdEmployeeId(StringUtils.obj2str(guestMap.get("emplId"))).build()));
            }
        }
        return multiGuestList;
    }



    /**
     *
     * @param ucToken
     * @param commonApplyReqDTO：通用审批单数据
     * @param attendanceDto :创建考勤数据
     * @param cancelAttendanceDto ：撤销考勤数据
     * @throws Exception
     */
    private void changeTripApprove( String ucToken ,
                                    DingtalkAttendanceDto attendanceDto,
                                    DingtalkAttendanceDto cancelAttendanceDto,
                                    CommonApplyReqDTO commonApplyReqDTO) throws Exception{
        DingtalkApprovalFormDTO dingtalkIsvTripApprovalFormDTO = attendanceDto.getDingtalkIsvTripApprovalFormDTO();
        if(checkIsTripApprove(dingtalkIsvTripApprovalFormDTO)){
            //变更行程
            TripApproveCreateReqDTO req = commonApplyService.buildTripApproveCreateReq(commonApplyReqDTO, ucToken, false);
            TripApproveChangeReqDTO changReq = new TripApproveChangeReqDTO();
            TripApproveApply reqApply = req.getApply();
            BeanUtils.copyProperties( req , changReq );
            TripApproveChangeApply changeApply = new TripApproveChangeApply();
            BeanUtils.copyProperties( reqApply , changeApply );
            changReq.setThirdType(ApplyTypeConstant.APPLY_TYPE_THIRD);
            //原审批单id
            changReq.setApplyId(cancelAttendanceDto.getThirdApplyId());
            changReq.setApply(changeApply);
            CreateApplyRespDTO createApplyRespDTO = openTripApplyService.changeTripApprove(ucToken, changReq);
            attendanceDto.setApplyId(createApplyRespDTO.getId());
            cancelAttendanceDto.setApplyId(createApplyRespDTO.getId());
        }else{
            //变更非行程
            MultiTripApproveCreateReqDTO multiTripReq = buildMultiTrip(commonApplyReqDTO , dingtalkIsvTripApprovalFormDTO);
            //原审批单id
            multiTripReq.setApplyId(cancelAttendanceDto.getThirdApplyId());
            multiTripReq.setThirdType(ApplyTypeConstant.APPLY_TYPE_THIRD);
            boolean close = checkTravelStatisticsClose(attendanceDto.getCompanyId());
            if(close){
                multiTripReq.getApply().setTravelDay(null);
                multiTripReq.getApply().setTravelTimeList(CollectionUtils.newArrayList());
            }
            String applyid = openTripApplyService.changeMultiTripApply(ucToken, multiTripReq, attendanceDto.getCompanyId());
            attendanceDto.setApplyId(applyid);
            cancelAttendanceDto.setApplyId(applyid);
        }
        //撤销原单的考勤数据
        cancelAttendance(ucToken , cancelAttendanceDto);
        pushAttendanceNew( attendanceDto );
    }

    /**
     *  行程审批单撤销
     * @param dingtalkIsvCompany  ：企业信息
     * @param ucToken
     * @param dingtalkIsvTripApprovalFormDTO ：表单数据
     * @param applicantId :申请人三方id
     */
    private void revokeTripApprove(DingtalkIsvCompany dingtalkIsvCompany , String ucToken ,
                                   DingtalkApprovalFormDTO dingtalkIsvTripApprovalFormDTO ,String applicantId ){
        try {
            //通过三方id查询审批单信息
            int type = checkIsTripApprove(dingtalkIsvTripApprovalFormDTO) ? 1 : 23;
            OpenApplyRecord openApplyRecord = openApplyRecordDao.getOpenApplyRecord(dingtalkIsvCompany.getCompanyId(), dingtalkIsvTripApprovalFormDTO.getMainProcessInstanceId(), type);
            if(openApplyRecord == null){
                log.info("未查询到对应的审批单，三方审批单ID：{}", dingtalkIsvTripApprovalFormDTO.getMainProcessInstanceId());
            }
            TripApproveChangeApply changeApply = new TripApproveChangeApply();
            changeApply.setCompanyId(dingtalkIsvCompany.getCompanyId());
            TripApproveChangeReqDTO changReq = TripApproveChangeReqDTO.builder().thirdType(ApplyTypeConstant.APPLY_TYPE_THIRD).applyId(dingtalkIsvTripApprovalFormDTO.getMainProcessInstanceId()).
                apply(changeApply).build();
            //撤销审批单信息
            openTripApplyService.cancelTripApprove(ucToken, changReq);
            //原审批单信息
            DingtalkAttendanceDto attendanceDto = DingtalkAttendanceDto.builder()
                .companyId(dingtalkIsvCompany.getCompanyId())
                .corpId(dingtalkIsvCompany.getCorpId())
                .applicantId(applicantId)
                .thirdApplyId(dingtalkIsvTripApprovalFormDTO.getMainProcessInstanceId())
                .applyId(openApplyRecord.getApplyId())
                .build();
            //撤销考勤
            cancelAttendance(ucToken , attendanceDto );
        } catch (Exception e) {
            throw new OpenApiDingtalkException(DingtalkResponseCode.DINGTALK_SYNC_APPLY_FAILED, e);
        }
    }


    private CommonApplyReqDTO initApplyReqDTO(String instanceId,  LoginResVO loginResVO,String companyId) {
        //部门名称
        String orgName = loginResVO.getCompany_info().getOrg_unit().getName();
        //部门id,部门传值为空，如果传固定值，费用归属部门会新多一条部门信息
        String orgId = "";
        CommonApplyReqDTO commonApplyReqDTO = new CommonApplyReqDTO();
        DingtalkTripCommonApplyDTO commonApply = formParser.buildApply(instanceId, orgId, orgName);
        commonApply.setCompanyId(companyId);
        commonApply.setEmployeeId(Optional.of(loginResVO).map(LoginResVO::getUser_info).map(UserInfoVO::getId).orElse(null));
        commonApplyReqDTO.setApply(commonApply);
        return commonApplyReqDTO;
    }


    /**
     * 同步考勤数据(新)
     * @param attendanceDto
     */
    private void pushAttendanceNew(DingtalkAttendanceDto attendanceDto){
        List<String> employeeThirdIdList = getEmployeeThirdIdList(attendanceDto.getGuestList(),attendanceDto.getApplicantId(),attendanceDto.getCompanyId());
        DingtalkTripKitTravelTimeDTO timeDTO = getTimeDTO( attendanceDto.getDingtalkIsvTripApprovalFormDTO() );
        if (CollectionUtils.isBlank(employeeThirdIdList) || ObjectUtils.isEmpty(timeDTO)){
            return;
        }
        try{
            String accessToken = dingtalkIsvCompanyAuthService.getCorpAccessTokenByCorpId(attendanceDto.getCorpId());
            employeeThirdIdList.stream().forEach(employeeThirdId->{
                OapiAttendanceApproveFinishRequest req = setAttendanceApproveFinishHalfHour(employeeThirdId, timeDTO , attendanceDto);
                dingtalkAttendanceApprove.approveFinish( req , accessToken , dingtalkHost );
            });
        }catch (Exception e){
            log.error("同步考勤失败！", e);
        }
    }

    /**
     * 获取需要同步考勤的三方人员列表
     * @param guestList 同行人(id为分贝通id，同步考勤时需专成三方id)
     * @param applyThirdId 申请提交人（id为三方id）
     * @return
     */
    private List<String> getEmployeeThirdIdList(List<CommonApplyGuest> guestList, String applyThirdId , String companyId) {
        if (CollectionUtils.isBlank(guestList)) {
            return Collections.singletonList(applyThirdId);
        }

        List<String> employeeIds = guestList.stream()
            .map(guest -> guest.getId())
            .filter(Objects::nonNull)
            .map(Objects::toString)
            .collect(Collectors.toList());
        return openIdTranService.fbIdToThirdIdBatch(companyId,
            employeeIds,
            IdBusinessTypeEnums.EMPLOYEE.getKey(),
            true).values().stream().collect(Collectors.toList()) ;
    }

    /**
     * 获取出差时间信息
     * @param dingtalkIsvTripApprovalFormDTO 钉钉数据
     * @return 出差时间信息
     */
    private DingtalkTripKitTravelTimeDTO getTimeDTO(DingtalkApprovalFormDTO dingtalkIsvTripApprovalFormDTO) {
        List<DingtalkApprovalFormDTO.FormValueVOS> formValueVOSList = dingtalkIsvTripApprovalFormDTO.getFormValueVOS();
        DingtalkTripKitTravelTimeDTO timeDTO = null;
        if (CollectionUtils.isNotBlank(formValueVOSList)) {
            String timeJsom = formValueVOSList.stream()
                .filter(formValueVO -> IFormFieldAliasConstant.TRAVEL_BUSINESS_TIME.equals(formValueVO.getBizAlias()))
                .findFirst().map(DingtalkApprovalFormDTO.FormValueVOS::getExtValue).map(Object::toString).orElse(null);
            timeJsom = StringEscapeUtils.unescapeJava(timeJsom);
            timeDTO = JsonUtils.toObj(timeJsom, DingtalkTripKitTravelTimeDTO.class);
        }
        return timeDTO;
    }

    /**
     * 取消考勤
     * @param attendanceDto
     */
    public void cancelAttendance(String ucToken ,  DingtalkAttendanceDto attendanceDto ) {
        try{
            //调用sass接口查询审批单信息获取原单同行人id
            CompanyApplyDetailReqDTO companyApplyDetailReqDTO = CompanyApplyDetailReqDTO.builder().applyId(attendanceDto.getApplyId()).build();
            Map<String, Object> saasApplyDetailMap = openApplyService.getCompanyApproveDetail(ucToken , companyApplyDetailReqDTO);
            ApplyDetailDTO saasApplyDetail = JsonUtils.toObj(JsonUtils.toJson(saasApplyDetailMap), ApplyDetailDTO.class);
            List<ApplyDetailDTO.GuestList> guestList = saasApplyDetail.getGuestList();
            List<CommonApplyGuest> commonApplyGuestList = new ArrayList<>();
            if(CollectionUtils.isBlank(guestList)){
                String employeeId = saasApplyDetail.getApply().getEmployeeId();
                attendanceDto.setApplicantId( openIdTranService.fbIdToThirdId(attendanceDto.getCompanyId() , employeeId ,IdBusinessTypeEnums.EMPLOYEE.getKey() ));
            }else{
                guestList.forEach(guest -> {
                    CommonApplyGuest commonApplyGuest = new CommonApplyGuest();
                    commonApplyGuest.setId(guest.getId());
                    commonApplyGuestList.add(commonApplyGuest);
                });
            }
            List<String> employeeThirdIdList = getEmployeeThirdIdList(commonApplyGuestList,attendanceDto.getApplicantId(),attendanceDto.getCompanyId());
            if (CollectionUtils.isBlank(employeeThirdIdList) ){
                return;
            }
            String accessToken = dingtalkIsvCompanyAuthService.getCorpAccessTokenByCorpId(attendanceDto.getCorpId());
            employeeThirdIdList.stream().forEach(employeeThirdId->{
                dingtalkAttendanceApprove.approveCancel( employeeThirdId , attendanceDto.getThirdApplyId()  , accessToken , dingtalkHost );
            });
        }catch(Exception e){
            log.error("撤销考勤失败！", e);
        }
    }

    /**
     * 出差时间同步精确到半天
     * @param thirdEmployeeId 三方用户id
     * @param timeDTO 出差时间信息
     * @param  attendanceDto 考勤相关审批单信息
     */
    private OapiAttendanceApproveFinishRequest setAttendanceApproveFinishHalfHour(String thirdEmployeeId , DingtalkTripKitTravelTimeDTO timeDTO, DingtalkAttendanceDto attendanceDto){
        OapiAttendanceApproveFinishRequest req = new OapiAttendanceApproveFinishRequest();
        req.setFromTime(DingtalkKitValueConstant.AM_TYPE.equals(timeDTO.getStartDayType()) ? (timeDTO.getStartTime() + "AM") : (timeDTO.getStartTime()  + "PM"));
        req.setToTime( DingtalkKitValueConstant.AM_TYPE.equals(timeDTO.getEndDayType()) ? (timeDTO.getEndTime()+ "AM") : (timeDTO.getEndTime() + "PM") );
        req.setUserid(thirdEmployeeId);
        // 1:加班 2:出差 3:请假
        req.setBizType(2L);
        req.setDurationUnit("halfDay");
        // 0:按自然日计算 1:按工作日计算
        req.setCalculateModel(0L);
        req.setTagName("出差");
        req.setApproveId(attendanceDto.getThirdApplyId());
        String uri = webappHost + String.format(DingtalkIsvConstant.DINGTALK_ISV_APP_HOME, attendanceDto.getCorpId());
        String url = uri + "url=application/trip/detail?type=2&apply_id="+attendanceDto.getApplyId();
        //出差审批页面（申请人）
        req.setJumpUrl( url );
        return req;
    }

}


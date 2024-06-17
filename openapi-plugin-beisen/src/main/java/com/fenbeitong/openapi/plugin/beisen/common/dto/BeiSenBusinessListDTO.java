package com.fenbeitong.openapi.plugin.beisen.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>Title: BeiSenBusinessListDTO</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author duhui
 * @date 2021/9/27 3:59 下午
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BeiSenBusinessListDTO {

    public DataBean Data;
    public Integer Code;
    public String Message;

    @Data
    public static class DataBean {
        public List<Businesslist> BusinessList;
        public Integer Total;
    }

    @Data
    public static class Businesslist {
        public Integer StartDateTimePeriod;
        public Integer StaffId;
        public String CardNumber;
        public String ApplyUser;
        public String DepartmentId;
        public String DocumentType;
        public Integer BusinessMarking;
        public String CreatedTime;
        public String SerialNumber;
        public List<Businessdetailssync> BusinessDetailsSync;
        public String ReservationUser;
        public String DurationDisplay;
        public String Reason;
        public String StdOrganizationCode;
        public String StaffEmail;
        public String StartDateTime;
        public String ApplyTime;
        public String ApproveStatus;
        public Integer DayValOfDuration;
        public String ParentId;
        public Integer BusinessDuration;
        public String OId;
        public String StdOrganization;
        public String ObjectId;
        public String StopDateTime;
        public String JobNumber;
        public String ModifiedTime;
        public Integer StopDateTimePeriod;
    }

    @Data
    public static class Businessdetailssync {
        public Integer StartDateTimePeriod;
        public Integer StaffId;
        public String CardNumber;
        public String CreatedTime;
        public Integer BusinessMarking;
        public String BusinessVehicle;
        public String SerialNumber;
        public String Address;
        public String DurationDisplay;
        public String Remark;
        public String StaffEmail;
        public String StartDateTime;
        public String DeparturePlace;
        public double DayValOfDuration;
        public String extsf011092581188086539;
        public String Destination;
        public Integer BusinessDuration;
        public String StopDateTime;
        public String JobNumber;
        public String ModifiedTime;
        public String ApproStatus;
        public Integer StopDateTimePeriod;
    }




}

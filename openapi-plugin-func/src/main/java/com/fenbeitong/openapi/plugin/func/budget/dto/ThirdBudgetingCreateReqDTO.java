package com.fenbeitong.openapi.plugin.func.budget.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fenbeitong.openapi.plugin.core.util.EnumValidator;
import com.fenbeitong.openapi.plugin.core.util.ValidatorUtils;
import com.fenbeitong.openapi.plugin.support.bill.constants.OrderCategory;
import com.fenbeitong.openapi.plugin.support.budget.dto.ThirdBudgetingDTO;
import com.fenbeitong.openapi.plugin.support.budget.enums.BudgetingCycle;
import com.fenbeitong.openapi.plugin.support.budget.enums.BudgetingObjectType;
import com.fenbeitong.openapi.plugin.support.budget.enums.BudgetingType;
import com.fenbeitong.openapi.plugin.util.DateUtils;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.BindException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 预算编制信息
 */
@Data
public class ThirdBudgetingCreateReqDTO extends ThirdBudgetingDTO {


}

package com.fenbeitong.openapi.plugin.demo.dto;

import lombok.Data;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

/**
 * validated demo
 * Created by log.chang on 2019/12/6.
 */
@Data
public class ValidatedDemoDTO {

    /**
     * 数据验证组1
     */
    public interface Group1 {
    }

    public interface Group2 {
    }

    public interface Group3 {
    }

    /**
     * 身份认证参数验证组
     */
    @GroupSequence({Default.class, Group1.class, Group2.class})
    public interface Group4 {
    }

    /**
     * 订单参数验证组
     */
    @GroupSequence({Default.class, Group1.class, Group3.class})
    public interface Group5 {
    }

    @NotNull(message = "{p1.notNull}", groups = Group1.class)
    private String p1;
    @NotNull(message = "{p2.notNull}", groups = Group1.class)
    private String p2;
    @NotNull(message = "{p3.notNull}", groups = Group3.class)
    private String p3;
    @NotNull(message = "{p4.notNull}", groups = Group4.class)
    private String p4;
    @NotNull(message = "{p5.notNull}", groups = Group5.class)
    private String p5;
    @NotNull(message = "{p6.notNull}", groups = Group5.class)
    private String p6;
    @NotNull(message = "{p7.notNull}")
    private String p7;

}

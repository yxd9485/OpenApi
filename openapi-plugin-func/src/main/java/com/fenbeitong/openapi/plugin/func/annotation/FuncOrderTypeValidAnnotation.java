package com.fenbeitong.openapi.plugin.func.annotation;

import org.springframework.util.ObjectUtils;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author helu
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FuncOrderTypeValidAnnotation.CheckValidator.class)
public @interface FuncOrderTypeValidAnnotation {

    /**
     * 订单类型，没有值时默认为1
     */
    int orderType() default 1;

    /**
     * 自定义异常返回信息
     */
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class CheckValidator implements ConstraintValidator<FuncOrderTypeValidAnnotation, Object> {

        @Override
        public boolean isValid(Object value, ConstraintValidatorContext context) {
            //为空时候传默认商务
            return ObjectUtils.isEmpty(value) || (Integer.valueOf(1).equals(value) || Integer.valueOf(2).equals(value));
        }
    }
}

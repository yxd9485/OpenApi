package com.fenbeitong.openapi.plugin.root;

import org.springframework.util.Assert;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

import javax.servlet.ServletRequest;

/**
 * <p>Title: CustomServletModelAttributeMethodProcessor</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2019/12/6 8:04 PM
 */
public class CustomServletModelAttributeMethodProcessor extends ServletModelAttributeMethodProcessor {

    public CustomServletModelAttributeMethodProcessor(final boolean annotationNotRequired) {
        super(annotationNotRequired);
    }

    @Override
    protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest request) {
        ServletRequest servletRequest = request.getNativeRequest(ServletRequest.class);
        Assert.state(servletRequest != null, "No ServletRequest");
        ServletRequestDataBinder servletBinder = (ServletRequestDataBinder) binder;
        /**
         * ServletModelAttributeMethodProcessor此处使用的servletBinder.bind(servletRequest)
         * 修改的目的是为了将ServletRequestDataBinder换成自定的CustomServletRequestDataBinder
         */
        new CustomServletRequestDataBinder(servletBinder.getTarget()).bind(servletRequest);
    }
}

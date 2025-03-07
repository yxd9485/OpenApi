package com.fenbeitong.openapi.plugin.root;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.web.bind.ServletRequestDataBinder;

import javax.servlet.ServletRequest;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Title: CustomServletRequestDataBinder</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2019/12/6 8:05 PM
 */
public class CustomServletRequestDataBinder extends ServletRequestDataBinder {

    private final Pattern underLinePattern = Pattern.compile("_(\\w)");

    public CustomServletRequestDataBinder(final Object target) {
        super(target);
    }

    @Override
    protected void addBindValues(MutablePropertyValues mpvs, ServletRequest request) {
        List<PropertyValue> pvs = mpvs.getPropertyValueList();
        List<PropertyValue> adds = new LinkedList<>();
        for (PropertyValue pv : pvs) {
            String name = pv.getName();
            String camel = this.underLineToCamel(name);
            if (!name.equals(camel)) {
                adds.add(new PropertyValue(camel, pv.getValue()));
            }
        }
        pvs.addAll(adds);
    }

    private String underLineToCamel(final String value) {
        final StringBuffer sb = new StringBuffer();
        Matcher m = this.underLinePattern.matcher(value);
        while (m.find()){
            m.appendReplacement(sb, m.group(1).toUpperCase());
        }
        m.appendTail(sb);
        return sb.toString();
    }
}

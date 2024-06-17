package com.fenbeitong.openapi.plugin;

import com.fenbeitong.openapi.plugin.event.order.service.AirplaneTicketOrderEventListenner;
import com.finhub.framework.core.SpringUtils;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;


/**
 * module: 应用模块名称<br/>
 * <p>
 * description: 描述<br/>
 *
 * @author FuQiang
 * @date 2022/6/17 00:38
 * @since 2.0
 */

@Slf4j
@RestController
@RequestMapping("/test/kafka")
public class KafkaController implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @RequestMapping("/sendOrder")
    public Object getSeeyonEmpDetail(@RequestParam String topic, @RequestParam String json) {
//        testAssist();
        kafkaTemplate.send(topic, json);
        return "success";
    }

    @SneakyThrows
    @RequestMapping("/assist")
    public String testAssist() {

        ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getBeanFactory();
        //反射获取Factory中的singletonObjects 将该名称下的bean进行替换
        Field singletonObjects = DefaultSingletonBeanRegistry.class.getDeclaredField("singletonObjects");
        ReflectionUtils.makeAccessible(singletonObjects);
        Map<String, Object> map = (Map<String, Object>) singletonObjects.get(beanFactory);

        AirplaneTicketOrderEventListenner eventListener = SpringUtils.getBean(AirplaneTicketOrderEventListenner.class);
        Field[] fs = eventListener.getClass().getDeclaredFields();
        for (Field f : fs) {
            log.info("参数为：{}", f.getName());
            if ("log".equals(f.getName())) {
                f.setAccessible(true);
                Logger logger = (Logger) f.get(eventListener);
                map.put(logger.getName() + "-logger", logger);
//                boolean r = setValue(eventListener, AirplaneTicketOrderEventListenner.class, "log", customLogger);
            }
        }
        log.info("响应结果：");
        return null;
    }

    public static boolean setValue(Object source, Class<?> target,
                                   String name, Object value) {
        Field field = null;
        int modify = 0;
        Field modifiersField = null;
        boolean removeFinal = false;
        try {
            field = target.getDeclaredField(name);
            modify = field.getModifiers();
            //final修饰的基本类型不可修改
            if (field.getType().isPrimitive() && Modifier.isFinal(modify)) {
                return false;
            }
            //获取访问权限
            if (!Modifier.isPublic(modify) || Modifier.isFinal(modify)) {
                field.setAccessible(true);
            }
            //static final同时修饰
            removeFinal = Modifier.isStatic(modify) && Modifier.isFinal(modify);
            if (removeFinal) {
                modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, modify & ~Modifier.FINAL);
            }
            //按照类型调用设置方法
            if (value != null && field.getType().isPrimitive()) {
                if ("int".equals(field.getType().getName()) && value instanceof Number) {
                    field.setInt(source, ((Number) value).intValue());
                } else if ("boolean".equals(field.getType().getName()) && value instanceof Boolean) {
                    field.setBoolean(source, (Boolean) value);
                } else if ("byte".equals(field.getType().getName()) && value instanceof Byte) {
                    field.setByte(source, (Byte) value);
                } else if ("char".equals(field.getType().getName()) && value instanceof Character) {
                    field.setChar(source, (Character) value);
                } else if ("double".equals(field.getType().getName()) && value instanceof Number) {
                    field.setDouble(source, ((Number) value).doubleValue());
                } else if ("long".equals(field.getType().getName()) && value instanceof Number) {
                    field.setLong(source, ((Number) value).longValue());
                } else if ("float".equals(field.getType().getName()) && value instanceof Number) {
                    field.setFloat(source, ((Number) value).floatValue());
                } else if ("short".equals(field.getType().getName()) && value instanceof Number) {
                    field.setShort(source, ((Number) value).shortValue());
                } else {
                    return false;
                }
            } else {
                field.set(source, value);
            }
        } catch (Exception e) {
            return false;
        } finally {
            try {
                //权限还原
                if (field != null) {
                    if (removeFinal && modifiersField != null) {
                        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                        modifiersField.setAccessible(false);
                    }
                    if (!Modifier.isPublic(modify) || Modifier.isFinal(modify)) {
                        field.setAccessible(false);
                    }
                }
            } catch (IllegalAccessException e) {
                //
                e.printStackTrace();
            }
        }
        return true;
    }

    public static class CustomLogger implements Logger {

        private Logger internalLogger;

        public CustomLogger(Logger internalLogger) {
            this.internalLogger = internalLogger;
        }

        @Override
        public String getName() {
            return internalLogger.getName();
        }

        @Override
        public boolean isTraceEnabled() {
            return internalLogger.isTraceEnabled();
        }

        @Override
        public void trace(String s) {
            internalLogger.trace(s);
        }

        @Override
        public void trace(String s, Object o) {
            internalLogger.trace(s, o);
        }

        @Override
        public void trace(String s, Object o, Object o1) {
            internalLogger.trace(s, o, o1);
        }

        @Override
        public void trace(String s, Object... objects) {
            internalLogger.trace(s, objects);
        }

        @Override
        public void trace(String s, Throwable throwable) {
            internalLogger.trace(s, throwable);
        }

        @Override
        public boolean isTraceEnabled(Marker marker) {
            return internalLogger.isTraceEnabled(marker);
        }

        @Override
        public void trace(Marker marker, String s) {
            internalLogger.trace(marker, s);
        }

        @Override
        public void trace(Marker marker, String s, Object o) {
            internalLogger.trace(marker, s, o);
        }

        @Override
        public void trace(Marker marker, String s, Object o, Object o1) {
            internalLogger.trace(marker, s, o, o1);
        }

        @Override
        public void trace(Marker marker, String s, Object... objects) {
            internalLogger.trace(marker, s, objects);
        }

        @Override
        public void trace(Marker marker, String s, Throwable throwable) {
            internalLogger.trace(marker, s, throwable);
        }

        @Override
        public boolean isDebugEnabled() {
            return internalLogger.isDebugEnabled();
        }

        @Override
        public void debug(String s) {
            internalLogger.debug(s);
        }

        @Override
        public void debug(String s, Object o) {
            internalLogger.debug(s, o);
        }

        @Override
        public void debug(String s, Object o, Object o1) {

        }

        @Override
        public void debug(String s, Object... objects) {

        }

        @Override
        public void debug(String s, Throwable throwable) {

        }

        @Override
        public boolean isDebugEnabled(Marker marker) {
            return false;
        }

        @Override
        public void debug(Marker marker, String s) {

        }

        @Override
        public void debug(Marker marker, String s, Object o) {

        }

        @Override
        public void debug(Marker marker, String s, Object o, Object o1) {

        }

        @Override
        public void debug(Marker marker, String s, Object... objects) {

        }

        @Override
        public void debug(Marker marker, String s, Throwable throwable) {

        }

        @Override
        public boolean isInfoEnabled() {
            return false;
        }

        @Override
        public void info(String s) {

        }

        @Override
        public void info(String s, Object o) {
            internalLogger.info(s, o);
        }

        @Override
        public void info(String s, Object o, Object o1) {

        }

        @Override
        public void info(String s, Object... objects) {

        }

        @Override
        public void info(String s, Throwable throwable) {

        }

        @Override
        public boolean isInfoEnabled(Marker marker) {
            return false;
        }

        @Override
        public void info(Marker marker, String s) {

        }

        @Override
        public void info(Marker marker, String s, Object o) {

        }

        @Override
        public void info(Marker marker, String s, Object o, Object o1) {

        }

        @Override
        public void info(Marker marker, String s, Object... objects) {

        }

        @Override
        public void info(Marker marker, String s, Throwable throwable) {

        }

        @Override
        public boolean isWarnEnabled() {
            return false;
        }

        @Override
        public void warn(String s) {

        }

        @Override
        public void warn(String s, Object o) {

        }

        @Override
        public void warn(String s, Object... objects) {

        }

        @Override
        public void warn(String s, Object o, Object o1) {

        }

        @Override
        public void warn(String s, Throwable throwable) {

        }

        @Override
        public boolean isWarnEnabled(Marker marker) {
            return false;
        }

        @Override
        public void warn(Marker marker, String s) {

        }

        @Override
        public void warn(Marker marker, String s, Object o) {

        }

        @Override
        public void warn(Marker marker, String s, Object o, Object o1) {

        }

        @Override
        public void warn(Marker marker, String s, Object... objects) {

        }

        @Override
        public void warn(Marker marker, String s, Throwable throwable) {

        }

        @Override
        public boolean isErrorEnabled() {
            return false;
        }

        @Override
        public void error(String s) {

        }

        @Override
        public void error(String s, Object o) {

        }

        @Override
        public void error(String s, Object o, Object o1) {

        }

        @Override
        public void error(String s, Object... objects) {

        }

        @Override
        public void error(String s, Throwable throwable) {

        }

        @Override
        public boolean isErrorEnabled(Marker marker) {
            return false;
        }

        @Override
        public void error(Marker marker, String s) {

        }

        @Override
        public void error(Marker marker, String s, Object o) {

        }

        @Override
        public void error(Marker marker, String s, Object o, Object o1) {

        }

        @Override
        public void error(Marker marker, String s, Object... objects) {

        }

        @Override
        public void error(Marker marker, String s, Throwable throwable) {

        }
    }

}

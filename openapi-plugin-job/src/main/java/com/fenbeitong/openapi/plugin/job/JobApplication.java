package com.fenbeitong.openapi.plugin.job;

import com.fenbeitong.finhub.kafka.producer.impl.KafkaProducerPublisher;
import com.fenbeitong.finhub.kafka.producer.impl.SaturnKafkaProducerPublisher;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * job模块的启动类
 *
 * @author ctl
 * @date 2022/7/11
 */
@ComponentScan(value = {"com.fenbeitong"}, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {SaturnKafkaProducerPublisher.class, KafkaProducerPublisher.class}))
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, MybatisAutoConfiguration.class, RocketMQAutoConfiguration.class})
@SpringBootConfiguration
@EnableCaching
@EnableScheduling
@EnableAsync(proxyTargetClass = true)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ServletComponentScan(basePackages = "com.fenbeitong")
@EnableDubbo
public class JobApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobApplication.class, args);
    }
}

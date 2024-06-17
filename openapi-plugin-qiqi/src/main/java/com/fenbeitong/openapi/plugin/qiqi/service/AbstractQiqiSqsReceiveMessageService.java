package com.fenbeitong.openapi.plugin.qiqi.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.opensdk.BaseRequest;
import com.amazonaws.opensdk.SdkRequestConfig;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.q7link.openapi.Openapi;
import com.q7link.openapi.model.GetQueueRequest;
import com.q7link.openapi.model.Queue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName AbstractQiqiSqsReceiveMessageService
 * @Description 企企消息消费方法
 * @Company www.fenbeitong.com
 * @Author wangxd
 * @Date 2022/05/19
 **/
@Component
@Slf4j
public class AbstractQiqiSqsReceiveMessageService {

    private static Regions regions = Regions.CN_NORTHWEST_1;
    private static AmazonSQS amazonSQS;
    private static Openapi openapi;

    /**
     * 接收消息,推荐使用长轮询
     * <pre>
     *   1.最大等待时间：20秒 值范围：1-20秒
     *   2.最大接收消息数：10 值范围：1-10
     *   3.默认可见性超时：60秒 值范围：0-12天
     *      根据业务需求适当调整可见性超时时间
     * </pre>
     *
     * @param queueUrl 队列url
     * @return 消息列表
     */
    public static List<Message> receiveMessages(String queueUrl, String accessKeyId,String secret) {
        ReceiveMessageRequest request = new ReceiveMessageRequest()
            // 队列url
            .withQueueUrl(queueUrl)
            // 可见性超时：默认60秒，收到消息在可见性超时时间内不会再次收到该消息。注意：可见性超时无法保证不会接收消息两次
            .withVisibilityTimeout(10)
            // 最大等待时间：20秒 值范围：1-20秒
            .withWaitTimeSeconds(20)
            // 最大接收消息数：10 值范围：1-10
            .withMaxNumberOfMessages(10);
        return amazonSQS(accessKeyId,secret).receiveMessage(request).getMessages();
    }

    /**
     * 获取队列信息
     *
     * @return 队列对象
     */
    public static Queue getQueue(String accessKeyId,String secret,String openId) {
        GetQueueRequest request = new GetQueueRequest();
        request.sdkRequestConfig(getSdkRequestConfig(request, accessKeyId, openId));
        return openapi(accessKeyId, secret).getQueue(request).getQueue();
    }

    public static SdkRequestConfig getSdkRequestConfig(BaseRequest request, String accessKeyId,String openId) {
        return request.sdkRequestConfig().copyBuilder()
            .customHeader("Content-Type", "application/json")
            .customHeader("Access-Key-Id", accessKeyId)
            .customHeader("Open-Id", openId)
            .build();
    }

    public static Openapi openapi(String accessKeyId,String secret) {
        if (openapi == null) {
            String accessKey = accessKeyId;
            String accessSecret = secret;
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, accessSecret);
            openapi = Openapi.builder()
                .iamCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
        }
        return openapi;
    }

    public static AmazonSQS amazonSQS(String accessKeyId,String secret) {
        if (amazonSQS == null) {
            String accessKey = accessKeyId;
            String accessSecret = secret;
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, accessSecret);
            amazonSQS = AmazonSQSClientBuilder.standard()
                .withRegion(regions)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
        }
        return amazonSQS;
    }
}

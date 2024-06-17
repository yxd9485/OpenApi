package com.fenbeitong.openapi.plugin.func.callback.dto;

import com.fenbeitong.openapi.plugin.event.core.BaseEvent;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>Title: RocketMQCheckOrderEventMessage</p>
 * <p>Description: 订单消息检查 </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2021/12/10 6:56 PM
 */
@Data
public class RocketMQCheckOrderEventMessage implements Serializable, BaseEvent {

    private String id;

    private Integer status;

    private String collectionName;

    private Integer times;

}

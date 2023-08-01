package cn.vincent.service;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class Consumer0721Service {

    private DefaultMQPushConsumer consumer = null;

    /**
     * 初始化消费者
     */
    @PostConstruct
    public void init0721MQConsumer() {
        consumer = new DefaultMQPushConsumer("TAG_0721_Group");
        consumer.setMaxReconsumeTimes(20);// 设置最大重试次数
        consumer.setNamesrvAddr("localhost:9876");
        try {
            consumer.subscribe("TEST-0717-TOPIC", "TAG_0721");
            consumer.setMaxReconsumeTimes(20);// 设置最大重试次数
            consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
                for (MessageExt msg : msgs) {
                    msg.getReconsumeTimes();// 获取消息的重试次数
                    System.out.println("TAG:TAG_0721 => Message Received: " + new String(msg.getBody()));
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                // 消费逻辑失败 稍后重试
                //return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            });
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void shutDown0721Consumer() {
        if (consumer != null) {
            consumer.shutdown();
        }
    }
}

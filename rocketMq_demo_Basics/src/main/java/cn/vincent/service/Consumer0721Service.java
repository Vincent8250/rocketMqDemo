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
        consumer.setNamesrvAddr("localhost:9876");
        try {
            consumer.subscribe("TEST-0717-TOPIC", "TAG_0721");
            consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
                for (MessageExt msg : msgs) {
                    System.out.println("TAG:TAG_0721 => Message Received: " + new String(msg.getBody()));
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
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

package cn.vincent.service;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * 生产者
 */
@Service
public class ProducerService {

    private DefaultMQProducer producer = null;

    /**
     * 生产者初始化
     */
    @PostConstruct
    public void initMQProducer() throws MQClientException {
        producer = new DefaultMQProducer("defaultGroup");
        producer.setNamesrvAddr("localhost:9876");// 设置nameserver地址
        producer.setRetryTimesWhenSendFailed(3);
        producer.start();
    }

    /**
     * 发送消息
     * @param topic
     * @param tags
     * @param content
     * @return
     */
    public boolean send(String topic, String tags, String content) {
        Message msg = new Message(topic, tags, "", content.getBytes());
        try {
            producer.send(msg);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 销毁前关闭资源
     */
    @PreDestroy
    public void shutDownProducer() {
        if(producer != null) {
            producer.shutdown();
        }
    }
}

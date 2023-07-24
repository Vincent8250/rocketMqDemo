package cn.vincent.listener;

import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(
        topic = "my-topic",
        selectorExpression = "Message_02",
        consumerGroup = "consumer-group-message_02",
        nameServer = "${rocketmq.name-server}",
        messageModel = MessageModel.CLUSTERING,// 设置消息模式 广播模式||集群模式
        consumeMode = ConsumeMode.ORDERLY // 设置消费模型
)
public class RocketMessage2Listener implements RocketMQListener<String> {
    @Override
    public void onMessage(String message) {
        System.out.println("TAG：Message_02 => Message：" + message);
    }
}
